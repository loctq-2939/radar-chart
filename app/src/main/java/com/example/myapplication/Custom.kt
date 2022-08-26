package com.example.myapplication

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View


// Stroke width for the the paint.
private const val STROKE_WIDTH = 12f

/**
 * Custom view that follows touch events to draw on a canvas.
 */
class MyCanvasView @JvmOverloads
constructor(mContext : Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    View(mContext,attrs, defStyleAttr) {

    private var p = Path()
    private var p1 = Path()

    private val strokePaint = Paint().apply {
        isAntiAlias = false                   // pass true does not make change
        color = Color.LTGRAY
        strokeWidth = 6.toFloat()
        style = Paint.Style.STROKE
    }

    private val strokePaintThin = Paint().apply {
        isAntiAlias = false                   // pass true does not make change
        color = Color.LTGRAY
        strokeWidth = 2.toFloat()
        style = Paint.Style.STROKE
    }

    val line = 6
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

    override fun onDraw(canvas: Canvas) {
        val midX = (width / 2).toFloat()
        val midY = (height / 2).toFloat()

        for (line in 0..line) {
            if(line % 2 == 1) {
                p1.draw(midX, midY, line)
            }
            else {
                p.draw(midX, midY, line)
            }
        }

        p.moveTo(midX, midY)
        p.lineTo(midX, midY - defaultPointFirst - (pointCenter * line) - extrasDistance)

        p.moveTo(midX, midY)
        p.lineTo(midX + defaultLineFirst.first + (pointCenter * line) - extrasDistance, midY + defaultLineFirst.second - (pointCenterY * line) - extrasDistance)

        p.moveTo(midX, midY)
        p.lineTo(midX + defaultLineSecond.first + (pointBotX * line) - extrasDistance, midY + defaultLineSecond.second + (pointBotY * line))

        p.moveTo(midX, midY)
        p.lineTo(midX - defaultLineThird.first - (pointBotX * line) + extrasDistance, midY + defaultLineThird .second + (pointBotY * line) + extrasDistance)

        p.moveTo(midX, midY)
        p.lineTo(midX - defaultLineFourth.first - (pointCenter * line) + extrasDistance, midY +  defaultLineFourth.second - (pointCenterY * line) - extrasDistance)

        canvas.drawPath(p, strokePaint)
        canvas.drawPath(p1, strokePaintThin)
    }

    private fun Path.draw(midX: Float, midY: Float, line: Int) {
        moveTo(midX, midY - defaultPointFirst - (pointCenter * line)) //  30
        lineTo(midX + defaultLineFirst.first + (pointCenter * line), midY + defaultLineFirst.second - (pointCenterY * line)) // x : 30, y : 5
        lineTo(midX + defaultLineSecond.first + (pointBotX * line), midY + defaultLineSecond.second + (pointBotY * line))  // x : 15, y : 30
        lineTo(midX - defaultLineThird.first - (pointBotX * line), midY + defaultLineThird .second + (pointBotY * line)) // x : 15, y : 30
        lineTo(midX - defaultLineFourth.first - (pointCenter * line), midY +  defaultLineFourth.second - (pointCenterY * line)) // x : 30, y : 5
        lineTo(midX, midY - defaultPointFirst - (pointCenter * line))
    }
}

const val defaultPointFirst = 60
const val extrasDistance = 2

var size = 400

val pointCenter = size * 0.0875.toFloat() //35
val pointCenterY = size * 0.03.toFloat() //12
val pointBotX = size * 0.0525.toFloat() //21
val pointBotY = size * 0.075.toFloat() //30
val pointLineX = size * 0.2375.toFloat() //30
val pointLineBotX = size * 0.0375.toFloat() //15
val pointLineCenterY = size * 0.1125.toFloat() //45
val pointLineBotY = size * 0.02.toFloat() //45

val defaultLineFirst: Pair<Int, Int> = Pair(55, -17)
val defaultLineSecond: Pair<Int, Int> = Pair(30, 45)
val defaultLineThird: Pair<Int, Int> = Pair(30, 45)
val defaultLineFourth: Pair<Int, Int> = Pair(55, -17)