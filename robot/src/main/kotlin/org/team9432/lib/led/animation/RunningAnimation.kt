package org.team9432.lib.led.animation

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class RunningAnimation(val animation: Animation, private val priority: Int = 99): AnimationJob {
    override var job: Job? = null
    private var callback: AnimationColorCallback? = null

    override fun start() {
        job = AnimationManager.animationScope.launch(Dispatchers.Default) {
            try {
                callback = { priority to animation.getIdealBaseStripColors() }.also {
                    AnimationManager.callbacks.add(it)
                }
                animation.runAnimation(this)
            } finally {
                AnimationManager.callbacks.remove(callback)
                job = null
            }
        }
    }
}