## org/team9432/robot/subsystems/

This package contains subsystem code for each part of our robot.

We use [motor wrappers to log inputs](../../lib/wrappers/neo/LoggedNeo.kt) instead of structuring each subsystem in the
traditional AdvantageKit style. This is because all of our subsystems were recording the same outputs from the motors,
and this reduces several boilerplate files that weren't needed.

---

### Subsystem Details

- Drivetrain
    - SDS Mk4i with NEO Vortex drive, NEO steer.
- Amp
    - Just a simple pair of rollers powered by one NEO, fed from the superstructure.
- Hood
    - Adjusts angle of the shooter, powered by one NEO.
- Shooter
    - Speaker scorer, one NEO Vortex on each side for spin inspired by 6328.
- Superstructure
    - Intakes and hopper mechanism, collects notes and moves them to different positions in the robot.