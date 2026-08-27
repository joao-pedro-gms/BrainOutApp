#!/usr/bin/env bash
# Instala os git hooks do repositório. Rode uma vez após clonar:
#   ./scripts/setup.sh
set -euo pipefail

ROOT="$(git rev-parse --show-toplevel)"
HOOKS_SRC="$ROOT/scripts/hooks"
HOOKS_DST="$ROOT/.git/hooks"

if [ ! -d "$HOOKS_SRC" ]; then
  echo "erro: pasta $HOOKS_SRC não encontrada. Rode a partir da raiz do repo."
  exit 1
fi

for hook in "$HOOKS_SRC"/*; do
  name="$(basename "$hook")"
  cp "$hook" "$HOOKS_DST/$name"
  chmod +x "$HOOKS_DST/$name"
  echo "hook instalado: $name"
done

echo "Pronto. Hooks ativos: pre-commit (scan de secrets) e commit-msg (conventional commits)."
