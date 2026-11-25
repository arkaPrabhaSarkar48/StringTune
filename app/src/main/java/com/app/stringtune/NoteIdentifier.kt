package com.app.stringtune

import kotlin.math.ln
import kotlin.math.pow
import kotlin.math.roundToInt

data class NoteResult(
    val noteName: String,
    val octave: Int,
    val frequency: Float,
    val targetFrequency: Float,
    val cents: Float
)

class NoteIdentifier {

    private val noteNames = arrayOf(
        "C", "C#", "D", "D#", "E", "F",
        "F#", "G", "G#", "A", "A#", "B"
    )

    fun identify(frequency: Float): NoteResult? {

        if (frequency <= 0f) return null

        // Calculate distance from A4 (440 Hz)
        val exactMidiNote = 69.0 +
                12.0 * (ln(frequency / 440.0) / ln(2.0))

        // Find nearest musical note
        val midiNote = exactMidiNote.roundToInt()

        // Calculate target frequency
        val targetFrequency = (
                440.0 * 2.0.pow((midiNote - 69) / 12.0)
                ).toFloat()

        // Calculate cents deviation
        val cents = (
                1200.0 * (ln(frequency / targetFrequency) / ln(2.0))
                ).toFloat()

        val noteIndex = midiNote % 12
        val octave = (midiNote / 12) - 1

        return NoteResult(
            noteName = noteNames[noteIndex],
            octave = octave,
            frequency = frequency,
            targetFrequency = targetFrequency,
            cents = cents
        )
    }
}