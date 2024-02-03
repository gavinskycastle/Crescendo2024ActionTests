// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands.shooter;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.constants.FLYWHEEL.FLYWHEEL_STATE;
import frc.robot.subsystems.Flywheel;

public class AutoSetPercentSetpoint extends Command {
  Flywheel m_flywheel;
  FLYWHEEL_STATE m_state;

  public AutoSetPercentSetpoint(Flywheel flywheel, FLYWHEEL_STATE state) {
    m_flywheel = flywheel;
    m_state = state;

    addRequirements(m_flywheel);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {}

  @Override
  public void execute() {
    m_flywheel.setPercentOutput(m_state.get());
  }

  // Called every time the scheduler runs while the command is scheduled.

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {}

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return true;
  }
}
