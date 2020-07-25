package io.kotlinovsky.appkit.avatar

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import io.kotlinovsky.appkit.badge.BadgeView
import kotlin.math.cos
import kotlin.math.sin

/**
 * Layout для аватара со значком.
 */
class AvatarLayout : FrameLayout {

    private var avatarImageView: ImageView? = null
    private var badgeView: BadgeView? = null

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    @SuppressLint("RtlHardcoded")
    override fun onViewAdded(child: View) {
        if (child is ImageView) {
            avatarImageView = child
        } else if (child is BadgeView) {
            (child.layoutParams as LayoutParams).gravity = Gravity.TOP or Gravity.LEFT
            badgeView = child
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val radiusX = avatarImageView!!.measuredWidth / 2
        val radiusY = avatarImageView!!.measuredHeight / 2

        (badgeView!!.layoutParams as MarginLayoutParams).let {
            val x = (radiusX + cos(-Math.PI / 4) * radiusX).toInt() - badgeView!!.measuredWidth / 2

            if (layoutDirection == View.LAYOUT_DIRECTION_RTL) {
                it.rightMargin = x
            } else {
                it.leftMargin = x
            }

            it.topMargin = (radiusY - sin(-Math.PI / 4) * radiusY).toInt() - badgeView!!.measuredHeight / 2
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }
}