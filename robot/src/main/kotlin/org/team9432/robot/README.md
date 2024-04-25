## org/team9432/robot/

This package contains season-specific robot code.

---

### Notable Packages

- [`org.team9432.robot.auto`](auto) Auto routines, constants, and dashboard builder.
- [`org.team9432.robot.oi`](oi) Operator interface, has button bindings and switch control.
- [`org.team9432.robot.sensors`](sensors) Contains logged AdvantageKit-style classes for all the sensors on the robot.
- [`org.team9432.robot.LEDState.kt`](LEDState.kt) Defines what leds are active and when,
  uses [our led library](../lib/led).
- [`org.team9432.robot.RobotState.kt`](RobotState.kt) Tracks robot states, including note position and beam break
  states.
- [`org.team9432.robot.RobotPositon.kt`](RobotPosition.kt) Tracks robot position and provides utility methods to use it
  elsewhere.