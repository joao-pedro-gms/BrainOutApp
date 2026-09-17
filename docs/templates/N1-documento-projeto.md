# Template N1 — Documento de Projeto

Preencher este arquivo e converter para PDF (`PI2026-2_NomeDaEquipe_N1.pdf`) na entrega de 02/10. Estrutura segue Apêndice A.1 do documento norteador.

Issue: #1.

---

## 1. Contexto e caracterização do domínio

(Por que gestão de projetos? Qual o problema? Onde se aplica?)

## 2. Problema, justificativa e objetivos

### 2.1 Objetivo geral

### 2.2 Objetivos específicos

## 3. Público-alvo e personas

(Mínimo 2 personas: Gerente e Colaborador. Com nome, cargo, dores, objetivos.)

## 4. Requisitos

### 4.1 Requisitos funcionais

(Referenciar R1-R14 com rastreabilidade.)

### 4.2 Requisitos não funcionais

(Performance, segurança, usabilidade, manutenibilidade.)

## 5. Regras de negócio

| ID | Descrição | Comportamento esperado | Mensagem ao usuário |
|----|-----------|------------------------|---------------------|
| RN01 | (ver [`docs/regras-negocio.md`](../regras-negocio.md)) | | |
| RN02 | | | |
| RN03 | | | |

## 6. Cronograma e responsabilidades

(Usar [`docs/quadro.md`](../quadro.md) como referência.)

| Ciclo | Período | Issues | Responsáveis |
|-------|---------|--------|--------------|
| Concepção | semanas 1-6 | #1-#5 | |
| Ciclo 1 | 14/09 - 27/09 | #6-#10 | |

## 7. Stack tecnológica (resumo)

(Justificativa completa em [`docs/adr/`](../adr/).)

| Camada | Escolha | Justificativa |
|--------|---------|---------------|
| App | Kotlin + Compose | ADR 0001 |
| Backend | Python + FastAPI | ADR 0002 |
| Banco | SQLite / PostgreSQL | ADR 0002 |

## 8. Apêndices

- A: Diagramas (modelo ER, camadas) → [`docs/modelo-dados.md`](../modelo-dados.md), [`docs/arquitetura.md`](../arquitetura.md)
- B: Ficha de avaliação por ciclo → preencher durante o semestre
- C: Lista de verificação R1-R14 → [`templates/lista-verificacao-R1-R14.md`](lista-verificacao-R1-R14.md)
- D: Roteiro de apresentação → [`templates/apresentacao-roteiro.md`](apresentacao-roteiro.md)
