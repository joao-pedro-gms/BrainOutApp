# ADR 0002 — Backend: Python + FastAPI

- **Status:** Aceito
- **Data:** 2026-09-17
- **Issue:** #14

## Contexto

A Seção 4 do documento norteador admite: API própria (Spring Boot, Node.js, FastAPI ou equivalente) ou BaaS (Firebase, Supabase). Restrição: **sem no-code/low-code** e sem reprodução de terceiros.

## Decisão

**Python 3.12 + FastAPI + SQLAlchemy + Alembic**. Banco: SQLite em dev, PostgreSQL em produção (self-hosted, custo zero).

## Consequências

**Positivas:**
- FastAPI gera OpenAPI/Swagger automaticamente → cumpre "API documentada" da issue #14
- Pydantic dá validação de entrada sem boilerplate
- SQLAlchemy é o ORM Python mais maduro; Alembic versiona migrations
- Custo zero (SQLite local; Postgres self-hosted em VPS gratuita ou no GitHub Codespaces)
- Curva menor para ADS (Python é a linguagem do curso)

**Negativas:**
- Async do FastAPI exige cuidado (misturar com sync trava event loop)
- Postgres em produção precisa de backup/HA (decidir na N2)

**Mitigações:**
- Todo o I/O usa `async def` + `AsyncSession`
- Backup do Postgres documentado em [`dev-guide.md`](../dev-guide.md)
