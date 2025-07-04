package com.github.dashboardapp.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.dashboardapp.data.local.model.Task
import com.github.dashboardapp.data.local.model.TaskStatus
import com.github.dashboardapp.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: HomeViewModel = hiltViewModel()) {
    val tasks by viewModel.tasks.collectAsStateWithLifecycle()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var isSheetOpen by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    var showStopDialog by remember { mutableStateOf<Task?>(null) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            FloatingActionButton(onClick = { isSheetOpen = true }, shape = CircleShape, containerColor = MaterialTheme.colorScheme.primary) {
                Icon(Icons.Filled.Add, "Add Task", tint = MaterialTheme.colorScheme.background)
            }
        },
        topBar = {
            TopAppBar(
                title = { Text("Today") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
                actions = { TextButton(onClick = { /* TODO */ }) { Text("End Day") } }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            items(tasks, key = { it.id }) { task ->
                TaskCard(
                    task = task,
                    onCheckedChange = { viewModel.onTaskCheckedChanged(task, it) },
                    onPlayPause = { viewModel.onTimerPlayPause(task) },
                    onStop = { showStopDialog = task }
                )
            }
        }

        if (isSheetOpen) {
            AddTaskBottomSheet(
                onDismiss = { isSheetOpen = false },
                onAddTask = { name, groupId ->
                    viewModel.addTask(name, groupId)
                    scope.launch { sheetState.hide() }.invokeOnCompletion { if (!sheetState.isVisible) isSheetOpen = false }
                },
                sheetState = sheetState
            )
        }

        showStopDialog?.let { taskToStop ->
            StopTimerDialog(
                onDismiss = { showStopDialog = null },
                onConfirm = { saveTime ->
                    viewModel.onTimerStop(taskToStop, saveTime)
                    showStopDialog = null
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskBottomSheet(
    onDismiss: () -> Unit,
    onAddTask: (String, String?) -> Unit,
    sheetState: SheetState
) {
    ModalBottomSheet(sheetState = sheetState, onDismissRequest = onDismiss, containerColor = CardBackground) {
        var taskName by remember { mutableStateOf("") }
        var selectedGroupId by remember { mutableStateOf<Int?>(null) }

        Column(
            modifier = Modifier.padding(16.dp).navigationBarsPadding().padding(bottom = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Create New Task", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            OutlinedTextField(
                value = taskName,
                onValueChange = { taskName = it },
                label = { Text("Task Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Text("Select a Group (Optional)", modifier = Modifier.align(Alignment.Start))
            GroupPicker(selectedGroupId = selectedGroupId, onGroupSelected = { selectedGroupId = it })
            Button(
                onClick = { onAddTask(taskName, selectedGroupId?.toString()) },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = taskName.isNotBlank()
            ) { Text("Save Task") }
        }
    }
}

@Composable
fun StopTimerDialog(onDismiss: () -> Unit, onConfirm: (Boolean) -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Stop Timer") },
        text = { Text("Do you want to save the logged time to this task?") },
        confirmButton = { Button(onClick = { onConfirm(true) }) { Text("Save") } },
        dismissButton = { TextButton(onClick = { onConfirm(false) }) { Text("Abort") } }
    )
}

@Composable
fun GroupPicker(selectedGroupId: Int?, onGroupSelected: (Int?) -> Unit) {
    val groups = (1..10).toList()
    LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        items(groups) { groupId ->
            val isSelected = selectedGroupId == groupId
            Box(
                modifier = Modifier.size(48.dp).clip(CircleShape).background(getGradientById(groupId) ?: SkyPulse).clickable { onGroupSelected(if (isSelected) null else groupId) },
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) Icon(Icons.Filled.Check, "Selected", tint = MatteBlack)
            }
        }
    }
}

@Composable
fun TaskCard(task: Task, onCheckedChange: (Boolean) -> Unit, onPlayPause: () -> Unit, onStop: () -> Unit) {
    val gradient = getGradientById(task.groupId?.toIntOrNull())
    val isChecked = task.status == TaskStatus.COMPLETED
    var displayedTime by remember { mutableStateOf(task.timeLoggedInMillis) }

    LaunchedEffect(key1 = task.timerIsRunning, key2 = task.timeLoggedInMillis) {
        if (task.timerIsRunning) {
            val resumeTime = System.currentTimeMillis()
            val timeWhenPaused = task.timeLoggedInMillis
            while (true) {
                val elapsed = System.currentTimeMillis() - resumeTime
                displayedTime = timeWhenPaused + elapsed
                delay(1000)
            }
        } else {
            displayedTime = task.timeLoggedInMillis
        }
    }
    
    fun formatTime(millis: Long): String {
        val totalSeconds = millis / 1000
        val seconds = totalSeconds % 60
        val minutes = (totalSeconds / 60) % 60
        val hours = totalSeconds / 3600
        return if (hours > 0) String.format("%d:%02d:%02d", hours, minutes, seconds) else String.format("%02d:%02d", minutes, seconds)
    }

    Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            if (gradient != null) Box(Modifier.width(6.dp).height(80.dp).background(gradient))
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp).weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Checkbox(checked = isChecked, onCheckedChange = onCheckedChange)
                Column(Modifier.weight(1f)) {
                    Text(text = task.name, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    if (task.timerIsRunning || displayedTime > 0) {
                        // THIS IS THE FIXED LINE
                        Text(text = formatTime(displayedTime), color = if (task.timerIsRunning) Color(0xFFFFC0CB) else TextSecondary, fontSize = 14.sp)
                    } else {
                        Text(text = "Due today", fontSize = 12.sp, color = TextSecondary)
                    }
                }
                Row {
                    IconButton(onClick = onPlayPause, enabled = !isChecked) {
                        Icon(imageVector = if (task.timerIsRunning) Icons.Filled.PauseCircle else Icons.Filled.PlayCircle, contentDescription = "Play/Pause Timer", tint = if (isChecked) IconTint.copy(alpha = 0.5f) else IconTint)
                    }
                    IconButton(onClick = onStop, enabled = (task.timerIsRunning || task.timeLoggedInMillis > 0) && !isChecked) {
                        Icon(imageVector = Icons.Filled.StopCircle, contentDescription = "Stop Timer", tint = if (!task.timerIsRunning && task.timeLoggedInMillis == 0L || isChecked) IconTint.copy(alpha = 0.5f) else IconTint)
                    }
                }
            }
        }
    }
}
