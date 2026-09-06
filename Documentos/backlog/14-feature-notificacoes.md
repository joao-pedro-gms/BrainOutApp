---
title: "[FEATURE] Notificações locais de prazo de tarefas"
labels: [feature, android, ciclo-3, "prioridade: alta", R8]
---

## Requisito(s)
- [x] R8 — Recurso nativo do dispositivo (notificações)

## História de usuário
**Como** Colaborador  
**quero** ser notificado antes do prazo da tarefa  
**para** não esquecer entregas.

## Escopo
### Inclui
- Agendar notificação local (AlarmManager/WorkManager + NotificationChannel)
- Gatilhos: X horas antes do prazo; opcional no dia
- Deep link para a tarefa ao tocar
- Respeitar permissão de notificação (Android 13+)
### Não inclui
- Push FCM obrigatório (desejável depois)

## Critérios de aceite
- [ ] Notificação dispara em dispositivo/emulador de teste
- [ ] Cancelamento ao concluir/excluir tarefa
- [ ] Canal com nome legível; funciona com app em background
- [ ] Sem crash se permissão negada (explica na UI)

## Plano de teste
| Nº | Cenário | Esperado |
|----|---------|----------|
| 1 | Tarefa com prazo em 1 min (teste) | Notifica |
| 2 | Concluir antes | Não notifica |
| 3 | Permissão negada | App segue; CTA para settings |
