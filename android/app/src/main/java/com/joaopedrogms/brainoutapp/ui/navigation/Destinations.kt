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
    const val LOGIN = "login"
    const val PROJETOS = "projetos"
    const val DETALHES_PROJETO_ROUTE = "detalhes/{projetoId}"
    const val TAREFAS = "tarefas"
    const val CRIACAO = "criacao"
    const val DASHBOARD = "dashboard"

    // Helpers para navegação tipada com argumentos.
    const val DETALHES_PROJETO_ARG = "projetoId"
    fun detalhesProjeto(projetoId: String) = "detalhes/$projetoId"
}
