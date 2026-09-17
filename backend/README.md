# Esqueleto da API Python (FastAPI). Será implementado na issue #14.
# Esta pasta existe para que o workflow backend-ci.yml encontre o diretório
# e deixe de ser no-op.

## Setup local

```bash
cd backend
python -m venv .venv
source .venv/bin/activate
pip install -r requirements.txt
cp .env.example .env
uvicorn app.main:app --reload
```

Documentação interativa em http://localhost:8000/docs.
