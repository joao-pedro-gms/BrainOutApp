# 🏗️ Arquitetura

> **BrainOutApp** — documento vivo.
> 📅 Última atualização: 2026-09-17

## 📐 Visão geral

Aplicativo Android nativo (Kotlin + Jetpack Compose) + API REST em Python (FastAPI). Persistência local no dispositivo (Room) com sincronização bidirecional contra a API. Modo **offline-first**: o app funciona sem rede e sincroniza quando ela volta.

```text
┌──────────────────────────┐         ┌────────────────────────┐
│ 📱 Android (Kotlin)      │         │ 🐍 Backend (Python)    │
│                          │         │                        │
│  ┌────────────────────┐  │         │  ┌──────────────────┐  │
│  │ 🎨 UI (Compose)     │  │  HTTPS  │  │ 🚦 FastAPI       │  │
│  │  - screens/         │  │ ◄─────► │  │ /auth /projects  │  │
│  │  - components/      │  │   JWT   │  │ /tasks /sync     │  │
│  └────────┬───────────┘  │         │  └────────┬─────────┘  │
│           │              │         │           │            │
│  ┌────────▼───────────┐  │         │  ┌────────▼─────────┐  │
│  │ 🧠 ViewModel        │  │         │  │ ⚙️  Services      │  │
│  │ (StateFlow)        │  │         │  │ (use cases)      │  │
│  └────────┬───────────┘  │         │  └────────┬─────────┘  │
│           │              │         │           │            │
│  ┌────────▼───────────┐  │         │  ┌────────▼─────────┐  │
│  │ 📐 Domain           │  │         │  │ 🗄️  Repositories │  │
│  │ (use cases,         │  │         │  │ (SQLAlchemy)     │  │
│  │  RN01-RN03)         │  │         │  │                 │  │
│  └────────┬───────────┘  │         │  └────────┬─────────┘  │
│           │              │         │           │            │
│  ┌────────▼───────────┐  │         │  ┌────────▼─────────┐  │
│  │ 💾 Data              │  │         │  │ 🐘 PostgreSQL     │  │
│  │  - Room (local)     │  │         │  │ (ou SQLite dev)  │  │
│  │  - Retrofit (sync)  │  │         │  │                  │  │
│  └────────────────────┘  │         │  └──────────────────┘  │
└──────────────────────────┘         └────────────────────────┘
```

---

## 🧱 Camadas

### 📱 Android (R12 — separação obrigatória)

> **Regra de ouro (R12):** Activity / Fragment / Composable **nunca** contém lógica de negócio. ViewModel orquestra → Use Case executa regra → Repository persiste.

| Camada | Pacote | Responsabilidade |
|--------|--------|------------------|
| 🎨 `ui/` | `com.joaopedrogms.brainoutapp.ui` | Composables, navegação, tema Material 3 |
| 🎨 `ui/screens/<feature>` | `...ui.screens.projetos` | Telas por feature |
| 🧠 `viewmodel/` | `...viewmodel` | `StateFlow` da UI, eventos |
| 📐 `domain/` | `...domain.model` | Entidades puras, regras RN01-RN03 |
| 📐 `domain/usecase/` | `...domain.usecase` | Casos de uso (criar projeto, marcar tarefa) |
| 💾 `data/local/` | `...data.local` | Room: DAOs, entities, migrations |
| 💾 `data/remote/` | `...data.remote` | Retrofit: API, DTOs, mappers |
| 💾 `data/repository/` | `...data.repository` | Implementação (local + remoto + sync) |
| 🪡 `di/` | `...di` | Módulos Hilt |

### 🐍 Backend (FastAPI)

| Camada | Módulo | Responsabilidade |
|--------|--------|------------------|
| 🚦 `app/routers/` | FastAPI | Endpoints REST (`/auth`, `/projects`, `/tasks`, `/sync`) |
| ⚙️ `app/services/` | `services` | Casos de uso (validação, RN do servidor) |
| 🗄️ `app/models/` | SQLAlchemy 2 | ORM (entities) |
| 📋 `app/schemas/` | Pydantic | Validação de entrada/saída |
| 🔌 `app/db/` | conexão | Engine, sessão, migrations (Alembic) |
| 🔐 `app/auth/` | JWT | Geração, validação, dependências |
| 🧪 `tests/` | pytest + httpx | Unit + integration |

---

## 🗃️ Modelo de dados

```text
┌─────────────┐ 1     n ┌─────────────┐
│  📁 Projeto │────────►│ ✅  Tarefa   │
├─────────────┤         ├─────────────┤
│ id (UUID)   │         │ id (UUID)   │
│ nome        │         │ projeto_id  │
│ descricao   │         │ titulo      │
│ data_inicio │         │ descricao   │
│ prazo       │         │ responsavel │
│ status      │         │ prioridade  │
│ created_at  │         │ status      │
│ updated_at  │         │ prazo       │
└─────────────┘         │ created_at  │
                        │ updated_at  │
┌─────────────┐         └─────────────┘
│  👤 Usuario │
├─────────────┤
│ id (UUID)   │
│ email       │
│ senha_hash  │
│ perfil      │  enum: gerente | colaborador
│ created_at  │
└─────────────┘
```

Decisões detalhadas em [`modelo-dados.md`](modelo-dados.md):

- 🔑 **UUID v7** como PK (ordenável por tempo, melhor para sync)
- 🗑️ **Soft delete** com `deleted_at` (mantém histórico p/ sync e relatórios)
- 🕐 Timestamps em **UTC**, conversão no cliente
- 🔗 `ON DELETE RESTRICT` em Tarefa→Projeto (RN02)

---

## 📐 Regras de negócio (RN01-RN03)

> Implementadas em `domain/` no Android **E** em `app/services/` no backend — **defesa em profundidade**. Cliente bloqueia antes da chamada de rede; servidor revalida.

| ID | Regra | Onde validar | Mensagem |
|----|-------|--------------|----------|
| **RN01** | Tarefa só pode ser concluída se todas as dependências estiverem concluídas | `domain/` + `app/services/tarefas.py` | _"Não é possível concluir: a tarefa '<título>' ainda está pendente."_ |
| **RN02** | Projeto não pode ser concluído enquanto houver tarefa aberta | `domain/` + `app/services/projetos.py` | _"Projeto tem <N> tarefa(s) em aberto."_ |
| **RN03** | Prazo de tarefa não pode ultrapassar o prazo do projeto | `domain/` + `app/schemas/tarefa.py` | _"Prazo da tarefa (DD/MM) ultrapassa o prazo do projeto (DD/MM)."_ |

📄 Detalhamento + testes em [`regras-negocio.md`](regras-negocio.md).

---

## 🔄 Sincronização offline → online (R6)

**Estratégia:** _sync pull-push por timestamp_.

```text
┌──────────┐                                ┌──────────┐
│ Cliente  │                                │ Servidor │
└────┬─────┘                                └────┬─────┘
     │ 1. Usuário cria tarefa offline           │
     ▼                                          │
   Room (pending_sync=true)                     │
     │                                          │
     │ 2. Conectou → WorkManager dispara        │
     │ GET /sync?since=<last_sync_at>           │
     ├─────────────────────────────────────────►│
     │◄─────────────────────────────────────────┤
     │  atualiza Room                           │
     │                                          │
     │ 3. POST /sync/batch com pendentes        │
     ├─────────────────────────────────────────►│
     │◄─────────────────────────────────────────┤
     │  IDs servidor + conflitos (server-wins)  │
     ▼                                          │
   Room (sync OK)                              │
```

**Conflitos:** _server-wins_ por padrão. Usuário recebe notificação e escolhe manter a versão local.

📄 ADR da resolução de conflito: [`docs/adr/`] (issue #14).

---

## 🔔 Recursos nativos (R8)

Notificações locais agendadas via **WorkManager** (sobrevive a reboot). Permissão solicitada em contexto (Android 13+). Deep link abre a tarefa correspondente.

📄 Decisão registrada em [`docs/adr/0003-recurso-nativo-notificacoes.md`](adr/0003-recurso-nativo-notificacoes.md).

## 🌐 Integração externa (R7)

| Candidata | Custo | Pertinência |
|-----------|-------|-------------|
| 🇧🇷 [BrasilAPI — feriados](https://brasilapi.com.br/api/feriados/v1/{ano}) | $0 | ⭐⭐⭐ Alta — alerta prazos em feriado |
| 📮 [ViaCEP](https://viacep.com.br/) | $0 | ⭐⭐ Média — endereço de cliente/obra |

📄 Decisão final em [`docs/adr/0004-integracao-externa.md`](adr/0004-integracao-externa.md) (issue #15).

---

## 🧪 Padrões transversais

| Tema | Android | Backend |
|------|---------|---------|
| 🪡 Injeção de dependência | Hilt | FastAPI `Depends` |
| ⚡ Assíncrono | Coroutines + `Flow` | `async def` + `AsyncSession` |
| 📜 Logs | Logcat + Timber | structlog |
| ❌ Erros | `sealed class Result` | `HTTPException` |
| 🔐 Config | `local.properties` | `.env` + GitHub Secrets em CI |
| 🧪 Testes | JUnit + MockK | pytest + httpx |

---

## 💸 Custos e selfhosting

| Item | 💰 | Observação |
|------|----|------------|
| GitHub Actions | $0 | 2000 min/mês free em repo público |
| GitHub Projects | $0 | Plano free |
| PostgreSQL self-hosted | $0 | Só se produção sair do SQLite |
| API externa (BrasilAPI / ViaCEP) | $0 | Públicas e gratuitas |
| SonarCloud / Codecov | $0 | Free para OSS |
| Domínio / hospedagem | _opcional_ | Apresentação roda local no APK |

> 🎯 Toda a stack é **open source** ou **self-hosted**. Nenhum SaaS pago.

---

## 🛣️ Próximas revisões

- 🔐 Estratégia de autenticação no cliente (token storage com EncryptedSharedPreferences) — issue **#7**
- 🔄 Resolução de conflito de sync — issue **#14**
- 📊 Biblioteca de gráficos do dashboard — issue **#13**

---

<div align="center">

<sub>🧠 BrainOutApp — gestão de projetos sem perder prazos, mesmo offline.</sub>

</div>
