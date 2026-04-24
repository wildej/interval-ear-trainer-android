package com.muxaeji.intervalo.data.audio

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class MidiFromAssetFileNameTest {
    @Test
    fun parses_pack_filenames() {
        assertEquals(60, midiFromAssetFileName("334538__teddy_frost__c4.wav"))
        assertEquals(62, midiFromAssetFileName("334536__teddy_frost__piano-normal-d4.wav"))
        assertEquals(69, midiFromAssetFileName("334534__teddy_frost__piano-a4-sound.wav"))
        assertEquals(72, midiFromAssetFileName("334537__teddy_frost__c5.wav"))
    }

    @Test
    fun ignores_non_note_files() {
        assertNull(midiFromAssetFileName("LICENSE_README.txt"))
    }
}
