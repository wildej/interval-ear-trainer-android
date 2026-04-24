package com.example.intervaltrainer.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.intervaltrainer.domain.Interval

private const val ROUTE_SETUP = "setup"
private const val ROUTE_TRAINING = "training"
private const val ROUTE_SUMMARY = "summary"

@Composable
fun IntervalTrainerApp() {
    val navController = rememberNavController()
    val vm: TrainingViewModel = viewModel()
    AppNavHost(navController = navController, vm = vm)
}

@Composable
private fun AppNavHost(navController: NavHostController, vm: TrainingViewModel) {
    NavHost(navController = navController, startDestination = ROUTE_SETUP) {
        composable(ROUTE_SETUP) {
            SessionSetupScreen(vm = vm, onStart = {
                vm.startSession()
                navController.navigate(ROUTE_TRAINING)
            })
        }
        composable(ROUTE_TRAINING) {
            TrainingScreen(
                vm = vm,
                onOpenSummary = { navController.navigate(ROUTE_SUMMARY) }
            )
        }
        composable(ROUTE_SUMMARY) {
            SessionSummaryScreen(
                vm = vm,
                onRestart = {
                    navController.popBackStack(ROUTE_SETUP, inclusive = false)
                },
                onRetake = {
                    vm.startSession()
                    navController.popBackStack(ROUTE_TRAINING, inclusive = false)
                }
            )
        }
    }
}

@Composable
private fun SessionSetupScreen(vm: TrainingViewModel, onStart: () -> Unit) {
    val state by vm.uiState.collectAsState()
    Scaffold { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            Text("Выберите интервалы", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(12.dp))
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(Interval.entries) { interval ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = state.selectedIntervals.contains(interval),
                            onCheckedChange = { vm.toggleInterval(interval) }
                        )
                        Text("${interval.shortName} - ${interval.displayName}")
                    }
                }
            }
            Button(
                onClick = onStart,
                enabled = state.selectedIntervals.isNotEmpty(),
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics { contentDescription = "start_training_button" }
            ) {
                Text("Начать тренировку")
            }
        }
    }
}

@Composable
private fun TrainingScreen(vm: TrainingViewModel, onOpenSummary: () -> Unit) {
    val state by vm.uiState.collectAsState()
    val question = state.currentQuestion
    Scaffold { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text("Тренировка", style = MaterialTheme.typography.headlineSmall)
            Text("Верных: ${state.stats.correct}/${state.stats.total}")
            Button(
                onClick = { vm.playCurrentQuestion() },
                enabled = question != null && !state.isPlaying,
                modifier = Modifier.semantics { contentDescription = "listen_again_button" }
            ) {
                Text(if (state.isPlaying) "Воспроизведение..." else "Слушать / Еще раз")
            }
            Text("Выберите интервал:")
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(question?.options.orEmpty()) { option ->
                    Button(
                        onClick = { vm.selectAnswer(option) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp)
                    ) {
                        Text("${option.shortName} - ${option.displayName}")
                    }
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { vm.checkAnswer() },
                    enabled = state.selectedAnswer != null && !state.isAnswerChecked,
                    modifier = Modifier.weight(1f)
                ) { Text("Проверить") }
                Button(
                    onClick = { vm.nextQuestion() },
                    enabled = state.isAnswerChecked,
                    modifier = Modifier.weight(1f)
                ) { Text("Следующий") }
            }
            if (state.isAnswerChecked && question != null) {
                val feedback = if (state.isAnswerCorrect) "Верно" else "Неверно"
                Text("$feedback. Правильный: ${question.interval.shortName}")
            }
            Button(
                onClick = onOpenSummary,
                modifier = Modifier.fillMaxWidth()
            ) { Text("Завершить сессию") }
        }
    }
}

@Composable
private fun SessionSummaryScreen(vm: TrainingViewModel, onRestart: () -> Unit, onRetake: () -> Unit) {
    val state by vm.uiState.collectAsState()
    Scaffold { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Результаты", style = MaterialTheme.typography.headlineSmall)
            Text("Верных ответов: ${state.stats.correct}")
            Text("Всего ответов: ${state.stats.total}")
            Text("Точность: ${state.stats.accuracyPercent}%")
            Button(onClick = onRetake, modifier = Modifier.fillMaxWidth()) {
                Text("Новая сессия")
            }
            Button(onClick = onRestart, modifier = Modifier.fillMaxWidth()) {
                Text("Изменить набор")
            }
        }
    }
}
