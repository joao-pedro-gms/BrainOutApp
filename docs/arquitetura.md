# Arquitetura

Documento vivo. Última atualização: 2026-09-17.

## Visão geral

Aplicativo Android nativo (Kotlin + Compose) + API REST em Python (FastAPI). Persistência local no dispositivo (Room) com sincronização bidirecional contra a API. Modo offline funcional.

```
┌──────────────────────────┐         ┌────────────────────────┐
│  Android (Kotlin)        │         │  Backend (Python)      │
│                          │         │                        │
│  ┌────────────────────┐  │         │  ┌──────────────────┐  │
│  │  UI (Compose)      │  │  HTTPS  │  │  FastAPI routers │  │
│  │  - screens/        │  │ ◄─────► │  │  /auth /projects │  │
│  │  - components/     │  │  JWT    │  │  /tasks /sync    │  │
│  └────────┬───────────┘  │         │  └────────┬─────────┘  │
│           │              │         │           │            │
│  ┌────────▼───────────┐  │         │  ┌────────▼─────────┐  │
│  │  ViewModel         │  │         │  │  Services        │  │
│  │  (StateFlow)       │  │         │  │  (use cases)     │  │
│  └────────┬───────────┘  │         │  └────────┬─────────┘  │
│           │              │         │           │            │
│  ┌────────▼───────────┐  │         │  ┌────────▼─────────┐  │
│  │  Domain (use cases,│  │         │  │  Repositories    │  │
│  │  regras RN01-RN03) │  │         │  │  (SQLAlchemy)    │  │
│  └────────┬───────────┘  │         │  └────────┬─────────┘  │
│           │              │         │           │            │
│  ┌────────▼───────────┐  │         │  ┌────────▼─────────┐  │
│  │  Data              │  │         │  │  PostgreSQL      │  │
│  │  - Room (local)    │  │         │  │  (ou SQLite dev) │  │
│  │  - Retrofit (sync) │  │         │  │                  │  │
│  └────────────────────┘  │         │  └──────────────────┘  │
└──────────────────────────┘         └────────────────────────┘
```

## Camadas

### Android (R12 — separação em camadas obrigatória)

| Camada | Pacote | Responsabilidade |
|--------|--------|------------------|
| `ui/` | `com.joaopedrogms.gerenciadeprojetos.ui` | Composables, navegação, tema |
| `ui/screens/<feature>` | ... | Telas por feature |
| `viewmodel/` | `...viewmodel` | `StateFlow` da UI, eventos |
| `domain/` | `...domain.model` | Entidades puras, regras de negócio (RN01-RN03) |
| `domain/usecase/` | `...domain.usecase` | Casos de uso (criar projeto, marcar tarefa, ...) |
| `data/local/` | `...data.local` | Room DAOs, entities, migrations |
| `data/remote/` | `...data.remote` | Retrofit API, DTOs, mappers |
| `data/repository/` | `...data.repository` | Implementação (local + remoto, sync) |
| `di/` | `...di` | Módulos Hilt |

**Regra (R12):** Activity/Fragment/Composable nunca contém lógica de negócio. ViewModel orquestra, Use Case executa regra, Repository persiste.

### Backend

| Camada | Módulo | Responsabilidade |
|--------|--------|------------------|
| `app/routers/` | FastAPI | Endpoints REST |
| `app/services/` | `services` | Casos de uso (validação, RN do servidor) |
| `app/models/` | SQLAlchemy | ORM |
| `app/schemas/` | Pydantic | Validação de entrada/saída |
| `app/db/` | conexão | Engine, sessão, migrations |
| `app/auth/` | JWT | Geração, validação, dependências |
| `tests/` | pytest | Unit + integration |

## Modelo de dados (resumo — detalhamento em [#2](../issues/2))

```
┌─────────────┐ 1     n ┌─────────────┐
│  Projeto    │────────►│   Tarefa    │
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
                        └─────────────┘

┌─────────────┐
│  Usuario    │
├─────────────┤
│ id (UUID)   │
│ email       │
│ senha_hash  │
│ perfil      │  enum: gerente | colaborador
│ created_at  │
└─────────────┘
```

## Regras de negócio (RN01-RN03) — [#11](../issues/11)

Implementadas em `domain/` no Android E em `app/services/` no backend (defesa em profundidade). Cliente bloqueia antes de chamar a rede; servidor revalida.

- **RN01** — Tarefa só pode ser concluída se todas as dependências estiverem concluídas
- **RN02** — Projeto não pode ser concluído enquanto houver tarefa aberta
- **RN03** — Prazo de tarefa não pode ultrapassar o prazo do projeto

Detalhamento, validação, mensagens de erro e testes em [`docs/regras-negocio.md`](regras-negocio.md).

## Sincronização offline → online (R6)

Estratégia: **sync pull-push por timestamp**.

1. Cliente sempre lê do Room (resposta instantânea, mesmo offline).
2. Mudanças locais são marcadas com `pending_sync = true` e enfileiradas.
3. Em foreground ou `WorkManager` periódico, o cliente faz:
   - `GET /sync?since=<last_sync_at>` → atualiza Room
   - `POST /sync/batch` com pendentes → recebe IDs servidor + conflitos
4. Conflitos: **server-wins** por padrão; usuário vê notificação e escolhe manter local.

Resolução de conflito documentada em [`docs/arquitetura.md`](arquitetura.md#sincronização-offline--online-r6) e ADR na issue #14.

## Recursos nativos (R8)

Notificações locais agendadas via WorkManager (sobrevive a reboot). Permissão solicitada em contexto (Android 13+). Deep link abre a tarefa. Decisão registrada em [`docs/adr/0003-recurso-nativo-notificacoes.md`](adr/0003-recurso-nativo-notificacoes.md).

## Integração externa (R7)

Candidatas:
- BrasilAPI (feriados) para alertar prazos em feriado
- ViaCEP para cadastro

Decisão registrada em [`docs/adr/0004-integracao-externa.md`](adr/0004-integracao-externa.md) na issue #15.

## Padrões

- **Injeção de dependência:** Hilt (Android), FastAPI Depends (backend)
- **Assíncrono:** Coroutines + Flow no Android; async/await no backend
- **Logs:** Logcat (Android, `Timber`), structlog (backend)
- **Erros:** sealed class `Result` no Android; `HTTPException` no backend
- **Config:** `local.properties` (Android), `.env` (backend), GitHub Secrets em CI

## Custos e selfhosting

| Item | Custo | Observação |
|------|-------|-----------|
| GitHub Actions | $0 | 2000 min/mês free em repo público |
| Postgres self-hosted | $0 | Só se produção sair do SQLite |
| API externa (BrasilAPI/ViaCEP) | $0 | Públicas e gratuitas |
| SonarCloud / Codecov | $0 | Free para OSS |
| Domínio / hospedagem | opcional | Apresentação roda local no APK |

Toda a stack é open source ou self-hosted.

## Próximas revisões

- Definir estratégia de autenticação no cliente (token storage com EncryptedSharedPreferences) — #7
- Definir resolução de conflito de sync — #14
- Definir biblioteca de gráficos do dashboard — #13
