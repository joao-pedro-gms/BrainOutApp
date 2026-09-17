"""Testes do endpoint ``POST /auth/register``."""
from __future__ import annotations

from fastapi.testclient import TestClient


def test_register_caminho_feliz(client: TestClient) -> None:
    resposta = client.post(
        "/auth/register",
        json={
            "email": "gerente@brainoutapp.dev",
            "senha": "senha-forte-123",
            "perfil": "gerente",
        },
    )
    assert resposta.status_code == 201, resposta.text
    corpo = resposta.json()
    assert corpo["email"] == "gerente@brainoutapp.dev"
    assert corpo["perfil"] == "gerente"
    assert "id" in corpo and len(corpo["id"]) > 0
    assert "senha" not in corpo
    assert "senha_hash" not in corpo
    assert "created_at" in corpo


def test_register_email_duplicado_retorna_409(client: TestClient) -> None:
    payload = {
        "email": "duplicado@brainoutapp.dev",
        "senha": "outra-senha-123",
        "perfil": "colaborador",
    }
    primeira = client.post("/auth/register", json=payload)
    assert primeira.status_code == 201, primeira.text

    segunda = client.post("/auth/register", json=payload)
    assert segunda.status_code == 409
    assert segunda.json()["detail"] == "email já cadastrado"


def test_register_senha_curta_retorna_422(client: TestClient) -> None:
    resposta = client.post(
        "/auth/register",
        json={
            "email": "curto@brainoutapp.dev",
            "senha": "abc",  # < 8 caracteres
            "perfil": "gerente",
        },
    )
    assert resposta.status_code == 422
    # Detalhe da validação Pydantic — formato FastAPI padrão
    corpo = resposta.json()
    assert "detail" in corpo


def test_register_perfil_invalido_retorna_422(client: TestClient) -> None:
    resposta = client.post(
        "/auth/register",
        json={
            "email": "perfilerrado@brainoutapp.dev",
            "senha": "senha-valida-123",
            "perfil": "admin",  # não está no enum
        },
    )
    assert resposta.status_code == 422


def test_register_email_invalido_retorna_422(client: TestClient) -> None:
    resposta = client.post(
        "/auth/register",
        json={
            "email": "nao-eh-email",
            "senha": "senha-valida-123",
            "perfil": "colaborador",
        },
    )
    assert resposta.status_code == 422
