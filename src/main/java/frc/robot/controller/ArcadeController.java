package frc.robot.controller;

import frc.robot.kinematics.Speeds;

public class ArcadeController extends Controller {
    public ArcadeController(double deadzone, double precisionExponent) {
        super(deadzone, precisionExponent);
    }

    @Override
    public Speeds desiredSpeeds() {
        throw new UnsupportedOperationException("Unimplemented method 'desiredSpeeds'");
    }
}