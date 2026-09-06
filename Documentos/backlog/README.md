# Backlog do produto — GerenciaDeProjetosApp

Issues prontas para o GitHub Issues, alinhadas ao [documento norteador](../Faculdade/) (R1–R14) e ao domínio do app (gestão de projetos e tarefas).

## Como publicar no GitHub

Com um token que tenha permissão de **Issues** (o token do Cloud Agent atual não tem):

```bash
# dry-run (só lista)
./scripts/publish-backlog-issues.sh

# cria as issues que ainda não existem (compara pelo título)
./scripts/publish-backlog-issues.sh --apply
```

Cada arquivo `NN-*.md` abaixo vira **uma issue**. O frontmatter define título e labels.

## Ordem sugerida (Checkpoint 1 → N2)

| # | Arquivo | Ciclo | Foco |
|---|---------|-------|------|
| 01 | Escopo e personas | concepção | Artefato N1 |
| 02 | Modelagem e arquitetura | concepção | Artefato N1 |
| 03 | Protótipo navegável | concepção | Checkpoint 1 |
| 04 | Scaffold Android + camadas | ciclo-1 | R12 |
| 05 | Navegação (6+ telas) | ciclo-1 | R1 |
| 06 | Auth + perfis Gerente/Colaborador | ciclo-1 | R2 |
| 07 | CRUD Projetos + Room | ciclo-1 | R3, R5 |
| 08 | CRUD Tarefas + validação | ciclo-2 | R3 |
| 09 | Regras de negócio (3+) | ciclo-2 | R4 |
| 10 | Busca, filtros e ordenação | ciclo-2 | R9 |
| 11 | Dashboard consolidado | ciclo-2 | R9 |
| 12 | API FastAPI + sync | ciclo-3 | R6, R10 |
| 13 | Integração API externa | ciclo-3 | R7 |
| 14 | Notificações de prazo | ciclo-3 | R8 |
| 15 | Acessibilidade e estados de UI | ciclo-4 | R10, R11 |
| 16 | Roteiro de testes + usabilidade | verificação | Seção 6.3 |
| 17 | APK e dispositivo físico | ciclo-4 | R14 |
| 18 | Relatório técnico final | encerramento | N2 |

## Convenção de labels

Use as labels já criadas no repositório: `feature`, `docs`, `test`, `android`, `backend`, `concepção`, `ciclo-1`…`ciclo-4`, `verificação`, `encerramento`, `R1`…`R14`, `prioridade: alta|média|baixa`, `qualidade`.
