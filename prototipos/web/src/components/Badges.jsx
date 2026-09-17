import { STATUS_TAREFA, PRIORIDADE_TAREFA, STATUS_PROJETO } from '../data/mock';
import { Icon } from '../lib/icons.jsx';

/**
 * Mapa de icone por status/prioridade. Adicionar aqui (e ao icons.jsx) para cada novo tipo.
 * Mantemos os labels em mock.js para que o data continue com a string exibida.
 */
const ICON_POR_STATUS_TAREFA = {
  aberta:       'circle',
  em_andamento: 'play',
  concluida:    'checkAll',     // check duplo para indicar "completamente pronto"
  cancelada:    'circleSlash',
};

const ICON_POR_PRIORIDADE = {
  baixa: 'chevronRight',  // seta pequena, neutra
  media: 'flag',
  alta:  'alert',
};

const ICON_POR_STATUS_PROJETO = {
  planejado:    'inbox',
  em_andamento: 'play',
  concluido:    'checkAll',
  cancelado:    'circleSlash',
};

export function StatusTarefaBadge({ status, withIcon = true }) {
  const meta = STATUS_TAREFA[status] ?? STATUS_TAREFA.aberta;
  return (
    <span className={`badge ${meta.cor} whitespace-nowrap inline-flex items-center gap-1`}>
      {withIcon && <Icon name={ICON_POR_STATUS_TAREFA[status] ?? 'circle'} size="xs" />}
      <span>{meta.label}</span>
    </span>
  );
}

export function PrioridadeBadge({ prioridade, withIcon = true }) {
  const meta = PRIORIDADE_TAREFA[prioridade] ?? PRIORIDADE_TAREFA.media;
  return (
    <span className={`badge ${meta.cor} whitespace-nowrap inline-flex items-center gap-1`}>
      {withIcon && <Icon name={ICON_POR_PRIORIDADE[prioridade] ?? 'flag'} size="xs" />}
      <span>{meta.label}</span>
    </span>
  );
}

export function StatusProjetoBadge({ status, withIcon = true }) {
  const meta = STATUS_PROJETO[status] ?? STATUS_PROJETO.planejado;
  return (
    <span className={`badge ${meta.cor} whitespace-nowrap shrink-0 inline-flex items-center gap-1`}>
      {withIcon && <Icon name={ICON_POR_STATUS_PROJETO[status] ?? 'inbox'} size="xs" />}
      <span>{meta.label}</span>
    </span>
  );
}
