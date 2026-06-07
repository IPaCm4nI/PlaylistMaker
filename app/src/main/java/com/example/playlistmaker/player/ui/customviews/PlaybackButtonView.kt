package com.example.playlistmaker.player.ui.customviews

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.core.graphics.drawable.toBitmap
import com.example.playlistmaker.R

class PlaybackButtonView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0,
    @StyleRes defStyleRes: Int = 0,
) : View(context, attrs, defStyleAttr, defStyleRes) {

    private val imagePlay: Bitmap?
    private val imageStop: Bitmap?
    private var imageRect = RectF(0f, 0f, 0f, 0f)
    private var isPlaying: Boolean = false
    var onStateChanged: (() -> Unit)? = null

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.PlaybackButtonView,
            defStyleAttr,
            defStyleRes
        ).apply {
            try {

                imagePlay = getDrawable(R.styleable.PlaybackButtonView_imagePlayResId)?.toBitmap()
                imageStop = getDrawable(R.styleable.PlaybackButtonView_imageStopResId)?.toBitmap()

            } finally {
                recycle()
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredWidth = imagePlay?.width ?: suggestedMinimumWidth
        val desiredHeight = imagePlay?.height ?: suggestedMinimumHeight
        setMeasuredDimension(
            resolveSize(desiredWidth, widthMeasureSpec),
            resolveSize(desiredHeight, heightMeasureSpec)
        )
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        imageRect = RectF(0f, 0f, measuredWidth.toFloat(), measuredHeight.toFloat())
    }

    override fun onDraw(canvas: Canvas) {
        if(isPlaying) {
            imageStop?.let {
                canvas.drawBitmap(imageStop, null, imageRect, null)
            }
        } else {
            imagePlay?.let {
                canvas.drawBitmap(imagePlay, null, imageRect, null)
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                return isEnabled
            }
            MotionEvent.ACTION_UP -> {
                isPlaying = !isPlaying
                invalidate()
                onStateChanged?.invoke()
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    fun setIsPlaying(playing: Boolean) {
        isPlaying = playing
        invalidate()
    }
}