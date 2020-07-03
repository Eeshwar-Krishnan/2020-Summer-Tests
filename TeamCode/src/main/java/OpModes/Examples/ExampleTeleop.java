package OpModes.Examples;

import Hardware.*;
import Hardware.Packets.HardwareData;
import Hardware.Packets.SensorData;
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

public class ExampleTeleop extends BasicOpmode {
    public ExampleTeleop() {
        super(new SkystoneHardware());
    }

    @Override
    public void setup() {
        hardware.registerAll();
        hardware.enableAll();
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
    }
}
