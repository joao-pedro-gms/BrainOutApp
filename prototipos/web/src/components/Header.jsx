import { Link, NavLink, useNavigate } from 'react-router-dom';
import { USUARIOS } from '../data/mock';
import { Icon } from '../lib/icons.jsx';

export default function Header({ perfil, onTrocarPerfil }) {
  const navigate = useNavigate();
  const usuario = USUARIOS.find((u) => u.id === perfil);
  const outro = USUARIOS.find((u) => u.id !== perfil);

  return (
    <header className="bg-white border-b border-slate-200 sticky top-0 z-10">
      <div className="max-w-md mx-auto px-4 py-3 flex items-center justify-between gap-3">
        <Link to="/" className="flex items-center gap-2 font-semibold text-slate-900 shrink-0">
          <Icon name="projects" size="lg" className="text-brand-600" />
          <span className="hidden sm:inline">BrainOutApp</span>
        </Link>

        <nav className="flex items-center gap-0.5 sm:gap-1 text-sm">
          <NavTab to="/projetos"   label="Projetos" iconName="projects" hideLabelOnMobile />
          <NavTab to="/tarefas"    label="Tarefas"  iconName="tasks" hideLabelOnMobile />
          <NavTab to="/dashboard"  label="Dashboard" iconName="dashboard" hideLabelOnMobile />
        </nav>

        <div className="flex items-center gap-2 shrink-0">
          {/* Em mobile, mostra só um botão compacto "Trocar perfil" para caber nos 375px */}
          <button
            type="button"
            onClick={() => { onTrocarPerfil(outro.id); navigate('/'); }}
            className="md:hidden w-9 h-9 rounded-lg border border-slate-300 bg-white hover:bg-slate-50 grid place-items-center shrink-0 text-slate-600"
            aria-label={`Trocar para ${outro?.nome}`}
            title={`Trocar para ${outro?.nome} (${outro?.perfil})`}
          >
            <Icon name="refresh" size="sm" />
          </button>

          {/* Em desktop, select completo */}
          <select
            value={perfil}
            onChange={(e) => { onTrocarPerfil(e.target.value); navigate('/'); }}
            className="hidden md:block text-sm rounded-lg border border-slate-300 px-2 py-1.5 bg-white focus:outline-none focus:ring-2 focus:ring-brand-500"
            aria-label="Trocar perfil"
          >
            {USUARIOS.map((u) => (
              <option key={u.id} value={u.id}>
                {u.nome} ({u.perfil === 'gerente' ? 'Gerente' : 'Colaborador'})
              </option>
            ))}
          </select>

          {/* Avatar: ícone de usuário (consistente com Login) */}
          <div
            className="w-9 h-9 rounded-full bg-brand-100 text-brand-700 grid place-items-center shrink-0"
            title={usuario?.email}
            aria-label={`Usuário atual: ${usuario?.nome}`}
          >
            <Icon name="user" size="sm" />
          </div>
        </div>
      </div>
    </header>
  );
}

function NavTab({ to, label, iconName, hideLabelOnMobile = false }) {
  return (
    <NavLink
      to={to}
      className={({ isActive }) =>
        `px-2 sm:px-3 py-1.5 rounded-md whitespace-nowrap inline-flex items-center gap-1.5 ${isActive ? 'bg-brand-50 text-brand-700' : 'text-slate-600 hover:bg-slate-100'}`
      }
    >
      <Icon name={iconName} size="sm" />
      <span className={hideLabelOnMobile ? 'hidden sm:inline' : ''}>{label}</span>
    </NavLink>
  );
}
