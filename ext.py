import yaml, pdf2image, pytesseract, os, ollama, PIL, PIL.ImageFilter, time, dotenv
from google import genai

with open("config.yaml", "r") as file:
    cfg = yaml.safe_load(file) or {}

# =========================
# CONFIGURATION
# =========================

INPUT_PATH = cfg.get("input_path", "input_files")
OUTPUT_PATH = cfg.get("output_path", "output_files")
ALLOWED_INPUT_IMAGE_EXTENSIONS = set(cfg.get("allowed_input_image_extensions", [".png", ".jpg", ".jpeg", ".tiff", ".bmp"]))
DPI = cfg.get("dpi", 300)
GRAYSCALE = cfg.get("grayscale", True)
SCALE = cfg.get("scale", 4)
LANGUAGES_FOR_OCR = cfg.get("languages_for_ocr", "eng")
CONFIG_FOR_OCR = cfg.get("config_for_ocr", "--oem 3 --psm 6")
STOP_LLM = cfg.get("stop_llm", False)
MODEL = cfg.get("model", "gemini-2.5-flash")
DOCUMENTS = cfg.get("documents", {})

# =========================
# FUNCTIONS
# =========================

def extract_text_from_image(image_path_or_image):
    return pytesseract.image_to_string(
        image_path_or_image,
        lang=LANGUAGES_FOR_OCR,
        config=CONFIG_FOR_OCR
    )

def process_pdf_file(input_file_path):
    print(f"📄 Processing PDF file: {input_file_path}")
    images = pdf2image.convert_from_path(input_file_path, dpi=DPI)

    processed_images = []

    for image in images:
        if GRAYSCALE:
            image = image.convert("L")
        image = PIL.ImageOps.autocontrast(image)
        width, height = image.size
        new_width = int(width * SCALE)
        new_height = int(height * SCALE)
        image = image.resize((new_width, new_height), PIL.Image.Resampling.LANCZOS)
        # image = image.filter(PIL.ImageFilter.MedianFilter(size=3))
        processed_images.append(image)

    text = ""
    for i, page in enumerate(processed_images):
        print(f"📄 Processing page {i + 1}")
        page_text = extract_text_from_image(page)
        if page_text.strip():  # Skip empty pages
            text += page_text + "\n" # slower but readable
    return text


def generate_with_gemma(prompt):
    client = genai.Client()
    return client.models.generate_content(
        model=MODEL,   # e.g., "gemma-3-27b"
        contents=prompt
    ).text

def generate_with_gemini(prompt):
    client = genai.Client()
    return client.models.generate_content(
        model=MODEL,
        contents=prompt
    ).text

def generate_with_ollama(prompt):
    return ollama.chat(
        model=MODEL,
        messages=[
            {"role": "system", "content": "You extract structured data and output only valid JSON."},
            {"role": "user", "content": prompt},
        ],
        options={"temperature": 0.2},
    )["message"]["content"]

def generate_json(prompt, max_retries=50, retry_delay=2):
    global MODEL
    if MODEL.startswith("gemma-") or MODEL.startswith("gemini-"):
        attempt = 0
        while True:
            try:
                if MODEL.startswith("gemma-"):
                    return generate_with_gemma(prompt)
                else:
                    return generate_with_gemini(prompt)
            except Exception as e:
                attempt += 1
                print(f"⚠️ {MODEL} failed (attempt {attempt}): {e}")

                if attempt >= max_retries:
                    print(f"❌ Max retries reached for {MODEL}.")
                    return {}

                retry_delay *= 2
                time.sleep(retry_delay)
    else:
        return generate_with_ollama(prompt)

def detect_board_and_prompt(text):
    text = text.upper()
    scores = {}

    for board, data in DOCUMENTS.items():
        scores[board] = sum(k in text for k in data["keywords"])

    best = max(scores, key=scores.get)

    if scores[best] == 0:
        return "UNKNOWN", None

    return best, DOCUMENTS[best]["prompt"]

def process_image_file(input_file_path, output_dir):
    print(f"🖼️ Processing image file: {input_file_path}")
    image = PIL.Image.open(input_file_path)
    if GRAYSCALE:
        image = image.convert("L")  # grayscale (ignore color)
    image = PIL.ImageOps.autocontrast(image)

    data = pytesseract.image_to_data(image, output_type=pytesseract.Output.DICT)
    
    heights = [data['height'][i] for i in range(len(data['text'])) if data['text'][i].strip()]
    
    if heights:
        avg_font_height = sum(heights) / len(heights)
        # SCALE = 120 / avg_font_height  # dynamic scale factor (taget_font_size = 20)
        print(f"⬆️ SCALE = {SCALE}")

    width, height = image.size
    new_width = int(width * SCALE)   # double the size
    new_height = int(height * SCALE)
    image = image.resize((new_width, new_height), PIL.Image.Resampling.LANCZOS)
    # image = image.filter(PIL.ImageFilter.MedianFilter()) # image becomes smother (remove noise)
    image.save(os.path.join(output_dir, "preprocessed.png"))
    return extract_text_from_image(image)

def process_file(input_file_path):
    os.makedirs(OUTPUT_PATH, exist_ok=True)
    base_name = os.path.splitext(os.path.basename(input_file_path))[0]
    output_dir = os.path.join(OUTPUT_PATH, base_name)
    os.makedirs(output_dir, exist_ok=True)

    ocr_start = time.perf_counter()
    extension = os.path.splitext(input_file_path)[1].lower()

    if extension in ALLOWED_INPUT_IMAGE_EXTENSIONS:
        text = process_image_file(input_file_path, output_dir)
    elif extension == ".pdf":
        text = process_pdf_file(input_file_path)
    else:
        print(f"❌ Unsupported file extension: {extension}")
        raise ValueError(f"Unsupported file extension: {extension}")

    output_file_path_for_orc = os.path.join(output_dir, "ocr.txt")
    with open(output_file_path_for_orc, "w", encoding="utf-8") as f:
        f.write(text)
        print(f"✅ OCR text saved to: {output_file_path_for_orc}")

    ocr_end = time.perf_counter()
    ocr_time = ocr_end - ocr_start
    print(f"⏰ OCR Time: {ocr_time:.3f} seconds")
    board, prompt = detect_board_and_prompt(text)
    print(f"🧠 Detected Board: {board}")

    if board == "UNKNOWN" or prompt is None:
        print("❌ Unable to detect board or no prompt available. Skipping LLM processing.")
        return

    llm_start = time.perf_counter()

    if STOP_LLM:
        print("🛑 STOP_LLM is set to True. Exiting before LLM processing.")
        return
    
    prompt += f"\n\nOCR TEXT:\n{text}\n"
    print(f"🤖 Sending data to {MODEL} for structured JSON extraction...")
    json_output = generate_json(prompt)

    if json_output.startswith("```json"):
        json_output = json_output[len("```json"):].strip()

    if json_output.endswith("```"):
        json_output = json_output[:-3].strip()


    output_file_path_for_json = os.path.join(output_dir, "json.json")
    with open(output_file_path_for_json, "w", encoding="utf-8") as f:
        f.write(json_output)
    print(f"✅ JSON output saved to: {output_file_path_for_json}")

    llm_end = time.perf_counter()
    llm_time = llm_end - llm_start
    total_time = ocr_time + llm_time
    print(f"⏰ LLM Time: {llm_time:.3f} seconds")
    print(f"⏰ Total Time: {total_time:.3f} seconds")

def process():
    dotenv.load_dotenv()

    if os.path.isfile(INPUT_PATH):
        process_file(INPUT_PATH)

    elif os.path.isdir(INPUT_PATH):
        for name in sorted(os.listdir(INPUT_PATH)):
            full_path = os.path.join(INPUT_PATH, name)
            if os.path.isfile(full_path):
                process_file(full_path)
    else:
        raise ValueError(f"Invalid path: {INPUT_PATH}")

# =========================
# MAIN PROCESSING LOGIC
# =========================

process()