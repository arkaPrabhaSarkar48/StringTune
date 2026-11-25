package com.app.stringtune

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import android.animation.ValueAnimator
import android.view.animation.DecelerateInterpolator

class TuningGaugeView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private var needleAnimator: ValueAnimator? = null

    private val gaugePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 12f
        strokeCap = Paint.Cap.ROUND
    }

    private val needlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        strokeWidth = 4f
        strokeCap = Paint.Cap.ROUND
    }

    private val centerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(66, 214, 154)
        style = Paint.Style.FILL
    }

    private var cents = 0f

    fun setCents(value: Float) {
        val targetCents = value.coerceIn(-50f, 50f)

        needleAnimator?.cancel()

        needleAnimator = ValueAnimator.ofFloat(
            cents,
            targetCents
        ).apply {

            duration = 120L
            interpolator = DecelerateInterpolator()

            addUpdateListener { animator ->
                cents = animator.animatedValue as Float
                invalidate()
            }

            start()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val cx = width / 2f
        val cy = height * 0.90f

        val radius = min(
            width * 0.43f,
            height * 0.82f
        )

        val left = cx - radius
        val top = cy - radius
        val right = cx + radius
        val bottom = cy + radius

        // LEFT THIRD: FLAT
        gaugePaint.color = Color.rgb(255, 100, 100)
        canvas.drawArc(
            left, top, right, bottom,
            180f, 60f, false, gaugePaint
        )

        // MIDDLE THIRD: IN TUNE
        gaugePaint.color = Color.rgb(66, 214, 154)
        canvas.drawArc(
            left, top, right, bottom,
            240f, 60f, false, gaugePaint
        )

        // RIGHT THIRD: SHARP
        gaugePaint.color = Color.rgb(255, 100, 100)
        canvas.drawArc(
            left, top, right, bottom,
            300f, 60f, false, gaugePaint
        )

        // NEEDLE POSITION
        val angle = Math.toRadians(
            (270f + cents * 1.8f).toDouble()
        )

        val needleLength = radius * 0.88f

        val endX = cx + cos(angle).toFloat() * needleLength
        val endY = cy + sin(angle).toFloat() * needleLength

        canvas.drawLine(
            cx, cy,
            endX, endY,
            needlePaint
        )

        // CENTER DOT
        canvas.drawCircle(cx, cy, 9f, centerPaint)
    }
}