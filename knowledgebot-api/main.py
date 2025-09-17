from fastapi import FastAPI
from pydantic import BaseModel
import json, os

class AskRequest(BaseModel):
    question: str
    userId: str

# Locate policies.json in Java resources
BASE_DIR = os.path.dirname(os.path.abspath(__file__))
POLICIES_PATH = os.path.join(BASE_DIR, "..", "src", "main", "resources", "policies.json")

with open(os.path.abspath(POLICIES_PATH), "r") as f:
    policies = json.load(f)

app = FastAPI(title="KnowledgeBot API", version="1.0")

@app.post("/ask")
async def ask_bot(req: AskRequest):
    question = req.question.lower()

    # Try to match question with policy keys
    for key, answer in policies.items():
        if key.replace("_", " ") in question or key in question:
            return {"answer": answer}

    return {"answer": "Sorry, I don't know the answer to that."}
