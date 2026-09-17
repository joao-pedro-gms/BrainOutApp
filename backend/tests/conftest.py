"""Fixtures compartilhadas pelos testes.

Usa SQLite **em arquivo temporário** (``pytest``-managed) para cada
sessão de teste, com ``StaticPool`` para evitar problemas de thread
quando o ``TestClient`` dispara a FastAPI lifespan. Em paralelo,
substitui ``app.db.get_db`` por um override que devolve sessões sobre
o engine de teste.
"""
from __future__ import annotations

import contextlib
import os
import tempfile
from collections.abc import Iterator
from pathlib import Path

import pytest
from fastapi.testclient import TestClient
from sqlalchemy import create_engine
from sqlalchemy.orm import Session, sessionmaker
from sqlalchemy.pool import StaticPool

from app.config import get_settings
from app.db import Base, get_db
from app.main import app
from app.models import usuario as _  # noqa: F401  garante registro em metadata


@pytest.fixture(scope="session")
def engine_test():
    """Cria engine SQLite temporário por sessão de testes."""
    fd, path = tempfile.mkstemp(suffix=".db")
    os.close(fd)
    try:
        eng = create_engine(
            f"sqlite:///{path}",
            connect_args={"check_same_thread": False},
            poolclass=StaticPool,
            future=True,
        )
        Base.metadata.create_all(bind=eng)
        yield eng
    finally:
        with contextlib.suppress(OSError):
            Path(path).unlink()


@pytest.fixture(scope="session")
def session_factory_test(engine_test):
    return sessionmaker(bind=engine_test, autoflush=False, autocommit=False, future=True)


@pytest.fixture()
def db_session(session_factory_test) -> Iterator[Session]:
    """Sessão isolada por teste com rollback automático."""
    session = session_factory_test()
    try:
        yield session
    finally:
        session.rollback()
        session.close()


@pytest.fixture()
def client(session_factory_test) -> Iterator[TestClient]:
    """TestClient FastAPI com ``get_db`` redirecionado ao DB de teste."""
    # Garante JWT_SECRET com tamanho mínimo e BCRYPT_ROUNDS baixo p/ velocidade
    os.environ.setdefault("JWT_SECRET", "test-secret-com-pelo-menos-trinta-e-dois-chars")
    os.environ.setdefault("BCRYPT_ROUNDS", "4")

    def _override_get_db() -> Iterator[Session]:
        db = session_factory_test()
        try:
            yield db
        finally:
            db.close()

    app.dependency_overrides[get_db] = _override_get_db
    with TestClient(app) as c:
        yield c
    app.dependency_overrides.clear()


@pytest.fixture(autouse=True)
def _ensure_settings_reload(monkeypatch: pytest.MonkeyPatch):
    """Garante valores de settings estáveis durante a sessão de testes."""
    monkeypatch.setenv("JWT_SECRET", "test-secret-com-pelo-menos-trinta-e-dois-chars")
    monkeypatch.setenv("JWT_EXPIRES_MIN", "60")
    monkeypatch.setenv("BCRYPT_ROUNDS", "4")
    monkeypatch.setenv(
        "DATABASE_URL", "sqlite:///./brainoutapp.test.db"
    )
    get_settings.cache_clear()  # type: ignore[attr-defined]
    yield
    get_settings.cache_clear()  # type: ignore[attr-defined]
