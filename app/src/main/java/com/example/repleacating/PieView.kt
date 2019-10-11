package com.example.repleacating

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF
import android.graphics.RectF
import android.graphics.Region
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat

import java.util.ArrayList
import java.util.Objects
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt

class PieView : View {

    private var angleValue = 0f
    private val mPaint = Paint()
    private val circlePaint = Paint()
    private val textPaint = Paint()
    private val centerTextPaint = Paint()
    private var mData: ArrayList<PieBeat> = ArrayList()
    private var mColors: ArrayList<Int>? = null
    private var centralCircleRadius: Float = 0.toFloat()
    private var centerInnerCir = 0
    private var centerTextFirstLine: String? = null
    private var centerTextSecondLine: String? = null
    private var selectArcPosition = -1
    private var centerX: Float = 0.toFloat()
    private var centerY: Float = 0.toFloat()
    private var textColor: Int = 0
    private var centerTextColor: Int = 0
    private var isShowImage = false

    private var radius: Float = 0.toFloat()

    private val centerP: Path = Path()
    private val centerR: Region = Region()
    private var globalRegion: Region? = null
    private var clickListener: OnPieClick? = null
    private val allValue = 360f

    private var touchFlag = -1
    private var currentFlag = -1

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {

        mPaint.style = Paint.Style.FILL
        mPaint.isAntiAlias = true
        circlePaint.style = Paint.Style.FILL
        circlePaint.isAntiAlias = true
        circlePaint.color = ContextCompat.getColor(getContext(), R.color.background_color)

        textPaint.style = Paint.Style.FILL
        textPaint.color = Color.WHITE
        textPaint.isAntiAlias = true
        textPaint.isDither = true

        centerTextPaint.style = Paint.Style.FILL
        centerTextPaint.isAntiAlias = true
        centerTextPaint.isDither = true
        centerTextPaint.color = Color.BLACK
    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        //находиим середину вьюхи
        centerX = (w / 2).toFloat()
        centerY = (h / 2).toFloat()
        radius = min(centerX, centerY)
        centralCircleRadius = if (centerInnerCir == 1) {
            radius / 2 + radius / 8
        } else {
            (radius / 2.5).toFloat()
        }
        globalRegion = Region(-w, -h, w, h)
        centerTextPaint.textSize = radius / 10
    }

    override fun onDraw(canvas: Canvas) {
        if (mData.size == 0)
            return
        canvas.drawColor(ContextCompat.getColor(context, android.R.color.transparent))
        @SuppressLint("DrawAllocation")
        val rect = RectF(centerX - radius, centerY - radius, centerX + radius, centerY + radius)
        val oneAngleValue = (allValue / mData.size).toInt()
        for (i in mData.indices) {
            val pie = mData[i]
            if (i == selectArcPosition)
                mPaint.color = ContextCompat.getColor(context, R.color.colorAccent)
            else
                mPaint.color = pie.color

            canvas.drawArc(rect, angleValue, oneAngleValue.toFloat(), true, mPaint)
            val arcP = Path()
            val arcR = Region()
            arcR.set((centerX - radius).toInt(), (centerY - radius).toInt(), (centerX + radius).toInt(), (centerY + radius).toInt())
            arcP.moveTo(centerX, centerY)
            val cx = oneAngleValue - angleValue //угол сектора
            val nx = (sin(cx.toDouble()) * radius).toFloat()// находим x
            val ny = (cos(cx.toDouble()) * radius).toFloat()/*sqrt((radius * radius + nx * nx).toDouble()).toFloat()*/ //Находим у
            arcP.lineTo(nx, ny)
            arcP.addArc(rect, angleValue, oneAngleValue.toFloat())
            arcP.lineTo(centerX, centerY)
            arcR.setPath(arcP, arcR)
            pie.region = arcR

            val textAngle = angleValue + oneAngleValue / 2
            val textPointX = (centerX + radius * 0.75 * cos(Math.toRadians(textAngle.toDouble()))).toFloat()
            val textPointY = (centerY + radius * 0.75 * sin(Math.toRadians(textAngle.toDouble()))).toFloat()
            @SuppressLint("DrawAllocation")
            val pointF = PointF(textPointX, textPointY)
            textPaint.textAlign = Paint.Align.CENTER
            textPaint.color = textColor
            if (isShowImage) {
                textPaint.textSize = radius / 15
                @SuppressLint("DrawAllocation")
                val icon = BitmapFactory.decodeResource(context.resources, pie.drawable)
                canvas.drawBitmap(icon, pointF.x - 40, pointF.y - 90, textPaint)
                val fm = textPaint.fontMetrics
                val textH = fm.bottom - fm.top
                canvas.drawText(pie.name!!, pointF.x, pointF.y + textH + 10f, textPaint)
            } else {
                textPaint.textSize = radius / 11
                if (pie.name!!.length > 10 && pie.name!!.contains(" ")) {
                    val first = pie.name!!.substring(0, pie.name!!.indexOf(" "))
                    val second = pie.name!!.substring(pie.name!!.indexOf(" "))
                    var positionDefx = 0
                    if (centerX > pointF.x - 30)
                        positionDefx = 50
                    else if (centerX < pointF.x + 30)
                        positionDefx = -50
                    val positionDefY: Int = if (centerY > pointF.y) {
                        30
                    } else -30
                    canvas.drawText(first, pointF.x + positionDefx, pointF.y + 10f + positionDefY.toFloat(), textPaint)
                    canvas.drawText(second, pointF.x + positionDefx, pointF.y - 50 + positionDefY, textPaint)
                } else
                    canvas.drawText(pie.name!!, pointF.x, pointF.y + 10, textPaint)
            }

            angleValue += oneAngleValue
        }
        //Отрисовка центральной окружности
        if (centerTextFirstLine != null) {
            centerP.addCircle(rect.centerX(), rect.centerY(), centralCircleRadius, Path.Direction.CW)
            centerR.setPath(centerP, globalRegion)
            canvas.drawCircle(rect.centerX(), rect.centerY(), centralCircleRadius, circlePaint)
            centerTextPaint.color = centerTextColor
            val fm = centerTextPaint.fontMetrics
            val fontHeight = fm.descent - fm.ascent
            val fontWidthFirst = centerTextPaint.measureText(centerTextFirstLine)
            val fontWidthSecondLine = centerTextPaint.measureText(centerTextSecondLine)
            canvas.drawText(centerTextFirstLine!!, rect.centerX() - fontWidthFirst / 2, (rect.centerY() - fontHeight / 4.5).toFloat(), centerTextPaint)
            canvas.drawText(centerTextSecondLine!!, rect.centerX() - fontWidthSecondLine / 2, rect.centerY() + fontHeight, centerTextPaint)
        }
        super.onDraw(canvas)
    }

    fun setData(mData: ArrayList<PieBeat>) {
        initData(mData)
        this.mData = mData
    }

    fun setCenterInnerCir(centerInnerCir: Int) {
        this.centerInnerCir = centerInnerCir
    }

    fun setCenterText(centerTextFirstLine: String, centerTextSecondLine: String) {
        this.centerTextFirstLine = centerTextFirstLine
        this.centerTextSecondLine = centerTextSecondLine
    }

    fun setmColors(mColors: ArrayList<Int>) {
        this.mColors = mColors
    }

    fun setTextColor(textColor: Int) {
        this.textColor = textColor
    }

    fun setCenterTextColor(color: Int) {
        this.centerTextColor = color
    }

    fun setShowImage(isPercentageShow: Boolean) {
        this.isShowImage = isPercentageShow
    }

    private fun initData(mData: ArrayList<PieBeat>?) {
        if (mData == null || mData.size == 0) {
            return
        }
        for (i in mData.indices) {
            val pieBean = mData[i]
            pieBean.color = ContextCompat.getColor(context!!, mColors!![i])
        }
        for (i in mData.indices) {
            mData[i].angle = allValue / mData.size
        }

    }

    private fun getTouchedPath(x: Int, y: Int): Int {
        if (centerR.contains(x, y)) {
            return 0
        } else if (selectArcPosition >= 0 && Objects.requireNonNull<Region>(mData[selectArcPosition].region).contains(x, y)) {
            return 1
        }
        return -1
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {

        val x = event.x.toInt()
        val y = event.y.toInt()

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                touchFlag = getTouchedPath(x, y)
                currentFlag = touchFlag
                val mx = x - centerX
                val my = y - centerY
                val result = mx * mx + my * my
                if (result <= radius * radius) {
                    for (i in mData.indices) {
                        val pieBit = mData[i]
                        if (pieBit.region!!.contains(x, y)) {
                            selectArcPosition = i
                            invalidate()
                        }
                    }
                }
                touchFlag = getTouchedPath(x, y)
                currentFlag = touchFlag
            }
            MotionEvent.ACTION_MOVE -> {
                currentFlag = getTouchedPath(x, y)
            }

            MotionEvent.ACTION_UP -> {
                currentFlag = getTouchedPath(x, y)
                if (currentFlag == touchFlag && currentFlag != -1) {
                    if (selectArcPosition >= 0 && clickListener != null) {
                        clickListener!!.onClick(selectArcPosition)
                    }
                }
                currentFlag = -1
                touchFlag = currentFlag
                invalidate()
                selectArcPosition = -1
            }
            MotionEvent.ACTION_CANCEL -> {
                currentFlag = -1
                touchFlag = currentFlag
            }
        }
        return true
    }

    fun setClickListener(clickLIstener: OnPieClick) {
        this.clickListener = clickLIstener
    }
}