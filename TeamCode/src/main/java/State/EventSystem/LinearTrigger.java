package State.EventSystem;

import Hardware.Packets.HardwareData;
import Hardware.Packets.SensorData;
import State.StateMachine;

/**
 * Linear Trigger
 * Linear Trigger triggers its states when a given number matches an internal number
 * This is intended to be used in a linear manner, with the external given number incremented by one when the next group needs to be triggered
 * if disableOnEndTrigger is true, the logicstates and drivestates added to the trigger will be disabled when the number changes
 */

public class LinearTrigger extends EventSystemTrigger {
    private int order, currentNumber;
    boolean disableOnEndTrigger;
    public LinearTrigger(StateMachine stateMachine, int order, boolean disableOnEndTrigger) {
        super(stateMachine);
        this.order = order;
        currentNumber = 0;
        this.disableOnEndTrigger = disableOnEndTrigger;
    }

    public void setCurrentNumber(int currentNumber){
        this.currentNumber = currentNumber;
    }

    @Override
    public boolean trigger(SensorData sensorData, HardwareData hardwareData) {
        if(order != currentNumber && disableOnEndTrigger){
            for(String state : states){
                stateMachine.deactivateState(state);
            }
            stateMachine.deactivateState(driveState);
        }
        return order == currentNumber;
    }
}
