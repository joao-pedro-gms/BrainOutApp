"""Regras de negócio do recurso ``Usuario``.

Funções retornam o objeto de domínio (:class:`Usuario`) ou ``None`` quando a
operação falha por motivo "esperado" (credenciais inválidas). Erros de
validação de negócio traduzidos em HTTPException ficam na camada de router.
"""
from __future__ import annotations

from sqlalchemy import select
from sqlalchemy.orm import Session

from app.auth.security import hash_senha, verifica_senha
from app.logging_config import get_logger
from app.models.usuario import Usuario
from app.schemas.usuario import UsuarioCreate

logger = get_logger(__name__)


def _email_normalizado(email: str) -> str:
    return email.strip().lower()


def buscar_por_email(db: Session, email: str) -> Usuario | None:
    """Busca case-insensitive por email (índice unique já cobre exato)."""
    stmt = select(Usuario).where(Usuario.email == _email_normalizado(email))
    return db.execute(stmt).scalar_one_or_none()


def criar_usuario(db: Session, payload: UsuarioCreate) -> Usuario:
    """Cria novo usuário a partir de ``payload``.

    Assume unicidade já validada (ver ``verificar_email_duplicado``); o
    índice ``UNIQUE`` no banco é a rede de segurança final.
    """
    email = _email_normalizado(payload.email)
    usuario = Usuario(
        email=email,
        senha_hash=hash_senha(payload.senha),
        perfil=payload.perfil,
    )
    db.add(usuario)
    db.commit()
    db.refresh(usuario)
    # Log nunca inclui senha/email completo (apenas id e perfil)
    logger.info(
        "usuario.criado id=%s perfil=%s",
        usuario.id,
        usuario.perfil.value,
    )
    return usuario


def autenticar(db: Session, email: str, senha: str) -> Usuario | None:
    """Verifica credenciais. Retorna o usuário em caso positivo, senão ``None``.

    Importante: logamos apenas o ID encontrado (sem email/senha) para
    evitar vazamento de PII em logs.
    """
    usuario = buscar_por_email(db, email)
    if usuario is None:
        logger.warning("auth.falha motivo=email_nao_encontrado")
        return None
    if not verifica_senha(senha, usuario.senha_hash):
        logger.warning("auth.falha motivo=senha_incorreta id=%s", usuario.id)
        return None
    logger.info("auth.ok id=%s", usuario.id)
    return usuario
