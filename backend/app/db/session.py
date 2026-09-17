"""Engine e sessão SQLAlchemy 2.x.

Suporta SQLite (dev) e PostgreSQL (prod) lendo ``DATABASE_URL`` de
``app.config``. Usa ``check_same_thread=False`` apenas para SQLite,
permitindo o uso do engine em threads (ex.: testes com fixtures).
"""
from __future__ import annotations

from collections.abc import Iterator

from sqlalchemy import create_engine
from sqlalchemy.engine import Engine
from sqlalchemy.orm import DeclarativeBase, Session, sessionmaker

from app.config import get_settings


class Base(DeclarativeBase):
    """Classe base declarativa para todos os modelos ORM."""


def _build_engine() -> Engine:
    settings = get_settings()
    url = settings.database_url
    connect_args: dict[str, object] = {}
    if url.startswith("sqlite"):
        connect_args["check_same_thread"] = False
    return create_engine(url, connect_args=connect_args, future=True)


engine: Engine = _build_engine()
SessionLocal = sessionmaker(bind=engine, autoflush=False, autocommit=False, future=True)


def get_db() -> Iterator[Session]:
    """Dependência FastAPI que fornece uma sessão por request."""
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()
