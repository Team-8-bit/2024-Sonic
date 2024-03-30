package org.team9432.lib.logged.cancoder

class LoggedCancoderIOSim(val config: LoggedCancoder.Config): LoggedCancoderIO {
    override fun updateInputs(inputs: LoggedCancoderIO.CancoderIOInputs) {
        inputs.position = config.simPositionSupplier.invoke()
    }
}