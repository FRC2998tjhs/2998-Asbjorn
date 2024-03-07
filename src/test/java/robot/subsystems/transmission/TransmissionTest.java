import frc.robot.subsystems.transmission.Transmission;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TransmissionTest {
    @Test
    public void noInput_isEmpty() {
        var subject = new Transmission(1., 0., 1.);

        var result = subject.robotControl(0., 0., 0., 0.);

        assertEquals(result.x, 0.);
        assertEquals(result.y, 0.);
    }

    @Test
    public void movingFowardSlightly() {
        var subject = new Transmission(1., 0., 1.);

        var result = subject.robotControl(0., 0.3, 0., 0.);

        assertEquals(result.x, 0.);
        assertEquals(result.y, 0.3);
    }

    @Test
    public void controllerDiagonal() {
        var subject = new Transmission(1., 0., 1.);

        var result = subject.robotControl(0.3, 0.3, 0., 0.);

        assertEquals(result.x, 0.3);
        assertEquals(result.y, 0.3);
    }
    
    @Test
    public void moreThanOne() {
        var subject = new Transmission(1., 0., 1.);

        var result = subject.robotControl(1.1, 1.1, 0., 0.);

        assertEquals(result.x, 1.);
        assertEquals(result.y, 1.);
    }
    
    @Test
    public void shiftToHigh() {
        var subject = new Transmission(0.8, 0.7, 3.);

        var result = subject.robotControl(0., 1., 0.85, 0.85);

        assertEquals(result.shiftToHigh, true);
    }

    @Test
    public void notShiftToHigh() {
        var subject = new Transmission(0.8, 0.7, 3.);

        var result = subject.robotControl(0., 1., 0.3, 0.3);

        assertEquals(result.shiftToHigh, false);
    }
    
    @Test
    public void shiftWhileTurning() {
        var subject = new Transmission(0.8, 0.7, 3.);

        var result = subject.robotControl(0., 1., 0.75, 0.9);

        assertEquals(result.shiftToHigh, true);
    }

    @Test
    public void shiftWhileTurningALot() {
        var subject = new Transmission(0.8, 0.7, 3.);

        var result = subject.robotControl(0., 1., -0.85, 0.85);

        assertEquals(result.shiftToHigh, false);
    }

    @Test
    public void doesNotShiftAfterAlreadyShifted() {
        var subject = new Transmission(0.8, 0.7, 3.);

        subject.robotControl(0., 1., 0.85, 0.85);

        var result = subject.robotControl(0., 1., 0.85, 0.85);
        assertEquals(result.shiftToHigh, false);
    }
    
    @Test
    public void shiftToLowGear() {
        var subject = new Transmission(0.8, 0.7, 3.);

        subject.robotControl(0., 1., 0.85, 0.85);

        var result = subject.robotControl(0., 1., 0.5, 0.5);
        assertEquals(result.shiftToLow, true);
    }

    @Test
    public void staysInHigh() {
        var subject = new Transmission(0.8, 0.7, 3.);

        subject.robotControl(0., 1., 0.85, 0.85);

        var result = subject.robotControl(0., 1., 0.85, 0.85);
        assertEquals(result.shiftToLow, false);
    }

    @Test
    public void inHighGear_throttlesInput() {
        var subject = new Transmission(0.8, 0.5, 3.);

        subject.robotControl(0., 1., 0.85, 0.85);

        var result = subject.robotControl(0., 1., 0.85, 0.85);
        assertEquals(result.shiftToLow, false);
    }
}
