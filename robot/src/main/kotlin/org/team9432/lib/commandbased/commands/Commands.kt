package org.team9432.lib.commandbased.commands

import org.team9432.lib.commandbased.KCommand
import org.team9432.lib.commandbased.KSubsystem

fun PrintCommand(message: String) = InstantCommand { println(message) }
fun InstantCommand(vararg requirements: KSubsystem, runnable: () -> Unit) = SimpleCommand(initialize = runnable, isFinished = { true }, requirements = requirements.toSet())