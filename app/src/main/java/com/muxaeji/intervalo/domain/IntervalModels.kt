package com.muxaeji.intervalo.domain

enum class Interval(val semitones: Int, val shortName: String, val displayName: String) {
    MINOR_SECOND(1, "m2", "Малая секунда"),
    MAJOR_SECOND(2, "M2", "Большая секунда"),
    MINOR_THIRD(3, "m3", "Малая терция"),
    MAJOR_THIRD(4, "M3", "Большая терция"),
    PERFECT_FOURTH(5, "P4", "Чистая кварта"),
    TRITONE(6, "TT", "Тритон"),
    PERFECT_FIFTH(7, "P5", "Чистая квинта"),
    MINOR_SIXTH(8, "m6", "Малая секста"),
    MAJOR_SIXTH(9, "M6", "Большая секста"),
    MINOR_SEVENTH(10, "m7", "Малая септима"),
    MAJOR_SEVENTH(11, "M7", "Большая септима"),
    OCTAVE(12, "P8", "Октава")
}

data class Note(val midi: Int) {
    val frequencyHz: Double = 440.0 * Math.pow(2.0, (midi - 69) / 12.0)
}

data class Question(
    val root: Note,
    val top: Note,
    val interval: Interval,
    val options: List<Interval>
)

data class SessionStats(
    val total: Int = 0,
    val correct: Int = 0
) {
    val accuracyPercent: Int = if (total == 0) 0 else (correct * 100 / total)
}
