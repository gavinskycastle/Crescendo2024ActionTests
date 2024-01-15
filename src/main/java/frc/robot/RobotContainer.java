// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PS4Controller;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import frc.robot.commands.amp.AmpFlipperForward;
import frc.robot.commands.autos.DriveStriaghtTest;
import frc.robot.commands.autos.ScoreAmp;
import frc.robot.commands.autos.ScoreSpeaker;
import frc.robot.commands.intake.RunIntake;
import frc.robot.commands.intake.SetIntakePercentOutput;
import frc.robot.commands.shooter.SetAndHoldRPMSetpoint;
import frc.robot.commands.swerve.SetSwerveDrive;
import frc.robot.commands.uptake.RunUptake;
import frc.robot.constants.USB;
import frc.robot.simulation.FieldSim;
import frc.robot.subsystems.AmpFlipper;
import frc.robot.subsystems.AmpShooter;
import frc.robot.subsystems.Climber;
import frc.robot.subsystems.Controls;
import frc.robot.subsystems.Intake;
import frc.robot.subsystems.LED;
import frc.robot.subsystems.RobotTime;
import frc.robot.subsystems.Shooter;
import frc.robot.subsystems.SwerveDrive;
import frc.robot.subsystems.Uptake;

public class RobotContainer {
  private final SwerveDrive m_swerveDrive = new SwerveDrive();
  private final Intake m_intake = new Intake();
  private final Uptake m_uptake = new Uptake();
  private final Shooter m_shooter = new Shooter();
  private final AmpFlipper m_flipper = new AmpFlipper();
  private final AmpShooter m_ampshooter = new AmpShooter();
  private final Climber m_climber = new Climber();
  private final LED m_led = new LED();
  private final RobotTime m_robotTime = new RobotTime();
  private final Controls m_controls = new Controls();
  private final FieldSim m_fieldSim = new FieldSim(m_swerveDrive);

  private final CommandXboxController xboxController =
      new CommandXboxController(USB.xBoxController);

  private final SendableChooser<Command> m_autoChooser = new SendableChooser<>();

  private final Joystick leftJoystick = new Joystick(USB.leftJoystick);
  private final Joystick rightJoystick = new Joystick(USB.rightJoystick);

  private final PS4Controller m_testController = new PS4Controller(USB.testController);

  public RobotContainer() {
    initializeSubsystems();
    configureBindings();
    initializeAutoChooser();
  }

  private void initializeSubsystems() {
    if (RobotBase.isReal()) {
      m_swerveDrive.setDefaultCommand(
          new SetSwerveDrive(
              m_swerveDrive,
              () -> leftJoystick.getRawAxis(1),
              () -> leftJoystick.getRawAxis(0),
              () -> rightJoystick.getRawAxis(0)));
    } else {
      m_swerveDrive.setDefaultCommand(
          new SetSwerveDrive(
              m_swerveDrive,
              () -> -m_testController.getRawAxis(1),
              () -> -m_testController.getRawAxis(0),
              () -> -m_testController.getRawAxis(2)));
    }

    m_intake.setDefaultCommand(
        new SetIntakePercentOutput(
            m_intake, xboxController.getLeftY(), xboxController.getRightY()));
  }

  private void configureBindings() {
    //    xboxController.b().whileTrue(new SetIntakePercentOutput(m_intake, -0.85, -0.85));
    //    xboxController.a().whileTrue(new SetIntakePercentOutput(m_intake, -0.75, -0.75));
    //    xboxController.y().whileTrue(new SetIntakePercentOutput(m_intake, -1.0, -1.0));

    xboxController.a().whileTrue(new SetAndHoldRPMSetpoint(m_shooter, 420.69)); //amp
    xboxController.b().whileTrue(new SetAndHoldRPMSetpoint(m_shooter, 420.69)); //sbeaker
    xboxController.rightBumper().whileTrue(new RunIntake(m_intake, 0.5));
    xboxController.povDown().whileTrue(new RunUptake(m_uptake, -0.5));
    xboxController.povUp().whileTrue(new RunUptake(m_uptake, 0.5));
    xboxController.y().whileTrue(new AmpFlipperForward(m_flipper));
  }

  public void initializeAutoChooser() {
    m_autoChooser.addOption("Amp Test", new ScoreAmp(m_flipper, m_ampshooter));
    m_autoChooser.addOption("Speaker Test", new ScoreSpeaker(m_shooter, m_uptake));

    m_autoChooser.setDefaultOption("DriveStraightTest", new DriveStriaghtTest(m_swerveDrive));

    SmartDashboard.putData("AutoChooser", m_autoChooser);
  }

  public Command getAutonomousCommand() {
    // An example command will be run in autonomous
    return m_autoChooser.getSelected();
  }

  public void periodic() {
    m_fieldSim.periodic();
  }
}
