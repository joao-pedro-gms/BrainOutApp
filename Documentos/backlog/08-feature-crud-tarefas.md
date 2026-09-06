---
title: "[FEATURE] CRUD de Tarefas com validação e vínculo a Projeto"
labels: [feature, android, ciclo-2, "prioridade: alta", R3, R5]
---

## Requisito(s)
- [x] R3 — CRUD na 2ª entidade (Tarefa)
- [x] R5 — Persistência local

## História de usuário
**Como** Gerente  
**quero** criar e atribuir tarefas a colaboradores dentro de um projeto  
**para** distribuir o trabalho.  
**Como** Colaborador  
**quero** atualizar o status das minhas tarefas  
**para** refletir o andamento.

## Escopo
### Inclui
- Entidade `Task` (título, descrição, prazo, status, projectId, assigneeId)
- CRUD completo + listagem por projeto e “minhas tarefas”
- Validação de entrada; tarefa exige projeto existente
- Permissões: Colaborador edita status/campos permitidos; não exclui projeto
### Não inclui
- Regras avançadas de atraso (issue R4)
- Sync remoto

## Critérios de aceite
- [ ] CRUD completo demonstrável
- [ ] Filtro por projeto funciona
- [ ] Colaborador só altera tarefas atribuídas a si (ou política documentada)
- [ ] Estados vazios/erro/loading

## Plano de teste
| Nº | Cenário | Esperado |
|----|---------|----------|
| 1 | Gerente cria tarefa e atribui | Colaborador vê em “minhas” |
| 2 | Colaborador conclui tarefa | Status DONE persistido |
| 3 | Tarefa sem título | Validação bloqueia |
