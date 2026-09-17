# Protótipo navegável — BrainOutApp

> Aplicação web estática para validar o fluxo de telas **antes** de codificar o app Android (issue #6) e o backend (issue #14).

## Stack

- **React 18** + **Vite 5** — build rápido, sem configuração
- **Tailwind CSS 3** — design system consistente
- **React Router 6** — 6 telas, navegação real
- **localStorage** — persiste estado entre reloads (substitui o Room do app real)

## Telas (R1)

1. **Login** — escolha de perfil (Gerente / Colaborador)
2. **Projetos** — lista, criar, editar, excluir
3. **Detalhe do projeto** — info + lista de tarefas
4. **Tarefas** — lista global com filtros (status, prioridade, responsável)
5. **Formulário de tarefa** — criar / editar (com validação de prazo ≤ projeto, RN03)
6. **Dashboard** — cards + gráfico SVG de barras

## Comandos

```bash
cd prototipos/web
npm install
npm run dev          # http://localhost:5173
npm run build        # gera dist/
npm run preview      # serve dist/ em http://localhost:4173
```

## Deploy

Automático via GitHub Actions: `.github/workflows/pages.yml` builda em `prototipos/web/` e publica em **https://joao-pedro-gms.github.io/BrainOutApp/** (custo zero, repo público).

## Limitações (intencionais)

- **Sem backend**: dados ficam no localStorage do navegador
- **Sem autenticação real**: perfil é um toggle (não há senha)
- **R10 (tratamento de erros)**: estados de UI básicos (loading vazio), sem retry elaborado
- **R7 (integração externa)**: não implementada — BrasilAPI entra no Ciclo 3
- **R8 (notificações)**: não implementada — entra no Ciclo 3

Esses pontos viram nas issues #14, #15, #16 e #17.
