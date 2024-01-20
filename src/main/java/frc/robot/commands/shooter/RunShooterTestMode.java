// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands.shooter;

import edu.wpi.first.networktables.DoubleSubscriber;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.Shooter;

public class RunShooterTestMode extends CommandBase {
  private Shooter m_shooter;
  private DoubleSubscriber kSetpointSub;

  public void RunElevatorTestMode(Shooter shooter) {
    m_shooter = shooter;

    addRequirements(m_shooter);

    NetworkTable shooterNtTab = NetworkTableInstance.getDefault().getTable("Shuffleboard");
    try {
      shooterNtTab.getDoubleTopic("kSetpointRPM").publish().set(0);
    } catch (Exception m_ignored) {
    }
    kSetpointSub = shooterNtTab.getDoubleTopic("kSetpointRPM").subscribe(0);
  }

  @Override
  public boolean runsWhenDisabled() {
    return true;
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {}

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    DriverStation.reportWarning("SHOOTER TEST MODE ENABLED", false);
    double newSetpoint = kSetpointSub.get(0);

    m_shooter.setDesiredRPM(newSetpoint);
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {}

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return false;
  }
}
