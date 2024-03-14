package frc.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkLowLevel.MotorType;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Shooter extends SubsystemBase {
    private VictorSPX[] flywheels;
    private CANSparkMax positioner;

    private Timer timer = new Timer();

    private double timeToMaxFlywheel;
    private Double flywheelLaunchingSince = null;

    public Shooter(int[] flywheelPorts, int positionerPort, double timeToMaxFlywheel) {
        flywheels = new VictorSPX[flywheelPorts.length];
        for (int i = 0; i < flywheelPorts.length; i++) {
            flywheels[i] = new VictorSPX(flywheelPorts[i]);
        }

        positioner = new CANSparkMax(positionerPort, MotorType.kBrushless);

        this.timeToMaxFlywheel = timeToMaxFlywheel;

        disableAll();
        timer.start();
    }

    public void setFlywheelVoltage(double voltage) {
        for (var f : flywheels) {
            f.set(ControlMode.PercentOutput, voltage / 12);
        }
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
        return Commands.startEnd(() -> setPositionerVoltage(12),
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
