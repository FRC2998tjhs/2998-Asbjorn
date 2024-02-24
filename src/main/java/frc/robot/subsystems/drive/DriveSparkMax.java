package frc.robot.subsystems.drive;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import java.util.function.Supplier;

import com.ctre.phoenix6.controls.Follower;
import com.ctre.phoenix6.hardware.TalonFX;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkLowLevel.MotorType;

import edu.wpi.first.wpilibj.drive.DifferentialDrive;

public class DriveSparkMax extends SubsystemBase {

    // initiates the variables that will be come the motor control's using CANSparkMax
    private CANSparkMax m_leftLeader;
    private CANSparkMax m_leftFollower;
    private CANSparkMax m_rightLeader;
    private CANSparkMax m_rightFollower;

    // initiates a DifferentialDrive variable
    private DifferentialDrive m_driveTrain;

    // uses the Supplier to set up a weird function thingy to get assinged in a function used in Robot.java
    // it will get the left stick x and y to be used to drive the robot
    private Supplier<Double> m_xControl;
    private Supplier<Double> m_rotationControl;

    // sets up a function that will be used in Robot.java to drive
    public DriveSparkMax(int leftLeaderPort, int leftFollowerPort, int rightLeaderPort, int rightFollowerPort) {
        
        // sets up the left motors to connect to the SparkMax thingy's that tell the motor to move
        // the leader is the one that gets told to move and the follower is set to follow the leader DUH
        m_leftLeader = new CANSparkMax(leftLeaderPort, MotorType.kBrushless);
        m_leftFollower = new CANSparkMax(leftFollowerPort, MotorType.kBrushless);
        m_leftFollower.follow(m_leftLeader);

        // same as left but on the right motors and inverted somehow
        m_rightLeader = new CANSparkMax(rightLeaderPort, MotorType.kBrushless);
        // m_rightLeader.setInverted(false);
        System.out.println(m_rightLeader.getInverted());
        m_rightFollower = new CANSparkMax(rightFollowerPort, MotorType.kBrushless);
        m_rightFollower.follow(m_rightLeader);

        // connects the leader CANSparkMax to the Drive train
        m_driveTrain = new DifferentialDrive(m_leftLeader::set, m_rightLeader::set);
    }

    @Override
    public void periodic() {
        // System.out.println(String.format("Y: %f, X: %f", m_xControl.get(), m_rotationControl.get()));
    }

    /* sets the drive train to use arcade drive and sets the controls to left stick x (forward/backword)
     and y (rotation)*/
    public void arcade() {
        m_driveTrain.arcadeDrive(m_xControl.get(), m_rotationControl.get());
    }

    // uses a function to set the variables to the x and y of left stick
    // it uses a function so its callable through other files
    public void linkController(Supplier<Double> xControl, Supplier<Double> rotationControl) {
        m_xControl = xControl;
        m_rotationControl = rotationControl;
    }

    
}

