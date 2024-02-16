package org.team9432.lib.commandbased.commands

fun PrintCommand(message: String) = InstantCommand { println(message) }
fun InstantCommand(runnable: () -> Unit) = SimpleCommand(initialize = runnable, isFinished = { true })