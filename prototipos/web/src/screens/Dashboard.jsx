import { useMemo } from 'react';
import { Link } from 'react-router-dom';
import { useProjetos, useTarefas } from '../lib/store';
import GraficoBarras from '../components/GraficoBarras';
import { StatusTarefaBadge, StatusProjetoBadge } from '../components/Badges';
import { Icon } from '../lib/icons.jsx';
import { USUARIOS, HOJE } from '../data/mock';
import { tarefaAtrasada, formatarData } from '../lib/regras';

export default function Dashboard({ perfil }) {
  const { projetos } = useProjetos();
  const { tarefas } = useTarefas();
  const isGerente = perfil === 'u1';

  const stats = useMemo(() => {
    const concluidas  = tarefas.filter((t) => t.status === 'concluida').length;
    const emAberto    = tarefas.filter((t) => t.status === 'aberta' || t.status === 'em_andamento').length;
    const atrasadas   = tarefas.filter(tarefaAtrasada).length;
    const proximo7d   = tarefas.filter((t) => {
      if (t.status === 'concluida' || t.status === 'cancelada') return false;
      const prazo = new Date(t.prazo + 'T00:00:00');
      const limite = new Date(HOJE + 'T00:00:00');
      limite.setDate(limite.getDate() + 7);
      return prazo <= limite;
    }).length;
    return { concluidas, emAberto, atrasadas, proximo7d };
  }, [tarefas]);

  const porResponsavel = useMemo(() => {
    return USUARIOS.map((u) => ({
      rotulo: u.nome,
      valor: tarefas.filter((t) => t.responsavelId === u.id && t.status !== 'concluida' && t.status !== 'cancelada').length,
    }));
  }, [tarefas]);

  const porProjeto = useMemo(() => {
    return projetos.map((p) => ({
      rotulo: p.nome.length > 22 ? p.nome.slice(0, 20) + '…' : p.nome,
      valor: tarefas.filter((t) => t.projetoId === p.id && t.status !== 'concluida' && t.status !== 'cancelada').length,
    }));
  }, [tarefas, projetos]);

  const proximas = useMemo(() => {
    return tarefas
      .filter((t) => t.status !== 'concluida' && t.status !== 'cancelada')
      .sort((a, b) => a.prazo.localeCompare(b.prazo))
      .slice(0, 5);
  }, [tarefas]);

  return (
    <div>
      <div className="mb-6">
        <h1 className="text-2xl font-bold text-slate-900">Dashboard</h1>
        <p className="text-slate-600 text-sm">
          {isGerente ? 'Visão consolidada para o Gerente.' : 'Visão filtrada — apenas tarefas atribuídas a você.'}
        </p>
      </div>

      <div className="grid grid-cols-2 lg:grid-cols-4 gap-3 mb-6">
        <CardStat titulo="Tarefas concluídas" valor={stats.concluidas}  iconName="check"      cor="text-green-700" />
        <CardStat titulo="Em aberto"          valor={stats.emAberto}    iconName="clipboardList" cor="text-blue-700" />
        <CardStat titulo="Atrasadas"          valor={stats.atrasadas}    iconName="alert"      cor="text-red-700"   destaque={stats.atrasadas > 0} />
        <CardStat titulo="Vencem em 7 dias"   valor={stats.proximo7d}   iconName="calendarClock" cor="text-amber-700" />
      </div>

      <div className="grid lg:grid-cols-2 gap-4 mb-6">
        <div className="card p-5">
          <h2 className="font-semibold text-slate-900 mb-3">Tarefas em aberto por responsável</h2>
          <GraficoBarras
            rotulos={porResponsavel.map((r) => r.rotulo)}
            valores={porResponsavel.map((r) => r.valor)}
            cor="#4f46e5"
          />
        </div>
        <div className="card p-5">
          <h2 className="font-semibold text-slate-900 mb-3">Tarefas em aberto por projeto</h2>
          <GraficoBarras
            rotulos={porProjeto.map((r) => r.rotulo)}
            valores={porProjeto.map((r) => r.valor)}
            cor="#0ea5e9"
          />
        </div>
      </div>

      <div className="card p-5">
        <div className="flex items-center justify-between mb-3">
          <h2 className="font-semibold text-slate-900">Próximas tarefas a vencer</h2>
          <Link to="/tarefas" className="text-sm text-brand-600 hover:underline">Ver todas →</Link>
        </div>
        {proximas.length === 0 ? (
          <p className="text-sm text-slate-500">Nenhuma tarefa pendente.</p>
        ) : (
          <ul className="divide-y divide-slate-100">
            {proximas.map((t) => {
              const projeto = projetos.find((p) => p.id === t.projetoId);
              const atrasada = tarefaAtrasada(t);
              return (
                <li key={t.id} className="py-2 flex items-center justify-between gap-3">
                  <div className="min-w-0">
                    <Link to={`/tarefas/${t.id}/editar`} className="font-medium text-slate-900 hover:text-brand-700 truncate block">{t.titulo}</Link>
                    <div className="text-xs text-slate-500">{projeto?.nome}</div>
                  </div>
                  <div className="flex items-center gap-2 text-xs shrink-0">
                    <StatusTarefaBadge status={t.status} />
                    <span className={atrasada ? 'text-red-600 font-semibold' : 'text-slate-600'}>{formatarData(t.prazo)}</span>
                  </div>
                </li>
              );
            })}
          </ul>
        )}
      </div>

      {!isGerente && (
        <div className="card p-4 mt-6 bg-slate-50 border-slate-300">
          <p className="text-sm text-slate-700 inline-flex items-start gap-2">
            <Icon name="info" size="sm" className="text-slate-500 shrink-0 mt-0.5" />
            <span>
              <strong>Nota:</strong> como Colaborador, você está vendo as estatísticas globais.
              No app real (issue #7) o dashboard filtra para mostrar apenas suas tarefas e os projetos em que você participa.
            </span>
          </p>
        </div>
      )}
    </div>
  );
}

function CardStat({ titulo, valor, iconName, cor, destaque }) {
  return (
    <div className={`card p-4 ${destaque ? 'border-red-300 bg-red-50' : ''}`}>
      <div className="flex items-center justify-between">
        <span className="text-xs uppercase tracking-wide text-slate-500">{titulo}</span>
        <Icon name={iconName} size="md" className={cor} />
      </div>
      <div className={`mt-2 text-3xl font-bold ${cor}`}>{valor}</div>
    </div>
  );
}
