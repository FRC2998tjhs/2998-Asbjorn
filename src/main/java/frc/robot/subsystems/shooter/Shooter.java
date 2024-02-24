package frc.robot.subsystems.shooter;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkLowLevel.MotorType;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Shooter extends SubsystemBase {
    private CANSparkMax m_flywheel;
    private CANSparkMax m_positioner;
    private boolean m_flywheelEnabled;

    public Shooter(int flywheelPort, int positionerPort) {
        m_flywheel = new CANSparkMax(flywheelPort, MotorType.kBrushless);
        m_positioner = new CANSparkMax(positionerPort, MotorType.kBrushless);
        m_flywheelEnabled = false;
    }

    public void setFlywheelVoltage(double voltage) {
        m_flywheel.setVoltage(voltage);
    }

    public void setPositionerVoltage(double voltage) {
        m_positioner.setVoltage(voltage);
    }

    public Command intakePositioner() {
        return Commands.runEnd(() -> {
                    setPositionerVoltage(-4);
                    setFlywheelVoltage(-4);
                },
                () -> {
                    setPositionerVoltage(0);
                    setFlywheelVoltage(0);
                });
    }

    public Command exitPositioner() {
        return Commands.runEnd(() -> setPositionerVoltage(8),
                () -> setPositionerVoltage(0));
    }

    public Command toggleFlywheel() {
        return Commands.runOnce(() -> {
            m_flywheelEnabled = !m_flywheelEnabled;
            if (m_flywheelEnabled) {
                setFlywheelVoltage(12);
            } else {
                setFlywheelVoltage(0);
            }
        });
    }

}
