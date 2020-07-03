package OpModes.Examples;

import java.util.*;

import Hardware.*;
import Hardware.Packets.HardwareData;
import Hardware.Packets.SensorData;
import MathUtils.*;
import Motion.PurePursuitOptimized.PurePursuitOptimizedBuilder;
import Motion.Terminators.OrientationTerminator;
import Motion.Terminators.Terminator;
import Odometry.*;
import OpModes.*;
import State.*;
import State.EventSystem.LinearTriggerBuilder;

public class ExampleAutonomous extends BasicOpmode {
    Vector3 position, velocity;
    Odometer odometer;
    public ExampleAutonomous() {
        super(new SkystoneHardware());
    }

    @Override
    public void setup() {
        position = Vector3.ZERO();
        velocity = Vector3.ZERO();
        odometer = new ConstantVOdometer(stateMachine, position, velocity);
        eventSystem.onStart("Odometer", odometer);

        opmodeVariables.integers.put("count", 0);

        HashMap<String, DriveState> driveStates = new HashMap<>();
        driveStates.put("First Movement", new PurePursuitOptimizedBuilder(stateMachine, position)
                .addTarget(new Vector2(-10, 30))
                .addTarget(new Vector2(-20, 30))
                .addTarget(new Vector2(-20, 50))
                .setSpeed(0.4)
                .complete());
        driveStates.put("Second Movement", new PurePursuitOptimizedBuilder(stateMachine, position)
                .addTarget(new Vector2(-20, 50))
                .addTarget(new Vector2(-10, 90))
                .addTarget(new Vector2(-15, 20))
                .setSpeed(0.4)
                .complete());
        driveStates.put("Third Movement", new PurePursuitOptimizedBuilder(stateMachine, position)
                .addTarget(new Vector2(-15, 20))
                .addTarget(new Vector2(-15, 80))
                .setSpeed(0.4)
                .complete());
        driveStates.put("Fourth Movement", new PurePursuitOptimizedBuilder(stateMachine, position)
                .addTarget(new Vector2(-15, 80))
                .addTarget(new Vector2(-20, 30))
                .addTarget(new Vector2(-10, 70))
                .setSpeed(0.4)
                .complete());
        stateMachine.appendDriveStates(driveStates);

        LinearTriggerBuilder builder = new LinearTriggerBuilder(stateMachine, opmodeVariables, "count");

        eventSystem.onTrigger(builder.setOrder(1).disableOnEndTrigger().build(), "Move 1", new LogicState(stateMachine){
            Terminator orientationTerminator;
            @Override
            public void init(SensorData sensorData, HardwareData hardwareData) {
                stateMachine.setActiveDriveState("First Movement");
                orientationTerminator = new OrientationTerminator(position, new Vector3(-20, 50, 0), 5);
            }

            @Override
            public void update(SensorData sensorData, HardwareData hardwareData) {
                if(orientationTerminator.shouldTerminate(sensorData, hardwareData)){
                    opmodeVariables.integers.put("count", 2);
                    stateMachine.deactivateState("First Movement");
                }
            }
        });

        eventSystem.onTrigger(builder.setOrder(2).disableOnEndTrigger().build(), "Move 2", new LogicState(stateMachine){
            Terminator orientationTerminator;
            @Override
            public void init(SensorData sensorData, HardwareData hardwareData) {
                stateMachine.setActiveDriveState("Second Movement");
                orientationTerminator = new OrientationTerminator(position, new Vector3(-15, 20, 0), 5);
            }

            @Override
            public void update(SensorData sensorData, HardwareData hardwareData) {
                if(orientationTerminator.shouldTerminate(sensorData, hardwareData)){
                    opmodeVariables.integers.put("count", 3);
                    stateMachine.deactivateState("Second Movement");
                }
            }
        });

        eventSystem.onTrigger(builder.setOrder(3).disableOnEndTrigger().build(), "Move 3", new LogicState(stateMachine){
            Terminator orientationTerminator;
            @Override
            public void init(SensorData sensorData, HardwareData hardwareData) {
                stateMachine.setActiveDriveState("Third Movement");
                orientationTerminator = new OrientationTerminator(position, new Vector3(-15, 80, 0), 5);
            }

            @Override
            public void update(SensorData sensorData, HardwareData hardwareData) {
                if(orientationTerminator.shouldTerminate(sensorData, hardwareData)){
                    opmodeVariables.integers.put("count", 4);
                    stateMachine.deactivateState("Third Movement");
                }
            }
        });

        eventSystem.onTrigger(builder.setOrder(4).disableOnEndTrigger().build(), "Move 4", new LogicState(stateMachine){
            Terminator orientationTerminator;
            @Override
            public void init(SensorData sensorData, HardwareData hardwareData) {
                stateMachine.setActiveDriveState("Fourth Movement");
                orientationTerminator = new OrientationTerminator(position, new Vector3(-10, 70, 0), 5);
            }

            @Override
            public void update(SensorData sensorData, HardwareData hardwareData) {
                if(orientationTerminator.shouldTerminate(sensorData, hardwareData)){
                    opmodeVariables.integers.put("count", 5);
                    stateMachine.deactivateState("Fourth Movement");
                }
            }
        });

        eventSystem.onTrigger(builder.setOrder(5).disableOnEndTrigger().build(), "End", new LogicState(stateMachine) {
            @Override
            public void init(SensorData sensorData, HardwareData hardwareData) {
                stateMachine.appendDriveState("Stop", new VelocityDriveState(stateMachine) {
                    @Override
                    public Vector3 getVelocities() {
                        return Vector3.ZERO();
                    }

                    @Override
                    public void update(SensorData sensorData, HardwareData hardwareData) {

                    }
                });
                stateMachine.setActiveDriveState("Stop");
            }

            @Override
            public void update(SensorData sensorData, HardwareData hardwareData) {
                telemetry.addData("Finished", true);
            }
        });

        eventSystem.onStart("startCount", new SingleLogicState(stateMachine) {
            @Override
            public void main(SensorData sensorData, HardwareData hardwareData) {
                opmodeVariables.integers.put("count", 1);
            }
        });

        eventSystem.onStart("telemetryData", new LogicState(stateMachine) {
            @Override
            public void update(SensorData sensorData, HardwareData hardwareData) {
                telemetry.addData("Position", position);
                telemetry.addData("Count", opmodeVariables.integers.get("count"));
            }
        });
    }
}
