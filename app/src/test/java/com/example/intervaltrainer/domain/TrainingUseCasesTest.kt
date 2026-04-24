package com.example.intervaltrainer.domain

import kotlin.random.Random
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TrainingUseCasesTest {

    @Test
    fun generateQuestion_keepsTopNoteInRange() {
        val useCase = GenerateQuestionUseCase(
            random = Random(42),
            minMidi = 48,
            maxMidi = 72
        )
        repeat(100) {
            val q = useCase.generate(Interval.entries)
            assertTrue(q.top.midi <= 72)
            assertTrue(q.root.midi >= 48)
            assertTrue(q.interval.semitones == q.top.midi - q.root.midi)
        }
    }

    @Test
    fun checkAnswer_returnsTrueOnlyForExactInterval() {
        val question = Question(
            root = Note(60),
            top = Note(64),
            interval = Interval.MAJOR_THIRD,
            options = listOf(Interval.MAJOR_THIRD, Interval.MINOR_THIRD)
        )
        val useCase = CheckAnswerUseCase()
        assertTrue(useCase.isCorrect(question, Interval.MAJOR_THIRD))
        assertFalse(useCase.isCorrect(question, Interval.MINOR_THIRD))
    }
}
