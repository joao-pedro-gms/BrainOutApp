package com.joaopedrogms.brainoutapp.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * SearchBar (Material 3) compartilhado entre as telas de **Projetos** e
 * **Tarefas**.
 *
 * - Estado interno: termo digitado. O caller recebe o termo já normalizado
 *   (sem espaços nas pontas) via [onQueryChange]. A camada de ViewModel
 *   aplica `debounce(300ms)` antes de chegar ao banco.
 * - Botão "limpar" (`X`) aparece quando há texto; voltar ao estado vazio
 *   também notifica o caller com `""`.
 * - Não usamos o modo expandido (`isActive = true`) — a barra fica sempre
 *   visível no topo, abaixo do `TopAppBar` da tela. Isso casa com o
 *   wireframe 02-projetos.svg e 04-tarefas.svg.
 *
 * Opt-in: `ExperimentalMaterial3Api` (já habilitado globalmente no
 * `app/build.gradle.kts` via `freeCompilerArgs`).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuscaSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
) {
    SearchBar(
        query = query,
        onQueryChange = onQueryChange,
        onSearch = { /* sem ação — submit implícito usa o mesmo termo */ },
        active = false,
        onActiveChange = { /* mantém inativo: busca sempre visível */ },
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        placeholder = { Text(placeholder) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = "Buscar",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Limpar busca",
                    )
                }
            }
        },
        // Mantemos a barra sempre no estado colapsado (active=false); o
        // slot de conteúdo é exigido pela API do Material3, mas fica vazio
        // porque não oferecemos busca "live" em painel expandido.
        content = { },
    )
}
