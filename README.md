# GerenciaDeProjetosApp

Aplicativo Android para gestão de projetos e tarefas, desenvolvido como Projeto Integrador do curso de Análise e Desenvolvimento de Sistemas da PUC Goiás (2026/2).

[![Android CI](https://github.com/joao-pedro-gms/GerenciaDeProjetosApp/actions/workflows/android-ci.yml/badge.svg)](https://github.com/joao-pedro-gms/GerenciaDeProjetosApp/actions/workflows/android-ci.yml)
[![Backend CI](https://github.com/joao-pedro-gms/GerenciaDeProjetosApp/actions/workflows/backend-ci.yml/badge.svg)](https://github.com/joao-pedro-gms/GerenciaDeProjetosApp/actions/workflows/backend-ci.yml)
[![Security](https://github.com/joao-pedro-gms/GerenciaDeProjetosApp/actions/workflows/security.yml/badge.svg)](https://github.com/joao-pedro-gms/GerenciaDeProjetosApp/actions/workflows/security.yml)
![Kotlin](https://img.shields.io/badge/Android-Kotlin-7F52FF?logo=kotlin&logoColor=white)
![Python](https://img.shields.io/badge/Backend-Python-3776AB?logo=python&logoColor=white)

## O que o app faz

Um gerente cria projetos, distribui tarefas para a equipe e acompanha prazos em um dashboard. O colaborador vê suas tarefas, atualiza o status e recebe notificação quando um prazo se aproxima. Funciona offline e sincroniza com o servidor quando a conexão volta.

- Dois perfis de acesso: **Gerente** e **Colaborador**, com permissões distintas
- CRUD completo de projetos e tarefas, com validação de entrada
- Busca, filtros combináveis e ordenação nas listagens
- Dashboard com indicadores: tarefas atrasadas, concluídas vs. abertas, prazos da semana
- Persistência local (Room) + sincronização com API própria
- Notificações locais de prazo (recurso nativo)
- Integração com API externa de apoio ao domínio

## Stack

| Camada | Tecnologia |
|--------|-----------|
| App | Android nativo, Kotlin, Jetpack Compose, Room, Retrofit |
| Backend | API REST em Python (FastAPI) |
| Qualidade | ktlint, detekt, ruff, gitleaks, GitHub Actions |

## Estrutura do repositório

```
├── android/        app Android (em breve)
├── backend/        API em Python (em breve)
├── prototipos/     protótipos de tela
├── Documentos/     documento norteador e artefatos de engenharia
├── scripts/        hooks de git e checagens locais
└── .github/        templates de issue/PR e workflows de CI
```

## Como contribuir

Leia o [CONTRIBUTING.md](CONTRIBUTING.md). Resumo:

```bash
git clone https://github.com/joao-pedro-gms/GerenciaDeProjetosApp.git
cd GerenciaDeProjetosApp
./scripts/setup.sh        # ativa os hooks de commit
```

Depois disso, todo commit passa por scan de secrets e validação de mensagem (Conventional Commits). Antes de abrir PR, rode `./scripts/quality-check.sh`.

## Roadmap

Seguimos o cronograma do documento norteador, em 4 ciclos:

| Marco | Data | Conteúdo |
|-------|------|----------|
| Checkpoint 1 | 11/09/2026 | Escopo, protótipo navegável e backlog |
| N1 | 28/09 a 02/10 | Documento de projeto, modelagem, app parcial (navegação, autenticação, CRUD de projetos) |
| Checkpoint 2 | 06/11/2026 | Versão beta: CRUD de tarefas, regras de negócio, backend e integrações |
| Testes | 09 a 13/11 | Testes funcionais e sessões de usabilidade com 5+ usuários externos |
| Congelamento | 27/11/2026 | Fim de novas funcionalidades, geração do APK |
| N2 | 07 a 11/12 | Entrega final, documentação e mostra de projetos |

O acompanhamento de tarefas fica nas [Issues](https://github.com/joao-pedro-gms/GerenciaDeProjetosApp/issues) (cada uma mapeada aos requisitos R1-R14 do projeto) e no quadro do GitHub Projects.

## Documentação

- [Documento norteador do Projeto Integrador](Documentos/Faculdade/) (requisitos, cronograma e avaliação)
- Documento de projeto, modelagem e relatório de testes entram em `Documentos/` ao longo do semestre

## Status

Fase de concepção: escopo, modelagem e protótipo. O código do app e do backend começa no Ciclo 1 (14/09/2026).

---

**João Pedro G M Silva**
Análise e Desenvolvimento de Sistemas — PUC Goiás
Projeto Integrador, semestre 2026/2
