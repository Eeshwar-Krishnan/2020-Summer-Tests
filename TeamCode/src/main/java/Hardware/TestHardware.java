package Hardware;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;

import Hardware.Packets.HardwareData;
import Hardware.Packets.SensorData;

public class TestHardware extends Hardware {
    public TestHardware(LinearOpMode opMode) {
        super(opMode);
    }

    @Override
    public void registerDevices(HardwareMap map) {

    }

    @Override
    public void setHardware(HardwareData hardware) {

    }

    @Override
    public void setSensors(SensorData sensorData) {

    }
}
