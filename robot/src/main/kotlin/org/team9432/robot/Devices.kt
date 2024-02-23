package org.team9432.robot

object Devices {
    const val PIGEON = 1

    // Drivetrain
    const val FRONT_LEFT_CANCODER = 5
    const val FRONT_RIGHT_CANCODER = 6
    const val BACK_LEFT_CANCODER = 7
    const val BACK_RIGHT_CANCODER = 8

    const val FRONT_LEFT_DRIVE = 11
    const val FRONT_LEFT_STEER = 12
    const val FRONT_RIGHT_DRIVE = 13
    const val FRONT_RIGHT_STEER = 14
    const val BACK_LEFT_DRIVE = 15
    const val BACK_LEFT_STEER = 16
    const val BACK_RIGHT_DRIVE = 17
    const val BACK_RIGHT_STEER = 18

    // Hopper
    const val HOPPER_ID = 21
    val HOPPER_AMP_SIDE_BEAMBREAK_PORT: Nothing = TODO()
    val HOPPER_SHOOTER_SIDE_BEAMBREAK_PORT: Nothing = TODO()

    // Intake
    const val AMP_SIDE_INTAKE_ID = 31
    const val SPEAKER_SIDE_INTAKE_ID = 32
    val INTAKE_AMP_SIDE_BEAMBREAK_PORT: Nothing = TODO()
    val INTAKE_SHOOTER_SIDE_BEAMBREAK_PORT: Nothing = TODO()
    val INTAKE_CENTER_BEAMBREAK_PORT: Nothing = TODO()

    // Amp
    const val AMP_ID = 41

    // Speaker shooter
    const val LEFT_SHOOTER_ID = 51
    const val RIGHT_SHOOTER_ID = 52

    // Hood
    const val HOOD_ID = 53

    // Climbers
    const val LEFT_CLIMBER_ID = 61
    const val RIGHT_CLIMBER_ID = 62
    val LEFT_CLIMBER_LIMIT_PORT: Nothing = TODO()
    val RIGHT_CLIMBER_LIMIT_PORT: Nothing = TODO()

    // Limelight
    const val LIMELIGHT_MOTOR_ID = 60
}