package org.team9432.lib.commandbased.commands

import org.team9432.lib.commandbased.KSubsystem

/** A Command that prints a given string when initialized. */
fun PrintCommand(message: String) = InstantCommand { println(message) }

/** A Command that runs instantly; it will initialize, execute once, and end on the same iteration of the scheduler. */
fun InstantCommand(vararg requirements: KSubsystem, runnable: () -> Unit) = SimpleCommand(initialize = runnable, isFinished = { true }, requirements = requirements.toSet())