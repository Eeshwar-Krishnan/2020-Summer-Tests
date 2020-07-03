package OpModes.Examples;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import Hardware.*;
import Hardware.Packets.HardwareData;
import Hardware.Packets.SensorData;
import MathUtils.Vector3;
import Odometry.ConstantVOdometer;
import Odometry.Odometer;
import OpModes.*;
import State.GamepadDriveState;
import State.LogicState;

/**
 * Example Teleop class
 * Demonstrates the usage of
 * Hardware register and enable functions
 * GamepadDriveState
 * Event System onStart trigger
 */
@TeleOp
public class ExampleTeleop extends BasicOpmode {
    Vector3 position, velocity;
    Odometer odometer;
    public ExampleTeleop() {
        super(new SkystoneHardware());
    }

    @Override
    public void setup() {
        hardware.registerAll();
        hardware.enableAll();
        position = Vector3.ZERO();
        velocity = Vector3.ZERO();
        odometer = new ConstantVOdometer(stateMachine, position, velocity);
        eventSystem.onStart("Drive", new GamepadDriveState(stateMachine, gamepad1));
        eventSystem.onStart("Intake", new LogicState(stateMachine) {
            @Override
            public void update(SensorData sensorData, HardwareData hardwareData) {
                if(gamepad2.right_trigger > 0.1) {
                    hardwareData.setIntakePowers(gamepad2.right_trigger);
                }else if(gamepad2.left_trigger > 0.1){
                    hardwareData.setIntakePowers(-gamepad2.left_trigger);
                }else{
                    hardwareData.setIntakePowers(0);
                }
            }
        });
        eventSystem.onStart("Odometer", odometer);
        eventSystem.onStart("Telemetry", new LogicState(stateMachine) {
            @Override
            public void update(SensorData sensorData, HardwareData hardwareData) {
                telemetry.addData("Position", position);
                telemetry.addData("FPS", fps);
                telemetry.addData("Intake", hardwareData.getIntakeLeft());
            }
        });
    }
}
