package Hardware.Packets;

import MathUtils.Vector4;

public class HardwareData {
    private double bl, br, fl, fr;
    public HardwareData(){
        bl = 0;
        br = 0;
        fl = 0;
        fr = 0;
    }

    public void setDriveMotors(Vector4 powers){
        fl = powers.getA();
        fr = powers.getB();
        bl = powers.getC();
        br = powers.getD();
    }

    public double getBl() {
        return bl;
    }

    public double getBr() {
        return br;
    }

    public double getFl() {
        return fl;
    }

    public double getFr() {
        return fr;
    }
}
