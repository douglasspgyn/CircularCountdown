package douglasspgyn.com.github.circularcountdown

import android.content.Context
import android.content.res.Resources
import android.content.res.TypedArray
import android.graphics.Color
import android.graphics.drawable.LayerDrawable
import android.os.CountDownTimer
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.drawable.DrawableCompat
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.FrameLayout
import kotlinx.android.synthetic.main.countdown_view.view.*

/**
 * Created by Douglas on 23/01/18.
 */

class CircularCountdown : FrameLayout {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircularCountdown)
        updateStyle(typedArray)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircularCountdown)
        updateStyle(typedArray)
    }

    companion object {
        val TYPE_SECOND: Long = 1000
        val TYPE_MINUTE: Long = 1000 * 60
        val TYPE_HOUR: Long = 1000 * 60 * 60
        val TYPE_DAY: Long = 1000 * 60 * 60 * 24
    }

    private var pastTime: Long = 0
    private var endTime: Long = 0
    private var progress: Long = 0
    private var isFirstTime = true

    private var timeType: Long = TYPE_SECOND
    private var loop: Boolean = true
    private var cycleCount: Int = 0
    private var maxCycles: Int = -1

    private var countdownTimer: CountDownTimer? = null
    private var listener: OnCountdownFinish? = null

    private var countdownTextSize: Float = 14f
    private var countdownTextColor: String = "#" + Integer.toHexString(ContextCompat.getColor(context, R.color.countDownText))
    private var countdownForegroundColor: String = "#" + Integer.toHexString(ContextCompat.getColor(context, R.color.countDownForeground))
    private var countdownBackgroundColor: String = "#" + Integer.toHexString(ContextCompat.getColor(context, R.color.countDownBackground))

    init {
        View.inflate(context, R.layout.countdown_view, this)
        val makeVertical = RotateAnimation(0f, -90f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
        makeVertical.fillAfter = true
        countdownProgress.startAnimation(makeVertical)
    }

    private fun updateStyle(typedArray: TypedArray) {
        typedArray.let {
            countdownTextSize = it.getDimensionPixelSize(R.styleable.CircularCountdown_countdownTextSize, context.resources.getDimension(R.dimen.circularCountdownTextSize).toInt()).toFloat()
            it.getString(R.styleable.CircularCountdown_countdownTextColor)?.let {
                countdownTextColor = it
            }
            it.getString(R.styleable.CircularCountdown_countdownForegroundColor)?.let {
                countdownForegroundColor = it
            }
            it.getString(R.styleable.CircularCountdown_countdownBackgroundColor)?.let {
                countdownBackgroundColor = it
            }

            it.recycle()
        }

        setProgressTextSize(countdownTextSize / Resources.getSystem().displayMetrics.density)
        setProgressTextColor(countdownTextColor)
        setProgressForegroundColor(countdownForegroundColor)
        setProgressBackgroundColor(countdownBackgroundColor)
    }

    fun create(pastTime: Long, endTime: Long, timeType: Long): CircularCountdown {
        this.pastTime = pastTime
        this.endTime = endTime
        this.timeType = timeType
        setCustomProgress(pastTime, endTime)
        return this
    }

    fun listener(listener: OnCountdownFinish? = null): CircularCountdown {
        this.listener = listener
        return this
    }

    fun loop(loop: Boolean): CircularCountdown {
        this.loop = loop
        return this
    }

    fun maxCycles(maxCycles: Int): CircularCountdown {
        this.maxCycles = maxCycles
        return this
    }

    fun start(): CircularCountdown {
        countdownTimer = object : CountDownTimer(if (isFirstTime) (endTime - pastTime) * timeType else endTime * timeType, timeType) {
            override fun onTick(millisUntilFinished: Long) {
                onTimerTick()
            }

            override fun onFinish() {
                onTimerFinish()
            }
        }.start()

        return this
    }

    fun stop() {
        countdownTimer?.cancel()
    }

    private fun setCustomProgress(pastTime: Long, endTime: Long) {
        countdownProgress.max = endTime.toInt()
        countdownProgress.secondaryProgress = endTime.toInt()
        countdownProgress.progress = pastTime.toInt()
        val elapsedTime = endTime - pastTime
        countdownText.text = elapsedTime.toInt().toString()
    }

    fun setProgress(value: Int) {
        countdownProgress.progress = value
    }

    private fun onTimerTick() {
        when {
            progress == endTime - 1 -> {
                countdownText.text = "0"
                setCustomProgress(progress, endTime)
                progress += 1
            }
            progress > endTime -> {
                countdownText.text = (endTime - 1).toString()
                val anim = ProgressBarAnimation(this, endTime.toFloat(), 1.toFloat())
                anim.duration = 500
                startAnimation(anim)
                progress = 2
            }
            else -> {
                if (isFirstTime) {
                    progress = endTime - (endTime - pastTime)
                    isFirstTime = false
                }

                setCustomProgress(progress, endTime)
                progress += 1
            }
        }
    }

    private fun onTimerFinish() {
        cycleCount++

        if (maxCycles > 0) {
            loop = cycleCount < maxCycles
        }

        if (loop) {
            start()
        } else {
            countdownText.text = "0"
            setCustomProgress(endTime, endTime)
            stop()
        }

        listener?.onFinish(loop, cycleCount)
    }

    fun setProgressTextSize(size: Float) {
        countdownText.textSize = size
    }

    fun setProgressTextColor(color: Int) {
        setProgressTextColor("#" + Integer.toHexString(ContextCompat.getColor(context, color)));
    }

    private fun setProgressTextColor(color: String) {
        try {
            countdownText.setTextColor(Color.parseColor(color))
        } catch (iae: IllegalArgumentException) {
            Log.d("setProgressFgColor", iae.message)
        }
    }

    fun setProgressForegroundColor(color: Int) {
        setProgressForegroundColor("#" + Integer.toHexString(ContextCompat.getColor(context, color)));
    }

    private fun setProgressForegroundColor(color: String) {
        try {
            val progressDrawable = DrawableCompat.wrap(countdownProgress.progressDrawable) as LayerDrawable
            val foreground = progressDrawable.findDrawableByLayerId(android.R.id.progress)
            DrawableCompat.setTint(foreground.mutate(), Color.parseColor(color))
        } catch (iae: IllegalArgumentException) {
            Log.d("setProgressFgColor", iae.message)
        }
    }

    fun setProgressBackgroundColor(color: Int) {
        setProgressBackgroundColor("#" + Integer.toHexString(ContextCompat.getColor(context, color)));
    }

    private fun setProgressBackgroundColor(color: String) {
        try {
            val progressDrawable = DrawableCompat.wrap(countdownProgress.progressDrawable) as LayerDrawable
            val background = progressDrawable.findDrawableByLayerId(android.R.id.secondaryProgress)
            DrawableCompat.setTint(background.mutate(), Color.parseColor(color))
        } catch (iae: IllegalArgumentException) {
            Log.d("setProgressBgColor", iae.message)
        }
    }

    fun isLoopEnable(): Boolean = loop

    fun enableLoop() {
        loop = true
    }

    fun disableLoop() {
        loop = false
    }

    fun getMaxCycles(): Int = maxCycles
}
