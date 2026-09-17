"""Testes do endpoint ``POST /auth/login``."""
from __future__ import annotations

from fastapi.testclient import TestClient


def _registrar(client: TestClient, email: str, senha: str = "senha-forte-123") -> None:
    r = client.post(
        "/auth/register",
        json={"email": email, "senha": senha, "perfil": "gerente"},
    )
    assert r.status_code == 201, r.text


def test_login_caminho_feliz_retorna_token(client: TestClient) -> None:
    _registrar(client, "login-ok@brainoutapp.dev")
    resposta = client.post(
        "/auth/login",
        json={"email": "login-ok@brainoutapp.dev", "senha": "senha-forte-123"},
    )
    assert resposta.status_code == 200, resposta.text
    corpo = resposta.json()
    assert corpo["token_type"] == "bearer"
    assert isinstance(corpo["access_token"], str) and len(corpo["access_token"]) > 0
    assert corpo["expires_in"] == 60 * 60  # 60 minutos em segundos


def test_login_senha_errada_retorna_401(client: TestClient) -> None:
    _registrar(client, "login-erro@brainoutapp.dev")
    resposta = client.post(
        "/auth/login",
        json={"email": "login-erro@brainoutapp.dev", "senha": "outra-senha"},
    )
    assert resposta.status_code == 401
    assert resposta.json()["detail"] == "credenciais inválidas"


def test_login_email_inexistente_retorna_401(client: TestClient) -> None:
    resposta = client.post(
        "/auth/login",
        json={
            "email": "fantasma@brainoutapp.dev",
            "senha": "qualquer-coisa",
        },
    )
    assert resposta.status_code == 401
    assert resposta.json()["detail"] == "credenciais inválidas"


def test_login_payload_invalido_retorna_422(client: TestClient) -> None:
    resposta = client.post(
        "/auth/login",
        json={"email": "sem-senha"},  # falta campo senha
    )
    assert resposta.status_code == 422
