package OpModes.Examples;

import Debug.Dashboard.Dashboard;
import Hardware.*;
import Hardware.Packets.HardwareData;
import Hardware.Packets.SensorData;
import MathUtils.*;
import Motion.PurePursuit.*;
import Motion.Terminators.OrientationTerminator;
import Odometry.*;
import OpModes.*;
import State.LogicState;
import State.VelocityDriveState;

public class MovementTest extends BasicOpmode {
    Vector3 position, velocity;
    public MovementTest() {
        super(new SkystoneHardware());
    }

    @Override
    public void setup() {
        position = Vector3.ZERO();
        velocity = Vector3.ZERO();
        eventSystem.onStart("Odometry", new ConstantVOdometer(stateMachine, position, velocity));

        eventSystem.onStart("Move", new PurePursuitBuilder(stateMachine, position)
                .addTarget(new Vector2(-10, 15))
                .addTarget(new Vector2(-5, 40))
                .setSpeed(0.3)
                .complete());

        eventSystem.onInit("Dashboard", new LogicState(stateMachine) {
            @Override
            public void update(SensorData sensorData, HardwareData hardwareData) {
                Dashboard.setPosition(position);
            }
        });

        stateMachine.appendDriveState("Stop", new VelocityDriveState(stateMachine) {
            @Override
            public Vector3 getVelocities() {
                return Vector3.ZERO();
            }

            @Override
            public void update(SensorData sensorData, HardwareData hardwareData) {

            }
        });

        eventSystem.onStart("Move Manager", new LogicState(stateMachine) {
            OrientationTerminator orientationTerminator;

            @Override
            public void init(SensorData sensorData, HardwareData hardwareData) {
                orientationTerminator = new OrientationTerminator(position, new Vector3(-5, 40, 0), 5);
            }

            @Override
            public void update(SensorData sensorData, HardwareData hardwareData) {
                if(orientationTerminator.shouldTerminate(sensorData, hardwareData)){
                    stateMachine.setActiveDriveState("Stop");
                    deactivateThis();
                }
            }
        });
    }
}
