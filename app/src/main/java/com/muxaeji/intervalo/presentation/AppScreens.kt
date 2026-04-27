package com.muxaeji.intervalo.presentation

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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.TextButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.muxaeji.intervalo.R
import com.muxaeji.intervalo.domain.Interval

private const val ROUTE_SETUP = "setup"
private const val ROUTE_TRAINING = "training"
private const val ROUTE_SUMMARY = "summary"
private const val ROUTE_MODE_SELECT = "mode_select"
private const val ROUTE_SHOW = "show"
private const val ROUTE_GAME = "game"

@Composable
fun IntervalTrainerApp() {
    val navController = rememberNavController()
    val vm: TrainingViewModel = viewModel()
    AppNavHost(navController = navController, vm = vm)
}

@Composable
private fun AppNavHost(navController: NavHostController, vm: TrainingViewModel) {
    val state by vm.uiState.collectAsState()
    NavHost(navController = navController, startDestination = ROUTE_MODE_SELECT) {
        composable(ROUTE_MODE_SELECT) {
            ModeSelectScreen(
                gameBestRounds = state.gameBestRounds,
                onOpenTraining = { navController.navigate(ROUTE_SETUP) },
                onOpenShowMode = { navController.navigate(ROUTE_SHOW) },
                onOpenGameMode = {
                    vm.startGameMode()
                    navController.navigate(ROUTE_GAME)
                }
            )
        }
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
                    navController.popBackStack(ROUTE_MODE_SELECT, inclusive = false)
                },
                onRetake = {
                    vm.startSession()
                    navController.popBackStack(ROUTE_TRAINING, inclusive = false)
                }
            )
        }
        composable(ROUTE_SHOW) {
            ShowModeScreen(
                vm = vm,
                onBack = { navController.popBackStack() }
            )
        }
        composable(ROUTE_GAME) {
            GameModeScreen(
                vm = vm,
                onBack = { navController.popBackStack() }
            )
        }
    }
}

@Composable
private fun ModeSelectScreen(
    gameBestRounds: Int,
    onOpenTraining: () -> Unit,
    onOpenShowMode: () -> Unit,
    onOpenGameMode: () -> Unit
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(20.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.mascot),
                contentDescription = "Mascot",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onOpenGameMode,
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF00D2B8),
                    contentColor = Color(0xFF01211C)
                )
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Играть", fontWeight = FontWeight.Bold)
                    Text(
                        "Рекорд: $gameBestRounds",
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center
                    )
                }
            }
            Button(
                onClick = onOpenTraining,
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large
            ) {
                Text("Тренироваться")
            }
            Spacer(modifier = Modifier.weight(1f))
            TextButton(
                onClick = onOpenShowMode,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("прослушать")
            }
        }
    }
}

@Composable
private fun SessionSetupScreen(vm: TrainingViewModel, onStart: () -> Unit) {
    val state by vm.uiState.collectAsState()
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(20.dp)
                .fillMaxSize()
        ) {
            Text("Intervalo", style = MaterialTheme.typography.headlineMedium)
            Text("Выберите интервалы для тренировки", style = MaterialTheme.typography.bodyMedium)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = state.useFixedBaseNote,
                    onCheckedChange = { vm.toggleFixedBaseNote() }
                )
                Text("Фиксированная базовая нота: C4")
            }
            Spacer(Modifier.height(16.dp))
            Card(
                modifier = Modifier.weight(1f),
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                LazyColumn(modifier = Modifier.padding(12.dp)) {
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
            }
            Spacer(Modifier.height(14.dp))
            Button(
                onClick = onStart,
                enabled = state.selectedIntervals.isNotEmpty(),
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics { contentDescription = "start_training_button" },
                shape = MaterialTheme.shapes.large,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Начать тренировку")
            }
        }
    }
}

@Composable
private fun ShowModeScreen(vm: TrainingViewModel, onBack: () -> Unit) {
    val state by vm.uiState.collectAsState()
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(20.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Режим показа", style = MaterialTheme.typography.headlineMedium)
            Text("Нажмите на интервал, чтобы прослушать", style = MaterialTheme.typography.bodyMedium)
            Card(
                modifier = Modifier.weight(1f),
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                LazyColumn(modifier = Modifier.padding(12.dp)) {
                    items(Interval.entries) { interval ->
                        Button(
                            onClick = { vm.playIntervalPreview(interval) },
                            enabled = !state.isPlaying,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp),
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Text("${interval.shortName} - ${interval.displayName}")
                        }
                    }
                }
            }
            Button(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large
            ) {
                Text("Назад")
            }
        }
    }
}

@Composable
private fun GameModeScreen(vm: TrainingViewModel, onBack: () -> Unit) {
    val state by vm.uiState.collectAsState()
    val total = state.gameItems.size
    val completedCount = state.gameCompleted.size
    val isFinished = total > 0 && completedCount == total
    val isGameOver = state.isGameOver
    val gameErrorMessage = state.gameErrorMessage
    val leftListState = rememberLazyListState()
    val rightListState = rememberLazyListState()
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(20.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Игровой режим", style = MaterialTheme.typography.headlineMedium)
            Text(
                "Жизни: ${"❤".repeat(state.gameLives)}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text("Рекорд: ${state.gameBestRounds} раунд(ов)", style = MaterialTheme.typography.bodyMedium)
            Text(
                if (isGameOver) {
                    "Поражение. Пройдено раундов: ${state.gameRoundsCompleted}"
                } else if (isFinished) {
                    "Готово: $total из $total"
                } else {
                    "Угадано: $completedCount из $total"
                },
                style = MaterialTheme.typography.bodyMedium
            )
            if (!isFinished && !isGameOver) {
                Text(
                    "Слушайте слева в любом порядке и выбирайте справа для последней нажатой кнопки",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    shape = MaterialTheme.shapes.large,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    LazyColumn(
                        state = leftListState,
                        modifier = Modifier.padding(12.dp)
                    ) {
                        itemsIndexed(
                            items = state.gameItems,
                            key = { _, interval -> interval.name }
                        ) { index, interval ->
                            val isSelected = state.gameSelectedLeft == interval
                            val isCompleted = state.gameCompleted.contains(interval)
                            AnimatedVisibility(
                                visible = !isCompleted,
                                exit = fadeOut(animationSpec = tween(durationMillis = 300)) +
                                    shrinkVertically(animationSpec = tween(durationMillis = 300))
                            ) {
                                Button(
                                    onClick = { vm.playGamePrompt(index) },
                                    enabled = !state.isPlaying && !isGameOver,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 3.dp),
                                    shape = MaterialTheme.shapes.medium,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isSelected) MaterialTheme.colorScheme.tertiary
                                        else MaterialTheme.colorScheme.primary
                                    )
                                ) {
                                    Text("🔊 Интервал ${index + 1}")
                                }
                            }
                        }
                    }
                }
                Card(
                    modifier = Modifier.weight(1f),
                    shape = MaterialTheme.shapes.large,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    LazyColumn(
                        state = rightListState,
                        modifier = Modifier.padding(12.dp)
                    ) {
                        items(
                            items = state.gameOptions,
                            key = { option -> option.name }
                        ) { option ->
                            val isCompleted = state.gameCompleted.contains(option)
                            AnimatedVisibility(
                                visible = !isCompleted,
                                exit = fadeOut(animationSpec = tween(durationMillis = 300)) +
                                    shrinkVertically(animationSpec = tween(durationMillis = 300))
                            ) {
                                Button(
                                    onClick = { vm.selectGameAnswer(option) },
                                    enabled = state.gameSelectedLeft != null &&
                                        !state.isPlaying &&
                                        !isFinished &&
                                        !isGameOver,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 3.dp),
                                    shape = MaterialTheme.shapes.medium
                                ) {
                                    Text("${option.shortName} - ${option.displayName}")
                                }
                            }
                        }
                    }
                }
            }
            if (gameErrorMessage != null) {
                Text(
                    gameErrorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            if (isFinished && !isGameOver) {
                Button(
                    onClick = { vm.startNextGameRound() },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.large
                ) {
                    Text("Следующий раунд")
                }
            }
            if (isGameOver) {
                Button(
                    onClick = { vm.startGameMode() },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.large
                ) {
                    Text("Новая игра")
                }
            }
            Button(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large
            ) {
                Text("Назад")
            }
        }
    }
}

@Composable
private fun TrainingScreen(vm: TrainingViewModel, onOpenSummary: () -> Unit) {
    val state by vm.uiState.collectAsState()
    val question = state.currentQuestion
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(20.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Тренировка", style = MaterialTheme.typography.headlineMedium)
            Card(
                shape = MaterialTheme.shapes.medium,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Text(
                    "Верных: ${state.stats.correct}/${state.stats.total}",
                    modifier = Modifier.padding(12.dp),
                    style = MaterialTheme.typography.titleMedium
                )
            }
            Button(
                onClick = { vm.playCurrentQuestion() },
                enabled = question != null && !state.isPlaying,
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics { contentDescription = "listen_again_button" },
                shape = MaterialTheme.shapes.large,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) {
                Text(if (state.isPlaying) "Воспроизведение..." else "Проиграть еще раз")
            }
            Text("Выберите интервал:")
            Card(
                modifier = Modifier.weight(1f),
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                LazyColumn(modifier = Modifier.padding(12.dp)) {
                    items(question?.options.orEmpty()) { option ->
                        val isSelected = state.selectedAnswer == option
                        Button(
                            onClick = { vm.selectAnswer(option) },
                            enabled = !state.isAnswerChecked,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp),
                            shape = MaterialTheme.shapes.medium,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSelected) {
                                    MaterialTheme.colorScheme.tertiary
                                } else {
                                    MaterialTheme.colorScheme.primary
                                },
                                disabledContainerColor = if (isSelected) {
                                    MaterialTheme.colorScheme.tertiary
                                } else {
                                    MaterialTheme.colorScheme.surface
                                }
                            )
                        ) {
                            Text("${option.shortName} - ${option.displayName}")
                        }
                    }
                }
            }
            Button(
                onClick = { vm.nextQuestion() },
                enabled = state.isAnswerChecked,
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            ) { Text("Дальше") }
            if (state.isAnswerChecked && question != null) {
                val feedback = if (state.isAnswerCorrect) "Верно" else "Неверно"
                val correctLabel =
                    "${question.interval.displayName} (${question.interval.shortName})"
                Card(
                    shape = MaterialTheme.shapes.medium,
                    colors = CardDefaults.cardColors(
                        containerColor = if (state.isAnswerCorrect) {
                            MaterialTheme.colorScheme.secondary.copy(alpha = 0.18f)
                        } else {
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.16f)
                        }
                    )
                ) {
                    Text(
                        "$feedback. Правильный ответ: $correctLabel",
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
            Button(
                onClick = onOpenSummary,
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large
            ) { Text("Завершить тренировку") }
        }
    }
}

@Composable
private fun SessionSummaryScreen(vm: TrainingViewModel, onRestart: () -> Unit, onRetake: () -> Unit) {
    val state by vm.uiState.collectAsState()
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(20.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Результаты", style = MaterialTheme.typography.headlineMedium)
            Card(
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Верных ответов: ${state.stats.correct}")
                    Text("Всего ответов: ${state.stats.total}")
                    Text("Точность: ${state.stats.accuracyPercent}%", style = MaterialTheme.typography.titleLarge)
                }
            }
            Button(onClick = onRetake, modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.large) {
                Text("Новая тренировка")
            }
            Button(onClick = onRestart, modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.large) {
                Text("Изменить набор")
            }
        }
    }
}
