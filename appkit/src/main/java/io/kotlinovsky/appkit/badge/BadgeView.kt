package io.kotlinovsky.appkit.badge

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.util.TypedValue
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.getBooleanOrThrow
import androidx.core.content.res.getColorOrThrow
import androidx.core.content.res.getDimensionPixelSizeOrThrow
import androidx.core.content.res.use
import io.kotlinovsky.appkit.R
import kotlin.math.min

/**
 * View для отображения значка.
 */
class BadgeView : AppCompatTextView {

    var color: Int
        get() = paint.color
        set(value) {
            if (color != value) {
                paint.color = value
                invalidate()
            }
        }

    private var rect = RectF()
    private var animationDuration = 0L
    private var paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var outlinePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var outlineRadius = 0F
    private var showOutline = false
    private var outlineRect = RectF()
    private var animator = animate()
    private var radius = 0F

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, R.attr.badgeViewStyle)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        context.obtainStyledAttributes(attrs, R.styleable.BadgeView, defStyleAttr, 0).use {
            paint.color = it.getColorOrThrow(R.styleable.BadgeView_badgeColor)
            showOutline = it.getBooleanOrThrow(R.styleable.BadgeView_badgeOutline)
            animationDuration = it.getInt(R.styleable.BadgeView_badgeAnimationDuration, 0).toLong()

            if (showOutline) {
                outlinePaint.color = it.getColorOrThrow(R.styleable.BadgeView_badgeOutlineColor)
                outlinePaint.strokeWidth = it.getDimensionPixelSizeOrThrow(R.styleable.BadgeView_badgeOutlineWidth).toFloat()
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)

        if (widthMode == MeasureSpec.EXACTLY && heightMode == MeasureSpec.EXACTLY) {
            val availableWidth = MeasureSpec.getSize(widthMeasureSpec)
            val availableHeight = MeasureSpec.getSize(heightMeasureSpec)

            if (showOutline) {
                rect.set(
                    outlinePaint.strokeWidth,
                    outlinePaint.strokeWidth,
                    availableWidth.toFloat() + outlinePaint.strokeWidth,
                    availableHeight.toFloat() + outlinePaint.strokeWidth
                )

                outlineRect.set(0F, 0F, availableWidth + outlinePaint.strokeWidth * 2, availableHeight + outlinePaint.strokeWidth * 2)
                outlineRadius = min(outlineRect.width(), outlineRect.height()) / 2F
                setMeasuredDimension(outlineRect.width().toInt(), outlineRect.height().toInt())
            } else {
                rect.set(0F, 0F, availableWidth.toFloat(), availableHeight.toFloat())
                setMeasuredDimension(availableWidth, availableHeight)
            }

            radius = min(rect.width(), rect.height()) / 2F
        } else {
            setTextSize(TypedValue.COMPLEX_UNIT_PX, MeasureSpec.getSize(heightMeasureSpec) * 0.6F)
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }
    }

    override fun onDraw(canvas: Canvas) {
        if (showOutline) {
            canvas.drawRoundRect(outlineRect, outlineRadius, outlineRadius, outlinePaint)
        }

        canvas.drawRoundRect(rect, radius, radius, paint)
        super.onDraw(canvas)
    }

    /**
     * Включает отображение значка в элементе списка.
     *
     * @param toggle Включить отображение?
     * @param flagForAnimating Нужный флаг для анимированного скрытия/показа.
     * @param changePayload Флаги изменений.
     */
    fun toggle(toggle: Boolean, flagForAnimating: Any, changePayload: List<Any>?) {
        if (changePayload?.contains(flagForAnimating) == true) {
            if (toggle) {
                showWithAnimation()
            } else {
                hideWithAnimation()
            }
        } else if (toggle) {
            show()
        } else {
            hide()
        }
    }

    /**
     * Показывает View
     */
    fun show() {
        animator.cancel()
        scaleX = 1F
        scaleY = 1F
        alpha = 1F
    }

    /**
     * Скрывает View
     */
    fun hide() {
        animator.cancel()
        scaleX = 0F
        scaleY = 0F
        alpha = 0F
    }

    /**
     * Анимированно показывает View.
     */
    fun showWithAnimation() {
        animator.cancel()

        animator
            .alpha(1F)
            .scaleX(1F)
            .scaleY(1F)
            .setDuration(animationDuration)
            .start()
    }

    /**
     * Анимированно скрывает View.
     */
    fun hideWithAnimation() {
        animator.cancel()
        animator
            .alpha(0F)
            .scaleX(0F)
            .scaleY(0F)
            .setDuration(animationDuration)
            .start()
    }
}