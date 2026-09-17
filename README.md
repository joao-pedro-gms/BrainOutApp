# GerenciaDeProjetosApp

Aplicativo Android para gestão de projetos e tarefas — Projeto Integrador do curso de Análise e Desenvolvimento de Sistemas da PUC Goiás (2026/2).

[![Android CI](https://github.com/joao-pedro-gms/GerenciaDeProjetosApp/actions/workflows/android-ci.yml/badge.svg)](https://github.com/joao-pedro-gms/GerenciaDeProjetosApp/actions/workflows/android-ci.yml)
[![Backend CI](https://github.com/joao-pedro-gms/GerenciaDeProjetosApp/actions/workflows/backend-ci.yml/badge.svg)](https://github.com/joao-pedro-gms/GerenciaDeProjetosApp/actions/workflows/backend-ci.yml)
[![PR Checks](https://github.com/joao-pedro-gms/GerenciaDeProjetosApp/actions/workflows/pr-checks.yml/badge.svg)](https://github.com/joao-pedro-gms/GerenciaDeProjetosApp/actions/workflows/pr-checks.yml)
[![Security](https://github.com/joao-pedro-gms/GerenciaDeProjetosApp/actions/workflows/security.yml/badge.svg)](https://github.com/joao-pedro-gms/GerenciaDeProjetosApp/actions/workflows/security.yml)
![Kotlin](https://img.shields.io/badge/Android-Kotlin-7F52FF?logo=kotlin&logoColor=white)
![Python](https://img.shields.io/badge/Backend-Python-3776AB?logo=python&logoColor=white)
![License](https://img.shields.io/badge/license-MIT-blue)

## Visão geral

Um gerente cria projetos, distribui tarefas para a equipe e acompanha prazos em um dashboard. O colaborador vê suas tarefas, atualiza o status e recebe notificação quando um prazo se aproxima. O app funciona **offline** (persistência local com Room) e sincroniza com a API quando a conexão volta.

### Funcionalidades principais

- **Dois perfis** com permissões distintas — Gerente e Colaborador (R2)
- **CRUD completo** de Projetos e Tarefas, com validação de entrada (R3)
- **3+ regras de negócio** (RN01-RN03) implementadas na camada de domínio (R4)
- **Persistência local** com Room e modo offline (R5)
- **Sincronização** com API REST em Python (FastAPI) (R6)
- **Integração com API externa** pertinente ao domínio (R7)
- **Recurso nativo**: notificações locais de prazo (R8)
- **Listagens** com filtro, busca, ordenação e **dashboard** consolidado (R9)
- **Tratamento explícito** de erros e estados de UI (R10)
- **Acessibilidade**: contraste, áreas de toque, rótulos para TalkBack (R11)
- **Geração de APK** assinado e validado em dispositivo físico (R14)

## Stack

| Camada | Tecnologia | Por quê (resumo) |
|--------|-----------|------------------|
| App Android | Kotlin + Jetpack Compose | Nativo, curva menor, ecossistema PUC Goiás |
| Persistência local | Room | Padrão Android, integra com Compose |
| Injeção de dependência | Hilt | Padrão Jetpack |
| Backend | Python + FastAPI | Sintaxe clara, doc automática |
| ORM | SQLAlchemy + Alembic | Migrations versionadas |
| Banco | SQLite (dev) / PostgreSQL (prod) | Custo zero em dev; Postgres grátis self-hosted |
| Qualidade Android | ktlint, detekt | Estilo e complexidade |
| Qualidade Python | ruff, pytest | Lint rápido + testes |
| Segredos | GitHub Secrets + `local.properties` / `.env` | Nada de credencial no repo (R12) |

Detalhamento em [`docs/arquitetura.md`](docs/arquitetura.md) e ADRs em [`docs/adr/`](docs/adr/).

## Estrutura do repositório

```
.
├── android/                    App Android (Kotlin)
│   ├── app/                    módulo principal
│   └── gradle/                 wrappers e libs versionadas
├── backend/                    API Python (FastAPI)
│   ├── app/                    código
│   └── tests/                  testes pytest
├── prototipos/                 protótipos de tela (Figma export, PNG, memorial)
├── docs/                       arquitetura, ADRs, guias, templates (custo zero)
│   ├── arquitetura.md
│   ├── dev-guide.md
│   ├── PLAN-IMPLEMENTACAO.md   plano de execução por issue
│   ├── adr/                    Architecture Decision Records
│   └── templates/              templates N1/N2 em Markdown
├── Documentos/                 PDFs formais exigidos pelo PI (norteador, N1, N2)
│   └── Faculdade/
├── scripts/                    hooks de git e checagens locais
│   ├── hooks/
│   ├── quality-check.sh
│   └── setup.sh
└── .github/
    ├── ISSUE_TEMPLATE/         bug, feature, docs, teste
    ├── workflows/              android-ci, backend-ci, pr-checks, security, release
    ├── CODEOWNERS
    └── dependabot.yml
```

## Como começar (desenvolvedor)

```bash
git clone https://github.com/joao-pedro-gms/GerenciaDeProjetosApp.git
cd GerenciaDeProjetosApp
./scripts/setup.sh                 # ativa hooks de commit (Conventional Commits + scan secrets)

# Antes de abrir PR:
./scripts/quality-check.sh
```

Configuração local (não vai pro repo):

- `android/local.properties` → copie de `android/local.properties.example`
- `backend/.env` → copie de `backend/.env.example`

## Roadmap

| Marco | Data | Status | Conteúdo |
|-------|------|--------|----------|
| Checkpoint 1 | 11/09/2026 | ⬜ | Escopo, protótipo navegável e backlog (#1-#5) |
| N1 | 28/09 a 02/10 | ⬜ | Documento de projeto, modelagem, app parcial (#6-#10, #2, #9) |
| Checkpoint 2 | 06/11/2026 | ⬜ | Versão beta: CRUD de tarefas, backend, integrações (#11-#17) |
| Testes | 09 a 13/11 | ⬜ | Testes funcionais + usabilidade (#18-#20) |
| Congelamento | 27/11/2026 | ⬜ | Refino + APK (#21-#23) |
| N2 | 07 a 11/12 | ⬜ | Relatório final + apresentação (#24-#26) |

Plano detalhado em [`docs/PLAN-IMPLEMENTACAO.md`](docs/PLAN-IMPLEMENTACAO.md). Acompanhamento em [Issues](https://github.com/joao-pedro-gms/GerenciaDeProjetosApp/issues).

## CI/CD

| Workflow | Quando roda | O que faz |
|----------|-------------|-----------|
| **Android CI** | push/PR em `android/**` | ktlint, testes unitários, build debug |
| **Backend CI** | push/PR em `backend/**` | ruff, pytest |
| **PR Checks** | PR aberto/editado | Conventional Commit no título + nome de branch padronizado |
| **Security** | push/PR + semanal (seg 12h UTC) | gitleaks, pip-audit |
| **Release** | tag `v*` ou manual | build APK debug + anexa como artifact |

Tudo custo zero — GitHub Actions free tier (2000 min/mês em repos públicos).

## Contribuição

Leia [`CONTRIBUTING.md`](CONTRIBUTING.md). Regras principais:

- Nada de commit direto na `master` — sempre PR
- Mensagens em Conventional Commits (`feat:`, `fix:`, `docs:`, ...)
- 1 branch por issue: `feature/<escopo>`, `fix/<escopo>`, `docs/<escopo>`, `chore/<escopo>`
- Vincular a issue no PR: `Closes #N`
- 1 aprovação + CI verde para mergear

## Documentação

- [Documento norteador do PI](Documentos/Faculdade/Documento%20Norteador%20Projeto%20Integrador%20ADS%202026-2.pdf) — requisitos R1-R14, cronograma, avaliação
- [`docs/arquitetura.md`](docs/arquitetura.md) — arquitetura em camadas
- [`docs/dev-guide.md`](docs/dev-guide.md) — setup, comandos, debugging
- [`docs/adr/`](docs/adr/) — decisões técnicas registradas
- [`docs/PLAN-IMPLEMENTACAO.md`](docs/PLAN-IMPLEMENTACAO.md) — plano por issue
- Templates N1/N2 em [`docs/templates/`](docs/templates/) (versionados, custo zero)

## Status atual

**Fase de concepção** (semanas 1-6). Issues e governança completas (#1-#5 ✅ no checklist da #4). Código começa no Ciclo 1.

---

**João Pedro G M Silva** — ADS, PUC Goiás — Projeto Integrador 2026/2
