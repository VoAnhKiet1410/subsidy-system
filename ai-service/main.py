import io
import cv2
import numpy as np
import easyocr
from fastapi import FastAPI, File, UploadFile
from PIL import Image, ImageChops, ImageEnhance
from typing import Dict, Any

app = FastAPI(title="TroCap AI Service", version="1.0.0", description="OCR and Fraud Detection")

# 1. Khởi tạo mô hình EasyOCR (hỗ trợ Tiếng Việt & Tiếng Anh)
print("Loading EasyOCR model for Vietnamese... (Máy cần RAM & CPU/GPU)")
try:
    reader = easyocr.Reader(['vi', 'en'])
    print("EasyOCR module loaded successfully.")
except Exception as e:
    print(f"Failed to load EasyOCR: {e}")
    reader = None

def detect_tampering_ela(image_bytes: bytes) -> float:
    """
    Thuật toán ELA (Error Level Analysis) cơ bản để phát hiện ảnh bị cắt ghép.
    Mức chênh lệch pixel càng cao -> Có khả năng dùng phần mềm (Photoshop) dán chữ/ảnh đè lên.
    """
    try:
        original = Image.open(io.BytesIO(image_bytes)).convert('RGB')
        
        # Lưu ra JPEG ở mức chất lượng 90%
        temp_buffer = io.BytesIO()
        original.save(temp_buffer, 'JPEG', quality=90)
        
        # Mở lại để so sánh
        resaved = Image.open(temp_buffer)
        
        # Mức chênh lệch
        ela_image = ImageChops.difference(original, resaved)
        
        extrema = ela_image.getextrema()
        max_diff = max([ex[1] for ex in extrema])
        
        if max_diff == 0:
            scale = 1
        else:
            scale = 255.0 / max_diff
            
        ela_image = ImageEnhance.Brightness(ela_image).enhance(scale)
        
        ela_np = np.array(ela_image)
        mean_diff = float(np.mean(ela_np))
        
        # Scale về điểm gian lận từ 0 -> 1 
        score = min(mean_diff / 50.0, 1.0)
        return score
    except Exception as e:
        print(f"ELA Error: {e}")
        return 0.0

@app.post("/api/v1/analyze-document")
async def analyze_document(file: UploadFile = File(...)) -> Dict[str, Any]:
    file_bytes = await file.read()
    
    # --- BƯỚC 1: TRÍCH XUẤT VĂN BẢN BẰNG OCR ---
    text_result = ""
    mean_confidence = 0.0
    
    if reader:
        # Chuyển đổi Bytes sang Numpy Array cho OpenCV 
        np_arr = np.frombuffer(file_bytes, np.uint8)
        img = cv2.imdecode(np_arr, cv2.IMREAD_COLOR)
        if img is not None:
            # Đọc text từ Ả
            results = reader.readtext(img)
            extracted_parts = []
            confidences = []
            for (bbox, text, prob) in results:
                extracted_parts.append(text)
                confidences.append(prob)
            
            text_result = " ".join(extracted_parts)
            if confidences:
                mean_confidence = sum(confidences) / len(confidences)
    
    # --- BƯỚC 2: PHÁT HIỆN ẢNH BỊ CHỈNH SỬA / CẮT GHÉP ---
    tamper_score = detect_tampering_ela(file_bytes)
    
    # Đặt ngưỡng trên 60% khác thường (0.6) thì cắm cờ
    is_tampered = tamper_score > 0.6  
    
    return {
        "filename": file.filename,
        "ocr_text": text_result,
        "ocr_confidence": round(mean_confidence, 2),
        "tampering_score": round(tamper_score, 2),
        "is_tampered": is_tampered,
        "status": "success"
    }

if __name__ == "__main__":
    import uvicorn
    # Mở port 8000
    uvicorn.run("main:app", host="0.0.0.0", port=8000, reload=True)
