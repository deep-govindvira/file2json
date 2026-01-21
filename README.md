# File2JSON

**File2JSON** is a Python tool that converts files (images or PDFs) containing text into JSON format using OCR (Optical Character Recognition). It is designed to batch-process files in a folder and generate structured JSON outputs.

## Requirements

- macOS / Linux
- Python 3.9.6+
- Tesseract OCR installed and added to your system PATH
- Ollama

## Setup Instructions (Mac)

### 1. Install Ollama

1. Install Ollama via Homebrew:

```bash
brew install ollama
```

2. Verify installation:

```bash
ollama --version
```

3. Start the Ollama server:

```bash
ollama serve
```

---

### 2. Copy traineddata files

Copy your `.traineddata` files to Tesseract’s `tessdata` folder. By default, this is:

```
/opt/homebrew/share/tessdata/
```

Example commands:

```bash
cp -R -i traineddata/. /opt/homebrew/share/tessdata/
```

> ⚠️ You may need to use `sudo` if you get a permission error:

```bash
sudo cp -R -i traineddata/. /opt/homebrew/share/tessdata/
```

---

### 3. Create and activate Python virtual environment

```bash
python3 -m venv venv
source venv/bin/activate
```

---

### 4. Install Python dependencies

```bash
pip install pytesseract pdf2image Pillow opencv-python PyYAML numpy python-dotenv
```

---

### 5. Create a `.env` file (for Gemini model)

Get your API key from:  
https://aistudio.google.com/api-keys

In your project folder, create a file named `.env` and add your Gemini API key:

```env
GEMINI_API_KEY=your_gemini_api_key_here
```

> Replace `your_gemini_api_key_here` with your actual Gemini API key if you plan to use Gemini instead of Ollama.

---

### 6. Run Python script

```bash
python -W ignore ext.py
```

---

### 7. Deactivate virtual environment

```bash
deactivate
```

## Setup Instructions (Windows)

### 1. Install Ollama

1. Download the Windows installer from [https://ollama.com/download](https://ollama.com/download).
2. Run the installer and follow the prompts.
3. Verify installation:

```bat
ollama --version
```

4. Start the Ollama server:

```bat
ollama serve
```

---

### 2. Copy traineddata files

Copy your `.traineddata` files to Tesseract’s `tessdata` folder. By default, this is:

```
C:\Program Files\Tesseract-OCR\tessdata\
```

Example commands:

```bat
xcopy /Y /I traineddata\* "C:\Program Files\Tesseract-OCR\tessdata\"
```

> ⚠️ Administrator privileges may be required.

---

### 3. Create and activate Python virtual environment

```bat
python -m venv venv
venv\Scripts\activate
```

---

### 4. Install Python dependencies

```bat
pip install pytesseract pdf2image Pillow opencv-python PyYAML numpy python-dotenv
```

---

if you plan to use a **Gemini model**, the API key needs to be stored in a `.env` file so your Python script can read it securely. 

---

## 5. Create a `.env` file 

Get your API key from:  
https://aistudio.google.com/api-keys

In your project folder, create a file named `.env` (if it doesn’t exist) and add your API key:

```env
GEMINI_API_KEY=your_gemini_api_key_here
```

> Replace `your_gemini_api_key_here` with your actual Gemini API key if you want to use gemini model instead of Ollama.
---


### 6. Run Python script

```bat
python -W ignore ext.py
```

---

### 7. Deactivate virtual environment

```bat
deactivate
```
