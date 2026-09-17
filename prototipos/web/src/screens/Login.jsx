import { USUARIOS } from '../data/mock';
import { Icon } from '../lib/icons.jsx';

export default function Login({ onLogin }) {
  return (
    <div className="min-h-full grid place-items-center px-4 py-8">
      <div className="w-full max-w-sm card p-7">
        <div className="text-center mb-6">
          <div className="mx-auto mb-4 w-14 h-14 rounded-2xl bg-brand-100 text-brand-700 grid place-items-center">
            <Icon name="projects" size="2xl" strokeWidth={1.75} />
          </div>
          <h1 className="text-2xl font-bold text-slate-900">BrainOutApp</h1>
          <p className="text-slate-600 text-sm mt-1">Gestão de projetos e tarefas</p>
        </div>

        <p className="text-sm text-slate-600 mb-4 text-center">
          Escolha um perfil para entrar. O protótipo simula os 2 perfis do app real (R2).
        </p>

        <div className="space-y-3">
          {USUARIOS.map((u) => {
            const isGerente = u.perfil === 'gerente';
            return (
              <button
                key={u.id}
                onClick={() => onLogin(u.id)}
                className="w-full text-left card p-4 hover:border-brand-400 hover:shadow-md transition"
              >
                <div className="flex items-center gap-3">
                  <div className="w-10 h-10 rounded-full bg-brand-100 text-brand-700 grid place-items-center shrink-0">
                    <Icon name="user" size="md" />
                  </div>
                  <div className="flex-1">
                    <div className="font-semibold text-slate-900">{u.nome}</div>
                    <div className="text-xs text-slate-500">{u.email}</div>
                  </div>
                  <span className={`badge ${isGerente ? 'bg-brand-100 text-brand-700' : 'bg-slate-100 text-slate-700'}`}>
                    {isGerente ? 'Gerente' : 'Colaborador'}
                  </span>
                </div>
              </button>
            );
          })}
        </div>

        <p className="text-xs text-slate-400 text-center mt-6">
          Sem senha — o protótipo não implementa autenticação real (issue #7).
        </p>
      </div>
    </div>
  );
}
