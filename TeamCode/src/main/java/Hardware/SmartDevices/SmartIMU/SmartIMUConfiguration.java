package Hardware.SmartDevices.SmartIMU;

import com.qualcomm.hardware.bosch.BNO055IMU;

public class SmartIMUConfiguration {
    SmartIMU.AngleUnit angleUnit;
    BNO055IMU.SensorMode sensorMode;
    public SmartIMUConfiguration(){
        angleUnit = SmartIMU.AngleUnit.DEGREES;
        sensorMode = BNO055IMU.SensorMode.IMU;
    }

    public SmartIMU.AngleUnit getAngleUnit() {
        return angleUnit;
    }

    public void setAngleUnit(SmartIMU.AngleUnit angleUnit) {
        this.angleUnit = angleUnit;
    }

    public BNO055IMU.SensorMode getSensorMode() {
        return sensorMode;
    }

    public void setSensorMode(BNO055IMU.SensorMode sensorMode) {
        this.sensorMode = sensorMode;
    }

    @Override
    public String toString() {
        return "SmartIMUConfiguration{" +
                "angleUnit=" + angleUnit +
                ", sensorMode=" + sensorMode +
                '}';
    }
}
