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
| 5a| `criacao`             | `prototipos/wireframes/05-tarefa-form.svg`        | ✅   |
| 5b| `criacao/tarefa?projetoId=&tarefaId=` | (formulário de **tarefa**)            | ✅ (#10) |
| 6 | `dashboard`           | `prototipos/wireframes/06-dashboard.svg`          | ✅   |

> A rota 5a (`criacao`) continua sendo o formulário de **projeto** (issue #8).
> A rota 5b (`criacao/tarefa?…`) é a nova entrada do CRUD de **tarefa** (issue #10).
> Ambas são distintas para evitar generalizar o form antes da hora.

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


## 🔁 O que entrou no ciclo 4 (issue #12 — Filtro + Busca + Ordenação)

- **DAO** (`ProjetoDao` e `TarefaDao`): novas queries `buscarPorNome`,
  `buscarPorPrazo`, `buscarPorCriacao` (projetos) e `buscarPorPrazo`,
  `buscarPorPrioridade`, `buscarPorTitulo` (tarefas). Todas usam `LIKE`
  case-insensitive (`LOWER(col) LIKE LOWER(:query)`) e filtro opcional
  de status via `(:statusFiltro IS NULL OR status = :statusFiltro)`.
  Queries estáticas — Room valida em build time.
- **ViewModels**: `ProjetoListViewModel` e `TarefaListViewModel` agora
  expõem `query`, `statusFiltro`/`filtroStatus` e `sortBy` como
  `StateFlow`s. Pipeline `combine → debounce(300ms) → flatMapLatest`
  consome o DAO direto (a interface `domain/repository/` está fora do
  escopo desta lane). Filtro de status das tarefas é aplicado no domínio
  (em memória) para evitar combinatorial explosion de queries no DAO.
- **UI**: componente compartilhado `BuscaSearchBar` (Material 3 `SearchBar`)
  e `OrdenacaoDropdown` (Material 3 `ExposedDropdownMenuBox`) em
  `ui/components/`. Ambas as telas (`ProjetosScreen`, `TarefasScreen`)
  ganharam: `BuscaSearchBar` no topo + `LazyRow` de `FilterChip` por
  status (Projetos) + `OrdenacaoDropdown` (NOME/PAZO/CRIAÇÃO para
  projetos; PRAZO/PRIORIDADE/TÍTULO para tarefas).
- **Empty state** diferencia "nenhum item" de "nenhum resultado com
  filtro ativo" — texto e CTA mudam.
- Não mexe em `TarefaFormScreen`, `ProjetoFormScreen`, `DetalhesScreen`,
  navegação, tema, preferências, segurança, DI ou domínio.

## 🔁 O que entrou no ciclo 3 (issue #10 — CRUD Tarefas)


- **Nova entidade Room `TarefaEntity`** (`tableName = "tarefas"`) com FK
  `RESTRICT` para `projetos(id)` (RN02 — não excluir projeto com tarefa aberta).
  Campos: `id`, `projeto_id`, `titulo`, `descricao`, `prazo_millis`, `status`,
  `prioridade`, `responsavel`, `created_at`, `updated_at`, `deleted_at` (soft
  delete, mesmo padrão de `ProjetoEntity`).
- **Banco atualizado para v2** com `MIGRATION_1_2` explícita (`CREATE TABLE
  tarefas` + índice em `projeto_id`). O `fallbackToDestructiveMigration()` da
  issue #8 foi **removido** (a justificativa está no KDoc de `AppDatabase`).
- **`TarefaDao`** com `insert`, `update`, `softDelete`, `getById`, `getAll`
  e `getByProjeto(projetoId): Flow<List<TarefaEntity>>` — reatividade Compose
  via `Flow`.
- **Camada `domain/`** com `Tarefa` + `StatusTarefa` (`ABERTA`, `EM_ANDAMENTO`,
  `CONCLUIDA`, `CANCELADA`) + `PrioridadeTarefa` (`BAIXA`, `MEDIA`, `ALTA`,
  `URGENTE`) + mappers entity↔domain. `TarefaRepository` interface e
  `TarefaRepositoryImpl`.
- **5 use cases**: `CriarTarefaUseCase`, `EditarTarefaUseCase`,
  `ListarTarefasUseCase`, `ExcluirTarefaUseCase`, `ListarTarefasPorProjetoUseCase`.
- **`viewmodel/`** com 3 `@HiltViewModel`: `TarefaListViewModel` (filtro por
  status via `FilterChip`), `TarefaDetailViewModel`, `TarefaFormViewModel`
  (com seletor de projeto pai — lista carregada do `ProjetoRepository`,
  validações de título ≤100, descrição ≤500, projeto obrigatório, prazo ≥ hoje).
- **Telas reais**:
  - `TarefasScreen`: lista com chips de filtro por status (`Todas / Aberta /
    Em andamento / Concluída / Cancelada`), FAB `+`, empty state com ícone
    `Assignment` e CTA.
  - `TarefaFormScreen` (em `ui/screens/tarefas/`, **separada** de
    `CriacaoScreen` por opção arquitetural da lane — não generalizamos o
    form de projeto): dropdown de projeto pai (carregado reativamente),
    campos título/descrição/prazo (DatePicker)/prioridade (chips)/status
    (chips)/responsável, validações inline.
- **DI atualizado**: provider para `TarefaDao`; novo `@Binds` para
  `TarefaRepositoryImpl`. `RepositoryModule` segue em arquivo separado.
- **Rotas**: nova entrada `criacao/tarefa?projetoId=&tarefaId=` em
  `Destinations.kt` (com query params opcionais — o `BrainOutAppNavHost.kt`
  é responsabilidade de outra lane). Helpers `criacaoTarefaNova(projetoId?)`
  e `criacaoTarefaEditar(tarefaId)`.
- **Validações do form de tarefa**: titulo ≤100, descricao ≤500, projetoId
  obrigatório, prazo ≥ hoje se preenchido, prioridade BAIXA (padrão),
  status ABERTA (padrão).

## 🔁 O que entrou no ciclo 4 (issue #11 — RN01-RN03)

- **`domain/exception/RegrasNegocioException`** — exceção tipada para
  violações de regras. Mensagem amigável (R10) é propagada direto para
  a UI; ViewModel captura especificamente para distinguir de erros
  genéricos.
- **3 use cases em `domain/usecase/`:**
  - `ConcluirTarefaUseCase(tarefaId)` — RN01. Tarefa só vira
    `CONCLUIDA` se todas as entradas em `dependencias` estiverem
    `CONCLUIDA`. Lança `RegrasNegocioException` com `"Não é possível
    concluir: a tarefa '<título>' ainda está pendente."` quando há
    bloqueio. Dependência soft-deleted é tratada como satisfeita.
    Idempotente.
  - `ConcluirProjetoUseCase(projetoId)` — RN02. Projeto só vira
    `CONCLUIDO` se não houver tarefa em `ABERTA` ou `EM_ANDAMENTO`.
    Lança `"Projeto tem N tarefa(s) em aberto. Conclua ou cancele
    antes."` quando há bloqueio. `CANCELADA` não bloqueia.
    Idempotente.
  - **RN03** estende `CriarTarefaUseCase` e `EditarTarefaUseCase`:
    prazo da tarefa, se preenchido, deve ser `≤` prazo do projeto.
    Lança `"Prazo da tarefa (DD/MM) ultrapassa o prazo do projeto
    (DD/MM)."` quando viola. Projeto sem prazo → tarefa pode ter
    qualquer prazo. Projeto inexistente (id órfão) → regra é pulada
    silenciosamente (FK do SQLite cuida depois).
- **Tarefa ganhou `dependencias: List<String>`** no domínio +
  entity Room (serializado como JSON array de strings em coluna
  TEXT, sem dependência externa — `kotlinx.serialization` continua
  fora do catálogo). Default `emptyList()`. Migration v2 → v3
  (`MIGRATION_2_3`) adiciona a coluna `dependencias TEXT NOT NULL
  DEFAULT '[]'` via `ALTER TABLE`. `AppDatabase` agora em `version =
  3` e o builder do Room registra `MIGRATION_2_3` em `AppModule.kt`
  (sem essa registration, abrir v2 com `version = 3` quebra com
  `IllegalStateException`).
- **`TarefaDao`** ganhou `getByIdsOnce(ids)` (snapshot batch, filtra
  soft-deleted) e `getByProjetoOnce(projetoId)` (snapshot único,
  usado por `ConcluirProjetoUseCase`). **O contrato dos repositórios
  foi estendido** (não quebrou callers existentes).
- **Camada de `viewmodel/`:**
  - `TarefaFormViewModel` ganhou `dependenciasTexto` no `FormState`
    (textarea crua, uma id por linha), validador
    `validarDependencias` (regex fraca de UUID) e tratamento
    específico de `RegrasNegocioException` em `salvar()` — a mensagem
    da RN vai para `mensagemErroGeral` e o form fica editável (UX).
  - `TarefaFormScreen` ganhou o campo "Dependências (RN01)" entre
    Responsável e os botões. Placeholder explica o formato.
- **`android/app/src/test/java/.../fakes/`** — `FakeProjetoRepository`
  e `FakeTarefaRepository` in-memory (sem Room, sem `android.util.Log`).
  Cobertura JUnit 4 dos use cases de regras:
  - `ConcluirTarefaUseCaseTest` — 8 testes (sem deps; com dep aberta;
    com dep concluída; EM_ANDAMENTO bloqueia; múltiplas deps;
    idempotência; inexistente; soft-deleted).
  - `ConcluirProjetoUseCaseTest` — 9 testes (sem tarefas; com 1
    aberta; com 1 concluída; EM_ANDAMENTO bloqueia; CANCELADA não;
    contagem na mensagem; idempotência; inexistente; isolamento por
    projeto).
  - `CriarTarefaUseCaseTest` — 7 testes (prazo menor; igual;
    maior viola com mensagem R10; projeto sem prazo; tarefa sem
    prazo; projeto inexistente; dependências preservadas).
- **`libs.versions.toml`** ganhou `kotlinx-coroutines-test = 1.7.3`     
  (necessário para `runTest`); `build.gradle.kts` registra como
  `testImplementation`.
- **Mensagens R10 (validação dupla cliente↔servidor):**
  - `"Não é possível concluir: a tarefa '<título>' ainda está pendente."`
  - `"Projeto tem <N> tarefa(s) em aberto. Conclua ou cancele antes."`
  - `"Prazo da tarefa (DD/MM) ultrapassa o prazo do projeto (DD/MM)."`
- **Pendências / fora de escopo desta lane:**
  - UX "bonita" para dependências (chips + autocomplete via dropdown
    de tarefas existentes) — a lane só precisava do modelo + regra;
    UI textarea foi o suficiente.
  - Botões "Concluir" nas telas de detalhe de tarefa/projeto — a
    regra está pronta, falta UI (próxima lane de fluxos).
  - Backend equivalente (`app/services/`) — ADR-0005: app 100%
    offline, regra fica só no Android. Quando #14 vier, vai duplicar
    a regra no FastAPI com as mesmas mensagens.

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
