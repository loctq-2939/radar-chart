package com.example.myapplication

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import kotlin.math.min

class MyCanvasView @JvmOverloads constructor(
    mContext: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(mContext, attrs, defStyleAttr) {

    private var p = Path() // strokePath
    private var p1 = Path()
    private var size = 300
    val textLineSpace = 1.4f
    val line = 10
    val maxValue = 5
    private var yLineSpace = ((size * 0.4) / line).toFloat()
    private var webColor = Color.LTGRAY
    private var labelTop = ""
    private var labelTopRight = ""
    private var labelBottomRight = ""
    private var labelBottomLeft = ""
    private var labelTopLeft = ""
    private var data: List<Float>? = null
    private var dataWidth = 5f
    private var webWidth = 2f
    private var dataColor = Color.CYAN
    private var textColor = Color.BLACK

    private val webBoldPaint = Paint().apply {
        isAntiAlias = false                   // pass true does not make change
        color = webColor ?: Color.LTGRAY
        strokeWidth = 5f
        style = Paint.Style.STROKE
    }
    private val dataPaint = Paint().apply {
        isAntiAlias = false                   // pass true does not make change
        color = webColor ?: Color.CYAN
        strokeWidth = 5f
        style = Paint.Style.STROKE
    }
    private val dataPointPaint = Paint().apply {
        isAntiAlias = false                   // pass true does not make change
        color = Color.CYAN
        strokeWidth = 5f
        style = Paint.Style.FILL
    }
    private val dataFillPaint = Paint().apply {
        isAntiAlias = false                   // pass true does not make change
        color = Color.CYAN
        strokeWidth = 5f
        style = Paint.Style.FILL
        alpha = 60
    }

    private val webThinPaint = Paint().apply {
        isAntiAlias = false                   // pass true does not make change
        color = Color.LTGRAY
        strokeWidth = 2f
        style = Paint.Style.STROKE
    }

    private val textPaint = Paint().apply {
        isAntiAlias = false                   // pass true does not make change
        color = Color.BLACK
        style = Paint.Style.FILL
        textSize = 20f
        textAlign = Paint.Align.CENTER
        setPadding(0, 0, 0, 20)
    }

    private val textBoldPaint = Paint().apply {
        isAntiAlias = false                   // pass true does not make change
        color = Color.BLACK
        style = Paint.Style.FILL
        textSize = 20f
        textAlign = Paint.Align.CENTER
        setPadding(0, 0, 0, 20)
    }
    var widthView = 0f
    var heightView = 0f
    var textSizeParam: Float? = null

    init {
        val typeArray = context.obtainStyledAttributes(
            attrs, R.styleable.MyCanvasView, defStyleAttr, defStyleAttr
        )

        attrs?.let {
            widthView = typeArray.getDimension(R.styleable.MyCanvasView_size, 0f)
            textSizeParam = typeArray.getDimension(R.styleable.MyCanvasView_text_size, 0f)
            dataWidth = typeArray.getDimension(R.styleable.MyCanvasView_data_width, 0f)
            webWidth= typeArray.getDimension(R.styleable.MyCanvasView_web_width, 0f)
            textColor = typeArray.getColor(R.styleable.MyCanvasView_text_color, 0)
            dataColor = typeArray.getColor(R.styleable.MyCanvasView_data_color, 0)
            webColor = typeArray.getColor(R.styleable.MyCanvasView_web_color, 0)
        }
        webBoldPaint.apply {
            strokeWidth = webWidth*2.5f
            color = webColor
        }
        webThinPaint.apply {
            strokeWidth = webWidth
            color = webColor
        }
        textPaint.apply {
            textSize = textSizeParam ?: 20f
            color = webColor
        }
        textPaint.apply {
            textSize = textSizeParam ?: 20f
            color = textColor
        }
        textBoldPaint.apply {
            textSize = textSizeParam ?: 20f
            color = textColor
        }
        dataPaint.apply {
            color = dataColor
            strokeWidth = dataWidth
        }
        dataPointPaint.apply {
            color = dataColor
            strokeWidth = dataWidth
        }

        dataFillPaint.apply {
            color = dataColor
            strokeWidth = dataWidth
            alpha = 80
        }
        typeArray.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val minSize = min(measuredWidth, measuredHeight)
        setMeasuredDimension(minSize, minSize)
        setRadarSize(minSize * 3 / 10)
    }

    private fun setRadarSize(value: Int) {
        size = value
        yLineSpace = ((size) / line).toFloat()
    }

    override fun onDraw(canvas: Canvas) {
        val midX = (width / 2).toFloat()
        val midY = (height / 2).toFloat()

        //draw web
        drawWeb(midX, midY)
        drawYAxis(midX, midY)
        drawData(canvas, midX, midY)
        drawLabel(midX, midY, canvas)
    }

    private fun drawData(canvas: Canvas, midX: Float, midY: Float) {
        canvas.drawPath(p, webBoldPaint)
        canvas.drawPath(p1, webThinPaint)
        val pa = Path()
        var first = Pair(0f, 0f)
        data?.forEachIndexed { i, value ->
            val x = getPosition(Pair(midX, midY), yLineSpace * 10 / 5 * value, i * 72)
            canvas.drawCircle(x.first, x.second, dataPointPaint.strokeWidth*2, dataPointPaint)
            if (i == 0) {
                pa.moveTo(x.first, x.second)
                first = x
            } else {
                pa.lineTo(x.first, x.second)
            }
            canvas.drawCircle(x.first, x.second, 10f, dataPointPaint)
            if (i == data!!.size - 1) pa.lineTo(first.first, first.second)
        }

        canvas.drawPath(pa, dataFillPaint)
        canvas.drawPath(pa, dataPaint)
    }

    private fun drawLabel(midX: Float, midY: Float, canvas: Canvas) {
        val xLabelTop =
            getPosition(Pair(midX, midY), yLineSpace * 10f + getTextSize() * (-0.2F), 0 * 72)
        (xLabelTop.first to xLabelTop.second).apply {
            canvas.drawText(
                labelTop,
                this.first,
                this.second - (textSizeParam ?: 20f) * textLineSpace,
                textPaint
            )
            canvas.drawText(data?.let { it[0].toString() } ?: "_",
                this.first,
                this.second,
                textBoldPaint)
        }
        val xLabelTopRight =
            getPosition(Pair(midX, midY), yLineSpace * 10f + getTextSize() * 1f, 1 * 72)
        (xLabelTopRight.first to xLabelTopRight.second).apply {
            canvas.drawText(
                labelTopRight,
                this.first,
                this.second - (textSizeParam ?: 20f) * 0.2f,
                textPaint
            )
            canvas.drawText(data?.let { it[1].toString() } ?: "_",
                this.first,
                this.second + (textSizeParam ?: 20f) * (textLineSpace - 0.2f),
                textBoldPaint
            )
        }

        val xLabelBottomRight =
            getPosition(Pair(midX, midY), yLineSpace * 10f + getTextSize() * 1.45f, 2 * 72)
        (xLabelBottomRight.first to xLabelBottomRight.second).apply {
            canvas.drawText(
                labelBottomRight,
                this.first,
                this.second,
                textPaint
            )
            canvas.drawText(data?.let { it[2].toString() } ?: "_",
                this.first,
                this.second + (textSizeParam ?: 20f) * textLineSpace,
                textBoldPaint)
        }
        val xLabelBottomLeft =
            getPosition(Pair(midX, midY), yLineSpace * 10f + getTextSize() * 1.45f, 3 * 72)
        (xLabelBottomLeft.first to xLabelBottomLeft.second).apply {
            canvas.drawText(
                labelBottomLeft,
                this.first,
                this.second,
                textPaint
            )
            canvas.drawText(data?.let { it[3].toString() } ?: "_",
                this.first,
                this.second + (textSizeParam ?: 20f) * textLineSpace,
                textBoldPaint)
        }
        val xLabelTopLeft =
            getPosition(Pair(midX, midY), yLineSpace * 10f + getTextSize() * 0.8f, 4 * 72)
        (xLabelTopLeft.first to xLabelTopLeft.second).apply {
            canvas.drawText(
                labelTopLeft,
                this.first,
                this.second - (textSizeParam ?: 20f) * 0.2f,
                textPaint
            )
            canvas.drawText(data?.let { it[4].toString() } ?: "_",
                this.first,
                this.second + (textSizeParam ?: 20f) * (textLineSpace - 0.2f),
                textBoldPaint)
        }
    }

    private fun drawYAxis(midX: Float, midY: Float) {
        for (i in 0..4) {
            p.moveTo(midX, midY)
            val x = getPosition(Pair(midX, midY), (yLineSpace * line), 72 * i)
            p.lineTo(x.first, x.second)
        }
    }

    private fun drawWeb(midX: Float, midY: Float) {
        for (line in 0..line) {
            if (line % 2 == 1) {
                p1.drawWebLine(midX, midY, line)
            } else {
                p.drawWebLine(midX, midY, line)
            }
        }
    }

    private fun Path.drawWebLine(midX: Float, midY: Float, line: Int) {
        val start = getPosition(Pair(midX, midY), yLineSpace * line, 0)
        moveTo(start.first, start.second)
        for (i in 0..5) {
            val x = getPosition(Pair(midX, midY), (yLineSpace * line), 72 * i)
            lineTo(x.first, x.second)
        }
    }

    private fun getTextSize() = textSizeParam ?: 0f

    private fun getPosition(
        center: Pair<Float, Float>,
        dist: Float,
        angle: Int
    ): Pair<Float, Float> {
        val x = (center.first + dist * Math.cos(Math.toRadians(angle.toDouble() + 270)))
        val y = (center.second + dist * Math.sin(Math.toRadians(angle.toDouble() + 270)))
        return Pair(x.toFloat(), y.toFloat())
    }

    // public function
    fun setLabel(
        top: String = "",
        topRight: String = "",
        bottomRight: String = "",
        bottomLeft: String = "",
        topLeft: String = "",
    ) {
        labelTop = top
        labelTopRight = topRight
        labelBottomRight = bottomRight
        labelBottomLeft = bottomLeft
        labelTopLeft = topLeft
        invalidate()
    }

    fun setData(data: List<Float>) {
        if (data.size != 5) {
            this.data = null
        } else {
            this.data = data
        }
        invalidate()
    }
}