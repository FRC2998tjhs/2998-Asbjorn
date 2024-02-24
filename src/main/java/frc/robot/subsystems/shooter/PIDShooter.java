package frc.robot.subsystems.shooter;

import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.CANSparkLowLevel.MotorType;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.lib.utils.TunableNumber;

public class PIDShooter extends SubsystemBase {
    public static class ShooterConstants {
        // Proportional Integral Derivative to get it up to speed and not over shoot the speed

        // sets the default PID numbers and makes the variables
        public static final TunableNumber kP = new TunableNumber("Shooter P", 0.1);
        public static final TunableNumber kI = new TunableNumber("Shooter I", 0.0);
        public static final TunableNumber kD = new TunableNumber("Shooter D", 0.000);

        // velocity for the speaker
        public static final TunableNumber kSpeakerVelocity = new TunableNumber("Shooter Speaker Velocity", 8.0);
    }

    // the flywheel is what shoots the note
    private CANSparkMax m_flywheel;
    // the encoder counts how many times it rotates
    private RelativeEncoder m_encoder;
    // the positioner pushes it in to the
    private CANSparkMax m_positioner;

    private PIDController m_controller;

    private final double kWheelRadius = Units.inchesToMeters(2.5);

    public PIDShooter(int flywheelPort, int positionerPort) {
        
        // sets the PID variables to the PID controller
        m_controller = new PIDController(ShooterConstants.kP.get(), ShooterConstants.kI.get(), ShooterConstants.kD.get());
        m_controller.setTolerance(1);

        // assings the Flywheel, encoder, and posistioner variables to there respective thingy's
        m_flywheel = new CANSparkMax(flywheelPort, MotorType.kBrushless);
        m_encoder = m_flywheel.getEncoder();
        m_encoder.setVelocityConversionFactor(2 * Math.PI * kWheelRadius / (500 * 0.4));
        m_positioner = new CANSparkMax(positionerPort, MotorType.kBrushless);
    }

    // detects the change in the PID numbers and sets them to the ussed values
    @Override
    public void periodic() {
        if (ShooterConstants.kP.hasChanged() || ShooterConstants.kI.hasChanged() || ShooterConstants.kD.hasChanged()) {
            m_controller = new PIDController(ShooterConstants.kP.get(), ShooterConstants.kI.get(), ShooterConstants.kD.get());
            m_controller.setTolerance(0.2);
        }

        double pidVoltage = m_controller.calculate(m_encoder.getVelocity());
        m_flywheel.setVoltage(pidVoltage);

        System.out.println(String.format(
            "PID Voltage: %f\nEncoder Velocity: %f\nDesired Velocity: %f",
            pidVoltage, m_encoder.getVelocity(), m_controller.getSetpoint()
        ));
    }

    public void setDesiredVelocity(double velocity) {
        m_controller.setSetpoint(velocity);
    }

    public void setPositionerVoltage(double voltage) {
        m_positioner.setVoltage(voltage);
    }

    public Command intakePositioner() {
        return Commands.runEnd(() -> setPositionerVoltage(-4),
                () -> setPositionerVoltage(0));
    }

    public Command exitPositioner() {
        return Commands.runEnd(() -> setPositionerVoltage(8),
                () -> setPositionerVoltage(0));
    }

    public Command speakerSpeed() {
        return Commands.runOnce(() -> setDesiredVelocity(ShooterConstants.kSpeakerVelocity.get()));
    }

    public Command stopFlywheel() {
        return Commands.runOnce(() -> setDesiredVelocity(0));
    }
}
