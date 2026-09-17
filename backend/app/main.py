"""Entry-point FastAPI do BrainOutApp.

Registra os routers e configura logging. Tabelas são criadas em dev via
``Base.metadata.create_all`` quando o módulo é importado fora de um
contexto de teste (testes usam fixture próprio em :mod:`tests.conftest`).
"""
from __future__ import annotations

from contextlib import asynccontextmanager

from fastapi import FastAPI

# Importar modelos garante que ``Base.metadata`` conhece todas as tabelas
# antes de ``create_all`` rodar.
import app.models  # noqa: F401
from app.config import get_settings
from app.db import Base, engine
from app.logging_config import configure_logging
from app.routers import auth


@asynccontextmanager
async def lifespan(_: FastAPI):
    """Cria tabelas em SQLite (dev) na inicialização."""
    configure_logging()
    settings = get_settings()
    if settings.database_url.startswith("sqlite"):
        # Em dev (SQLite), criar tabelas automaticamente. Em prod
        # (PostgreSQL) quem manda é ``alembic upgrade head``.
        Base.metadata.create_all(bind=engine)
    yield


app = FastAPI(
    title="BrainOutApp API",
    version="0.1.0",
    description="API REST do BrainOutApp (issue #7: autenticação JWT).",
    lifespan=lifespan,
)

app.include_router(auth.router)


@app.get("/health", tags=["health"])
def health() -> dict[str, str]:
    """Endpoint de healthcheck (200 OK)."""
    return {"status": "ok"}
