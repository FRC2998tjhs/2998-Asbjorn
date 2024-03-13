package frc.robot.controller;

import frc.robot.kinematics.Speeds;

public abstract class Controller {
    private double deadzone;
    private double precisionExponent;

    public Controller(double deadzone, double precisionExponent) {
        this.deadzone = deadzone;
        this.precisionExponent = precisionExponent;
    }

    public abstract Speeds desiredSpeeds();

    // Should this live somewhere else?
    double deadzoneAndSmooth(double v) {
        if (v < 0) {
            return -deadzoneAndSmooth(-v);
        }

        if (v < deadzone) {
            return 0.;
        }

        var scaled = (v - deadzone) * (1. / (1. - deadzone));
        return Math.pow(scaled, precisionExponent);
    }
}
