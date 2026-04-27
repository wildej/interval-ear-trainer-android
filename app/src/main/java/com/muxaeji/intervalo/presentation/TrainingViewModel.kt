package com.muxaeji.intervalo.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.muxaeji.intervalo.data.audio.IntervalAudioPlayer
import com.muxaeji.intervalo.data.audio.IntervalAudioPlayerProvider
import com.muxaeji.intervalo.domain.CheckAnswerUseCase
import com.muxaeji.intervalo.domain.GenerateQuestionUseCase
import com.muxaeji.intervalo.domain.Interval
import com.muxaeji.intervalo.domain.Note
import com.muxaeji.intervalo.domain.Question
import com.muxaeji.intervalo.domain.SessionStats
import kotlin.random.Random
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TrainingUiState(
    val selectedIntervals: Set<Interval> = setOf(Interval.MINOR_SECOND, Interval.MAJOR_SECOND),
    val useFixedBaseNote: Boolean = false,
    val baseMidiNote: Int = 60,
    val currentQuestion: Question? = null,
    val selectedAnswer: Interval? = null,
    val isAnswerChecked: Boolean = false,
    val isAnswerCorrect: Boolean = false,
    val isPlaying: Boolean = false,
    val stats: SessionStats = SessionStats(),
    val gameItems: List<Interval> = emptyList(),
    val gameOptions: List<Interval> = emptyList(),
    val gameCurrentIndex: Int = 0,
    val gameAwaitingAnswer: Boolean = false,
    val gameCompleted: Set<Interval> = emptySet(),
    val gameErrorMessage: String? = null
)

class TrainingViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val generateQuestionUseCase = GenerateQuestionUseCase()
    private val checkAnswerUseCase = CheckAnswerUseCase()
    private val audioPlayer: IntervalAudioPlayer =
        IntervalAudioPlayerProvider.create(application)
    private val random: Random = Random.Default

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

    fun toggleFixedBaseNote() {
        _uiState.update { it.copy(useFixedBaseNote = !it.useFixedBaseNote) }
    }

    private fun generateQuestionFromSettings(): Question {
        val state = _uiState.value
        return generateQuestionUseCase.generate(
            selectedIntervals = state.selectedIntervals.toList(),
            fixedRootMidi = if (state.useFixedBaseNote) state.baseMidiNote else null
        )
    }

    fun startSession() {
        val question = generateQuestionFromSettings()
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
        val state = _uiState.value
        if (state.isAnswerChecked) return
        val question = state.currentQuestion ?: return
        val correct = checkAnswerUseCase.isCorrect(question, interval)
        _uiState.update {
            it.copy(
                selectedAnswer = interval,
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
        val question = generateQuestionFromSettings()
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

    fun playIntervalPreview(interval: Interval) {
        if (_uiState.value.isPlaying) return
        val root = Note(60)
        val top = Note(root.midi + interval.semitones)
        viewModelScope.launch {
            _uiState.update { it.copy(isPlaying = true) }
            runCatching {
                audioPlayer.playInterval(root, top)
            }
            _uiState.update { it.copy(isPlaying = false) }
        }
    }

    fun startGameMode() {
        val items = Interval.entries.shuffled(random).take(6)
        _uiState.update {
            it.copy(
                gameItems = items,
                gameOptions = items.shuffled(random),
                gameCurrentIndex = 0,
                gameAwaitingAnswer = false,
                gameCompleted = emptySet(),
                gameErrorMessage = null
            )
        }
    }

    fun playGamePrompt(index: Int) {
        val state = _uiState.value
        val currentIndex = state.gameCurrentIndex
        if (state.isPlaying || index != currentIndex || currentIndex >= state.gameItems.size) return
        val interval = state.gameItems[currentIndex]
        val root = Note(60)
        val top = Note(root.midi + interval.semitones)
        viewModelScope.launch {
            _uiState.update { it.copy(isPlaying = true, gameAwaitingAnswer = true, gameErrorMessage = null) }
            runCatching {
                audioPlayer.playInterval(root, top)
            }
            _uiState.update { it.copy(isPlaying = false) }
        }
    }

    fun selectGameAnswer(selected: Interval) {
        val state = _uiState.value
        val currentIndex = state.gameCurrentIndex
        if (!state.gameAwaitingAnswer || currentIndex >= state.gameItems.size) return
        val expected = state.gameItems[currentIndex]
        if (selected == expected) {
            val completed = state.gameCompleted + expected
            _uiState.update {
                it.copy(
                    gameCompleted = completed,
                    gameCurrentIndex = currentIndex + 1,
                    gameAwaitingAnswer = false,
                    gameErrorMessage = null
                )
            }
        } else {
            _uiState.update {
                it.copy(
                    gameOptions = it.gameOptions.shuffled(random),
                    gameErrorMessage = "Неверно, попробуйте ещё раз"
                )
            }
        }
    }

    fun reshuffleGameOptions() {
        _uiState.update { it.copy(gameOptions = it.gameOptions.shuffled(random), gameErrorMessage = null) }
    }
}
