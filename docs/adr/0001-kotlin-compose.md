# ADR 0001 — Linguagem e framework Android: Kotlin + Compose

- **Status:** Aceito
- **Data:** 2026-09-17
- **Issue:** #6

## Contexto

A Seção 4 do documento norteador admite 3 caminhos para a app móvel: Android nativo (Kotlin/Java), Flutter (Dart) ou React Native (JS/TS). A equipe precisa decidir considerando:

- Curva de aprendizado (equipe com formação ADS em Kotlin básico)
- Adequação ao domínio (gestão de projetos — UI densa, formulários, listas)
- Custo (zero)
- Suporte no ecossistema PUC Goiás
- Exigência do documento de **justificativa técnica da escolha** (Seção 4)

## Decisão

**Kotlin + Jetpack Compose**, com estrutura em camadas (R12) e Hilt para DI.

## Consequências

**Positivas:**
- Compose é o padrão atual do Google; menos XML
- Type-safety em Kotlin reduz bugs de UI
- Room/Hilt/Compose formam um stack coeso com documentação oficial
- Curva menor para a equipe (ADS já viu Kotlin)

**Negativas:**
- Compose ainda evolui; breaking changes entre versões (pinned em BOM)
- Menos empregos no mercado que Flutter em algumas regiões (irrelevante para o escopo)

**Mitigações:**
- Pinar versões via Version Catalog (`gradle/libs.versions.toml`)
- Acompanhar notas de release antes de atualizar BOM
