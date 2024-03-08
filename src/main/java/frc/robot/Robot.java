// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.button.CommandJoystick;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import frc.robot.subsystems.drive.Drive;
import frc.robot.subsystems.shooter.Shooter;
import frc.robot.subsystems.pneumatics.Pneumatics;
import frc.lib.utils.TunableNumber;
import frc.robot.subsystems.compresser.Compresser;

/**
 * This is a demo program showing the use of the DifferentialDrive class. Runs
 * the motors with
 * arcade steering.
 */
public class Robot extends TimedRobot {

  private final Compresser m_compresser = new Compresser(7);

  private final Pneumatics m_gearShift = new Pneumatics(3, 2, 7);

  private final Pneumatics m_ampPneumatics = new Pneumatics(1, 0, 7);

  // grabs drive2's drive function
  private final Drive m_drive = new Drive(1, 2, 3, 4, m_gearShift);
  // grabs Shooters Shooter function
  private final Shooter m_shooter = new Shooter(5, 6);
  // asings the controller
  private final CommandXboxController m_controller = new CommandXboxController(0);

  private final CommandJoystick leftController = new CommandJoystick(0);
  private final CommandJoystick rightController = new CommandJoystick(1);

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
    autonomousCommandDrive = Commands.sequence(
        setDirection(0.0, -0.6),
        Commands.waitSeconds(4),
        setDirection(0.0, 0.0));

    autonomousCommandShootAndDrive = Commands.sequence(
        m_shooter.toggleFlywheel(),
        Commands.waitSeconds(2),
        Commands.runOnce(() -> m_shooter.setPositionerVoltage(8.0)),
        Commands.waitSeconds(1),
        m_shooter.toggleFlywheel(),
        Commands.runOnce(() -> m_shooter.setPositionerVoltage(0.0)),
        setDirection(0.0, 0.7),
        Commands.waitSeconds(1),
        setDirection(-0.4, 0.0),
        Commands.waitSeconds(2),
        setDirection(0.0, 0.7),
        Commands.waitSeconds(3),
        setDirection(0.0, 0.0));

    autonomousCommandShoot = Commands.sequence(
        Commands.waitSeconds(1),
        m_shooter.toggleFlywheel(),
        Commands.waitSeconds(2),
        Commands.runOnce(() -> m_shooter.setPositionerVoltage(8.0)),
        Commands.waitSeconds(5),
        m_shooter.toggleFlywheel(),
        Commands.runOnce(() -> m_shooter.setPositionerVoltage(0.0)));

    autonomousCommandShootAndDriveReverse = Commands.sequence(m_shooter.toggleFlywheel(),
        Commands.waitSeconds(2),
        Commands.runOnce(() -> m_shooter.setPositionerVoltage(8.0)),
        Commands.waitSeconds(1),
        m_shooter.toggleFlywheel(),
        Commands.runOnce(() -> m_shooter.setPositionerVoltage(0.0)),
        setDirection(0.0, 0.6),
        Commands.waitSeconds(1),
        setDirection(0.4, 0.0),
        Commands.waitSeconds(2),
        setDirection(0.0, 0.7),
        Commands.waitSeconds(3),
        setDirection(0.0, 0.0));

    autonomousCommandAmp = Commands.sequence(m_gearShift.setPneumaticCommand(true),
        Commands.waitSeconds(2),
        m_gearShift.setPneumaticCommand(false),
        setDirection(0.5, -0.1),
        Commands.waitSeconds(2),
        setDirection(0.0, -0.5),
        Commands.waitSeconds(4),
        setDirection(0.0, 0.0));

    autonomousCommand = autonomousCommandShoot;

    if (autonomousCommand != null) {
      autonomousCommand.schedule();
    }
  }

  private Command setDirection(double x, double y) {
    return Commands.runOnce(() -> m_drive.linkController(() -> x, () -> y));
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

    m_drive.linkController(() -> leftController.getY(), () -> -rightController.getY() * 1.1);
    // TODO(shelbyd): Remove this?
    // m_controller.leftBumper().onTrue(m_gearShift.setPneumaticCommand(true))
    //     .onFalse(m_gearShift.setPneumaticCommand(false));

    // m_controller.leftTrigger(0.1).whileTrue(m_shooter.intakePositioner());
    // m_controller.rightTrigger(0.1).whileTrue(m_shooter.exitPositioner());
    // m_controller.rightBumper().onTrue(m_shooter.toggleFlywheel());

    // m_controller.y().onTrue(m_ampPneumatics.scoreAmp());
  }

  // it starts arcade drive. LEARN TO READ.
  @Override
  public void teleopPeriodic() {
    // Drive with arcade drive.
    // That means that the Y axis drives forward
    // and backward, and the X turns left and right.
    // System.out.println("Test");
    m_drive.arcadeAutoGearshift();
    // System.out.println(m_pneumatics.getSolinoidChannels());
    // System.out.println(m_compresser.GetEnabled());
    // System.out.println(m_controller.getLeftY() + " " + m_controller.getLeftX());

  }

  @Override
  public void autonomousPeriodic() {
    m_drive.arcadeAutoGearshift();
  }
}
