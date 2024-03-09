package frc.robot;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BooleanSupplier;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import frc.robot.kinematics.Speeds;

public class Autonomous {
    private Movement movement;
    private double deltaTime;

    private Command command;

    public Autonomous(Movement movement, double deltaTime) {
        this.movement = movement;
        this.deltaTime = deltaTime;
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
        return Commands.sequence(
                moveForward(Field.ROBOT_STARTING_ZONE_WIDTH + 0.5));
    }

    private Command moveForward(double distance) {
        var traveled = new AtomicReference<Double>(0.);
        // TODO: Make stopping zone configurable.
        var stopAt = distance - 0.5;

        return Commands.sequence(
            Commands.waitUntil(() -> {
                var actualSpeed = movement.move(new Speeds(1., 1.), deltaTime);
                // Since we're moving forward, left == right.
                traveled.set(traveled.get() + actualSpeed.left * deltaTime);
                return traveled.get() >= stopAt;
            }),
            // TODO: Make this more adaptive to actual movement.
            Commands.waitUntil(() -> {
                movement.move(new Speeds(), deltaTime);
                return !movement.isMoving();
            })
        );
    }
}
