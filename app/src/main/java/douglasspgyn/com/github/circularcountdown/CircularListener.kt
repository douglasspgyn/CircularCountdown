package douglasspgyn.com.github.circularcountdown

/**
 * Created by Douglas on 23/01/18.
 */

interface CircularListener {
    fun onTick(progress: Int)
    fun onFinish(newCycle: Boolean, cycleCount: Int)
}