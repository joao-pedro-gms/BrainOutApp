"""Endpoints REST de autenticação (``/auth/register``, ``/auth/login``, ``/auth/me``).

Issue #7 — subescopo backend.
"""
from __future__ import annotations

from typing import Annotated

from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.exc import IntegrityError
from sqlalchemy.orm import Session

from app.auth.deps import get_current_user
from app.auth.jwt import criar_token
from app.config import get_settings
from app.db import get_db
from app.models.usuario import Usuario
from app.schemas.usuario import (
    LoginInput,
    TokenOut,
    UsuarioCreate,
    UsuarioPublic,
)
from app.services import usuarios as usuarios_service

router = APIRouter(prefix="/auth", tags=["auth"])


@router.post(
    "/register",
    response_model=UsuarioPublic,
    status_code=status.HTTP_201_CREATED,
    summary="Registra um novo usuário",
    responses={
        409: {"description": "Email já cadastrado"},
        400: {"description": "Payload inválido"},
    },
)
def registrar(
    payload: UsuarioCreate,
    db: Annotated[Session, Depends(get_db)],
) -> UsuarioPublic:
    """Cria um usuário, retornando os dados públicos (sem ``senha_hash``)."""
    existente = usuarios_service.buscar_por_email(db, payload.email)
    if existente is not None:
        raise HTTPException(
            status_code=status.HTTP_409_CONFLICT,
            detail="email já cadastrado",
        )

    try:
        usuario = usuarios_service.criar_usuario(db, payload)
    except IntegrityError as exc:
        db.rollback()
        raise HTTPException(
            status_code=status.HTTP_409_CONFLICT,
            detail="email já cadastrado",
        ) from exc

    return UsuarioPublic.model_validate(usuario)


@router.post(
    "/login",
    response_model=TokenOut,
    summary="Autentica e retorna um access token",
    responses={401: {"description": "credenciais inválidas"}},
)
def login(
    credenciais: LoginInput,
    db: Annotated[Session, Depends(get_db)],
) -> TokenOut:
    """Verifica email/senha e emite JWT (HS256)."""
    usuario = usuarios_service.autenticar(
        db, credenciais.email, credenciais.senha
    )
    if usuario is None:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="credenciais inválidas",
            headers={"WWW-Authenticate": "Bearer"},
        )

    settings = get_settings()
    expiracao_min = settings.jwt_expires_min
    token = criar_token(
        {"sub": usuario.id, "perfil": usuario.perfil.value},
    )
    return TokenOut(access_token=token, expires_in=expiracao_min * 60)


@router.get(
    "/me",
    response_model=UsuarioPublic,
    summary="Retorna os dados do usuário autenticado",
    responses={401: {"description": "credenciais inválidas"}},
)
def me(usuario_atual: Annotated[Usuario, Depends(get_current_user)]) -> UsuarioPublic:
    """Retorna o usuário dono do token JWT do header ``Authorization``."""
    return UsuarioPublic.model_validate(usuario_atual)
