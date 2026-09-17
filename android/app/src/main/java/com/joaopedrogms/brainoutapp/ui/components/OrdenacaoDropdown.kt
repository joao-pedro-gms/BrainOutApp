package com.joaopedrogms.brainoutapp.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Dropdown de ordenação (Material 3 `ExposedDropdownMenuBox`) usado nas
 * listas de **Projetos** e **Tarefas**.
 *
 * - Recebe a lista de rótulos canônicos (`opcoes`) e o índice selecionado.
 * - Renderiza como `OutlinedTextField` read-only com caret — visualmente
 *   consistente com o resto do form (mesmo padrão de `TarefaFormScreen`).
 * - Não tem papel semântico próprio; o conteúdo da opção selecionada é
 *   lido pelo `TalkBack` via `Text`.
 *
 * Reaproveitável: o caller passa os `opcoes` (ex.: `["Nome", "Prazo",
 * "Criação"]`) e o índice atual — o componente não conhece o domínio.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> OrdenacaoDropdown(
    label: String,
    opcoes: List<T>,
    selecionado: T,
    onSelecionar: (T) -> Unit,
    rotulo: (T) -> String,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
    ) {
        OutlinedTextField(
            value = rotulo(selecionado),
            onValueChange = { /* read-only */ },
            readOnly = true,
            label = { Text(label) },
            leadingIcon = {
                Icon(Icons.Filled.Sort, contentDescription = null)
            },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Filled.ArrowDropDown,
                    contentDescription = "Abrir opções de ordenação",
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            opcoes.forEach { opcao ->
                DropdownMenuItem(
                    text = { Text(rotulo(opcao)) },
                    onClick = {
                        onSelecionar(opcao)
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                )
            }
        }
    }
}
