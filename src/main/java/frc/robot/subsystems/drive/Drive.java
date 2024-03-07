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

    private TalonFX m_leftLeader;
    private TalonFX m_leftFollower;
    private TalonFX m_rightLeader;
    private TalonFX m_rightFollower;

    private DifferentialDrive m_driveTrain;
    private DifferentialDriveKinematics m_test;

    private Supplier<Double> controllerX;
    private Supplier<Double> controllerY;

    private double currentY;
    private double currentX;

    private Transmission transmission;
    private Pneumatics gearShift;

    public Drive(int leftLeaderPort, int leftFollowerPort, int rightLeaderPort, int rightFollowerPort, Pneumatics gearShift) {
        this.gearShift = gearShift;

        m_leftLeader = new TalonFX(leftLeaderPort);
        m_leftFollower = new TalonFX(leftFollowerPort);
        m_leftFollower.setControl(new Follower(leftLeaderPort, false));

        m_rightLeader = new TalonFX(rightLeaderPort);
        m_rightFollower = new TalonFX(rightFollowerPort);
        m_rightFollower.setControl(new Follower(rightLeaderPort, false));

        m_driveTrain = new DifferentialDrive(m_leftLeader::set, m_rightLeader::set);

        transmission = new Transmission();
    }

    @Override
    public void periodic() {
        // System.out.print("leftLeader get: ");
        // System.out.println(m_leftLeader.get());

        double desiredY = 0.0;
        if (controllerY != null) {
            desiredY = controllerY.get();
        }
        currentY = tickControlTowards(currentY, desiredY, 0.02);

        // double errorY = Math.abs(desiredY - currentY);
        // if (errorY > 0.02 && currentY < desiredY) {
        //     currentY += 0.02;
        // } else if (errorY > 0.02 && currentY > desiredY) {
        //     currentY -= 0.02;
        // }
        // currentY = MathUtil.clamp(currentY, -1.0, 1.0);

        double desiredX = 0.0;
        if (controllerX != null) {
            desiredX = controllerX.get();
        }
        double errorX = Math.abs(desiredX - currentX);
        if (errorX > 0.02 && currentX < desiredX) {
            currentX += 0.02;
        } else if (errorX > 0.02 && currentX > desiredX) {
            currentX -= 0.02;
        }
        currentX = MathUtil.clamp(currentX, -0.5, 0.5);

        // arcade();
    }

    // TODO(shelbyd): Is this the desired bahavior?
    private double tickControlTowards(double current, double desired, double tickSize) {
        var direction = Math.signum(desired - current);
        return MathUtil.clamp(current + direction * tickSize, -desired, desired);
    }

    // TODO(shelbyd): Can we do this in our own periodic?
    public void arcadeAutoGearshift() {
        var desiredMovement = transmission.transmissioning(currentX, currentY, m_leftLeader.get(), m_rightLeader.get());
        m_driveTrain.arcadeDrive(desiredMovement.x, desiredMovement.y);

        if (desiredMovement.shiftToHigh) {
            shiftToHigh().schedule();
        } else if (desiredMovement.shiftToLow) {
            shiftToLow().schedule();
        }
    }

    private Command shiftToHigh() {
        return gearShift.setPneumaticCommand(true);
    }

    private Command shiftToLow() {
        return gearShift.setPneumaticCommand(false);
    }

    public void linkController(Supplier<Double> controllerX, Supplier<Double> controllerY) {
        this.controllerX = controllerX;
        this.controllerY = controllerY;
    }

    public Command autoDrive() {
        return Commands.runOnce(() -> m_driveTrain.arcadeDrive(1, 0)).andThen(Commands.waitSeconds(2))
                .andThen(Commands.runOnce(() -> m_driveTrain.arcadeDrive(0, 0)));
    }

}
