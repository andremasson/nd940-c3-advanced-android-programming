package com.udacity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.LinearInterpolator
import androidx.core.content.withStyledAttributes
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0
    private var label : String? = ""
    private val valueAnimator = ValueAnimator()
    private var loadingLabel : String? = ""
    private var actionColor : Int = 0
    private val path = Path()
    private var animationCompletion : Float = 0.0f

    private val circleRadius = resources.getDimension(R.dimen.circleRadius)
    private val textOffset = resources.getDimension(R.dimen.textOffset)
    private val animationDuration = 4000L
    private var animator = ValueAnimator.ofFloat(0f, 1.0f)

    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->
        if (new == ButtonState.Completed) animationCompletion = 0.0f
        if (new == ButtonState.Clicked) startAnimation()
    }


    init {
        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            label = getString(R.styleable.LoadingButton_label)
            loadingLabel = getString(R.styleable.LoadingButton_loadingLabel)
            val boxBackgroundColor = getColor(R.styleable.LoadingButton_boxBackgroundColor, 0)
            actionColor = getColor(R.styleable.LoadingButton_actionColor, 0)
            setBackgroundColor(boxBackgroundColor)
        }
    }

    fun onStartDownload() {
        if (buttonState != ButtonState.Completed) return
        buttonState = ButtonState.Clicked
    }

    private fun startAnimation() {
        buttonState = ButtonState.Loading

        animator.interpolator = AccelerateDecelerateInterpolator()
        animator.duration = animationDuration
        animator.addUpdateListener {
            animationCompletion = it.animatedValue as Float
            invalidate()
        }
        animator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator?) {
                buttonState = ButtonState.Loading
            }

            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                buttonState = ButtonState.Completed
            }
        })
        animator.start()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        drawLoadingRect(canvas)
        drawLoadingCircle(canvas)
        drawButtonText(canvas)
    }

    private fun drawLoadingCircle(canvas: Canvas) {
        val x = widthSize * 0.8f - circleRadius / 2
        val y = heightSize / 2f - circleRadius / 2
        val rectf = RectF(x, y, x + circleRadius, y + circleRadius)
        paint.color = Color.YELLOW
        // canvas.drawCircle(x, y, circleRadius, paint)
        canvas.drawArc(rectf, 0f, -360f * animationCompletion, true, paint)
    }

    private fun drawLoadingRect(canvas: Canvas) {
        val loadingWidth = (widthSize * animationCompletion).toInt()
        val rect = Rect(
                0, 0, loadingWidth, heightSize
        )
        paint.color = actionColor
        canvas.drawRect(rect, paint)
    }

    private fun drawButtonText(canvas: Canvas) {
        paint.color = Color.WHITE
        if (buttonState == ButtonState.Loading) loadingLabel?.let { canvas.drawText(it, (widthSize / 2).toFloat(), textOffset, paint) }
        else label?.let { canvas.drawText(it, (widthSize / 2).toFloat(), textOffset, paint) }

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = resources.getDimension(R.dimen.buttonTextSize)
        typeface = Typeface.create( "", Typeface.NORMAL)
        strokeWidth = resources.getDimension(R.dimen.strokeWidth)
    }

}