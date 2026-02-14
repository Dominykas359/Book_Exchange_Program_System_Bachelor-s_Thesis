from fastapi import FastAPI
from pydantic import BaseModel
from sentence_transformers import SentenceTransformer

app = FastAPI(title="ML Embedding Service")

MODEL_NAME = "sentence-transformers/paraphrase-multilingual-mpnet-base-v2"
model = SentenceTransformer(MODEL_NAME)

class EmbedRequest(BaseModel):
    text: str

class EmbedBatchRequest(BaseModel):
    texts: list[str]

@app.get("/health")
def health():
    return {"status": "ok", "model": MODEL_NAME, "dim": 768}

@app.post("/embed")
def embed(req: EmbedRequest):
    vec = model.encode(req.text, normalize_embeddings=True).tolist()
    return {"vector": vec, "dim": len(vec), "model": MODEL_NAME}

@app.post("/embed-batch")
def embed_batch(req: EmbedBatchRequest):
    vectors = model.encode(req.texts, normalize_embeddings=True).tolist()
    return {"vectors": vectors, "count": len(vectors), "dim": len(vectors[0]) if vectors else 0, "model": MODEL_NAME}
