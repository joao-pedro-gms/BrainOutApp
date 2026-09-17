"""Schemas Pydantic para ``Usuario`` (entrada/saída da API de auth).

- ``UsuarioCreate``: payload de ``POST /auth/register`` (com senha em texto
  plano, validada por Pydantic — será descartada após o hash).
- ``UsuarioPublic``: payload de saída — nunca inclui ``senha_hash``.
- ``LoginInput``: payload de ``POST /auth/login``.
- ``TokenOut``: resposta de login (JWT + expiração em segundos).
"""
from __future__ import annotations

from datetime import datetime

from pydantic import BaseModel, ConfigDict, EmailStr, Field

from app.models.usuario import Perfil


class UsuarioCreate(BaseModel):
    """Dados para registrar um novo usuário."""

    email: EmailStr = Field(..., description="Email único do usuário.")
    senha: str = Field(
        ...,
        min_length=8,
        max_length=128,
        description="Senha em texto plano (mín. 8 caracteres). Será hashada.",
    )
    perfil: Perfil = Field(..., description="Perfil de acesso: gerente ou colaborador.")


class UsuarioPublic(BaseModel):
    """Representação pública do usuário (sem dados sensíveis)."""

    model_config = ConfigDict(from_attributes=True)

    id: str
    email: EmailStr
    perfil: Perfil
    created_at: datetime


class LoginInput(BaseModel):
    """Credenciais para ``POST /auth/login``."""

    email: EmailStr
    senha: str = Field(..., min_length=1)


class TokenOut(BaseModel):
    """Resposta de login bem-sucedido."""

    access_token: str
    token_type: str = "bearer"
    expires_in: int = Field(..., description="Tempo de expiração em segundos.")
