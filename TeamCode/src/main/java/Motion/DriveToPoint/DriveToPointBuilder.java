package Motion.DriveToPoint;
import MathUtils.*;
import State.StateMachine;

/**
 * Builds a standard drive to point class, returning a driveState that can be used in the statemachine
 */

public class DriveToPointBuilder {
    private StateMachine stateMachine;
    private double speed;
    private Vector3 position, target;
    public DriveToPointBuilder(StateMachine stateMachine, Vector3 position){
        this.position = position;
        this.stateMachine = stateMachine;
        target = Vector3.ZERO();
    }

    public DriveToPointBuilder setSpeed(double speed){
        this.speed = speed;
        return this;
    }

    public DriveToPointBuilder setTarget(Vector3 target){
        target.set(target);
        return this;
    }

    public DriveToPoint complete(){
        return new DriveToPoint(stateMachine, position, target, speed) {
            @Override
            public void setTarget() {
                localTarget.set(target);
            }
        };
    }
}
