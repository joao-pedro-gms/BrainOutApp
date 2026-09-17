# Wireframes — BrainOutApp

Wireframes estáticos de **baixa fidelidade** (estilo Balsamiq) das 6 telas do protótipo.

## Convenção

- **Formato:** SVG vetorial, 1 arquivo por tela
- **Tamanho:** 393×852 (iPhone 14 Pro)
- **Estilo:** formas geométricas, retângulos cinzas com borda, placeholders `[texto]` em monoespaçado
- **Sem interatividade** — são imagens estáticas para validar escopo e layout antes do código real
- **Sem build** — servidos direto pelo GitHub Pages (arquivos `.svg` + `index.html`)

## Telas

| # | Arquivo | Conteúdo |
|---|---------|----------|
| 1 | [01-login.svg](01-login.svg) | Login com seleção de perfil (Gerente / Colaborador) — cobre R2 |
| 2 | [02-projetos.svg](02-projetos.svg) | Lista de projetos com cards + botão "Novo projeto" — cobre R3, R5 |
| 3 | [03-projeto-detalhe.svg](03-projeto-detalhe.svg) | Detalhe do projeto + lista de tarefas — cobre R3 |
| 4 | [04-tarefas.svg](04-tarefas.svg) | Lista global de tarefas com filtros — cobre R9 |
| 5 | [05-tarefa-form.svg](05-tarefa-form.svg) | Form de criar/editar tarefa com cenário de erro RN03 — cobre R3, R4 |
| 6 | [06-dashboard.svg](06-dashboard.svg) | 4 KPIs + 2 gráficos + próximas tarefas — cobre R9 |

## Visualização

- **[index.html](index.html)** — galeria de todas as telas
- **GitHub Pages:** https://joao-pedro-gms.github.io/BrainOutApp/

## Próximo passo

Após validar layout/escopo nos wireframes, partir para o **app real**:

- **#6** Estrutura Android (Kotlin + Compose)
- **#14** Backend Python (FastAPI + SQLAlchemy)
- **#7** Autenticação com 2 perfis

Até lá, wireframes são a referência visual.
