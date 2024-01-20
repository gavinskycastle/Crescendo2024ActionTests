// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import com.ctre.phoenix6.configs.Slot0Configs;
import com.ctre.phoenix6.controls.VelocityVoltage;
import com.ctre.phoenix6.hardware.TalonFX;
import edu.wpi.first.math.controller.SimpleMotorFeedforward;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.constants.CAN;
import frc.robot.constants.FLYWHEEL;
import frc.robot.constants.SWERVE.MODULE;

public class Shooter extends SubsystemBase {
  private final TalonFX flywheelmotor1 = new TalonFX(CAN.flywheel1);
  private final TalonFX flywheelmotor2 = new TalonFX(CAN.flywheel2);
  private double m_RPM;
  private double flywheelRPMConversion = 1 / 1;
  private boolean m_testMode = false;
  private final VelocityVoltage driveVelocityControl = new VelocityVoltage(0);

  private final SimpleMotorFeedforward feedforward =
      new SimpleMotorFeedforward(
          FLYWHEEL.ksDriveVoltSecondsPerMeter,
          FLYWHEEL.kvDriveVoltSecondsSquaredPerMeter,
          FLYWHEEL.kaDriveVoltSecondsSquaredPerMeter);

  // private final ConfigFactoryDefault configSelectedFeedbackSensor = new Config
  /* Creates a new Intake. */
  public Shooter() {

    Slot0Configs PEAK1 = new Slot0Configs();

    PEAK1.kP = FLYWHEEL.kP_1;
    PEAK1.kI = FLYWHEEL.kI_1;
    PEAK1.kD = FLYWHEEL.kD_1;

    Slot0Configs PEAK2 = new Slot0Configs();

    PEAK2.kP = FLYWHEEL.kP_2;
    PEAK2.kI = FLYWHEEL.kI_2;
    PEAK2.kD = FLYWHEEL.kD_2;

    // flywheel motor 1
    flywheelmotor1.setInverted(true);
    flywheelmotor1.getConfigurator().apply(PEAK1);
    // flywheel motor 2
    flywheelmotor2.setInverted(false);
    flywheelmotor2.getConfigurator().apply(PEAK2);
  }

  // values that we set

  public void setFlywheelTestMode(boolean mode) {
    m_testMode = mode;
  }

  public void setDesiredRPM(double RPM) {
    double velocityRPS = RPM / (MODULE.kWheelDiameterMeters * Math.PI);
    flywheelmotor1.setControl(
        driveVelocityControl.withVelocity(velocityRPS).withFeedForward(feedforward.calculate(RPM)));
  }

  // values that we are pulling
  public double getRPM1() {
    return m_RPM;
  }

  public double getRPM2() {
    return m_RPM * flywheelRPMConversion;
  }

  private void updateShuffleboard() {
    SmartDashboard.putNumber("RPM1", this.getRPM1());
    SmartDashboard.putNumber("RPM2", this.getRPM2());
  }

  @Override
  public void periodic() {
    this.updateShuffleboard();
  }
}
