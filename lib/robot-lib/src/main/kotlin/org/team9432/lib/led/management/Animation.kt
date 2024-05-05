package org.team9432.lib.led.management

import org.team9432.lib.coroutines.SuspendFunction

/** Class defining an animation. Subclasses should override the [runAnimation] for animation-specific code. */
abstract class Animation(val section: Section): SuspendFunction {
    var priority: Int? = null
    var name: String = ""

    val colorset = section.getColorSet()
    abstract suspend fun runAnimation()

    /** Runs this animation and adds it to the animation manager. */
    override suspend fun invoke() {
        try {
            AnimationManager.addAnimation(this)
            runAnimation()
        } finally {
            AnimationManager.removeAnimation(this)
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