import { Icon } from '../lib/icons.jsx';

/**
 * Layout principal do protótipo.
 *
 * Em desktop (≥md), envolve a UI num "phone frame" — moldura que simula
 * um celular físico, para deixar claro que o design alvo é mobile.
 *
 * Em mobile (<md), sem moldura: app full-width, preenche a tela.
 */
export default function Layout({ children }) {
  return (
    <div className="min-h-screen bg-slate-100 md:bg-gradient-to-br md:from-slate-200 md:via-slate-100 md:to-slate-200 md:grid md:place-items-center md:py-10">
      {/* Mobile: app full-width no celular real */}
      <div className="md:hidden min-h-screen flex flex-col bg-slate-50">
        <main className="flex-1 w-full">{children}</main>
        <footer className="border-t border-slate-200 py-3 px-4 text-center text-xs text-slate-500">
          BrainOutApp · Protótipo · PI ADS/PUC-GO 2026/2
        </footer>
      </div>

      {/* Desktop: phone frame */}
      <div className="hidden md:block relative">
        <div
          className="relative bg-slate-50 rounded-[2.5rem] border-[10px] border-slate-900 shadow-[0_30px_60px_-15px_rgba(0,0,0,0.4)] overflow-hidden"
          style={{ width: '420px', height: '860px' }}
        >
          {/* notch (Dynamic Island) */}
          <div className="absolute top-2 left-1/2 -translate-x-1/2 w-24 h-6 bg-slate-900 rounded-full z-20 pointer-events-none" />

          {/* Status bar fake */}
          <div className="absolute top-0 inset-x-0 h-8 z-10 px-7 flex items-center justify-between text-[11px] font-semibold text-slate-900 pointer-events-none">
            <span>9:41</span>
            <span className="flex items-center gap-1 text-slate-700">
              <Icon name="check" size="xs" />
              <Icon name="play" size="xs" />
              <Icon name="calendarClock" size="xs" />
            </span>
          </div>

          {/* Conteudo do app com padding-top para status bar */}
          <main className="absolute inset-0 overflow-y-auto pt-8">
            {children}
            <footer className="border-t border-slate-200 py-3 px-4 text-center text-xs text-slate-500">
              BrainOutApp · Protótipo · PI ADS/PUC-GO 2026/2
            </footer>
          </main>

          {/* Home indicator */}
          <div className="absolute bottom-1.5 left-1/2 -translate-x-1/2 w-32 h-1 bg-slate-900 rounded-full pointer-events-none z-20" />
        </div>

        <p className="text-center text-xs text-slate-500 mt-4">
          Simulação de tela — iPhone 14 Pro (393×852)
        </p>
      </div>
    </div>
  );
}
