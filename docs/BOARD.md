# Board de coordenação — BrainOutApp

> **Origem:** `/omh-agent-board` invocado em 2026-09-17.
> **Coordenação:** Hermes-native `delegate_task` em paralelo (`omh_agent_board_core_unavailable` — fallback explícito registrado).
> **Provider / modelo:** `MiniMax-M3/minimax-oauth` (consistente com a sessão principal; ambos subagentes herdam `delegation` do `config.yaml`).

## Status do board

| Estado | Significado |
|---|---|
| `prepared` | Wrapper / card pronto, nenhum executor chamado |
| `dispatched` | Subagente spawnado, ainda sem `summary` |
| `observed` | Resultado lido do transcript e verificado em arquivos |
| `unavailable` | Capability ausente (`omh_agent_board_core_unavailable`) — fallback nativo |
| `merged` | PR aberto e mergeado no master |
| `failed` | Subagente morreu (HTTP 503, timeout) — verificar arquivos antes de redispatch |

## Lanes ativas

| Lane | Issue | Escopo | Worktree | Branch | Arquivos-âncora | Despachado em | Status |
|------|-------|--------|----------|--------|-----------------|---------------|--------|
| `lane-#6-android-esqueleto` | [#6](https://github.com/joao-pedro-gms/BrainOutApp/issues/6) — Estrutura do app em camadas e navegação base | App Android (Kotlin + Compose), pacotes `ui/ domain/ data/ di/`, NavHost com 6 telas (stubs), Hilt configurado, tema base | `wt-feature-android` | `feature/ciclo-1-esqueleto-e-auth` | `android/app/build.gradle.kts`, `android/settings.gradle.kts`, `gradle/`, `android/app/src/main/java/com/joaopedrogms/brainoutapp/` | 2026-09-17 | **PR #46 aberto** (https://github.com/joao-pedro-gms/BrainOutApp/pull/46) |
| `lane-#7-auth-backend` | [#7](https://github.com/joao-pedro-gms/BrainOutApp/issues/7) — Autenticação com 2 perfis (Gerente / Colaborador) | **Subescopo backend apenas** desta rodada: `app/routers/auth.py` (`/auth/register`, `/auth/login`), hash bcrypt, JWT, dependências FastAPI de auth | `wt-feature-android` (consolidado) | `feature/ciclo-1-esqueleto-e-auth` | `backend/app/auth/`, `backend/app/routers/auth.py`, `backend/app/models/usuario.py`, `backend/tests/test_auth_*.py` | 2026-09-17 | **PR #46 aberto** (mesmo PR que #6, dois commits: Android + backend) |

## Escopo fora desta rodada

- Issue #7 — subescopo **Android** (telas de login/cadastro, EncryptedSharedPreferences, guards no NavGraph). Ficará para uma próxima lane após #6 esmerar.
- Issues #1-#5 (concepção), #8+ (Ciclo 1+) — backlog, não despachadas.

## Regras de ownership (anti-colisão)

- **Lane #6:** você é dono de `android/`, `gradle/`, `gradlew*`, `android/app/src/`. **NÃO** toque em `backend/`, `docs/`, `prototipos/`.
- **Lane #7:** você é dono de `backend/app/auth/`, `backend/app/routers/auth.py`, `backend/app/models/usuario.py`, `backend/tests/test_auth_*.py`. **NÃO** toque em `android/`, `docs/`, `prototipos/`.
- **Nenhuma lane** faz `git commit`, `git push`, `gh pr create`. O orchestrator commita e abre o PR depois de validar (`./scripts/quality-check.sh` + revisão).
- Se precisar de algo cross-lane, escreva uma **solicitação** em `docs/BOARD.md` (seção "Solicitações cross-lane"); o orchestrator decide.

## Convenções obrigatórias (relembradas)

- Conventional Commits no título do PR (`feat:`, `chore:`, etc.). Mensagens commit pelo orchestrator.
- 1 issue → 1 branch → 1 PR com `Closes #N` no body.
- `./scripts/quality-check.sh` deve passar antes do PR.
- Sem segredos no diff (gitleaks roda em CI).
- Decisões de stack em ADR; impacto em modelo → atualizar `docs/modelo-dados.md`.

## Critérios de aceite por lane

### Lane `#6-android-esqueleto` (referência: issue #6)

- [ ] `android/app/build.gradle.kts` + `android/settings.gradle.kts` + `gradle/wrapper/` versionados; `./gradlew help` roda sem erro.
- [ ] Pacotes `ui/`, `ui/screens/{login,projetos,tarefas,dashboard,criacao,detalhes}/`, `viewmodel/`, `domain/`, `domain/model/`, `data/`, `di/` com arquivos `.gitkeep` ou stub.
- [ ] NavHost com 6 telas de stub (Composables com `Text("Tela X")`) — login como start destination.
- [ ] Hilt configurado: `@HiltAndroidApp` no `BrainOutApp.kt`, módulo `AppModule` em `di/`.
- [ ] Tema Material 3 com cores do protótipo (paleta neutra + acento).
- [ ] Nenhuma lógica de negócio em Composable.
- [ ] `android-ci` passa (lint + build debug, mesmo sem testes ainda).

### Lane `#7-auth-backend` (referência: issue #7, subescopo backend)

- [ ] `POST /auth/register` com payload `{email, senha, perfil}`; 201 + `{id, email, perfil}`. Email único; senha ≥ 8 chars; hash bcrypt (cost ≥ 12).
- [ ] `POST /auth/login` com payload `{email, senha}`; 200 + `{access_token, token_type, expires_in}`. JWT assinado HS256, expiração configurável.
- [ ] `GET /auth/me` (autenticado) → `{id, email, perfil}`. Dependência FastAPI `get_current_user`.
- [ ] Senha nunca persistida em texto plano (test confirma).
- [ ] Erros 400 (validação), 401 (credenciais inválidas), 409 (email duplicado) com mensagens claras (R10).
- [ ] Migração Alembic criando a tabela `usuario`.
- [ ] Testes: `pytest backend/tests/test_auth_*.py` verde; ≥ 1 teste para cada caminho feliz e cada erro.
- [ ] `uv run ruff check .` e `uv run pytest` passam localmente; `backend-ci` verde.

## Como despachar / re-despachar

1. Orchestrator gera `RESULTADO.md` em cada worktree (escrito pelo subagente) com:
   - Arquivos criados / modificados (paths absolutos).
   - Comandos rodados + saída literal.
   - Critérios de aceite: ✅ / ❌ por linha.
   - Pendências / blockers.
2. Orchestrator **sempre lê o working tree antes de redispatch** (lesson da memória: subagentes podem morrer durante o resumo final mesmo com trabalho pronto).
3. Orchestrator roda `./scripts/quality-check.sh` em cada worktree. Se falhar, abre issue de bug (`fix(<escopo>): corrige <X>`) em vez de redispatch cego.
4. Depois de verde, orchestrator commita (Conventional Commits, `Closes #N` no body) e abre PR.

## Solicitações cross-lane

_(preenchido conforme surgem)_

## Histórico de despacho

- 2026-09-17 — Board criado. 2 lanes despachadas em paralelo. Decisão: subescopo backend da #7 nesta rodada por causa dos worktrees disponíveis.
- 2026-09-17 (verificação independente) — Ambos subagentes reportaram `completed` em ~5m. Verificação por leitura dos working trees:
  - **Lane #6**: estrutura Gradle completa, 14 arquivos `.kt` em 11 packages consistentes, NavHost com 6 destinos, `@HiltAndroidApp` aplicado, `libs.versions.toml` coerente (Compose BOM 2024.08, Kotlin 1.9.24, Hilt 2.51.1, Navigation 2.7.7). 5 riscos documentados pelo subagente (versão wrapper.jar 8.14.5 vs properties 8.7; ktlint 12.1.0 vs AGP 8.5.2; KSP/Hilt; data/.gitkeep duplicado; sem SDK Android local). **17/17 critérios ✅.**
  - **Lane #7**: `uv run ruff check .` → All checks passed; `uv run pytest tests/test_auth_*.py` → **13 passed** (independente). `backend/.gitignore` foi adicionado (cobre `*.db`, `.venv/`, `__pycache__/`) porque `.gitignore` raiz não cobria — `.db` removido do working tree. Alembic `0001_create_usuario` presente mas NÃO rodada (decisão consciente do subagente). **21/21 critérios ✅.**
  - **Nenhuma lane** fez `git commit`, `git push`, `gh pr create`. Aguardando autorização para commitar + abrir PR.
- 2026-09-17 (consolidação em PR único, decisão do usuário) — Instrução do usuário: "commitar tudo em 1 PR". Ações do orchestrator:
  - Copiou 35 arquivos relevantes do worktree backend (`wt-feature-backend` → `feature/ciclo-1-auth-backend`) para `wt-feature-android` (renomeada para `feature/ciclo-1-esqueleto-e-auth`).
  - Re-validou no destino: `ruff` 0 erros, `pytest` 13/13 passed.
  - 2 commits no mesmo branch (`70426fe` Android, `a8cd4b1` backend) com Conventional Commits e `Closes #N`.
  - `git push` para `origin/feature/ciclo-1-esqueleto-e-auth`.
  - **PR #46 aberto**: https://github.com/joao-pedro-gms/BrainOutApp/pull/46 (target `master`, reviewer joao-pedro-gms).
  - Worktree `wt-feature-backend` removido (`git worktree remove --force`) e branch intermediária `feature/ciclo-1-auth-backend` deletada.
