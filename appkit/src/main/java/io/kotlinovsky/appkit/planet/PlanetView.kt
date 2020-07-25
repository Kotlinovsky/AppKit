package io.kotlinovsky.appkit.planet

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.annotation.ColorInt
import androidx.core.content.res.getColorOrThrow
import androidx.core.content.res.getDimensionPixelSizeOrThrow
import androidx.core.content.res.use
import androidx.core.view.updateLayoutParams
import io.kotlinovsky.appkit.R
import kotlin.math.cos
import kotlin.math.sin

/**
 * View на подобие планеты
 */
class PlanetView : FrameLayout {

    private var color = 0
    private var isActive = false
    private var orbitWidth = 0F
    private var orbitMargin = 0F
    private var activeColor = 0
    private var satelliteAngle = 0F
    private var satelliteOutlineWidth = 0
    private var satelliteOutlineColor = 0
    private var activeOrbitWidth = 0F
    private var satelliteView: ImageView? = null
    private var imageView: ImageView? = null
    private var orbitRect = RectF()
    private var orbitRadiusX = 0F
    private var orbitRadiusY = 0F
    private var orbitPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
    }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, R.attr.planetViewStyle)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        context.obtainStyledAttributes(attrs, R.styleable.PlanetView, defStyleAttr, 0).use {
            color = it.getColorOrThrow(R.styleable.PlanetView_planetColor)
            activeColor = it.getColorOrThrow(R.styleable.PlanetView_planetActiveColor)
            satelliteAngle = it.getInteger(R.styleable.PlanetView_planetSatelliteAngle, 0) * Math.PI.toFloat() / 180
            activeOrbitWidth = it.getDimensionPixelSizeOrThrow(R.styleable.PlanetView_planetActiveOrbitWidth).toFloat()
            satelliteOutlineWidth = it.getDimensionPixelSizeOrThrow(R.styleable.PlanetView_planetSatelliteOutlineWidth)
            satelliteOutlineColor = it.getColorOrThrow(R.styleable.PlanetView_planetSatelliteOutlineColor)
            orbitMargin = it.getDimensionPixelSizeOrThrow(R.styleable.PlanetView_planetOrbitMargin).toFloat()
            orbitWidth = it.getDimensionPixelSizeOrThrow(R.styleable.PlanetView_planetOrbitWidth).toFloat()
        }

        orbitPaint.color = color
        orbitPaint.strokeWidth = orbitWidth
    }

    @SuppressLint("RtlHardcoded")
    override fun onViewAdded(child: View) {
        if (child is ImageView) {
            if (imageView == null) {
                imageView = child
                imageView!!.updateLayoutParams<LayoutParams> {
                    gravity = Gravity.CENTER
                }
            } else {
                satelliteView = child
                satelliteView!!.updateLayoutParams<LayoutParams> {
                    gravity = Gravity.TOP or Gravity.LEFT
                }
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val needAdd = 2 * (orbitMargin + orbitWidth).toInt()
        val needHeight = imageView!!.measuredHeight + needAdd
        val needWidth = imageView!!.measuredWidth + needAdd
        val diff = orbitPaint.strokeWidth - orbitWidth

        val leftBorder = paddingLeft.toFloat() + orbitPaint.strokeWidth - diff
        val topBorder = paddingTop.toFloat() + orbitPaint.strokeWidth - diff
        val bottomBorder = needHeight - orbitPaint.strokeWidth + diff
        val rightBorder = needWidth - orbitPaint.strokeWidth + diff
        val centerY = needHeight / 2
        val centerX = needWidth / 2

        orbitRect.set(leftBorder, topBorder, rightBorder, bottomBorder)
        orbitRadiusX = orbitRect.width() / 2
        orbitRadiusY = orbitRect.height() / 2

        satelliteView?.updateLayoutParams<MarginLayoutParams> {
            val x = (centerX + cos(satelliteAngle) * centerX).toInt() - satelliteView!!.measuredWidth / 2
            val y = (centerY - sin(satelliteAngle) * centerY).toInt() - satelliteView!!.measuredHeight / 2

            leftMargin = x
            topMargin = y
        }

        setMeasuredDimension(needWidth, needHeight)
    }

    override fun dispatchDraw(canvas: Canvas) {
        canvas.drawRoundRect(orbitRect, orbitRadiusX, orbitRadiusY, orbitPaint)
        super.dispatchDraw(canvas)

        if (satelliteView != null) {
            canvas.save()
            canvas.translate(
                satelliteView!!.left.toFloat() - satelliteOutlineWidth,
                satelliteView!!.top.toFloat() - satelliteOutlineWidth
            )

            val scaleX = (satelliteView!!.measuredWidth + 2F * satelliteOutlineWidth) / satelliteView!!.measuredWidth
            val scaleY = (satelliteView!!.measuredHeight + 2F * satelliteOutlineWidth) / satelliteView!!.measuredHeight

            canvas.scale(scaleX, scaleY)
            satelliteView!!.setColorFilter(satelliteOutlineColor)
            satelliteView!!.draw(canvas)
            satelliteView!!.colorFilter = null
            canvas.restore()

            canvas.save()
            canvas.translate(satelliteView!!.left.toFloat(), satelliteView!!.top.toFloat())
            satelliteView!!.draw(canvas)
            canvas.restore()
        }
    }

    /**
     * Выставляет угол спутника.
     * Отсчет ведется с центра правой стороны.
     *
     * @param angle Угол расположения спутника (в градусах)
     */
    fun setSatelliteAngle(angle: Int) {
        val recalculatedAngle = angle * Math.PI.toFloat() / 180

        if (satelliteAngle != recalculatedAngle) {
            satelliteAngle = recalculatedAngle
            requestLayout()
            invalidate()
        }
    }

    /**
     * Выставляет толщину орбиты спутника
     *
     * @param strokeWidth Толщина орбиты спутника.
     */
    fun setOrbitWidth(strokeWidth: Float) {
        if (orbitPaint.strokeWidth != strokeWidth) {
            orbitPaint.strokeWidth = strokeWidth
            requestLayout()
            invalidate()
        }
    }

    /**
     * Переводит состояние View
     *
     * @param active Активировать View?
     * @param orbitColor Цвет активной орбиты
     */
    fun setActive(active: Boolean, @ColorInt orbitColor: Int = activeColor) {
        if (isActive != active || orbitColor != orbitPaint.color) {
            if (active) {
                orbitPaint.strokeWidth = activeOrbitWidth
                orbitPaint.color = orbitColor
            } else {
                orbitPaint.strokeWidth = orbitWidth
                orbitPaint.color = color
            }

            requestLayout()
            invalidate()
        }
    }
}