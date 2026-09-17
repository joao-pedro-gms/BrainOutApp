import { useEffect, useState } from 'react';
import { Routes, Route, Navigate, useNavigate } from 'react-router-dom';
import Header from './components/Header.jsx';
import Layout from './components/Layout.jsx';
import Login from './screens/Login.jsx';
import Projetos from './screens/Projetos.jsx';
import ProjetoDetalhe from './screens/ProjetoDetalhe.jsx';
import Tarefas from './screens/Tarefas.jsx';
import TarefaForm from './screens/TarefaForm.jsx';
import Dashboard from './screens/Dashboard.jsx';
import { USUARIOS } from './data/mock';

const KEY_PERFIL = 'brainoutapp:perfil:v1';

export default function App() {
  const [perfil, setPerfil] = useState(() => localStorage.getItem(KEY_PERFIL) || '');
  const navigate = useNavigate();

  useEffect(() => {
    if (perfil) localStorage.setItem(KEY_PERFIL, perfil);
  }, [perfil]);

  const onLogin = (id) => {
    setPerfil(id);
    navigate('/projetos');
  };

  const onTrocarPerfil = (id) => {
    setPerfil(id);
  };

  const onSair = () => {
    setPerfil('');
    localStorage.removeItem(KEY_PERFIL);
    navigate('/login');
  };

  const usuario = USUARIOS.find((u) => u.id === perfil);

  if (!perfil) {
    return (
      <Routes>
        <Route path="/login"  element={<Login onLogin={onLogin} />} />
        <Route path="*"       element={<Navigate to="/login" replace />} />
      </Routes>
    );
  }

  return (
    <>
      <Header perfil={perfil} onTrocarPerfil={onTrocarPerfil} />
      <Layout>
        <Routes>
          <Route path="/"                  element={<Navigate to="/projetos" replace />} />
          <Route path="/projetos"          element={<Projetos perfil={perfil} />} />
          <Route path="/projetos/:id"      element={<ProjetoDetalhe perfil={perfil} />} />
          <Route path="/tarefas"           element={<Tarefas perfil={perfil} />} />
          <Route path="/tarefas/nova"      element={<TarefaForm perfil={perfil} />} />
          <Route path="/tarefas/:id/editar" element={<TarefaForm perfil={perfil} />} />
          <Route path="/dashboard"         element={<Dashboard perfil={perfil} />} />
          <Route path="/login"             element={<Login onLogin={onLogin} />} />
          <Route path="*"                  element={<NaoEncontrado perfilLabel={usuario?.perfil} onSair={onSair} />} />
        </Routes>
      </Layout>
    </>
  );
}

function NaoEncontrado({ onSair }) {
  return (
    <div className="text-center py-16">
      <p className="text-6xl mb-4">🧭</p>
      <h2 className="text-2xl font-semibold mb-2">Página não encontrada</h2>
      <p className="text-slate-600 mb-6">A rota pedida não existe no protótipo.</p>
      <button className="btn-secondary" onClick={onSair}>Voltar ao login</button>
    </div>
  );
}
