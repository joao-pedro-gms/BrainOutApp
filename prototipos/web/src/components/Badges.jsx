import { STATUS_TAREFA, PRIORIDADE_TAREFA, STATUS_PROJETO } from '../data/mock';

export function StatusTarefaBadge({ status }) {
  const meta = STATUS_TAREFA[status] ?? STATUS_TAREFA.aberta;
  return <span className={`badge ${meta.cor} whitespace-nowrap`}>{meta.label}</span>;
}

export function PrioridadeBadge({ prioridade }) {
  const meta = PRIORIDADE_TAREFA[prioridade] ?? PRIORIDADE_TAREFA.media;
  return <span className={`badge ${meta.cor} whitespace-nowrap`}>{meta.label}</span>;
}

export function StatusProjetoBadge({ status }) {
  const meta = STATUS_PROJETO[status] ?? STATUS_PROJETO.planejado;
  return <span className={`badge ${meta.cor} whitespace-nowrap shrink-0`}>{meta.label}</span>;
}
