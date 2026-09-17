package com.joaopedrogms.brainoutapp.ui.navigation

/**
 * Rotas canônicas do NavHost.
 *
 * Convenção: `lowercase-hyphen`, sem acentos, estável entre ciclos.
 * Argumentos opcionais (ex.: `id` do projeto) seguem a forma `path/{arg}`.
 *
 * Mapeamento com os wireframes (prototipos/wireframes/):
 *   - Login              → 01-login.svg              (R2 — autenticação)
 *   - Projetos (lista)   → 02-projetos.svg           (R3, R5)
 *   - Detalhes projeto   → 03-projeto-detalhe.svg    (R3)
 *   - Tarefas (lista)    → 04-tarefas.svg            (R9)
 *   - Criação (form)     → 05-tarefa-form.svg        (R3, R4 — RN03)
 *   - Dashboard          → 06-dashboard.svg          (R9)
 */
object Destinations {
    const val ONBOARDING = "onboarding"
    const val LOGIN = "login"
    const val PROJETOS = "projetos"
    const val DETALHES_PROJETO_ROUTE = "detalhes/{projetoId}"
    const val TAREFAS = "tarefas"
    const val CRIACAO = "criacao"

    /**
     * Variante da rota `criacao` em **modo edição de projeto**: recebe o
     * `projetoId` para que o `ProjetoFormViewModel` carregue os dados do
     * projeto e pré-popule o formulário.
     *
     * O registro do `composable(...)` correspondente no
     * `BrainOutAppNavHost.kt` é responsabilidade da lane de onboarding
     * (que já mexe no startDestination). Esta lane (#8) só adiciona o
     * helper canônico aqui para evitar divergência.
     */
    const val CRIACAO_PROJETO_ROUTE = "criacao/{projetoId}"
    const val CRIACAO_PROJETO_ARG = "projetoId"
    fun criacaoProjeto(projetoId: String) = "criacao/$projetoId"

    /**
     * Rota de formulário de **tarefa** (issue #10 — CRUD Tarefas).
     *
     * Aceita dois argumentos opcionais via query string (não path):
     *  - `projetoId` — pré-seleciona o projeto pai. Quando ausente e
     *    criando, o usuário escolhe via dropdown.
     *  - `tarefaId` — quando preenchido, entra em modo edição e carrega
     *    a tarefa do DAO.
     *
     * **Por que query e não path?** o Compose Navigation não suporta
     * segmentos de path opcionais (`{projetoId?}/{tarefaId?}` precisa de
     * dois `composable` separados para cada combinação). Query string
     * mantém um único `composable` e os dois argumentos ficam
     * independentemente opcionais — exatamente o que o briefing pede.
     *
     * Convenção: o nome da rota tem `tarefa?` para distinguir do
     * `criacao` legado de projeto sem precisar generalizar a rota
     * `criacao` (decisão arquitetural da lane #10 — opção A do briefing).
     */
    const val CRIACAO_TAREFA_ROUTE = "criacao/tarefa?projetoId={projetoId}&tarefaId={tarefaId}"
    const val CRIACAO_TAREFA_PROJETO_ARG = "projetoId"
    const val CRIACAO_TAREFA_ARG = "tarefaId"

    /**
     * Helper para abrir o form em **modo criar**:
     *  - sem `projetoId` → usuário escolhe via dropdown.
     *  - com `projetoId` → tarefa já nasce naquele projeto.
     */
    fun criacaoTarefaNova(projetoId: String? = null): String =
        if (projetoId.isNullOrBlank()) "criacao/tarefa"
        else "criacao/tarefa?projetoId=$projetoId"

    /** Helper para abrir o form em **modo editar** (com tarefa pré-carregada). */
    fun criacaoTarefaEditar(tarefaId: String): String =
        "criacao/tarefa?tarefaId=$tarefaId"

    const val DASHBOARD = "dashboard"

    // Helpers para navegação tipada com argumentos.
    const val DETALHES_PROJETO_ARG = "projetoId"
    fun detalhesProjeto(projetoId: String) = "detalhes/$projetoId"
}
