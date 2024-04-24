package org.team9432.lib

import edu.wpi.first.wpilibj.DriverStation

/** Returns this if the robot alliance is blue, else other. */
infix fun <T> T.ifBlueElse(other: T) = if (State.alliance == DriverStation.Alliance.Blue) this else other

/** Returns this if the robot alliance is red, else other. */
infix fun <T> T.ifRedElse(other: T) = if (State.alliance == DriverStation.Alliance.Red) this else other

/** Prints this and returns it. Useful for inserting into call chains while debugging. */
inline fun <T> T.println(): T {
    println(this.toString())
    return this
}

/** Passes this into [text], prints the output, and returns it. Useful for inserting into call chains while debugging. */
inline fun <T> T.println(text: (T) -> String): T {
    println(text(this))
    return this
}
