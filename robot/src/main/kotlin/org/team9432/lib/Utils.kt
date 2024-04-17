package org.team9432.lib

import edu.wpi.first.wpilibj.DriverStation
import org.team9432.lib.unit.Time
import org.team9432.lib.unit.inMilliseconds

infix fun <T> T.ifBlueElse(other: T) = if (State.alliance == DriverStation.Alliance.Blue) this else other
infix fun <T> T.ifRedElse(other: T) = if (State.alliance == DriverStation.Alliance.Red) this else other

fun <T> T.println(): T {
    println(this.toString())
    return this
}

suspend fun delay(time: Time) = kotlinx.coroutines.delay(time.inMilliseconds.toLong())