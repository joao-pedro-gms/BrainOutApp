package com.joaopedrogms.brainoutapp.domain.usecase

import com.joaopedrogms.brainoutapp.data.local.UuidV7
import com.joaopedrogms.brainoutapp.domain.exception.RegrasNegocioException
import com.joaopedrogms.brainoutapp.domain.model.PrioridadeTarefa
import com.joaopedrogms.brainoutapp.domain.model.StatusTarefa
import com.joaopedrogms.brainoutapp.domain.model.Tarefa
import com.joaopedrogms.brainoutapp.domain.repository.ProjetoRepository
import com.joaopedrogms.brainoutapp.domain.repository.TarefaRepository
import java.time.Instant
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

/**
 * Cria uma nova tarefa.
 *
 * Validações:
 *  - `titulo`, `projetoId` etc. são responsabilidade da UI (form).
 *  - **RN03 (issue #11):** `prazo` da tarefa, se preenchido, deve ser
 *    `≤` prazo do projeto. Se o projeto não tem prazo, qualquer prazo
 *    é aceito. Se o projeto não existe (id órfão), a regra é pulada
 *    silenciosamente — a FK do SQLite já vai barrar o insert no nível
 *    do banco e a `SQLiteConstraintException` propagada vira mensagem
 *    amigável na próxima camada (fora de escopo desta lane).
 *
 * **Padrões:**
 *  - `status` inicial = [StatusTarefa.ABERTA].
 *  - `prioridade` inicial = [PrioridadeTarefa.BAIXA] (sobrescrevível pela UI).
 *  - `dependencias` default = `emptyList()`.
 */
class CriarTarefaUseCase @Inject constructor(
    private val repository: TarefaRepository,
    private val projetoRepository: ProjetoRepository,
) {
    /**
     * @param projetoId   FK para o projeto pai (obrigatório, validado na UI).
     * @param titulo      título (validado pela UI).
     * @param descricao   descrição opcional.
     * @param prazo       prazo opcional; `null` quando não informado.
     * @param prioridade  prioridade; default [PrioridadeTarefa.BAIXA].
     * @param responsavel nome do responsável (opcional).
     * @param dependencias ids de tarefas das quais esta depende (opcional).
     * @return a [Tarefa] recém-criada.
     * @throws RegrasNegocioException se [prazo] ultrapassar o prazo do projeto
     *         (RN03). Mensagem R10: `"Prazo da tarefa (DD/MM) ultrapassa
     *         o prazo do projeto (DD/MM)."`
     */
    suspend operator fun invoke(
        projetoId: String,
        titulo: String,
        descricao: String?,
        prazo: LocalDate?,
        prioridade: PrioridadeTarefa = PrioridadeTarefa.BAIXA,
        responsavel: String? = null,
        dependencias: List<String> = emptyList(),
    ): Tarefa {
        validarPrazoContraProjeto(prazo, projetoId, projetoRepository)

        val agora = Instant.now()
        val tarefa = Tarefa(
            id = UuidV7.novo(),
            projetoId = projetoId,
            titulo = titulo,
            descricao = descricao,
            prazo = prazo,
            status = StatusTarefa.ABERTA,
            prioridade = prioridade,
            responsavel = responsavel,
            createdAt = agora,
            updatedAt = agora,
            dependencias = dependencias,
        )
        repository.insert(tarefa)
        return tarefa
    }
}

/**
 * Implementação de RN03 compartilhada entre [CriarTarefaUseCase] e
 * [EditarTarefaUseCase]. Não é `internal` por design — se uma lane
 * futura precisar validar (ex.: ao mover tarefa entre projetos), pode
 * chamar diretamente.
 *
 * Regras:
 *  - Se `prazo == null`, tarefa sem prazo: regra não se aplica.
 *  - Se projeto não existe (id órfão), regra não se aplica — quem
 *    cuida é a FK do SQLite.
 *  - Se `projeto.prazo == null`, projeto sem prazo: tarefa pode ter
 *    qualquer prazo (inclusive antes do projeto existir — não há
 *    restrição).
 *  - Caso contrário, `prazo da tarefa > prazo do projeto` viola e
 *    lança [RegrasNegocioException] com mensagem R10.
 */
suspend fun validarPrazoContraProjeto(
    prazo: LocalDate?,
    projetoId: String,
    projetoRepository: ProjetoRepository,
) {
    if (prazo == null) return
    val projeto = projetoRepository.getByIdOnce(projetoId) ?: return
    val prazoProjeto = projeto.prazo ?: return
    if (prazo.isAfter(prazoProjeto)) {
        val fmt = DateTimeFormatter.ofPattern("dd/MM", Locale("pt", "BR"))
        throw RegrasNegocioException(
            "Prazo da tarefa (${prazo.format(fmt)}) ultrapassa o prazo do projeto (${prazoProjeto.format(fmt)}).",
        )
    }
}
