package org.team9432.lib.geometry

import edu.wpi.first.math.geometry.Pose2d
import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.math.geometry.Translation2d
import org.team9432.lib.unit.Angle
import org.team9432.lib.unit.Length
import org.team9432.lib.unit.inMeters
import org.team9432.lib.unit.asRotation2d

fun Pose2d(x: Length, y: Length, rotation: Rotation2d) = Pose2d(x.inMeters, y.inMeters, rotation)
fun Pose2d(x: Length, y: Length, rotation: Angle) = Pose2d(x.inMeters, y.inMeters, rotation.asRotation2d)
fun Pose2d(translation: Translation2d, rotation: Angle) = Pose2d(translation, rotation.asRotation2d)
fun Translation2d(x: Length, y: Length) = Translation2d(x.inMeters, y.inMeters)