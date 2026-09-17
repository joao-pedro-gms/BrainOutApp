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
| `lane-onboarding` | [#7-reciclado](https://github.com/joao-pedro-gms/BrainOutApp/issues/7) — Perfil local (subescopo) | Tela de onboarding (escolha Gerente/Colaborador), DataStore Preferences para persistir perfil, NavHost começa em `onboarding` se perfil indefinido | `wt-onboarding` | `feature/onboarding` | `android/app/src/main/java/com/joaopedrogms/brainoutapp/ui/screens/onboarding/`, `data/preferences/`, `ui/navigation/BrainOutAppNavHost.kt` | 2026-09-17 | **merged (#52)** |
| `lane-crud-projetos` | [#8](https://github.com/joao-pedro-gms/BrainOutApp/issues/8) — CRUD Projetos | Room entity `Projeto` (UUID v7, nome, descrição, prazo, status, soft delete), DAO, Repository, ViewModel, telas reais (lista/criar/editar/detalhe), validações, empty state | `wt-crud-projetos` | `feature/crud-projetos` | `android/app/src/main/java/com/joaopedrogms/brainoutapp/data/local/`, `domain/model/`, `domain/usecase/`, `viewmodel/`, telas | 2026-09-17 | **merged (#53)** |
| `lane-applock` | [#7-reciclado](https://github.com/joao-pedro-gms/BrainOutApp/issues/7) — Perfil local (subescopo) | Tela de bloqueio no cold start se senha definida, EncryptedSharedPreferences para hash, `androidx.security:security-crypto` no `libs.versions.toml` | `wt-applock` | `feature/applock` | `android/app/src/main/java/com/joaopedrogms/brainoutapp/ui/screens/applock/`, `data/security/` | 2026-09-17 | **merged (#54)** |
| `lane-crud-tarefas` | [#10](https://github.com/joao-pedro-gms/BrainOutApp/issues/10) — CRUD Tarefas | Room entity `Tarefa` com FK para `Projeto` (RESTRICT, registrado ADR-0007), DAO, Repository, ViewModel, telas, seletor de projeto pai | `wt-crud-tarefas` | `feature/crud-tarefas` | `android/app/src/main/java/com/joaopedrogms/brainoutapp/data/local/`, telas de tarefas | 2026-09-17 | **merged (#55)** |
| `lane-rn` | [#11](https://github.com/joao-pedro-gms/BrainOutApp/issues/11) — RN01-RN03 | Implementar em `domain/usecase/`: RN01 (dependências), RN02 (projeto sem tarefa aberta), RN03 (prazo tarefa ≤ prazo projeto). Testes JUnit cobrindo cada regra | `wt-rn` | `feature/rn01-03` | `android/app/src/main/java/com/joaopedrogms/brainoutapp/domain/usecase/` | 2026-09-17 | **merged (#56)** |
| `lane-filtro-busca` | [#12](https://github.com/joao-pedro-gms/BrainOutApp/issues/12) — Filtro + busca + ordenação | Chips de status (Aberto/Concluído/Atrasado), busca por nome (Room LIKE — FTS só se necessário), sort por prazo/nome, debounce na busca | `wt-filtro-busca` | `feature/filtro-busca` | telas de projetos e tarefas | 2026-09-17 | **merged (#57)** |
| `lane-dashboard` | [#13](https://github.com/joao-pedro-gms/BrainOutApp/issues/13) — Dashboard | Tela Dashboard com cards: atrasadas, concluídas vs abertas, prazo 7d. Compose Canvas simples (barras horizontais) | `wt-dashboard` | `feature/dashboard` | `android/app/src/main/java/com/joaopedrogms/brainoutapp/ui/screens/dashboard/` | 2026-09-17 | **merged (#58)** |
| `lane-uistate` | [#17](https://github.com/joao-pedro-gms/BrainOutApp/issues/17) — Tratamento de erros | Refatorar todas as telas para `sealed class UiState { Loading, Empty, Error, Content }`, mensagens acionáveis | _(a criar — Fase 4)_ | `feature/uistate` | telas, `ui/components/` | 2026-09-17 | **pulada** (UiState base já granular o suficiente; refino entra em iteração futura) |
| `lane-release-v0.2` | — | Tag `v0.2.0`, ajustar workflow `release.yml` se necessário, garantir que APK debug é anexado, validar CI verde | (raiz) | — | `.github/workflows/release.yml` | 2026-09-17 | **merged (#59)** |
| `lane-compilacao` | — | Workflow reutilizável `verify-android.yml` (roda gradlew assembleDebug + ktlintCheck + testDebugUnitTest em ubuntu-latest com SDK 34) + script `lane-compilacao/verificar.sh` (análise estática local) | (raiz) | — | `.github/workflows/verify-android.yml`, `lane-compilacao/verificar.sh` | 2026-09-17 | **merged (#62, #64, #65, #69, #70, #71, #72, #73, #74, #75, #76, #77, #78, #79, #80, #81)** — pipeline verde após 16 PRs de fix de CI |

## Plano v0.2

- **Fase 1 (paralelo):** onboarding + crud-projetos.
- **Fase 2 (paralelo):** applock + crud-tarefas (dependem da Fase 1).
- **Fase 3 (paralelo):** rn01-03 + filtro-busca + dashboard (dependem da Fase 2).
- **Fase 4:** uistate + release-v0.2.

## Escopo fora desta rodada

- Issue #7 — **reciclar**: reescrever para "seleção de perfil local sem senha" (sem backend). Pendente após #48 mergar.
- Issue #14 — **avaliar**: o backend FastAPI deixa de ser necessário? Se sim, fechar como não-aplicável e remover do plano. Pendente.
- Issue #15 — **avaliar**: integração externa (BrasilAPI feriados) sem backend? Possível consumir direto do app, mas vira ADR-0007 separado. Pendente.
- Issues #1-#5 (concepção), #8+ (Ciclo 1+) — backlog.

## Regras de ownership (anti-colisão)

- **Lane design-system:** você é dono de `docs/design-system.md` (novo), `android/app/src/main/java/com/joaopedrogms/brainoutapp/ui/theme/*.kt` (Color/Type/Theme). **NÃO** toque em telas (`ui/screens/**`), backend, modelos, ADRs.
- **Lane diagramação-datamodels:** você é dono de `docs/modelo-dados.md`, `docs/arquitetura.md`, `docs/adr/0005-*.md`, `docs/adr/0006-*.md`. **NÃO** toque em `android/`, `backend/`, código de produção.
- **Nenhuma lane** faz `git commit`, `git push`, `gh pr create`. O orchestrator commita e abre o PR depois de validar.
- Se precisar de algo cross-lane, escreva uma **solicitação** em `docs/BOARD.md` (seção "Solicitações cross-lane"); o orchestrator decide.

## Convenções obrigatórias (relembradas)

- Conventional Commits no título do PR (`feat:`, `chore:`, etc.). Mensagens commit pelo orchestrator.
- 1 issue → 1 branch → 1 PR com `Closes #N` no body.
- `./scripts/quality-check.sh` deve passar antes do PR.
- Sem segredos no diff (gitleaks roda em CI).
- Decisões de stack em ADR; impacto em modelo → atualizar `docs/modelo-dados.md`.

## Critérios de aceite por lane

### Lane `lane-design-system`

- [ ] `docs/design-system.md` com seções: Princípios, Paleta (cores primária/secundária/semânticas), Tipografia (escala e pesos), Espaçamentos (escala 4/8/16/24/32/48), Raios de borda, Elevação/sombras, Ícones (mapeamento Lucide).
- [ ] `android/app/src/main/java/com/joaopedrogms/brainoutapp/ui/theme/Color.kt` declara tokens (Nomes semânticos: `surfacePrimary`, `surfaceSecondary`, `onSurfacePrimary`, `accentPrimary`, `stateSuccess`, `stateError`, etc.) — Material 3 `ColorScheme`.
- [ ] `Type.kt` com escala tipográfica (display/headline/title/body/label) — Material 3 `Typography`.
- [ ] `Theme.kt` aplica `ColorScheme` + `Typography` em `lightColorScheme()` e `darkColorScheme()`.
- [ ] Compatibilidade com Compose BOM 2024.08 + Material 3.
- [ ] Mapeamento Lucide icons para contextos recorrentes: login (User, Lock, Eye, EyeOff), projetos (Folder, Plus, Edit, Trash), tarefas (CheckSquare, Square, Clock, AlertCircle), dashboard (BarChart, Calendar, TrendingUp), navegação (Home, Settings, ChevronRight). Lista deve ter pelo menos 15 ícones mapeados.
- [ ] Nenhuma alteração fora de `docs/design-system.md` e `android/app/src/main/java/com/joaopedrogms/brainoutapp/ui/theme/`.

### Lane `lane-diagramacao-datamodels`

- [ ] `docs/modelo-dados.md` reescrito: ER (mermaid) **sem** tabela `usuarios`, **sem** `auth/sync`. Mantém `projeto` e `tarefa` com FK. Decisões registradas (UUID v7, soft delete).
- [ ] `docs/arquitetura.md` reescrito: diagrama de camadas **offline-only** (sem coluna "Backend Python", sem setas HTTPS para API). App = Compose + Room + WorkManager. Anota as camadas removidas.
- [ ] `docs/adr/0005-sem-autenticacao-online.md` criado: contexto (decisão do usuário em 2026-09-17), opções consideradas (online com JWT vs local com perfil vs offline total), decisão (offline total + perfil local), consequências (R2/R6 ficam não-aplicáveis; #7/#14 reciclados).
- [ ] `docs/adr/0006-perfil-local-opcional.md` criado: contexto, decisão (perfil escolhido no primeiro uso, senha opcional só para bloquear app), armazenamento (DataStore Preferences), consequências.
- [ ] README.md seção "Requisitos" tabela atualizada: R2 marcado como "não-aplicável — autenticação local sem senha (ver ADR-0005/0006)"; R6 marcado como "não-aplicável — app 100% offline".
- [ ] Nenhuma alteração em código de produção (`android/`, `backend/`).

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
- 2026-09-17 (mudança de escopo: sem auth online) — Decisão do usuário: aplicação 100% local, perfil de usuário local, senha opcional. Ações do orchestrator:
  - Fechou PR #46 sem merge (superseded).
  - `git revert a8cd4b1` no branch `feature/ciclo-1-esqueleto-e-auth` → commit `bed20ea` (Conventional Commits).
  - Renomeou branch para `feature/ciclo-1-esqueleto-android`, deletou branch remota `feature/ciclo-1-esqueleto-e-auth`.
  - Push do branch novo (3 commits: 70426fe + a8cd4b1 + bed20ea revert).
  - **PR #48 aberto**: https://github.com/joao-pedro-gms/BrainOutApp/pull/48 (só Android + revert; closes #6).
  - Board atualizado com 2 novas lanes a despachar: `lane-design-system` e `lane-diagramacao-datamodels`.
- 2026-09-17 (verificação independente das lanes design + diagramação) — Ambas lanes reportaram `completed` em ~3m48s.
  - **Lane design-system**: 30 tokens semânticos em `Color.kt` (verificado); `Type.kt` declara os 15 slots Material 3 com FontWeight/fontSize/lineHeight/letterSpacing do M3 type scale oficial; `Theme.kt` aplica Light/Dark via `isSystemInDarkTheme()`; `docs/design-system.md` com 312 linhas e 10 seções; mapeamento Lucide com 19 ícones. **15/15 critérios ✅**. Commit `deb7a32`.
  - **Lane diagramação**: `modelo-dados.md` sem `usuarios`; `arquitetura.md` com 1 mermaid + seção explícita "Removido nesta revisão" listando FastAPI/SQLAlchemy/Alembic/Retrofit/OkHttp/JWT/bcrypt/structlog; ADRs 0005 (98 linhas) e 0006 (159 linhas); README R2/R6 marcados não-aplicáveis. **6/6 critérios ✅**. Commit `16750b0`.
  - **PR #49 aberto**: https://github.com/joao-pedro-gms/BrainOutApp/pull/49 (design-system).
  - **PR #50 aberto**: https://github.com/joao-pedro-gms/BrainOutApp/pull/50 (diagramação + ADRs).
