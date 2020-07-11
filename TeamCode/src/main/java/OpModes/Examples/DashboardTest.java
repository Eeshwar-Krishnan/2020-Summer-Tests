package OpModes.Examples;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.robotcontroller.internal.WebInterface.InterfaceHandler;

import Debug.Dashboard.Dashboard;
import Hardware.*;
import Hardware.Packets.HardwareData;
import Hardware.Packets.SensorData;
import MathUtils.Vector3;
import OpModes.*;
import State.LogicState;
@TeleOp
public class DashboardTest extends BasicOpmode {
    public DashboardTest() {
        super(new SkystoneHardware());
    }

    @Override
    public void setup() {
        eventSystem.onInit("init", new LogicState(stateMachine) {
            @Override
            public void update(SensorData sensorData, HardwareData hardwareData) {
                Dashboard.addData("Status", "init");
                Dashboard.setPosition(Vector3.ZERO());
                if(isStarted()){
                    deactivateThis();
                }
            }
        });
        eventSystem.onStart("start", new LogicState(stateMachine) {
            Vector3 position = Vector3.ZERO();
            double x = 0, y = 0, r = 0;
            @Override
            public void update(SensorData sensorData, HardwareData hardwareData) {
                Dashboard.addData("Status", "Start");
                Dashboard.setPosition(position);
                Gamepad gamepad = Dashboard.getVGamepad();
                x += gamepad.left_stick_x;
                y += gamepad.left_stick_y;
                r += gamepad.right_stick_x;
                position.set(x, y, r);
                Dashboard.addData("Position", position.toString());
                Dashboard.addData("Click", InterfaceHandler.getInstance().getMousePos());
                Dashboard.addData("FPS", fps);
            }
        });
    }
}
