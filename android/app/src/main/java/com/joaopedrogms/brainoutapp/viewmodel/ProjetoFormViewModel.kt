package com.joaopedrogms.brainoutapp.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joaopedrogms.brainoutapp.data.local.dao.ProjetoDao
import com.joaopedrogms.brainoutapp.data.local.entity.ProjetoEntity
import com.joaopedrogms.brainoutapp.domain.model.Projeto
import com.joaopedrogms.brainoutapp.domain.model.StatusProjeto
import com.joaopedrogms.brainoutapp.domain.model.toDomain
import com.joaopedrogms.brainoutapp.domain.usecase.CriarProjetoUseCase
import com.joaopedrogms.brainoutapp.domain.usecase.EditarProjetoUseCase
import com.joaopedrogms.brainoutapp.ui.navigation.Destinations
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

/**
 * ViewModel do **formulário** de projeto (criar e editar).
 *
 * Fluxo:
 *  - Recebe `projetoId` opcional do [SavedStateHandle] da rota. Quando
 *    preenchido, pré-popula o estado com os campos do projeto vindo do
 *    DAO. Quando ausente, estado inicial = "criar".
 *  - Mantém [FormState] com valores atuais, mensagens de erro por campo
 *    e flag `submetendo` para o botão Salvar mostrar progresso.
 *  - `salvar()` valida localmente, chama `CriarProjetoUseCase` ou
 *    `EditarProjetoUseCase`, e emite [Event.Concluido] one-shot.
 *
 * **Validações (issue #8):**
 *  - `nome`: obrigatório, não vazio após trim, ≤ 100 caracteres.
 *  - `descricao`: opcional, ≤ 500 caracteres.
 *  - `prazo`: opcional; se preenchido, **≥ hoje** (UTC).
 *
 * Regras RN01-RN03 (ciclo 3) continuam fora — esta classe aplica só as
 * validações de input declaradas na issue #8.
 */
@HiltViewModel
class ProjetoFormViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val dao: ProjetoDao,
    private val criarProjeto: CriarProjetoUseCase,
    private val editarProjeto: EditarProjetoUseCase,
) : ViewModel() {

    /** Evento one-shot consumido pela UI (snackbar / navegação). */
    sealed interface Event {
        data object Concluido : Event
    }

    /** Estado do formulário. */
    data class FormState(
        val carregando: Boolean = false,
        val ehEdicao: Boolean = false,
        val projetoId: String? = null,
        val nome: String = "",
        val descricao: String = "",
        val prazo: LocalDate? = null,
        val status: StatusProjeto = StatusProjeto.ABERTO,
        val nomeError: String? = null,
        val descricaoError: String? = null,
        val prazoError: String? = null,
        val submetendo: Boolean = false,
        val mensagemErroGeral: String? = null,
    ) {
        val podeSubmeter: Boolean
            get() = !submetendo &&
                nomeError == null && descricaoError == null && prazoError == null &&
                nome.isNotBlank()
    }

    private val _state = MutableStateFlow(FormState(carregando = true))
    val state: StateFlow<FormState> = _state.asStateFlow()

    private val _event = Channel<Event>(Channel.BUFFERED)
    val event = _event.receiveAsFlow()

    init {
        val projetoId = savedStateHandle
            .get<String>(Destinations.DETALHES_PROJETO_ARG)
            ?.takeIf { it.isNotBlank() }
        if (projetoId == null) {
            _state.update { it.copy(carregando = false) }
        } else {
            carregarExistente(projetoId)
        }
    }

    private fun carregarExistente(id: String) {
        viewModelScope.launch {
            val entity: ProjetoEntity? = dao.getByIdOnce(id)
            if (entity == null) {
                _state.update {
                    it.copy(
                        carregando = false,
                        ehEdicao = false,
                        mensagemErroGeral = "Projeto não encontrado (id=$id).",
                    )
                }
                return@launch
            }
            val projeto: Projeto = entity.toDomain()
            _state.update {
                it.copy(
                    carregando = false,
                    ehEdicao = true,
                    projetoId = projeto.id,
                    nome = projeto.nome,
                    descricao = projeto.descricao.orEmpty(),
                    prazo = projeto.prazo,
                    status = projeto.status,
                )
            }
        }
    }

    // -------- entradas do formulário (chamadas pela UI a cada mudança) --------

    fun onNomeChange(value: String) {
        _state.update { it.copy(nome = value, nomeError = validarNome(value)) }
    }

    fun onDescricaoChange(value: String) {
        _state.update { it.copy(descricao = value, descricaoError = validarDescricao(value)) }
    }

    fun onPrazoChange(value: LocalDate?) {
        _state.update { it.copy(prazo = value, prazoError = validarPrazo(value)) }
    }

    fun onStatusChange(value: StatusProjeto) {
        _state.update { it.copy(status = value) }
    }

    /** Submissão do formulário (criar ou editar). */
    fun salvar() {
        val atual = _state.value
        // Revalida tudo antes de submeter (cobre o caso de o usuário clicar
        // no botão sem mexer nos campos depois do último erro).
        val nomeErr = validarNome(atual.nome)
        val descErr = validarDescricao(atual.descricao)
        val prazoErr = validarPrazo(atual.prazo)
        if (nomeErr != null || descErr != null || prazoErr != null || atual.nome.isBlank()) {
            _state.update {
                it.copy(
                    nomeError = nomeErr ?: "Nome é obrigatório.",
                    descricaoError = descErr,
                    prazoError = prazoErr,
                )
            }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(submetendo = true, mensagemErroGeral = null) }
            val resultado = runCatching {
                if (atual.ehEdicao && atual.projetoId != null) {
                    // Edição: precisamos do `Projeto` original para preservar `createdAt`.
                    val entity = dao.getByIdOnce(atual.projetoId)
                        ?: throw IllegalStateException("Projeto sumiu do banco durante edição.")
                    val original = entity.toDomain()
                    editarProjeto(
                        atual = original,
                        nome = atual.nome.trim(),
                        descricao = atual.descricao.trim().ifBlank { null },
                        prazo = atual.prazo,
                        status = atual.status,
                    )
                } else {
                    criarProjeto(
                        nome = atual.nome.trim(),
                        descricao = atual.descricao.trim().ifBlank { null },
                        prazo = atual.prazo,
                    )
                }
            }
            resultado
                .onSuccess {
                    _state.update { it.copy(submetendo = false) }
                    _event.send(Event.Concluido)
                }
                .onFailure { ex ->
                    _state.update {
                        it.copy(
                            submetendo = false,
                            mensagemErroGeral = ex.message ?: "Falha ao salvar o projeto.",
                        )
                    }
                }
        }
    }

    // -------- validações puras (estáticas para facilitar teste futuro) --------

    companion object {
        const val NOME_MAX = 100
        const val DESCRICAO_MAX = 500

        internal fun validarNome(value: String): String? {
            val trimmed = value.trim()
            if (trimmed.isEmpty()) return "Nome é obrigatório."
            if (trimmed.length > NOME_MAX) return "Nome deve ter no máximo $NOME_MAX caracteres."
            return null
        }

        internal fun validarDescricao(value: String): String? {
            if (value.length > DESCRICAO_MAX) return "Descrição deve ter no máximo $DESCRICAO_MAX caracteres."
            return null
        }

        /**
         * Prazo é opcional; quando preenchido, não pode ser anterior a hoje
         * (UTC). Comparação em [LocalDate] usa o dia — não importa o horário.
         */
        internal fun validarPrazo(value: LocalDate?): String? {
            if (value == null) return null
            val hoje = LocalDate.now()
            if (value.isBefore(hoje)) return "Prazo deve ser igual ou posterior a hoje."
            return null
        }
    }
}