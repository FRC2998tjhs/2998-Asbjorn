package frc.robot;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkLowLevel.MotorType;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Shooter extends SubsystemBase {
    private CANSparkMax flywheel;
    private CANSparkMax positioner;
    private boolean flywheelEnabled;

    public Shooter(int flywheelPort, int positionerPort) {
        flywheel = new CANSparkMax(flywheelPort, MotorType.kBrushless);
        positioner = new CANSparkMax(positionerPort, MotorType.kBrushless);
        flywheelEnabled = false;
    }

    public void setFlywheelVoltage(double voltage) {
        flywheel.setVoltage(voltage);
    }

    public void setPositionerVoltage(double voltage) {
        positioner.setVoltage(voltage);
    }

    public Command intake() {
        return Commands.startEnd(() -> {
            setPositionerVoltage(-4);
            setFlywheelVoltage(-4);
        },
                () -> {
                    setPositionerVoltage(0);
                    setFlywheelVoltage(0);
                });
    }

    public Command launch() {
        return Commands.startEnd(() -> setPositionerVoltage(8),
                () -> setPositionerVoltage(0));
    }

    // TODO: Instead of toggle, should this be a launch sequence?
    public Command toggleFlywheel() {
        return Commands.runOnce(() -> {
            flywheelEnabled = !flywheelEnabled;
            if (flywheelEnabled) {
                setFlywheelVoltage(12);
            } else {
                setFlywheelVoltage(0);
            }
        });
    }

}
