---
title: "[FEATURE] Integração com API externa de apoio ao domínio"
labels: [feature, android, backend, ciclo-3, "prioridade: média", R7, R10]
---

## Requisito(s)
- [x] R7 — Consumo de ≥ 1 API externa pertinente ao domínio

## História de usuário
**Como** Gerente  
**quero** enriquecer o projeto com dado externo útil (ex.: feriados/prazos, CEP/endereço do cliente, ou cotação de esforço)  
**para** planejar com contexto real.

## Escopo
### Inclui
- Escolher **uma** API pública estável e documentar a escolha no memorial
- Cliente HTTP (app e/ou backend como BFF)
- Exibir o dado em pelo menos uma tela (ex.: aviso de feriado no prazo da tarefa)
- Cache local do resultado; timeout e fallback se a API falhar
### Não inclui
- Dependência crítica: o app deve funcionar se a API externa cair

## Critérios de aceite
- [ ] Chamada real em runtime (não mock-only na demo N2)
- [ ] Falha da API não quebra fluxo principal — R10
- [ ] Chave (se houver) só em `.env` / `local.properties`
- [ ] Justificativa de pertinência ao domínio no doc

## Plano de teste
| Nº | Cenário | Esperado |
|----|---------|----------|
| 1 | API OK | Dado aparece na UI |
| 2 | API 500 / timeout | Fallback + mensagem |
