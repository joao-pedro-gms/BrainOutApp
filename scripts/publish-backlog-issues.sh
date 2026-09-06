#!/usr/bin/env bash
# Publica issues a partir de Documentos/backlog/*.md
# Uso:
#   ./scripts/publish-backlog-issues.sh           # dry-run
#   ./scripts/publish-backlog-issues.sh --apply    # cria no GitHub
#
# Requer: gh autenticado com permissão de Issues no repositório.

set -euo pipefail

ROOT="$(git rev-parse --show-toplevel)"
BACKLOG="$ROOT/Documentos/backlog"
APPLY=0
[[ "${1:-}" == "--apply" ]] && APPLY=1

if ! command -v gh >/dev/null 2>&1; then
  echo "gh CLI não encontrado. Instale: https://cli.github.com/"
  exit 1
fi

if ! gh auth status >/dev/null 2>&1; then
  echo "gh não autenticado. Rode: gh auth login"
  exit 1
fi

existing="$(gh issue list --state all --limit 200 --json title --jq '.[].title' 2>/dev/null || true)"

created=0
skipped=0
failed=0

shopt -s nullglob
for file in "$BACKLOG"/[0-9][0-9]-*.md; do
  # frontmatter YAML entre --- 
  title="$(python3 - "$file" <<'PY'
import sys, re
from pathlib import Path
text = Path(sys.argv[1]).read_text(encoding="utf-8")
m = re.match(r"^---\n(.*?)\n---\n(.*)$", text, re.S)
if not m:
    print("", end="")
    sys.exit(0)
import yaml
meta = yaml.safe_load(m.group(1)) or {}
print(meta.get("title", "").strip())
PY
)"
  labels_csv="$(python3 - "$file" <<'PY'
import sys, re
from pathlib import Path
text = Path(sys.argv[1]).read_text(encoding="utf-8")
m = re.match(r"^---\n(.*?)\n---\n(.*)$", text, re.S)
import yaml
meta = yaml.safe_load(m.group(1)) or {}
labels = meta.get("labels") or []
out = []
for lab in labels:
    if isinstance(lab, dict):
        # YAML interpretou "prioridade: alta" como mapping
        out.extend(f"{k}: {v}" for k, v in lab.items())
    else:
        out.append(str(lab))
print(",".join(out))
PY
)"
  body="$(python3 - "$file" <<'PY'
import sys, re
from pathlib import Path
text = Path(sys.argv[1]).read_text(encoding="utf-8")
m = re.match(r"^---\n(.*?)\n---\n(.*)$", text, re.S)
print(m.group(2).strip() if m else text.strip())
PY
)"

  if [[ -z "$title" ]]; then
    echo "IGNORADO (sem title): $file"
    continue
  fi

  if echo "$existing" | grep -Fxq "$title"; then
    echo "SKIP já existe: $title"
    skipped=$((skipped + 1))
    continue
  fi

  echo "NOVA: $title"
  echo "  labels: ${labels_csv:-"(nenhuma)"}"
  echo "  arquivo: $(basename "$file")"

  if [[ "$APPLY" -eq 1 ]]; then
    args=(issue create --title "$title" --body "$body")
    if [[ -n "$labels_csv" ]]; then
      IFS=',' read -ra labs <<< "$labels_csv"
      for lab in "${labs[@]}"; do
        args+=(--label "$lab")
      done
    fi
    if gh "${args[@]}"; then
      created=$((created + 1))
      existing="${existing}"$'\n'"${title}"
    else
      echo "FALHA ao criar: $title" >&2
      failed=$((failed + 1))
    fi
  fi
done

echo
echo "Resumo: criar=${created} skip=${skipped} fail=${failed} apply=${APPLY}"
if [[ "$APPLY" -eq 0 ]]; then
  echo "Dry-run apenas. Reexecute com --apply para publicar."
fi
[[ "$failed" -eq 0 ]]
