import { USUARIOS } from '../data/mock';

export default function Login({ onLogin }) {
  return (
    <div className="min-h-screen bg-gradient-to-br from-brand-50 to-slate-100 grid place-items-center px-4">
      <div className="card w-full max-w-md p-8">
        <div className="text-center mb-6">
          <div className="text-5xl mb-3">🧠</div>
          <h1 className="text-2xl font-bold text-slate-900">BrainOutApp</h1>
          <p className="text-slate-600 text-sm mt-1">Gestão de projetos e tarefas — protótipo</p>
        </div>

        <p className="text-sm text-slate-600 mb-4 text-center">
          Escolha um perfil para entrar. O protótipo simula os 2 perfis do app real (R2).
        </p>

        <div className="space-y-3">
          {USUARIOS.map((u) => (
            <button
              key={u.id}
              onClick={() => onLogin(u.id)}
              className="w-full text-left card p-4 hover:border-brand-400 hover:shadow-md transition"
            >
              <div className="flex items-center gap-3">
                <div className="w-10 h-10 rounded-full bg-brand-100 text-brand-700 grid place-items-center font-semibold">
                  {u.nome.split(' ').map((p) => p[0]).slice(0, 2).join('')}
                </div>
                <div className="flex-1">
                  <div className="font-semibold text-slate-900">{u.nome}</div>
                  <div className="text-xs text-slate-500">{u.email}</div>
                </div>
                <span className={`badge ${u.perfil === 'gerente' ? 'bg-brand-100 text-brand-700' : 'bg-slate-100 text-slate-700'}`}>
                  {u.perfil === 'gerente' ? 'Gerente' : 'Colaborador'}
                </span>
              </div>
            </button>
          ))}
        </div>

        <p className="text-xs text-slate-400 text-center mt-6">
          Sem senha — o protótipo não implementa autenticação real (issue #7).
        </p>
      </div>
    </div>
  );
}
