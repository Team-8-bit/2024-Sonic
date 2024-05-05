package org.team9432.lib.wrappers.cancoder

class LoggedCancoderIOSim(val config: LoggedCancoder.Config): LoggedCancoderIO {
    override fun updateInputs(inputs: LoggedCancoderIO.CancoderIOInputs) {
        inputs.position = config.simPositionSupplier.invoke()
    }
}