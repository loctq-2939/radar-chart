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
) :
    View(mContext, attrs, defStyleAttr) {

    private var p = Path() // strokePath
    private var p1 = Path()
    private var size = 300
    private var pointCenter = size * 0.0875f //35
    private var pointCenterY = size * 0.03f //12
    private var pointBotX = size * 0.0525f //21
    private var pointBotY = size * 0.075f //30
    private var pointLineX = size * 0.2375f //30
    private var pointLineBotX = size * 0.0375f //15
    private var pointLineCenterY = size * 0.1125f //45
    private var pointLineBotY = size * 0.02f //45

    var webColor: Int? = null

    private val webBoldPaint = Paint().apply {
        isAntiAlias = false                   // pass true does not make change
        color = webColor ?: Color.LTGRAY
        strokeWidth = 4f
        style = Paint.Style.STROKE
    }

    private val webThinPaint = Paint().apply {
        isAntiAlias = false                   // pass true does not make change
        color = webColor ?: Color.LTGRAY
        strokeWidth = 2f
        style = Paint.Style.STROKE
    }

    val line = 10
    var widthView = 0f
    var heightView = 0f

    init {
        val typeArray = context.obtainStyledAttributes(
            attrs, R.styleable.MyCanvasView, defStyleAttr, defStyleAttr
        )

        attrs?.let {
            widthView = typeArray.getDimension(R.styleable.MyCanvasView_size, 0f)
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
        pointCenter = size * 0.0875f //35
        pointCenterY = size * 0.03f //12
        pointBotX = size * 0.0525f //21
        pointBotY = size * 0.075f //30
        pointLineX = size * 0.2375f //30
        pointLineBotX = size * 0.0375f //15
        pointLineCenterY = size * 0.1125f //45
        pointLineBotY = size * 0.02f //45
    }

    override fun onDraw(canvas: Canvas) {
        val midX = (width / 2).toFloat()
        val midY = (height / 2).toFloat()

        //draw web
        drawWeb(midX, midY)
        drawYAxis(midX, midY)

        canvas.drawPath(p, webBoldPaint)
        canvas.drawPath(p1, webThinPaint)
    }

    private fun drawYAxis(midX: Float, midY: Float) {
        //line (12h)
        p.moveTo(midX, midY)
        p.lineTo(midX, midY - defaultPointFirst - (pointCenter * line) - extrasDistance)

        //line (2h)
        p.moveTo(midX, midY)
        p.lineTo(
            midX + defaultLineFirst.first + (pointCenter * line) - extrasDistance,
            midY + defaultLineFirst.second - (pointCenterY * line) - extrasDistance

        )

        //line 5h
        p.moveTo(midX, midY)
        p.lineTo(
            midX + defaultLineSecond.first + (pointBotX * line) - extrasDistance,
            midY + defaultLineSecond.second + (pointBotY * line)
        )

        //line 7h
        p.moveTo(midX, midY)
        p.lineTo(
            midX - defaultLineThird.first - (pointBotX * line) + extrasDistance,
            midY + defaultLineThird.second + (pointBotY * line) + extrasDistance
        )

        //line 10h
        p.moveTo(midX, midY)
        p.lineTo(
            midX - defaultLineFourth.first - (pointCenter * line) + extrasDistance,
            midY + defaultLineFourth.second - (pointCenterY * line) - extrasDistance
        )
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
        moveTo(midX, midY - defaultPointFirst - (pointCenter * line)) //  30
        lineTo(
            midX + defaultLineFirst.first + (pointCenter * line),
            midY + defaultLineFirst.second - (pointCenterY * line)
        ) // x : 30, y : 5
        lineTo(
            midX + defaultLineSecond.first + (pointBotX * line),
            midY + defaultLineSecond.second + (pointBotY * line)
        )  // x : 15, y : 30
        lineTo(
            midX - defaultLineThird.first - (pointBotX * line),
            midY + defaultLineThird.second + (pointBotY * line)
        ) // x : 15, y : 30
        lineTo(
            midX - defaultLineFourth.first - (pointCenter * line),
            midY + defaultLineFourth.second - (pointCenterY * line)
        ) // x : 30, y : 5
        lineTo(midX, midY - defaultPointFirst - (pointCenter * line))
    }

    companion object {
        const val defaultPointFirst = 60
        const val extrasDistance = 2

        val defaultLineFirst: Pair<Int, Int> = Pair(55, -17)
        val defaultLineSecond: Pair<Int, Int> = Pair(30, 45)
        val defaultLineThird: Pair<Int, Int> = Pair(30, 45)
        val defaultLineFourth: Pair<Int, Int> = Pair(55, -17)
    }
}