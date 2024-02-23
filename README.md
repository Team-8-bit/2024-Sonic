# 2024-Sonic

---
_Code for Team 8-Bit's 2024 robot, Sonic_

## Directory Functions
- [`annotation/`](annotation/src/main/kotlin/org/team9432/lib/annotation)

  Contains a custom annotation processor to integrate AdvantageKit with Kotlin code

- [`robot/`](robot/src/main/kotlin/org/team9432)

  Contains our robot code for the season

## Packages

- [`org.team9432.robot`](robot/src/main/kotlin/org/team9432/robot)

  Contains season-specific robot code and features

- [`org.team9432.robot.subsystems`](robot/src/main/kotlin/org/team9432/robot/subsystems)

  Contains our subsystem definitions for the robot

- [`org.team9432.lib`](robot/src/main/kotlin/org/team9432/lib)

  Contains library utilities that can be used across multiple robots and seasons

- [`org.team9432.lib.commandbased`](robot/src/main/kotlin/org/team9432/lib/commandbased)

  A Kotlin implementation of the WPILib command-based subsystem manager

- [`org.team9432.lib.drivers`](robot/src/main/kotlin/org/team9432/lib/drivers)

  Kotlin wrappers around frequently used devices

## Inspiration

- Team 6328's swerve drive AdvantageKit example