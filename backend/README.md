# Esqueleto da API Python (FastAPI). Será implementado na issue #14.
# Esta pasta existe para que o workflow backend-ci.yml encontre o diretório
# e deixe de ser no-op.

## Setup local

Pré-requisito: **uv** (10-100× mais rápido que pip, lockfile determinístico, gerencia o venv).

```bash
# Instalar uv uma vez: https://docs.astral.sh/uv/getting-started/installation/
curl -LsSf https://astral.sh/uv/install.sh | sh

cd backend
uv sync                   # cria .venv, instala deps de prod+dev via uv.lock
uv run pytest             # roda os testes
cp .env.example .env      # editar com seus valores
uv run uvicorn app.main:app --reload   # API em http://localhost:8000
```

Documentação interativa em http://localhost:8000/docs.

## Comandos úteis

```bash
uv run ruff check .       # lint
uv run ruff format .      # format
uv run pytest             # testes
uv add fastapi            # adicionar dep + atualizar uv.lock
uv run pip-audit          # auditoria de vulnerabilidades
```

## Lockfile

`uv.lock` é versionado. Para builds reproduzíveis use sempre `uv sync --frozen` (CI).
