"""Testes do endpoint ``GET /auth/me``."""
from __future__ import annotations

from fastapi.testclient import TestClient


def _registrar_e_logar(
    client: TestClient,
    email: str,
    senha: str = "senha-forte-123",
    perfil: str = "gerente",
) -> str:
    r = client.post(
        "/auth/register",
        json={"email": email, "senha": senha, "perfil": perfil},
    )
    assert r.status_code == 201, r.text
    token_resp = client.post(
        "/auth/login", json={"email": email, "senha": senha}
    )
    assert token_resp.status_code == 200, token_resp.text
    return token_resp.json()["access_token"]


def test_me_com_token_valido_retorna_usuario(client: TestClient) -> None:
    token = _registrar_e_logar(client, "me-ok@brainoutapp.dev", perfil="colaborador")
    resposta = client.get(
        "/auth/me", headers={"Authorization": f"Bearer {token}"}
    )
    assert resposta.status_code == 200, resposta.text
    corpo = resposta.json()
    assert corpo["email"] == "me-ok@brainoutapp.dev"
    assert corpo["perfil"] == "colaborador"
    assert "senha" not in corpo
    assert "senha_hash" not in corpo


def test_me_sem_token_retorna_401(client: TestClient) -> None:
    resposta = client.get("/auth/me")
    assert resposta.status_code == 401


def test_me_token_invalido_retorna_401(client: TestClient) -> None:
    resposta = client.get(
        "/auth/me", headers={"Authorization": "Bearer token-que-nao-existe"}
    )
    assert resposta.status_code == 401
    assert resposta.json()["detail"] == "credenciais inválidas"


def test_me_token_mal_formado_retorna_401(client: TestClient) -> None:
    resposta = client.get(
        "/auth/me", headers={"Authorization": "Bearer  "}
    )
    assert resposta.status_code == 401
