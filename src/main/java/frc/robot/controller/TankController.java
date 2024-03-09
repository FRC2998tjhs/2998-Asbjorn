package frc.robot.controller;

import java.util.function.Supplier;

import frc.robot.kinematics.Speeds;

public class TankController extends Controller {
    private Supplier<Double> leftAxis;
    private Supplier<Double> rightAxis;

    public TankController(Supplier<Double> leftAxis, Supplier<Double> rightAxis, double deadzone,
            double precisionExponent) {
        super(deadzone, precisionExponent);
        this.leftAxis = leftAxis;
        this.rightAxis = rightAxis;
    }

    @Override
    public Speeds desiredSpeeds() {
        return new Speeds(
                deadzoneAndSmooth(leftAxis.get()),
                deadzoneAndSmooth(rightAxis.get()));
    }
}