package com.example.myapplication

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import kotlin.math.min

class MyRadarChartView @JvmOverloads constructor(
    mContext: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(mContext, attrs, defStyleAttr) {

    companion object {
        const val MAGICAL_CORNER = 270
        const val yLineNumber = 5
        const val xLineNumber = 10
        const val maxValue = 5f
    }


    private var size = 300
    private val textLineSpace = 1.5f
    private var yLineSpace = ((size * 0.4) / xLineNumber).toFloat()
    private var webColor = Color.LTGRAY
    private var labelTop = ""
    private var labelTopRight = ""
    private var labelBottomRight = ""
    private var labelBottomLeft = ""
    private var labelTopLeft = ""
    private var centerLabel = ""
    private var data: List<Float>? = null
    private var centerValue: Float? = null
    private var dataWidth = 5f
    private var webWidth = 2f
    private var dataColor = Color.CYAN
    private var textColor = Color.BLACK
    private val corner = 360/ yLineNumber

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

    private val centerTextPaint = Paint().apply {
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
    private var widthView = 0f
    private var textSizeParam: Float? = null
    private var centerTextSizeParam: Float? = null

    init {
        val typeArray = context.obtainStyledAttributes(
            attrs, R.styleable.MyRadarChartView, defStyleAttr, defStyleAttr
        )

        attrs?.let {
            widthView = typeArray.getDimension(R.styleable.MyRadarChartView_size, 0f)
            textSizeParam = typeArray.getDimension(R.styleable.MyRadarChartView_text_size, 20f)
            centerTextSizeParam =
                typeArray.getDimension(R.styleable.MyRadarChartView_text_center_size, 20f)
            dataWidth = typeArray.getDimension(R.styleable.MyRadarChartView_data_width, 1f)
            webWidth = typeArray.getDimension(R.styleable.MyRadarChartView_web_width, 1f)
            textColor = typeArray.getColor(R.styleable.MyRadarChartView_text_color, 0)
            dataColor = typeArray.getColor(R.styleable.MyRadarChartView_data_color, 0)
            webColor = typeArray.getColor(R.styleable.MyRadarChartView_web_color, 0)
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
        centerTextPaint.apply {
            textSize = centerTextSizeParam ?: 20f
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
        yLineSpace = ((size) / xLineNumber).toFloat()
    }

    override fun onDraw(canvas: Canvas) {
        val midX = (width / 2).toFloat()
        val midY = (height / 2).toFloat()

        //draw web
        drawWeb(canvas, midX, midY)
        drawYAxis(canvas, midX, midY)
        drawData(canvas, midX, midY)
        drawLabel(canvas, midX, midY)
    }

    private fun drawData(canvas: Canvas, midX: Float, midY: Float) {

        val pData = Path()
        var startPoint = Pair(0f, 0f)
        if (data?.size != 5) return
        data?.forEachIndexed { i, value ->
            val x = getPosition(Pair(midX, midY), yLineSpace * 10 / 5 * value, i * corner)
            canvas.drawCircle(x.first, x.second, dataPointPaint.strokeWidth * 2, dataPointPaint)
            if (i == 0) {
                pData.moveTo(x.first, x.second)
                startPoint = x
            } else {
                pData.lineTo(x.first, x.second)
            }
            canvas.drawCircle(x.first, x.second, 10f, dataPointPaint)
            if (i == data!!.size - 1) pData.lineTo(startPoint.first, startPoint.second)
        }

        canvas.drawPath(pData, dataFillPaint)
        canvas.drawPath(pData, dataPaint)
    }

    private fun drawLabel(canvas: Canvas, midX: Float, midY: Float) {
        //(-0.2: move point near to y0)
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
            getPosition(Pair(midX, midY), yLineSpace * 10f + getTextSize() * 1f, 1 * corner)
        (xLabelTopRight.first to xLabelTopRight.second).apply {
            //0.2 :  move bottom 0.2*textSize
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
//        1.45 : move far away bottom right point
        val xLabelBottomRight =
            getPosition(Pair(midX, midY), yLineSpace * 10f + getTextSize() * 1.45f, 2 * corner)
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
            getPosition(Pair(midX, midY), yLineSpace * 10f + getTextSize() * 1.45f, 3 * corner)
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
            getPosition(Pair(midX, midY), yLineSpace * 10f + getTextSize() * 0.8f, 4 * corner)
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
        //draw center Label
        (midX to midY).apply {
            centerTextPaint.isFakeBoldText = false
//            0.4f move to bottom 0.4
            canvas.drawText(
                centerLabel,
                this.first,
                this.second - (textSizeParam ?: 20f) * 0.4f,
                centerTextPaint
            )
            centerTextPaint.isFakeBoldText = true
            canvas.drawText(data?.let { it[4].toString() } ?: "_",
                this.first,
                this.second + (textSizeParam ?: 20f) * (textLineSpace - 0.4f),
                centerTextPaint)
        }
    }

    private fun drawYAxis(canvas: Canvas, midX: Float, midY: Float) {
        val path = Path()
        for (i in 0 until yLineNumber) {
            path.moveTo(midX, midY)
            val x = getPosition(Pair(midX, midY), (yLineSpace * xLineNumber), corner * i)
            path.lineTo(x.first, x.second)
        }
        canvas.drawPath(path, webBoldPaint)
    }

    private fun drawWeb(canvas: Canvas, midX: Float, midY: Float) {
        val p = Path() // strokePath
        val p1 = Path()
        for (line in 0..xLineNumber) {
            if (line % 2 == 1) {
                p1.drawWebLine(midX, midY, line)
            } else {
                p.drawWebLine(midX, midY, line)
            }
        }
        canvas.drawPath(p, webBoldPaint)
        canvas.drawPath(p1, webThinPaint)
    }

    private fun Path.drawWebLine(midX: Float, midY: Float, line: Int) {
        val start = getPosition(Pair(midX, midY), yLineSpace * line, 0)
        moveTo(start.first, start.second)
        for (i in 0..yLineNumber) {
            val x = getPosition(Pair(midX, midY), (yLineSpace * line), corner * i)
            lineTo(x.first, x.second)
        }
    }

    private fun getTextSize() = textSizeParam ?: 0f

    private fun getPosition(
        center: Pair<Float, Float>,
        dist: Float,
        angle: Int
    ): Pair<Float, Float> {
        val x = (center.first + dist * Math.cos(Math.toRadians(angle.toDouble() + MAGICAL_CORNER)))
        val y = (center.second + dist * Math.sin(Math.toRadians(angle.toDouble() + MAGICAL_CORNER)))
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

    fun setCenter(label: String, value: Float) {
        centerLabel = label
        centerValue = value
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