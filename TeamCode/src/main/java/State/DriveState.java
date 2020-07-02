package State;

import MathUtils.Vector4;

public abstract class DriveState extends LogicState {
    public abstract Vector4 getDriveVelocities();
}
