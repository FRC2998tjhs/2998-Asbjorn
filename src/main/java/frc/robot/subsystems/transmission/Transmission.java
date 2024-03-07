package frc.robot.subsystems.transmission;

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

    public TransmissionReturn transmissioning(Double controllerX, Double controllerY, double leftSpeed,
            double rightSpeed) {
        return new TransmissionReturn(controllerX, controllerY);
    }
}