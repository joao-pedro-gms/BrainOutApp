---
title: "[FEATURE] Backend FastAPI + sincronização offline do app"
labels: [feature, android, backend, ciclo-3, "prioridade: alta", R6, R5, R10]
---

## Requisito(s)
- [x] R6 — Persistência remota e sincronização
- [x] R5 — Comportamento definido sem conectividade
- [x] R10 — Falhas de rede e estados de sync

## História de usuário
**Como** colaborador em campo  
**quero** continuar editando tarefas offline e sincronizar depois  
**para** não perder progresso sem sinal.

## Escopo
### Inclui
- `backend/` FastAPI: auth JWT (ou sessão), CRUD projects/tasks, OpenAPI
- `requirements.txt`, `.env.example`, README de execução (uvicorn)
- App: Retrofit + interceptor; fila `dirty` / pull-push
- Estratégia de conflito documentada (ex.: last-write-wins por `updatedAt`)
- UI: banner “offline” / “sincronizando” / “erro de sync”
### Não inclui
- Multi-região / CRDT

## Critérios de aceite
- [ ] API sobe localmente; `/docs` acessível
- [ ] Create offline → volta rede → aparece no servidor
- [ ] Servidor indisponível: app usável com dados locais + mensagem
- [ ] `backend-ci.yml` e `pip-audit` passam a rodar de verdade
- [ ] Sem secrets no repo

## Notas técnicas
| Camada | Esperado |
|--------|----------|
| Backend | routers, schemas Pydantic, SQLite/Postgres |
| Data app | `RemoteDataSource`, `SyncWorker` (WorkManager) |

## Plano de teste
| Nº | Cenário | Esperado |
|----|---------|----------|
| 1 | CRUD via Swagger | 2xx |
| 2 | Airplane mode edit → online | Sync OK |
| 3 | API down | App não trava; erro explícito |
