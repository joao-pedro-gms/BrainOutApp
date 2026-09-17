# BrainOutApp — Backend (FastAPI)

API REST do BrainOutApp (Projeto Integrador ADS 2026/2). Implementa a
**issue #7**: autenticação JWT (bcrypt + access token) com perfis
`gerente` e `colaborador`.

## Stack

- **Python 3.12+**
- **FastAPI 0.115+** (rotas REST + validação automática)
- **SQLAlchemy 2.x** (ORM)
- **Pydantic v2** (schemas)
- **Alembic** (migrations)
- **passlib[bcrypt]** + **python-jose** (auth)
- **uv** (gerenciador de pacotes/venv)
- **pytest** + **httpx** (testes)

## Setup local

Pré-requisito: **uv** ([instalação](https://docs.astral.sh/uv/getting-started/installation/)).

```bash
cd backend
uv sync                              # cria .venv, instala deps de prod+dev via uv.lock
cp .env.example .env                 # editar com seus valores
# IMPORTANTE: gere um JWT_SECRET real com:
#   python -c "import secrets; print(secrets.token_urlsafe(32))"
```

## Comandos úteis

```bash
uv run uvicorn app.main:app --reload  # sobe o servidor em http://127.0.0.1:8000
uv run pytest                        # roda os testes
uv run ruff check .                  # lint
uv run ruff format .                 # format
uv run alembic revision --autogenerate -m "msg"   # cria nova migration
uv run alembic upgrade head          # aplica migrations
uv run pip-audit                     # auditoria de vulnerabilidades
```

## Endpoints — Autenticação (issue #7)

Todos os endpoints vivem sob o prefixo `/auth`. Senhas trafegam **sempre**
em texto plano pelo canal (HTTPS obrigatório em produção), mas são
**imediatamente descartadas após o bcrypt** no servidor.

### `POST /auth/register`

Cria um novo usuário.

**Request:**

```bash
curl -X POST http://127.0.0.1:8000/auth/register \
  -H "Content-Type: application/json" \
  -d '{
        "email": "gerente@brainoutapp.dev",
        "senha": "senha-forte-123",
        "perfil": "gerente"
      }'
```

**Respostas:**

| Status | Significado                                                            |
|--------|------------------------------------------------------------------------|
| `201`  | Criado. Body: `UsuarioPublic` (sem `senha_hash`)                       |
| `409`  | `{"detail": "email já cadastrado"}`                                    |
| `422`  | Payload inválido (Pydantic: email/senha/perfil)                        |

### `POST /auth/login`

Autentica e retorna um JWT (HS256, expiração padrão 60 min).

**Request:**

```bash
curl -X POST http://127.0.0.1:8000/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "gerente@brainoutapp.dev", "senha": "senha-forte-123"}'
```

**Respostas:**

| Status | Significado                                                            |
|--------|------------------------------------------------------------------------|
| `200`  | `{"access_token": "...", "token_type": "bearer", "expires_in": 3600}`  |
| `401`  | `{"detail": "credenciais inválidas"}`                                  |
| `422`  | Payload inválido                                                       |

### `GET /auth/me`

Retorna os dados do usuário dono do token.

**Request:**

```bash
curl http://127.0.0.1:8000/auth/me \
  -H "Authorization: Bearer <access_token>"
```

**Respostas:**

| Status | Significado                                                            |
|--------|------------------------------------------------------------------------|
| `200`  | `UsuarioPublic` (id, email, perfil, created_at)                       |
| `401`  | Sem token / token inválido / token expirado (`credenciais inválidas`)  |

## Tabela de erros consolidada

| Endpoint             | 201/200 | 401 | 409 | 422 |
|----------------------|:-------:|:---:|:---:|:---:|
| `POST /auth/register`|    ✅   |  —  |  ✅ |  ✅ |
| `POST /auth/login`   |    ✅   |  ✅ |  —  |  ✅ |
| `GET  /auth/me`      |    ✅   |  ✅ |  —  |  —  |

## Estrutura

```
backend/
├── app/
│   ├── auth/         # JWT + bcrypt + deps FastAPI
│   ├── db/           # engine, sessão, Base
│   ├── models/       # ORM (Usuario)
│   ├── routers/      # endpoints FastAPI (auth)
│   ├── schemas/      # Pydantic (entrada/saída)
│   ├── services/     # use cases (regras de negócio)
│   ├── config.py     # Settings (env)
│   ├── logging_config.py
│   └── main.py       # entry-point FastAPI
├── alembic/          # migrations (Alembic)
├── tests/            # pytest
├── alembic.ini
├── pyproject.toml
└── uv.lock
```

## Variáveis de ambiente

Veja `.env.example`. As principais:

| Var                | Padrão                          | Obrigatória | Descrição                              |
|--------------------|---------------------------------|-------------|----------------------------------------|
| `DATABASE_URL`     | `sqlite:///./brainoutapp.db`    | não         | URL SQLAlchemy (SQLite dev / Postgres prod) |
| `JWT_SECRET`       | `change-me-...`                 | **sim** (≥ 32 chars) | Segredo HS256. **Troque em produção.**  |
| `JWT_EXPIRES_MIN`  | `60`                            | não         | Expiração do access token (minutos)    |
| `BCRYPT_ROUNDS`    | `12`                            | não         | Custo bcrypt (≥ 12 recomendado)        |
| `LOG_LEVEL`        | `INFO`                          | não         | Nível de log                           |

> ⚠️ **Nunca** commite `.env`. O `.gitignore` do repositório raiz já cobre isso.

## Lockfile

`uv.lock` é versionado. Para builds reproduzíveis use sempre
`uv sync --frozen` (CI).