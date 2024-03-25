package org.team9432.lib.geometry

import edu.wpi.first.math.geometry.Pose2d
import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.math.geometry.Translation2d
import org.team9432.lib.unit.Length
import org.team9432.lib.unit.inMeters

fun Pose2d(x: Length, y: Length, rotation: Rotation2d) = Pose2d(x.inMeters, y.inMeters, rotation)
fun Translation2d(x: Length, y: Length) = Translation2d(x.inMeters, y.inMeters)