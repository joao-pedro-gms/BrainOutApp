import { useParams, Link } from 'react-router-dom';
import { useProjetos, useTarefas } from '../lib/store';
import { StatusTarefaBadge, PrioridadeBadge, StatusProjetoBadge } from '../components/Badges';
import { Icon } from '../lib/icons.jsx';
import { formatarData, tarefaAtrasada, diasAte } from '../lib/regras';

export default function ProjetoDetalhe({ perfil }) {
  const { id } = useParams();
  const { projetos } = useProjetos();
  const { tarefas, atualizar } = useTarefas();
  const isGerente = perfil === 'u1';

  const projeto = projetos.find((p) => p.id === id);

  if (!projeto) {
    return (
      <div className="card p-8 text-center">
        <div className="mx-auto mb-3 w-12 h-12 rounded-2xl bg-slate-100 text-slate-400 grid place-items-center">
          <Icon name="compass" size="xl" strokeWidth={1.5} />
        </div>
        <h2 className="text-xl font-semibold mb-2">Projeto não encontrado</h2>
        <Link to="/projetos" className="text-brand-600 hover:underline">← Voltar para projetos</Link>
      </div>
    );
  }

  const tarefasDoProjeto = tarefas.filter((t) => t.projetoId === projeto.id);

  function handleAvancar(tarefa) {
    const proximo = { aberta: 'em_andamento', em_andamento: 'concluida' }[tarefa.status];
    if (!proximo) return;
    if (proximo === 'concluida' && !isGerente && tarefa.responsavelId !== perfil) {
      alert('Colaborador só pode alterar status das próprias tarefas.');
      return;
    }
    atualizar(tarefa.id, { status: proximo });
  }

  return (
    <div>
      <Link to="/projetos" className="text-sm text-brand-600 hover:underline mb-3 inline-block">← Projetos</Link>

      <div className="card p-6 mb-6">
        <div className="flex items-start justify-between gap-3 mb-2">
          <h1 className="text-2xl font-bold text-slate-900">{projeto.nome}</h1>
          <StatusProjetoBadge status={projeto.status} />
        </div>
        {projeto.descricao && <p className="text-slate-600 mb-4">{projeto.descricao}</p>}
        <div className="flex flex-wrap gap-4 text-sm text-slate-600">
          <span className="inline-flex items-center gap-1.5">
            <Icon name="calendar" size="sm" className="text-slate-400" />
            Início: <strong className="text-slate-700">{formatarData(projeto.dataInicio)}</strong>
          </span>
          <span className="inline-flex items-center gap-1.5">
            <Icon name="calendarClock" size="sm" className="text-slate-400" />
            Prazo: <strong className="text-slate-700">{formatarData(projeto.prazo)}</strong>
            {diasAte(projeto.prazo) != null && (
              <span className="text-slate-400"> ({diasAte(projeto.prazo) >= 0 ? `em ${diasAte(projeto.prazo)}d` : `${Math.abs(diasAte(projeto.prazo))}d atrás`})</span>
            )}
          </span>
          <span className="inline-flex items-center gap-1.5">
            <Icon name="clipboardList" size="sm" className="text-slate-400" />
            {tarefasDoProjeto.length} tarefa(s)
          </span>
        </div>
      </div>

      <div className="flex items-center justify-between mb-3">
        <h2 className="text-lg font-semibold text-slate-900">Tarefas</h2>
        {isGerente && (
          <Link to={`/tarefas/nova?projeto=${projeto.id}`} className="btn-primary text-sm">+ Nova tarefa</Link>
        )}
      </div>

      {tarefasDoProjeto.length === 0 ? (
        <div className="card p-8 text-center text-slate-500">
          <div className="mx-auto mb-2 w-10 h-10 rounded-2xl bg-slate-100 text-slate-400 grid place-items-center">
            <Icon name="clipboardList" size="lg" strokeWidth={1.5} />
          </div>
          <p>Nenhuma tarefa nesse projeto ainda.</p>
        </div>
      ) : (
        <ul className="space-y-2">
          {tarefasDoProjeto.map((t) => {
            const atrasada = tarefaAtrasada(t);
            return (
              <li key={t.id} className="card p-4 flex items-center gap-3">
                <div className="flex-1 min-w-0">
                  <div className="flex items-center gap-2 mb-1 flex-wrap">
                    <span className="font-medium text-slate-900">{t.titulo}</span>
                    <StatusTarefaBadge status={t.status} />
                    <PrioridadeBadge prioridade={t.prioridade} />
                    {atrasada && (
                      <span className="badge bg-red-100 text-red-700 inline-flex items-center gap-1">
                        <Icon name="alert" size="xs" /> Atrasada
                      </span>
                    )}
                  </div>
                  <div className="text-xs text-slate-500 inline-flex items-center gap-1">
                    <Icon name="calendar" size="xs" />
                    Prazo: {formatarData(t.prazo)}
                  </div>
                </div>
                {t.status !== 'concluida' && t.status !== 'cancelada' && (
                  <button onClick={() => handleAvancar(t)} className="btn-secondary text-xs">
                    {t.status === 'aberta' ? 'Iniciar' : 'Concluir'}
                  </button>
                )}
              </li>
            );
          })}
        </ul>
      )}
    </div>
  );
}
