package frc.robot.subsystems.drive;

import java.util.function.Supplier;

import com.ctre.phoenix6.controls.Follower;
import com.ctre.phoenix6.hardware.TalonFX;
// import com.revrobotics.CANSparkMax;

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

    public void autonomousDriveFunc(boolean on_off) {
        if (on_off) {
            m_driveTrain.arcadeDrive(1, 0);
        } else {
            m_driveTrain.arcadeDrive(0, 0);
        }
    }

    public void arcade() {
        m_driveTrain.arcadeDrive(m_xControl.get(), m_rotationControl.get());
    }

    public void linkController(Supplier<Double> xControl, Supplier<Double> rotationControl) {
        m_xControl = xControl;
        m_rotationControl = rotationControl;
    }

    public Command autoDrive() {
        return Commands.runOnce(() -> autonomousDriveFunc(true)).andThen(Commands.waitSeconds(2)).andThen(Commands.runOnce(() -> autonomousDriveFunc(false)));
    }
    
}
