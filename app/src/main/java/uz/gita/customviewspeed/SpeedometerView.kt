package uz.gita.customviewspeed

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

class SpeedometerView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private var speed = 0
    private var maxSpeed = DEFAULT_MAX_SPEED
    private val borderWidth = 20f
    private val indicatorTextRect = Rect()
    private val metricTextRect = Rect()
    private val borderRect = RectF()
    private val tickBorderRect = RectF()

    private val indicatorTextPaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
        color = Color.BLACK
        textSize = 70f
    }

    private val metricTextPaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
        color = Color.BLACK
        textSize = 50f
    }


    private val borderPaint = Paint().apply {
        color = Color.BLACK
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeWidth = borderWidth
    }


    private val speedPaint = Paint().apply {
        color = Color.GREEN
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeWidth = borderWidth
    }

    private val tickBorderPaint = Paint().apply {
        color = Color.parseColor("#06C2E4")
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeWidth = borderWidth / 3
    }
    private val tickNumberPaint = Paint().apply {
        color = Color.BLACK
        isAntiAlias = true
        textSize = 40f
        style = Paint.Style.FILL
    }

    private val pointPaint = Paint().apply {
        color = Color.RED
        style = Paint.Style.FILL
        strokeWidth = 30f
        strokeCap = Paint.Cap.ROUND
    }

    private val paintMajorTick = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.STROKE
        color = Color.RED
        strokeWidth = 8f
        strokeCap = Paint.Cap.BUTT
    }

    private val paintMajorTick1 = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.STROKE
        color = Color.parseColor("#06C2E4")
        strokeWidth = 4f
        strokeCap = Paint.Cap.BUTT
    }

    private val paintMinorTick = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.STROKE
        color = Color.BLACK
        strokeWidth = 2f
        strokeCap = Paint.Cap.BUTT
    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        borderRect.set(BORDER_MARGIN, BORDER_MARGIN, w - BORDER_MARGIN, h - BORDER_MARGIN)
        val margin = BORDER_MARGIN + TICK_BORDER_MARGIN + borderWidth
        tickBorderRect.set(margin, margin, w - margin, h - margin)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val min = min(measuredWidth, measuredHeight)
        setMeasuredDimension(min, min)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        drawSpeedIndicator(canvas)
        drawBorder(canvas)
        drawTickBorder(canvas)
        drawFillBorder(canvas)
        drawTickNumbers(canvas)
        drawLineSpeed(canvas)

    }

    private fun drawSpeedIndicator(canvas: Canvas) {
        canvas.drawTextInCenter(
            speed.toString(),
            width / 2,
            height / 2 + 150,
            indicatorTextPaint,
            indicatorTextRect
        )
        canvas.drawTextInCenter(
            metric,
            width / 2,
            (height / 2) + (indicatorTextRect.height() / 2) + 180,
            metricTextPaint,
            metricTextRect
        )
    }

    private fun drawBorder(canvas: Canvas) {
        canvas.drawArc(borderRect, MIN_ANGLE, 360f - (MIN_ANGLE - MAX_ANGLE), false, borderPaint)
    }

    private fun drawTickBorder(canvas: Canvas) {
        canvas.drawArc(
            tickBorderRect,
            MIN_ANGLE,
            360f - (MIN_ANGLE - MAX_ANGLE),
            false,
            tickBorderPaint
        )
    }

    private fun drawFillBorder(canvas: Canvas) {
        canvas.drawArc(borderRect, MIN_ANGLE, mapSpeedToDegree(speed), false, speedPaint)
    }

    private fun drawLineSpeed(canvas: Canvas) {
        val cx = width / 2f
        val cy = height / 2f
        val radius = width / 1.25f
        canvas.drawLine(
            cx + (radius - 300 - BORDER_MARGIN - 250) * cos(((mapSpeedToDegree(speed) + 150) * -1).toRadian()).toFloat(),
            cy - (radius - 300 - BORDER_MARGIN - 250) * sin(((mapSpeedToDegree(speed) + 150) * -1).toRadian()).toFloat(),
            cx + (radius - TICK_BORDER_MARGIN - borderWidth - BORDER_MARGIN - 210) * cos(
                ((mapSpeedToDegree(
                    speed
                ) + 150) * -1).toRadian()
            ).toFloat(),
            cy - (radius - TICK_BORDER_MARGIN - borderWidth - BORDER_MARGIN - 210) * sin(
                ((mapSpeedToDegree(
                    speed
                ) + 150) * -1).toRadian()
            ).toFloat(),
            paintMajorTick
        )
    }

    private fun drawTickNumbers(canvas: Canvas) {
        var speed = 0
        for (i in MIN_ANGLE.toInt()..(MIN_ANGLE + 240).toInt() step 24) {

            val point = getTickCoordinate(i.toFloat() * -1, TICK_NUMBER_PADDING)
            val cx = width / 2f
            val cy = height / 2f
            val radius = width / 2f
            canvas.drawLine(
                cx + (radius - TICK_NUMBER_PADDING) * cos((i.toFloat() * -1).toRadian()).toFloat(),
                cy - (radius - TICK_NUMBER_PADDING) * sin((i.toFloat() * -1).toRadian()).toFloat(),
                cx + (radius - TICK_BORDER_MARGIN - borderWidth - BORDER_MARGIN) * cos((i.toFloat() * -1).toRadian()).toFloat(),
                cy - (radius - TICK_BORDER_MARGIN - borderWidth - BORDER_MARGIN) * sin((i.toFloat() * -1).toRadian()).toFloat(),
                paintMajorTick1
            )
            canvas.drawTextInCenter(
                "$speed",
                point.x.toInt(),
                point.y.toInt() + 35,
                tickNumberPaint,
                Rect()
            )
            speed += 20
        }

        for (i in MIN_ANGLE.toInt()..(MIN_ANGLE + 240).toInt() step 6) {
            val cx = width / 2f
            val cy = height / 2f
            val radius = width / 2f
            if ((i - MIN_ANGLE.toInt()) % 24 != 0)
                canvas.drawLine(
                    cx + (radius - TICK_NUMBER_PADDING + BORDER_MARGIN) * cos((i.toFloat() * -1).toRadian()).toFloat(),
                    cy - (radius - TICK_NUMBER_PADDING + BORDER_MARGIN) * sin((i.toFloat() * -1).toRadian()).toFloat(),
                    cx + (radius - TICK_BORDER_MARGIN - borderWidth - BORDER_MARGIN) * cos((i.toFloat() * -1).toRadian()).toFloat(),
                    cy - (radius - TICK_BORDER_MARGIN - borderWidth - BORDER_MARGIN) * sin((i.toFloat() * -1).toRadian()).toFloat(),
                    paintMinorTick
                )
        }

    }

    private fun getTickCoordinate(degree: Float, padding: Float): PointF {
        val cx = width / 2f
        val cy = height / 2f
        val radius = width / 2f

        return if (degree > -300 && degree != -0f) {
            PointF(
                cx + (radius - padding - TICK_BORDER_MARGIN - borderWidth - 20) * cos(degree.toRadian()).toFloat(),
                cy - (radius - padding - TICK_BORDER_MARGIN - borderWidth - 20) * sin(degree.toRadian()).toFloat()
            )
        } else {
            PointF(
                cx + (radius - padding - TICK_BORDER_MARGIN - borderWidth - 40) * cos(degree.toRadian()).toFloat(),
                cy - (radius - padding - TICK_BORDER_MARGIN - borderWidth - 40) * sin(degree.toRadian()).toFloat()
            )
        }
    }

    private fun Float.toRadian() =
        this * Math.PI / 180

    fun setSpeed(speed: Int) {
        this.speed = speed
        invalidate()
    }

    private fun Canvas.drawTextInCenter(text: String, cx: Int, cy: Int, paint: Paint, rect: Rect) {
        paint.getTextBounds(text, 0, text.length, rect)

        drawText(
            text,
            cx - rect.width() / 2f,
            cy - rect.height() / 2f,
            paint
        )
    }


    fun mapSpeedToDegree(speed: Int): Float = speed / (maxSpeed / (360f - (MIN_ANGLE - MAX_ANGLE)))


    companion object {
        val MIN_ANGLE = 150f
        val TICK_NUMBER_PADDING = 120f
        val MAX_ANGLE = 30f
        val MIN_SPEED = 0
        val BORDER_MARGIN = 30f
        val DEFAULT_INDICATOR_TEXT_SIZE = 100f
        val DEFAULT_MAX_SPEED = 200

        val TICK_BORDER_MARGIN = 10f

        val metric = "km/h"
        val METRIC_TEXT_SIZE = 50f

    }


    private fun drawPath(canvas: Canvas) {
        val paint = Paint().apply {
            color = Color.GREEN
            style = Paint.Style.STROKE
            strokeWidth = 5f
        }
        val path = Path()
        path.moveTo((width / 2).toFloat() - 100, (height / 2).toFloat() + 50)
        path.lineTo((width / 2).toFloat() - 110, (height / 2).toFloat() + 40)
        path.lineTo(300f, 675f)
        path.lineTo((height / 2).toFloat() - 100, (height / 2).toFloat() + 50)
        canvas.drawPath(path, paint)
    }

}