# ADR 0004 — Integração externa (a decidir na issue #15)

- **Status:** Pendente
- **Data:** a definir
- **Issue:** [#15](../../issues/15)

## Candidatas

| API | Custo | Pertinência ao domínio | Risco |
|-----|-------|------------------------|-------|
| [BrasilAPI — feriados](https://brasilapi.com.br/api/feriados/v1/{ano}) | $0 | Alta: alerta de prazos em feriados | Baixo |
| [ViaCEP](https://viacep.com.br/) | $0 | Média: endereço de cliente/obra | Baixo |
| [HG Brasil Weather](https://hgbrasil.com/status/weather) | $0 (com limite) | Baixa para o domínio | Médio |

## Decisão proposta

**BrasilAPI — feriados** como integração primária. Motivo: pertinência direta ao domínio de prazos. Fallback gracioso (R10) quando offline.

## A confirmar na issue

- Endpoint exato
- Cache local (30 dias, via Room)
- Estratégia de retry (3 tentativas, backoff exponencial)
- Mensagem ao usuário quando offline
