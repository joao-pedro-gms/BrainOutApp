---
title: "[FEATURE] Scaffold Android: Gradle, pacotes em camadas e CI verde"
labels: [feature, android, ciclo-1, "prioridade: alta", R12, qualidade]
---

## Requisito(s)
- [x] R12 — Organização do código em camadas
- [x] R13 — Versionamento + README de execução

## História de usuário
**Como** desenvolvedor da equipe  
**quero** um módulo `android/` compilável com estrutura em camadas  
**para** implementar navegação e auth sem retrabalho de base.

## Escopo
### Inclui
- Projeto Android (Kotlin, minSdk alinhado a dispositivos de teste, Compose)
- Pacotes: `ui`, `domain`, `data` (ou equivalente claro)
- Gradle Wrapper versionado; `ktlint` (e `detekt` se couber no ciclo)
- `local.properties.example`; Application theme básico
- README atualizado: Android Studio, JDK 17, como rodar
### Não inclui
- Telas de negócio completas (issues seguintes)
- Backend

## Critérios de aceite
- [ ] `./gradlew assembleDebug` e `testDebugUnitTest` passam localmente
- [ ] Workflow `android-ci.yml` executa de fato (não só o guard)
- [ ] Nenhuma credencial no repo; `.gitignore` respeitado
- [ ] `./scripts/quality-check.sh` não falha no trecho Android

## Notas técnicas
| Camada | Esperado |
|--------|----------|
| UI | `MainActivity` + theme Compose |
| Domain | package vazio ou `model` mínimo |
| Data | Room/Retrofit ainda opcionais nesta issue |

## Plano de teste
| Nº | Cenário | Esperado |
|----|---------|----------|
| 1 | Abre o app no emulador | Splash/placeholder sem crash |
| 2 | CI no PR | Jobs Android verdes |
