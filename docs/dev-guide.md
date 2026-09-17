# Dev Guide

Setup, comandos do dia a dia e debugging.

## Setup inicial

```bash
# 1. Clonar
git clone https://github.com/joao-pedro-gms/GerenciaDeProjetosApp.git
cd GerenciaDeProjetosApp

# 2. Hooks de commit
./scripts/setup.sh

# 3. Android (criar em #6)
cp android/local.properties.example android/local.properties
# editar com sdk.dir=/caminho/para/Android/sdk

# 4. Backend (criar em #14)
cd backend
python -m venv .venv
source .venv/bin/activate
pip install -e ".[dev]"
cp .env.example .env
# editar .env
pytest
```

## Workflow diário

```bash
# Sincronizar
git checkout master && git pull

# Criar branch a partir da issue
git checkout -b feature/<escopo-curto>   # ou fix/, docs/, chore/

# Trabalhar
git add .
git commit -m "feat(auth): adiciona login com e-mail e senha"
# hook valida Conventional Commits + scan secrets

# Antes do PR
./scripts/quality-check.sh
git push -u origin feature/<escopo-curto)
gh pr create --fill  # preenche o template; editar Closes #N

# Após merge
git checkout master && git pull
git branch -d feature/<escopo-curto>
```

## Comandos por stack

### Android

```bash
cd android

# Build
./gradlew assembleDebug
./gradlew assembleRelease   # requer keystore em local.properties

# Lint e testes
./gradlew ktlintCheck       # estilo (R12)
./gradlew detekt            # complexidade (R12)
./gradlew testDebugUnitTest # testes unitários
./gradlew testReleaseUnitTest

# Banco
./gradlew :app:room.schemaLocation="$PWD/schemas"
# versionar schemas/ para migrations (R5)

# Limpar
./gradlew clean
```

### Backend

```bash
cd backend
source .venv/bin/activate

# Lint e testes
ruff check .                       # lint (R12)
ruff format .                      # formatador
pytest                             # testes
pytest --cov=app --cov-report=term-missing  # cobertura

# Banco
alembic revision --autogenerate -m "msg"  # criar migration
alembic upgrade head                      # aplicar
alembic downgrade -1                      # reverter

# Servir
uvicorn app.main:app --reload --port 8000
# docs em http://localhost:8000/docs

# Auditoria de deps
pip-audit -r requirements.txt
```

## Debugging

### Android

- **Logcat** filtrar por tag do app: `adb logcat -s "GPA:*"`
- **Layout Inspector** → Android Studio → Tools → Layout Inspector
- **Database Inspector** → Studio → View → Tool Windows → Database Inspector (Room)
- **Compose Preview** → abre o Composable e usa o painel direito
- **Stetho / Flipper** → opcional, não instalado

### Backend

- **FastAPI docs** → `http://localhost:8000/docs`
- **pytest -x -v** → para no primeiro erro
- **ipython + breakpoint** → `breakpoint()` em qualquer lugar
- **SQLAlchemy echo** → `echo=True` na engine mostra SQL gerado
- **Migrations quebradas** → `alembic downgrade base && alembic upgrade head`

### CI

```bash
# Rodar Android CI local
docker run --rm -v "$PWD":/workspace -w /workspace \
  eclipse-temurin:17-jdk bash -c "cd android && ./gradlew ktlintCheck testDebugUnitTest"

# Rodar Backend CI local
cd backend && ruff check . && pytest
```

## Variáveis de ambiente

### Android (`local.properties`, **não versionado**)

```properties
sdk.dir=/home/SEU_USUARIO/Android/Sdk
API_BASE_URL=http://10.0.2.2:8000
# (opcional) RELEASE_KEYSTORE_PATH=...
```

### Backend (`.env`, **não versionado**)

Copie `backend/.env.example` → `backend/.env`. Nunca comite o `.env`.

## Erros comuns

| Erro | Causa | Solução |
|------|-------|---------|
| `commit-msg` rejeita | Mensagem fora do Conventional Commits | `git commit --amend -m "feat(...): ..."` |
| `pre-commit` bloqueia secret | Token/key no staged | Remover do arquivo, ver [`docs/segredos.md`](segredos.md) |
| `./gradlew assembleDebug` falha | JDK errado | Instalar JDK 17, `update-alternatives` |
| Room migration falha | Mudou schema sem migration | `gradlew :app:room.schemaLocation` e gerar migration |
| `pytest` falha em import | `.venv` não ativado | `source backend/.venv/bin/activate` |
| CI verde local, vermelha remoto | Cache stale | Limpar `.gradle` e Actions cache |
| gitleaks falha no CI | Padrão em `.env.example` pareceu secret | Trocar valor de exemplo |

## Quando algo dá errado no fluxo

1. Leia a mensagem de erro inteira — não chute
2. Rode o comando localmente (mais rápido que iterar no CI)
3. Se for bug em runtime, escreva um teste que reproduza **antes** de corrigir (TDD, R4)
4. Documente a decisão no [`docs/adr/`](adr/) se for arquitetural

## Recursos

- [Documento norteador do PI](../Documentos/Faculdade/Documento%20Norteador%20Projeto%20Integrador%20ADS%202026-2.pdf)
- [Plano de implementação](PLAN-IMPLEMENTACAO.md)
- [CONTRIBUTING.md](../CONTRIBUTING.md)
- [ADRs](adr/)
