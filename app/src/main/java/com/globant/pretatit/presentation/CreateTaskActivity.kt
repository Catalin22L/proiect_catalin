package com.globant.pretatit.presentation

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.globant.pretatit.DI
import com.globant.pretatit.R
import com.globant.pretatit.components.SimpleDropdown
import com.globant.pretatit.domain.CreateTaskUseCase.Params
import com.globant.pretatit.domain.CreateTaskUseCaseImpl
import com.globant.pretatit.presentation.theme.PretatitTheme

class CreateTaskActivity : ComponentActivity() {

    private var task: Task = Task("", "", TaskPriority.NONE)
    private val createTaskUseCase = CreateTaskUseCaseImpl(DI.taskRepository)

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            PretatitTheme {
                Scaffold(
                    topBar = {
                        CreateListTopAppBar(navAction = {
                            navigateBack()
                        }, actionAction = {
                            navigateWithResult()
                        })
                    }) { padding ->
                    ScreenContent(Modifier.padding(padding))
                }
            }
        }
    }

    private fun navigateBack() {
        finish()
    }

    private fun navigateWithResult() {

        createTaskUseCase.invoke(Params(task))

        val result = Intent()
        result.putExtra(CREATE_TASK_RESULT, task)
        setResult(RESULT_OK, result)

        finish()
    }

    @Composable
    private fun ScreenContent(modifier: Modifier) {
        Column(modifier) {
            TextAndEditText(
                stringResource(R.string.create_task_task_title),
                stringResource(R.string.create_task_enter_task)
            ) { newValue ->
                task = task.copy(title = newValue)
            }

            Spacer(Modifier.size(20.dp))

            TextAndEditText(
                stringResource(R.string.create_task_task_description),
                stringResource(R.string.create_task_task_enter_description)
            )
            { newValue ->
                task = task.copy(description = newValue)
            }

            Spacer(Modifier.size(20.dp))

            ShowTaskPriority()
        }
    }

    @Composable
    private fun TextAndEditText(
        textString: String,
        editTextLabel: String,
        onValueChanged: (String) -> Unit
    ) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(Modifier.size(20.dp))
            Text(text = textString, fontSize = 20.sp)
            Spacer(Modifier.size(20.dp))
            EditText(label = editTextLabel, { newValue -> onValueChanged(newValue) })
            Spacer(Modifier.size(20.dp))
        }
    }

    @Composable
    private fun ShowTaskPriority() {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(Modifier.size(20.dp))
            Text(stringResource(R.string.create_task_task_priority), fontSize = 20.sp)
            Spacer(Modifier.size(20.dp))
            SimpleDropdown(TaskPriority.toListOfStrings()) { newValue ->
                val newTaskPriority = TaskPriority.getValueByName(newValue)
                task = task.copy(taskPriority = newTaskPriority)
            }
        }
    }

    @Composable
    private fun EditText(label: String, onValueChanged: (String) -> Unit) {
        var text: String by remember { mutableStateOf("") }

        OutlinedTextField(
            singleLine = true,
            value = text,
            onValueChange = {
                text = it
                onValueChanged(text)
            },
            label = { Text(label, fontSize = 20.sp) }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateListTopAppBar(navAction: () -> Unit, actionAction: () -> Unit) {
    TopAppBar(title = {
        Text(stringResource(R.string.create_task_title))
    }, navigationIcon = {
        IconButton(onClick = navAction) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.task_list_create_task_back_description)
            )
        }
    }, actions = {
        IconButton(onClick = actionAction) {
            Icon(
                imageVector = Icons.Default.Done,
                contentDescription = stringResource(R.string.task_list_save_task_description)
            )
        }
    })
}