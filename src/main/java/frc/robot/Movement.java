package frc.robot;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import frc.robot.kinematics.Speeds;
import frc.robot.motor.Motor;

public class Movement {

    private Pneumatics gearShift;
    private Motor leftDrive;
    private Motor rightDrive;
    private DriveTrain driveTrain;

    private Speeds lastSpeeds = new Speeds();
    private boolean shifting = false;
    private Gear currentGear;

    public Movement(Motor leftDrive, Motor rightDrive, Pneumatics gearShift, DriveTrain driveTrain) {
        this.leftDrive = leftDrive;
        this.rightDrive = rightDrive;
        this.gearShift = gearShift;
        this.driveTrain = driveTrain;
    }

    void shiftTo(Gear gear) {
        shiftToCommand(gear).schedule();
    }

    Command shiftToCommand(Gear gear) {
        if (this.shifting) {
            // TODO : Should throw exception during testing.
            return Commands.none();
        }

        return Commands.sequence(
                Commands.runOnce(() -> this.gearShift.setIf(gear == Gear.HIGH)),
                Commands.waitSeconds(Hardware.BEGIN_SHIFT_WAIT_S),
                Commands.runOnce(() -> {
                    this.shifting = true;
                    this.leftDrive.set(0);
                    this.rightDrive.set(0);
                }),
                Commands.waitSeconds(Hardware.SHIFT_DURATION_S),
                Commands.runOnce(() -> {
                    this.shifting = false;
                    this.currentGear = gear;
                }));
    }

    public Speeds move(Speeds desiredSpeeds, double deltaTime) {
        if (this.shifting) {
            return new Speeds();
        }

        var activity = driveTrain.towardSpeeds(desiredSpeeds, lastSpeeds, deltaTime, currentGear);
        if (activity.shiftTo != null) {
            shiftTo(activity.shiftTo);
        }

        lastSpeeds = activity.logicalSpeeds;
        leftDrive.set(activity.motorSpeeds().left);
        rightDrive.set(activity.motorSpeeds().right);

        return activity.logicalSpeeds;
    }

    public boolean isMoving() {
        boolean isStill = lastSpeeds.left == 0. && lastSpeeds.right == 0.;
        return !isStill;
    }
}
