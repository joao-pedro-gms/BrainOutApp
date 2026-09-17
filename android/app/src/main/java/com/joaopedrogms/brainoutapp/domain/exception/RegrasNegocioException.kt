package com.joaopedrogms.brainoutapp.domain.exception

/**
 * Exceção base para violações de regras de negócio (RN01-RN03).
 *
 * **Por que uma exceção tipada e não `IllegalStateException` cru?**
 *  - Permite que a camada de UI (ViewModel) capture especificamente
 *    erros de regra (sem engolir outras `IllegalStateException` de
 *    programação, como "tarefa sumiu do banco durante edição").
 *  - Centraliza a forma da mensagem — backend vai jogar mensagens
 *    equivalentes (issue #14), e a UI quer texto consistente.
 *  - Documenta o catálogo: subclasses poderiam detalhar o tipo (RN01 vs
 *    RN02 vs RN03), mas o brief desta lane pede **uma** exceção
 *    genérica com mensagem amigável — então mantemos simples.
 *
 * **Mensagem (R10 — amigável):** o construtor recebe a mensagem já
 * pronta para a UI exibir. As regras estão em
 * [com.joaopedrogms.brainoutapp.domain.usecase.ConcluirTarefaUseCase],
 * [com.joaopedrogms.brainoutapp.domain.usecase.ConcluirProjetoUseCase],
 * e estendida em [com.joaopedrogms.brainoutapp.domain.usecase.CriarTarefaUseCase]
 * / [com.joaopedrogms.brainoutapp.domain.usecase.EditarTarefaUseCase].
 *
 * Por convenção, o caller (ViewModel) exibe `message` num snackbar/
 * textbox de erro e mantém o `state` permitindo nova tentativa.
 */
open class RegrasNegocioException(
    override val message: String,
    cause: Throwable? = null,
) : Exception(message, cause)
