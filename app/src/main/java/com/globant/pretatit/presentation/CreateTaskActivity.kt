package com.globant.pretatit.presentation

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.DatePicker
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import com.globant.pretatit.DI
import com.globant.pretatit.R
import com.globant.pretatit.components.SimpleDropdown
import com.globant.pretatit.presentation.theme.PretatitTheme
import com.globant.pretatit.presentation.viewmodel.CreateTaskModelFactory
import com.globant.pretatit.presentation.viewmodel.CreateTaskViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

const val EDIT_TASK_EXTRA = "edit_task_extra"

class CreateTaskActivity : ComponentActivity() {

    private lateinit var createTaskViewModel: CreateTaskViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()

        val taskToEdit = intent.getSerializableExtra(EDIT_TASK_EXTRA) as? Task
        createTaskViewModel.loadTask(taskToEdit)

        setContent {
            PretatitTheme {
                Scaffold(
                    topBar = {
                        CreateListTopAppBar(
                            isEditMode = createTaskViewModel.isEditMode,
                            navAction = { finish() },
                            actionAction = {
                                createTaskViewModel.saveOrUpdateTask { success ->
                                    if (success) {
                                        setResult(RESULT_OK)
                                        finish()
                                    } else {
                                        Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        )
                    }
                ) { padding ->
                    ScreenContent(Modifier.padding(padding), createTaskViewModel)
                }
            }
        }
    }

    private fun initViewModel() {
        val viewModelFactory = CreateTaskModelFactory(DI.taskRepository)
        createTaskViewModel = ViewModelProvider(this, viewModelFactory)[CreateTaskViewModel::class.java]
    }
}

@Composable
private fun ScreenContent(modifier: Modifier, viewModel: CreateTaskViewModel) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
    ) {
        OutlinedTextField(
            value = viewModel.title,
            onValueChange = { viewModel.title = it },
            label = { Text(stringResource(R.string.create_task_task_title)) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            value = viewModel.description,
            onValueChange = { viewModel.description = it },
            label = { Text(stringResource(R.string.create_task_task_description)) },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
        )
        Spacer(Modifier.height(24.dp))
        LabeledDropdown(
            label = "Priority",
            options = TaskPriority.toListOfStrings(),
            initialValue = viewModel.priority.name,
            onValueChanged = { viewModel.priority = TaskPriority.getValueByName(it) }
        )
        Spacer(Modifier.height(16.dp))
        LabeledDropdown(
            label = "Category",
            options = TaskCategory.toListOfStrings(),
            initialValue = viewModel.category.name,
            onValueChanged = { viewModel.category = TaskCategory.getValueByName(it) }
        )
        Spacer(Modifier.height(24.dp))
        DatePickerView(
            selectedDate = viewModel.dueDate,
            onDateSelected = { viewModel.dueDate = it }
        )
    }
}

@Composable
private fun LabeledDropdown(label: String, options: List<String>, initialValue: String, onValueChanged: (String) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge)
        SimpleDropdown(options = options, initialValue = initialValue, onValueChanged = onValueChanged)
    }
}

@Composable
fun DatePickerView(selectedDate: Long?, onDateSelected: (Long) -> Unit) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    selectedDate?.let { calendar.timeInMillis = it }

    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val newDate = Calendar.getInstance().apply { set(year, month, dayOfMonth) }
            onDateSelected(newDate.timeInMillis)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text("Due Date:", style = MaterialTheme.typography.bodyLarge)
        Button(onClick = { datePickerDialog.show() }) {
            Text(
                selectedDate?.let {
                    SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(it)
                } ?: "Select Date"
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateListTopAppBar(isEditMode: Boolean, navAction: () -> Unit, actionAction: () -> Unit) {
    val title = if (isEditMode) "Edit Task" else stringResource(R.string.create_task_title)
    TopAppBar(
        title = { Text(title) },
        navigationIcon = {
            IconButton(onClick = navAction) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.task_list_create_task_back_description)
                )
            }
        },
        actions = {
            IconButton(onClick = actionAction) {
                Icon(
                    imageVector = Icons.Default.Done,
                    contentDescription = stringResource(R.string.task_list_save_task_description)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimary
        )
    )
}