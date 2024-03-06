package frc.robot.subsystems.pneumatics;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Pneumatics extends SubsystemBase {
    DoubleSolenoid m_pneumatic;

    // int climberForwardPort, int climberReversePort,
    public Pneumatics(int pneumaticForwardPort, int pneumaticReversePort, int PMCPort) {
        // m_climber = new DoubleSolenoid(PneumaticsModuleType.REVPH, climberForwardPort, climberReversePort);
        
        m_pneumatic = new DoubleSolenoid(PMCPort, PneumaticsModuleType.CTREPCM, pneumaticForwardPort, pneumaticReversePort);
        m_pneumatic.set(DoubleSolenoid.Value.kReverse);
    }

    public void setPneumatic() {
        m_pneumatic.set(DoubleSolenoid.Value.kForward);
    }

    public void stopPneumatic() {
        m_pneumatic.set(DoubleSolenoid.Value.kReverse);
    }

    public Command holdToggle() {
        return Commands.runEnd(() -> setPneumatic(), () -> stopPneumatic());
    }

    public Command toggleGears() {
        return Commands.run(() -> {
            stopPneumatic();
        });
    }

    public Command setPneumaticCommand(boolean up) {
        if (up) {
            return Commands.runOnce(() -> setPneumatic());
        } else {
            return Commands.runOnce(() -> stopPneumatic());
        }
    }

    public Command scoreAmp() {
        return Commands.waitSeconds(1).andThen(setPneumaticCommand(true)).andThen(Commands.waitSeconds(2).andThen(setPneumaticCommand(false)));
    }
}
