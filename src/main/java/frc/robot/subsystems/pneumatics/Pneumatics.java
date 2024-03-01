package frc.robot.subsystems.pneumatics;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Pneumatics extends SubsystemBase {
    DoubleSolenoid m_climber;
    DoubleSolenoid m_ampScorer;

    // int climberForwardPort, int climberReversePort,
    public Pneumatics( int ampForwardPort, int ampReversePort) {
        // m_climber = new DoubleSolenoid(PneumaticsModuleType.REVPH, climberForwardPort, climberReversePort);
        
        m_ampScorer = new DoubleSolenoid(PneumaticsModuleType.REVPH, ampForwardPort, ampReversePort);
        m_ampScorer.set(DoubleSolenoid.Value.kReverse);
    }

    public void getSolinoidChannels() {
        m_ampScorer.set(DoubleSolenoid.Value.kForward);
    }

    // public void setClimber(boolean forward) {
    //     // true: forward
    //     // false: reverse
    //     if (forward) {
    //         m_climber.set(DoubleSolenoid.Value.kForward);
    //     } else {
    //         m_climber.set(DoubleSolenoid.Value.kReverse);
    //     }
            
    // }

    // public void stopClimber() {
    //     m_climber.set(DoubleSolenoid.Value.kOff);
    // }

    public void setAmpScorer(boolean up) {
        // true: up
        // false: down
        if (up) {
            m_ampScorer.set(DoubleSolenoid.Value.kForward);
        } else {
            m_ampScorer.set(DoubleSolenoid.Value.kReverse);
        }
    }

    public void stopAmpScorer() {
        m_ampScorer.set(DoubleSolenoid.Value.kReverse);
    }

    // public Command climbUp() {
    //     return Commands.runEnd(() -> setClimber(true),
    //             this::stopClimber);
    // }

    // public Command climbDown() {
    //     return Commands.runEnd(() -> setClimber(false),
    //             this::stopClimber);
    // }

    public Command scoreAmp() {
        return Commands.runEnd(() -> setAmpScorer(true), () -> setAmpScorer(false)).andThen(Commands.waitSeconds(1)).andThen(Commands.runOnce(this::stopAmpScorer));
    }

    public Command BoyKisser() {
        return Commands.runOnce(() -> getSolinoidChannels());
    }
}
