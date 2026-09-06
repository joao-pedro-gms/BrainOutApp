---
title: "[TEST] Roteiro funcional + sessões de usabilidade (mín. 5 usuários)"
labels: [test, verificação, "prioridade: alta", R11]
---

## Tipo de verificação
- [x] Teste funcional (roteiro)
- [x] Sessão de usabilidade (mín. 5 usuários externos)

## Objetivo
Cobrir fluxos principais antes da N2 e gerar evidências da Seção 6.3 / item 3 da N2.

## Escopo
- Fluxos: cadastro/login, CRUD projeto, CRUD tarefa, filtros, dashboard, offline/sync, notificação, API externa
- Build sob teste: tag ou SHA registrado
- Perfis: Gerente e Colaborador

## Casos de teste (esqueleto)
| Nº | Fluxo | Pré-condição | Resultado esperado | Resultado obtido | Status |
|----|-------|--------------|--------------------|------------------|--------|
| 1 | Login Gerente | usuário existente | home projetos | | |
| 2 | Criar projeto | Gerente autenticado | projeto na lista | | |
| 3 | Criar/atribuir tarefa | projeto existe | colaborador vê tarefa | | |
| 4 | RN01 atraso | tarefa vencida | bloqueio sem justificativa | | |
| 5 | Filtro combinado | base seed | interseção correta | | |
| 6 | Dashboard | base seed | indicadores coerentes | | |
| 7 | Offline edit + sync | API acessível depois | dados no servidor | | |
| 8 | API externa down | mock falha | fallback | | |
| 9 | Notificação | prazo próximo | dispara | | |
| 10 | Permissão Colaborador | tenta excluir projeto | bloqueado | | |

## Usabilidade
| Usuário | Perfil | Tarefas | Problemas | Notas |
|---------|--------|---------|-----------|-------|
| U1–U5 | externo | 3 tarefas do script | | |

## Critérios de aceite
- [ ] Roteiro executado com esperado × obtido preenchidos
- [ ] ≥ 5 usuários externos documentados
- [ ] Defeitos abertos com severidade (template Bug)
- [ ] Relatório em `Documentos/` para a N2
