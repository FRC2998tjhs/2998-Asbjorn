package frc.robot.subsystems.drive;

import java.util.function.Supplier;

import javax.swing.text.StyledEditorKit;

import com.ctre.phoenix6.controls.Follower;
import com.ctre.phoenix6.hardware.TalonFX;
// import com.revrobotics.CANSparkMax;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.kinematics.DifferentialDriveKinematics;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.subsystems.pneumatics.Pneumatics;
import frc.robot.subsystems.transmission.Transmission;

public class Drive extends SubsystemBase {

    public TalonFX m_leftLeader;
    private TalonFX m_leftFollower;
    public TalonFX m_rightLeader;
    private TalonFX m_rightFollower;

    // private DifferentialDrive m_driveTrain;
    // private DifferentialDriveKinematics m_test;

    private Supplier<Double> controllerX;
    private Supplier<Double> controllerY;

    private double currentY;
    private double currentX;

    private Transmission transmission;
    private Pneumatics gearShift;
    private boolean powerToMotorEnabled = true;

    public Drive(int leftLeaderPort, int leftFollowerPort, int rightLeaderPort, int rightFollowerPort, Pneumatics gearShift) {
        this.gearShift = gearShift;

        m_leftLeader = new TalonFX(leftLeaderPort);
        m_leftFollower = new TalonFX(leftFollowerPort);
        m_leftFollower.setControl(new Follower(leftLeaderPort, false));

        m_rightLeader = new TalonFX(rightLeaderPort);
        m_rightFollower = new TalonFX(rightFollowerPort);
        m_rightFollower.setControl(new Follower(rightLeaderPort, false));

        // m_driveTrain = new DifferentialDrive(m_leftLeader::set, m_rightLeader::set);

        transmission = new Transmission(0.95, 0.1, 2.27);
    }

    @Override
    public void periodic() {
        double tickSize = 0.02;

        double desiredY = 0.0;
        if (controllerY != null) {
            desiredY = controllerY.get();
        }
        currentY = tickControlTowards(currentY, desiredY, tickSize);

        double desiredX = 0.0;
        if (controllerX != null) {
            desiredX = controllerX.get();
        }
        currentX = tickControlTowards(currentX, desiredX, tickSize);
    }

    // TODO(shelbyd): Is this the desired bahavior?
    private double tickControlTowards(double current, double desired, double tickSize) {
        desired = targetSpeed(desired);

        var direction = Math.signum(desired - current);
        var result = MathUtil.clamp(current + direction * tickSize, -Math.abs(desired), Math.abs(desired));
        return result;
    }

    public double targetSpeed(double control) {
        if (control < 0) {
            return -targetSpeed(-control);
        }
        if (control > 1) {
            return targetSpeed(1.);
        }

        double deadzone = 0.1;
        if (control < deadzone) {
            return 0.;
        }

        double scaled = (control - deadzone) * (1. / (1. - deadzone));
        return Math.pow(scaled, 1.5);
    }

    // TODO(shelbyd): Can we do this in our own periodic?
    public void arcadeAutoGearshift() {
        if (!powerToMotorEnabled) {
            return;
        }
        var desiredMovement = transmission.robotControl(currentX, currentY);
        // m_driveTrain.arcadeDrive(desiredMovement.x, desiredMovement.y);
        m_leftLeader.set(desiredMovement.y);
        m_rightLeader.set(desiredMovement.x);
    
        if (desiredMovement.shiftToHigh) {
            shiftPneumatics(true).schedule();
        } else if (desiredMovement.shiftToLow) {
            shiftPneumatics(false).schedule();
        }
    }

    private Command shiftPneumatics(boolean on) {
        return Commands.sequence(
            gearShift.setPneumaticCommand(on),
            Commands.waitSeconds(0.01),
            Commands.runOnce(() -> {
                this.powerToMotorEnabled = false;
                m_leftLeader.set(0);
                m_rightLeader.set(0);
            }),
            Commands.waitSeconds(0.03),
            Commands.runOnce(() -> this.powerToMotorEnabled = true)
        );
    }

    public void linkController(Supplier<Double> controllerX, Supplier<Double> controllerY) {
        this.controllerX = controllerX;
        this.controllerY = controllerY;
    }

    // public Command autoDrive() {
    //     return Commands.runOnce(() -> m_driveTrain.arcadeDrive(1, 0)).andThen(Commands.waitSeconds(2))
    //             .andThen(Commands.runOnce(() -> m_driveTrain.arcadeDrive(0, 0)));
    // }
}
