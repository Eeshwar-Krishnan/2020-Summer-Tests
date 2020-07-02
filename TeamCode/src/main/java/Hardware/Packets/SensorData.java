package Hardware.Packets;

public class SensorData {
    private double odometryLeft, odometryRight, odometryAux;
    public SensorData(){
        odometryAux = 0;
        odometryLeft = 0;
        odometryRight = 0;
    }

    public double getOdometryAux() {
        return odometryAux;
    }

    public void setOdometryAux(double odometryAux) {
        this.odometryAux = odometryAux;
    }

    public double getOdometryLeft() {
        return odometryLeft;
    }

    public void setOdometryLeft(double odometryLeft) {
        this.odometryLeft = odometryLeft;
    }

    public void setOdometryRight(double odometryRight) {
        this.odometryRight = odometryRight;
    }

    public double getOdometryRight() {
        return odometryRight;
    }
}
