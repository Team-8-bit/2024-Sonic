package io

import org.team9432.lib.dashboard.ValueUpdateMessage
import ui.valueMap

object MessageProcessor {
    fun process(message: ValueUpdateMessage) {
        valueMap[message.key] = message.value
    }
}