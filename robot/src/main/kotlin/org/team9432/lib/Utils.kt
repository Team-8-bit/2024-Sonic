package org.team9432.lib

import edu.wpi.first.wpilibj.DriverStation

infix fun <T> T.ifBlueElse(other: T) = if (State.alliance == DriverStation.Alliance.Blue) this else other
infix fun <T> T.ifRedElse(other: T) = if (State.alliance == DriverStation.Alliance.Red) this else other

fun <T> T.println(): T {
    println(this.toString())
    return this
}