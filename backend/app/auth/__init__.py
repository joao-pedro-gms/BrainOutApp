"""Pacote de autenticação JWT + bcrypt.

API pública:
- :func:`hash_senha` / :func:`verifica_senha` — hashing bcrypt.
- :func:`criar_token` / :func:`decodificar_token` — emissão/validação JWT.
- :func:`get_current_user` — dependência FastAPI que retorna o
  :class:`app.models.Usuario` autenticado.
"""
from app.auth.deps import get_current_user, oauth2_scheme
from app.auth.jwt import criar_token, decodificar_token
from app.auth.security import hash_senha, verifica_senha

__all__ = [
    "criar_token",
    "decodificar_token",
    "get_current_user",
    "hash_senha",
    "oauth2_scheme",
    "verifica_senha",
]
