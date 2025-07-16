package com.globant.pretatit.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun SimpleDropdown(
    options: List<String>,
    initialValue: String = options.firstOrNull() ?: "",
    onValueChanged: (String) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }
    var selectedValue by remember(initialValue) { mutableStateOf(initialValue) }

    Box(
        modifier = Modifier.wrapContentSize(Alignment.TopEnd)
    ) {
        TextButton(onClick = { isExpanded = !isExpanded }) {
            Text(selectedValue)
        }

        DropdownMenu(expanded = isExpanded, onDismissRequest = { isExpanded = false }) {
            options.forEach {
                DropdownMenuItem(
                    text = { Text(it) },
                    onClick = {
                        isExpanded = false
                        selectedValue = it
                        onValueChanged(it)
                    })
            }
        }
    }
}