# Regras de Negócio — RN01-RN03

Implementadas em `domain/` no Android (camada de domínio) **e** em `app/services/` no backend (defesa em profundidade). Cliente bloqueia antes da chamada de rede; servidor revalida.

Cobertura por testes é critério de aceite da issue #11.

## RN01 — Conclusão de tarefa com dependências

> Uma tarefa só pode ser marcada como concluída se todas as subtarefas/dependências estiverem concluídas.

**Implementação:**

- Backend: em `app/services/tarefas.py::concluir()`, carregar dependências e validar status.
- Android: `domain/usecase/ConcluirTarefaUseCase.invoke(tarefaId)` faz o mesmo check antes de chamar o repo.

**Mensagem de erro (R10):** `"Não é possível concluir: a tarefa '<título>' ainda está pendente."`

**Teste cobre:** tarefa com dependência aberta → falha; todas concluídas → sucesso; tarefa sem dependências → sempre pode concluir.

## RN02 — Conclusão de projeto

> Projeto não pode ser concluído enquanto houver tarefa aberta.

**Implementação:**

- Backend: `app/services/projetos.py::concluir()` consulta `COUNT(tarefas WHERE status IN ('aberta','em_and'))`.
- Android: `ConcluirProjetoUseCase.invoke()` faz a mesma checagem.

**Mensagem:** `"Projeto tem <N> tarefa(s) em aberto. Conclua ou cancele antes."`

**Implementação ajustada:** backend usa `status IN ('aberta', 'em_andamento')` (sem abreviação).

**Teste cobre:** projeto com tarefa aberta → falha; sem tarefas abertas → sucesso.

## RN03 — Prazo de tarefa ≤ prazo do projeto

> Prazo de tarefa não pode ultrapassar o prazo do projeto.

**Implementação:**

- Backend: validator em `app/schemas/tarefa.py::TarefaCreate.prazo`.
- Android: em `CriarTarefaUseCase` e `EditarTarefaUseCase`.

**Mensagem:** `"Prazo da tarefa (DD/MM) ultrapassa o prazo do projeto (DD/MM)."`

**Teste cobre:** tarefa com prazo > projeto → falha na criação/edição; tarefa com prazo ≤ projeto → ok.

## Validação dupla

Por que validar no servidor **e** no cliente:

- **Cliente:** resposta instantânea, sem desperdício de rede, bom UX offline.
- **Servidor:** única fonte da verdade; protege de cliente modificado/fora de data.
- Mensagens idênticas nos dois lados — **código da mensagem vive no contrato da API** (`/errors/code` em #14); cada lado mapeia código → texto traduzido (strings.xml no Android, .po no backend). Isso evita drift entre as implementações.
