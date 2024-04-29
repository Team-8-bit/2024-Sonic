package org.team9432.lib.robot

import edu.wpi.first.cameraserver.CameraServerShared
import edu.wpi.first.cameraserver.CameraServerSharedStore
import edu.wpi.first.hal.FRCNetComm.tInstances
import edu.wpi.first.hal.FRCNetComm.tResourceType
import edu.wpi.first.hal.HAL
import edu.wpi.first.hal.HALUtil
import edu.wpi.first.math.MathShared
import edu.wpi.first.math.MathSharedStore
import edu.wpi.first.math.MathUsageId
import edu.wpi.first.networktables.MultiSubscriber
import edu.wpi.first.networktables.NetworkTableInstance
import edu.wpi.first.util.WPIUtilJNI
import edu.wpi.first.wpilibj.DriverStation
import edu.wpi.first.wpilibj.Notifier
import edu.wpi.first.wpilibj.RuntimeType
import edu.wpi.first.wpilibj.livewindow.LiveWindow
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard
import edu.wpi.first.wpilibj.util.WPILibVersion
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking
import org.team9432.lib.coroutines.RIODispatcher
import java.io.File
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.util.concurrent.locks.ReentrantLock
import java.util.function.Supplier
import kotlin.system.exitProcess

/**
 * Implement a Robot Program framework.
 * The RobotBase class is intended to be subclassed to create a robot program.
 * The user must implement [startCompetition], which will be called once and is not expected to exit.
 * The user must also implement [endCompetition], which signals to the code in [.startCompetition] that it should exit.
 *
 * It is not recommended to subclass this class directly - instead subclass IterativeRobotBase or TimedRobot.
 */
abstract class RobotBase protected constructor(): AutoCloseable {
    private val suball: MultiSubscriber

    override fun close() {
        suball.close()
    }

    /** Start the main robot code. This function will be called once and should not exit until signalled by [endCompetition]. */
    abstract fun startCompetition()

    /** Ends the main loop in [startCompetition].  */
    abstract fun endCompetition()

    /**
     * Constructor for a generic robot program. User code can be placed in the constructor that runs
     * before the Autonomous or Operator Control period starts. The constructor will run to completion
     * before Autonomous is entered.
     *
     *
     * This must be used to ensure that the communications code starts. In the future it would be
     * nice to put this code into its own task that loads on boot so ensure that it runs.
     */
    init {
        val inst = NetworkTableInstance.getDefault()
        mainThreadId = Thread.currentThread().id
        setupCameraServerShared()
        setupMathShared()
        // subscribe to "" to force persistent values to propagate to local
        suball = MultiSubscriber(inst, arrayOf(""))
        if (!isSimulation) {
            inst.startServer("/home/lvuser/networktables.json")
        } else {
            inst.startServer()
        }

        // wait for the NT server to actually start
        try {
            var count = 0
            while (inst.networkMode.contains(NetworkTableInstance.NetworkMode.kStarting)) {
                Thread.sleep(10)
                count++
                if (count > 100) {
                    throw InterruptedException()
                }
            }
        } catch (ex: InterruptedException) {
            System.err.println("timed out while waiting for NT server to start")
        }

        LiveWindow.setEnabled(false)
        Shuffleboard.disableActuatorWidgets()
    }

    companion object {
        /** The ID of the main Java thread. This is usually 1, but it is best to make sure. */
        var mainThreadId: Long = -1
            private set

        private fun setupCameraServerShared() {
            val shared: CameraServerShared =
                object: CameraServerShared {
                    override fun reportVideoServer(id: Int) {
                        HAL.report(tResourceType.kResourceType_PCVideoServer, id + 1)
                    }

                    override fun reportUsbCamera(id: Int) {
                        HAL.report(tResourceType.kResourceType_UsbCamera, id + 1)
                    }

                    override fun reportDriverStationError(error: String) {
                        DriverStation.reportError(error, true)
                    }

                    override fun reportAxisCamera(id: Int) {
                        HAL.report(tResourceType.kResourceType_AxisCamera, id + 1)
                    }

                    override fun getRobotMainThreadId(): Long {
                        return mainThreadId
                    }

                    override fun isRoboRIO(): Boolean {
                        return !isSimulation
                    }
                }

            CameraServerSharedStore.setCameraServerShared(shared)
        }

        private fun setupMathShared() {
            MathSharedStore.setMathShared(
                object: MathShared {
                    override fun reportError(error: String, stackTrace: Array<StackTraceElement>) {
                        DriverStation.reportError(error, stackTrace)
                    }

                    override fun reportUsage(id: MathUsageId, count: Int) {
                        when (id) {
                            MathUsageId.kKinematics_DifferentialDrive -> HAL.report(
                                tResourceType.kResourceType_Kinematics,
                                tInstances.kKinematics_DifferentialDrive
                            )

                            MathUsageId.kKinematics_MecanumDrive -> HAL.report(
                                tResourceType.kResourceType_Kinematics, tInstances.kKinematics_MecanumDrive
                            )

                            MathUsageId.kKinematics_SwerveDrive -> HAL.report(
                                tResourceType.kResourceType_Kinematics, tInstances.kKinematics_SwerveDrive
                            )

                            MathUsageId.kTrajectory_TrapezoidProfile -> HAL.report(tResourceType.kResourceType_TrapezoidProfile, count)
                            MathUsageId.kFilter_Linear -> HAL.report(tResourceType.kResourceType_LinearFilter, count)
                            MathUsageId.kOdometry_DifferentialDrive -> HAL.report(
                                tResourceType.kResourceType_Odometry, tInstances.kOdometry_DifferentialDrive
                            )

                            MathUsageId.kOdometry_SwerveDrive -> HAL.report(tResourceType.kResourceType_Odometry, tInstances.kOdometry_SwerveDrive)
                            MathUsageId.kOdometry_MecanumDrive -> HAL.report(tResourceType.kResourceType_Odometry, tInstances.kOdometry_MecanumDrive)
                            MathUsageId.kController_PIDController2 -> HAL.report(tResourceType.kResourceType_PIDController2, count)
                            MathUsageId.kController_ProfiledPIDController -> HAL.report(tResourceType.kResourceType_ProfiledPIDController, count)
                            else -> {}
                        }
                    }

                    override fun getTimestamp(): Double {
                        return WPIUtilJNI.now() * 1.0e-6
                    }
                })
        }

        /** The current runtime type. */
        private val runtimeType: RuntimeType
            get() = RuntimeType.getValue(HALUtil.getHALRuntimeType())

        /** True if the robot is running in simulation. */
        val isSimulation: Boolean
            get() = runtimeType == RuntimeType.kSimulation

        /** If the robot is running in the real world. */
        val isReal: Boolean
            get() {
                val runtimeType = runtimeType
                return runtimeType == RuntimeType.kRoboRIO || runtimeType == RuntimeType.kRoboRIO2
            }

        private val runMutex = ReentrantLock()
        private var robotCopy: RobotBase? = null
        private var suppressExitWarning = false

        /** Run the robot main loop.  */
        private fun <T: RobotBase> runRobot(robotSupplier: Supplier<T>) {
            println("********** Robot program starting **********")

            val robot: T
            try {
                robot = robotSupplier.get()
            } catch (throwable: Throwable) {
                val error = throwable.cause ?: throwable
                val elements = error.stackTrace
                val robotName = elements.firstOrNull()?.className ?: "Unknown"

                DriverStation.reportError("Unhandled exception instantiating robot $robotName $error", elements)
                DriverStation.reportError(
                    """
                        The robot program quit unexpectedly.
                        This is usually due to a code error.
                        The above stacktrace can help determine where the error occurred.
                        See https://wpilib.org/stacktrace for more information.
                    """.trimIndent(),
                    /* printTrace = */ false
                )
                DriverStation.reportError("Could not instantiate robot $robotName!", false)
                return
            }

            runMutex.lock()
            robotCopy = robot
            runMutex.unlock()

            if (!isSimulation) {
                val file = File("/tmp/frc_versions/FRC_Lib_Version.ini")
                try {
                    if (file.exists() && !file.delete()) {
                        throw IOException("Failed to delete FRC_Lib_Version.ini")
                    }

                    if (!file.createNewFile()) {
                        throw IOException("Failed to create new FRC_Lib_Version.ini")
                    }

                    Files.newOutputStream(file.toPath()).use { output ->
                        output.write("Java ".toByteArray(StandardCharsets.UTF_8))
                        output.write(WPILibVersion.Version.toByteArray(StandardCharsets.UTF_8))
                    }
                } catch (ex: IOException) {
                    DriverStation.reportError("Could not write FRC_Lib_Version.ini: $ex", ex.stackTrace)
                }
            }

            var errorOnExit = false
            try {
                robot.startCompetition()
            } catch (throwable: Throwable) {
                val error = throwable.cause ?: throwable
                DriverStation.reportError("Unhandled exception: $error", error.stackTrace)
                errorOnExit = true
            } finally {
                runMutex.lock()
                val suppressExitWarning = suppressExitWarning
                runMutex.unlock()
                if (!suppressExitWarning) {
                    // startCompetition never returns unless exception occurs....
                    DriverStation.reportError(
                        """
                            The robot program quit unexpectedly.
                            This is usually due to a code error.
                            The above stacktrace can help determine where the error occurred.
                            See https://wpilib.org/stacktrace for more information.
                        """.trimIndent(),
                        /* printTrace = */ false
                    )
                    if (errorOnExit) {
                        DriverStation.reportError("The startCompetition() method (or methods called by it) should have handled the exception above.", false)
                    } else {
                        DriverStation.reportError("Unexpected return from startCompetition() method.", false)
                    }
                }
            }
        }

        /**
         * Suppress the "The robot program quit unexpectedly." message.
         *
         * @param value True if exit warning should be suppressed.
         */
        fun suppressExitWarning(value: Boolean) {
            runMutex.lock()
            suppressExitWarning = value
            runMutex.unlock()
        }

        lateinit var coroutineScope: CoroutineScope
            private set

        /** Starting point for the robot. */
        fun startRobot(robotSupplier: Supplier<RobotBase>) {
            if (!HAL.initialize(500, 0)) {
                throw IllegalStateException("Failed to initialize. Terminating")
            }

            // Force refresh DS data
            DriverStation.refreshData()

            if (!Notifier.setHALThreadPriority(true, 40)) {
                DriverStation.reportWarning("Setting HAL Notifier RT priority to 40 failed", false)
            }

            if (HAL.hasMain()) {
                val userCodeThread = Thread(
                    {
                        runBlocking(RIODispatcher) {
                            coroutineScope = this
                            runRobot(robotSupplier)
                        }
                        HAL.exitMain()
                    },
                    "robot main"
                )
                userCodeThread.isDaemon = true
                userCodeThread.start()
                HAL.runMain()
                suppressExitWarning(true)
                runMutex.lock()
                val robot = robotCopy
                runMutex.unlock()
                robot?.endCompetition()

                try {
                    userCodeThread.join(1000)
                } catch (ex: InterruptedException) {
                    Thread.currentThread().interrupt()
                }
            } else {
                runRobot(robotSupplier)
            }

            HAL.shutdown()

            println("code ending!")

            exitProcess(0)
        }
    }
}