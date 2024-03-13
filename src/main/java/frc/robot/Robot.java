// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.button.CommandJoystick;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.controller.Controller;
import frc.robot.controller.TankController;
import frc.robot.motor.LeaderFollower;
import frc.robot.motor.Motor;
import frc.robot.motor.Reverse;
import frc.robot.kinematics.Speeds;

public class Robot extends TimedRobot {

  @SuppressWarnings("unused")
  // TODO: Probably should remove.
  private final Compressor compresser = new Compressor(Hardware.COMPRESSOR_PORT, PneumaticsModuleType.CTREPCM);

  private final Pneumatics amp = new Pneumatics(Hardware.AMP_UP_PORT, Hardware.AMP_DOWN_PORT, Hardware.PMC_PORT);
  private final Pneumatics lift = new Pneumatics(Hardware.LIFT_UP_PORT, Hardware.LIFT_DOWN_PORT, Hardware.PMC_PORT);

  private final Shooter shooter = new Shooter(Hardware.FLYWHEEL_PORT, Hardware.POSITIONER_PORT,
      Hardware.TIME_TO_MAX_FLYWHEEL);

  private final CommandXboxController xbox = new CommandXboxController(Hardware.XBOX_CONTROLLER_PORT);
  private final CommandJoystick leftJoystick = new CommandJoystick(Hardware.FIRST_JOYSTICK_PORT);
  private final CommandJoystick rightJoystick = new CommandJoystick(Hardware.SECOND_JOYSTICK_PORT);

  private final Controller controller = new TankController(
      () -> -leftJoystick.getY() + -xbox.getLeftY(),
      () -> -rightJoystick.getY() + -xbox.getRightY(),

      DriverProfile.current.deadzone,
      DriverProfile.current.precisionExponent);

  final Pneumatics gearShift = new Pneumatics(Hardware.GEAR_SHIFT_HIGH_PORT, Hardware.GEAR_SHIFT_LOW_PORT,
      Hardware.PMC_PORT);

  private final DriveTrain driveTrain = new DriveTrain(DriverProfile.current, Hardware.GEAR_RATIO);

  // We need to invert one side of the drivetrain so that positive voltages
  // result in both sides moving forward. Depending on how your robot's
  // gearbox is constructed, you might have to invert the left side instead.
  Motor leftDrive = new Reverse(new LeaderFollower(Hardware.LEFT_LEADER_PORT, Hardware.LEFT_FOLLOWER_PORT));
  Motor rightDrive = new LeaderFollower(Hardware.RIGHT_LEADER_PORT, Hardware.RIGHT_FOLLOWER_PORT);

  private Movement movement = new Movement(leftDrive, rightDrive, gearShift, driveTrain);
  private Autonomous auto = new Autonomous(movement, getPeriod(), shooter, amp);

  @Override
  public void robotInit() {
    movement.shiftTo(Gear.LOW);
  }

  @Override
  public void robotPeriodic() {
    CommandScheduler.getInstance().run();
  }

  @Override
  public void autonomousInit() {
    auto.init();
  }

  @Override
  public void autonomousPeriodic() {
    auto.periodic(getPeriod());
  }

  @Override
  public void teleopInit() {
    auto.cancel();

    anyTrigger(xbox.leftTrigger(0.1), leftJoystick.trigger())
        .whileTrue(shooter.intake());
    anyTrigger(xbox.rightTrigger(0.1))
        .whileTrue(shooter.launch());
    anyTrigger(xbox.rightBumper())
        .whileTrue(shooter.spinUpFlywheel());
    anyTrigger(xbox.a(), rightJoystick.trigger())
        .whileTrue(shooter.launchSequence());

    anyTrigger(xbox.y(), leftJoystick.button(5))
        .whileTrue(Commands.startEnd(() -> amp.setPneumatic(), () -> amp.stopPneumatic()));
    anyTrigger(xbox.x(), leftJoystick.button(6))
        .whileTrue(Commands.startEnd(() -> lift.setPneumatic(), () -> lift.stopPneumatic()));
  }

  private Trigger anyTrigger(Trigger... triggers) {
    return new Trigger(() -> {
      for (Trigger trigger : triggers) {
        if (trigger.getAsBoolean())
          return true;
      }
      return false;
    });
  }

  @Override
  public void teleopPeriodic() {
    movementPeriodic();
  }

  private void movementPeriodic() {
    var desiredSpeeds = controller.desiredSpeeds();
    movement.move(desiredSpeeds, getPeriod());
  }
}
