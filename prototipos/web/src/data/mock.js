/**
 * Dados fixos para o protótipo. Determinístico entre reloads.
 * Formato compatível com o que virá do backend FastAPI (issue #14).
 */

const hoje = new Date();
const isoHoje = hoje.toISOString().slice(0, 10);
const addDias = (dias) => {
  const d = new Date(hoje);
  d.setDate(d.getDate() + dias);
  return d.toISOString().slice(0, 10);
};

export const PROJETOS_INICIAIS = [
  {
    id: 'p1',
    nome: 'Redesign do site institucional',
    descricao: 'Modernizar a landing page e migrar para um CMS headless.',
    dataInicio: addDias(-20),
    prazo: addDias(15),
    status: 'em_andamento',
    criadoPor: 'u1',
  },
  {
    id: 'p2',
    nome: 'App mobile de pedidos',
    descricao: 'Aplicativo para clientes finais fazerem pedidos direto pelo celular.',
    dataInicio: addDias(-10),
    prazo: addDias(45),
    status: 'em_andamento',
    criadoPor: 'u1',
  },
  {
    id: 'p3',
    nome: 'Integração com API de pagamentos',
    descricao: 'Conectar o sistema ao gateway de pagamento X.',
    dataInicio: addDias(-40),
    prazo: addDias(-2),
    status: 'concluido',
    criadoPor: 'u1',
  },
];

export const TAREFAS_INICIAIS = [
  // Projeto 1
  { id: 't1',  projetoId: 'p1', titulo: 'Levantar requisitos com stakeholders',          descricao: 'Reunião com marketing e vendas.',                       responsavelId: 'u1', prioridade: 'alta',  status: 'concluida',     prazo: addDias(-15) },
  { id: 't2',  projetoId: 'p1', titulo: 'Wireframes de baixa fidelidade',              descricao: '',                                                     responsavelId: 'u1', prioridade: 'alta',  status: 'concluida',     prazo: addDias(-10) },
  { id: 't3',  projetoId: 'p1', titulo: 'Aprovar design visual com cliente',            descricao: '',                                                     responsavelId: 'u2', prioridade: 'media', status: 'em_andamento',  prazo: addDias(2)   },
  { id: 't4',  projetoId: 'p1', titulo: 'Implementar seção hero',                       descricao: '',                                                     responsavelId: 'u2', prioridade: 'media', status: 'aberta',        prazo: addDias(8)   },
  { id: 't5',  projetoId: 'p1', titulo: 'Configurar CMS headless',                      descricao: '',                                                     responsavelId: 'u1', prioridade: 'baixa', status: 'aberta',        prazo: addDias(12)  },
  // Projeto 2
  { id: 't6',  projetoId: 'p2', titulo: 'Definir stack (React Native vs Flutter)',      descricao: '',                                                     responsavelId: 'u1', prioridade: 'alta',  status: 'concluida',     prazo: addDias(-7)  },
  { id: 't7',  projetoId: 'p2', titulo: 'Protótipo de navegação',                       descricao: '',                                                     responsavelId: 'u2', prioridade: 'media', status: 'em_andamento',  prazo: addDias(3)   },
  { id: 't8',  projetoId: 'p2', titulo: 'Tela de catálogo',                             descricao: '',                                                     responsavelId: 'u2', prioridade: 'media', status: 'aberta',        prazo: addDias(20)  },
  { id: 't9',  projetoId: 'p2', titulo: 'Carrinho de compras',                          descricao: '',                                                     responsavelId: 'u2', prioridade: 'alta',  status: 'aberta',        prazo: addDias(30)  },
  { id: 't10', projetoId: 'p2', titulo: 'Integração com backend de pedidos',            descricao: '',                                                     responsavelId: 'u1', prioridade: 'alta',  status: 'aberta',        prazo: addDias(40)  },
  // Projeto 3 (já concluído, mas com tarefas atrasadas para popular dashboard)
  { id: 't11', projetoId: 'p3', titulo: 'Configurar webhook de pagamento',              descricao: '',                                                     responsavelId: 'u1', prioridade: 'alta',  status: 'concluida',     prazo: addDias(-5)  },
  { id: 't12', projetoId: 'p3', titulo: 'Testes de integração em sandbox',              descricao: '',                                                     responsavelId: 'u1', prioridade: 'media', status: 'concluida',     prazo: addDias(-3)  },
  // Tarefa atrasada propositalmente para mostrar no dashboard
  { id: 't13', projetoId: 'p1', titulo: 'Reunião de follow-up (atrasada)',              descricao: 'Essa tarefa aparece como atrasada no dashboard.',       responsavelId: 'u2', prioridade: 'media', status: 'aberta',        prazo: addDias(-3)  },
];

export const USUARIOS = [
  { id: 'u1', nome: 'Ana Gerente',   perfil: 'gerente',      email: 'ana@brainoutapp.dev'  },
  { id: 'u2', nome: 'Carlos Colab.', perfil: 'colaborador',  email: 'carlos@brainoutapp.dev' },
];

export const STATUS_TAREFA = {
  aberta:        { label: 'Aberta',         cor: 'bg-slate-100 text-slate-700' },
  em_andamento:  { label: 'Em andamento',   cor: 'bg-blue-100 text-blue-700' },
  concluida:     { label: 'Concluída',      cor: 'bg-green-100 text-green-700' },
  cancelada:     { label: 'Cancelada',      cor: 'bg-slate-100 text-slate-500' },
};

export const PRIORIDADE_TAREFA = {
  baixa:  { label: 'Baixa',  cor: 'bg-slate-100 text-slate-600' },
  media:  { label: 'Média',  cor: 'bg-yellow-100 text-yellow-700' },
  alta:   { label: 'Alta',   cor: 'bg-red-100 text-red-700' },
};

export const STATUS_PROJETO = {
  planejado:     { label: 'Planejado',     cor: 'bg-slate-100 text-slate-700' },
  em_andamento:  { label: 'Em andamento',  cor: 'bg-blue-100 text-blue-700' },
  concluido:     { label: 'Concluído',     cor: 'bg-green-100 text-green-700' },
  cancelado:     { label: 'Cancelado',     cor: 'bg-slate-100 text-slate-500' },
};

export const HOJE = isoHoje;
