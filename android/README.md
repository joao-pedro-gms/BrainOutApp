# BrainOutApp — App Android (ciclo 1)

Aplicativo Android nativo do **BrainOutApp** — gestão de projetos e tarefas,
offline-first. Este módulo contém a estrutura base criada na **issue #6**
(esqueleto do app em camadas com navegação entre as 6 telas do protótipo).

> Stack: Kotlin 1.9.24 + Jetpack Compose Material 3 + Hilt (DI) + Compose
> Navigation. Room/Retrofit chegam nos ciclos 2-3.

---

## 📁 Estrutura

```
android/
├── build.gradle.kts               # plugins raiz (sem aplicar)
├── settings.gradle.kts            # inclui :app
├── gradle.properties              # JVM args, AndroidX, configuration cache
├── gradle/
│   ├── libs.versions.toml         # version catalog (Compose BOM, Hilt, etc.)
│   └── wrapper/
│       ├── gradle-wrapper.jar
│       └── gradle-wrapper.properties  # Gradle 8.7
├── gradlew, gradlew.bat           # Gradle wrapper (executável)
├── .editorconfig                  # 4 spaces, LF, max 120 cols (ktlint-friendly)
├── local.properties.example       # caminho do SDK + URL do backend
└── app/
    ├── build.gradle.kts           # módulo :app (Compose + Hilt + KSP)
    ├── proguard-rules.pro
    └── src/
        ├── main/
        │   ├── AndroidManifest.xml
        │   ├── res/               # strings, themes, ic_launcher (adaptive)
        │   └── java/com/joaopedrogms/brainoutapp/
        │       ├── BrainOutApp.kt          # @HiltAndroidApp
        │       ├── MainActivity.kt         # @AndroidEntryPoint
        │       ├── ui/
        │       │   ├── BrainOutApp.kt      # composable raiz
        │       │   ├── theme/              # Color.kt, Type.kt, Theme.kt
        │       │   ├── navigation/         # Destinations.kt, BrainOutAppNavHost.kt
        │       │   └── screens/            # 6 telas stub (login/projetos/tarefas/
        │       │                           #   dashboard/criacao/detalhes)
        │       ├── viewmodel/              # .gitkeep — ciclo 2+
        │       ├── domain/                 # .gitkeep — ciclo 2+
        │       │   ├── model/
        │       │   └── usecase/
        │       ├── data/                   # .gitkeep — ciclo 2+
        │       │   ├── local/              # Room
        │       │   ├── remote/             # Retrofit
        │       │   └── repository/
        │       └── di/
        │           └── AppModule.kt        # @InstallIn(SingletonComponent::class)
        └── test/
            └── java/com/joaopedrogms/brainoutapp/
                └── AppSmokeTest.kt
```

## 🧭 Telas (navegação base — issue #6)

| # | Rota                  | Wireframe                          | Stub |
|---|-----------------------|------------------------------------|------|
| 1 | `login`               | `prototipos/wireframes/01-login.svg`              | ✅   |
| 2 | `projetos`            | `prototipos/wireframes/02-projetos.svg`           | ✅   |
| 3 | `detalhes/{projetoId}`| `prototipos/wireframes/03-projeto-detalhe.svg`    | ✅   |
| 4 | `tarefas`             | `prototipos/wireframes/04-tarefas.svg`            | ✅   |
| 5 | `criacao`             | `prototipos/wireframes/05-tarefa-form.svg`        | ✅   |
| 6 | `dashboard`           | `prototipos/wireframes/06-dashboard.svg`          | ✅   |

`startDestination` = `login`. A rota `detalhes/{projetoId}` recebe o id como
argumento de navegação (`NavType.StringType`).

## 🧱 Camadas (R12 — separação obrigatória)

| Camada            | Pacote                                    | Conteúdo no ciclo 1       |
|-------------------|-------------------------------------------|---------------------------|
| 🎨 `ui/`          | `com.joaopedrogms.brainoutapp.ui`         | Telas + Navigation + Theme |
| 🧠 `viewmodel/`   | `...viewmodel`                            | `.gitkeep` (ciclo 2+)     |
| 📐 `domain/`      | `...domain.model`, `...domain.usecase`    | `.gitkeep` (ciclo 2+)     |
| 💾 `data/`        | `...data.local`, `...data.remote`, `...data.repository` | `.gitkeep` (ciclo 2+) |
| 🪡 `di/`          | `...di`                                   | `AppModule.kt` (Hilt)     |

> **Regra de ouro (R12):** nenhuma lógica de negócio em Composable. As telas
> do ciclo 1 são puramente declarativas (Scaffold + Text + Button) e disparam
> navegação via callbacks.

## 🚀 Como rodar

### Pré-requisitos

1. **JDK 17** (Temurin / Zulu / Oracle). CI usa Temurin.
2. **Android SDK 34** (compileSdk = 34, targetSdk = 34, minSdk = 26).
3. Copie `local.properties.example` → `local.properties` e preencha o
   `sdk.dir` (R12, Seção 6.2 — `local.properties` **não** vai para o repo).

```bash
cd android
cp local.properties.example local.properties
$EDITOR local.properties   # ajuste sdk.dir=/caminho/para/Android/Sdk
```

### Build

```bash
# Em uma máquina com Android SDK:
./gradlew assembleDebug

# Lint (ktlint) e testes unitários:
./gradlew ktlintCheck testDebugUnitTest
```

> O CI do projeto (`.github/workflows/android-ci.yml`) detecta automaticamente
> se `./gradlew` existe. Caso ausente, pula o build com aviso até a issue #6
> adicionar o wrapper — o que já foi feito nesta entrega.

### Sem SDK Android disponível

Se você só quer validar o grafo de arquivos (ex.: revisão de código), basta
listar a estrutura:

```bash
find android -name '*.kt' -o -name '*.kts' -o -name '*.toml' | head -30
```

## 🧪 Verificações executadas nesta entrega

| Verificação                                       | Como                                     |
|---------------------------------------------------|------------------------------------------|
| Arquivos Gradle presentes                         | `cat android/settings.gradle.kts`        |
| Version catalog com Compose BOM + Hilt 2.51       | `cat android/gradle/libs.versions.toml`  |
| 6 telas + Navigation + Hilt Application           | `find android/app/src/main/java -name '*.kt'` |
| Sem lógica em Composable (R12)                    | revisão manual — apenas Scaffold + Text + callbacks |
| Lint-friendly (4 spaces, 120 col)                 | `cat android/.editorconfig`              |

## 📦 Próximos passos (ciclo 2+)

| Issue | O que entra                                                                 |
|-------|-----------------------------------------------------------------------------|
| #7    | Autenticação real (2 perfis: Gerente / Colaborador) — `viewmodel/` + DataStore |
| #10   | Criação de projeto + Room (`data/local/`, `domain/model/`, `domain/usecase/`) |
| #13   | Biblioteca de gráficos do dashboard                                        |
| #14   | Sincronização offline → online (WorkManager + Retrofit + `data/repository/`) |
| #15   | Deep links + notificações (R7 + R8)                                        |

## 🔗 Referências

- [`docs/arquitetura.md`](../docs/arquitetura.md) — camadas e regra R12
- [`docs/modelo-dados.md`](../docs/modelo-dados.md) — entidades Room/SQLAlchemy
- [`docs/regras-negocio.md`](../docs/regras-negocio.md) — RN01-RN03
- [`prototipos/wireframes/`](../prototipos/wireframes/) — referência visual
- [Issue #6](https://github.com/joao-pedro-gms/BrainOutApp/issues/6) — esta entrega
