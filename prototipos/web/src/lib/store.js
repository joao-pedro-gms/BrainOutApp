import { useEffect, useState, useCallback } from 'react';
import { PROJETOS_INICIAIS, TAREFAS_INICIAIS } from '../data/mock';

const KEY_PROJETOS = 'brainoutapp:projetos:v1';
const KEY_TAREFAS  = 'brainoutapp:tarefas:v1';

/**
 * Store simples baseado em localStorage. Não usa Context — expõe
 * funções e um pequeno hook de assinatura. Suficiente para o protótipo.
 */

function load(key, inicial) {
  try {
    const raw = localStorage.getItem(key);
    if (raw == null) return inicial;
    return JSON.parse(raw);
  } catch {
    return inicial;
  }
}

function save(key, valor) {
  try {
    localStorage.setItem(key, JSON.stringify(valor));
  } catch {
    /* localStorage indisponível — ignora */
  }
}

const ouvintes = new Set();

function notificar() {
  for (const cb of ouvintes) cb();
}

export function getProjetos()   { return load(KEY_PROJETOS, PROJETOS_INICIAIS); }
export function getTarefas()    { return load(KEY_TAREFAS,  TAREFAS_INICIAIS);  }

export function setProjetos(lista) { save(KEY_PROJETOS, lista); notificar(); }
export function setTarefas(lista)  { save(KEY_TAREFAS,  lista); notificar(); }

export function resetarDados() {
  localStorage.removeItem(KEY_PROJETOS);
  localStorage.removeItem(KEY_TAREFAS);
  notificar();
}

/** Hook que re-renderiza quando qualquer setter for chamado. */
export function useStore() {
  const [, setTick] = useState(0);
  useEffect(() => {
    const cb = () => setTick((t) => t + 1);
    ouvintes.add(cb);
    return () => ouvintes.delete(cb);
  }, []);
  return {
    projetos: getProjetos(),
    tarefas:  getTarefas(),
    setProjetos,
    setTarefas,
    resetarDados,
  };
}

export function useProjetos() {
  const { projetos, setProjetos } = useStore();

  const criar = useCallback((dados) => {
    const novo = { id: 'p' + Date.now(), criadoPor: 'u1', status: 'em_andamento', ...dados };
    setProjetos([novo, ...projetos]);
    return novo;
  }, [projetos, setProjetos]);

  const atualizar = useCallback((id, patch) => {
    setProjetos(projetos.map((p) => (p.id === id ? { ...p, ...patch } : p)));
  }, [projetos, setProjetos]);

  const remover = useCallback((id) => {
    setProjetos(projetos.filter((p) => p.id !== id));
  }, [projetos, setProjetos]);

  return { projetos, criar, atualizar, remover };
}

export function useTarefas() {
  const { tarefas, setTarefas } = useStore();

  const criar = useCallback((dados) => {
    const nova = { id: 't' + Date.now(), status: 'aberta', prioridade: 'media', ...dados };
    setTarefas([nova, ...tarefas]);
    return nova;
  }, [tarefas, setTarefas]);

  const atualizar = useCallback((id, patch) => {
    setTarefas(tarefas.map((t) => (t.id === id ? { ...t, ...patch } : t)));
  }, [tarefas, setTarefas]);

  const remover = useCallback((id) => {
    setTarefas(tarefas.filter((t) => t.id !== id));
  }, [tarefas, setTarefas]);

  return { tarefas, criar, atualizar, remover };
}
