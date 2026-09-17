<div align="center">

# 🧠 BrainOutApp

**Aplicativo Android para gestão de projetos e tarefas — Projeto Integrador ADS / PUC Goiás 2026/2**

[![Android CI](https://img.shields.io/github/actions/workflow/status/joao-pedro-gms/BrainOutApp/android-ci.yml?branch=master&label=Android%20CI&logo=android&logoColor=white&style=for-the-badge)](https://github.com/joao-pedro-gms/BrainOutApp/actions/workflows/android-ci.yml)
[![PR Checks](https://img.shields.io/github/actions/workflow/status/joao-pedro-gms/BrainOutApp/pr-checks.yml?label=PR%20Checks&logo=github&style=for-the-badge)](https://github.com/joao-pedro-gms/BrainOutApp/actions/workflows/pr-checks.yml)
[![Security](https://img.shields.io/github/actions/workflow/status/joao-pedro-gms/BrainOutApp/security.yml?label=Security&logo=shield&style=for-the-badge)](https://github.com/joao-pedro-gms/BrainOutApp/actions/workflows/security.yml)

![Kotlin](https://img.shields.io/badge/Kotlin-1.9.x-7F52FF?logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Compose-Material%203-4285F4?logo=jetpackcompose&logoColor=white)
![Offline](https://img.shields.io/badge/Offline--first-100%25-success?style=flat-square)
![License](https://img.shields.io/badge/license-MIT-blue?style=flat-square)

> _Gerencie projetos sem perder prazos — mesmo offline._

</div>

---

## ✨ O que é o BrainOutApp?

O **BrainOutApp** é um app Android nativo para **gestão de projetos e tarefas em pequenos times**. O foco é simples: quem coordena precisa enxergar tudo num dashboard; quem executa precisa atualizar o status sem fricção, mesmo sem internet. App **100% offline**, sem backend — dados e perfil vivem no próprio dispositivo.

| 👤 Gerente | 👥 Colaborador |
|------------|-----------------|
| Cria projetos, atribui tarefas, acompanha o time | Vê só o que é dele, atualiza status das próprias tarefas |
| Visão completa: atrasadas, concluídas vs. abertas, prazos da semana | Recebe notificação local 24h antes do prazo |
| CRUD completo em qualquer entidade | Permissões aplicadas na UI e revalidadas no ViewModel (defesa em profundidade) |

Tudo **offline-first** (Room como fonte da verdade). App é **100% local** (perfil e dados no dispositivo) — sem backend, sem sincronização entre devices. Ver [ADR-0005](docs/adr/0005-sem-autenticacao-online.md).

---

## 🚀 Funcionalidades

> 📌 **Mudança de escopo registrada em 2026-09-17:** o BrainOutApp é **100% offline**. R2 e R6 deixarão de ser implementados como autenticação/sync e foram marcados como 🟡 **não-aplicáveis** nessa forma. Detalhes em [`docs/adr/0005-sem-autenticacao-online.md`](docs/adr/0005-sem-autenticacao-online.md) e [`docs/adr/0006-perfil-local-opcional.md`](docs/adr/0006-perfil-local-opcional.md).

| # | Requisito | Status planejado |
|---|-----------|------------------|
| 👥 **R2** | Autenticação com 2 perfis (Gerente / Colaborador) | 🟡 Não-aplicável — autenticação local sem senha (ver [ADR-0005](docs/adr/0005-sem-autenticacao-online.md)/[0006](docs/adr/0006-perfil-local-opcional.md)). App seleciona perfil no onboarding. |
| 🗂️ **R3** | CRUD completo em 2+ entidades com validação | 🟢 Ciclo 1 |
| 📐 **R4** | 3+ regras de negócio (RN01-RN03) | 🟡 Ciclo 2 |
| 📱 **R5** | Persistência local (Room) + modo offline | 🟢 Ciclo 1 |
| ☁️ **R6** | Persistência remota + sincronização | 🟡 Não-aplicável — app 100% offline ([ADR-0005](docs/adr/0005-sem-autenticacao-online.md)). Sem sincronização entre dispositivos. |
| 🌐 **R7** | Integração com API externa (BrasilAPI feriados) | 🟠 Ciclo 3 (reavaliação pendente — sem backend, integração direta no app exigiria novo ADR) |
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
| ⚙️ **Preferências (perfil, onboarding)** | DataStore Preferences | Async, type-safe, moderno; substitui SharedPreferences |
| 🔐 **Segredos locais (senha opcional)** | EncryptedSharedPreferences (`androidx.security:security-crypto`) | Padrão AndroidX para dados sensíveis no device |
| ⏰ **Tarefas em background** | WorkManager (notificações R8) | Sobrevive a reboot; deadlines persistem |
| 🪡 **Injeção de dependência** | Hilt | Padrão Jetpack, mínimo boilerplate |
| ✅ **Qualidade Android** | ktlint, detekt | Estilo + complexidade |

Justificativas detalhadas em [`docs/arquitetura.md`](docs/arquitetura.md) e ADRs em [`docs/adr/`](docs/adr/).

> 📌 **Stack anterior (2026-09-17 e antes):** incluía FastAPI + SQLAlchemy + Alembic + PostgreSQL/SQLite + Retrofit + JWT. Removidos do plano em [ADR-0005](docs/adr/0005-sem-autenticacao-online.md) — app é 100% offline. A pasta `backend/` será tratada separadamente pelo orchestrator.

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
├── 🎨 prototipos/                 wireframes SVG estáticos (393×852)
│   ├── wireframes/                6 telas (login, projetos, tarefas, dashboard, form, 404)
│   └── README.md                  convenções e índice
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

# 3. Configurar Android
cp android/local.properties.example android/local.properties
# editar com sdk.dir=/caminho/para/Android/sdk

# 4. Antes de abrir PR
./scripts/quality-check.sh
```

> 🔒 **Configuração local** (nunca vai pro repo):
> - `android/local.properties` — copiado de `local.properties.example`
>
> 📌 Backend Python (`backend/`) **não faz mais parte do plano** (ver [ADR-0005](docs/adr/0005-sem-autenticacao-online.md)). O repositório pode mantê-lo por questões históricas até o orchestrator decidir removê-lo.

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

🟢 **Fase de concepção** (semanas 1–6) — issues de governança escritas (#1 a #5), templates/labels/milestones/hooks/CI prontos. **Código começa no Ciclo 1** (#6 — esqueleto Android). Issue #4 tem checklist parcial a fechar até a primeira aula de implementação.

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
| ~~PostgreSQL self-hosted~~ | ~~$0~~ | ❌ Backend removido do plano ([ADR-0005](docs/adr/0005-sem-autenticacao-online.md)) |
| ~~APIs externas (BrasilAPI, ViaCEP)~~ | ~~$0~~ | ⏸ Integração externa em reavaliação |
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
