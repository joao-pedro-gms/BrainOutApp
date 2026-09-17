/**
 * Mapa de Lucide icons usados no protótipo.
 *
 * Centraliza os imports para:
 *  - evitar N imports repetidos pelos componentes
 *  - manter tamanho de bundle pequeno (tree-shaking)
 *  - padronizar tamanho/cor por contexto (sm, md, lg)
 *
 * Quando precisar de um ícone novo, adicione aqui e use via `import { Icon } from '@/lib/icons'`.
 */
import {
  // Header / nav
  LayoutDashboard,
  ListTodo,
  FolderKanban,
  // Login / estados
  CircleUserRound,
  Eye,
  EyeOff,
  // Empty states
  FolderOpen,
  SearchX,
  Inbox,
  ClipboardList,
  Lock,
  Compass,
  // Ações inline
  RefreshCcw,
  Plus,
  X,
  Pencil,
  Trash2,
  ChevronRight,
  ChevronLeft,
  // Cards e dashboards
  CheckCircle2,
  ClipboardCheck,
  AlertTriangle,
  CalendarClock,
  Info,
  // Filtros / domínios
  Filter,
  CalendarDays,
  Flag,
  User as UserIcon,
  Briefcase,
  Calendar,
  // Categorias / estado
  PlayCircle,
  CheckCheck,
  CircleSlash,
  Circle,
} from 'lucide-react';

/**
 * Wrapper que aplica tamanho + acessibilidade padronizados.
 * Use `<Icon name="folder" />` ao invés de importar lucide direto,
 * para garantir consistência visual.
 */
const REGISTRY = {
  // Header / nav
  'dashboard':    LayoutDashboard,
  'tasks':        ListTodo,
  'projects':     FolderKanban,
  // Login
  'user':         CircleUserRound,
  'eye':          Eye,
  'eyeOff':       EyeOff,
  // Empty states
  'folderOpen':   FolderOpen,
  'searchX':      SearchX,
  'inbox':        Inbox,
  'clipboardList':ClipboardList,
  'lock':         Lock,
  'compass':      Compass,
  // Ações
  'refresh':      RefreshCcw,
  'plus':         Plus,
  'close':        X,
  'pencil':       Pencil,
  'trash':        Trash2,
  'chevronRight': ChevronRight,
  'chevronLeft':  ChevronLeft,
  // Dashboard
  'check':        CheckCircle2,
  'checkAll':     CheckCheck,
  'clipboardCheck':ClipboardCheck,
  'alert':        AlertTriangle,
  'calendarClock':CalendarClock,
  'info':         Info,
  // Filtros
  'filter':       Filter,
  'calendarDays': CalendarDays,
  'flag':         Flag,
  'userSmall':    UserIcon,
  'briefcase':    Briefcase,
  'calendar':     Calendar,
  // Status
  'play':         PlayCircle,
  'circleSlash':  CircleSlash,
  'circle':       Circle,
};

export const ICON_SIZES = { xs: 14, sm: 16, md: 20, lg: 24, xl: 32, '2xl': 48 };

/**
 * `<Icon name="folder" size="md" className="text-slate-500" />`
 */
export function Icon({ name, size = 'md', className = '', strokeWidth = 2, ...rest }) {
  const Cmp = REGISTRY[name];
  if (!Cmp) {
    if (import.meta.env.DEV) console.warn(`[Icon] nome desconhecido: ${name}`);
    return null;
  }
  const px = typeof size === 'number' ? size : ICON_SIZES[size] ?? ICON_SIZES.md;
  return <Cmp size={px} strokeWidth={strokeWidth} className={className} aria-hidden="true" {...rest} />;
}

/** Lista de nomes válidos (útil para typos em dev) */
export const ICON_NAMES = Object.keys(REGISTRY);
