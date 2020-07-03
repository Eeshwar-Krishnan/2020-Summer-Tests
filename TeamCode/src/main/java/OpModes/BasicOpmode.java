package OpModes;

import com.qualcomm.robotcore.eventloop.opmode.*;

import Hardware.*;
import Hardware.Packets.*;
import State.EventSystem.*;
import State.*;

/**
 * Basic OpMode
 * All OpMode classes should extend this class
 * Contains setup for the statemachine, eventsystem, and hardware
 * Updates hardware by itself, and updates telemetry by itself
 */

public abstract class BasicOpmode extends LinearOpMode {
    public StateMachine stateMachine;
    public EventSystem eventSystem;
    public Hardware hardware;
    private SensorData sensorData;
    private HardwareData hardwareData;
    private boolean triggeredRun;

    public BasicOpmode(Hardware hardware){
        this.hardware = hardware;
        hardware.attachOpmode(this);
        stateMachine = new StateMachine();
        eventSystem = new EventSystem(stateMachine);
        triggeredRun = false;
    }

    @Override
    public void runOpMode() {
        sensorData = new SensorData();
        hardwareData = new HardwareData();
        setup();
        hardware.init();
        Thread robotThread = new Thread(hardware);
        eventSystem.triggerInit();
        while(opModeIsActive()){
            if(isStarted() && !triggeredRun){
                eventSystem.triggerStart();
                triggeredRun = true;
            }
            sensorData = hardware.getSensorData();
            robotThread.start();
            stateMachine.update(sensorData, hardwareData);
            eventSystem.update(sensorData, hardwareData);
            hardwareData.setDriveMotors(stateMachine.getDriveVelocities());
            hardware.setHardware(hardwareData);
            telemetry.update();
        }
    }

    public abstract void setup();
}
