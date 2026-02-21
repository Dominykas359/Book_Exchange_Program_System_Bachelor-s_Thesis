from fastapi import FastAPI
from pydantic import BaseModel, Field
from sentence_transformers import SentenceTransformer

app = FastAPI(title="ML Embedding Service")

MODELS = {
    "bert": SentenceTransformer("sentence-transformers/bert-base-nli-mean-tokens"),
    "distilbert": SentenceTransformer("sentence-transformers/distilbert-base-nli-stsb-mean-tokens"),
    "roberta": SentenceTransformer("sentence-transformers/roberta-base-nli-stsb-mean-tokens"),
}

DEFAULT_MODEL = "roberta"  # choose what you want as default

class EmbedRequest(BaseModel):
    text: str
    modelKey: str = Field(default=DEFAULT_MODEL)

@app.get("/health")
def health():
    return {"status": "ok", "models": list(MODELS.keys()), "dim": 768, "default": DEFAULT_MODEL}

@app.post("/embed")
def embed(req: EmbedRequest):
    if req.modelKey not in MODELS:
        return {"error": f"Unknown modelKey: {req.modelKey}", "allowed": list(MODELS.keys())}

    vec = MODELS[req.modelKey].encode(req.text, normalize_embeddings=True).tolist()
    return {"vector": vec, "dim": len(vec), "modelKey": req.modelKey}