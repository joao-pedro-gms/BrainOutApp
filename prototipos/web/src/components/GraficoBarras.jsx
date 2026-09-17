/**
 * Gráfico de barras horizontal em SVG puro. Sem dependências externas.
 * Recebe { rotulos, valores, valorMax, cor } e renderiza.
 */
export default function GraficoBarras({ rotulos, valores, valorMax, cor = '#4f46e5', altura = 28 }) {
  if (!rotulos || rotulos.length === 0) {
    return <p className="text-sm text-slate-500">Sem dados para exibir.</p>;
  }
  const max = Math.max(valorMax ?? 0, ...valores, 1);
  const larguraLabel = Math.max(...rotulos.map((r) => r.length)) * 7 + 12;

  return (
    <svg
      role="img"
      aria-label="Gráfico de barras"
      viewBox={`0 0 360 ${rotulos.length * (altura + 8)}`}
      width="100%"
      height={rotulos.length * (altura + 8)}
      className="text-xs"
    >
      {rotulos.map((r, i) => {
        const v = valores[i] ?? 0;
        const w = (v / max) * (320 - larguraLabel);
        const y = i * (altura + 8);
        return (
          <g key={r}>
            <text x="0" y={y + altura / 2 + 4} fill="#475569" fontSize="11">{r}</text>
            <rect x={larguraLabel} y={y + 4} width={w} height={altura - 8} fill={cor} rx="4" />
            <text x={larguraLabel + w + 4} y={y + altura / 2 + 4} fill="#0f172a" fontSize="11" fontWeight="600">
              {v}
            </text>
          </g>
        );
      })}
    </svg>
  );
}
