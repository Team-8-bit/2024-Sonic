package io

import org.team9432.lib.dashboard.modules.InitialMessage
import org.team9432.lib.dashboard.modules.LayoutMessage
import org.team9432.lib.dashboard.modules.Message
import org.team9432.lib.dashboard.modules.ValueUpdateMessage
import ui.layout
import ui.valueMap

object MessageProcessor {
    fun process(message: Message): Unit = when (message) {
        is InitialMessage -> processInitialMessage(message)
        is LayoutMessage -> processLayoutMessage(message)
        is ValueUpdateMessage<*> -> processValueUpdateMessage(message)
    }

    private fun processInitialMessage(message: InitialMessage) {
        message.messages.forEach(::process)
    }

    private fun processLayoutMessage(message: LayoutMessage) {
        layout = message.layout
    }

    private fun processValueUpdateMessage(message: ValueUpdateMessage<*>) {
        valueMap[message.key] = message.value
    }
}