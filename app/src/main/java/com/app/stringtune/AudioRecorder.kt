package com.app.stringtune

import android.Manifest
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import androidx.annotation.RequiresPermission

class AudioRecorder {

    companion object {
        const val SAMPLE_RATE = 44100
        const val WINDOW_SIZE = 8192
        const val HOP_SIZE = 4096
    }

    private val minBufferSize = AudioRecord.getMinBufferSize(
        SAMPLE_RATE,
        AudioFormat.CHANNEL_IN_MONO,
        AudioFormat.ENCODING_PCM_16BIT
    )

    private var audioRecord: AudioRecord? = null
    @Volatile private var isRecording = false
    private var recordingThread: Thread? = null

    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    fun startRecording(onAudioCaptured: (ShortArray, Int) -> Unit) {

        if (minBufferSize <= 0) {
            throw IllegalStateException("Invalid audio buffer size")
        }

        audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            SAMPLE_RATE,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            maxOf(minBufferSize * 2, WINDOW_SIZE * 2)
        )

        val recorder = audioRecord
            ?: throw IllegalStateException("Unable to create AudioRecord")

        if (recorder.state != AudioRecord.STATE_INITIALIZED) {
            recorder.release()
            audioRecord = null
            throw IllegalStateException("Unable to initialize microphone")
        }

        recorder.startRecording()
        isRecording = true

        recordingThread = Thread {

            val readBuffer = ShortArray(HOP_SIZE)
            val analysisBuffer = ShortArray(WINDOW_SIZE)

            var filledSamples = 0

            while (isRecording) {

                val readCount = recorder.read(
                    readBuffer,
                    0,
                    readBuffer.size
                )

                if (readCount > 0) {

                    if (filledSamples + readCount > WINDOW_SIZE) {
                        val overflow = filledSamples + readCount - WINDOW_SIZE

                        System.arraycopy(
                            analysisBuffer,
                            overflow,
                            analysisBuffer,
                            0,
                            filledSamples - overflow
                        )

                        filledSamples -= overflow
                    }

                    System.arraycopy(
                        readBuffer,
                        0,
                        analysisBuffer,
                        filledSamples,
                        readCount
                    )

                    filledSamples += readCount

                    if (filledSamples >= WINDOW_SIZE) {
                        onAudioCaptured(
                            analysisBuffer.copyOf(),
                            WINDOW_SIZE
                        )
                    }
                }
            }
        }

        recordingThread?.start()
    }

    fun stopRecording() {

        isRecording = false

        try {
            recordingThread?.join(500)
        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt()
        }

        audioRecord?.let { recorder ->

            if (recorder.recordingState == AudioRecord.RECORDSTATE_RECORDING) {
                recorder.stop()
            }

            recorder.release()
        }

        audioRecord = null
        recordingThread = null
    }
}