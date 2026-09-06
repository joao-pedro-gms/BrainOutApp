---
title: "[FEATURE] Dashboard com indicadores e prazos da semana"
labels: [feature, android, ciclo-2, "prioridade: alta", R9]
---

## Requisito(s)
- [x] R9 — Visão consolidada (resumo/indicadores)

## História de usuário
**Como** Gerente  
**quero** ver atrasadas, concluídas vs abertas e prazos da semana  
**para** priorizar o dia sem abrir cada projeto.

## Escopo
### Inclui
- Tela Dashboard com cards/indicadores (não precisa gráfico complexo):
  - Total abertas / concluídas / atrasadas
  - Prazos nos próximos 7 dias
  - (Gerente) resumo por projeto ou por colaborador
- Atualização a partir do Room (offline)
- Toques nos indicadores navegam para lista pré-filtrada
### Não inclui
- BI / exportação CSV

## Critérios de aceite
- [ ] Números batem com queries da base em amostra conhecida
- [ ] Acessível (contentDescription nos indicadores) — R11
- [ ] Loading e empty (“sem tarefas nesta semana”)

## Plano de teste
| Nº | Cenário | Esperado |
|----|---------|----------|
| 1 | Seed com 2 atrasadas | Indicador = 2 |
| 2 | Tap em “atrasadas” | Lista filtrada |
