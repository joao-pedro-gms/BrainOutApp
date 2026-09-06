---
title: "[FEATURE] CRUD de Projetos com validação e Room"
labels: [feature, android, ciclo-1, "prioridade: alta", R3, R5, R10]
---

## Requisito(s)
- [x] R3 — CRUD completo com validação (1ª entidade)
- [x] R5 — Persistência local estruturada
- [x] R10 — Listas vazias, loading, erros de validação

## História de usuário
**Como** Gerente  
**quero** criar, listar, editar e arquivar/excluir projetos  
**para** organizar o trabalho da equipe no dispositivo.

## Escopo
### Inclui
- Entidade `Project` (nome, descrição, prazo, status, ownerId, timestamps)
- Room: DAO + repositório
- UI: lista, detalhe, formulário create/edit
- Validação: nome obrigatório; prazo ≥ hoje (ou regra documentada)
- Soft-delete ou exclusão com confirmação
### Não inclui
- Membros remotos / sync (Ciclo 3)
- Tarefas (issue seguinte)

## Critérios de aceite
- [ ] Create/Read/Update/Delete verificáveis na UI e no banco
- [ ] Validação impede salvar inválido com mensagem clara
- [ ] Lista vazia com CTA “Criar projeto”
- [ ] Dados sobrevivem a kill do app

## Plano de teste
| Nº | Cenário | Esperado |
|----|---------|----------|
| 1 | Criar projeto válido | Aparece na lista |
| 2 | Nome vazio | Erro de validação |
| 3 | Editar e reabrir app | Alteração persistida |
| 4 | Excluir | Some da lista; confirmação exigida |
