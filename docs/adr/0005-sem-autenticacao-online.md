# ADR 0005 — Sem autenticação online

- **Status:** Aceito
- **Data:** 2026-09-17
- **Substitui / complementa:** ADR-0002 (backend FastAPI deixa de existir no plano)
- **Issue:** #14 (avaliada e fechada como não-aplicável)

## Contexto

O documento norteador do Projeto Integrador (Seção 4) admitia duas vias para a persistência compartilhada: **API própria** (FastAPI, Spring Boot, Node, …) ou **BaaS** (Firebase, Supabase). O ADR-0002 (2026-09-17) tinha escolhido **FastAPI + SQLAlchemy + Alembic** com SQLite/PostgreSQL.

Em 2026-09-17, após o PR #46 abrir com o backend de autenticação (`/auth/register`, `/auth/login`, JWT, bcrypt, SQLAlchemy `Usuario`), o usuário reconsiderou o escopo durante a revisão e decidiu simplificar a aplicação:

> _"Sem backend. App 100% offline. Sem login online. Perfil local."_

A motivação registrada foi reduzir **complexidade operacional desnecessária** para um app de gestão de projetos voltado a **pequenos times / uso pessoal**. Toda a equipe é, na prática, dona do próprio dispositivo com os próprios dados — a centralização em servidor não agregava valor proporcional ao custo (deploy, hospedagem, migrations, segurança de credenciais, LGPD para dados em servidor).

PRD original previa 2 perfis (Gerente / Colaborador) com permissões diferentes; a distinção é **preservada** localmente (ADR-0006), apenas deixa de ser uma autenticação contra servidor.

## Decisão

**Não há autenticação online.** Especificamente:

- ❌ Sem backend de usuário.
- ❌ Sem JWT, sem sessão, sem cookie.
- ❌ Sem `/auth/register`, `/auth/login`, `/auth/me`.
- ❌ Sem bcrypt/argon2 no servidor (e não precisa no cliente).
- ❌ Sem `Retrofit`, `OkHttp`, interceptors, certificados cliente.
- ❌ Sem migração de dados entre dispositivos.
- ❌ Sem recuperação de senha.
- O "usuário" do app é o **dono do dispositivo**. O **perfil** (Gerente/Colaborador) é seleção local, persistida em DataStore Preferences (chave `perfil_usuario`).
- O **acesso ao app** é opcionalmente protegido por **senha local** (AppLock), com hash em `EncryptedSharedPreferences`. Detalhes em [ADR-0006](0006-perfil-local-opcional.md).

## Opções consideradas

| Opção | Descrição | Pró | Contra | Decisão |
|-------|-----------|-----|--------|---------|
| **A — Online com JWT** (status quo do ADR-0002 + PR #46) | Backend FastAPI com `POST /auth/register`, `POST /auth/login`, JWT assinado, bcrypt. Sincronização entre dispositivos. | Sincronização multi-device real; isolamento de credenciais no servidor. | Custo operacional (deploy, backup, LGPD); complexidade (refresh token, rotação); overkill para uso pessoal/de times pequenos; adiciona superfície de ataque. | ❌ Rejeitada |
| **B — Local com perfil único** | Sem distinção Gerente/Colaborador. Toda a UI trata igual. | Implementação mais simples; menos telas. | PRD exige 2 perfis com permissões diferentes (RN implicada em R2); quebraria o requisito. | ❌ Rejeitada |
| **C — Local com 2 perfis** (escolhida) | Sem backend, sem rede. Perfil selecionado no onboarding; persistido em DataStore. Opcionalmente, senha local (AppLock). | Mantém distinção Gerente/Colaborador (PRD); zero ops; zero dependências externas; dados ficam no device do usuário (LGPD-friendly por default). | Sem sync multi-device; dependência total do backup do Android (mitigado por export/import futuro, se virar issue). | ✅ **Escolhida** |
| **D — BaaS (Firebase Auth)** | Google Identity Toolkit / Firebase Auth gerencia sessão. | SDK pronto, login social, sem manter backend. | Dependência de SaaS proprietário; excede a regra do projeto ("stack open-source / self-hosted"); adiciona coleta de dados do usuário em servidor terceiro. | ❌ Rejeitada |

## Consequências

### Positivas
- 🔥 **Complexidade operacional removida**: sem deploy, sem banco, sem migrations Alembic, sem HTTPS/TLS, sem refresh tokens.
- 🪶 **App fica leve**: APK sem dependências de rede (Retrofit/OkHttp/JWT/servidor).
- 🔐 **Privacidade por padrão**: dados ficam no dispositivo; usuário controla o quê compartilhar.
- 📉 **Custo zero total**: não há servidor a pagar (já era zero, agora também não há infra).

### Negativas / trade-offs
- 📵 **Sem sync multi-device**: editar projeto no celular não aparece no tablet. Cada instalação é independente. (Mitigação futura: export/import via JSON, se virar issue — não está no roadmap atual.)
- 🔑 **Sem "esqueci minha senha"**: se o usuário esquecer a senha local, perde o AppLock; mas os dados continuam acessíveis resetando o app (decisão explícita em ADR-0006).
- 👤 **Não distingue usuários no mesmo device**: dois membros da família usando o mesmo tablet verão a mesma base. (Fora do escopo — app é single-user por device.)
- 📜 Rastreabilidade R↔issue muda:
  - **R2** ("Autenticação com 2 perfis") deixa de ser autenticação → vira **seleção de perfil local**. Status no README: 🟡 não-aplicável como auth (ver ADR-0006).
  - **R6** ("Persistência remota + sincronização") → 🟡 **não-aplicável**. App 100% offline.
  - **R5** ("Persistência local + modo offline") continua ✅ e vira o pilar central (Room como fonte da verdade).
  - **R12** (camadas + sem segredos no repo) → mais simples ainda: não há credenciais a esconder.

### Issues e ADRs impactados

| Item | Ação | Por quê |
|------|------|---------|
| Issue **#7** ("subescopo backend auth") | 🔁 **Reciclar** para "seleção de perfil local sem senha". | O que era backend vira feature Android (ADR-0006). |
| Issue **#14** ("ADR backend FastAPI") | ✅ Avaliada → ❌ Fechada como **não-aplicável**. | Backend Python sai do plano; ADR-0002 perde a justificativa operacional. |
| Issue **#15** ("integração externa") | ⏸ Deixar em avaliação. | Sem backend, integração externa precisaria ser revisada (consumir direto do app). Pode virar ADR-0007 em outra rodada. |
| ADR-0002 (FastAPI) | ⚠️ **Torna-se obsoleto de fato** — esta ADR (0005) e a issue #14 fechada são o registro da reversão. | Nenhum trecho do ADR-0002 é violado; ele só deixa de ser executado. O conteúdo histórico é preservado para rastreabilidade. |
| `docs/modelo-dados.md` | ✏️ Removida tabela `usuarios`; removida coluna `server_version`; nota sobre perfil em DataStore Preferences. | Reflete a nova realidade. |
| `docs/arquitetura.md` | ✏️ Diagrama de camadas offline-only; removidas seções de backend, FastAPI, Retrofit, JWT, sync. | Reflete a nova arquitetura. |
| `docs/regras-negocio.md` | ✏️ Remover menção a `app/services/*` (backend). Regras ficam só no Android. | Sem servidor para revalidar. |
| `README.md` | ✏️ R2 e R6 marcados como 🟡 não-aplicáveis. | Rastreabilidade. |

### Dependências removidas (a atualizar no build quando a lane vizinha `lane-design-system`/issue #7 iniciar)

| Pacote | Onde estava | Status |
|--------|-------------|--------|
| `retrofit` / `retrofit-converter-*` | `android/app/build.gradle.kts` | ❌ Removido |
| `okhttp` / `okhttp-logging` | `android/app/build.gradle.kts` | ❌ Removido |
| `bcrypt` (Python) | `backend/pyproject.toml` | ❌ Removido |
| `python-jose` / `pyjwt` | `backend/pyproject.toml` | ❌ Removido |
| `fastapi`, `uvicorn`, `pydantic`, `sqlalchemy`, `alembic`, `aiosqlite` | `backend/pyproject.toml` | ❌ Pasta `backend/` sai do plano |
| `androidx.datastore:datastore-preferences` | _novo_ | ✅ Adicionado (perfil local) |
| `androidx.security:security-crypto` | _novo_ | ✅ Adicionado (senha local opcional) |

> 📝 Atualizar `libs.versions.toml` e `pyproject.toml` é responsabilidade da lane de implementação, não desta ADR. Esta ADR registra apenas a decisão e seu impacto documental.

---

## Próximos passos

1. ✅ Atualizar `docs/modelo-dados.md` (remover `usuarios`, adicionar nota sobre perfil em DataStore).
2. ✅ Atualizar `docs/arquitetura.md` (camadas offline-only).
3. ✅ Criar `docs/adr/0006-perfil-local-opcional.md` (detalha o "como" do perfil + senha opcional).
4. ✅ Atualizar `README.md` (R2/R6 como não-aplicáveis).
5. 🟡 Reciclar issue **#7** para "seleção de perfil local sem senha".
6. 🟡 Fechar issue **#14** como não-aplicável.
7. 🟡 Reabrir `backend/` para remoção (ou deixar como histórico até N2? — a decidir pelo orchestrator).
