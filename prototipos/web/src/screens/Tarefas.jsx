import { useMemo, useState } from 'react';
import { Link } from 'react-router-dom';
import { useProjetos, useTarefas } from '../lib/store';
import { StatusTarefaBadge, PrioridadeBadge } from '../components/Badges';
import { Icon } from '../lib/icons.jsx';
import { USUARIOS } from '../data/mock';
import { formatarData, tarefaAtrasada } from '../lib/regras';

export default function Tarefas({ perfil }) {
  const { projetos } = useProjetos();
  const { tarefas } = useTarefas();
  const isGerente = perfil === 'u1';

  const [filtros, setFiltros] = useState({
    texto: '',
    status: 'todos',
    prioridade: 'todas',
    responsavel: 'todos',
    projeto: 'todos',
  });

  const filtradas = useMemo(() => {
    return tarefas.filter((t) => {
      if (filtros.texto && !t.titulo.toLowerCase().includes(filtros.texto.toLowerCase())) return false;
      if (filtros.status !== 'todos' && t.status !== filtros.status) return false;
      if (filtros.prioridade !== 'todas' && t.prioridade !== filtros.prioridade) return false;
      if (filtros.responsavel !== 'todos' && t.responsavelId !== filtros.responsavel) return false;
      if (filtros.projeto !== 'todos' && t.projetoId !== filtros.projeto) return false;
      return true;
    });
  }, [tarefas, filtros]);

  return (
    <div>
      <div className="flex items-center justify-between gap-3 mb-4">
        <div className="min-w-0">
          <h1 className="text-2xl font-bold text-slate-900">Tarefas</h1>
          <p className="text-slate-600 text-sm">
            {filtradas.length} tarefa(s)
            {!isGerente && ' · você só vê as tarefas atribuídas a você como Colaborador.'}
          </p>
        </div>
        {isGerente && (
          <Link to="/tarefas/nova" className="btn-primary whitespace-nowrap shrink-0">
            <Icon name="plus" size="sm" />
            <span className="hidden sm:inline">Nova tarefa</span>
            <span className="sm:hidden">Nova</span>
          </Link>
        )}
      </div>

      <div className="card p-4 mb-4 grid sm:grid-cols-5 gap-3">
        <div className="sm:col-span-2">
          <label className="label">Buscar</label>
          <input className="input" placeholder="Título da tarefa…"
                 value={filtros.texto} onChange={(e) => setFiltros({ ...filtros, texto: e.target.value })} />
        </div>
        <div>
          <label className="label">Status</label>
          <select className="input" value={filtros.status} onChange={(e) => setFiltros({ ...filtros, status: e.target.value })}>
            <option value="todos">Todos</option>
            <option value="aberta">Aberta</option>
            <option value="em_andamento">Em andamento</option>
            <option value="concluida">Concluída</option>
            <option value="cancelada">Cancelada</option>
          </select>
        </div>
        <div>
          <label className="label">Prioridade</label>
          <select className="input" value={filtros.prioridade} onChange={(e) => setFiltros({ ...filtros, prioridade: e.target.value })}>
            <option value="todas">Todas</option>
            <option value="alta">Alta</option>
            <option value="media">Média</option>
            <option value="baixa">Baixa</option>
          </select>
        </div>
        <div>
          <label className="label">Responsável</label>
          <select className="input" value={filtros.responsavel} onChange={(e) => setFiltros({ ...filtros, responsavel: e.target.value })}>
            <option value="todos">Todos</option>
            {USUARIOS.map((u) => <option key={u.id} value={u.id}>{u.nome}</option>)}
          </select>
        </div>
      </div>

      {filtradas.length === 0 ? (
        <div className="card p-10 text-center text-slate-500">
          <div className="mx-auto mb-3 w-12 h-12 rounded-2xl bg-slate-100 text-slate-400 grid place-items-center">
            <Icon name="searchX" size="xl" strokeWidth={1.5} />
          </div>
          <p>Nenhuma tarefa bate com os filtros.</p>
        </div>
      ) : (
        <ul className="space-y-2">
          {filtradas.map((t) => {
            const projeto = projetos.find((p) => p.id === t.projetoId);
            const responsavel = USUARIOS.find((u) => u.id === t.responsavelId);
            const atrasada = tarefaAtrasada(t);
            return (
              <li key={t.id} className="card p-4">
                <div className="flex items-center gap-2 mb-1 flex-wrap">
                  <Link to={`/tarefas/${t.id}/editar`} className="font-medium text-slate-900 hover:text-brand-700">{t.titulo}</Link>
                  <div className="flex gap-1 flex-wrap">
                    <StatusTarefaBadge status={t.status} />
                    <PrioridadeBadge prioridade={t.prioridade} />
                    {atrasada && (
                      <span className="badge bg-red-100 text-red-700 inline-flex items-center gap-1">
                        <Icon name="alert" size="xs" /> Atrasada
                      </span>
                    )}
                  </div>
                </div>
                <div className="text-xs text-slate-500 flex flex-wrap gap-x-3 gap-y-1">
                  <span className="inline-flex items-center gap-1">
                    <Icon name="folderOpen" size="xs" />
                    {projeto?.nome ?? '—'}
                  </span>
                  <span className="inline-flex items-center gap-1">
                    <Icon name="userSmall" size="xs" />
                    {responsavel?.nome ?? '—'}
                  </span>
                  <span className="inline-flex items-center gap-1">
                    <Icon name="calendar" size="xs" />
                    {formatarData(t.prazo)}
                  </span>
                </div>
              </li>
            );
          })}
        </ul>
      )}
    </div>
  );
}
