import { Link, NavLink, useNavigate } from 'react-router-dom';
import { USUARIOS } from '../data/mock';

export default function Header({ perfil, onTrocarPerfil }) {
  const navigate = useNavigate();
  const usuario = USUARIOS.find((u) => u.id === perfil);

  return (
    <header className="bg-white border-b border-slate-200 sticky top-0 z-10">
      <div className="max-w-6xl mx-auto px-4 py-3 flex items-center justify-between gap-4">
        <Link to="/" className="flex items-center gap-2 font-semibold text-slate-900">
          <span className="text-2xl" aria-hidden>🧠</span>
          <span className="hidden sm:inline">BrainOutApp</span>
        </Link>

        <nav className="flex items-center gap-1 text-sm">
          <NavTab to="/projetos"   label="Projetos" />
          <NavTab to="/tarefas"    label="Tarefas" />
          <NavTab to="/dashboard"  label="Dashboard" />
        </nav>

        <div className="flex items-center gap-2">
          <select
            value={perfil}
            onChange={(e) => { onTrocarPerfil(e.target.value); navigate('/'); }}
            className="text-sm rounded-lg border border-slate-300 px-2 py-1.5 bg-white focus:outline-none focus:ring-2 focus:ring-brand-500"
            aria-label="Trocar perfil"
          >
            {USUARIOS.map((u) => (
              <option key={u.id} value={u.id}>
                {u.nome} ({u.perfil === 'gerente' ? 'Gerente' : 'Colaborador'})
              </option>
            ))}
          </select>
          <div className="hidden md:block w-9 h-9 rounded-full bg-brand-100 text-brand-700 grid place-items-center text-sm font-semibold" title={usuario?.email}>
            {usuario?.nome.split(' ').map((p) => p[0]).slice(0, 2).join('')}
          </div>
        </div>
      </div>
    </header>
  );
}

function NavTab({ to, label }) {
  return (
    <NavLink
      to={to}
      className={({ isActive }) =>
        `px-3 py-1.5 rounded-md ${isActive ? 'bg-brand-50 text-brand-700' : 'text-slate-600 hover:bg-slate-100'}`
      }
    >
      {label}
    </NavLink>
  );
}
