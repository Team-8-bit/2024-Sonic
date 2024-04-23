package org.team9432.lib.led.animation

import org.team9432.lib.led.strip.Section

abstract class Animation(val section: Section, var priority: Int? = null, var name: String = ""): AnimationJob {
    val colors = section.getColorSet()
    abstract suspend fun runAnimation()

    override suspend fun run() {
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