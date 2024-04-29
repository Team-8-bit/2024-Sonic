package io

import org.team9432.lib.dashboard.InitialMessage
import org.team9432.lib.dashboard.Message
import org.team9432.lib.dashboard.ValueUpdateMessage
import ui.valueMap

object MessageProcessor {
    fun process(message: Message): Unit = when (message) {
        is InitialMessage -> message.messages.forEach(::process)
        is ValueUpdateMessage -> processValueUpdateMessage(message)
    }

    private fun processValueUpdateMessage(message: ValueUpdateMessage) {
        valueMap[message.key] = message.value
    }
}