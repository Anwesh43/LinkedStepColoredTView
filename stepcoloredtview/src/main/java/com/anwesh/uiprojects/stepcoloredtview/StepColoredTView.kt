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

class StepColoredTView(ctx : Context) : View(ctx) {

    private val renderer : Renderer = Renderer(this)

    override fun onDraw(canvas : Canvas) {
        renderer.render(canvas)
    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when(event.action) {
            MotionEvent.ACTION_DOWN -> {
                renderer.handleTap()
            }
        }
        return true
    }

    data class State(var scale : Float = 0f, var dir : Float = 0f, var prevScale : Float = 0f) {

        fun update(cb : (Float) -> Unit) {
            scale += scGap * dir
            if (Math.abs(scale - prevScale) > 1) {
                scale = prevScale + dir
                dir = 0f
                prevScale = scale
                cb(prevScale)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            if (dir == 0f) {
                dir = 1f - 2 * prevScale
                cb()
            }
        }
    }

    data class Animator(var view : View, var animated : Boolean = false) {

        fun animate(cb : () -> Unit) {
            if (animated) {
                cb()
                try {
                    Thread.sleep(delay)
                    view.invalidate()
                } catch(ex : Exception) {

                }
            }
        }

        fun start() {
            if (!animated) {
                animated = true
                view.postInvalidate()
            }
        }

        fun stop() {
            if (animated) {
                animated = false
            }
        }
    }

    data class SCTNode(var i : Int, val state : State = State()) {

        private var next : SCTNode? = null
        private var prev : SCTNode? = null

        init {
            addNeighbor()
        }

        fun addNeighbor() {
            if (i < colors.size - 1) {
                next = SCTNode(i + 1)
                next?.prev = this
            }
        }

        fun draw(canvas : Canvas, paint : Paint) {
            canvas.drawSCTNode(i, state.scale, paint)
        }

        fun update(cb : (Float) -> Unit) {
            state.update(cb)
        }

        fun startUpdating(cb : () -> Unit) {
            state.startUpdating(cb)
        }

        fun getNext(dir : Int, cb : () -> Unit) : SCTNode {
            var curr : SCTNode? = prev
            if (dir == 1) {
                curr = next
            }
            if (curr != null) {
                return curr
            }
            cb()
            return this
        }
    }

    data class StepColoredT(var i : Int) {

        private var curr : SCTNode = SCTNode(0)
        private var dir : Int = 1

        fun draw(canvas : Canvas, paint : Paint) {
            curr.draw(canvas, paint)
        }

        fun update(cb : (Float) -> Unit) {
            curr.update {
                curr = curr.getNext(dir) {
                    dir *= -1
                }
                cb(it)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            curr.startUpdating(cb)
        }
    }

    data class Renderer(var view : StepColoredTView) {

        private val animator : Animator = Animator(view)
        private val sct : StepColoredT = StepColoredT(0)
        private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)

        fun render(canvas : Canvas) {
            canvas.drawColor(backColor)
            sct.draw(canvas, paint)
            animator.animate {
                sct.update {
                    animator.stop()
                }
            }
        }

        fun handleTap() {
            sct.startUpdating {
                animator.start()
            }
        }
    }

    companion object {

        fun create(activity : Activity) : StepColoredTView {
            val view : StepColoredTView = StepColoredTView(activity)
            activity.setContentView(view)
            return view
        }
    }
}
