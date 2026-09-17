package com.joaopedrogms.brainoutapp.viewmodel

/**
 * Estado de UI reutilizável para a lane CRUD Projetos.
 *
 * Pequeno wrapper sealed — uma lane posterior (UiState dedicada) pode
 * refinar com Loading/Empty/Error granulares por feature. Para já:
 *  - [Loading]             → primeira leitura / após ação explícita.
 *  - [Success]<T>          → dado pronto para a UI renderizar.
 *  - [Error]               → erro recuperável (mensagem amigável R10).
 *
 * Uso: `StateFlow<UiState<List<Projeto>>>` no `ProjetoListViewModel`,
 * `StateFlow<UiState<Projeto?>>` no `ProjetoDetailViewModel`.
 */
sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
}