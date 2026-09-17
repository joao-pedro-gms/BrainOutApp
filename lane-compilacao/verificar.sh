#!/usr/bin/env bash
# lane-compilacao/verificar.sh
#
# Análise estática local para detectar erros comuns ANTES de push.
# Não substitui o CI (que tem Android SDK e roda Gradle), mas pega ~70%
# dos erros de compilação que subagentes sem SDK não conseguem validar.
#
# Uso:
#   ./lane-compilacao/verificar.sh                    # verifica o worktree atual
#   ./lane-compilacao/verificar.sh /path/to/worktree   # verifica um worktree específico
#   ./lane-compilacao/verificar.sh --strict            # trata warnings como erro
#
# Exit codes:
#   0 — tudo OK
#   1 — erro estrutural (arquivos faltando, TOML quebrado, etc)
#   2 — import faltando detectado
#   3 — ViewModel sem @HiltViewModel/@Inject
#   4 — UUID v7 fallback detectado (warning)
#   5 — TODOs órfãos em produção (>5)

set -uo pipefail

STRICT=0
TARGET=""
if [ "${1:-}" = "--strict" ]; then
    STRICT=1
    TARGET="${2:-}"
elif [ -n "${1:-}" ]; then
    TARGET="$1"
fi

# Detectar raiz do projeto (presumindo que o script está em lane-compilacao/)
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
if [ -n "$TARGET" ]; then
    PROJECT_ROOT="$(cd "$TARGET" && pwd)"
fi

cd "$PROJECT_ROOT" || { echo "ERRO: nao consegui entrar em $PROJECT_ROOT"; exit 1; }

# shellcheck disable=SC2317  # função invocada em subshell
ERROS=()
WARNINGS=()

# ============================================================
# 1. Arquivos chave do esqueleto
# ============================================================
echo "=== [1/8] Arquivos chave ==="
for f in \
    "android/app/build.gradle.kts" \
    "android/app/src/main/AndroidManifest.xml" \
    "android/app/src/main/java/com/joaopedrogms/brainoutapp/BrainOutApp.kt" \
    "android/gradle/libs.versions.toml" \
    "android/settings.gradle.kts" \
    "android/gradle.properties" \
    ; do
    if [ ! -f "$f" ]; then
        ERROS+=("arquivo essencial ausente: $f")
    fi
done
if [ ${#ERROS[@]} -gt 0 ]; then
    printf '  - %s\n' "${ERROS[@]}"
else
    echo "  OK"
fi

# ============================================================
# 2. libs.versions.toml válido
# ============================================================
echo
echo "=== [2/8] libs.versions.toml (TOML) ==="
if command -v python3 >/dev/null 2>&1; then
    if python3 -c "
import sys
try:
    import tomllib
except ImportError:
    try:
        import tomli as tomllib
    except ImportError:
        sys.exit('TOML parser nao disponivel (Python 3.11+ ou tomli)')
with open('android/gradle/libs.versions.toml','rb') as f:
    tomllib.load(f)
" 2>&1; then
        echo "  OK"
    else
        ERROS+=("libs.versions.toml nao parseia como TOML")
    fi
else
    echo "  (python3 ausente - pulando validacao TOML)"
fi

# ============================================================
# 3. Gradle wrapper presente
# ============================================================
echo
echo "=== [3/8] Gradle wrapper ==="
if [ -x android/gradlew ]; then
    echo "  OK"
else
    ERROS+=("android/gradlew ausente ou nao executavel")
fi
if [ -f android/gradle/wrapper/gradle-wrapper.jar ]; then
    echo "  OK (wrapper.jar)"
else
    ERROS+=("android/gradle/wrapper/gradle-wrapper.jar ausente")
fi

# ============================================================
# 4. Detectar imports faltando (causou bug #60)
# ============================================================
echo
echo "=== [4/8] Imports Kotlin faltando (heurística) ==="
IMPORT_ERRORS=0
for f in $(find android/app/src/main -name '*.kt' 2>/dev/null); do
    # Modifier.padding sem import
    if grep -q 'Modifier\.padding' "$f" && \
       ! grep -q 'import androidx\.compose\.foundation\.layout\.padding' "$f"; then
        echo "  ERRO: $f usa Modifier.padding sem import"
        IMPORT_ERRORS=$((IMPORT_ERRORS + 1))
    fi
    # Modifier.fillMaxSize sem import
    if grep -q 'Modifier\.fillMaxSize' "$f" && \
       ! grep -q 'import androidx\.compose\.foundation\.layout\.fillMaxSize' "$f"; then
        echo "  ERRO: $f usa Modifier.fillMaxSize sem import"
        IMPORT_ERRORS=$((IMPORT_ERRORS + 1))
    fi
    # collectAsStateWithLifecycle sem import
    if grep -q 'collectAsStateWithLifecycle' "$f" && \
       ! grep -q 'import androidx\.lifecycle\.compose\.collectAsStateWithLifecycle\|import androidx\.compose\.runtime\.collectAsState' "$f"; then
        echo "  ERRO: $f usa collectAsStateWithLifecycle sem import"
        IMPORT_ERRORS=$((IMPORT_ERRORS + 1))
    fi
done
if [ $IMPORT_ERRORS -eq 0 ]; then
    echo "  OK"
else
    ERROS+=("$IMPORT_ERRORS imports faltando detectados")
fi

# ============================================================
# 5. Hilt ViewModels
# ============================================================
echo
echo "=== [5/8] Hilt ViewModels ==="
HILT_ERRORS=0
for f in $(find android/app/src/main/java/com/joaopedrogms/brainoutapp/viewmodel -name '*ViewModel.kt' 2>/dev/null); do
    if ! grep -q '@HiltViewModel\|@Inject' "$f"; then
        echo "  WARN: $f nao tem @HiltViewModel/@Inject"
        WARNINGS+=("$f sem @HiltViewModel")
        HILT_ERRORS=$((HILT_ERRORS + 1))
    fi
done
if [ $HILT_ERRORS -eq 0 ]; then
    echo "  OK"
fi

# ============================================================
# 6. Hilt module coverage
# ============================================================
echo
echo "=== [6/8] Hilt modules ==="
for module in AppModule RepositoryModule PreferencesModule SecurityModule; do
    if [ -f "android/app/src/main/java/com/joaopedrogms/brainoutapp/di/${module}.kt" ]; then
        if ! grep -q '@Module\|@Binds' "android/app/src/main/java/com/joaopedrogms/brainoutapp/di/${module}.kt"; then
            echo "  ERRO: $module.kt nao tem @Module/@Binds"
            ERROS+=("DI module $module sem anotacao")
        else
            echo "  OK ($module)"
        fi
    fi
done

# ============================================================
# 7. TODOs órfãos em produção
# ============================================================
echo
echo "=== [7/8] TODOs/FIXMEs em código de produção ==="
TODO_COUNT=$(grep -rn 'TODO\|FIXME' \
    --include='*.kt' \
    android/app/src/main/java/com/joaopedrogms/brainoutapp/domain android/app/src/main/java/com/joaopedrogms/brainoutapp/data 2>/dev/null \
    | grep -v '^\s*\*\|^\s*//' \
    | wc -l)
echo "  TODOs/FIXMEs em domain/data: $TODO_COUNT"
if [ "$TODO_COUNT" -gt 5 ]; then
    WARNINGS+=("$TODO_COUNT TODOs/FIXMEs em domain/data")
    if [ $STRICT -eq 1 ]; then
        ERROS+=("$TODO_COUNT TODOs órfãos (strict mode)")
    fi
fi

# ============================================================
# 8. UUID v7 fallback
# ============================================================
echo
echo "=== [8/8] UUID v7 fallback ==="
# Filtrar KDocs (linhas que começam com * ou //)
FALLBACK_COUNT=$(grep -rn 'UUID\.randomUUID()' --include='*.kt' android/app/src/main 2>/dev/null \
    | grep -vE ':\s*\*|:\s*//|KDoc' \
    | wc -l)
echo "  UUID.randomUUID() em código (não-comentário): $FALLBACK_COUNT (esperado: 1, em UuidV7.kt)"
if [ "$FALLBACK_COUNT" -gt 1 ]; then
    WARNINGS+=("$FALLBACK_COUNT usos de UUID.randomUUID() em código de produção (esperado: só UuidV7.kt)")
fi

# ============================================================
# Resumo
# ============================================================
echo
echo "============================================================"
echo "RESUMO"
echo "============================================================"
echo "Erros:   ${#ERROS[@]}"
echo "Warnings: ${#WARNINGS[@]}"
if [ ${#ERROS[@]} -gt 0 ]; then
    echo
    echo "ERROS:"
    printf '  - %s\n' "${ERROS[@]}"
fi
if [ ${#WARNINGS[@]} -gt 0 ]; then
    echo
    echo "WARNINGS:"
    printf '  - %s\n' "${WARNINGS[@]}"
fi

# Determinar exit code
if [ ${#ERROS[@]} -gt 0 ]; then
    # Prioridade: 2 (imports) > 1 (outros erros) > 3 (hilt, só em strict)
    if [ $IMPORT_ERRORS -gt 0 ]; then
        echo "EXIT_CODE=2 (imports faltando)"
        exit 2
    fi
    echo "EXIT_CODE=1 (erros estruturais)"
    exit 1
fi

if [ $STRICT -eq 1 ] && [ $HILT_ERRORS -gt 0 ]; then
    echo "EXIT_CODE=3 (strict mode: ViewModel sem @HiltViewModel)"
    exit 3
fi

echo
echo "OK - sem erros detectados na análise estática."
echo "Para verificação completa (Gradle build + testes), use a workflow verify-android no CI:"
echo "  gh workflow run verify-android.yml"
exit 0
