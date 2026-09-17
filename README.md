<div align="center">

# 🧠 BrainOutApp

**Aplicativo Android para gestão de projetos e tarefas — Projeto Integrador ADS / PUC Goiás 2026/2**

[![Android CI](https://img.shields.io/github/actions/workflow/status/joao-pedro-gms/BrainOutApp/android-ci.yml?branch=master&label=Android%20CI&logo=android&logoColor=white&style=for-the-badge)](https://github.com/joao-pedro-gms/BrainOutApp/actions/workflows/android-ci.yml)
[![Backend CI](https://img.shields.io/github/actions/workflow/status/joao-pedro-gms/BrainOutApp/backend-ci.yml?branch=master&label=Backend%20CI&logo=python&logoColor=white&style=for-the-badge)](https://github.com/joao-pedro-gms/BrainOutApp/actions/workflows/backend-ci.yml)
[![PR Checks](https://img.shields.io/github/actions/workflow/status/joao-pedro-gms/BrainOutApp/pr-checks.yml?label=PR%20Checks&logo=github&style=for-the-badge)](https://github.com/joao-pedro-gms/BrainOutApp/actions/workflows/pr-checks.yml)
[![Security](https://img.shields.io/github/actions/workflow/status/joao-pedro-gms/BrainOutApp/security.yml?label=Security&logo=shield&style=for-the-badge)](https://github.com/joao-pedro-gms/BrainOutApp/actions/workflows/security.yml)

![Kotlin](https://img.shields.io/badge/Kotlin-1.9.x-7F52FF?logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Compose-Material%203-4285F4?logo=jetpackcompose&logoColor=white)
![FastAPI](https://img.shields.io/badge/FastAPI-0.115-009688?logo=fastapi&logoColor=white)
![uv](https://img.shields.io/endpoint?url=https%3A%2F%2Fraw.githubusercontent.com%2Fastral-sh%2Fuv%2Fmain%2Fassets%2Fbadge%2Fv0.json&style=flat-square)
![License](https://img.shields.io/badge/license-MIT-blue?style=flat-square)

> _Gerencie projetos sem perder prazos — mesmo offline._

</div>

---

## ✨ O que é o BrainOutApp?

O **BrainOutApp** é um app Android nativo + API REST em Python para **gestão de projetos e tarefas em pequenos times**. O foco é simples: quem coordena precisa enxergar tudo num dashboard; quem executa precisa atualizar o status sem fricção, mesmo sem internet.

| 👤 Gerente | 👥 Colaborador |
|------------|-----------------|
| Cria projetos, atribui tarefas, acompanha o time | Vê só o que é dele, atualiza status das próprias tarefas |
| Visão completa: atrasadas, concluídas vs. abertas, prazos da semana | Recebe notificação local 24h antes do prazo |
| CRUD completo em qualquer entidade | Permissões aplicadas na UI e revalidadas no backend |

Tudo **offline-first** (Room) com sincronização contra a API quando a rede volta.

---

## 🚀 Funcionalidades

| # | Requisito | Status planejado |
|---|-----------|------------------|
| 👥 **R2** | Autenticação com 2 perfis (Gerente / Colaborador) | 🔵 Concepção |
| 🗂️ **R3** | CRUD completo em 2+ entidades com validação | 🟢 Ciclo 1 |
| 📐 **R4** | 3+ regras de negócio (RN01-RN03) | 🟡 Ciclo 2 |
| 📱 **R5** | Persistência local (Room) + modo offline | 🟢 Ciclo 1 |
| ☁️ **R6** | Persistência remota + sincronização | 🟠 Ciclo 3 |
| 🌐 **R7** | Integração com API externa (BrasilAPI feriados) | 🟠 Ciclo 3 |
| 🔔 **R8** | Recurso nativo: notificações locais | 🟠 Ciclo 3 |
| 🔎 **R9** | Filtro + busca + dashboard consolidado | 🟡 Ciclo 2 |
| 🛡️ **R10** | Tratamento de erros e estados de UI | 🟠 Ciclo 3 |
| ♿ **R11** | Usabilidade e acessibilidade | 🟣 Ciclo 4 |
| 🧱 **R12** | Código em camadas, sem segredos no repo | ✅ desde o bootstrap |
| 🪪 **R13** | Versionamento disciplinado + README | ✅ desde o bootstrap |
| 📦 **R14** | APK assinado em dispositivo físico | 🟣 Ciclo 4 |

📄 Rastreabilidade completa: cada issue mapeada para R1-R14 (e cada R\* mapeada para uma issue). Veja [Issues](https://github.com/joao-pedro-gms/BrainOutApp/issues).

---

## 🧰 Stack

| Camada | Tecnologia | Por quê |
|--------|-----------|---------|
| 📱 **App Android** | Kotlin + Jetpack Compose | Nativo, type-safe, ecossistema PUC Goiás |
| 💾 **Persistência local** | Room | Padrão Android, integra direto com Compose |
| 🪡 **Injeção de dependência** | Hilt | Padrão Jetpack, mínimo boilerplate |
| 🌐 **Backend** | Python 3.12 + FastAPI | Sintaxe clara, OpenAPI automático |
| 🗃️ **ORM** | SQLAlchemy 2 + Alembic | Migrations versionadas, async nativo |
| 🐘 **Banco** | SQLite (dev) → PostgreSQL (prod) | Custo zero em dev, self-hosted em prod |
| ⚡ **Gerenciador Python** | [uv](https://docs.astral.sh/uv/) | 10–100× mais rápido que pip, lockfile determinístico |
| ✅ **Qualidade Android** | ktlint, detekt | Estilo + complexidade |
| ✅ **Qualidade Python** | ruff, pytest | Lint rápido + testes |
| 🔐 **Segredos** | GitHub Secrets + `local.properties` / `.env` | Nada de credencial no repo |

Justificativas detalhadas em [`docs/arquitetura.md`](docs/arquitetura.md) e ADRs em [`docs/adr/`](docs/adr/).

---

## 📁 Estrutura do repositório

```
BrainOutApp/
├── 📱 android/                    App Android (Kotlin + Compose)
│   ├── app/                       módulo principal
│   └── gradle/                    wrappers e libs versionadas
├── 🐍 backend/                    API Python (FastAPI)
│   ├── app/                       código (package brainoutapp-backend)
│   ├── tests/                     testes pytest
│   └── uv.lock                    lockfile versionado
├── 🎨 prototipos/                 protótipos de tela (Figma export, PNG, memorial)
├── 📚 docs/                       documentação técnica (custo zero)
│   ├── arquitetura.md             arquitetura em camadas
│   ├── dev-guide.md               setup, comandos, debugging
│   ├── modelo-dados.md            ER + decisões de modelagem
│   ├── regras-negocio.md          RN01-RN03 detalhadas
│   ├── prototipo.md               memorial de UX
│   ├── quadro.md                  atribuições por integrante
│   ├── segredos.md                política de credenciais
│   ├── PLAN-IMPLEMENTACAO.md      cronograma por issue
│   ├── adr/                       Architecture Decision Records
│   └── templates/                 templates N1, N2, lista R1-R14, roteiro
├── 📄 Documentos/                 PDFs formais exigidos pelo PI
│   └── Faculdade/                 documento norteador
├── 🛠️ scripts/                    hooks de git + quality-check + setup
│   ├── hooks/                     pre-commit + commit-msg
│   ├── quality-check.sh           lint local antes do PR
│   └── setup.sh                   ativa hooks (rode 1× após clonar)
└── 🤖 .github/
    ├── ISSUE_TEMPLATE/            bug, feature, docs, teste
    ├── workflows/                 android-ci, backend-ci, pr-checks, security, release
    ├── CODEOWNERS                 quem revisa o quê
    └── dependabot.yml             updates semanais de deps
```

---

## 🏁 Quick start

```bash
# 1. Clonar
git clone https://github.com/joao-pedro-gms/BrainOutApp.git
cd BrainOutApp

# 2. Ativar hooks de commit (Conventional Commits + scan de secrets)
./scripts/setup.sh

# 3. Configurar Android (após issue #6 criar o esqueleto)
cp android/local.properties.example android/local.properties
# editar com sdk.dir=/caminho/para/Android/sdk

# 4. Configurar Backend
cd backend
# Instalar uv uma vez: https://docs.astral.sh/uv/getting-started/installation/
uv sync                # cria .venv e instala tudo via uv.lock
uv run pytest          # roda os testes
cp .env.example .env   # editar com seus valores
cd ..

# 5. Antes de abrir PR
./scripts/quality-check.sh
```

> 🔒 **Configuração local** (nunca vai pro repo):
> - `android/local.properties` — copiado de `local.properties.example`
> - `backend/.env` — copiado de `.env.example`

---

## 🗺️ Roadmap

Acompanhamento detalhado em [`docs/PLAN-IMPLEMENTACAO.md`](docs/PLAN-IMPLEMENTACAO.md) e nas [Issues](https://github.com/joao-pedro-gms/BrainOutApp/issues).

| Marco | 📅 Data | Status | Entrega |
|-------|---------|--------|---------|
| **Checkpoint 1** | 11/09/2026 | ⬜ Pendente | Escopo, protótipo navegável, backlog |
| **N1** | 28/09 → 02/10 | ⬜ Pendente | Documento de projeto, modelagem, app parcial |
| **Checkpoint 2** | 06/11/2026 | ⬜ Pendente | Beta: CRUD de tarefas, backend, integrações |
| **Testes com usuários** | 09 → 13/11 | ⬜ Pendente | Testes funcionais + 5+ sessões de usabilidade |
| **Congelamento** | 27/11/2026 | ⬜ Pendente | Refino + APK assinado em dispositivo |
| **N2** | 07 → 11/12 | ⬜ Pendente | Relatório final + apresentação |

### Status atual

🟢 **Fase de concepção** (semanas 1–6) — issues e governança completas (#1 a #5 ✅ no checklist da #4).  
🔵 **Código começa no Ciclo 1** (#6 — esqueleto Android).

---

## ⚙️ CI/CD

Tudo **custo zero** — GitHub Actions free tier (2000 min/mês em repos públicos) + dependabot semanal.

| Workflow | 🔔 Quando | ✅ O que faz |
|----------|-----------|--------------|
| **Android CI** | push/PR em `android/**` | ktlint · testes unitários · build debug |
| **Backend CI** | push/PR em `backend/**` | `uv sync --frozen` · ruff · pytest |
| **PR Checks** | PR aberto/editado | Conventional Commit no título · branch padronizado |
| **Security** | push/PR + semanal (seg 12h UTC) | gitleaks (CLI) · `uv run pip-audit` |
| **Release** | tag `v*` ou manual | build APK debug · anexa como artifact |

📦 O CI do Android faz **skip gracioso** até a issue #6 criar o `gradlew` (em vez de falhar feio).

---

## 🤝 Contribuição

Leia [`CONTRIBUTING.md`](CONTRIBUTING.md). Resumo:

- 🚫 Nada de commit direto na `master` — sempre via PR
- ✍️ Mensagens em [Conventional Commits](https://www.conventionalcommits.org/pt-br/) (`feat:`, `fix:`, `docs:`, ...)
- 🌿 1 branch por issue: `feature/<escopo>`, `fix/<escopo>`, `docs/<escopo>`, `chore/<escopo>`, `test/<escopo>`
- 🔗 Vincular issue no PR: `Closes #N`
- ✅ 1 aprovação de outro membro + CI verde para mergear

### Workflow do dia

```bash
git checkout master && git pull
git checkout -b feature/<escopo>

# trabalhar...
git add .
git commit -m "feat(auth): adiciona login com e-mail e senha"   # hook valida

./scripts/quality-check.sh
git push -u origin feature/<escopo>
gh pr create --fill   # editar Closes #N

# após o merge
git checkout master && git pull
git branch -d feature/<escopo>
```

---

## 📚 Documentação

| Onde | O quê |
|------|-------|
| 📄 [Documento norteador do PI](Documentos/Faculdade/Documento%20Norteador%20Projeto%20Integrador%20ADS%202026-2.pdf) | Requisitos R1-R14, cronograma, avaliação |
| 🏗️ [`docs/arquitetura.md`](docs/arquitetura.md) | Arquitetura em camadas + diagrama |
| 🛠️ [`docs/dev-guide.md`](docs/dev-guide.md) | Setup, comandos, debugging |
| 🗃️ [`docs/modelo-dados.md`](docs/modelo-dados.md) | ER (mermaid) + decisões de modelagem |
| 📐 [`docs/regras-negocio.md`](docs/regras-negocio.md) | RN01-RN03 com mensagens e testes |
| 🎨 [`docs/prototipo.md`](docs/prototipo.md) | Memorial de UX e telas |
| 👥 [`docs/quadro.md`](docs/quadro.md) | Atribuição de issues por integrante |
| 🔐 [`docs/segredos.md`](docs/segredos.md) | Política de credenciais |
| 🗺️ [`docs/PLAN-IMPLEMENTACAO.md`](docs/PLAN-IMPLEMENTACAO.md) | Cronograma detalhado por issue |
| 📋 [`docs/adr/`](docs/adr/) | Decisões arquiteturais (ADR 0001 → 0004) |
| 📝 [`docs/templates/`](docs/templates/) | N1, N2, lista R1-R14, roteiro de apresentação |

---

## 💸 Custos

| Item | 💰 Custo | 📝 Observação |
|------|----------|---------------|
| GitHub Actions | $0 | 2000 min/mês free em repo público |
| GitHub Projects | $0 | Plano free |
| PostgreSQL self-hosted | $0 | Se a produção sair do SQLite |
| APIs externas (BrasilAPI, ViaCEP) | $0 | Públicas e gratuitas |
| Domínio / hospedagem | _opcional_ | Apresentação roda local no APK |

Toda a stack é **open source** ou **self-hosted**. Nenhum SaaS pago.

---

## 📜 Licença

MIT — ver [`LICENSE`](LICENSE) (a adicionar quando a equipe definir).

---

<div align="center">

**João Pedro G M Silva** · ADS · PUC Goiás · Projeto Integrador 2026/2

<sub>Construído com ☕, Kotlin, Python, uv, GitHub Actions e um plano de execução bem definido.</sub>

</div>
