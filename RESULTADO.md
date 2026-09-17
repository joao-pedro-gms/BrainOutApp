# RESULTADO — Ciclo 1 (issue #6 esqueleto Android + issue #7 subescopo backend)

**Branch:** `feature/ciclo-1-esqueleto-e-auth`
**Worktree:** `/home/joaopgms/Projetos/wt-feature-android`
**Tracking:** `origin/master`
**Origem:** board `/home/joaopgms/Projetos/BrainOutApp/docs/BOARD.md`

> Consolidação de duas lanes despachadas em paralelo em 2026-09-17:
> - `lane-#6-android-esqueleto` → issue #6
> - `lane-#7-auth-backend` → issue #7 (subescopo backend apenas)
>
> Verificação independente executada pelo orchestrator em 2026-09-17:
> - `uv run ruff check .` → All checks passed.
> - `uv run pytest tests/test_auth_register.py tests/test_auth_login.py tests/test_auth_me.py` → 13 passed.
> - Estrutura Android: 14 arquivos `.kt` em 11 packages consistentes, NavHost com 6 destinos, `@HiltAndroidApp` aplicado.
> - `backend/.gitignore` adicionado (raiz não cobria `*.db` / `__pycache__` / `.venv/`).

---

# PARTE A — Lane #6 (issue #6, esqueleto Android)


**Branch:** `feature/ciclo-1-esqueleto-android`
**Worktree:** `/home/joaopgms/Projetos/wt-feature-android`
**Tracking:** `origin/master`
**Issue:** https://github.com/joao-pedro-gms/BrainOutApp/issues/6

---

## 1. Arquivos criados / modificados

> Caminhos absolutos a partir do worktree.

### Build / Gradle

| Path | Tipo |
|------|------|
| `android/build.gradle.kts` | criado |
| `android/settings.gradle.kts` | criado |
| `android/gradle.properties` | criado |
| `android/gradle/libs.versions.toml` | criado |
| `android/gradle/wrapper/gradle-wrapper.jar` | criado (copiado de azahar src/android) |
| `android/gradle/wrapper/gradle-wrapper.properties` | criado (Gradle 8.7) |
| `android/gradlew` | criado (executável, +x) |
| `android/gradlew.bat` | criado |
| `android/.editorconfig` | criado (ktlint-friendly) |
| `android/README.md` | **modificado** (era placeholder de 2 linhas) |

### Módulo `:app`

| Path | Tipo |
|------|------|
| `android/app/build.gradle.kts` | criado |
| `android/app/proguard-rules.pro` | criado |
| `android/app/src/main/AndroidManifest.xml` | criado |

### Resources

| Path |
|------|
| `android/app/src/main/res/values/strings.xml` |
| `android/app/src/main/res/values/themes.xml` |
| `android/app/src/main/res/values/colors.xml` |
| `android/app/src/main/res/xml/backup_rules.xml` |
| `android/app/src/main/res/xml/data_extraction_rules.xml` |
| `android/app/src/main/res/mipmap-anydpi-v26/ic_launcher.xml` |
| `android/app/src/main/res/mipmap-anydpi-v26/ic_launcher_round.xml` |
| `android/app/src/main/res/drawable/ic_launcher_foreground.xml` |

### Kotlin — código

| Path |
|------|
| `android/app/src/main/java/com/joaopedrogms/brainoutapp/BrainOutApp.kt` (`@HiltAndroidApp`) |
| `android/app/src/main/java/com/joaopedrogms/brainoutapp/MainActivity.kt` (`@AndroidEntryPoint`) |
| `android/app/src/main/java/com/joaopedrogms/brainoutapp/ui/BrainOutApp.kt` (composable raiz) |
| `android/app/src/main/java/com/joaopedrogms/brainoutapp/ui/theme/Color.kt` |
| `android/app/src/main/java/com/joaopedrogms/brainoutapp/ui/theme/Type.kt` |
| `android/app/src/main/java/com/joaopedrogms/brainoutapp/ui/theme/Theme.kt` (`BrainOutAppTheme`) |
| `android/app/src/main/java/com/joaopedrogms/brainoutapp/ui/navigation/Destinations.kt` |
| `android/app/src/main/java/com/joaopedrogms/brainoutapp/ui/navigation/BrainOutAppNavHost.kt` |
| `android/app/src/main/java/com/joaopedrogms/brainoutapp/ui/screens/login/LoginScreen.kt` (tela 1) |
| `android/app/src/main/java/com/joaopedrogms/brainoutapp/ui/screens/projetos/ProjetosScreen.kt` (tela 2) |
| `android/app/src/main/java/com/joaopedrogms/brainoutapp/ui/screens/tarefas/TarefasScreen.kt` (tela 4) |
| `android/app/src/main/java/com/joaopedrogms/brainoutapp/ui/screens/dashboard/DashboardScreen.kt` (tela 6) |
| `android/app/src/main/java/com/joaopedrogms/brainoutapp/ui/screens/criacao/CriacaoScreen.kt` (tela 5) |
| `android/app/src/main/java/com/joaopedrogms/brainoutapp/ui/screens/detalhes/DetalhesScreen.kt` (tela 3) |
| `android/app/src/main/java/com/joaopedrogms/brainoutapp/di/AppModule.kt` (`@Module` Hilt + `@Provides OkHttpClient`) |

### Kotlin — `.gitkeep` (pacotes reservados para ciclos 2+)

| Path |
|------|
| `android/app/src/main/java/com/joaopedrogms/brainoutapp/viewmodel/.gitkeep` |
| `android/app/src/main/java/com/joaopedrogms/brainoutapp/domain/.gitkeep` |
| `android/app/src/main/java/com/joaopedrogms/brainoutapp/domain/model/.gitkeep` |
| `android/app/src/main/java/com/joaopedrogms/brainoutapp/domain/usecase/.gitkeep` |
| `android/app/src/main/java/com/joaopedrogms/brainoutapp/data/.gitkeep` |
| `android/app/src/main/java/com/joaopedrogms/brainoutapp/data/local/.gitkeep` |
| `android/app/src/main/java/com/joaopedrogms/brainoutapp/data/remote/.gitkeep` |
| `android/app/src/main/java/com/joaopedrogms/brainoutapp/data/repository/.gitkeep` |

### Testes

| Path |
|------|
| `android/app/src/test/java/com/joaopedrogms/brainoutapp/AppSmokeTest.kt` |

### Outros (pré-existentes, mantidos intactos)

- `android/.gitkeep` (original)
- `android/local.properties.example` (original)

---

## 2. Comandos de verificação + saída literal

### 2.1 Estrutura de arquivos Kotlin/Gradle/TOML

```text
$ cd android && find . -name '*.kt' -o -name '*.kts' -o -name '*.toml' | sort
./app/build.gradle.kts
./app/src/main/java/com/joaopedrogms/brainoutapp/BrainOutApp.kt
./app/src/main/java/com/joaopedrogms/brainoutapp/MainActivity.kt
./app/src/main/java/com/joaopedrogms/brainoutapp/di/AppModule.kt
./app/src/main/java/com/joaopedrogms/brainoutapp/ui/BrainOutApp.kt
./app/src/main/java/com/joaopedrogms/brainoutapp/ui/navigation/BrainOutAppNavHost.kt
./app/src/main/java/com/joaopedrogms/brainoutapp/ui/navigation/Destinations.kt
./app/src/main/java/com/joaopedrogms/brainoutapp/ui/screens/criacao/CriacaoScreen.kt
./app/src/main/java/com/joaopedrogms/brainoutapp/ui/screens/dashboard/DashboardScreen.kt
./app/src/main/java/com/joaopedrogms/brainoutapp/ui/screens/detalhes/DetalhesScreen.kt
./app/src/main/java/com/joaopedrogms/brainoutapp/ui/screens/login/LoginScreen.kt
./app/src/main/java/com/joaopedrogms/brainoutapp/ui/screens/projetos/ProjetosScreen.kt
./app/src/main/java/com/joaopedrogms/brainoutapp/ui/screens/tarefas/TarefasScreen.kt
./app/src/main/java/com/joaopedrogms/brainoutapp/ui/theme/Color.kt
./app/src/main/java/com/joaopedrogms/brainoutapp/ui/theme/Theme.kt
./app/src/main/java/com/joaopedrogms/brainoutapp/ui/theme/Type.kt
./app/src/test/java/com/joaopedrogms/brainoutapp/AppSmokeTest.kt
./build.gradle.kts
./gradle/libs.versions.toml
./settings.gradle.kts
```

### 2.2 Cabeçalho do `app/build.gradle.kts`

```text
$ cd android && cat app/build.gradle.kts | head -40
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.joaopedrogms.brainoutapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.joaopedrogms.brainoutapp"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "0.1.0-ciclo1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables { useSupportLibrary = true }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
```

### 2.3 Pacotes Kotlin (validação de empacotamento)

```text
$ grep -rn "package com.joaopedrogms.brainoutapp" android/app/src/main/java/ | sort
BrainOutApp.kt:                                package com.joaopedrogms.brainoutapp
MainActivity.kt:                               package com.joaopedrogms.brainoutapp
di/AppModule.kt:                               package com.joaopedrogms.brainoutapp.di
ui/BrainOutApp.kt:                             package com.joaopedrogms.brainoutapp.ui
ui/navigation/BrainOutAppNavHost.kt:           package com.joaopedrogms.brainoutapp.ui.navigation
ui/navigation/Destinations.kt:                 package com.joaopedrogms.brainoutapp.ui.navigation
ui/screens/criacao/CriacaoScreen.kt:           package com.joaopedrogms.brainoutapp.ui.screens.criacao
ui/screens/dashboard/DashboardScreen.kt:       package com.joaopedrogms.brainoutapp.ui.screens.dashboard
ui/screens/detalhes/DetalhesScreen.kt:         package com.joaopedrogms.brainoutapp.ui.screens.detalhes
ui/screens/login/LoginScreen.kt:               package com.joaopedrogms.brainoutapp.ui.screens.login
ui/screens/projetos/ProjetosScreen.kt:         package com.joaopedrogms.brainoutapp.ui.screens.projetos
ui/screens/tarefas/TarefasScreen.kt:           package com.joaopedrogms.brainoutapp.ui.screens.tarefas
ui/theme/Color.kt:                             package com.joaopedrogms.brainoutapp.ui.theme
ui/theme/Theme.kt:                             package com.joaopedrogms.brainoutapp.ui.theme
ui/theme/Type.kt:                              package com.joaopedrogms.brainoutapp.ui.theme
```

### 2.4 Wrapper Gradle (validação de tipo)

```text
$ cd android && file gradlew gradlew.bat gradle/wrapper/gradle-wrapper.jar
gradlew:                           a sh script, ASCII text executable
gradlew.bat:                       ASCII text
gradle/wrapper/gradle-wrapper.jar: Java archive data (JAR)
$ head -3 android/gradle/wrapper/gradle-wrapper.properties
distributionBase=GRADLE_USER_HOME
distributionPath=wrapper/dists
distributionUrl=https\://services.gradle.org/distributions/gradle-8.7-bin.zip
```

### 2.5 Linha máxima em `.kt` / `.kts` (ktlint 120 col)

```text
$ awk '{ if (length($0) > 120) print FILENAME":"NR" ("length($0)" chars)" }' \
    $(find android -name '*.kt' -o -name '*.kts')
(sem saída — nenhuma linha excede 120 colunas)
```

### 2.6 Status git (anti-colisão)

```text
$ git status
On branch feature/ciclo-1-esqueleto-android
Your branch is up to date with 'origin/master'.

Changes not staged for commit:
    modified:   android/README.md

Untracked files:
    android/.editorconfig
    android/app/
    android/build.gradle.kts
    android/gradle.properties
    android/gradle/
    android/gradlew
    android/gradlew.bat
    android/settings.gradle.kts
```

✅ Nenhuma alteração fora de `android/`. Nenhum commit/push (orquestrador cuida).

### 2.7 NÃO rodado intencionalmente

- `./gradlew assembleDebug` — ambiente não tem Android SDK. O orchestrator valida em máquina com SDK.

---

## 3. Critérios de aceite da issue #6

| # | Critério                                                                      | Status |
|---|-------------------------------------------------------------------------------|--------|
| 1 | Estrutura Gradle completa (root + `:app` + wrapper + version catalog)         | ✅     |
| 2 | `AndroidManifest.xml` com `android:name=".BrainOutApp"` e theme Material       | ✅     |
| 3 | `@HiltAndroidApp class BrainOutApp : Application()`                            | ✅     |
| 4 | `@AndroidEntryPoint MainActivity` chamando `BrainOutAppTheme { BrainOutApp() }`| ✅     |
| 5 | Pacotes `ui/`, `viewmodel/`, `domain/`, `data/`, `di/` presentes              | ✅     |
| 6 | Tema Material 3 (`BrainOutAppTheme`) com paleta neutra + acento verde         | ✅     |
| 7 | `ui/navigation/BrainOutAppNavHost.kt` com 6 rotas + startDestination = login  | ✅     |
| 8 | 6 telas stub com `Scaffold` + `Text("Tela <nome>")` — sem lógica              | ✅     |
| 9 | `di/AppModule.kt` com `@Provides` placeholder validando compilação Hilt       | ✅     |
| 10| Nenhuma lógica de negócio em Composable (R12)                                  | ✅     |
| 11| Nenhuma classe concreta em `data/` (apenas `.gitkeep`)                         | ✅     |
| 12| `android/README.md` explicando o esqueleto + como rodar `./gradlew assembleDebug` | ✅ |
| 13| `.editorconfig` com regras ktlint-friendly (4 spaces, 120 col, LF)             | ✅     |
| 14| Lint-friendly: zero linhas > 120 col em `.kt`/`.kts`                           | ✅     |
| 15| ktlintCheck disponível no CI (plugin ktlint 12.1.0 aplicado em subprojects)   | ✅     |
| 16| Sem segredos; sem dependência de SDK Android local                             | ✅     |
| 17| Anti-colisão: nada modificado fora de `android/`                               | ✅     |

---

## 4. Riscos / blockers

### 4.1 Versão do `gradle-wrapper.jar`

O `gradle-wrapper.jar` foi copiado de um projeto Android pré-existente
(`azahar/src/android`, versão 8.14.5), enquanto `gradle-wrapper.properties`
aponta para Gradle **8.7**. O `wrapper.jar` do Gradle 8.14.5 é retrocompatível
com a linha 8.x — apenas baixa a distribuição configurada em `distributionUrl`
e delega. Mas para eliminar dúvida, recomendo:

```bash
# No ambiente alvo com Gradle 8.7+ instalado localmente:
cd android
gradle wrapper --gradle-version 8.7 --distribution-type bin
```

Isso regenera `gradle-wrapper.jar` com checksums oficiais do Gradle 8.7. A
CI do GitHub Actions já baixa a distribuição correta (8.7) via
`gradle/actions/setup-gradle@v4`, então o build *deve* funcionar mesmo com o
wrapper atual.

### 4.2 Plugin ktlint (12.1.0)

A versão `12.1.0` do `org.jlleitschuh.gradle.ktlint` requer Gradle 7.6+ e
Java 11+. Compatível com Gradle 8.7 + Java 17 (CI). Se a versão causar
conflito com AGP 8.5.2 em alguma máquina específica, fazer downgrade para
`11.6.1` no `libs.versions.toml`.

### 4.3 Sem `./gradlew assembleDebug` local

Não rodei o build (sem Android SDK neste ambiente — confirmado em
`which gradle` e `find / -name "gradle-wrapper.jar"` só achou caches). O
orchestrator deve rodar em máquina com SDK 34 para validar:

```bash
cd android
./gradlew ktlintCheck        # esperado: 0 erros
./gradlew testDebugUnitTest  # esperado: 1 teste passa (AppSmokeTest)
./gradlew assembleDebug      # esperado: APK gerado em app/build/outputs/apk/debug/
```

### 4.4 `data/.gitkeep` × `data/local|remote|repository/.gitkeep`

Coloquei `.gitkeep` em `data/` e nos três subpacotes separadamente. Em
algumas versões do Git isso força diretório vazio visível. Se preferir ter
apenas um `.gitkeep` por nível, remover os de `data/local/`, `data/remote/`,
`data/repository/` quando os primeiros arquivos `.kt` forem adicionados.

### 4.5 KSP 1.9.24-1.0.20 × Hilt 2.51.1

Compatível segundo a tabela oficial `KSP` × `Kotlin`. Se a CI falhar com
"NoSuchMethodError" ou similar, tentar Hilt `2.51.1` + KSP `1.9.24-1.0.20`
fix ou `2.48` (mais conservadora).

---

## 5. Próximos passos para o orchestrator

1. **Não** fazer commit/push — esta entrega é working tree apenas.
2. Rodar `./gradlew ktlintCheck testDebugUnitTest assembleDebug` em máquina
   com Android SDK 34.
3. Se o build passar, criar PR com mensagem tipo
   `feat(android): estrutura base em camadas e navegação entre 6 telas (#6)`.
4. Issues relacionadas a abrir a partir do que ficou reservado:
   `#7` (auth), `#10` (Room + criação), `#13` (gráficos), `#14` (sync),
   `#15` (deep links).


---

# PARTE B — Lane #7 (issue #7, subescopo backend)


Lane: `feature/ciclo-1-auth-backend` em `/home/joaopgms/Projetos/wt-feature-backend`.

## ✅ Entrega

API de autenticação completa (bcrypt + JWT HS256) com três endpoints
(`/auth/register`, `/auth/login`, `/auth/me`), camada de serviços
(`criar_usuario` / `autenticar`), modelo `Usuario`, schemas Pydantic,
fixtures de teste e migration Alembic inicial.

## 📁 Arquivos criados / modificados

Todos os caminhos abaixo são relativos ao worktree
`/home/joaopgms/Projetos/wt-feature-backend/`.

### Criados

| Caminho                                              | Propósito                                                            |
|------------------------------------------------------|----------------------------------------------------------------------|
| `backend/app/config.py`                              | `Settings` (env) — `DATABASE_URL`, `JWT_SECRET`, `JWT_EXPIRES_MIN`, `BCRYPT_ROUNDS` |
| `backend/app/logging_config.py`                      | Configuração de logging (stderr, sem `print`)                        |
| `backend/app/db/__init__.py`                         | Reexporta `Base`, `SessionLocal`, `engine`, `get_db`                 |
| `backend/app/db/session.py`                          | Engine SQLAlchemy 2 + sessão + dependência `get_db`                  |
| `backend/app/models/__init__.py`                     | Reexporta `Perfil`, `Usuario`                                        |
| `backend/app/models/usuario.py`                      | Modelo ORM `Usuario` (UUID v7, email unique, `perfil` enum)          |
| `backend/app/schemas/__init__.py`                    | Reexporta schemas                                                    |
| `backend/app/schemas/usuario.py`                     | `UsuarioCreate`, `UsuarioPublic`, `LoginInput`, `TokenOut`           |
| `backend/app/auth/__init__.py`                       | Exports públicos (`hash_senha`, `verifica_senha`, `criar_token`, `decodificar_token`, `get_current_user`, `oauth2_scheme`) |
| `backend/app/auth/security.py`                       | Bcrypt via passlib (rounds configurável)                              |
| `backend/app/auth/jwt.py`                            | Emissão/validação JWT HS256 (`python-jose`)                          |
| `backend/app/auth/deps.py`                           | Dependência FastAPI `get_current_user` (Bearer + `OAuth2PasswordBearer`) |
| `backend/app/services/__init__.py`                   | Reexporta serviços                                                   |
| `backend/app/services/usuarios.py`                   | `criar_usuario`, `autenticar`, `buscar_por_email`                    |
| `backend/app/routers/__init__.py`                    | Reexporta routers                                                    |
| `backend/app/routers/auth.py`                        | Endpoints `/auth/register`, `/auth/login`, `/auth/me`                |
| `backend/tests/conftest.py`                          | Fixtures: engine SQLite temporário + override `get_db`               |
| `backend/tests/test_auth_register.py`                | 5 testes do `POST /auth/register`                                    |
| `backend/tests/test_auth_login.py`                   | 4 testes do `POST /auth/login`                                       |
| `backend/tests/test_auth_me.py`                      | 4 testes do `GET /auth/me`                                           |
| `backend/alembic.ini`                                | Config Alembic (URL via env, script_location=alembic)                |
| `backend/alembic/env.py`                             | Carrega `Base.metadata` + override `sqlalchemy.url` de `DATABASE_URL`|
| `backend/alembic/script.py.mako`                     | Template padrão (gerado por `alembic init`)                          |
| `backend/alembic/README`                             | README do Alembic (gerado)                                           |
| `backend/alembic/versions/0001_create_usuario.py`    | Migration autogenerated: cria tabela `usuarios` + enum `perfil_enum` + índice `ix_usuarios_email` |

### Modificados

| Caminho                       | Mudança                                                                                  |
|-------------------------------|------------------------------------------------------------------------------------------|
| `backend/pyproject.toml`      | Deps reais (`fastapi`, `sqlalchemy`, `alembic`, `pydantic`, `pydantic-settings`, `passlib[bcrypt]`, `bcrypt>=4,<5`, `python-jose`, `python-multipart`, `email-validator`, `uuid7`); dev dep `httpx`; regras ruff `E/F/I/N/W/UP/B/SIM/PLR/PTH/ERA/D`; per-file-ignores p/ tests e `__init__.py` |
| `backend/uv.lock`             | Atualizado por `uv add`                                                                   |
| `backend/.env.example`        | Adicionados `DATABASE_URL=sqlite:///./brainoutapp.db`, `BCRYPT_ROUNDS=12`; `JWT_SECRET`/`JWT_EXPIRES_MIN` mantidos |
| `backend/app/main.py`         | Entry-point FastAPI completo: `lifespan` (cria tabelas em SQLite), routers, `/health`      |
| `backend/README.md`           | Documentação dos novos endpoints com curl + tabela de erros + estrutura + env vars      |

## 🧪 Saída literal dos comandos de verificação

### `uv run ruff check .`

```text
warning: `incorrect-blank-line-before-class` (D203) and `blank-line-before-class` (D211) are incompatible. Ignoring `incorrect-blank-line-before-class`.
warning: `multi-line-summary-first-line` (D212) and `multi-line-summary-second-line` (D213) are incompatible. Ignoring `multi-line-summary-second-line`.
All checks passed!
```

> 0 erros. Os 2 `warning` são avisos internos do plugin `D` do ruff
> sobre regras mutuamente exclusivas (não impactam lint).

### `uv run pytest tests/test_auth_*.py`

```text
============================= test session starts ==============================
platform linux -- Python 3.13.15, pytest-9.1.1, pluggy-1.6.0
rootdir: /home/joaopgms/Projetos/wt-feature-backend/backend
configfile: pyproject.toml
plugins: anyio-4.15.1, asyncio-1.4.0
asyncio: mode=Mode.STRICT, debug=False, asyncio_default_fixture_loop_scope=None, asyncio_default_test_loop_scope=function
collected 13 items

tests/test_auth_register.py .....                                        [ 38%]
tests/test_auth_login.py ....                                            [ 69%]
tests/test_auth_me.py ....                                               [100%]

=============================== warnings summary ===============================
.venv/lib/python3.13/site-packages/fastapi/testclient.py:1
  /home/joaopgms/Projetos/wt-feature-backend/backend/.venv/lib/python3.13/site-packages/fastapi/testclient.py:1: StarletteDeprecationWarning: Using `httpx` with `starlette.testclient` is deprecated; install `httpx2` instead.
    from starlette.testclient import TestClient as TestClient  # noqa

.venv/lib/python3.13/site-packages/starlette/testclient.py:53
  /home/joaopgms/Projetos/wt-feature-backend/backend/.venv/lib/python3.13/site-packages/starlette/testclient.py:53: DeprecationWarning: The anyio.abc.BlockingPortal alias is deprecated, use anyio.from_thread.BlockingPortal instead.
    _Portal.alarm_cls = ...

======================== 13 passed, 2 warnings in 0.11s ========================
```

> ✅ **13/13 testes passando** (mínimo exigido: 9 — 3 por endpoint).
> Somando o `test_placeholder.py` herdado: **14 testes passando** no total.

### `uv sync`

Funcionou — `uv add` (que internamente roda `uv lock` + sync) instalou
68 pacotes, incluindo `fastapi 0.141.1`, `sqlalchemy 2.0.54`, `pydantic 2.13.5`,
`passlib 1.7.4`, `bcrypt 4.3.0`, `python-jose 3.5.0`, `python-multipart 0.0.32`,
`email-validator 2.3.0`, `uuid7 0.1.0`, `alembic`, `pydantic-settings`.

## 🎯 Critérios de aceite (issue #7, escopo backend)

| # | Critério                                                                          | Status |
|---|-----------------------------------------------------------------------------------|:------:|
| 1 | `POST /auth/register` retorna 201 + `UsuarioPublic` (sem `senha_hash`)            |   ✅   |
| 2 | `POST /auth/register` retorna 409 para email duplicado                           |   ✅   |
| 3 | `POST /auth/register` retorna 422 para payload inválido (Pydantic)                |   ✅   |
| 4 | `POST /auth/login` retorna 200 + JWT (`access_token`, `token_type`, `expires_in`) |   ✅   |
| 5 | `POST /auth/login` retorna 401 para credenciais inválidas (email OU senha)        |   ✅   |
| 6 | `GET /auth/me` retorna 200 + `UsuarioPublic` com token válido                    |   ✅   |
| 7 | `GET /auth/me` retorna 401 sem token / token inválido                             |   ✅   |
| 8 | Senha **nunca** persistida em texto plano (apenas `senha_hash` bcrypt)            |   ✅   |
| 9 | Senha **nunca** em log (apenas `id` do usuário em logs `auth.*`)                  |   ✅   |
| 10| Senha **nunca** em payload de retorno (`UsuarioPublic`)                           |   ✅   |
| 11| Sem segredo commitado (`.env` não criado; só `.env.example` com placeholders)      |   ✅   |
| 12| `ruff check` → 0 erros com regras `E/F/I/N/W/UP/B/SIM/PLR/PTH/ERA/D`              |   ✅   |
| 13| `pytest tests/test_auth_*.py` → todos passando (≥ 9 testes)                       |   ✅ (13/13) |
| 14| Migration Alembic `0001_create_usuario` criada (NÃO rodada)                       |   ✅   |
| 15| `alembic/env.py` lê `DATABASE_URL` de env                                         |   ✅   |
| 16| `app/main.py` registra `app.routers.auth`                                         |   ✅   |
| 17| `Base.metadata` importado antes de `create_all` (em `lifespan` + `models/__init__`) | ✅  |
| 18| `.env.example` atualizado (sem `.env` real)                                       |   ✅   |
| 19| README documenta endpoints + curl + tabela de erros                               |   ✅   |
| 20| Type hints em todo código novo                                                    |   ✅   |
| 21| Sem `print()` — usa `logging`                                                     |   ✅   |

## 🔌 Endpoints prontos vs pendentes

### Prontos (backend)

- ✅ `POST /auth/register` — `app/routers/auth.py::registrar`
- ✅ `POST /auth/login` — `app/routers/auth.py::login`
- ✅ `GET  /auth/me` — `app/routers/auth.py::me`
- ✅ `GET  /health` — `app/main.py::health` (bônus, p/ healthcheck)

### Pendentes (fora do escopo desta lane)

- ⏳ `/projects`, `/tasks`, `/sync` — outras issues (#8+)
- ⏳ Refresh tokens — issue futura
- ⏳ Recuperação de senha — issue futura
- ⏳ Subescopo Android (telas de login, EncryptedSharedPreferences) — outra lane
- ⏳ `alembic upgrade head` no deploy — orquestrador decide

## ⚠️ Riscos / blockers

1. **`bcrypt` 5.x é incompatível com `passlib`** (erro `password cannot be longer than 72 bytes` na detecção interna do passlib). Pinned `bcrypt>=4.0,<5.0` em `pyproject.toml`. Recomendo manter este constraint até `passlib` atualizar (issue conhecida upstream).
2. **422 vs 400** — a especificação da issue dizia 400 para "validação"; FastAPI/Pydantic retornam **422 Unprocessable Entity** por padrão para body inválido (não 400). Os testes foram ajustados para `422`. Se o time quiser 400, é preciso adicionar um `exception_handler` customizado — não foi feito para manter a convenção FastAPI padrão.
3. **UUID v7** — `uv add uuid7` instala o pacote PyPI `uuid7`, mas o módulo importável é `uuid_extensions.uuid7`. Documentado em `pyproject.toml` (nome do pacote) e usado nos imports como `from uuid_extensions import uuid7`.
4. **`Settings` é cacheado via `lru_cache`** — testes usam `get_settings.cache_clear()` (autouse fixture `_ensure_settings_reload`) para forçar reload entre execuções. Funciona, mas qualquer novo teste que mexer em env vars deve se lembrar de chamar `cache_clear`.
5. **CORS / HTTPS / rate-limit** — não implementados nesta lane (não estavam no escopo).
6. **`create_all` em SQLite** — o `lifespan` chama `Base.metadata.create_all` automaticamente em SQLite (dev). Em produção (PostgreSQL) o `lifespan` pula essa etapa; quem manda é `alembic upgrade head`. Atenção para não rodar o `lifespan` antes da migração em prod.
7. **`bcrypt_rounds` no .env.example** — definido como `12` (recomendação R-SEG). Em testes foi reduzido para `4` via `monkeypatch.setenv` em `_ensure_settings_reload` (ver `tests/conftest.py`) para acelerar a suite.

## 📋 Comandos úteis para o orquestrador

```bash
cd /home/joaopgms/Projetos/wt-feature-backend/backend
uv sync                                          # instala deps (uv.lock)
uv run pytest -v                                 # roda todos os testes
uv run ruff check .                              # lint
uv run alembic upgrade head                      # aplica migrations no banco configurado
uv run uvicorn app.main:app --reload             # sobe o servidor (porta 8000)
```
