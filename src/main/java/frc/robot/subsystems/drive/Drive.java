package frc.robot.subsystems.drive;

import java.util.function.Supplier;

import com.ctre.phoenix6.controls.Follower;
import com.ctre.phoenix6.hardware.TalonFX;
import com.revrobotics.CANSparkMax;

import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Drive extends SubsystemBase {

    private TalonFX m_leftLeader;
    private TalonFX m_leftFollower;
    private TalonFX m_rightLeader;
    private TalonFX m_rightFollower;

    private DifferentialDrive m_driveTrain;

    private Supplier<Double> m_xControl;
    private Supplier<Double> m_rotationControl;

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
    }

    public void arcade() {
        m_driveTrain.arcadeDrive(m_xControl.get(), m_rotationControl.get());
    }

    public void linkController(Supplier<Double> xControl, Supplier<Double> rotationControl) {
        m_xControl = xControl;
        m_rotationControl = rotationControl;
    }

    
}
