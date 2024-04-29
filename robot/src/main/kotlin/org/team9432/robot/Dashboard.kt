package org.team9432.robot

import org.team9432.lib.dashboard.modules.*

fun getLayout() = Row(
    DoubleModule("count"),
    BooleanModule("Teleop"),

    Col(
        StringModule("Alliance"),
        BooleanModule("Disabled")
    ),

    BooleanModule("Autonomous")
)