package frc.robot;

import java.util.concurrent.atomic.AtomicReference;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import frc.robot.kinematics.Speeds;

public class Autonomous {
    private static final double STOP_WITHIN = 0.05;
    private static final double MAX_MOVEMENT_POWER = 0.3;
    // TODO: Depends on maxAcceleration, so maybe should be somewhere else.
    private static final double BRAKING_DISTANCE = 1.5;

    private static final double STOP_WITHIN_DEGREES = 2;
    // TODO: Why does this affect how much it turns?
    private static final double MAX_ROTATION_POWER = 0.1;
    private static final double BRAKING_DEGREES = 30;
    private static final double ROTATION_ERROR_FACTOR = 1.;

    private Movement movement;
    private double deltaTime;
    private Shooter shooter;
    private Pneumatics amp;

    private Command command;

    public Autonomous(Movement movement, double deltaTime, Shooter shooter, Pneumatics amp) {
        this.movement = movement;
        this.deltaTime = deltaTime;
        this.shooter = shooter;
        this.amp = amp;
    }

    public void init() {
        command = program();
        command.schedule();
    }

    public void periodic(double deltaTime) {
    }

    public void cancel() {
        if (command != null) {
            command.cancel();
            command = null;
        }
    }

    private Command program() {
         return ampThenPastBlack();
    }

    private Command ampThenPastBlack() {
        var startToAmpLong = 1.0;
        var startToAmpShort = 0.5;
        var startToBlack = 2;

        return Commands.sequence(
                move(-startToAmpLong),
                rotate(-90),
                move(-startToAmpShort),
                amp.activateFor(0.75),
                move(startToAmpShort),
                rotate(-90),
                move(startToBlack - startToAmpLong)
        );

    }

    private Command move(double distanceM) {
        var traveled = new AtomicReference<Double>(0.);

        return Commands.sequence(
                Commands.waitUntil(() -> {
                    var error = distanceM - traveled.get();
                    // TODO: Get rid of this?
                    if (Math.abs(error) < STOP_WITHIN) {
                        return true;
                    }

                    var percentOfBrakingDistance = MathUtil.clamp(error / BRAKING_DISTANCE, -MAX_MOVEMENT_POWER,
                            MAX_MOVEMENT_POWER);
                    var speed = new Speeds(percentOfBrakingDistance, percentOfBrakingDistance);

                    var actualSpeed = movement.move(speed, deltaTime);
                    var actualMovement = actualSpeed.left * deltaTime * Hardware.ONE_SECOND_MAX_POWER_M;
                    traveled.set(traveled.get() + actualMovement);

                    return false;
                }),
                stop());
    }

    private Command stop() {
        return Commands.waitUntil(() -> {
            movement.move(new Speeds(), deltaTime);
            return !movement.isMoving();
        });
    }

    private Command rotate(double degrees) {
        var rotated = new AtomicReference<Double>(0.);

        return Commands.sequence(
                Commands.waitUntil(() -> {
                    var error = degrees - rotated.get();
                    // TODO: Get rid of this?
                    if (Math.abs(error) < STOP_WITHIN_DEGREES) {
                        return true;
                    }

                    var percentOfBrakingDegrees = MathUtil.clamp(error / BRAKING_DEGREES, -MAX_ROTATION_POWER,
                            MAX_ROTATION_POWER);
                    var speed = new Speeds(-percentOfBrakingDegrees, percentOfBrakingDegrees);

                    var actualSpeed = movement.move(speed, deltaTime);
                    var distanceMoved = actualSpeed.right * deltaTime * Hardware.ONE_SECOND_MAX_POWER_M / 2;
                    var actualRotation = distanceMoved / (2 * Math.PI * Hardware.DISTANCE_FROM_CENTER_TO_WHEEL) * 360
                            * ROTATION_ERROR_FACTOR;
                    rotated.set(rotated.get() + actualRotation);

                    return false;
                }), stop());
    }
}
