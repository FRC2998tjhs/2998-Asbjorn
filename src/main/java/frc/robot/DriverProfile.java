package frc.robot;

public class DriverProfile {
    public static final DriverProfile current = spencer();

    // In % max speed / s;
    public double maxAcceleration = 1.;
    public double deadzone = 0.15;
    public double precisionExponent = 1.5;

    public double shiftUpAt = 0.9;
    public double shiftDownAt = 0.1;

    @SuppressWarnings("unused")
    private static DriverProfile spencer() {
        var result = new DriverProfile();

        return result;
    }

    @SuppressWarnings("unused")
    private static DriverProfile william() {
        var result = new DriverProfile();

        return result;
    }

    @SuppressWarnings("unused")
    private static DriverProfile chris() {
        var result = new DriverProfile();

        return result;
    }

    public double transformLeftJoystick(double y) {
        throw new UnsupportedOperationException("Unimplemented method 'transformLeftJoystick'");
    }

    public Object transformRightJoystick(double y) {
        throw new UnsupportedOperationException("Unimplemented method 'transformRightJoystick'");
    }
}
