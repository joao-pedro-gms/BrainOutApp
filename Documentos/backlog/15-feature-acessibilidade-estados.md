---
title: "[FEATURE] Acessibilidade, heurísticas de UI e estados de erro completos"
labels: [feature, android, ciclo-4, "prioridade: alta", R10, R11, accessibility]
---

## Requisito(s)
- [x] R10 — Tratamento explícito de falhas, loading e vazios
- [x] R11 — Usabilidade e acessibilidade

## História de usuário
**Como** usuário com leitor de tela ou em rede instável  
**quero** entender o estado do app e operar os controles  
**para** concluir meu fluxo sem barreiras.

## Escopo
### Inclui
- `contentDescription` em ícones acionáveis; foco em ordem lógica
- Contraste adequado no theme; áreas de toque ≥ 48dp
- Padronizar componentes de Empty / Error / Loading
- Revisão heurística (Nielsen) das telas principais — checklist no PR
### Não inclui
- Tema claro/escuro completo (desejável, Seção 5.1)

## Critérios de aceite
- [ ] TalkBack percorre login → lista → detalhe sem beco sem saída
- [ ] Todas as listagens principais têm empty/error/loading
- [ ] Nenhum fluxo principal “trava” em spinner infinito
- [ ] Achados da usabilidade priorizados entram como bugs linkados

## Plano de teste
| Nº | Cenário | Esperado |
|----|---------|----------|
| 1 | TalkBack no login | Labels compreensíveis |
| 2 | API off na sync | Error state com retry |
