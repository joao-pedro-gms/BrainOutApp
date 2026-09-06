---
name: "Feature"
about: Nova funcionalidade do aplicativo ou backend — história pronta para implementação
title: "[FEATURE] "
labels: ["feature"]
assignees: []
---

## Requisito(s) do Documento Norteador
<!-- Marque os requisitos R1–R14 relacionados (Seção 5) -->
- [ ] R1 — Telas e navegação (mín. 6 telas)
- [ ] R2 — Autenticação e 2 perfis
- [ ] R3 — CRUD com validação (2+ entidades)
- [ ] R4 — Regras de negócio (mín. 3)
- [ ] R5 — Persistência local + offline
- [ ] R6 — Persistência remota / sync
- [ ] R7 — API externa
- [ ] R8 — Recurso nativo
- [ ] R9 — Filtros / busca / visão consolidada
- [ ] R10 — Tratamento de erros e estados de UI
- [ ] R11 — Usabilidade / acessibilidade
- [ ] R12 — Organização em camadas
- [ ] R13 — Versionamento disciplinado
- [ ] R14 — Distribuição (APK/AAB)

## Ciclo / prioridade
- Ciclo: <!-- concepção | ciclo-1 | ciclo-2 | ciclo-3 | ciclo-4 | verificação | encerramento -->
- Prioridade: <!-- alta | média | baixa -->
- Labels sugeridas: <!-- ex.: android, R2, ciclo-1, prioridade: alta -->

## História de usuário
**Como** <!-- persona: Gerente | Colaborador -->  
**quero** <!-- ação -->  
**para** <!-- benefício -->

## Contexto e motivação
<!-- Por que esta issue existe agora? Dependências de outras issues? -->

## Escopo
### Inclui
- 
### Não inclui (explicitamente fora)
- 

## Critérios de aceite
- [ ] 
- [ ] 
- [ ] Fluxos de erro cobertos (rede ausente, validação, lista vazia) — R10
- [ ] Sem credenciais no código; config via `local.properties` / `.env` — R12

## Notas técnicas
| Camada | Arquivos / módulos esperados |
|--------|------------------------------|
| UI (Compose) | |
| Domain / use cases | |
| Data (Room / Retrofit) | |
| Backend (FastAPI) | |

- Dependências: <!-- #N, libs, endpoints -->
- Decisões abertas: <!-- o que precisa ser decidido antes/durante -->

## Plano de teste (mínimo)
| Nº | Cenário | Esperado |
|----|---------|----------|
| 1 | Caminho feliz | |
| 2 | Validação / erro | |
| 3 | Offline ou estado vazio | |

## Definition of Done
- [ ] Critérios de aceite marcados
- [ ] Lint local (`./scripts/quality-check.sh`) passando
- [ ] Commits em Conventional Commits; branch `feature/<escopo>`
- [ ] PR com `Closes #N`, revisão de 1 integrante e CI verde
- [ ] Labels e quadro do Projects atualizados
