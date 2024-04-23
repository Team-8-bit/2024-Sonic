package org.team9432.lib.led.animation

import org.team9432.lib.led.strip.Section
import org.team9432.lib.tasks.SuspendRunnable

abstract class Animation(val section: Section, var priority: Int? = null, var name: String = ""): SuspendRunnable {
    val colors = section.getColorSet()
    abstract suspend fun runAnimation()

    override suspend fun invoke() {
        try {
            AnimationManager.animationAddQueue.add(this@Animation)
            runAnimation()
        } finally {
            AnimationManager.animationRemoveQueue.add(this@Animation)
        }
    }

    fun withPriority(priority: Int?): Animation {
        this.priority = priority
        return this
    }

    fun withName(name: String): Animation {
        this.name = name
        return this
    }
}