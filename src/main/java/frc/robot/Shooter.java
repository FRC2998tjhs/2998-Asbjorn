package frc.robot;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkLowLevel.MotorType;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Shooter extends SubsystemBase {
    private CANSparkMax flywheel;
    private CANSparkMax positioner;

    private Timer timer = new Timer();

    private double timeToMaxFlywheel;
    private Double flywheelLaunchingSince = null;

    public Shooter(int flywheelPort, int positionerPort, double timeToMaxFlywheel) {
        flywheel = new CANSparkMax(flywheelPort, MotorType.kBrushless);
        positioner = new CANSparkMax(positionerPort, MotorType.kBrushless);

        this.timeToMaxFlywheel = timeToMaxFlywheel;

        disableAll();
        timer.start();
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
                () -> disableAll());
    }

    private void disableAll() {
        setPositionerVoltage(0);
        disableFlywheel();
    }

    public Command launch() {
        return Commands.startEnd(() -> setPositionerVoltage(8),
                () -> setPositionerVoltage(0));
    }

    private void disableFlywheel() {
        flywheelLaunchingSince = null;
        setFlywheelVoltage(0);
    }

    private double flywheelLaunchingFor() {
        if (flywheelLaunchingSince == null) {
            return 0.;
        }
        return timer.get() - flywheelLaunchingSince;
    }

    private void setFlywheelToLaunch() {
        if (flywheelLaunchingSince == null) {
            flywheelLaunchingSince = timer.get();
        }

        setFlywheelVoltage(12);
    }

    public Command spinUpFlywheel() {
        return Commands.runEnd(() -> setFlywheelToLaunch(), () -> disableFlywheel());
    }

    public Command launchSequence() {
        return Commands.sequence(
                Commands.runOnce(() -> setFlywheelToLaunch()),
                Commands.waitUntil(() -> flywheelLaunchingFor() >= timeToMaxFlywheel),
                Commands.runOnce(() -> setPositionerVoltage(8)),
                Commands.waitSeconds(0.2))
                .finallyDo(() -> disableAll());
    }
}
