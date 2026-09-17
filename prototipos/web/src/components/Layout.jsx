export default function Layout({ children }) {
  return (
    <div className="min-h-screen flex flex-col">
      <main className="flex-1 max-w-6xl mx-auto w-full px-4 py-6">{children}</main>
      <footer className="border-t border-slate-200 py-4 text-center text-xs text-slate-500">
        BrainOutApp · Protótipo navegável · Projeto Integrador ADS / PUC Goiás 2026/2
      </footer>
    </div>
  );
}
