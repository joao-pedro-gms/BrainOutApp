"""Configuração carregada de variáveis de ambiente.

Usamos pydantic-settings para ler ``DATABASE_URL``, ``JWT_SECRET`` e
``JWT_EXPIRES_MIN`` do ambiente (ou ``.env``). Validação acontece na
importação: variáveis inválidas falham rápido.
"""
from __future__ import annotations

from functools import lru_cache

from pydantic import Field
from pydantic_settings import BaseSettings, SettingsConfigDict


class Settings(BaseSettings):
    """Configuração global da aplicação."""

    model_config = SettingsConfigDict(
        env_file=".env",
        env_file_encoding="utf-8",
        case_sensitive=False,
        extra="ignore",
    )

    database_url: str = Field(
        default="sqlite:///./brainoutapp.db",
        description="URL SQLAlchemy de conexão com o banco.",
    )
    jwt_secret: str = Field(
        default="change-me-32-chars-min-aaaaaaaaaaaa",
        min_length=32,
        description="Segredo HS256 usado para assinar tokens JWT.",
    )
    jwt_expires_min: int = Field(
        default=60,
        ge=1,
        description="Tempo de expiração do access token em minutos.",
    )
    jwt_algorithm: str = Field(
        default="HS256",
        description="Algoritmo de assinatura JWT.",
    )
    bcrypt_rounds: int = Field(
        default=12,
        ge=4,
        description=(
            "Custo do bcrypt. Padrão 12 (R-SEG); testes podem reduzir para acelerar."
        ),
    )
    log_level: str = Field(default="INFO")


@lru_cache(maxsize=1)
def get_settings() -> Settings:
    """Retorna a instância singleton de Settings."""
    return Settings()
