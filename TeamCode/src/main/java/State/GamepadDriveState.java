package State;

import com.qualcomm.robotcore.hardware.*;

import Hardware.Packets.*;

import Hardware.RobotSystems.MecanumSystem;
import MathUtils.*;

public class GamepadDriveState extends VelocityDriveState {
    Gamepad gamepad;

    public GamepadDriveState(Gamepad gamepad){
        this.gamepad = gamepad;
    }

    @Override
    public Vector3 getVelocities() {
        return new Vector3(gamepad.left_stick_x, gamepad.left_stick_y, -gamepad.right_stick_x);
    }

    @Override
    public void update(SensorData sensorData, HardwareData hardwareData) {

    }
}
