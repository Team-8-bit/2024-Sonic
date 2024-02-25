package org.team9432.lib.commandbased.commands

import org.team9432.lib.commandbased.KSubsystem

fun PrintCommand(message: String) = InstantCommand { println(message) }
fun InstantCommand(requirements: MutableSet<KSubsystem> = mutableSetOf(), runnable: () -> Unit) = SimpleCommand(initialize = runnable, isFinished = { true }, requirements = requirements)