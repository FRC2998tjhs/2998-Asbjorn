// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.button.CommandJoystick;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.TunableNumber;
import frc.robot.controller.Controller;
import frc.robot.controller.TankController;
import frc.robot.kinematics.Speeds;
import frc.robot.motor.LeaderFollower;
import frc.robot.motor.Motor;
import frc.robot.motor.Reverse;

public class Robot extends TimedRobot {
  public static final TunableNumber kAutonomousMode = new TunableNumber("AutoMode", 1, "AutoMode");

  @SuppressWarnings("unused")
  // TODO: Probably should remove.
  private final Compressor compresser = new Compressor(Hardware.COMPRESSOR_PORT, PneumaticsModuleType.CTREPCM);

  private final Pneumatics amp = new Pneumatics(Hardware.AMP_UP_PORT, Hardware.AMP_DOWN_PORT, Hardware.PMC_PORT);

  private final Shooter shooter = new Shooter(Hardware.FLYWHEEL_PORT, Hardware.POSITIONER_PORT, Hardware.TIME_TO_MAX_FLYWHEEL);

  private final CommandXboxController xbox = new CommandXboxController(Hardware.XBOX_CONTROLLER_PORT);
  private final CommandJoystick leftJoystick = new CommandJoystick(Hardware.FIRST_JOYSTICK_PORT);
  private final CommandJoystick rightJoystick = new CommandJoystick(Hardware.SECOND_JOYSTICK_PORT);

  private final Controller controller = new TankController(
      () -> -xbox.getLeftY(),
      () -> -xbox.getRightY(),
      DriverProfile.current.deadzone,
      DriverProfile.current.precisionExponent);

  private final Pneumatics gearShift = new Pneumatics(Hardware.GEAR_SHIFT_HIGH_PORT, Hardware.GEAR_SHIFT_LOW_PORT,
      Hardware.PMC_PORT);

  private final DriveTrain driveTrain = new DriveTrain(DriverProfile.current, Hardware.GEAR_RATIO);

  // We need to invert one side of the drivetrain so that positive voltages
  // result in both sides moving forward. Depending on how your robot's
  // gearbox is constructed, you might have to invert the left side instead.
  private Motor leftDrive = new Reverse(
      new LeaderFollower(Hardware.LEFT_LEADER_PORT, Hardware.LEFT_FOLLOWER_PORT));
  private Motor rightDrive = new LeaderFollower(Hardware.RIGHT_LEADER_PORT, Hardware.RIGHT_FOLLOWER_PORT);

  private Speeds lastSpeeds = new Speeds();

  private Gear currentGear;
  private boolean shifting = false;

  @Override
  public void robotInit() {
    shiftTo(Gear.LOW).schedule();
  }

  private Command shiftTo(Gear gear) {
    if (this.shifting) {
      // TODO : Should throw exception during testing.
      return Commands.none();
    }

    return Commands.sequence(
        Commands.runOnce(() -> gearShift.setIf(gear == Gear.HIGH)),
        Commands.waitSeconds(Hardware.BEGIN_SHIFT_WAIT_S),
        Commands.runOnce(() -> {
          this.shifting = true;
          leftDrive.set(0);
          rightDrive.set(0);
        }),
        Commands.waitSeconds(Hardware.SHIFT_DURATION_S),
        Commands.runOnce(() -> {
          this.shifting = false;
          this.currentGear = gear;
        }));
  }

  @Override
  public void robotPeriodic() {
    CommandScheduler.getInstance().run();
  }

  @Override
  public void autonomousInit() {
  }

  @Override
  public void autonomousPeriodic() {
  }

  @Override
  public void teleopInit() {
    anyTrigger(xbox.leftTrigger(0.1), leftJoystick.trigger())
        .whileTrue(shooter.intake());
    anyTrigger(xbox.rightTrigger(0.1), rightJoystick.trigger())
        .whileTrue(shooter.launch());
    anyTrigger(xbox.rightBumper(), rightJoystick.button(0))
        .whileTrue(shooter.spinUpFlywheel());
    anyTrigger(xbox.a())
        .whileTrue(shooter.launchSequence());

    anyTrigger(xbox.y(), rightJoystick.button(1))
        .whileTrue(Commands.startEnd(() -> amp.setPneumatic(), () -> amp.stopPneumatic()));
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
    if (this.shifting) {
      return;
    }

    var desiredSpeeds = controller.desiredSpeeds();

    var activity = driveTrain.towardSpeeds(desiredSpeeds, lastSpeeds, getPeriod(), currentGear);
    if (activity.shiftTo != null) {
      shiftTo(activity.shiftTo).schedule();
    }

    lastSpeeds = activity.logicalSpeeds;
    leftDrive.set(activity.motorSpeeds().left);
    rightDrive.set(activity.motorSpeeds().right);
  }
}
