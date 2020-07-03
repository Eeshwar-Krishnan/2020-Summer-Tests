package State;

import Hardware.Packets.HardwareData;
import Hardware.Packets.SensorData;
import Odometry.Odometer;

/**
 * Orientation is a pre-made odometry logic state that updates the given odometer every frame
 */

public class Orientation extends LogicState {
    private Odometer odometer;
    public Orientation(StateMachine stateMachine, Odometer odometer) {
        super(stateMachine);
        this.odometer = odometer;
    }

    @Override
    public void update(SensorData sensorData, HardwareData hardwareData) {
        odometer.update(sensorData, hardwareData);
    }
}
