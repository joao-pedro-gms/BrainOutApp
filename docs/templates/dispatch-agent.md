# Como despachar um agente Hermes num worktree

Receita validada. Use para cada issue do [`PLAN-IMPLEMENTACAO.md`](../PLAN-IMPLEMENTACAO.md).

## Pré-condições

1. Worktree existe: `git worktree list` mostra `wt-<ciclo>`
2. Mestre atualizado: `cd wt-<ciclo> && git pull --rebase origin master`
3. Branch de feature pronta: `git checkout -b feature/<escopo>`

## Despachar

Comando (no Hermes):

```
/omh-agent-board
goal: implementar issue #N — <título>
context:
  - worktree: ../wt-<ciclo>/
  - branch atual: feature/<escopo>
  - arquivos a ler: docs/arquitetura.md, docs/regras-negocio.md (se aplicável), docs/adr/0001-kotlin-compose.md (se Android)
  - arquivos a NÃO tocar: backend/, android/app/src/main/AndroidManifest.xml (escopo fixo), Documentos/
  - padrão de qualidade: ./scripts/quality-check.sh deve passar
output_schema:
  - caminho_arquivos_criados: list[str]
  - testes_adicionados: list[str]
  - comandos_executados: list[str]
  - pendencias: list[str]
constraints:
  - NUNCA commitar (orchestrador commita)
  - NUNCA abrir PR (orchestrador abre)
  - NUNCA rodar `git push`
  - SEMPRE escrever RESULTADO-<issue>.md no worktree
  - SEMPRE rodar ./scripts/quality-check.sh antes de devolver
```

## Após retorno do agente

1. Ler `RESULTADO-<issue>.md` no worktree
2. Verificar `git status` e `git diff` — sem surpresas
3. Rodar `./scripts/quality-check.sh` você mesmo
4. Se OK: `git add . && git commit -m "feat(<escopo>): <msg>"`
5. `git push -u origin feature/<escopo>` e `gh pr create --fill`
6. Marcar issue com `Closes #N` na descrição do PR

## Falhas comuns

- Agente commita mesmo com `NUNCA commitar` → `git reset HEAD~1 && git checkout -- .`
- Agente roda `git push` → remover remote: `git remote remove origin` (recriar depois)
- Agente cria arquivos fora do escopo → apagar manualmente, pedir refazer
- Agente escreve sem testes → devolver com "implementar testes primeiro (TDD)"
