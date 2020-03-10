package com.anwesh.uiprojects.stepcoloredtview

/**
 * Created by anweshmishra on 10/03/20.
 */

import android.view.View
import android.view.MotionEvent
import android.graphics.Paint
import android.graphics.Color
import android.graphics.Canvas
import android.app.Activity
import android.content.Context

val colors : Array<String> = arrayOf("#3F51B5", "#1A237E", "#f44336", "#4CAF50", "#BF360C")
val lines : Int = 5
val scGap : Float = 0.02f
val strokeFactor : Int = 90
val backColor : Int = Color.parseColor("#BDBDBD")
val delay : Long = 20

fun Int.inverse() : Float = 1f / this
fun Float.maxScale(i : Int, n : Int) : Float = Math.max(0f, this - i * n.inverse())
fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.inverse(), maxScale(i, n)) * n
fun Float.sinify() : Float = Math.sin(this * Math.PI).toFloat()

fun Canvas.drawStepColoredT(i : Int, scale : Float, w : Float, paint : Paint) {
    val gap : Float = w / (colors.size + 1)
    val sf : Float = scale.sinify()
    val sfi : Float = sf.divideScale(i, lines)
    val sfi1 : Float = sfi.divideScale(0, 2)
    val sfi2 : Float = sfi.divideScale(1, 2)
    val y : Float = -gap * sfi2
    save()
    translate(0f, y)
    drawLine(-(gap / 2 * sfi1), 0f, (gap / 2 * sfi1), 0f, paint)
    restore()
    drawLine(0f, 0f, 0f, y, paint)
}

fun Canvas.drawColoredTs(scale : Float, w : Float, paint : Paint) {
    for (j in 0..(lines - 1)) {
        drawStepColoredT(j, scale, w, paint)
    }
}

fun Canvas.drawSCTNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    paint.color = Color.parseColor(colors[i])
    paint.strokeCap = Paint.Cap.ROUND
    paint.strokeWidth = Math.min(w, h) / strokeFactor
    save()
    translate(w / 2, 0f)
    drawColoredTs(scale, w, paint)
    restore()
}

