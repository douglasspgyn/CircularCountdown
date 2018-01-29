package douglasspgyn.com.github.circularcountdown

import android.os.Handler
import douglasspgyn.com.github.circularcountdown.listener.CascadeListener
import douglasspgyn.com.github.circularcountdown.listener.CircularListener
import douglasspgyn.com.github.circularcountdown.model.CascadeCountdown
import kotlinx.android.synthetic.main.countdown_view.view.*

/**
 * Created by Douglas on 29/01/18.
 */

class CircularCascadeCountdown {

    constructor(remainTime: Long, countdownSeconds: CircularCountdown) : this(remainTime, countdownSeconds, null)
    constructor(remainTime: Long, countdownSeconds: CircularCountdown, countdownMinutes: CircularCountdown?) : this(remainTime, countdownSeconds, countdownMinutes, null)
    constructor(remainTime: Long, countdownSeconds: CircularCountdown, countdownMinutes: CircularCountdown?, countdownHours: CircularCountdown?) : this(remainTime, countdownSeconds, countdownMinutes, countdownHours, null)
    constructor(remainTime: Long, countdownSeconds: CircularCountdown, countdownMinutes: CircularCountdown?, countdownHours: CircularCountdown?, countdownDays: CircularCountdown?) {
        this.remainTime = remainTime
        this.countdownSeconds = CascadeCountdown(countdownSeconds, CircularCountdown.TYPE_SECOND)
        countdownMinutes?.let {
            this.countdownMinutes = CascadeCountdown(countdownMinutes, CircularCountdown.TYPE_MINUTE)
        }
        countdownHours?.let {
            this.countdownHours = CascadeCountdown(countdownHours, CircularCountdown.TYPE_HOUR)
        }
        countdownDays?.let {
            this.countdownDays = CascadeCountdown(countdownDays, CircularCountdown.TYPE_DAY)
        }

        getTime()
        createCountdowns()
    }

    private val MAX_SECOND: Int = 60
    private val MAX_MINUTE: Int = 60
    private val MAX_HOUR: Int = 24
    private val MAX_DAY: Int = 30

    private var remainTime: Long = 0
    private var days: Int = 0
    private var hours: Int = 0
    private var minutes: Int = 0
    private var seconds: Int = 0

    private var countdownSeconds: CascadeCountdown
    private var countdownMinutes: CascadeCountdown? = null
    private var countdownHours: CascadeCountdown? = null
    private var countdownDays: CascadeCountdown? = null

    private var listener: CascadeListener? = null

    private fun getTime() {
        days = (remainTime / CircularCountdown.DAY_CONVERTER).toInt()
        hours = ((remainTime - (days * CircularCountdown.DAY_CONVERTER)) / CircularCountdown.HOUR_CONVERTER).toInt()
        minutes = ((remainTime - (days * CircularCountdown.DAY_CONVERTER) - (hours * CircularCountdown.HOUR_CONVERTER)) / CircularCountdown.MINUTE_CONVERTER).toInt()
        seconds = ((remainTime - (days * CircularCountdown.DAY_CONVERTER) - (hours * CircularCountdown.HOUR_CONVERTER) - (minutes * CircularCountdown.MINUTE_CONVERTER)) / CircularCountdown.SECOND_CONVERTER).toInt()
    }

    private fun createCountdowns() {
        countdownSeconds.let {
            it.circularCountdown.create(getPastTime(it), getEndTime(it), it.timeType)
        }
        countdownMinutes?.let {
            it.circularCountdown.create(getPastTime(it), getEndTime(it), it.timeType)
        }
        countdownHours?.let {
            it.circularCountdown.create(getPastTime(it), getEndTime(it), it.timeType)
        }
        countdownDays?.let {
            it.circularCountdown.create(getPastTime(it), getEndTime(it), it.timeType)
        }
    }

    private fun updateCountdowns(animateMinute: Boolean, animateHour: Boolean) {
        countdownMinutes?.let {
            if (animateMinute) {
                animateView(it)
            } else {
                it.circularCountdown.create(getPastTime(it), getEndTime(it), it.timeType)
            }
        }
        countdownHours?.let {
            if (animateHour) {
                animateView(it)
            } else {
                it.circularCountdown.create(getPastTime(it), getEndTime(it), it.timeType)
            }
        }
        countdownDays?.let {
            it.circularCountdown.create(getPastTime(it), getEndTime(it), it.timeType)
        }
    }

    private fun animateView(cascadeCountdown: CascadeCountdown) {
        val anim = ProgressBarAnimation(cascadeCountdown.circularCountdown, getEndTime(cascadeCountdown).toFloat(), 1.toFloat())
        anim.duration = 500
        cascadeCountdown.circularCountdown.startAnimation(anim)
        cascadeCountdown.circularCountdown.countdownText.text = (getEndTime(cascadeCountdown) - 1).toString()
    }

    private fun getPastTime(cascadeCountdown: CascadeCountdown): Int {
        return when (cascadeCountdown.timeType) {
            CircularCountdown.TYPE_SECOND -> MAX_SECOND - seconds
            CircularCountdown.TYPE_MINUTE -> MAX_MINUTE - minutes
            CircularCountdown.TYPE_HOUR -> MAX_HOUR - hours
            CircularCountdown.TYPE_DAY -> MAX_DAY - days
            else -> 0
        }.toInt()
    }

    private fun getEndTime(cascadeCountdown: CascadeCountdown): Int {
        return when (cascadeCountdown.timeType) {
            CircularCountdown.TYPE_SECOND -> MAX_SECOND
            CircularCountdown.TYPE_MINUTE -> MAX_MINUTE
            CircularCountdown.TYPE_HOUR -> MAX_HOUR
            CircularCountdown.TYPE_DAY -> MAX_DAY
            else -> 0
        }.toInt()
    }

    fun start(): CircularCascadeCountdown {
        countdownSeconds.circularCountdown
                .listener(object : CircularListener {
                    override fun onTick(progress: Int) {
                        seconds = MAX_SECOND - progress
                    }

                    override fun onFinish(newCycle: Boolean, cycleCount: Int) {
                        Handler().postDelayed({
                            if (minutes == 0 && hours == 0 && days == 0) {
                                countdownSeconds.circularCountdown.stop()
                                listener?.onFinish()
                            } else {
                                var animateMinute = false
                                var animateHour = false

                                if (minutes > 0) {
                                    minutes--
                                } else {
                                    if (hours > 0) {
                                        minutes = MAX_MINUTE - 1
                                        animateMinute = true
                                        hours--
                                    } else {
                                        if (days > 0) {
                                            hours = MAX_HOUR - 1
                                            animateHour = true
                                            minutes = MAX_MINUTE - 1
                                            animateMinute = true
                                            days--
                                        }
                                    }
                                }
                                updateCountdowns(animateMinute, animateHour)
                            }
                        }, CircularCountdown.SECOND_CONVERTER)
                    }
                }).start()

        return this
    }

    fun stop() {
        countdownSeconds.circularCountdown.stop()
    }

    fun listener(listener: CascadeListener? = null): CircularCascadeCountdown {
        this.listener = listener
        return this
    }

    fun isRunning(): Boolean = countdownSeconds.circularCountdown.isRunning()
}