package Hardware.SmartDevices;

public abstract class SmartDevice {
    String name;

    public SmartDevice(){
        this.name = "Unnamed Device";
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public abstract void calibrate();

    public abstract void update();
}
