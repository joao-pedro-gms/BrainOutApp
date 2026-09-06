---
title: "[FEATURE] Três regras de negócio verificáveis (atraso, capacidade, transição)"
labels: [feature, android, ciclo-2, "prioridade: alta", R4]
---

## Requisito(s)
- [x] R4 — Mínimo 3 regras de negócio não triviais, documentadas e verificáveis

## História de usuário
**Como** Gerente  
**quero** que o app imponha regras de prazo e atribuição  
**para** evitar planos inconsistentes.

## Regras propostas (ajustar nomes no doc de escopo)
1. **RN01 — Bloqueio de conclusão atrasada sem justificativa:** tarefa com prazo vencido não pode ir para `DONE` sem campo `justificativaAtraso`.
2. **RN02 — Limite de tarefas ativas por colaborador:** colaborador não pode ter mais que N tarefas `IN_PROGRESS` simultâneas (N configurável, default 5).
3. **RN03 — Transições de status válidas:** só permitir `TODO→IN_PROGRESS→DONE` (e `→BLOCKED` a partir de IN_PROGRESS); demais transições rejeitadas.

## Escopo
### Inclui
- Implementação no domain (use cases), coberta por testes unitários
- Feedback na UI quando a regra bloqueia
- Documentação das 3 RNs no PDF / `Documentos/`
### Não inclui
- Motor de regras genérico / DSL

## Critérios de aceite
- [ ] Cada RN tem teste unitário falhando→passando
- [ ] Demo manual mostra as 3 regras
- [ ] Texto das RNs idêntico entre código, issue e documento

## Plano de teste
| Nº | Cenário | Esperado |
|----|---------|----------|
| 1 | Concluir atrasada sem justificativa | Bloqueado |
| 2 | 6ª tarefa IN_PROGRESS | Bloqueado (N=5) |
| 3 | DONE → TODO | Bloqueado |
