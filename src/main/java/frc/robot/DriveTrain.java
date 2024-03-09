package frc.robot;

import edu.wpi.first.math.MathUtil;
import frc.robot.kinematics.Speeds;

public class DriveTrain {
    public class DriveActivity {
        private double gearScale;

        public Speeds logicalSpeeds = new Speeds();

        public Gear shiftTo = null;

        public DriveActivity(double gearScale) {
            this.gearScale = gearScale;
        }

        public Speeds motorSpeeds() {
            return new Speeds(
                this.logicalSpeeds.left * gearScale,
                this.logicalSpeeds.right * gearScale
            );
        }
    }

    private DriverProfile profile;
    private double gearRatio;

    public DriveTrain(DriverProfile profile, double gearRatio) {
        this.profile = profile;
        this.gearRatio = gearRatio;
    }

    public DriveActivity towardSpeeds(Speeds desiredSpeeds, Speeds lastSpeeds, double deltaTime, Gear currentGear) {
        var gearScale = 1.;
        if (currentGear == Gear.LOW) {
            // We need to put in `gearRatio` more power in low gear.
            gearScale = gearRatio;
        }

        var result = new DriveActivity(gearScale);

        result.logicalSpeeds.left = accelerateTo(desiredSpeeds.left, lastSpeeds.left, deltaTime);
        result.logicalSpeeds.right = accelerateTo(desiredSpeeds.right, lastSpeeds.right, deltaTime);

        var extremeSpeed = Math.max(Math.abs(result.motorSpeeds().left), Math.abs(result.motorSpeeds().right));
        if (extremeSpeed >= profile.shiftUpAt) {
            result.shiftTo = Gear.HIGH;
        }
        if (extremeSpeed <= profile.shiftDownAt) {
            result.shiftTo = Gear.LOW;
        }

        return result;
    }

    private double accelerateTo(double desired, double last, double deltaTime) {
        var diff = desired - last;
        var maxAcceleration = Math.abs(profile.maxAcceleration * deltaTime);

        var actualDelta = MathUtil.clamp(diff, -maxAcceleration, maxAcceleration);
        return MathUtil.clamp(last + actualDelta, -1, 1);
    }
}