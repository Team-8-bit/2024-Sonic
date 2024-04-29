package org.team9432.lib.dashboard.modules

import kotlinx.serialization.Serializable

@Serializable
sealed interface Message

@Serializable
sealed interface ValueUpdateMessage<T>: Message {
    val key: String
    val value: T
}

@Serializable
data class StringValueUpdateMessage(override val key: String, override val value: String): ValueUpdateMessage<String>

@Serializable
data class DoubleValueUpdateMessage(override val key: String, override val value: Double): ValueUpdateMessage<Double>

@Serializable
data class BooleanValueUpdateMessage(override val key: String, override val value: Boolean): ValueUpdateMessage<Boolean>

@Serializable
data class LayoutMessage(val layout: ModuleGroup): Message

@Serializable
data class InitialMessage(val messages: List<Message>): Message