// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.controls.Follower;
import com.ctre.phoenix6.controls.StaticBrake;
import com.ctre.phoenix6.hardware.TalonFX;

import edu.wpi.first.networktables.DoubleSubscriber;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.constants.CLIMBER;

public class Climber extends SubsystemBase {
  // private final TalonFX[] elevatorClimbMotors = {new TalonFX(CLIMBER.climbMotor1), new TalonFX(CLIMBER.climbMotor2)};
  private final TalonFX elevatorClimbMotors = new TalonFX(CLIMBER.climbMotor1);
  private final StaticBrake brake = new StaticBrake();
  private final Follower follower = new Follower(0, false);

  private double m_upperLimitMeters = CLIMBER.upperLimitMeters;
  private double m_lowerLimitMeters = CLIMBER.lowerLimitMeters;

  private boolean elevatorClimbSate;
  private double holdPosition;

   // Testing value for mech2d
  public double m_mechHeight = 0.1;

  public DoubleSubscriber m_mechHeightSub;

  NetworkTable climberNtTab =
      NetworkTableInstance.getDefault().getTable("Shuffleboard").getSubTable("Climber");
  
  /** Creates a new climberMechanism. */
  public Climber() {
    // Initialize Test Values
    climberNtTab.getDoubleTopic("Climber Sim Test Height").publish().set(m_mechHeight);
    m_mechHeightSub =
      climberNtTab.getDoubleTopic("Climber Sim Test Height").subscribe(m_mechHeight);


    // for (TalonFX motor : elevatorClimbMotors)
    elevatorClimbMotors.setControl(brake);
    elevatorClimbMotors.getConfigurator().apply(new TalonFXConfiguration());
    elevatorClimbMotors.setInverted(true);
    
    // elevatorClimbMotors[0].getConfigurator().apply(new TalonFXConfiguration());
    // elevatorClimbMotors[0].setInverted(true);
    // elevatorClimbMotors[1].setInverted(true);
    // elevatorClimbMotors[1].setControl(follower);
  }

  public void setClimbState(boolean state) {
    elevatorClimbSate = state;
  }

  public boolean getClimbState() {
    return elevatorClimbSate;
  }

  public void setPercentOutput(double output, boolean enforceLimits) {
    if (enforceLimits){
      if (getHeightMeters() > getUpperLimitMeters())
        output = Math.min(output, 0);

      if (getHeightMeters() < getLowerLimitMeters())
        output = Math.max(output, 0);
    }

    elevatorClimbMotors.set(output);
  }

  public double getVelocityMetersPerSecond() {
    return elevatorClimbMotors.getRotorVelocity().getValueAsDouble() * CLIMBER.encoderCountsToMeters *10;
  }

  public double getHeightMeters() {
    return getHeightEncoderCounts() * CLIMBER.encoderCountsToMeters;
  }

  public double getHeightEncoderCounts() {
    return elevatorClimbMotors.getRotorPosition().getValueAsDouble();
  }

  public void setSensorPosition(double meters) {
    elevatorClimbMotors.setPosition(meters / CLIMBER.encoderCountsToMeters);
  }

  public void holdClimber() {
    elevatorClimbMotors.set(holdPosition);
  }

  public void setHoldPosition(double position) {
    holdPosition = position;
  }

  public void setLowerLimitMeters(double meters) {
    m_lowerLimitMeters = meters;
  }

  public double getLowerLimitMeters() {
    return m_lowerLimitMeters;
  }

  public void setUpperLimitMeters(double meters) {
    m_upperLimitMeters = meters;
  }

  public double getUpperLimitMeters() {
    return m_upperLimitMeters;
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }
}
