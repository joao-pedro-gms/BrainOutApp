"""Dependências FastAPI para autenticação JWT."""
from __future__ import annotations

from typing import Annotated

from fastapi import Depends, HTTPException, status
from fastapi.security import OAuth2PasswordBearer
from jose import JWTError
from sqlalchemy.orm import Session

from app.auth.jwt import decodificar_token
from app.db import get_db
from app.models.usuario import Usuario

# ``tokenUrl`` aponta para o endpoint de login (apenas referencial para o
# Swagger UI; o esquema OAuth2 funciona com qualquer Bearer token válido).
oauth2_scheme: OAuth2PasswordBearer = OAuth2PasswordBearer(tokenUrl="/auth/login")


_CREDenciais = HTTPException(
    status_code=status.HTTP_401_UNAUTHORIZED,
    detail="credenciais inválidas",
    headers={"WWW-Authenticate": "Bearer"},
)


def get_current_user(
    token: Annotated[str, Depends(oauth2_scheme)],
    db: Annotated[Session, Depends(get_db)],
) -> Usuario:
    """Resolve o :class:`Usuario` a partir do token JWT no header ``Authorization``."""
    try:
        payload = decodificar_token(token)
        subject = payload.get("sub")
    except JWTError as exc:
        raise _CREDenciais from exc

    if not isinstance(subject, str) or not subject:
        raise _CREDenciais from None

    usuario = db.get(Usuario, subject)
    if usuario is None:
        raise _CREDenciais from None
    return usuario
