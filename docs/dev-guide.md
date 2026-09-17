# 🛠️ Dev Guide

> Setup, comandos do dia a dia, debugging e troubleshooting do **BrainOutApp**.

## 📋 Índice

- [🚀 Setup inicial](#-setup-inicial)
- [🌅 Workflow diário](#-workflow-diário)
- [📱 Comandos Android](#-comandos-android)
- [🐍 Comandos Backend (uv)](#-comandos-backend-uv)
- [🐞 Debugging](#-debugging)
- [🌍 Variáveis de ambiente](#-variáveis-de-ambiente)
- [❗ Erros comuns](#-erros-comuns)
- [🔗 Recursos](#-recursos)

---

## 🚀 Setup inicial

```bash
# 1. Clonar
git clone https://github.com/joao-pedro-gms/BrainOutApp.git
cd BrainOutApp

# 2. Ativar hooks de commit
./scripts/setup.sh
#   → pre-commit: scan de secrets
#   → commit-msg: Conventional Commits

# 3. Configurar Android (após issue #6 criar o esqueleto)
cp android/local.properties.example android/local.properties
# editar com sdk.dir=/caminho/para/Android/sdk

# 4. Configurar Backend com uv
cd backend
# Instalar uv uma vez: https://docs.astral.sh/uv/getting-started/installation/
uv sync                 # cria .venv e instala tudo a partir de uv.lock
uv run pytest           # roda testes
cp .env.example .env    # editar .env com seus valores
```

> 💡 **Dica:** uv é 10–100× mais rápido que pip e gera um lockfile determinístico (`uv.lock`). Não precisa ativar o venv manualmente — `uv run <cmd>` resolve tudo.

---

## 🌅 Workflow diário

```bash
# Sincronizar com master
git checkout master && git pull

# Criar branch a partir da issue
git checkout -b feature/<escopo-curto>    # ou fix/, docs/, chore/, test/

# Trabalhar
git add .
git commit -m "feat(auth): adiciona login com e-mail e senha"
#   ↑ hook valida Conventional Commits + scan secrets

# Antes do PR
./scripts/quality-check.sh
git push -u origin feature/<escopo-curto)
gh pr create --fill   # preenche o template; editar Closes #N

# Após o merge
git checkout master && git pull
git branch -d feature/<escopo-curto>
```

---

## 📱 Comandos Android

```bash
cd android

# 🔨 Build
./gradlew assembleDebug                      # APK debug
./gradlew assembleRelease                    # release (requer keystore em local.properties)

# 🔍 Lint e qualidade (R12)
./gradlew ktlintCheck                        # estilo
./gradlew detekt                             # complexidade
./gradlew lint                               # Android Lint oficial

# 🧪 Testes
./gradlew testDebugUnitTest                  # testes unitários
./gradlew testReleaseUnitTest                # testes release

# 🗃️ Banco (R5)
./gradlew :app:exportSchema -PschemasLocation="$PWD/schemas"
#   ↑ task configurada em android/app/build.gradle.kts via
#     ksp { arg("room.schemaLocation", ...) } — entra na issue #6

# 🧹 Limpar
./gradlew clean
```

| Tarefa | Comando |
|--------|---------|
| Buildar APK debug | `./gradlew assembleDebug` |
| Rodar todos os checks | `./gradlew check` |
| Gerar APK release | `./gradlew assembleRelease` |

---

## 🐍 Comandos Backend (uv)

```bash
cd backend

# 📦 Sincronizar dependências
uv sync                      # deps de produção + dev
uv sync --no-group dev       # só produção (para deploy/containers)

# 🔍 Lint e qualidade (R12)
uv run ruff check .          # lint
uv run ruff format .         # formatador

# 🧪 Testes
uv run pytest
uv run pytest --cov=app --cov-report=term-missing   # com cobertura

# ➕➖ Dependências
uv add fastapi                                    # produção
uv add --group dev httpx                          # só dev
uv remove flask

# 🗃️ Banco (Alembic)
uv run alembic revision --autogenerate -m "msg"   # criar migration
uv run alembic upgrade head                       # aplicar
uv run alembic downgrade -1                       # reverter 1 passo

# 🚀 Servir
uv run uvicorn app.main:app --reload --port 8000
#   → docs interativas: http://localhost:8000/docs

# 🔐 Auditoria de vulnerabilidades
uv run pip-audit              # usa uv.lock, sem pip externo
```

| Tarefa | Comando |
|--------|---------|
| Instalar deps | `uv sync` |
| Rodar testes | `uv run pytest` |
| Servir local | `uv run uvicorn app.main:app --reload` |
| Auditoria de deps | `uv run pip-audit` |

---

## 🐞 Debugging

### 📱 Android

| Ferramenta | Como usar |
|------------|-----------|
| **Logcat** | `adb logcat -s "GPA:*"` (filtra por tag do app) |
| **Layout Inspector** | Android Studio → Tools → Layout Inspector |
| **Database Inspector** | Studio → View → Tool Windows → Database Inspector (Room) |
| **Compose Preview** | Selecionar o Composable → painel direito |
| **Stetho / Flipper** | Opcional, não instalado |

### 🐍 Backend

| Ferramenta | Como usar |
|------------|-----------|
| **FastAPI docs** | http://localhost:8000/docs (auto-gerado) |
| **pytest -x -v** | Para no primeiro erro, modo verboso |
| **breakpoint()** | `breakpoint()` em qualquer lugar → abre REPL |
| **SQLAlchemy echo** | `engine = create_engine(..., echo=True)` mostra SQL |
| **Migrations quebradas** | `uv run alembic downgrade base && uv run alembic upgrade head` |

### ⚙️ CI local

```bash
# Android CI
docker run --rm -v "$PWD":/workspace -w /workspace \
  eclipse-temurin:17-jdk bash -c "cd android && ./gradlew ktlintCheck testDebugUnitTest"

# Backend CI
cd backend && uv sync && uv run ruff check . && uv run pytest
```

---

## 🌍 Variáveis de ambiente

### 📱 Android — `android/local.properties` (**não versionado**)

```properties
sdk.dir=/home/SEU_USUARIO/Android/Sdk
API_BASE_URL=http://10.0.2.2:8000
# (opcional) RELEASE_KEYSTORE_PATH=/caminho/seguro/release.jks
# (opcional) RELEASE_KEYSTORE_PASSWORD=...
# (opcional) RELEASE_KEY_ALIAS=...
# (opcional) RELEASE_KEY_PASSWORD=...
```

### 🐍 Backend — `backend/.env` (**não versionado**)

Copie `backend/.env.example` → `backend/.env` e edite. **Nunca** comite o `.env`.

---

## ❗ Erros comuns

| ❌ Erro | 🔍 Causa | ✅ Solução |
|---------|---------|-----------|
| `commit-msg` rejeita a mensagem | Fora do Conventional Commits | `git commit --amend -m "feat(...): ..."` |
| `pre-commit` bloqueia secret | Token/key no staged | Remover do arquivo, ver [`docs/segredos.md`](segredos.md) |
| `./gradlew assembleDebug` falha | JDK errado | Instalar JDK 17, `update-alternatives` |
| Room migration falha | Mudou schema sem migration | `./gradlew :app:room.schemaLocation` e gerar migration |
| `pytest` falha em import | `.venv` não existe | `cd backend && uv sync && uv run pytest` |
| CI verde local, vermelha remoto | Cache stale | Limpar `.gradle` e Actions cache |
| `uv sync` falha no CI | `uv.lock` mudou | Rodar `uv lock` local e commitar |
| gitleaks falha no CI | Padrão em `.env.example` pareceu secret | Trocar valor de exemplo (sem palavras-chave de secret) |

---

## 🆘 Quando algo dá errado no fluxo

1. 📖 Leia a mensagem de erro inteira — não chute
2. 💻 Rode o comando localmente (mais rápido que iterar no CI)
3. 🧪 Se for bug em runtime, escreva um teste que reproduza **antes** de corrigir (TDD, R4)
4. 📋 Documente a decisão no [`docs/adr/`](adr/) se for arquitetural

---

## 🔗 Recursos

| | |
|---|---|
| 📄 [Documento norteador do PI](../Documentos/Faculdade/Documento%20Norteador%20Projeto%20Integrador%20ADS%202026-2.pdf) | Requisitos R1-R14, cronograma, avaliação |
| 🗺️ [Plano de implementação](PLAN-IMPLEMENTACAO.md) | Mapeamento issue → comandos |
| 🤝 [CONTRIBUTING.md](../CONTRIBUTING.md) | Regras da equipe |
| 🏗️ [arquitetura.md](arquitetura.md) | Decisões arquiteturais |
| 📋 [ADRs](adr/) | Decisões técnicas registradas |
| 🐍 [uv docs](https://docs.astral.sh/uv/) | Gerenciador Python usado no projeto |

---

<div align="center">

<sub>🛠️ Dúvidas? Abra uma [issue](https://github.com/joao-pedro-gms/BrainOutApp/issues) com a label `question`.</sub>

</div>
