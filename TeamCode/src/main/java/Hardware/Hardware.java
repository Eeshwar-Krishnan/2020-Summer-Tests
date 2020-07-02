package Hardware;

import com.qualcomm.robotcore.eventloop.opmode.*;
import com.qualcomm.robotcore.hardware.*;

import java.lang.reflect.Array;
import java.util.*;

import Hardware.Packets.*;
import Hardware.SmartDevices.*;

public abstract class Hardware implements Runnable {
    public HashMap<String, SmartDevice> smartDevices = new HashMap<>();
    public LinearOpMode opMode;
    private final ArrayList<HardwareData> hardwarePackets;
    private ArrayList<SensorData> sensorPackets;

    public Hardware(LinearOpMode opMode){
        this.opMode = opMode;
        hardwarePackets = new ArrayList<>();
        sensorPackets = new ArrayList<>();
        sensorPackets.add(new SensorData());
    }

    public abstract void registerDevices(HardwareMap map);

    public void init(){
        registerDevices(opMode.hardwareMap);
        for(String s : smartDevices.keySet()){
            smartDevices.get(s).setName(s);
            smartDevices.get(s).calibrate();
        }
    }

    public abstract void setHardware(HardwareData hardware);

    public abstract void setSensors(SensorData sensorData);

    public SensorData getSensorData(){
        SensorData out = sensorPackets.get(0);
        if(sensorPackets.size() > 1){
            sensorPackets.remove(0);
        }
        return out;
    }

    public HashMap<String, SmartDevice> getSmartDevices(){
        return smartDevices;
    }

    public void addHardwarePacket(HardwareData hardware){
        hardwarePackets.add(hardware);
    }

    @Override
    public void run(){
        HardwareData hardwarePacket;
        synchronized (hardwarePackets) {
            hardwarePacket = hardwarePackets.get(0);
            if (hardwarePackets.size() > 1) {
                hardwarePackets.remove(0);
            }
        }
        setHardware(hardwarePacket);
        for(String device : smartDevices.keySet()){
            smartDevices.get(device).update();
        }
        SensorData sensorData = new SensorData();
        setSensors(sensorData);
        sensorPackets.add(sensorData);
    }
}
