---
title: "[FEATURE] Geração de APK/AAB e prova em dispositivo físico"
labels: [feature, android, ciclo-4, "prioridade: alta", R14]
---

## Requisito(s)
- [x] R14 — Pacote instalável e execução em dispositivo físico

## História de usuário
**Como** banca avaliadora  
**quero** instalar o app em um aparelho real  
**para** validar a entrega N2 sem depender só do emulador.

## Escopo
### Inclui
- Build release assinada com keystore **fora** do Git (`*.jks` ignorado)
- Instruções no README: gerar APK/AAB, instalar via `adb`
- Evidência: foto/vídeo ou ata de instalação em dispositivo físico
- Checklist pós-install: login, um CRUD, uma notificação (se possível)
### Não inclui
- Publicação na Play Store (desejável 5.1)

## Critérios de aceite
- [ ] APK ou AAB anexado à release/tag ou armazenado conforme combinado com o docente
- [ ] Instalação comprovada em aparelho físico
- [ ] Nenhum secret de assinatura no histórico Git
- [ ] Congelamento de escopo respeitado (após 27/11 só correções)

## Plano de teste
| Nº | Cenário | Esperado |
|----|---------|----------|
| 1 | `assembleRelease` / bundle | Artefato gerado |
| 2 | Install no aparelho | Abre e executa fluxo principal |
