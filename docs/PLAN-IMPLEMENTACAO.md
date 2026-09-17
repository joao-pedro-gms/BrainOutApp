# Plano de Implementação por Issue

Mapeamento de cada issue → worktree → agente → comandos. **Custo zero** (todos os agentes usam o gateway LLM local via Hermes; nenhum serviço externo pago).

## Estratégia geral

1. **Worktree por ciclo** — um worktree por fase do cronograma (6 ciclos: Concepção, 1, 2, 3, 4, Encerramento). Cada worktree é um diretório independente com branch própria.
2. **1 subagente sequencial por worktree** — o agente processa as issues do ciclo em ordem. Não paraleliza dentro do worktree para evitar conflitos.
3. **Não commita direto** — o agente escreve código, testes e resultado em `RESULTADO-<issue>.md`. O orquestrador valida, commita e abre PR.
4. **Validação obrigatória** — `./scripts/quality-check.sh` + testes + CI verde antes de merge.

## Estrutura de worktrees

```
BrainOutApp/                          ← master (recebe merges)
  wt-concepcao/                       ← branch chore/concepcao
  wt-ciclo-1/                         ← branch feature/ciclo-1
  wt-ciclo-2/                         ← branch feature/ciclo-2
  wt-ciclo-3/                         ← branch feature/ciclo-3
  wt-ciclo-4/                         ← branch feature/ciclo-4
  wt-encerramento/                    ← branch docs/encerramento
```

Cada worktree compartilha `.git/` (barato em disco). Memória dos agentes não vaza entre worktrees.

## Mapeamento de issues

### 🔵 Concepção (worktree `wt-concepcao`, branch `chore/concepcao`)

Issues já estão bem escritas e completas — **não há muito código a escrever**. Agente foca em fechar os artefatos textuais.

| Issue | Título | Trabalho do agente | Bloqueios |
|-------|--------|-------------------|-----------|
| #1 | Documento de projeto: escopo, personas, requisitos e regras de negócio | Preencher template N1 em [`templates/N1-documento-projeto.md`](templates/N1-documento-projeto.md). Validar contra Apêndice A.1 do norteador. | — |
| #2 | Modelagem de dados e definição arquitetural | Criar [`docs/modelo-dados.md`](modelo-dados.md) com diagrama ER (mermaid) + atualizar [`docs/arquitetura.md`](arquitetura.md) com diagrama de camadas. Justificar stack no memorial. | — |
| #3 | Protótipo navegável (mín. 6 telas) | Criar `prototipos/telas/` com PNG export do Figma + memorial descritivo em [`docs/prototipo.md`](prototipo.md). | #1 |
| #4 | Configurar repositório, branches, hooks e CI | Faltam: proteger master (já feito via gh CLI em 2026-09-17), criar GitHub Project, validar hooks em 2+ máquinas. | — |
| #5 | Backlog priorizado e distribuição de responsabilidades | Criar [`docs/quadro.md`](quadro.md) com tabela (responsável, prioridade, estimativa). Sincronizar com GitHub Projects. | — |

**Resultado esperado do worktree:** 4 PRs contra master (`chore(concepcao): docs N1`, etc), issues #1, #2, #3, #5 fechadas, #4 com checklist completo.

### 🟢 Ciclo 1 — semanas 7-8 (worktree `wt-ciclo-1`, branch `feature/ciclo-1`)

| Issue | Título | Trabalho do agente | Bloqueios |
|-------|--------|-------------------|-----------|
| #6 | Estrutura do app em camadas e navegação base | Criar `android/` com `app/build.gradle.kts`, `settings.gradle.kts`, gradle wrapper. Estrutura de pacotes ui/domain/data/di. NavHost com 6 telas (stubs). Hilt configurado. Tema base. | Concepção OK |
| #7 | Autenticação com 2 perfis | Telas login/cadastro. JWT storage em EncryptedSharedPreferences. Guards por perfil no NavGraph. Backend mínimo: endpoint `/auth/login`, `/auth/register`, hash bcrypt, JWT. | #6 |
| #8 | CRUD de Projetos com Room | Room entity + DAO + Repository. Telas lista/detalhe/criar/editar. ViewModel. Validações (nome obrigatório, prazo > início). Empty state. | #6 |
| #9 | Entrega N1: artefatos, demonstração e apresentação | Compor slides a partir dos artefatos já gerados em #1, #2, #3, #5. Roteiro Apêndice D. | #1, #2, #3 |

**Resultado:** APK debug instalável com login + CRUD projetos funcionando offline. Backend mínimo com /auth.

### 🟡 Ciclo 2 — semanas 11-12 (worktree `wt-ciclo-2`, branch `feature/ciclo-2`)

| Issue | Título | Trabalho | Bloqueios |
|-------|--------|----------|-----------|
| #10 | CRUD de Tarefas | Repetir padrão de #8 para Tarefa. FK para Projeto. Decidir cascade vs. bloqueio (registrar ADR). | #8 |
| #11 | Regras de negócio RN01-RN03 | Implementar em `domain/` Android e `services/` backend. Testes cobrindo cada regra (TDD). Documentar em [`docs/regras-negocio.md`](regras-negocio.md). | #8, #10 |
| #12 | Listagens com filtro, busca e ordenação | Search em `Room` (FTS se precisar). UI: chips de filtro + sort. Debounce na busca. | #8, #10 |
| #13 | Dashboard com visão consolidada | Tela Dashboard com cards: atrasadas, concluídas vs abertas, prazo 7d. Gráfico simples (Compose Canvas). | #8, #10 |

**Resultado:** Beta com CRUD completo + RN + dashboard.

### 🟠 Ciclo 3 — semanas 13-14 (worktree `wt-ciclo-3`, branch `feature/ciclo-3`)

| Issue | Título | Trabalho | Bloqueios |
|-------|--------|----------|-----------|
| #14 | API backend em Python com sincronização | Implementar backend completo: CRUD projetos/tarefas, JWT, sync endpoints. Documentar resolução de conflito (ADR). | #7, #8 |
| #15 | Integração com API externa | Escolher (BrasilAPI feriados ou ViaCEP) e registrar ADR. Cliente com timeout, retry, fallback gracioso (R10). | #14 |
| #16 | Recurso nativo: notificações | WorkManager agendado. Permissão Android 13+. Deep link para tarefa. | #8, #10 |
| #17 | Tratamento de erros e estados | Refatorar todas as telas para sealed class `UiState { Loading, Empty, Error, Content }`. Mensagens acionáveis. | — |

**Resultado:** Versão beta completa (Checkpoint 2).

### 🔴 Verificação — semana 15 (worktree `wt-ciclo-3`, mesmo branch — segue cronológico)

| Issue | Título | Trabalho | Bloqueios |
|-------|--------|----------|-----------|
| #18 | Roteiro de testes funcionais | Tabela com fluxos principais. Executar e preencher. | #17 |
| #19 | Sessões de usabilidade (5+ usuários) | Roteiro + termo + registro de observações. | #17 |
| #20 | Registro de defeitos | Bugs viram issues com severidade. Críticos/altos vão pro Ciclo 4. | #18, #19 |

### 🟣 Ciclo 4 — semanas 16-17 (worktree `wt-ciclo-4`, branch `feature/ciclo-4`)

| Issue | Título | Trabalho | Bloqueios |
|-------|--------|----------|-----------|
| #21 | Acessibilidade | Contraste WCAG AA. 48dp toque. contentDescription. TalkBack test. | — |
| #22 | Correção defeitos + refinamento UI | Fechar bugs de #20. | #20 |
| #23 | Gerar APK release assinado | Workflow `release.yml`. Testar em 2+ dispositivos físicos. Evidências. | #17 |

### ⚫ Encerramento — semanas 18-20 (worktree `wt-encerramento`, branch `docs/encerramento`)

| Issue | Título | Trabalho | Bloqueios |
|-------|--------|----------|-----------|
| #24 | Relatório técnico final | Compor [`templates/N2-relatorio-final.md`](templates/N2-relatorio-final.md). Apêndice A.2 do norteador. | — |
| #25 | Lista verificação R1-R14 | Tabela: requisito, atendido, onde verificar. | — |
| #26 | Apresentação final N2 | Slides 20 min + 10 de arguição. Roteiro Apêndice D. Demo ensaiada. | #24 |

## Como o agente trabalha em cada issue

1. Lê a issue completa + issues relacionadas (via `gh issue view`)
2. Cria uma branch **dentro do worktree** (`git checkout -b feature/<escopo>`)
3. Implementa seguindo o plano da issue (critérios de aceite)
4. Escreve testes antes do código (TDD, quando aplicável)
5. Roda `./scripts/quality-check.sh` local
6. Escreve `RESULTADO-<issue>.md` com:
   - O que foi feito
   - Arquivos criados/alterados
   - Como testar
   - Pendências / gaps conhecidos
7. **Para.** Não commita. O orquestrador valida e commita.

## Comandos do orquestrador (você/Hermes)

```bash
# Criar worktree de um ciclo
git worktree add ../wt-concepcao -b chore/concepcao master

# Despachar agente
# (via Hermes /omh-agent-board, ver docs/templates/dispatch-agent.md)

# Após retorno do agente: validar, commitar, abrir PR
cd wt-concepcao
git checkout feature/<escopo>
./scripts/quality-check.sh
git add .
git commit -m "feat(<escopo>): <msg>"
gh pr create --fill

# Após merge: limpar
cd ..
git worktree remove wt-concepcao
git branch -d chore/concepcao
```

## Métricas de acompanhamento

| Indicador | Onde | Meta |
|-----------|------|------|
| Issues fechadas / total | gh issues | 26/26 até N2 |
| Workflows verdes | gh actions | 100% |
| Cobertura de testes Android | CI / codecov (se habilitado) | ≥60% na N2 |
| Cobertura de testes backend | pytest --cov | ≥70% na N2 |
| PRs revisados por outro membro | gh | 100% (R13) |
| Mensagens Conventional Commits | git log | 100% |
| Issues por ciclo | este doc | segue cronograma |

## Riscos e mitigações

| Risco | Mitigação |
|-------|-----------|
| Agente alucina e escreve código errado | Orchestrador sempre roda quality-check + testes antes de aceitar |
| Worktree diverge do master | Rebase antes de merge (`git rebase master feature/<x>`) |
| Conflito entre issues do mesmo ciclo | Issues do mesmo ciclo em ordem sequencial, não paralelas |
| Trabalho desperdiçado em branch errada | Validar branch com `git branch --show-current` antes de qualquer commit |
| API externa muda | Retry + fallback gracioso + versionar schema se houver (R10) |
