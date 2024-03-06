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

public class Drive extends SubsystemBase {

    private TalonFX m_leftLeader;
    private TalonFX m_leftFollower;
    private TalonFX m_rightLeader;
    private TalonFX m_rightFollower;

    private DifferentialDrive m_driveTrain;
    private DifferentialDriveKinematics m_test;

    private Supplier<Double> m_xControl;
    private Supplier<Double> m_rotationControl;

    private double m_curX;
    private double m_curRot;

    public Drive(int leftLeaderPort, int leftFollowerPort, int rightLeaderPort, int rightFollowerPort) {
        m_leftLeader = new TalonFX(leftLeaderPort);
        m_leftFollower = new TalonFX(leftFollowerPort);
        m_leftFollower.setControl(new Follower(leftLeaderPort, false));

        m_rightLeader = new TalonFX(rightLeaderPort);
        m_rightFollower = new TalonFX(rightFollowerPort);
        m_rightFollower.setControl(new Follower(rightLeaderPort, false));

        m_driveTrain = new DifferentialDrive(m_leftLeader::set, m_rightLeader::set);
    }

    @Override
    public void periodic() {
        // System.out.print("leftLeader get: ");
        // System.out.println(m_leftLeader.get());

        double desiredX = 0.0;
        double desiredRot = 0.0;
        if (m_xControl != null) {
            desiredX = m_xControl.get();
        }
        if (m_rotationControl != null) {
            desiredRot = m_rotationControl.get();
        }

        double errorX = Math.abs(desiredX - m_curX);
        double errorRot = Math.abs(desiredRot - m_curRot);
        
        if (errorX > 0.02 && m_curX < desiredX) {
            m_curX += 0.02;
        } else if (errorX > 0.02 && m_curX > desiredX) {
            m_curX -= 0.02;
        }

        if (errorRot > 0.02 && m_curRot < desiredRot) {
            m_curRot += 0.02;
        } else if (errorRot > 0.02 && m_curRot > desiredRot) {
            m_curRot -= 0.02;
        }

        m_curX = MathUtil.clamp(m_curX, -1.0, 1.0);
        m_curRot = MathUtil.clamp(m_curRot, -0.5, 0.5);

        // arcade();
    }

    public void autonomousDriveFunc(boolean on_off) {
        if (on_off) {
            m_driveTrain.arcadeDrive(1, 0);
        } else {
            m_driveTrain.arcadeDrive(0, 0);
            
        }
    }

    public void arcadeAutoGearshift() {
        m_driveTrain.arcadeDrive(m_curRot, m_curX);
    }

    public void linkController(Supplier<Double> xControl, Supplier<Double> rotationControl) {
        m_xControl = xControl;
        m_rotationControl = rotationControl;
    }

    public Command autoDrive() {
        return Commands.runOnce(() -> autonomousDriveFunc(true)).andThen(Commands.waitSeconds(2)).andThen(Commands.runOnce(() -> autonomousDriveFunc(false)));
    }
    
}
