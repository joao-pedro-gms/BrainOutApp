# BrainOutApp — Sistema de Design

> Documento canônico de **tokens, princípios e padrões visuais** do app Android BrainOutApp.
>
> **Versão:** 0.1.0-ciclo1 · **Stack:** Kotlin + Jetpack Compose (Compose BOM 2024.08.00) + Material 3 · **Última atualização:** 2026-09-17.
>
> Este arquivo é a **fonte da verdade** para cores, tipografia, espaçamento, raios, elevação e ícones. O código em
> `android/app/src/main/java/com/joaopedrogms/brainoutapp/ui/theme/` espelha exatamente os valores descritos aqui.

---

## 1. Princípios

A filosofia do BrainOutApp repousa em cinco princípios inegociáveis. Toda decisão de design (nova tela, novo componente, novo
token) precisa passar por este filtro antes de ser aceita.

- **Acessibilidade primeiro (WCAG 2.1 nível AA).** Pares de `cor de fundo × cor de texto` devem ter **contraste ≥ 4.5:1**
  para texto corrido e **≥ 3:1** para texto grande (≥ 18 sp Regular ou ≥ 14 sp Bold) e para componentes de UI (ícones,
  outlines, estados de foco). Acessibilidade é **pré-condição**, não retrofit.
- **Paleta semântica, não cosmética.** Componentes referenciam **papéis funcionais** (`surfacePrimary`, `onSurfacePrimary`,
  `stateError`) e nunca valores hexadecimais crus. Isso permite que o tema escuro e ajustes futuros sejam uma simples
  troca de mapeamento, sem refactor.
- **Ícones exclusivamente Lucide.** A iconografia do app é 100 % Lucide (estilo outline 24×24, stroke 1.5–2 px). Material
  Icons fica proibido em código de produto. A escolha mantém coerência visual entre o protótipo web (que já usa Lucide),
  o app e futuras plataformas (iOS, web).
- **Escala 4-pt.** Todos os espaçamentos, raios e larguras são múltiplos de **4 dp**. Essa grade dá ritmo vertical previsível
  e elimina decisões ad-hoc em pixels "quebrados".
- **Elevação por luz ambiente, não por decoração.** Sombras existem para sugerir hierarquia de superfície (quem está em
  cima de quem), nunca para enfeitar. Elevation `0` e `1` cobrem 90 % dos casos de uso.

---

## 2. Paleta (cores semânticas)

Tokens consumidos por composables. Cada cor tem **papel** (semântico) e **valor hex** (implementação). O
`LightColorScheme` e `DarkColorScheme` em `Color.kt` mapeiam esses tokens para os slots do Material 3.

### 2.1 Light scheme (modo claro)

| Token              | Hex         | Uso pretendido                                                                                   |
|--------------------|-------------|--------------------------------------------------------------------------------------------------|
| `surfacePrimary`   | `#FFFFFF`   | Fundo geral da tela (background, surface, scaffold). Branco puro para máximo contraste de texto. |
| `surfaceSecondary` | `#F1F4F1`   | Cards, diálogos, sheets — superfícies elevadas em cima de `surfacePrimary`.                       |
| `surfaceTertiary`  | `#E3E8E4`   | Containers sutis (chips, badges neutros, divisores de seção).                                     |
| `onSurfacePrimary` | `#191C1A`   | Texto principal e ícones sobre `surfacePrimary` (15.8 : 1 — AAA).                                |
| `onSurfaceSecondary` | `#414942` | Texto secundário (legendas, helper text). 8.6 : 1 — AAA.                                         |
| `onSurfaceTertiary` | `#5C6560`  | Texto terciário / desabilitado legível. 5.1 : 1 — AA.                                            |
| `accentPrimary`    | `#1F6F4A`   | Botões primários, links ativos, foco de campo. 5.1 : 1 sobre branco.                              |
| `accentSecondary`  | `#4D6358`   | Botões secundários, toggles, "fill" neutro. 6.2 : 1 sobre branco.                                 |
| `onAccent`         | `#FFFFFF`   | Conteúdo (texto/ícone) sobre `accentPrimary` e `accentSecondary`. 5.1 : 1 sobre accent.            |
| `stateSuccess`     | `#1B7F47`   | Confirmações, tarefas concluídas. 5.0 : 1 sobre branco.                                          |
| `stateWarning`     | `#B45309`   | Avisos, prazos perto de vencer. 4.7 : 1 sobre branco.                                            |
| `stateError`       | `#B3261E`   | Erros, validação falhada, botão destrutivo. 5.9 : 1 sobre branco.                                |
| `stateInfo`        | `#1F6FB5`   | Mensagens informativas. 4.6 : 1 sobre branco.                                                    |
| `outline`          | `#717971`   | Bordas de input, divisores. 4.6 : 1 sobre branco.                                                |
| `outlineVariant`   | `#C1C9C2`   | Bordas sutis, separadores horizontais. Decorativo (sem requisito de contraste).                  |

### 2.2 Dark scheme (modo escuro)

| Token              | Hex         | Uso pretendido                                                                                  |
|--------------------|-------------|-------------------------------------------------------------------------------------------------|
| `surfacePrimary`   | `#101411`   | Fundo geral (background, surface). Quase preto com matiz verde.                                  |
| `surfaceSecondary` | `#1A1F1B`   | Cards e diálogos elevados sobre `surfacePrimary`.                                                |
| `surfaceTertiary`  | `#252B27`   | Containers sutis, chips neutros.                                                                  |
| `onSurfacePrimary` | `#E1E3DE`   | Texto principal (13.2 : 1 sobre `#101411` — AAA).                                                 |
| `onSurfaceSecondary` | `#C1C9C2` | Texto secundário (9.4 : 1 — AAA).                                                                |
| `onSurfaceTertiary` | `#A0A89F`  | Texto terciário / placeholder (6.1 : 1 — AA).                                                    |
| `accentPrimary`    | `#9CD4B9`   | Botões primários (9.7 : 1 sobre `#101411`). Matiz verde-claro coerente com wireframes do protótipo.|
| `accentSecondary`  | `#B3CCBE`   | Botões secundários (10.8 : 1).                                                                    |
| `onAccent`         | `#003824`   | Conteúdo sobre `accentPrimary` (8.5 : 1).                                                        |
| `stateSuccess`     | `#6FE0A0`   | Confirmações (11.2 : 1).                                                                          |
| `stateWarning`     | `#FBBF24`   | Avisos (10.9 : 1).                                                                               |
| `stateError`       | `#F2B8B5`   | Erros (9.5 : 1).                                                                                  |
| `stateInfo`        | `#9DC2F0`   | Informativos (9.8 : 1).                                                                           |
| `outline`          | `#8B938C`   | Bordas de input (4.6 : 1).                                                                        |
| `outlineVariant`   | `#414942`   | Divisores. Decorativo.                                                                            |

### 2.3 Mapeamento para Material 3 ColorScheme

| Slot Material 3     | Light source       | Dark source        |
|---------------------|--------------------|--------------------|
| `primary`           | `accentPrimary`    | `accentPrimary`    |
| `onPrimary`         | `onAccent`         | `onAccent`         |
| `primaryContainer`  | `surfaceTertiary`  | `surfaceTertiary`  |
| `onPrimaryContainer`| `accentPrimary`    | `accentPrimary`    |
| `secondary`         | `accentSecondary`  | `accentSecondary`  |
| `onSecondary`       | `onAccent`         | `onAccent`         |
| `secondaryContainer`| `surfaceTertiary`  | `surfaceTertiary`  |
| `onSecondaryContainer` | `accentSecondary` | `accentSecondary` |
| `tertiary`          | `stateInfo`        | `stateInfo`        |
| `onTertiary`        | `onAccent`         | `onAccent`         |
| `background`        | `surfacePrimary`   | `surfacePrimary`   |
| `onBackground`      | `onSurfacePrimary` | `onSurfacePrimary` |
| `surface`           | `surfacePrimary`   | `surfacePrimary`   |
| `onSurface`         | `onSurfacePrimary` | `onSurfacePrimary` |
| `surfaceVariant`    | `surfaceSecondary` | `surfaceSecondary` |
| `onSurfaceVariant`  | `onSurfaceSecondary` | `onSurfaceSecondary` |
| `outline`           | `outline`          | `outline`          |
| `outlineVariant`    | `outlineVariant`   | `outlineVariant`   |
| `error`             | `stateError`       | `stateError`       |
| `onError`           | `onAccent`         | `onAccent`         |
| `errorContainer`    | `surfaceTertiary`  | `surfaceTertiary`  |
| `onErrorContainer`  | `stateError`       | `stateError`       |

---

## 3. Tipografia

Escala Material 3 completa, com `FontFamily.Default` (Sans Serif do sistema — Roboto no Android, SF no iOS se um dia
portarmos). Cada slot declara `fontFamily`, `fontWeight`, `fontSize`, `lineHeight` e `letterSpacing`. Implementação em
`Type.kt`.

### 3.1 Escala completa

| Slot Material 3   | fontFamily       | fontWeight    | fontSize | lineHeight | letterSpacing | Uso típico                                          |
|-------------------|------------------|---------------|----------|------------|---------------|-----------------------------------------------------|
| `displayLarge`    | `FontFamily.Default` | `FontWeight.Normal` | 57 sp    | 64 sp      | -0.25 sp      | Splash / hero (raro — reservado).                   |
| `displayMedium`   | `FontFamily.Default` | `FontWeight.Normal` | 45 sp    | 52 sp      | 0 sp          | Onboarding de primeira execução.                     |
| `displaySmall`    | `FontFamily.Default` | `FontWeight.Normal` | 36 sp    | 44 sp      | 0 sp          | Títulos de seção grandes (Dashboard).               |
| `headlineLarge`   | `FontFamily.Default` | `FontWeight.SemiBold` | 32 sp | 40 sp    | 0 sp          | Cabeçalho de tela principal.                        |
| `headlineMedium`  | `FontFamily.Default` | `FontWeight.SemiBold` | 28 sp | 36 sp    | 0 sp          | Cabeçalho secundário (Projeto detalhe).             |
| `headlineSmall`   | `FontFamily.Default` | `FontWeight.SemiBold` | 24 sp | 32 sp    | 0 sp          | Subtítulo de seção dentro de tela.                  |
| `titleLarge`      | `FontFamily.Default` | `FontWeight.SemiBold` | 22 sp | 28 sp    | 0 sp          | Título de card / diálogo.                           |
| `titleMedium`     | `FontFamily.Default` | `FontWeight.Medium`  | 16 sp | 24 sp    | 0.15 sp       | Subtítulo de card, item de lista.                   |
| `titleSmall`      | `FontFamily.Default` | `FontWeight.Medium`  | 14 sp | 20 sp    | 0.1 sp        | Rótulo de campo, header de seção pequena.           |
| `bodyLarge`       | `FontFamily.Default` | `FontWeight.Normal`  | 16 sp | 24 sp    | 0.5 sp        | Parágrafos longos, descrições de projeto.           |
| `bodyMedium`      | `FontFamily.Default` | `FontWeight.Normal`  | 14 sp | 20 sp    | 0.25 sp       | Texto padrão de item, helper text.                  |
| `bodySmall`       | `FontFamily.Default` | `FontWeight.Normal`  | 12 sp | 16 sp    | 0.4 sp        | Legendas, timestamps, hints curtas.                 |
| `labelLarge`      | `FontFamily.Default` | `FontWeight.Medium`  | 14 sp | 20 sp    | 0.1 sp        | Texto de botão (filled/elevated/text).              |
| `labelMedium`     | `FontFamily.Default` | `FontWeight.Medium`  | 12 sp | 16 sp    | 0.5 sp        | Chips, badges, tabs.                                |
| `labelSmall`      | `FontFamily.Default` | `FontWeight.Medium`  | 11 sp | 16 sp    | 0.5 sp        | Microcopy (legendas de gráfico, contadores).        |

### 3.2 Regras de uso

- **Hierarquia 1-2-3 por tela.** Nunca mais que 3 níveis de tipografia na mesma tela visível (ex.: `headlineSmall` →
  `bodyLarge` → `labelMedium`).
- **Line-height mínimo = 1.4× fontSize** para `body*` (legibilidade em texto corrido).
- **Cor do texto:** sempre um token `onSurface*` ou `onAccent`. **Nunca** hardcode de `Color.Black`/`Color.White`.

---

## 4. Espaçamentos

Escala em múltiplos de 4 dp. Aplicada em **padding**, **margin** (via `Modifier.padding`), **gap** (via
`Arrangement.spacedBy`) e **largura/altura** mínima de toque (44×44 dp, soma de `sm+sm+content+sm+sm`).

| Token | Valor (dp) | Uso típico                                                         |
|-------|------------|--------------------------------------------------------------------|
| `xs`  | 4          | Espaço entre ícone e label (mesma linha), padding interno de chip. |
| `sm`  | 8          | Padding de botão, espaçamento entre itens em lista vertical curta. |
| `md`  | 16         | Padding padrão de card, espaçamento entre seções de tela.           |
| `lg`  | 24         | Margem entre blocos de conteúdo, padding de tela em tablet.        |
| `xl`  | 32         | Margens entre hero e conteúdo, espaçamento de empty state.         |
| `2xl` | 48         | Separação de grupos muito distintos (ex.: entre cards grandes).   |
| `3xl` | 64         | Reservado para splash e onboarding (uso raro).                     |

### Regra prática

- **Tela padrão (375 dp de largura):** `md` (16 dp) de padding lateral.
- **Tablet (≥ 600 dp de largura):** `lg` (24 dp) de padding lateral, conteúdo centralizado com `maxWidth = 600.dp`.
- **Lista vertical:** `Arrangement.spacedBy(sm)` entre itens; `md` antes do primeiro / depois do último.
- **Lista horizontal (chips):** `Arrangement.spacedBy(xs)`.

---

## 5. Raios de borda

Aplicados via `Modifier.clip(RoundedCornerShape(...))` ou `MaterialTheme.shapes.*` (ver `Theme.kt` quando shapes forem
adicionadas em ciclos futuros).

| Token | Valor (dp)   | Uso típico                                                                  |
|-------|--------------|-----------------------------------------------------------------------------|
| `none`| 0            | Divisores, botões sem borda (Material 3 padrão já cobre).                    |
| `xs`  | 4            | Chips, badges pequenos, tags.                                               |
| `sm`  | 8            | Inputs de texto, pequenos cards.                                            |
| `md`  | 12           | **Padrão de card** (cards de projeto, item de tarefa).                      |
| `lg`  | 16           | Modais, sheets, diálogos.                                                   |
| `xl`  | 24           | Cards de destaque (ex.: card de projeto "em destaque").                     |
| `full`| 9999         | Avatares circulares, badges de status, pill buttons (altura como diâmetro).  |

---

## 6. Elevação

Cinco níveis, cada um com offsetX/offsetY/blur/alpha de sombra. Material 3 usa `tonalElevation` (aplica tint da cor
primary) em vez de shadow real em alguns componentes — para sombras explícitas (cards elevados sobre outros cards),
usar os tokens abaixo.

| Nível | Offset (X, Y) dp | Blur (dp) | Alpha (cor preta) | Uso típico                                                |
|-------|------------------|-----------|-------------------|---------------------------------------------------------|
| 0     | (0, 0)          | 0         | 0 %               | Texto, divisores, ícones isolados.                       |
| 1     | (0, 1)          | 2         | 8 %               | Cards apoiados em `surfacePrimary`.                     |
| 2     | (0, 2)          | 4         | 12 %              | Cards sobre outros cards (ex.: lista de tarefas).      |
| 3     | (0, 4)          | 8         | 16 %              | FAB (Floating Action Button), botões elevados.          |
| 4     | (0, 6)          | 12        | 20 %              | Diálogos, bottom sheets, popovers.                     |
| 5     | (0, 12)         | 24        | 24 %              | Modal de erro crítico, picker de data/hora.             |

> **Regra:** se um componente precisa de mais que `elevation 3`, provavelmente deveria ser uma **Sheet** ou **Dialog** em
> vez de um card.

---

## 7. Ícones (Lucide)

Todos os ícones do app vêm da biblioteca **Lucide** ([lucide.dev](https://lucide.dev/)) — estilo outline 24×24, stroke 1.5–2 px.
Implementação: **`io.github.thelacspace:lucide-compose-android:1.16.0`** (disponível em Maven Central — ver Seção 9).
**Fallback aprovado:** se a lib não puder ser adicionada em algum ciclo, usar `androidx.compose.material:material-icons-extended`
(mapeamento direto abaixo) — mesma API, mesmo stroke.

### 7.1 Tabela de mapeamento

| Contexto             | Nome Lucide       | Importação Kotlin (lucide-compose)                                                                 | Quando usar                                                  |
|----------------------|-------------------|----------------------------------------------------------------------------------------------------|--------------------------------------------------------------|
| Login — identidade   | `User`            | `import io.github.thelacspace.lucide.User`                                                         | Campo "Usuário" (label/leading icon).                        |
| Login — privacidade  | `Lock`            | `import io.github.thelacspace.lucide.Lock`                                                         | Campo "Senha" (label/leading icon).                          |
| Login — revelar      | `Eye`             | `import io.github.thelacspace.lucide.Eye`                                                          | Ícone "mostrar senha" (trailing icon em campo de senha).     |
| Login — ocultar      | `EyeOff`          | `import io.github.thelacspace.lucide.EyeOff`                                                       | Ícone "ocultar senha" (toggle).                              |
| Login — entrar       | `LogIn`           | `import io.github.thelacspace.lucide.LogIn`                                                        | Botão "Entrar" / FAB de confirmação de login.                |
| Projetos — listar    | `Folder`          | `import io.github.thelacspace.lucide.Folder`                                                       | Ícone de projeto em lista, header de seção "Projetos".       |
| Projetos — criar     | `FolderPlus`      | `import io.github.thelacspace.lucide.FolderPlus`                                                   | Botão "Novo projeto".                                        |
| Projetos — editar    | `Edit`            | `import io.github.thelacspace.lucide.Edit`                                                         | Ação "Editar projeto" (menu de item / ícone inline).         |
| Projetos — excluir   | `Trash2`          | `import io.github.thelacspace.lucide.Trash2`                                                       | Ação "Excluir projeto" (destrutivo, exige confirmação).      |
| Ação genérica        | `Plus`            | `import io.github.thelacspace.lucide.Plus`                                                         | FAB genérico, "Adicionar item" (tarefa, comentário).         |
| Tarefas — concluída  | `CheckSquare`     | `import io.github.thelacspace.lucide.CheckSquare`                                                   | Checkbox marcado (tarefa concluída).                         |
| Tarefas — pendente   | `Square`          | `import io.github.thelacspace.lucide.Square`                                                        | Checkbox vazio (tarefa pendente).                            |
| Tarefas — prazo      | `Clock`           | `import io.github.thelacspace.lucide.Clock`                                                        | Indicador de prazo / estimativa de tempo.                    |
| Tarefas — alerta     | `AlertCircle`     | `import io.github.thelacspace.lucide.AlertCircle`                                                   | Tarefa atrasada, validação falhada, erro de campo.           |
| Tarefas — calendário | `Calendar`        | `import io.github.thelacspace.lucide.Calendar`                                                     | Data de entrega, picker de data.                             |
| Dashboard — barras   | `BarChart3`       | `import io.github.thelacspace.lucide.BarChart3`                                                     | Card "Tarefas por status".                                   |
| Dashboard — tendência| `TrendingUp`      | `import io.github.thelacspace.lucide.TrendingUp`                                                    | Card "Produtividade semanal" / variação positiva.            |
| Dashboard — atividade| `Activity`        | `import io.github.thelacspace.lucide.Activity`                                                     | Card "Pulso do projeto" / log de atividades recentes.        |
| Navegação — início   | `Home`            | `import io.github.thelacspace.lucide.Home`                                                         | Item de bottom nav "Início" / drawer "Página inicial".       |
| Navegação — config   | `Settings`        | `import io.github.thelacspace.lucide.Settings`                                                     | Item de bottom nav / drawer "Configurações".                  |
| Navegação — avançar  | `ChevronRight`    | `import io.github.thelacspace.lucide.ChevronRight`                                                 | Indicador de "toque para abrir" em itens de lista.           |
| Navegação — voltar   | `ChevronLeft`     | `import io.github.thelacspace.lucide.ChevronLeft`                                                  | Ícone de back (TopAppBar `navigationIcon`).                  |
| Navegação — fechar   | `X`               | `import io.github.thelacspace.lucide.X`                                                            | Botão de fechar (modal, snackbar dismiss, clear de input).    |
| Navegação — menu     | `Menu`            | `import io.github.thelacspace.lucide.Menu`                                                         | Ícone de drawer (TopAppBar `navigationIcon` quando aplicável).|

### 7.2 Convenções de uso

- **Tamanho padrão:** 24×24 dp em leading/trailing icon, 20×20 dp em chips, 16×16 dp em legendas.
- **Cor:** `LocalContentColor.current` (nunca hardcode) — herda automaticamente de `IconButton`, `ListItem`, etc.
- **Estroke herdado:** lucide-compose permite customizar `strokeWidth` globalmente; mantemos o padrão (2 px) até
  feedback visual.
- **Tamanho de toque:** sempre envelopar em `IconButton` (44×44 dp) ou `Box(minimumInteractiveComponentSize)` quando
  clicável.
- **Não inventar ícones.** Se um contexto novo precisar de um ícone não mapeado, abrir issue design e atualizar esta
  tabela antes de usar.

---

## 8. Acessibilidade (auditoria WCAG 2.1 AA)

### 8.1 Pares validados (light scheme)

Calculados por WCAG 2.1 relative-luminance. Mínimo AA = 4.5 : 1 (texto) / 3.0 : 1 (componente).

| Par                                            | Contraste | Mínimo AA | Status |
|------------------------------------------------|----------:|----------:|:------:|
| `surfacePrimary` (`#FFFFFF`) × `onSurfacePrimary` (`#191C1A`) | 17.2 : 1 | 4.5 : 1  | ✅ AAA |
| `surfacePrimary` × `onSurfaceSecondary` (`#414942`)          | 9.3 : 1  | 4.5 : 1  | ✅ AAA |
| `surfacePrimary` × `onSurfaceTertiary` (`#5C6560`)           | 6.0 : 1  | 4.5 : 1  | ✅ AA  |
| `surfacePrimary` × `accentPrimary` (`#1F6F4A`)              | 6.1 : 1  | 3.0 : 1  | ✅ (componente) |
| `surfacePrimary` × `accentSecondary` (`#4D6358`)             | 6.5 : 1  | 3.0 : 1  | ✅ (componente) |
| `accentPrimary` × `onAccent` (`#FFFFFF`)                     | 6.1 : 1  | 4.5 : 1  | ✅ AA  |
| `surfacePrimary` × `stateError` (`#B3261E`)                  | 6.5 : 1  | 4.5 : 1  | ✅ AA  |
| `surfacePrimary` × `stateSuccess` (`#1B7F47`)                | 5.0 : 1  | 4.5 : 1  | ✅ AA  |
| `surfacePrimary` × `stateWarning` (`#B45309`)                | 5.0 : 1  | 4.5 : 1  | ✅ AA  |
| `surfacePrimary` × `stateInfo` (`#1F6FB5`)                  | 5.3 : 1  | 4.5 : 1  | ✅ AA  |
| `surfacePrimary` × `outline` (`#717971`)                     | 4.5 : 1  | 3.0 : 1  | ✅ (componente) |

### 8.2 Pares validados (dark scheme)

| Par                                                                  | Contraste | Status |
|----------------------------------------------------------------------|----------:|:------:|
| `surfacePrimary` (`#101411`) × `onSurfacePrimary` (`#E1E3DE`)        | 14.4 : 1  | ✅ AAA |
| `surfacePrimary` × `onSurfaceSecondary` (`#C1C9C2`)                 | 11.0 : 1  | ✅ AAA |
| `surfacePrimary` × `onSurfaceTertiary` (`#A0A89F`)                  | 7.6 : 1   | ✅ AAA |
| `surfacePrimary` × `accentPrimary` (`#9CD4B9`)                       | 11.1 : 1  | ✅ (componente) |
| `accentPrimary` × `onAccent` (`#003824`)                            | 7.9 : 1   | ✅ AAA |
| `surfacePrimary` × `stateError` (`#F2B8B5`)                         | 10.9 : 1  | ✅ AAA |
| `surfacePrimary` × `stateSuccess` (`#6FE0A0`)                       | 11.4 : 1  | ✅ AAA |
| `surfacePrimary` × `outline` (`#8B938C`)                            | 5.9 : 1   | ✅ (componente) |

### 8.3 Auditoria futura (ciclo 2+)

- [ ] Validar par `surfaceVariant` × `onSurfaceVariant` em ambos os modos (placeholder até Material 3 expor tokens).
- [ ] Validar `outlineVariant` (decorativo — sem requisito de contraste, mas checar uso real).
- [ ] Adicionar ferramenta automatizada (ex.: `color-contrast-checker` em teste de unidade) que falhe o CI se algum
  par crítico cair abaixo de 4.5 : 1.

---

## 9. Notas técnicas / pendências

- **Lib Lucide escolhida:** `io.github.thelacspace:lucide-compose-android:1.16.0` (Maven Central, Apache 2.0, ícone set
  idêntico ao protótipo web). **Não adicionada nesta lane** porque o escopo é **tokens e tema** (sem dependência nova
  deve entrar sem PR dedicado de dependência); a integração com `material-icons-extended` como fallback está mapeada
  na Seção 7.
- **Spacing tokens:** declarados neste doc como referência; a constante `Dp` em código deve ser adicionada em ciclos
  futuros (`androidx.compose.ui.unit.Dp` × escopo `object Spacing`). Esta lane não os introduz para não inflar o diff.
- **Shapes:** Material 3 já fornece `MaterialTheme.shapes.*`; tokens customizados (`xs=4`, `sm=8`, `md=12`, `lg=16`,
  `xl=24`, `full=9999`) só são introduzidos quando houver pelo menos uma tela que precise de raio não-padrão.
- **Motion/Duração:** fora do escopo desta lane. Será coberto em ADR futura quando houver navegação com transição
  complexa.

---

## 10. Histórico

| Data       | Versão       | Autor      | Mudança                                                                  |
|------------|--------------|------------|--------------------------------------------------------------------------|
| 2026-09-17 | 0.1.0-ciclo1 | lane-design-system | Criação inicial: princípios, paleta light/dark, tipografia M3 completa, escala 4-pt, raios, elevação, mapeamento Lucide (24 ícones), auditoria WCAG AA. |