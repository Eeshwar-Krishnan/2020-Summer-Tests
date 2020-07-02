package State;

import Hardware.Packets.HardwareData;
import Hardware.Packets.SensorData;

public abstract class LogicState {
    String name;
    StateMachine stateMachine;

    public LogicState(StateMachine stateMachine){
        this.stateMachine = stateMachine;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void init(SensorData sensorData, HardwareData hardwareData){

    }

    public void deactivateThis(){
        stateMachine.deactivateState(name);
    }

    public void onStop(SensorData sensorData, HardwareData hardwareData){

    }

    public abstract void update(SensorData sensorData, HardwareData hardwareData);

    @Override
    public String toString() {
        return "LogicState{" +
                "name='" + name + '\'' +
                '}';
    }
}
