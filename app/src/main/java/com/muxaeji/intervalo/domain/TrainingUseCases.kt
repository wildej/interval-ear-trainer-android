package com.muxaeji.intervalo.domain

import kotlin.random.Random

class GenerateQuestionUseCase(
    private val random: Random = Random.Default,
    private val minMidi: Int = 48,
    private val maxMidi: Int = 72
) {
    fun generate(selectedIntervals: List<Interval>, fixedRootMidi: Int? = null): Question {
        require(selectedIntervals.isNotEmpty()) { "At least one interval must be selected." }
        val interval = selectedIntervals[random.nextInt(selectedIntervals.size)]
        val maxRoot = maxMidi - interval.semitones
        val rootMidi = fixedRootMidi ?: random.nextInt(minMidi, maxRoot + 1)
        require(rootMidi in minMidi..maxRoot) {
            "Base note must keep interval in available MIDI range."
        }
        val root = Note(rootMidi)
        val top = Note(rootMidi + interval.semitones)
        return Question(root = root, top = top, interval = interval, options = selectedIntervals)
    }
}

class CheckAnswerUseCase {
    fun isCorrect(question: Question, selected: Interval): Boolean = question.interval == selected
}
