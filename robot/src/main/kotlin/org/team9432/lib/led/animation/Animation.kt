package org.team9432.lib.led.animation

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.team9432.lib.led.color.PixelColor
import org.team9432.lib.led.strip.LEDStrip
import org.team9432.lib.led.strip.Section

abstract class Animation(section: Section, private var priority: Int? = null): AnimationJob {
    val colors = section.getColorSet()
    abstract suspend fun runAnimation(scope: CoroutineScope)

    private fun getIdealBaseStripColors(): MutableList<PixelColor?> {
        val list = LEDStrip.getInstance().emptyColorList.toMutableList()
        colors.getBasePixelList().forEach { (color, baseIndex) ->
            list[baseIndex] = color
        }
        return list
    }

    override var job: Job? = null
    private var callback: AnimationColorCallback? = null

    override fun start() {
        job = AnimationManager.animationScope.launch(Dispatchers.Default) {
            try {
                callback = { (priority ?: Integer.MAX_VALUE) to getIdealBaseStripColors() }.also {
                    AnimationManager.callbacks.add(it)
                }
                runAnimation(this)
            } finally {
                AnimationManager.callbacks.remove(callback)
                job = null
            }
        }
    }

    fun withPriority(priority: Int): Animation {
        this.priority = priority
        return this
    }
}