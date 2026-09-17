"""Configuração central de logging.

Substitui ``print()`` por saídas estruturadas em stderr. Em produção a
formatação ``%(message)s`` casa bem com coletores JSON.
"""
from __future__ import annotations

import logging
import sys

from app.config import get_settings


def configure_logging() -> None:
    """Inicializa o logger raiz uma única vez."""
    settings = get_settings()
    level = getattr(logging, settings.log_level.upper(), logging.INFO)
    logging.basicConfig(
        level=level,
        format="%(asctime)s %(levelname)s %(name)s :: %(message)s",
        datefmt="%Y-%m-%dT%H:%M:%S%z",
        stream=sys.stderr,
        force=True,
    )


def get_logger(name: str) -> logging.Logger:
    """Retorna um logger nomeado, garantindo que o root esteja configurado."""
    configure_logging()
    return logging.getLogger(name)
