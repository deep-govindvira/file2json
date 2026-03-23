FROM python:3.11-slim

ENV DEBIAN_FRONTEND=noninteractive

WORKDIR /app

# Install system dependencies
RUN apt-get update && apt-get install -y \
    tesseract-ocr \
    tesseract-ocr-eng \
    poppler-utils \
    libgl1 \
    libglib2.0-0 \
    libsm6 \
    libxext6 \
    libxrender1 \
    libfontconfig1 \
    && rm -rf /var/lib/apt/lists/*

# Copy traineddata into Tesseract tessdata folder
# (this is the correct path for Debian-based images)
COPY traineddata/*.traineddata /usr/share/tesseract-ocr/4.00/tessdata/

# Copy requirements
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# Copy application code
COPY config.yaml .
COPY api.py .

EXPOSE ${UVICORN_PORT}

CMD ["python", "-W", "ignore", "api.py"]

