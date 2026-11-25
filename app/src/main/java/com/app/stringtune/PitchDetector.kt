package com.app.stringtune

import kotlin.math.abs

class PitchDetector(
    private val sampleRate: Int = AudioRecorder.SAMPLE_RATE
) {

    fun detectPitch(samples: ShortArray): Float? {

        val size = samples.size

        if (size < 4096) return null

        val halfSize = size / 2

        val audio = FloatArray(size) { index ->
            samples[index].toFloat() / 32768f
        }

        // Reject extremely quiet audio
        var maxAmplitude = 0f

        for (sample in audio) {
            maxAmplitude = maxOf(maxAmplitude, abs(sample))
        }

        if (maxAmplitude < 0.005f) return null

        // Difference function
        val difference = FloatArray(halfSize)

        for (tau in 1 until halfSize) {

            var sum = 0.0

            for (i in 0 until halfSize) {

                val delta = audio[i] - audio[i + tau]

                sum += delta * delta
            }

            difference[tau] = sum.toFloat()
        }

        // Cumulative mean normalized difference
        val cmnd = FloatArray(halfSize)

        cmnd[0] = 1f

        var runningSum = 0f

        for (tau in 1 until halfSize) {

            runningSum += difference[tau]

            cmnd[tau] = if (runningSum > 0f) {
                difference[tau] * tau / runningSum
            } else {
                1f
            }
        }

        // Search for the first suitable local minimum
        val minTau = sampleRate / 1500
        val maxTau = minOf(halfSize - 2, sampleRate / 65)

        val threshold = 0.12f

        var tauEstimate = -1

        for (tau in maxOf(2, minTau) until maxTau) {

            if (cmnd[tau] < threshold) {

                var bestTau = tau

                while (
                    bestTau + 1 < maxTau &&
                    cmnd[bestTau + 1] < cmnd[bestTau]
                ) {
                    bestTau++
                }

                tauEstimate = bestTau
                break
            }
        }

        if (tauEstimate == -1) return null

        // Parabolic interpolation
        val betterTau = parabolicInterpolation(
            cmnd,
            tauEstimate
        )

        if (betterTau <= 0f) return null

        val frequency = sampleRate / betterTau

        if (frequency < 65f || frequency > 1500f) {
            return null
        }

        return frequency
    }

    private fun parabolicInterpolation(
        values: FloatArray,
        tau: Int
    ): Float {

        if (tau <= 0 || tau >= values.size - 1) {
            return tau.toFloat()
        }

        val left = values[tau - 1]
        val center = values[tau]
        val right = values[tau + 1]

        val denominator = left - 2f * center + right

        if (abs(denominator) < 0.000001f) {
            return tau.toFloat()
        }

        val adjustment =
            0.5f * (left - right) / denominator

        return tau + adjustment
    }
}