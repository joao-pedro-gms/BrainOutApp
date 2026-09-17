package com.joaopedrogms.brainoutapp.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joaopedrogms.brainoutapp.data.local.dao.TarefaDao
import com.joaopedrogms.brainoutapp.data.local.entity.TarefaEntity
import com.joaopedrogms.brainoutapp.domain.model.PrioridadeTarefa
import com.joaopedrogms.brainoutapp.domain.model.Projeto
import com.joaopedrogms.brainoutapp.domain.model.StatusTarefa
import com.joaopedrogms.brainoutapp.domain.model.Tarefa
import com.joaopedrogms.brainoutapp.domain.model.toDomain
import com.joaopedrogms.brainoutapp.domain.repository.ProjetoRepository
import com.joaopedrogms.brainoutapp.domain.usecase.CriarTarefaUseCase
import com.joaopedrogms.brainoutapp.domain.usecase.EditarTarefaUseCase
import com.joaopedrogms.brainoutapp.ui.navigation.Destinations
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

/**
 * ViewModel do **formulário** de tarefa (criar e editar).
 *
 * Lê dois argumentos da rota:
 *  - `projetoId` (opcional) — quando criando, pré-seleciona o projeto
 *    pai. Quando ausente e criando, força o usuário a escolher.
 *  - `tarefaId` (opcional) — quando preenchido, entra em modo edição e
 *    carrega a tarefa do DAO.
 *
 * Mantém [FormState] com valores atuais, erros por campo e flag
 * `submetendo`. Lista os projetos ativos para alimentar o dropdown
 * (seletor de projeto pai).
 *
 * **Validações (issue #10):**
 *  - `titulo`: obrigatório, não vazio após trim, ≤ 100 caracteres.
 *  - `descricao`: opcional, ≤ 500 caracteres.
 *  - `projetoId`: obrigatório (FK). Se vazio no submit, erro.
 *  - `prazo`: opcional; se preenchido, **≥ hoje** (UTC).
 *
 * Regras RN01-RN03 (ciclo 3) continuam fora — esta classe aplica só as
 * validações de input declaradas na issue #10.
 */
@HiltViewModel
class TarefaFormViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val tarefaDao: TarefaDao,
    private val projetoRepository: ProjetoRepository,
    private val criarTarefa: CriarTarefaUseCase,
    private val editarTarefa: EditarTarefaUseCase,
) : ViewModel() {

    /** Evento one-shot consumido pela UI (snackbar / navegação). */
    sealed interface Event {
        data object Concluido : Event
    }

    /** Estado do formulário. */
    data class FormState(
        val carregando: Boolean = false,
        val ehEdicao: Boolean = false,
        val tarefaId: String? = null,
        val projetoId: String? = null,
        val titulo: String = "",
        val descricao: String = "",
        val prazo: LocalDate? = null,
        val status: StatusTarefa = StatusTarefa.ABERTA,
        val prioridade: PrioridadeTarefa = PrioridadeTarefa.BAIXA,
        val responsavel: String = "",
        val tituloError: String? = null,
        val descricaoError: String? = null,
        val projetoError: String? = null,
        val prazoError: String? = null,
        val submetendo: Boolean = false,
        val mensagemErroGeral: String? = null,
    ) {
        val podeSubmeter: Boolean
            get() = !submetendo &&
                tituloError == null && descricaoError == null &&
                projetoError == null && prazoError == null &&
                titulo.isNotBlank() && projetoId != null
    }

    private val _state = MutableStateFlow(FormState(carregando = true))
    val state: StateFlow<FormState> = _state.asStateFlow()

    /** Lista de projetos ativos (para o dropdown de projeto pai). */
    val projetos: StateFlow<UiState<List<Projeto>>> = projetoRepository.getAll()
        .map<List<Projeto>, UiState<List<Projeto>>> { UiState.Success(it) }
        .catch { ex -> emit(UiState.Error(ex.message ?: "Erro ao listar projetos")) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = UiState.Loading,
        )

    private val _event = Channel<Event>(Channel.BUFFERED)
    val event = _event.receiveAsFlow()

    init {
        val projetoIdInicial = savedStateHandle
            .get<String>(Destinations.CRIACAO_TAREFA_PROJETO_ARG)
            ?.takeIf { it.isNotBlank() }
        val tarefaId = savedStateHandle
            .get<String>(Destinations.CRIACAO_TAREFA_ARG)
            ?.takeIf { it.isNotBlank() }

        _state.update { it.copy(projetoId = projetoIdInicial) }

        if (tarefaId == null) {
            _state.update { it.copy(carregando = false) }
        } else {
            carregarExistente(tarefaId)
        }
    }

    private fun carregarExistente(id: String) {
        viewModelScope.launch {
            val entity: TarefaEntity? = tarefaDao.getByIdOnce(id)
            if (entity == null) {
                _state.update {
                    it.copy(
                        carregando = false,
                        ehEdicao = false,
                        mensagemErroGeral = "Tarefa não encontrada (id=$id).",
                    )
                }
                return@launch
            }
            val tarefa: Tarefa = entity.toDomain()
            _state.update {
                it.copy(
                    carregando = false,
                    ehEdicao = true,
                    tarefaId = tarefa.id,
                    projetoId = tarefa.projetoId,
                    titulo = tarefa.titulo,
                    descricao = tarefa.descricao.orEmpty(),
                    prazo = tarefa.prazo,
                    status = tarefa.status,
                    prioridade = tarefa.prioridade,
                    responsavel = tarefa.responsavel.orEmpty(),
                )
            }
        }
    }

    // -------- entradas do formulário (chamadas pela UI a cada mudança) --------

    fun onTituloChange(value: String) {
        _state.update { it.copy(titulo = value, tituloError = validarTitulo(value)) }
    }

    fun onDescricaoChange(value: String) {
        _state.update { it.copy(descricao = value, descricaoError = validarDescricao(value)) }
    }

    fun onProjetoChange(value: String?) {
        _state.update { it.copy(projetoId = value, projetoError = validarProjeto(value)) }
    }

    fun onPrazoChange(value: LocalDate?) {
        _state.update { it.copy(prazo = value, prazoError = validarPrazo(value)) }
    }

    fun onStatusChange(value: StatusTarefa) {
        _state.update { it.copy(status = value) }
    }

    fun onPrioridadeChange(value: PrioridadeTarefa) {
        _state.update { it.copy(prioridade = value) }
    }

    fun onResponsavelChange(value: String) {
        // Sem limite rígido nesta lane; o campo é livre. Validações mais
        // finas (ex.: tamanho, normalização) entram na lane de RN.
        _state.update { it.copy(responsavel = value) }
    }

    /** Submissão do formulário (criar ou editar). */
    fun salvar() {
        val atual = _state.value
        // Revalida tudo antes de submeter (cobre o caso de o usuário clicar
        // no botão sem mexer nos campos depois do último erro).
        val tituloErr = validarTitulo(atual.titulo)
        val descErr = validarDescricao(atual.descricao)
        val projetoErr = validarProjeto(atual.projetoId)
        val prazoErr = validarPrazo(atual.prazo)
        if (tituloErr != null || descErr != null || projetoErr != null || prazoErr != null ||
            atual.titulo.isBlank() || atual.projetoId == null
        ) {
            _state.update {
                it.copy(
                    tituloError = tituloErr ?: "Título é obrigatório.",
                    descricaoError = descErr,
                    projetoError = projetoErr ?: "Selecione um projeto.",
                    prazoError = prazoErr,
                )
            }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(submetendo = true, mensagemErroGeral = null) }
            val resultado = runCatching {
                if (atual.ehEdicao && atual.tarefaId != null) {
                    // Edição: precisamos da `Tarefa` original para preservar
                    // `projetoId`, `createdAt`.
                    val entity = tarefaDao.getByIdOnce(atual.tarefaId)
                        ?: throw IllegalStateException("Tarefa sumiu do banco durante edição.")
                    val original = entity.toDomain()
                    editarTarefa(
                        atual = original,
                        titulo = atual.titulo.trim(),
                        descricao = atual.descricao.trim().ifBlank { null },
                        prazo = atual.prazo,
                        status = atual.status,
                        prioridade = atual.prioridade,
                        responsavel = atual.responsavel.trim().ifBlank { null },
                    )
                } else {
                    criarTarefa(
                        projetoId = atual.projetoId!!,
                        titulo = atual.titulo.trim(),
                        descricao = atual.descricao.trim().ifBlank { null },
                        prazo = atual.prazo,
                        prioridade = atual.prioridade,
                        responsavel = atual.responsavel.trim().ifBlank { null },
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
                            mensagemErroGeral = ex.message ?: "Falha ao salvar a tarefa.",
                        )
                    }
                }
        }
    }

    // -------- validações puras (estáticas para facilitar teste futuro) --------

    companion object {
        const val TITULO_MAX = 100
        const val DESCRICAO_MAX = 500

        internal fun validarTitulo(value: String): String? {
            val trimmed = value.trim()
            if (trimmed.isEmpty()) return "Título é obrigatório."
            if (trimmed.length > TITULO_MAX) return "Título deve ter no máximo $TITULO_MAX caracteres."
            return null
        }

        internal fun validarDescricao(value: String): String? {
            if (value.length > DESCRICAO_MAX) return "Descrição deve ter no máximo $DESCRICAO_MAX caracteres."
            return null
        }

        internal fun validarProjeto(value: String?): String? {
            if (value.isNullOrBlank()) return "Selecione um projeto."
            return null
        }

        /**
         * Prazo é opcional; quando preenchido, não pode ser anterior a hoje
         * (UTC). Comparação em [LocalDate] usa o dia — não importa o horário.
         *
         > **RN03** ("prazo da tarefa ≤ prazo do projeto") é responsabilidade
         > > da lane de regras de negócio. Aqui só validamos "≥ hoje".
         */
        internal fun validarPrazo(value: LocalDate?): String? {
            if (value == null) return null
            val hoje = LocalDate.now()
            if (value.isBefore(hoje)) return "Prazo deve ser igual ou posterior a hoje."
            return null
        }
    }
}
