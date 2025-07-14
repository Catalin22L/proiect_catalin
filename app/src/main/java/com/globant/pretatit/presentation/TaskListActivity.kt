package com.globant.pretatit.presentation

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.globant.pretatit.DI
import com.globant.pretatit.R
import com.globant.pretatit.presentation.theme.PretatitTheme
import kotlinx.coroutines.flow.MutableStateFlow
import timber.log.Timber

const val CREATE_TASK_RESULT = "create.task.result"

private val INITIAL_TASK_LIST = mutableListOf<Task>(
    Task("Feed the cat", "It likes to eat mice!", TaskPriority.HIGH),
    Task("Feed the dog", "It likes to eat mice!", TaskPriority.HIGH),
    Task("Feed the hamster", "It likes to eat mice!", TaskPriority.HIGH),
)

class TaskListActivity : ComponentActivity() {

    private val taskList = MutableStateFlow<MutableList<Task>>(INITIAL_TASK_LIST)

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val newTask = result.data?.getSerializableExtra(CREATE_TASK_RESULT) as Task
                taskList.value = taskList.value.toMutableList().apply { add(newTask) }
                Timber.d("result: $newTask")
            }
        }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            PretatitTheme {
                Scaffold(
                    topBar = {
                        TaskListTopAppBar {
                            startCreateActivityForResult()
                        }
                    }
                ) { paddingValue ->

                    ScreenContent(
                        Modifier.padding(paddingValue),
                        createTaskButtonClick = { startCreateActivityForResult() }
                    )
                }
            }
        }
    }

    private fun startCreateActivityForResult() {
        val intent = Intent(
            this@TaskListActivity, CreateTaskActivity::class.java
        )
        launcher.launch(intent)
    }

    @Composable
    private fun ScreenContent(modifier: Modifier, createTaskButtonClick: () -> Unit) {

        val taskList = taskList.collectAsState()

        Box(modifier) {
            if (taskList.value.isEmpty()) {
                ShowEmptyList {
                    createTaskButtonClick()
                }
            } else {
                ShowList()
            }
        }
    }

    @Composable
    private fun ShowEmptyList(createTaskButtonClick: () -> Unit) {
        Timber.d("ShowEmptyList()")
        Button(
            onClick = createTaskButtonClick,
            modifier = Modifier
                .padding(start = 25.dp, top = 25.dp),
        ) {
            Text(
                text = stringResource(R.string.create_task_title),
                fontSize = 20.sp
            )
        }
    }

    @Composable
    private fun ShowList() {
        Timber.d("ShowList()")

        val tasks = taskList.collectAsState().value

        LazyColumn {
            items(tasks.size) { index ->
                ShowTaskElement(taskList.collectAsState().value[index])
            }
        }

        Timber.d("SH taks: %s", DI.taskRepository.getAllTasks().toString())
    }

    @Composable
    private fun ShowTaskElement(task: Task) {
        Timber.d("ShowTaskElement(task : $task)")

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, top = 20.dp),

            elevation = CardDefaults.cardElevation(6.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colorResource(R.color.purple_200))
                    .padding(20.dp)

            ) {
                Text(text = task.title, fontSize = 30.sp)
                Text(text = task.description)
                Text(
                    text = task.taskPriority.name,
                    fontSize = 25.sp,
                    modifier = Modifier.align(
                        Alignment.End
                    )
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TaskListTopAppBar(actionClick: () -> Unit) {
    TopAppBar(
        title = { Text(stringResource(R.string.your_task_list_title)) },
        actions = {
            IconButton(onClick = actionClick) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.task_list_create_task_description)
                )
            }
        })
}
