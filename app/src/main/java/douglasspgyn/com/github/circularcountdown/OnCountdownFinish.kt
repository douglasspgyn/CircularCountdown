package douglasspgyn.com.github.circularcountdown

/**
 * Created by Douglas on 23/01/18.
 */

interface OnCountdownFinish {
    fun onFinish(newCycle: Boolean, cycleCount: Int)
}