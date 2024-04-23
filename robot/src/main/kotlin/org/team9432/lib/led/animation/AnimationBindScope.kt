package org.team9432.lib.led.animation

import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

data class AnimationContianer(val animation: AnimationJob, var lastEnabled: Boolean = false, var job: Job? = null)

class AnimationBindScope(private val enabled: () -> Boolean) {
    private val onTrue: MutableList<AnimationBindScope> = mutableListOf()
    private val onFalse: MutableList<AnimationBindScope> = mutableListOf()
    private val animations: MutableList<AnimationContianer> = mutableListOf()

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

    fun addAnimation(animation: AnimationJob) {
        this.animations.add(AnimationContianer(animation))
    }

    private fun setAnimations(isActive: Boolean) {
        for (animation in animations) {
            // Don't run if nothing has changed
            if (animation.lastEnabled == isActive) continue
            animation.lastEnabled = isActive

            if (isActive) {
                animation.job = AnimationManager.animationScope.launch {
                    animation.animation.run()
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