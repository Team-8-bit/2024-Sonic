package org.team9432.robot.led.ledinterface

import org.team9432.robot.led.LEDSection
import org.team9432.robot.led.color.PixelColor

fun LEDSection.forEachColor(update: PixelColor.() -> Unit) {
    indices.forEach { index -> get(index).update() }
}

fun LEDSection.applyToIndex(index: Int, update: PixelColor.() -> Unit) = get(index).update()