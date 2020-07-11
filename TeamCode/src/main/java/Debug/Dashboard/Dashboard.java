package Debug.Dashboard;

import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.robotcontroller.internal.WebInterface.*;
import org.firstinspires.ftc.robotcontroller.internal.WebInterface.VSD.*;

import Debug.Serialization.Vector3Serializer;
import MathUtils.Vector3;

public class Dashboard {
    private static InterfaceHandler instance = InterfaceHandler.getInstance();

    public static void addData(String caption, Object value){
        instance.internalAddTelemetry(caption, value);
    }

    public static void addLine(String line){
        instance.internalAddLine(line);
    }

    public static VirtualSD VirtualSD(){
        return VirtualSD.getInstance();
    }

    public static Vector3 getMousePos(){
        if(Vector3Serializer.isVector3(instance.getMousePos())){
            return Vector3Serializer.deserialize(instance.getMousePos());
        }
        return Vector3.ZERO();
    }

    public static Gamepad getVGamepad(){
        return instance.getVirtualGamepad();
    }

    public static void sendTelemetry(){
        instance.internalUpdateTelemetry();
    }

    public static void setPosition(Vector3 position){
        instance.internalSetPosition(position.toString());
    }
}
