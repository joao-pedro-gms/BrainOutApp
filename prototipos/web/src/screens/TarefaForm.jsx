import { useState, useEffect } from 'react';
import { useNavigate, useParams, useSearchParams } from 'react-router-dom';
import { useProjetos, useTarefas } from '../lib/store';
import { USUARIOS } from '../data/mock';
import { validarPrazoTarefa } from '../lib/regras';

export default function TarefaForm({ perfil }) {
  const { id } = useParams();
  const navigate = useNavigate();
  const [search] = useSearchParams();
  const { projetos } = useProjetos();
  const { tarefas, criar, atualizar } = useTarefas();
  const isGerente = perfil === 'u1';

  const editando = id ? tarefas.find((t) => t.id === id) : null;

  const [form, setForm] = useState(() => editando
    ? { ...editando }
    : {
        titulo: '',
        descricao: '',
        projetoId: search.get('projeto') || projetos[0]?.id || '',
        responsavelId: 'u1',
        prioridade: 'media',
        status: 'aberta',
        prazo: '',
      }
  );
  const [erro, setErro] = useState(null);

  useEffect(() => {
    if (editando) setForm({ ...editando });
  }, [editando]);

  const projeto = projetos.find((p) => p.id === form.projetoId);

  function handleSalvar(e) {
    e.preventDefault();
    if (!form.titulo.trim()) { setErro('Título é obrigatório.'); return; }
    if (!form.prazo) { setErro('Prazo é obrigatório.'); return; }
    if (!form.projetoId) { setErro('Selecione um projeto.'); return; }

    if (projeto) {
      const erroPrazo = validarPrazoTarefa(form.prazo, projeto.prazo);
      if (erroPrazo) { setErro(erroPrazo); return; }
    }

    if (editando) {
      atualizar(id, form);
    } else {
      criar(form);
    }
    navigate(`/projetos/${form.projetoId}`);
  }

  if (!isGerente && !editando) {
    return (
      <div className="card p-8 text-center">
        <p className="text-5xl mb-3">🔒</p>
        <p className="text-slate-600">Colaborador não cria tarefas (apenas Gerente).</p>
      </div>
    );
  }

  return (
    <div>
      <button onClick={() => navigate(-1)} className="text-sm text-brand-600 hover:underline mb-3 inline-block">← Voltar</button>
      <h1 className="text-2xl font-bold text-slate-900 mb-4">
        {editando ? 'Editar tarefa' : 'Nova tarefa'}
      </h1>

      <form onSubmit={handleSalvar} className="card p-6 space-y-4">
        {erro && <div className="bg-red-50 text-red-700 text-sm rounded-lg p-3">{erro}</div>}

        <div>
          <label className="label">Título *</label>
          <input className="input" value={form.titulo}
                 onChange={(e) => setForm({ ...form, titulo: e.target.value })} />
        </div>

        <div>
          <label className="label">Descrição</label>
          <textarea className="input" rows="3" value={form.descricao}
                    onChange={(e) => setForm({ ...form, descricao: e.target.value })} />
        </div>

        <div className="grid sm:grid-cols-2 gap-3">
          <div>
            <label className="label">Projeto *</label>
            <select className="input" value={form.projetoId}
                    onChange={(e) => setForm({ ...form, projetoId: e.target.value })}>
              {projetos.map((p) => (
                <option key={p.id} value={p.id}>
                  {p.nome} (prazo {p.prazo})
                </option>
              ))}
            </select>
            {projeto && (
              <p className="text-xs text-slate-500 mt-1">
                Prazo do projeto: <strong>{projeto.prazo}</strong>. RN03 bloqueia prazos maiores.
              </p>
            )}
          </div>
          <div>
            <label className="label">Responsável *</label>
            <select className="input" value={form.responsavelId}
                    onChange={(e) => setForm({ ...form, responsavelId: e.target.value })}>
              {USUARIOS.map((u) => <option key={u.id} value={u.id}>{u.nome}</option>)}
            </select>
          </div>
        </div>

        <div className="grid sm:grid-cols-3 gap-3">
          <div>
            <label className="label">Prioridade</label>
            <select className="input" value={form.prioridade}
                    onChange={(e) => setForm({ ...form, prioridade: e.target.value })}>
              <option value="baixa">Baixa</option>
              <option value="media">Média</option>
              <option value="alta">Alta</option>
            </select>
          </div>
          <div>
            <label className="label">Status</label>
            <select className="input" value={form.status}
                    onChange={(e) => setForm({ ...form, status: e.target.value })}>
              <option value="aberta">Aberta</option>
              <option value="em_andamento">Em andamento</option>
              <option value="concluida">Concluída</option>
              <option value="cancelada">Cancelada</option>
            </select>
          </div>
          <div>
            <label className="label">Prazo *</label>
            <input type="date" className="input" value={form.prazo}
                   onChange={(e) => setForm({ ...form, prazo: e.target.value })} />
          </div>
        </div>

        <div className="flex justify-end gap-2 pt-2">
          <button type="button" className="btn-secondary" onClick={() => navigate(-1)}>Cancelar</button>
          <button type="submit" className="btn-primary">{editando ? 'Salvar alterações' : 'Criar tarefa'}</button>
        </div>
      </form>
    </div>
  );
}
