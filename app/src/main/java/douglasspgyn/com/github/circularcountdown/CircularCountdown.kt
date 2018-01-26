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
import android.widget.RelativeLayout
import kotlinx.android.synthetic.main.countdown_view.view.*

/**
 * Created by Douglas on 23/01/18.
 */

class CircularCountdown : RelativeLayout {

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
        const val TYPE_SECOND: Int = 0
        const val TYPE_MINUTE: Int = 1
        const val TYPE_HOUR: Int = 2
        const val TYPE_DAY: Int = 3

        const val SECOND_CONVERTER: Long = 1000
        const val MINUTE_CONVERTER: Long = 1000 * 60
        const val HOUR_CONVERTER: Long = 1000 * 60 * 60
        const val DAY_CONVERTER: Long = 1000 * 60 * 60 * 24
    }

    private var pastTime: Long = 0
    private var endTime: Long = 0
    private var progress: Long = 0
    private var elapsedTime: Long = 0
        get() = endTime - progress

    private var timeConverter: Long = 0
    private var loop: Boolean = true
    private var cycleCount: Int = 0
    private var maxCycles: Int = -1

    private var countdownTimer: CountDownTimer? = null
    private var listener: CircularListener? = null

    private var countdownTextSize: Float = context.resources.getDimension(R.dimen.circularCountdownTextSize)
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

    fun create(pastTime: Int, endTime: Int, timeType: Int): CircularCountdown {
        this.pastTime = pastTime.toLong()
        this.endTime = endTime.toLong()
        this.timeConverter = when (timeType) {
            TYPE_SECOND -> SECOND_CONVERTER
            TYPE_MINUTE -> MINUTE_CONVERTER
            TYPE_HOUR -> HOUR_CONVERTER
            TYPE_DAY -> DAY_CONVERTER
            else -> SECOND_CONVERTER
        }

        progress = this.pastTime
        setProgressBar()

        return this
    }

    fun listener(listener: CircularListener? = null): CircularCountdown {
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
        val duration = if (endTime > pastTime) {
            (endTime - pastTime)
        } else {
            1
        }

        startCountdown(duration)

        return this
    }

    fun stop() {
        countdownTimer?.cancel()
    }

    private fun startCountdown(duration: Long) {
        countdownTimer = object : CountDownTimer(duration * timeConverter, timeConverter) {
            override fun onTick(millisUntilFinished: Long) {
                onTimerTick()
            }

            override fun onFinish() {
                onTimerFinish()
            }
        }.start()
    }

    private fun setProgressBar() {
        countdownProgress.max = endTime.toInt()
        countdownProgress.secondaryProgress = endTime.toInt()
        countdownProgress.progress = progress.toInt()
        countdownText.text = elapsedTime.toInt().toString()
    }

    fun setProgress(value: Long) {
        countdownProgress.progress = value.toInt()
    }

    private fun onTimerTick() {
        when {
            progress == endTime -> {
                countdownText.text = "0"
                setProgress(progress)
            }
            progress > endTime -> {
                progress = 1
                val anim = ProgressBarAnimation(this, endTime.toFloat(), 1.toFloat())
                anim.duration = 500
                startAnimation(anim)
                countdownText.text = elapsedTime.toInt().toString()
            }
            else -> {
                setProgress(progress)
                countdownText.text = elapsedTime.toInt().toString()
            }
        }

        listener?.onTick(progress.toInt())
        progress++
    }

    private fun onTimerFinish() {
        cycleCount++

        if (maxCycles > 0) {
            loop = cycleCount < maxCycles
        }

        if (loop) {
            startCountdown(endTime)
        } else {
            countdownText.text = "0"
            setProgress(endTime)
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