package com.globant.pretatit.presentation

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import com.globant.pretatit.DI
import com.globant.pretatit.R
import com.globant.pretatit.components.SimpleDropdown
import com.globant.pretatit.presentation.theme.PretatitTheme
import com.globant.pretatit.presentation.viewmodel.TaskListViewModel
import com.globant.pretatit.presentation.viewmodel.TaskViewModelFactory
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class TaskListActivity : ComponentActivity() {

    private lateinit var viewModel: TaskListViewModel

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                viewModel.init()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()

        setContent {
            PretatitTheme {
                val snackbarHostState = remember { SnackbarHostState() }
                val scope = rememberCoroutineScope()
                val taskToDelete by viewModel.showDeleteConfirmation.collectAsState()

                Scaffold(
                    snackbarHost = { SnackbarHost(snackbarHostState) },
                    topBar = { TaskListTopAppBar { launcher.launch(Intent(this, CreateTaskActivity::class.java)) } },
                    containerColor = MaterialTheme.colorScheme.background
                ) { paddingValue ->
                    ScreenContent(
                        modifier = Modifier.padding(paddingValue),
                        viewModel = viewModel
                    ) { task ->
                        val intent = Intent(this, CreateTaskActivity::class.java).apply {
                            putExtra(EDIT_TASK_EXTRA, task)
                        }
                        launcher.launch(intent)
                    }

                    if (taskToDelete != null) {
                        DeleteConfirmationDialog(
                            onConfirm = {
                                viewModel.confirmTaskDeletion()
                                scope.launch { snackbarHostState.showSnackbar("Task deleted successfully!") }
                            },
                            onDismiss = { viewModel.cancelTaskDeletion() }
                        )
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.init()
    }

    private fun initViewModel() {
        val viewModelFactory = TaskViewModelFactory(DI.taskRepository)
        viewModel = ViewModelProvider(this, viewModelFactory)[TaskListViewModel::class.java]
    }
}

@Composable
private fun ScreenContent(
    modifier: Modifier,
    viewModel: TaskListViewModel,
    onEditClick: (Task) -> Unit
) {
    val taskList by viewModel.taskList.collectAsState(initial = emptyList())

    Column(modifier.fillMaxSize()) {
        ControlsCard(viewModel)
        if (taskList.isEmpty()) {
            EmptyListMessage(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                items(taskList, key = { it.id }) { task ->
                    ShowTaskElement(
                        task = task,
                        onCheckedChange = { isChecked -> viewModel.onTaskCheckedChange(task, isChecked) },
                        onDeleteClick = { viewModel.onDeleteTaskClicked(task) },
                        onEditClick = { onEditClick(task) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
private fun ControlsCard(viewModel: TaskListViewModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Sort by:", style = MaterialTheme.typography.titleMedium)
                OutlinedButton(onClick = { viewModel.toggleSortCriteria() }) { Text("Priority / Date") }
                OutlinedButton(onClick = { viewModel.toggleSortOrder() }) { Text("ASC / DESC") }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Filter by:", style = MaterialTheme.typography.titleMedium)
                val categories = listOf("ALL") + TaskCategory.toListOfStrings()
                SimpleDropdown(options = categories, onValueChanged = { viewModel.filterByCategory(it) })
            }
        }
    }
}

@Composable
private fun ShowTaskElement(
    task: Task,
    onCheckedChange: (Boolean) -> Unit,
    onDeleteClick: () -> Unit,
    onEditClick: () -> Unit
) {
    val priorityColor = when (task.taskPriority) {
        TaskPriority.URGENT -> Color(0xFFD32F2F)
        TaskPriority.HIGH -> Color(0xFFF57C00)
        TaskPriority.MEDIUM -> Color(0xFFFFC107)
        TaskPriority.LOW -> Color(0xFF8BC34A)
        TaskPriority.NONE -> Color.Gray
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .width(5.dp)
                    .fillMaxHeight()
                    .background(priorityColor)
            )

            Row(
                modifier = Modifier
                    .padding(vertical = 8.dp, horizontal = 12.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = task.isDone,
                    onCheckedChange = onCheckedChange,
                    colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary)
                )
                Column(modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp)) {
                    Text(
                        text = task.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        textDecoration = if (task.isDone) TextDecoration.LineThrough else null,
                        color = if (task.isDone) Color.Gray else MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = task.description,
                        fontSize = 14.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        textDecoration = if (task.isDone) TextDecoration.LineThrough else null,
                        color = if (task.isDone) Color.Gray else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        task.dueDate?.let {
                            Text(
                                text = "Due: ${SimpleDateFormat("dd MMM", Locale.getDefault()).format(it)}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        Text(
                            text = task.category.name,
                            fontSize = 12.sp,
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                Column {
                    IconButton(onClick = onEditClick, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Task", tint = Color.Gray)
                    }
                    IconButton(onClick = onDeleteClick, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete Task", tint = Color.Gray)
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyListMessage(modifier: Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text("No tasks to display. Tap the '+' button to add one.", fontSize = 18.sp, color = Color.Gray)
    }
}

@Composable
fun DeleteConfirmationDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delete Task") },
        text = { Text("Are you sure you want to permanently delete this task?") },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) { Text("Delete") }
        },
        dismissButton = { OutlinedButton(onClick = onDismiss) { Text("Cancel") } }
    )
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
                    contentDescription = stringResource(R.string.task_list_create_task_description),
                    modifier = Modifier.size(28.dp)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimary
        )
    )
}