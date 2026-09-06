---
title: "[DOCS] Modelagem de dados e definição arquitetural"
labels: [docs, concepção, "prioridade: alta", R5, R6, R12]
---

## Artefato
Modelagem de dados + memorial de arquitetura (item 2 da N1), com justificativa da stack (Kotlin/Compose/Room/Retrofit + FastAPI).

## Entrega relacionada
- [x] N1 (28/09 a 02/10)

## Escopo
### Inclui
- Diagrama ER (mín.): `Usuario`, `Projeto`, `Tarefa`, `MembroProjeto` (ajustar nomes)
- Diagrama de camadas do app: UI → Domain → Data (local/remoto)
- Contrato preliminar da API REST (recursos `/auth`, `/projects`, `/tasks`)
- Decisão offline-first: Room como fonte de verdade local; fila de sync
- Justificativa da pilha vs. alternativas rejeitadas (1 parágrafo cada)
### Não inclui
- Implementação de endpoints (issue de backend)
- Protótipo visual

## Critérios de aceite
- [ ] ER cobre as 2 entidades de CRUD (Projeto e Tarefa) e vínculo usuário↔projeto
- [ ] Arquitetura deixa explícito onde ficam regras de negócio (não na UI) — R12
- [ ] Campos de sync (`updatedAt`, `dirty`, `remoteId`) previstos para R5/R6
- [ ] Revisado por 1 integrante; diagramas legíveis no PDF

## Notas técnicas
- Preferir enums de status de tarefa: `TODO | IN_PROGRESS | DONE | BLOCKED` (exemplo — validar no escopo)
- Perfis: `MANAGER` cria/edita projetos e atribui; `COLLABORATOR` atualiza status das próprias tarefas
