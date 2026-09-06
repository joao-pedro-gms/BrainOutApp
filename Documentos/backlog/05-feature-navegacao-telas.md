---
title: "[FEATURE] Navegação estruturada com no mínimo 6 telas Compose"
labels: [feature, android, ciclo-1, "prioridade: alta", R1]
---

## Requisito(s)
- [x] R1 — Mínimo 6 telas funcionais distintas com fluxo coerente

## História de usuário
**Como** Gerente ou Colaborador  
**quero** navegar entre login, projetos, tarefas e dashboard  
**para** cumprir meu fluxo diário sem telas mortas.

## Escopo
### Inclui
- Navigation Compose (ou equivalente) com grafo único
- Telas placeholder ou parcialmente funcionais cobrindo o protótipo:
  Login, ListaProjetos, DetalheProjeto, FormProjeto, Lista/DetalheTarefa, Dashboard
- Deep link interno básico entre detalhe ← lista
- App bar / bottom bar coerente com o perfil (mesmo que o perfil ainda seja mock)
### Não inclui
- Auth real (issue auth)
- Persistência

## Critérios de aceite
- [ ] ≥ 6 rotas distintas alcançáveis na demo
- [ ] Botão voltar do sistema comporta-se corretamente no grafo
- [ ] Nenhuma tela “em branco” sem título ou estado vazio — R10 mínimo
- [ ] Nomes de rotas e arquivos consistentes com a arquitetura — R12

## Plano de teste
| Nº | Cenário | Esperado |
|----|---------|----------|
| 1 | Percorrer o fluxo Gerente no protótipo espelhado | Todas as 6+ telas abrem |
| 2 | Recriar activity (rotação) | Estado de navegação razoável |
