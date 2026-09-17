# Plano de Implementação por Issue

Mapeamento de cada issue → comandos e entregáveis. Cronograma alinhado com o documento norteador (semanas 1-20).

## Organização

- **1 branch por issue** — `feature/<escopo>`, `fix/<escopo>`, `docs/<escopo>`, `chore/<escopo>`, `test/<escopo>`
- **1 PR por branch** — aponte a issue com `Closes #N` na descrição
- **Validação antes do PR** — `./scripts/quality-check.sh` deve passar + CI verde
- **Conventional Commits** — feat, fix, docs, chore, test, refactor, etc.

## Issues por ciclo

### Concepção (semanas 1-6)

| Issue | Título | Trabalho | Bloqueios |
|-------|--------|----------|-----------|
| #1 | Documento de projeto: escopo, personas, requisitos e regras de negócio | Preencher [`templates/N1-documento-projeto.md`](templates/N1-documento-projeto.md). Validar contra Apêndice A.1 do norteador. | — |
| #2 | Modelagem de dados e definição arquitetural | Criar [`modelo-dados.md`](modelo-dados.md) com diagrama ER (mermaid) + atualizar [`arquitetura.md`](arquitetura.md) com diagrama de camadas. Justificar stack no memorial. | — |
| #3 | Protótipo navegável (mín. 6 telas) | Criar `prototipos/telas/` com PNG export do Figma + memorial descritivo em [`prototipo.md`](prototipo.md). | #1 |
| #4 | Configurar repositório, branches, hooks e CI | Pendências: criar GitHub Project, validar hooks em 2+ máquinas. Master já protegida. | — |
| #5 | Backlog priorizado e distribuição de responsabilidades | Criar [`quadro.md`](quadro.md) com tabela (responsável, prioridade, estimativa). Sincronizar com GitHub Projects. | — |

### Ciclo 1 — semanas 7-8

| Issue | Título | Trabalho | Bloqueios |
|-------|--------|----------|-----------|
| #6 | Estrutura do app em camadas e navegação base | Criar `android/` com `app/build.gradle.kts`, `settings.gradle.kts`, gradle wrapper. Estrutura de pacotes ui/domain/data/di. NavHost com 6 telas (stubs). Hilt configurado. Tema base. | Concepção OK |
| #7 | Autenticação com 2 perfis | Telas login/cadastro. JWT storage em EncryptedSharedPreferences. Guards por perfil no NavGraph. Backend mínimo: endpoint `/auth/login`, `/auth/register`, hash bcrypt, JWT. | #6 |
| #8 | CRUD de Projetos com Room | Room entity + DAO + Repository. Telas lista/detalhe/criar/editar. ViewModel. Validações (nome obrigatório, prazo > início). Empty state. | #6 |
| #9 | Entrega N1: artefatos, demonstração e apresentação | Compor slides a partir dos artefatos já gerados em #1, #2, #3, #5. Roteiro Apêndice D. | #1, #2, #3 |

**Resultado do ciclo:** APK debug instalável com login + CRUD projetos funcionando offline. Backend mínimo com /auth.

### Ciclo 2 — semanas 11-12

| Issue | Título | Trabalho | Bloqueios |
|-------|--------|----------|-----------|
| #10 | CRUD de Tarefas | Repetir padrão de #8 para Tarefa. FK para Projeto. Decidir cascade vs. bloqueio (registrar ADR). | #8 |
| #11 | Regras de negócio RN01-RN03 | Implementar em `domain/` Android e `services/` backend. Testes cobrindo cada regra (TDD). Documentar em [`regras-negocio.md`](regras-negocio.md). | #8, #10 |
| #12 | Listagens com filtro, busca e ordenação | Search em `Room` (FTS se precisar). UI: chips de filtro + sort. Debounce na busca. | #8, #10 |
| #13 | Dashboard com visão consolidada | Tela Dashboard com cards: atrasadas, concluídas vs abertas, prazo 7d. Gráfico simples (Compose Canvas). | #8, #10 |

**Resultado:** Beta com CRUD completo + RN + dashboard.

### Ciclo 3 — semanas 13-14

| Issue | Título | Trabalho | Bloqueios |
|-------|--------|----------|-----------|
| #14 | API backend em Python com sincronização | Implementar backend completo: CRUD projetos/tarefas, JWT, sync endpoints. Documentar resolução de conflito (ADR). | #7, #8 |
| #15 | Integração com API externa | Escolher (BrasilAPI feriados ou ViaCEP) e registrar ADR. Cliente com timeout, retry, fallback gracioso (R10). | #14 |
| #16 | Recurso nativo: notificações | WorkManager agendado. Permissão Android 13+. Deep link para tarefa. | #8, #10 |
| #17 | Tratamento de erros e estados | Refatorar todas as telas para sealed class `UiState { Loading, Empty, Error, Content }`. Mensagens acionáveis. | — |

**Resultado:** Versão beta completa (Checkpoint 2).

### Verificação — semana 15

| Issue | Título | Trabalho | Bloqueios |
|-------|--------|----------|-----------|
| #18 | Roteiro de testes funcionais | Tabela com fluxos principais. Executar e preencher. | #17 |
| #19 | Sessões de usabilidade (5+ usuários) | Roteiro + termo + registro de observações. | #17 |
| #20 | Registro de defeitos | Bugs viram issues com severidade. Críticos/altos vão pro Ciclo 4. | #18, #19 |

### Ciclo 4 — semanas 16-17

| Issue | Título | Trabalho | Bloqueios |
|-------|--------|----------|-----------|
| #21 | Acessibilidade | Contraste WCAG AA. 48dp toque. contentDescription. TalkBack test. | — |
| #22 | Correção defeitos + refinamento UI | Fechar bugs de #20. | #20 |
| #23 | Gerar APK release assinado | Workflow `release.yml`. Testar em 2+ dispositivos físicos. Evidências. | #17 |

### Encerramento — semanas 18-20

| Issue | Título | Trabalho | Bloqueios |
|-------|--------|----------|-----------|
| #24 | Relatório técnico final | Compor [`templates/N2-relatorio-final.md`](templates/N2-relatorio-final.md). Apêndice A.2 do norteador. | — |
| #25 | Lista verificação R1-R14 | Tabela: requisito, atendido, onde verificar. | — |
| #26 | Apresentação final N2 | Slides 20 min + 10 de arguição. Roteiro Apêndice D. Demo ensaiada. | #24 |

## Comandos do dia a dia

```bash
# Criar branch a partir da issue
git checkout master && git pull
git checkout -b feature/<escopo-curto>   # ou fix/, docs/, chore/

# Trabalhar + commit
git add .
git commit -m "feat(<escopo>): descrição no imperativo"
# hook valida Conventional Commits + scan secrets

# Antes do PR
./scripts/quality-check.sh
git push -u origin feature/<escopo-curto>
gh pr create --fill   # editar Closes #N

# Após merge
git checkout master && git pull
git branch -d feature/<escopo-curto>
```

## Métricas de acompanhamento

| Indicador | Onde | Meta |
|-----------|------|------|
| Issues fechadas / total | gh issues | 26/26 até N2 |
| Workflows verdes | gh actions | 100% |
| Cobertura de testes Android | CI | ≥60% na N2 |
| Cobertura de testes backend | `uv run pytest --cov` | ≥70% na N2 |
| PRs revisados por outro membro | gh | 100% (R13) |
| Mensagens Conventional Commits | `git log` | 100% |

## Riscos e mitigações

| Risco | Mitigação |
|-------|-----------|
| Código com bug | Testes cobrindo regras + validação em CI antes de merge |
| Branch diverge do master | Rebase antes de merge (`git rebase master feature/<x>`) |
| Conflito entre issues | Resolver em ordem cronológica; 1 issue ativa por branch |
| Trabalho em branch errada | Validar `git branch --show-current` antes de qualquer commit |
| API externa muda | Retry + fallback gracioso + versionar schema se houver (R10) |
