---
name: "Bug"
about: Defeito com severidade, reprodução e rastreabilidade (Seção 6.3)
title: "[BUG] "
labels: ["bug"]
assignees: []
---

## Severidade
<!-- Seção 6.3 — classificação obrigatória -->
- [ ] Crítica (impede fluxo principal)
- [ ] Alta (funcionalidade quebrada, com alternativa)
- [ ] Média (comportamento incorreto não bloqueante)
- [ ] Baixa (cosmético ou de conveniência)

## Ciclo / impacto
- Descoberto em: <!-- ciclo-N | verificação | N1 | N2 | uso -->
- Fluxo principal afetado: <!-- login | projetos | tarefas | sync | notificações | … -->
- Bloqueia entrega do ciclo? <!-- sim / não -->

## Descrição do defeito
**Observado:**  
**Esperado:**  

## Passos para reproduzir
1. 
2. 
3. 

## Ambiente
- Dispositivo / emulador:
- Android:
- Versão do app / commit:
- Build: <!-- debug | release -->
- Rede: <!-- online | offline | instável -->

## Evidências
<!-- Screenshots, logcat, HAR, vídeo curto -->

```
# cole trechos relevantes de log aqui
```

## Hipótese / área suspeita
<!-- Camada (UI / domain / Room / API), arquivo ou issue relacionada -->

## Rastreabilidade
- Identificado em: <!-- teste funcional nº / sessão de usabilidade / uso -->
- Teste relacionado:
- Requisito(s): <!-- R* -->
- Issue de origem (se regressão): #

## Critérios de correção
- [ ] Passos acima passam no build corrigido
- [ ] Sem regressão no fluxo principal vizinho
- [ ] Tratamento de erro/estado adequado (R10), se aplicável
- [ ] Registro no relatório de testes / changelog do ciclo
