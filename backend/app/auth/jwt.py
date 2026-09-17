"""Emissão e validação de tokens JWT (HS256)."""
from __future__ import annotations

from datetime import UTC, datetime, timedelta

from jose import JWTError, jwt
from jose.exceptions import ExpiredSignatureError

from app.config import get_settings


def _agora() -> datetime:
    return datetime.now(tz=UTC)


def criar_token(claims: dict[str, object], *, expira_em: timedelta | None = None) -> str:
    """Assina um JWT HS256 com os ``claims`` informados.

    Adiciona ``exp`` e ``iat`` automaticamente, a menos que ``claims``
    já os forneça. Por padrão expira em ``JWT_EXPIRES_MIN`` minutos.
    """
    settings = get_settings()
    payload = dict(claims)
    agora = _agora()
    payload.setdefault("iat", int(agora.timestamp()))
    if expira_em is None:
        expira_em = timedelta(minutes=settings.jwt_expires_min)
    payload.setdefault("exp", int((agora + expira_em).timestamp()))
    return jwt.encode(payload, settings.jwt_secret, algorithm=settings.jwt_algorithm)


def decodificar_token(token: str) -> dict[str, object]:
    """Decodifica e valida um JWT.

    Levanta :class:`jose.exceptions.JWTError` em caso de erro (assinatura
    inválida, expirado, malformado). O caller decide como traduzir a
    exceção em resposta HTTP.
    """
    settings = get_settings()
    try:
        return jwt.decode(token, settings.jwt_secret, algorithms=[settings.jwt_algorithm])
    except ExpiredSignatureError as exc:
        raise JWTError("Token expirado") from exc
