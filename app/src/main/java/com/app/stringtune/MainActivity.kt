package com.app.stringtune

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.util.Log
import androidx.annotation.RequiresPermission
import kotlin.math.abs
import kotlin.math.ln



import android.widget.TextView

import java.util.Locale

class MainActivity : AppCompatActivity() {

    private var audioRecorder: AudioRecorder? = null

    private lateinit var pitchDetector: PitchDetector
    private lateinit var noteIdentifier: NoteIdentifier

    // UI components
    private lateinit var tvDetectedNote: TextView
    private lateinit var tvFrequency: TextView
    private lateinit var tvTuningStatus: TextView
    private lateinit var tvCents: TextView
    private lateinit var gauge: TuningGaugeView

    private var smoothedCents: Float? = null
    private lateinit var stringButtons: List<TextView>
    private var selectedStringIndex = 0
    private val stringFrequencies = listOf(
        82.41f,   // 6th string - E2
        110.00f,  // 5th string - A2
        146.83f,  // 4th string - D3
        196.00f,  // 3rd string - G3
        246.94f,  // 2nd string - B3
        329.63f   // 1st string - E4
    )

    private lateinit var tvTargetFrequency: TextView

    private val microphonePermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->

            if (isGranted) {
                startMicrophoneTest()

                Toast.makeText(
                    this,
                    "Microphone permission granted!",
                    Toast.LENGTH_SHORT
                ).show()

            } else {
                Toast.makeText(
                    this,
                    "Microphone permission is required for tuning.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Connect XML views
        tvDetectedNote = findViewById(R.id.tvDetectedNote)
        tvFrequency = findViewById(R.id.tvFrequency)
        tvTuningStatus = findViewById(R.id.tvTuningStatus)
        tvCents = findViewById(R.id.tvCents)
        gauge = findViewById(R.id.tuningGauge)


        pitchDetector = PitchDetector()
        noteIdentifier = NoteIdentifier()



        checkMicrophonePermission()
    }

    override fun onStart() {
        super.onStart()

        checkMicrophonePermission()
    }

    override fun onStop() {
        stopMicrophoneTest()

        super.onStop()
    }

    private fun checkMicrophonePermission() {

        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED -> {

                startMicrophoneTest()

            }

            else -> {
                microphonePermissionLauncher.launch(
                    Manifest.permission.RECORD_AUDIO
                )
            }
        }

    }

    private fun startMicrophoneTest() {

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        // Prevent duplicate recording sessions
        if (audioRecorder != null) {
            return
        }

        audioRecorder = AudioRecorder()

        try {
            audioRecorder?.startRecording { samples, _ ->

                val frequency = pitchDetector.detectPitch(samples)

                if (frequency != null) {

                    val result = noteIdentifier.identify(frequency)

                    if (result != null) {

                        val rawCents = result.cents

                        // Smooth pitch fluctuations
                        val previousCents = smoothedCents

                        val cents = if (previousCents == null) {
                            rawCents
                        } else {
                            previousCents + 0.20f * (rawCents - previousCents)
                        }

                        smoothedCents = cents

                        val status = when {
                            cents < -5f -> "Flat"
                            cents > 5f -> "Sharp"
                            else -> "In Tune"
                        }

                        runOnUiThread {

                            tvDetectedNote.text =
                                "${result.noteName}${result.octave}"

                            tvFrequency.text = String.format(
                                Locale.US,
                                "%.2f Hz",
                                result.frequency
                            )

                            tvCents.text = String.format(
                                Locale.US,
                                "%.1f cents",
                                cents
                            )

                            tvTuningStatus.text = status

                            gauge.setCents(cents)
                        }

                        Log.d(
                            "StringTunePitch",
                            "Note: ${result.noteName}${result.octave} | " +
                                    "Frequency: %.2f Hz | ".format(result.frequency) +
                                    "Cents: %.1f".format(cents)
                        )
                    }
                }
            }

        } catch (e: SecurityException) {
            Log.e("StringTuneAudio", "Microphone permission denied", e)

        } catch (e: Exception) {
            Log.e("StringTuneAudio", "Microphone error", e)
        }


    }

    private fun stopMicrophoneTest() {

        audioRecorder?.stopRecording()

        audioRecorder = null

        smoothedCents = null
    }


}
