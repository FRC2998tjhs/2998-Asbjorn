package frc.robot.motor;

public class Reverse implements Motor {
    private Motor inner;

    public Reverse(Motor inner) {
        this.inner = inner;
    }

    @Override
    public void set(double percentSpeed) {
        this.inner.set(-percentSpeed);
    }
}
