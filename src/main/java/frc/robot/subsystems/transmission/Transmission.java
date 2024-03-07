public class Transmission {
    public class TransmissionReturn {
        Double X;
        Double Y;
        boolean switchToHigh = false;
        boolean switchToLow = false;

        public TransmissionReturn(Double x, Double y) {
            X = x;
            Y = y;
        }
    }

    public TransmissionReturn transmissioning(Double controllerX, Double controllerY, double leftSpeed,
            double rightSpeed) {
        return new TransmissionReturn(controllerX, controllerY);
    }
}