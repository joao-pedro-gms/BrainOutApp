"""Modelo ORM de ``Usuario`` e enum de perfil.

Chave primária é ``UUID`` (versão 7 — ordenável por tempo, ideal para
sincronização futura com o app Android). ``email`` é único e indexado;
``senha_hash`` armazena bcrypt (nunca texto plano). ``perfil`` aceita
apenas ``gerente`` ou ``colaborador`` (RN00 — issue #7).
"""
from __future__ import annotations

import enum
from datetime import UTC, datetime

from sqlalchemy import DateTime, Enum, String
from sqlalchemy.orm import Mapped, mapped_column
from uuid_extensions import uuid7

from app.db import Base


class Perfil(enum.StrEnum):
    """Papéis disponíveis no BrainOutApp (issue #7)."""

    GERENTE = "gerente"
    COLABORADOR = "colaborador"


def _novo_uuid() -> str:
    """Gera UUID v7 em formato string (chamável como default SQLAlchemy)."""
    return str(uuid7())


def _utc_now() -> datetime:
    """Timestamp UTC sem timezone (comparável em SQLite/PostgreSQL)."""
    return datetime.now(tz=UTC).replace(tzinfo=None)


class Usuario(Base):
    """Usuário do BrainOutApp."""

    __tablename__ = "usuarios"

    id: Mapped[str] = mapped_column(
        String(36),
        primary_key=True,
        default=_novo_uuid,
    )
    email: Mapped[str] = mapped_column(
        String(255),
        unique=True,
        index=True,
        nullable=False,
    )
    senha_hash: Mapped[str] = mapped_column(
        String(255),
        nullable=False,
    )
    perfil: Mapped[Perfil] = mapped_column(
        Enum(Perfil, name="perfil_enum", values_callable=lambda e: [m.value for m in e]),
        nullable=False,
    )
    created_at: Mapped[datetime] = mapped_column(
        DateTime(timezone=False),
        default=_utc_now,
        nullable=False,
    )

    def __repr__(self) -> str:  # noqa: D105
        return f"Usuario(id={self.id!r}, email={self.email!r}, perfil={self.perfil!r})"
