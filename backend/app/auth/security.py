"""Hashing de senhas com bcrypt.

Usa ``passlib`` com rounds configurado via ``Settings.bcrypt_rounds``
(>= 12 em produção; testes reduzem para acelerar). O context é
reconstruído a cada chamada para refletir mudanças dinâmicas em
``Settings``.
"""
from __future__ import annotations

from functools import lru_cache

from passlib.context import CryptContext

from app.config import get_settings


@lru_cache(maxsize=1)
def _pwd_context() -> CryptContext:
    settings = get_settings()
    return CryptContext(
        schemes=["bcrypt"],
        deprecated="auto",
        bcrypt__rounds=settings.bcrypt_rounds,
    )


def hash_senha(senha: str) -> str:
    """Gera um hash bcrypt para ``senha``.

    >>> hash_senha("minha-senha").startswith("$2b$")
    True
    """
    return _pwd_context().hash(senha)


def verifica_senha(senha: str, hash_armazenado: str) -> bool:
    """Compara ``senha`` com ``hash_armazenado`` (constant-time)."""
    try:
        return _pwd_context().verify(senha, hash_armazenado)
    except (ValueError, TypeError):
        # hash inválido / corrompido — tratamos como "não confere"
        return False
