import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

// GitHub Pages publica em /BrainOutApp/, então o base precisa casar.
export default defineConfig({
  plugins: [react()],
  base: '/BrainOutApp/',
  build: {
    outDir: 'dist',
    sourcemap: false,
  },
  preview: {
    // Faz fallback para index.html em rotas SPA (necessário para testes locais
    // e para GitHub Pages). Em produção, o servidor do Pages já faz isso.
    historyApiFallback: true,
  },
});
