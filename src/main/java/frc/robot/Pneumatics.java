package frc.robot;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Pneumatics extends SubsystemBase {
    DoubleSolenoid solenoid;

    public Pneumatics(int forwardPort, int reversePort, int PMCPort) {
        solenoid = new DoubleSolenoid(PMCPort, PneumaticsModuleType.CTREPCM, forwardPort, reversePort);
        solenoid.set(DoubleSolenoid.Value.kReverse);
    }

    public void setPneumatic() {
        solenoid.set(DoubleSolenoid.Value.kForward);
    }

    public void stopPneumatic() {
        solenoid.set(DoubleSolenoid.Value.kReverse);
    }

    public void setIf(boolean b) {
        if (b) {
            setPneumatic();
        } else {
            stopPneumatic();
        }
    }

    public Command activateFor(double s) {
        return Commands.sequence(
            Commands.runOnce(() -> setPneumatic()),
            Commands.waitSeconds(s),
            Commands.runOnce(() -> stopPneumatic())
        );
    }
}
