package org.team9432.lib.led.animation

import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.team9432.lib.coroutineshims.RobotBase
import org.team9432.lib.tasks.SuspendRunnable

data class AnimationContainer(val lambda: SuspendRunnable, var lastEnabled: Boolean = false, var job: Job? = null)

class AnimationBindScope private constructor(private val enabled: () -> Boolean) {
    private val onTrue: MutableList<AnimationBindScope> = mutableListOf()
    private val onFalse: MutableList<AnimationBindScope> = mutableListOf()
    private val animations: MutableList<AnimationContainer> = mutableListOf()

    fun update() {
        val enabled = enabled.invoke()

        if (enabled) {
            onTrue.forEach { it.update() }
            onFalse.forEach { it.disableAnimation() }
        } else {
            onFalse.forEach { it.update() }
            onTrue.forEach { it.disableAnimation() }
        }

        setAnimations(enabled)
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

    fun addAnimation(animation: SuspendRunnable) {
        this.animations.add(AnimationContainer(animation))
    }

    private fun setAnimations(isActive: Boolean) {
        for (animation in animations) {
            // Don't run if nothing has changed
            if (animation.lastEnabled == isActive) continue
            animation.lastEnabled = isActive

            if (isActive) {
                animation.job = RobotBase.coroutineScope.launch {
                    animation.lambda.invoke()
                }
            } else {
                animation.job?.cancel()
                animation.job = null
            }
        }
    }

    private fun disableAnimation() {
        onTrue.forEach { it.disableAnimation() }
        onFalse.forEach { it.disableAnimation() }

        setAnimations(false)
    }

    companion object {
        fun build(bind: AnimationBindScope.() -> Unit): AnimationBindScope {
            val scope = AnimationBindScope { true }
            scope.bind()
            return scope
        }
    }
}