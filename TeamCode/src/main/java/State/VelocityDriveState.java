package State;

import Hardware.RobotSystems.*;
import MathUtils.*;

public abstract class VelocityDriveState extends DriveState{

    @Override
    public Vector4 getDriveVelocities() {
        return MecanumSystem.translate(getVelocities());
    }

    public abstract Vector3 getVelocities();
}
