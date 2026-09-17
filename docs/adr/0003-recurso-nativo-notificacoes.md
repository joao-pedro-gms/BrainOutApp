# ADR 0003 — Recurso nativo: notificações locais

- **Status:** Aceito
- **Data:** 2026-09-17
- **Issue:** #16

## Contexto

O requisito R8 exige "ao menos 1 recurso nativo do dispositivo". Candidatas: notificações, câmera (anexos), biometria (login), geolocalização, armazenamento.

## Decisão

**Notificações locais** de prazo de tarefa (24h antes do vencimento). Agendamento via WorkManager (sobrevive a reboot). Permissão Android 13+ solicitada em contexto.

## Consequências

**Positivas:**
- Aproveita diretamente o domínio (gestão de prazos)
- Demonstra WorkManager + permissão moderna
- Não exige integração externa (custo zero)

**Negativas:**
- Não exercita câmera/biometria (mas a equipe pode adicionar como desejável)

**Mitigações:**
- Biometria no login fica como item desejável (não obrigatória)
