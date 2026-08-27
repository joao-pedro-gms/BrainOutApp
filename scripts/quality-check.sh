#!/usr/bin/env bash
# Verificações de qualidade locais. Rode antes de abrir PR:
#   ./scripts/quality-check.sh
# Só executa o que existir no repositório (app ou backend ainda não criados não falham o script).

set -uo pipefail
ROOT="$(git rev-parse --show-toplevel)"
FAIL=0

echo "== Android =="
if [ -d "$ROOT/android" ] && [ -x "$ROOT/android/gradlew" ]; then
  (cd "$ROOT/android" && ./gradlew ktlintCheck detekt 2>/dev/null) \
    && echo "ktlint/detekt: ok" || { echo "ktlint/detekt: FALHOU"; FAIL=1; }
else
  echo "projeto Android ainda não existe, pulando"
fi

echo "== Backend =="
if [ -d "$ROOT/backend" ]; then
  if command -v ruff >/dev/null 2>&1; then
    ruff check "$ROOT/backend" && echo "ruff: ok" || { echo "ruff: FALHOU"; FAIL=1; }
  elif [ -f "$ROOT/backend/.venv/bin/ruff" ]; then
    "$ROOT/backend/.venv/bin/ruff" check "$ROOT/backend" && echo "ruff: ok" || { echo "ruff: FALHOU"; FAIL=1; }
  else
    echo "ruff não instalado (pip install ruff), pulando"
  fi
else
  echo "backend ainda não existe, pulando"
fi

echo "== Secrets no repositório =="
if command -v gitleaks >/dev/null 2>&1; then
  gitleaks detect --source "$ROOT" --no-banner && echo "gitleaks: ok" || { echo "gitleaks: FALHOU"; FAIL=1; }
else
  echo "gitleaks não instalado, usando scan básico"
  if git grep -nIE '(BEGIN (RSA|OPENSSH|EC|PGP) PRIVATE KEY|AKIA[0-9A-Z]{16}|AIza[0-9A-Za-z_-]{35}|ghp_[0-9A-Za-z]{36})' -- . ':(exclude)*.example' >/dev/null 2>&1; then
    echo "possível secret versionado:"; FAIL=1
    git grep -nIE '(BEGIN (RSA|OPENSSH|EC|PGP) PRIVATE KEY|AKIA[0-9A-Z]{16}|AIza[0-9A-Za-z_-]{35}|ghp_[0-9A-Za-z]{36})' -- . ':(exclude)*.example'
  else
    echo "scan básico: ok"
  fi
fi

if [ "$FAIL" -ne 0 ]; then
  echo; echo "quality-check: FALHOU"
  exit 1
fi
echo; echo "quality-check: tudo certo"
