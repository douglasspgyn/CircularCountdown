package douglasspgyn.com.github.circularcountdown.listener

/**
 * Created by Douglas on 23/01/18.
 */

interface CircularListener {
    fun onTick(progress: Int)
    fun onFinish(newCycle: Boolean, cycleCount: Int)
}