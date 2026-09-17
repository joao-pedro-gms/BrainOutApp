import { useState } from 'react';
import { Link } from 'react-router-dom';
import { useProjetos, useTarefas } from '../lib/store';
import { StatusProjetoBadge } from '../components/Badges';
import { Icon } from '../lib/icons.jsx';
import { formatarData, validarDatasProjeto } from '../lib/regras';

export default function Projetos({ perfil }) {
  const { projetos, criar, remover } = useProjetos();
  const { tarefas } = useTarefas();
  const isGerente = perfil === 'u1';

  const [mostrarForm, setMostrarForm] = useState(false);
  const [form, setForm] = useState({ nome: '', descricao: '', dataInicio: '', prazo: '' });
  const [erro, setErro] = useState(null);

  function handleCriar(e) {
    e.preventDefault();
    const erroDatas = validarDatasProjeto(form.dataInicio, form.prazo);
    if (erroDatas) { setErro(erroDatas); return; }
    if (!form.nome.trim()) { setErro('Nome do projeto é obrigatório.'); return; }
    criar(form);
    setForm({ nome: '', descricao: '', dataInicio: '', prazo: '' });
    setErro(null);
    setMostrarForm(false);
  }

  function handleRemover(id) {
    if (confirm('Excluir este projeto? (no app real, RN02 bloqueia com tarefas abertas)')) {
      remover(id);
    }
  }

  return (
    <div>
      <div className="flex items-center justify-between gap-3 mb-4">
        <div className="min-w-0">
          <h1 className="text-2xl font-bold text-slate-900">Projetos</h1>
          <p className="text-slate-600 text-sm">
            {isGerente
              ? 'Gerencie todos os projetos da equipe.'
              : 'Visualização somente leitura — apenas Gerente pode criar/editar.'}
          </p>
        </div>
        {isGerente && (
          <button
            onClick={() => setMostrarForm((v) => !v)}
            className="btn-primary whitespace-nowrap shrink-0"
          >
            <Icon name="plus" size="sm" />
            <span className="hidden sm:inline">{mostrarForm ? 'Cancelar' : 'Novo projeto'}</span>
            <span className="sm:hidden">{mostrarForm ? 'Cancelar' : 'Novo'}</span>
          </button>
        )}
      </div>

      {mostrarForm && (
        <form onSubmit={handleCriar} className="card p-5 mb-6 space-y-3">
          {erro && <div className="bg-red-50 text-red-700 text-sm rounded-lg p-3">{erro}</div>}
          <div>
            <label className="label">Nome *</label>
            <input className="input" value={form.nome}
                   onChange={(e) => setForm({ ...form, nome: e.target.value })}
                   placeholder="Ex.: Migração para Postgres" />
          </div>
          <div>
            <label className="label">Descrição</label>
            <textarea className="input" rows="2" value={form.descricao}
                      onChange={(e) => setForm({ ...form, descricao: e.target.value })} />
          </div>
          <div className="grid grid-cols-2 gap-3">
            <div>
              <label className="label">Data de início *</label>
              <input type="date" className="input" value={form.dataInicio}
                     onChange={(e) => setForm({ ...form, dataInicio: e.target.value })} />
            </div>
            <div>
              <label className="label">Prazo *</label>
              <input type="date" className="input" value={form.prazo}
                     onChange={(e) => setForm({ ...form, prazo: e.target.value })} />
            </div>
          </div>
          <div className="flex justify-end gap-2">
            <button type="button" className="btn-secondary" onClick={() => setMostrarForm(false)}>Cancelar</button>
            <button type="submit" className="btn-primary">Criar projeto</button>
          </div>
        </form>
      )}

      {projetos.length === 0 ? (
        <EmptyState mensagem="Nenhum projeto ainda. Crie o primeiro acima." />
      ) : (
        <div className="grid gap-4 sm:grid-cols-2">
          {projetos.map((p) => {
            const tarefasDoProjeto = tarefas.filter((t) => t.projetoId === p.id);
            const abertas = tarefasDoProjeto.filter((t) => t.status !== 'concluida' && t.status !== 'cancelada').length;
            return (
              <div key={p.id} className="card p-5 flex flex-col">
                <div className="flex items-start justify-between gap-2 mb-2">
                  <Link to={`/projetos/${p.id}`} className="font-semibold text-slate-900 hover:text-brand-700 min-w-0 flex-1">
                    {p.nome}
                  </Link>
                  <StatusProjetoBadge status={p.status} />
                </div>
                {p.descricao && (
                  <p className="text-sm text-slate-600 mb-3 line-clamp-2">{p.descricao}</p>
                )}
                <div className="mt-auto flex items-center justify-between text-xs text-slate-500">
                  <span className="inline-flex items-center gap-1">
                    <Icon name="calendar" size="xs" />
                    Início: {formatarData(p.dataInicio)}
                  </span>
                  <span className="inline-flex items-center gap-1">
                    <Icon name="calendarClock" size="xs" />
                    Prazo: <strong className="text-slate-700">{formatarData(p.prazo)}</strong>
                  </span>
                </div>
                <div className="mt-2 text-xs text-slate-500 flex items-center gap-3">
                  <span className="inline-flex items-center gap-1">
                    <Icon name="clipboardList" size="xs" />
                    {tarefasDoProjeto.length} tarefa(s)
                  </span>
                  <span className="inline-flex items-center gap-1 text-amber-600">
                    <Icon name="play" size="xs" />
                    {abertas} em aberto
                  </span>
                </div>
                {isGerente && (
                  <div className="mt-3 pt-3 border-t border-slate-100 flex justify-end">
                    <button onClick={() => handleRemover(p.id)} className="inline-flex items-center gap-1 text-xs text-red-600 hover:text-red-700">
                      <Icon name="trash" size="xs" />
                      Excluir
                    </button>
                  </div>
                )}
              </div>
            );
          })}
        </div>
      )}
    </div>
  );
}

function EmptyState({ mensagem }) {
  return (
    <div className="card p-10 text-center text-slate-500">
      <div className="mx-auto mb-3 w-12 h-12 rounded-2xl bg-slate-100 text-slate-400 grid place-items-center">
        <Icon name="folderOpen" size="xl" strokeWidth={1.5} />
      </div>
      <p>{mensagem}</p>
    </div>
  );
}
