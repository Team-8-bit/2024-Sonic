package org.team9432.lib.wrappers.cancoder

import com.ctre.phoenix6.BaseStatusSignal
import com.ctre.phoenix6.StatusSignal
import com.ctre.phoenix6.configs.CANcoderConfiguration
import com.ctre.phoenix6.hardware.CANcoder
import com.ctre.phoenix6.signals.AbsoluteSensorRangeValue
import edu.wpi.first.math.geometry.Rotation2d

class LoggedCancoderIOReal(val config: LoggedCancoder.Config): LoggedCancoderIO {
    private val cancoder = CANcoder(config.canID)

    private val positionSignal: StatusSignal<Double>

    init {
        val cancoderConfig = CANcoderConfiguration()
        cancoderConfig.MagnetSensor.AbsoluteSensorRange = AbsoluteSensorRangeValue.Unsigned_0To1
        cancoder.configurator.apply(cancoderConfig)

        positionSignal = cancoder.absolutePosition
        BaseStatusSignal.setUpdateFrequencyForAll(50.0, positionSignal)
        cancoder.optimizeBusUtilization()
    }

    override fun updateInputs(inputs: LoggedCancoderIO.CancoderIOInputs) {
        BaseStatusSignal.refreshAll(positionSignal)

        inputs.position = Rotation2d.fromRotations(positionSignal.valueAsDouble).minus(config.encoderOffset) / config.gearRatio
    }
}