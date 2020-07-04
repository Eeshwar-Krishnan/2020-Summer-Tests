package Hardware;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.hardware.*;
import com.qualcomm.robotcore.util.RobotLog;

import Hardware.Packets.*;
import Hardware.SmartDevices.SmartEncoder.SmartEncoder;
import Hardware.SmartDevices.SmartEncoder.SmartEncoderConfiguration;
import Hardware.SmartDevices.SmartIMU.SmartIMU;
import Hardware.SmartDevices.SmartIMU.SmartIMUConfiguration;
import Hardware.SmartDevices.SmartMotor.*;

public class SkystoneHardware extends Hardware {
    @Override
    public void registerDevices(HardwareMap map) {
        if(registeredDevices.contains(HardwareDevices.DRIVE_MOTORS)){
            smartDevices.put("Front Left", new SmartMotor(map.dcMotor.get("ll"), new SmartMotorConfiguration()));
            smartDevices.put("Front Right", new SmartMotor(map.dcMotor.get("lr"), new SmartMotorConfiguration()));
            smartDevices.put("Back Left", new SmartMotor(map.dcMotor.get("tl"), new SmartMotorConfiguration()));
            smartDevices.put("Back Right", new SmartMotor(map.dcMotor.get("tr"), new SmartMotorConfiguration()));
        }
        if(registeredDevices.contains(HardwareDevices.GYRO)){
            smartDevices.put("IMU", new SmartIMU(map.get(BNO055IMU.class, "imu"), new SmartIMUConfiguration().setAngleUnit(SmartIMU.AngleUnit.RADIANS)));
        }
        if(registeredDevices.contains(HardwareDevices.INTAKE)){
            smartDevices.put("Intake Left", new SmartMotor(map.dcMotor.get("leftIntake"), new SmartMotorConfiguration().reverseDirection()));
            smartDevices.put("Intake Right", new SmartMotor(map.dcMotor.get("rightIntake"), new SmartMotorConfiguration()));
        }
        if(registeredDevices.contains(HardwareDevices.ODOMETRY)){
            smartDevices.put("Odometry Left", new SmartEncoder(map.dcMotor.get("leftIntake"), new SmartEncoderConfiguration().reverseDirection()));
            smartDevices.put("Odometry Right", new SmartEncoder(map.dcMotor.get("rightIntake"), new SmartEncoderConfiguration()));
            smartDevices.put("Odometry Aux", new SmartEncoder(map.dcMotor.get("ll"), new SmartEncoderConfiguration()));
        }
    }

    @Override
    public void setHardware(HardwareData hardware) {
        if(enabledDevices.contains(HardwareDevices.DRIVE_MOTORS)){
            smartDevices.get("Front Left", SmartMotor.class).setPower(hardware.getFl());
            smartDevices.get("Front Right", SmartMotor.class).setPower(hardware.getFr());
            smartDevices.get("Back Left", SmartMotor.class).setPower(hardware.getBl());
            smartDevices.get("Back Right", SmartMotor.class).setPower(hardware.getBr());
        }
        if(enabledDevices.contains(HardwareDevices.INTAKE)){
            smartDevices.get("Intake Left", SmartMotor.class).setPower(hardware.getIntakeLeft());
            smartDevices.get("Intake Right", SmartMotor.class).setPower(hardware.getIntakeRight());
        }
    }

    @Override
    public void setSensors(SensorData sensorData) {
        if(enabledDevices.contains(HardwareDevices.GYRO)){
            sensorData.setGyro(smartDevices.get("IMU", SmartIMU.class).getHeading());
        }
        if(enabledDevices.contains(HardwareDevices.ODOMETRY)){
            sensorData.setOdometryLeft(smartDevices.get("Odometry Left", SmartEncoder.class).getCurrentPosition());
            sensorData.setOdometryRight(smartDevices.get("Odometry Right", SmartEncoder.class).getCurrentPosition());
            sensorData.setOdometryAux(smartDevices.get("Odometry Aux", SmartEncoder.class).getCurrentPosition());
        }
    }
}
