# Protótipo de Telas

Issue: [#3](../../issues/3). Memorial descritivo e artefatos visuais.

## Telas mínimas (R1 — mínimo 6)

| # | Tela | Componente Compose | Fluxo principal |
|---|------|-------------------|-----------------|
| 1 | Login | `LoginScreen` | autentica Gerente/Colaborador |
| 2 | Lista de Projetos | `ProjetosScreen` | entrada após login (Gerente) ou visão filtrada (Colaborador) |
| 3 | Detalhe do Projeto | `ProjetoDetalheScreen` | tarefas, membros, status |
| 4 | Lista de Tarefas | `TarefasScreen` | tarefas do usuário, filtros |
| 5 | Formulário de Tarefa | `TarefaFormScreen` | criar/editar tarefa (RN03 validada) |
| 6 | Dashboard | `DashboardScreen` | indicadores consolidados (R9) |

## Decisões de usabilidade (R11)

- **Material 3** como base — design system oficial Android, com tipografia dinâmica (acessível)
- **Tema claro e escuro** com persistência local
- **Contraste:** tokens M3 já cumprem WCAG AA
- **Áreas de toque:** mínimo 48dp (constraint do Material 3)
- **Rótulos TalkBack:** `contentDescription` em ícones e ações; foco navegável nos principais

## Pendente (a fechar no issue #3)

- [ ] PNG export do Figma (ou similar) em `prototipos/telas/`
- [ ] Gravar navegação entre telas (Loom curto ou GIF)
- [ ] Validar com 1 usuário externo (Checkpoint 1: 11/09)
