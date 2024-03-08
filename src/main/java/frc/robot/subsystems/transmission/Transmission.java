package frc.robot.subsystems.transmission;

import edu.wpi.first.math.MathUtil;

public class Transmission {
    public class TransmissionReturn {
        public Double x;
        public Double y;
        public boolean shiftToHigh = false;
        public boolean shiftToLow = false;

        public TransmissionReturn(Double x, Double y) {
            this.x = x;
            this.y = y;
        }
    }

    private double highShiftThreshold;
    private double lowShiftThreshold;
    private boolean isHighGear;
    private double lowToHighGearRatio;
    
    public Transmission(double highShiftThreshold, double lowShiftThreshold, double lowToHighGearRatio) {
        this.highShiftThreshold = highShiftThreshold;
        this.lowShiftThreshold = lowShiftThreshold;
        this.lowToHighGearRatio = lowToHighGearRatio;
    }

    public TransmissionReturn robotControl(Double controllerX, Double controllerY) {
        double powerY = MathUtil.clamp(controllerY, -1., 1.);
        double powerX = MathUtil.clamp(controllerX, -1., 1.);

        if (!isHighGear) {
            powerY *= lowToHighGearRatio;
            powerX *= lowToHighGearRatio;
        }

        var transmissionReturn = new TransmissionReturn(powerX, powerY);

        var turning = Math.signum(controllerX) != Math.signum(controllerY);

        var maxSpeed = Math.max(Math.abs(powerX), Math.abs(powerY));
        boolean shouldBeHigh = maxSpeed > highShiftThreshold;
        boolean shouldBeLow = maxSpeed < lowShiftThreshold;

        if (turning) {
            return transmissionReturn;
        }

        if (shouldBeHigh && !isHighGear) {
            transmissionReturn.shiftToHigh = true;
            isHighGear = true;
        }
        if (shouldBeLow && isHighGear) {
            transmissionReturn.shiftToLow = true;
            isHighGear = false;
        }

        return transmissionReturn;
    }
}