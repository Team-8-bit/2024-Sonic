# 2024-Sonic

---
_Code for Team 8-Bit's 2024 robot, Sonic_

## Directory Functions

- [`annotation/`](annotation/src/main/kotlin/org/team9432/lib/annotation) Contains a custom annotation processor to
  integrate AdvantageKit with Kotlin code
- [`robot/`](robot/src/main/kotlin/org/team9432) Contains our robot code for the season

## Packages

- [`org.team9432.robot`](robot/src/main/kotlin/org/team9432/robot) Season-specific robot code and features
    - [`org.team9432.robot.led`](robot/src/main/kotlin/org/team9432/robot/led) LED animation management and control
    - [`org.team9432.robot.subsystems`](robot/src/main/kotlin/org/team9432/robot/subsystems) Subsystem code
    - [`org.team9432.robot.commands`](robot/src/main/kotlin/org/team9432/robot/commands) Commands for each subsystem of
      the robot
- [`org.team9432.lib`](robot/src/main/kotlin/org/team9432/lib) Contains library utilities that can be used across
  multiple robots and seasons
    - [`org.team9432.lib.commandbased`](robot/src/main/kotlin/org/team9432/lib/commandbased) A Kotlin implementation of
      the WPILib command-based subsystem manager with custom features
    - [`org.team9432.lib.wrappers`](robot/src/main/kotlin/org/team9432/lib/wrappers)  Kotlin wrappers around frequently
      used devices

## Inspiration

- Team 1678's readme structure
- Team 6328's AdvantageKit swerve drive example
- Team 95's drivetrain skew fix
- Team 6328's led control
- Team 254's motor factories
- Team 4099's unit library
- Team 4481's led color classes
- Team 3847's vision filtering