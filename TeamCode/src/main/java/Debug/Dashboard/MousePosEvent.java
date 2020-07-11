package Debug.Dashboard;

import Hardware.Packets.HardwareData;
import Hardware.Packets.SensorData;
import MathUtils.Vector3;
import State.EventSystem.EventSystemTrigger;
import State.StateMachine;

public class MousePosEvent extends EventSystemTrigger {
    private Vector3 prevPos;
    public MousePosEvent(StateMachine stateMachine) {
        super(stateMachine);
        prevPos = Vector3.ZERO();
    }

    @Override
    public boolean trigger(SensorData sensorData, HardwareData hardwareData) {
        boolean trigger = prevPos.equals(Dashboard.getMousePos());
        if(trigger) {
            return false;
        }else{
            prevPos.equals(Dashboard.getMousePos());
            return false;
        }
    }
}
