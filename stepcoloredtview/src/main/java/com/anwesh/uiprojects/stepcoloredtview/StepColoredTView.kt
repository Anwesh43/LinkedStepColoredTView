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
