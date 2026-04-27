package com.muxaeji.intervalo.domain

enum class Interval(val semitones: Int, val shortName: String, val displayName: String) {
    PERFECT_UNISON(0, "ч.1", "Чистая прима (унисон)"),
    MINOR_SECOND(1, "м.2", "Малая секунда"),
    MAJOR_SECOND(2, "б.2", "Большая секунда"),
    MINOR_THIRD(3, "м.3", "Малая терция"),
    MAJOR_THIRD(4, "б.3", "Большая терция"),
    PERFECT_FOURTH(5, "ч.4", "Чистая кварта"),
    TRITONE(6, "ув.4/ум.5", "Тритон (увеличенная кварта или уменьшенная квинта)"),
    PERFECT_FIFTH(7, "ч.5", "Чистая квинта"),
    MINOR_SIXTH(8, "м.6", "Малая секста"),
    MAJOR_SIXTH(9, "б.6", "Большая секста"),
    MINOR_SEVENTH(10, "м.7", "Малая септима"),
    MAJOR_SEVENTH(11, "б.7", "Большая септима"),
    PERFECT_OCTAVE(12, "ч.8", "Чистая октава")
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
