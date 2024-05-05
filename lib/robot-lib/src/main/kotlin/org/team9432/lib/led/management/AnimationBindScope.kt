package org.team9432.lib.led.management

import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.team9432.lib.coroutines.SuspendFunction
import org.team9432.lib.robot.RobotBase

/** Binds animations to given boolean conditions. Schedules and ends them as needed when [update] is called. */
class AnimationBindScope private constructor(private val enabled: () -> Boolean) {
    /** List of scopes that are enabled when this scope is also enabled. */
    private val onTrue: MutableList<AnimationBindScope> = mutableListOf()

    /** List of scopes that are enabled when this scope is disabled. */
    private val onFalse: MutableList<AnimationBindScope> = mutableListOf()

    /** The animation run when this scope is enabled. */
    private var animation: SuspendFunction? = null

    /** The job of the animation if currently running. */
    private var job: Job? = null

    fun update() {
        val enabled = enabled.invoke()

        if (enabled) {
            onTrue.forEach { it.update() }
            onFalse.forEach { it.disableAnimation() }
        } else {
            onFalse.forEach { it.update() }
            onTrue.forEach { it.disableAnimation() }
        }

        setAnimationState(enabled)
    }

    /** Disables all animations for this scope, and all children. */
    private fun disableAnimation() {
        onTrue.forEach { it.disableAnimation() }
        onFalse.forEach { it.disableAnimation() }

        setAnimationState(false)
    }

    fun If(enabled: () -> Boolean, bind: AnimationBindScope.() -> Unit): AnimationBindScope {
        val scope = AnimationBindScope(enabled)
        this.onTrue.add(scope)
        scope.bind()
        return scope
    }

    fun ElseIf(enabled: () -> Boolean, bind: AnimationBindScope.() -> Unit): AnimationBindScope {
        val scope = AnimationBindScope(enabled)
        this.onFalse.add(scope)
        scope.bind()
        return scope
    }

    fun Else(bind: AnimationBindScope.() -> Unit): AnimationBindScope {
        val scope = AnimationBindScope { true }
        this.onFalse.add(scope)
        scope.bind()
        return scope
    }

    /** Sets the animation that is bound to this scope. */
    fun setAnimation(animation: SuspendFunction) {
        this.animation = animation
    }

    private var lastEnabled: Boolean = false

    /** Enabled or disable the animation of this scope. */
    private fun setAnimationState(enabled: Boolean) {
        // Don't run if nothing has changed
        if (lastEnabled == enabled) return
        lastEnabled = enabled

        // Also stop if there isn't an animation set
        val animationReference = animation ?: return

        if (enabled) {
            job = RobotBase.coroutineScope.launch {
                animationReference.invoke()
            }
        } else {
            job?.cancel()
            job = null
        }
    }

    companion object {
        /** Starts building a scope, using this as a base case that is always enabled. */
        fun build(bind: AnimationBindScope.() -> Unit): AnimationBindScope {
            val scope = AnimationBindScope { true }
            scope.bind()
            return scope
        }
    }
}