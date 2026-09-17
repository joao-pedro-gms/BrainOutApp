# BrainOutApp — App Android (ciclo 1)

Aplicativo Android nativo do **BrainOutApp** — gestão de projetos e tarefas,
offline-first. Este módulo contém a estrutura base criada na **issue #6**
(esqueleto do app em camadas com navegação entre as 6 telas do protótipo).

|> Stack: Kotlin 1.9.24 + Jetpack Compose Material 3 + Hilt (DI) + Compose
|> Navigation + **Room 2.6.1** (persistência local) + KSP. Retrofit continua
|> fora do plano (app 100% offline, ADR-0005).

---

## 📁 Estrutura

```
android/
├── build.gradle.kts               # plugins raiz (sem aplicar)
├── settings.gradle.kts            # inclui :app
├── gradle.properties              # JVM args, AndroidX, configuration cache
├── gradle/
│   ├── libs.versions.toml         # version catalog (Compose BOM, Hilt, Room, etc.)
│   └── wrapper/
│       ├── gradle-wrapper.jar
│       └── gradle-wrapper.properties  # Gradle 8.7
├── gradlew, gradlew.bat           # Gradle wrapper (executável)
├── .editorconfig                  # 4 spaces, LF, max 120 cols (ktlint-friendly)
├── local.properties.example       # caminho do SDK + URL do backend
└── app/
    ├── build.gradle.kts           # módulo :app (Compose + Hilt + KSP + Room)
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
        │       │   └── screens/            # 6 telas (projetos/criacao/detalhes são reais, ciclo 2 #8)
        │       ├── viewmodel/              # @HiltViewModel — ProjetoList/Detail/Form
        │       ├── domain/                 # puro (R12): model, repository, usecase
        │       │   ├── model/              # Projeto + StatusProjeto + mappers entity↔domain
        │       │   ├── repository/         # interface ProjetoRepository
        │       │   └── usecase/            # Criar/Editar/Listar/Excluir Projeto
        │   │       ├── data/                   # local (Room) + repository (impl)
        │   │       │   ├── local/              # entity/ProjetoEntity, dao/ProjetoDao, db/AppDatabase
        │   │       │   ├── preferences/        # PerfilPreferences (DataStore)
        │   │       │   ├── remote/             # reservado (vazio, ADR-0005)
        │   │       │   ├── repository/         # ProjetoRepositoryImpl
        │   │       │   └── security/           # SecurityPreferences (EncryptedSharedPreferences + PBKDF2, ADR-0006)
        │   │       └── di/
        │   │           ├── AppModule.kt        # @InstallIn(SingletonComponent::class) — Room + Hilt
        │   │           ├── PreferencesModule.kt
        │   │           └── SecurityModule.kt   # AppLock (MasterKey + EncryptedSharedPreferences)
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

## 🔁 O que entrou no ciclo 2 (issue #8 — CRUD Projetos)

- **Room 2.6.1** integrado via KSP (`androidx.room:room-runtime`, `room-ktx`,
  `room-compiler`). Banco local `brainoutapp.db` (SQLite) com a tabela `projetos`
  (PK UUID, soft delete, timestamps UTC). `fallbackToDestructiveMigration` é
  usado no builder para o ciclo de dev; ciclo 3+ adicionará migrations reais.
- **Camada `data/local/`** completa: `entity/ProjetoEntity.kt`,
  `dao/ProjetoDao.kt` (Flow + suspend), `db/AppDatabase.kt`,
  `db/Converters.kt`, helper `UuidV7` (com fallback `UUID.randomUUID()` —
  pendência documentada em `RESULTADO.md`).
- **Camada `domain/`** pura (R12): `Projeto` + `StatusProjeto` + mappers
  entity↔domain, interface `ProjetoRepository`, e 4 use cases
  (criar/editar/listar/excluir).
- **`data/repository/ProjetoRepositoryImpl.kt`** bridge Room → domain.
- **`viewmodel/`** com 3 `@HiltViewModel`: `ProjetoListViewModel`,
  `ProjetoDetailViewModel`, `ProjetoFormViewModel` (com validações de nome ≤100,
  descrição ≤500, prazo ≥ hoje).
- **DI** em `AppModule.kt`: providers para `AppDatabase` e `ProjetoDao`;
  `RepositoryModule` (módulo `@Binds` separado) pluga `ProjetoRepositoryImpl`
  na interface.
- **Telas reais** (`ui/screens/{projetos,criacao,detalhes}`):
  - `ProjetosScreen`: `LazyColumn` de `Card`, FAB `+` para criar, **empty state**
    explícito com ícone `Folder` e CTA, loading e erro.
  - `CriacaoScreen`: form completo com `OutlinedTextField` (validações inline
    + contador), `DatePickerDialog` para prazo, botões Salvar/Cancelar.
    Reaproveitado para criar e editar (`projetoId` opcional via `SavedStateHandle`).
  - `DetalhesScreen`: read-only com botões Editar (placeholder, ver
    pendências) e Excluir (com `AlertDialog` de confirmação e soft delete).
- **`ui/navigation/Destinations.kt`** ganhou a rota `criacao/{projetoId}` (helper
  canônico); o registro no `BrainOutAppNavHost.kt` é responsabilidade da lane
  de navegação.

## 🔐 AppLock (ADR-0006 — opcional)

Bloqueio do app no cold start com **senha local opcional**:

- **Storage:** `androidx.security:security-crypto` (MasterKey AES256_GCM +
  `EncryptedSharedPreferences`). O hash da senha **nunca** toca disco em
  texto plano.
- **KDF:** PBKDF2-HMAC-SHA256, 100.000 iterações, salt aleatório de 16 bytes.
- **Validação:** comparação constant-time para evitar timing-attack local.
- **Política de tentativas:** 3 falhas → cooldown de 30s (defesa contra
  força-bruta local). Após o cooldown, contador zera.
- **"Esqueci a senha":** não há recovery — `AlertDialog` explica que isso
  remove o AppLock mas preserva os dados (perfil/projetos/tarefas).
- **Fluxo de tela:** novo estado `RootStartState.NeedsUnlock`. O
  `BrainOutAppNavHost` renderiza `AppLockScreen` **fora** do `NavHost`
  (tela standalone) enquanto `NeedsUnlock`. Após `unlock()`, chama
  `rootViewModel.refresh()` e o NavHost monta a home (`projetos`).
- **Arquivos novos:**
  - `data/security/SecurityPreferences.kt` (interface + impl)
  - `di/SecurityModule.kt` (MasterKey + EncryptedSharedPreferences + bind)
  - `ui/screens/applock/AppLockViewModel.kt`
  - `ui/screens/applock/AppLockScreen.kt`
- **Arquivos estendidos:** `RootViewModel.kt` (novo estado `NeedsUnlock` +
  injeção do `SecurityPreferencesRepository`), `BrainOutAppNavHost.kt`
  (ramo `NeedsUnlock` no `when`), `gradle/libs.versions.toml` e
  `app/build.gradle.kts` (dep `androidx.security:security-crypto:1.1.0-alpha06`).
- **Fora do escopo desta lane (próxima):** tela de Configurações para
  definir/alterar/remover a senha. Hoje o AppLock pode ser removido
  apenas via "Esqueci a senha" no cold start.

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
