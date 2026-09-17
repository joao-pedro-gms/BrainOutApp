/**
 * Regras de negócio RN01-RN03 implementadas em JS para o protótipo.
 * No app real (issue #11) a regra também vive em Kotlin + Python
 * (defesa em profundidade). Aqui só validamos a UI.
 */

import { HOJE } from '../data/mock';

/** RN03: prazo da tarefa não pode ultrapassar o prazo do projeto */
export function validarPrazoTarefa(tarefaPrazo, projetoPrazo) {
  if (!tarefaPrazo || !projetoPrazo) return null;
  return tarefaPrazo <= projetoPrazo
    ? null
    : `Prazo da tarefa (${formatarData(tarefaPrazo)}) ultrapassa o prazo do projeto (${formatarData(projetoPrazo)}).`;
}

/** RN02: projeto não pode ser concluído com tarefas abertas */
export function validarConclusaoProjeto(projeto, tarefasDoProjeto) {
  const abertas = tarefasDoProjeto.filter(
    (t) => t.status === 'aberta' || t.status === 'em_andamento'
  );
  if (abertas.length === 0) return null;
  return `Projeto tem ${abertas.length} tarefa(s) em aberto. Conclua ou cancele antes.`;
}

/** RN01: tarefa só pode ser concluída se não houver dependência aberta.
 *  No protótipo não modelamos dependências; placeholder. */
export function validarConclusaoTarefa(/* tarefa, dependencias */) {
  return null;
}

/** Validação de data: prazo > dataInicio */
export function validarDatasProjeto(dataInicio, prazo) {
  if (!dataInicio || !prazo) return 'Preencha data de início e prazo.';
  return prazo >= dataInicio
    ? null
    : 'Prazo deve ser igual ou posterior à data de início.';
}

export function tarefaAtrasada(tarefa) {
  return tarefa.status !== 'concluida'
    && tarefa.status !== 'cancelada'
    && tarefa.prazo < HOJE;
}

export function formatarData(iso) {
  if (!iso) return '—';
  const [ano, mes, dia] = iso.split('-');
  return `${dia}/${mes}/${ano}`;
}

export function diasAte(iso) {
  if (!iso) return null;
  const hoje = new Date(HOJE + 'T00:00:00');
  const alvo = new Date(iso + 'T00:00:00');
  const ms = alvo.getTime() - hoje.getTime();
  return Math.round(ms / 86400000);
}
