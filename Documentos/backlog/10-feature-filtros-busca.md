---
title: "[FEATURE] Busca, filtros combináveis e ordenação nas listagens"
labels: [feature, android, ciclo-2, "prioridade: alta", R9]
---

## Requisito(s)
- [x] R9 — Listagens com filtro, ordenação ou busca (parte 1)

## História de usuário
**Como** Gerente  
**quero** filtrar tarefas por status, responsável e prazo e ordenar por data  
**para** achar gargalos rapidamente.

## Escopo
### Inclui
- Busca textual (título/descrição) com debounce
- Filtros combináveis: status, assignee, projeto, “só atrasadas”
- Ordenação: prazo asc/desc, atualização recente
- Persistência leve da última preferência de filtro (opcional)
### Não inclui
- Dashboard/gráficos (issue seguinte)

## Critérios de aceite
- [ ] Combinação de 2+ filtros produz resultado correto
- [ ] Busca vazia restaura lista
- [ ] Estado “nenhum resultado” distinto de lista vazia global — R10
- [ ] Funciona offline sobre Room

## Plano de teste
| Nº | Cenário | Esperado |
|----|---------|----------|
| 1 | Filtrar DONE + assignee X | Só interseção |
| 2 | Busca por termo inexistente | Empty state de busca |
| 3 | Ordenar por prazo | Ordem estável |
