// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import javax.swing.text.html.HTMLEditorKit.LinkController;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkLowLevel.MotorType;

import edu.wpi.first.util.sendable.SendableRegistry;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.motorcontrol.PWMSparkMax;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import frc.robot.subsystems.drive.Drive;
import frc.robot.subsystems.drive.DriveSparkMax;
import frc.robot.subsystems.shooter.Shooter;
import frc.robot.subsystems.pneumatics.Pneumatics;
import frc.lib.utils.TunableNumber;
import frc.robot.subsystems.compresser.Compresser;

/**
 * This is a demo program showing the use of the DifferentialDrive class. Runs the motors with
 * arcade steering.
 */
public class Robot extends TimedRobot {

  private final Compresser m_compresser = new Compresser(7);

  
  private final Pneumatics m_ampPneumatics = new Pneumatics(2, 3, 7);

  private final Pneumatics m_gearShiftPneumatics = new Pneumatics(0, 1, 7);

  // grabs drive2's drive function
  private final Drive m_drive = new Drive(1, 2, 3, 4);
  // grabs Shooters Shooter function
  private final Shooter m_shooter = new Shooter(5, 6);
  // asings the controller
  private final CommandXboxController m_controller = new CommandXboxController(0);
  // sets up for command schedualer?
  private Command autonomousCommandDrive;
  private Command autonomousCommandShootAndDrive;
  private Command autonomousCommandShootAndDriveReverse;
  private Command autonomousCommandShoot;
  private Command autonomousCommandAmp;
  private Command autonomousCommand;

  public Robot() {
    // SendableRegistry.addChild(m_robotDrive, m_leftMotor);
    // SendableRegistry.addChild(m_robotDrive, m_rightMotor);
  }


  public static final TunableNumber kAutonomousMode = new TunableNumber("AutoMode", 1, "AutoMode");

  @Override
  public void autonomousInit() {
    autonomousCommandDrive = Commands.runOnce(() -> {
        m_drive.linkController(() -> -0.6, () -> 0.0);
      }
    ).andThen(Commands.waitSeconds(4)).andThen(Commands.runOnce(() -> {
      m_drive.linkController(() -> 0.0, () -> 0.0);
    }));

    autonomousCommandShootAndDrive = m_shooter.toggleFlywheel().andThen(Commands.waitSeconds(2)).
    andThen(Commands.runOnce(() -> m_shooter.setPositionerVoltage(8.0))).andThen(Commands.waitSeconds(1)).andThen(m_shooter.toggleFlywheel())
    .andThen(Commands.runOnce(() -> m_shooter.setPositionerVoltage(0.0)))
    .andThen(Commands.runOnce(() -> m_drive.linkController(() -> 0.7, () -> 0.0))).andThen(Commands.waitSeconds(1))
    .andThen(Commands.runOnce(() -> m_drive.linkController(() -> 0.0, () -> -0.4))).andThen(Commands.waitSeconds(2))
    .andThen(Commands.runOnce(() -> m_drive.linkController(() -> 0.7, () -> 0.0))).andThen(Commands.waitSeconds(3)).andThen(Commands.runOnce(() -> m_drive.linkController(() -> 0.0, () -> 0.0)));
    
    autonomousCommandShoot = Commands.waitSeconds(1).andThen(m_shooter.toggleFlywheel()).andThen(Commands.waitSeconds(2)).
    andThen(Commands.runOnce(() -> m_shooter.setPositionerVoltage(8.0))).andThen(Commands.waitSeconds(5)).andThen(m_shooter.toggleFlywheel())
    .andThen(Commands.runOnce(() -> m_shooter.setPositionerVoltage(0.0)));

    autonomousCommandShootAndDriveReverse = m_shooter.toggleFlywheel().andThen(Commands.waitSeconds(2)).
    andThen(Commands.runOnce(() -> m_shooter.setPositionerVoltage(8.0))).andThen(Commands.waitSeconds(1)).andThen(m_shooter.toggleFlywheel())
    .andThen(Commands.runOnce(() -> m_shooter.setPositionerVoltage(0.0)))
    .andThen(Commands.runOnce(() -> m_drive.linkController(() -> 0.6, () -> 0.0))).andThen(Commands.waitSeconds(1))
    .andThen(Commands.runOnce(() -> m_drive.linkController(() -> 0.0, () -> 0.4))).andThen(Commands.waitSeconds(2))
    .andThen(Commands.runOnce(() -> m_drive.linkController(() -> 0.7, () -> 0.0))).andThen(Commands.waitSeconds(3)).andThen(Commands.runOnce(() -> m_drive.linkController(() -> 0.0, () -> 0.0)));

    autonomousCommandAmp = m_ampPneumatics.setPneumaticCommand(true).andThen(Commands.waitSeconds(2)).andThen(m_ampPneumatics.setPneumaticCommand(false)).andThen(Commands.runOnce(() -> m_drive.linkController(() -> -0.1, () -> 0.5))
    .andThen(Commands.waitSeconds(2)).andThen(Commands.runOnce(() -> m_drive.linkController(() -> -0.5, () -> 0.0)).andThen(Commands.waitSeconds(4))
    .andThen(Commands.runOnce(() -> m_drive.linkController(() -> 0.0, () -> 0.0)))));

    autonomousCommand = autonomousCommandShoot;

    if (autonomousCommand != null) {
      autonomousCommand.schedule();
    }
  }

  @Override
  public void robotInit() {

    // We need to invert one side of the drivetrain so that positive voltages
    // result in both sides moving forward. Depending on how your robot's
    // gearbox is constructed, you might have to invert the left side instead.

    // uses the linkController function in Drive2 to set the control variables

  }

  // runs command schedualer
  @Override
  public void robotPeriodic() {
    CommandScheduler.getInstance().run();
  }

  // ?
  @Override
  public void teleopInit() {
    if (autonomousCommand != null) {
      autonomousCommand.cancel();
    }

    m_drive.linkController(() -> m_controller.getLeftY(), () -> -m_controller.getLeftX());
    m_controller.leftTrigger(0.1).whileTrue(m_shooter.intakePositioner());
    m_controller.rightTrigger(0.1).whileTrue(m_shooter.exitPositioner());
    m_controller.rightBumper().onTrue(m_shooter.toggleFlywheel());
    m_controller.leftBumper().onTrue(m_ampPneumatics.setPneumaticCommand(true)).onFalse(m_ampPneumatics.setPneumaticCommand(false));
    m_controller.y().onTrue(m_gearShiftPneumatics.scoreAmp());
  }

  // it starts arcade drive. LEARN TO READ.
  @Override
  public void teleopPeriodic() {
    // Drive with arcade drive.
    // That means that the Y axis drives forward
    // and backward, and the X turns left and right.
    // System.out.println("Test");
    m_drive.arcade();
    // System.out.println(m_pneumatics.getSolinoidChannels());
    // System.out.println(m_compresser.GetEnabled());
    // System.out.println(m_controller.getLeftY() + " " + m_controller.getLeftX());
  }

  @Override
  public void autonomousPeriodic() {
    m_drive.arcade();
  }
}
