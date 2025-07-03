package com.github.dashboardapp.ui.home
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.dashboardapp.data.local.model.Task
import com.github.dashboardapp.ui.theme.*
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: HomeViewModel = hiltViewModel()) {
    val tasks by viewModel.tasks.collectAsStateWithLifecycle()
    var showAddTaskDialog by remember { mutableStateOf(false) }
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddTaskDialog = true }, shape = CircleShape) {
                Icon(Icons.Filled.Add, "Add Task")
            }
        },
        topBar = {
            TopAppBar(
                title = { Text("Today") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
                actions = { TextButton(onClick = {}) { Text("End Day") } }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            items(tasks, key = { it.id }) { task ->
                TaskCard(task = task, onCheckedChange = { viewModel.onTaskCheckedChanged(task, it) })
            }
        }
    }
    if (showAddTaskDialog) {
        AlertDialog(
            onDismissRequest = { showAddTaskDialog = false },
            title = { Text("Add New Task") },
            text = { Text("This is a placeholder. The full creation modal will be implemented here.") },
            confirmButton = {
                Button(onClick = {
                    val randomGroup = (1..10).random()
                    viewModel.addTask(Task(name = "Sample Task", groupId = randomGroup.toString(), timerEnabled = randomGroup % 2 == 0))
                    showAddTaskDialog = false
                }) { Text("Add Sample") }
            }
        )
    }
}
@Composable
fun TaskCard(task: Task, onCheckedChange: (Boolean) -> Unit) {
    val gradient = getGradientById(task.groupId?.toIntOrNull())
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            if (gradient != null) {
                Box(Modifier.width(6.dp).height(60.dp).background(gradient))
            }
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp).weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Checkbox(checked = false, onCheckedChange = onCheckedChange)
                Column(Modifier.weight(1f)) {
                    Text(text = task.name, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    Text(text = "Due today", fontSize = 12.sp, color = TextSecondary)
                }
                if (task.timerEnabled) {
                    Icon(Icons.Outlined.Timer, "Timer", tint = IconTint)
                }
            }
        }
    }
}
