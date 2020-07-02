package State;

import Hardware.Packets.HardwareData;
import Hardware.Packets.SensorData;

public abstract class SingleLogicState extends LogicState {
    public SingleLogicState(StateMachine stateMachine) {
        super(stateMachine);
    }

    public abstract void main(SensorData sensorData, HardwareData hardwareData);

    @Override
    public void update(SensorData sensorData, HardwareData hardwareData) {
        main(sensorData, hardwareData);
        deactivateThis();
    }
}
