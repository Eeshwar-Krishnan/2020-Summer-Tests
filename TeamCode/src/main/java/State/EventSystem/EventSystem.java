package State.EventSystem;

import java.lang.reflect.Array;
import java.util.*;

import Hardware.Packets.*;
import State.*;

public class EventSystem {
    private StateMachine stateMachine;
    private ArrayList<EventSystemTrigger> triggers;
    private ArrayList<String> initStates, startStates;
    private String initDriveState, startDriveState;
    public EventSystem(StateMachine stateMachine){
        this.stateMachine = stateMachine;
        triggers = new ArrayList<>();
        initStates = new ArrayList<>();
        startStates = new ArrayList<>();
    }

    public void onInit(String state, LogicState logicState){
        stateMachine.appendLogicState(state, logicState);
        initStates.add(state);
    }

    public void onInit(String state, DriveState driveState){
        stateMachine.appendDriveState(state, driveState);
        initDriveState = state;
    }

    public void onStart(String state, LogicState logicState){
        stateMachine.appendLogicState(state, logicState);
        startStates.add(state);
    }

    public void onStart(String state, DriveState driveState){
        stateMachine.appendDriveState(state, driveState);
        startDriveState = state;
    }

    public void addTrigger(EventSystemTrigger eventSystemTrigger){
        triggers.add(eventSystemTrigger);
    }

    public void update(SensorData sensorData, HardwareData hardwareData){
        for(EventSystemTrigger trigger : triggers){
            trigger.update(sensorData, hardwareData);
        }
    }

    public void triggerInit(){
        for(String state : initStates){
            stateMachine.activateLogic(state);
        }
    }

    public void triggerStart(){
        for(String state : startStates){
            stateMachine.activateLogic(state);
        }
    }
}
