package douglasspgyn.com.github.circularcountdown

import android.view.animation.Animation
import android.view.animation.Transformation

/**
 * Created by Douglas on 23/01/18.
 */

class ProgressBarAnimation(private val progressBar: CircularCountdown, private val from: Float, private val to: Float) : Animation() {

    override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
        super.applyTransformation(interpolatedTime, t)
        val value = from + (to - from) * interpolatedTime
        progressBar.setProgress(value.toLong())
    }
}
