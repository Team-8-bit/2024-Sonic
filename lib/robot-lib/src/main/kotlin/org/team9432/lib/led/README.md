## org/team9432/lib/led/

This package contains a custom led management library that allows for complex control and patterns using kotlin
coroutines.

---

### How it works

The led strip is split up into sections, and each section can run an animation. Sections can overlap as well, and each
animation is given a priority to determine what to do in these scenarios.

Each running animation has a list of the ideal colors of the leds it manages, and the animation manager is responsible
for combining those lists based on the priority of the animations being run.

Each time the strip attempts to "render" the current pattern, it finds the highest priority animation running that uses
each light on the strip and takes the color from there.

When each pattern runs is determined by using an [AnimationBindScope](management/AnimationBindScope.kt), the
implementation from this year is available [here](../../robot/LEDState.kt). Inspired by 6328's way of writing led
code, it defines which animation is running as a series of nested if/else statements, and animations are stopped/started
by running the update() method.

### Packages

- [`org.team9432.lib.led.animations`](animations) All of the currently defined animations.
- [`org.team9432.lib.led.color`](color) Color control that allows colors to be defined in RGB or HSV and used
  interchangeably throughout the code.
- [`org.team9432.lib.led.management`](management) The animation manager and related classes.
- [`org.team9432.lib.led.strip`](strip) Code for actually interfacing with the led strip on the robot.