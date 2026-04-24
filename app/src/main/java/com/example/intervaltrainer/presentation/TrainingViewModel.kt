package com.example.intervaltrainer.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.intervaltrainer.data.audio.IntervalAudioPlayer
import com.example.intervaltrainer.data.audio.SineWaveIntervalAudioPlayer
import com.example.intervaltrainer.domain.CheckAnswerUseCase
import com.example.intervaltrainer.domain.GenerateQuestionUseCase
import com.example.intervaltrainer.domain.Interval
import com.example.intervaltrainer.domain.Question
import com.example.intervaltrainer.domain.SessionStats
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TrainingUiState(
    val selectedIntervals: Set<Interval> = setOf(Interval.MINOR_SECOND, Interval.MAJOR_SECOND),
    val currentQuestion: Question? = null,
    val selectedAnswer: Interval? = null,
    val isAnswerChecked: Boolean = false,
    val isAnswerCorrect: Boolean = false,
    val isPlaying: Boolean = false,
    val stats: SessionStats = SessionStats()
)

class TrainingViewModel(
    private val generateQuestionUseCase: GenerateQuestionUseCase = GenerateQuestionUseCase(),
    private val checkAnswerUseCase: CheckAnswerUseCase = CheckAnswerUseCase(),
    private val audioPlayer: IntervalAudioPlayer = SineWaveIntervalAudioPlayer()
) : ViewModel() {

    private val _uiState = MutableStateFlow(TrainingUiState())
    val uiState: StateFlow<TrainingUiState> = _uiState.asStateFlow()

    fun toggleInterval(interval: Interval) {
        _uiState.update { state ->
            val updated = state.selectedIntervals.toMutableSet().apply {
                if (!add(interval)) remove(interval)
            }
            state.copy(selectedIntervals = updated)
        }
    }

    fun startSession() {
        val question = generateQuestionUseCase.generate(_uiState.value.selectedIntervals.toList())
        _uiState.update {
            it.copy(
                currentQuestion = question,
                selectedAnswer = null,
                isAnswerChecked = false,
                isAnswerCorrect = false,
                stats = SessionStats()
            )
        }
        playCurrentQuestion()
    }

    fun selectAnswer(interval: Interval) {
        _uiState.update { it.copy(selectedAnswer = interval) }
    }

    fun checkAnswer() {
        val state = _uiState.value
        val question = state.currentQuestion ?: return
        val selected = state.selectedAnswer ?: return
        val correct = checkAnswerUseCase.isCorrect(question, selected)
        _uiState.update {
            it.copy(
                isAnswerChecked = true,
                isAnswerCorrect = correct,
                stats = it.stats.copy(
                    total = it.stats.total + 1,
                    correct = it.stats.correct + if (correct) 1 else 0
                )
            )
        }
    }

    fun nextQuestion() {
        val question = generateQuestionUseCase.generate(_uiState.value.selectedIntervals.toList())
        _uiState.update {
            it.copy(
                currentQuestion = question,
                selectedAnswer = null,
                isAnswerChecked = false,
                isAnswerCorrect = false
            )
        }
        playCurrentQuestion()
    }

    fun playCurrentQuestion() {
        val question = _uiState.value.currentQuestion ?: return
        if (_uiState.value.isPlaying) return
        viewModelScope.launch {
            _uiState.update { it.copy(isPlaying = true) }
            runCatching {
                audioPlayer.playInterval(question.root, question.top)
            }
            _uiState.update { it.copy(isPlaying = false) }
        }
    }
}
