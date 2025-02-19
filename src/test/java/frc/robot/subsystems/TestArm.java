package frc.robot.subsystems;

import static frc.robot.simulation.SimConstants.kMotorResistance;
import static frc.robot.utils.TestUtils.refreshAkitData;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.ctre.phoenix6.controls.DutyCycleOut;
import com.ctre.phoenix6.controls.PositionVoltage;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.sim.TalonFXSimState;
import edu.wpi.first.hal.HAL;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.RobotController;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.simulation.*;
import frc.robot.constants.ARM;
import frc.robot.constants.ROBOT;
import frc.robot.utils.TestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.littletonrobotics.junction.Logger;

@Disabled("To Fix")
public class TestArm {
  static final double DELTA = 0.2; // acceptable deviation range
  static final double WAIT_TIME = 0.04;

  NetworkTableInstance m_nt;
  NetworkTable m_table;

  RobotTime m_robotTime;
  Arm m_arm;
  TalonFX m_armMotor;
  TalonFXSimState m_armMotorSimState;

  SingleJointedArmSim m_armModel;

  private void updateSimArm() {
    var testControl = m_armMotor.getAppliedControl();

    m_armMotorSimState.setSupplyVoltage(
        RobotController.getBatteryVoltage() - m_armModel.getCurrentDrawAmps() * kMotorResistance);
    TestUtils.refreshAkitData();
    Timer.delay(WAIT_TIME);

    var testMotorVoltage = m_armMotorSimState.getMotorVoltage();

    m_armModel.setInputVoltage(m_armMotorSimState.getMotorVoltage());

    m_armModel.update(0.02);

    m_armMotorSimState.setRawRotorPosition(
        Units.radiansToRotations(m_armModel.getAngleRads()) * ARM.gearRatio);

    m_armMotorSimState.setRotorVelocity(
        Units.radiansToRotations(m_armModel.getVelocityRadPerSec()) * ARM.gearRatio);
  }

  @BeforeEach
  public void constructDevices() {
    assert HAL.initialize(500, 0);

    Logger.start();

    m_robotTime = new RobotTime();

    m_arm = new Arm();
    m_armMotor = m_arm.getMotor();
    m_armMotorSimState = m_armMotor.getSimState();
    m_armModel =
        new SingleJointedArmSim(
            ARM.gearBox,
            ARM.gearRatio,
            SingleJointedArmSim.estimateMOI(ARM.length, ARM.mass),
            ARM.length,
            Units.degreesToRadians(ARM.minAngleDegrees),
            Units.degreesToRadians(ARM.maxAngleDegrees),
            false,
            Units.degreesToRadians(ARM.startingAngleDegrees));

    /* enable the robot */
    DriverStationSim.setEnabled(true);
    DriverStationSim.notifyNewData();
    RoboRioSim.resetData();
    SimHooks.setProgramStarted();
    refreshAkitData();

    m_nt = NetworkTableInstance.getDefault();
    m_nt.setServer("localhost", NetworkTableInstance.kDefaultPort4 + 1);
    m_nt.startClient4("unittest");
    m_table = m_nt.getTable("unittest");

    /* delay ~100ms so the devices can start up and enable */
    Timer.delay(0.300);
  }

  @Test
  public void testArmOutput() {
    m_arm.setControlMode(ROBOT.CONTROL_MODE.OPEN_LOOP);
    var percentOutput = new DutyCycleOut(0);
    m_armMotor.setControl(percentOutput.withOutput(1.0));
    refreshAkitData();
    Timer.delay(WAIT_TIME);

    var posPub = m_table.getDoubleTopic("position").publish();
    var voltagePub = m_table.getDoubleTopic("voltage").publish();
    var currentPub = m_table.getDoubleTopic("current").publish();

    for (int i = 0; i < 25; i++) {
      m_arm.periodic();
      updateSimArm();
      posPub.set(Units.radiansToDegrees(m_arm.getAngleDegrees()));
      //            voltagePub.set(m_testModule.getSteerMotor().getMotorVoltage().getValue());
      //            currentPub.set(m_testModule.getSteerMotor().getStatorCurrent().getValue());
      m_nt.flush();
    }

    var testPos = m_arm.getAngleDegrees();
    assertTrue(testPos > 0.0);
  }

  @Test
  public void testArmMotorSetpoint() {
    m_arm.setControlMode(ROBOT.CONTROL_MODE.OPEN_LOOP);
    var positionControl = new PositionVoltage(0);
    m_armMotor.setControl(positionControl.withPosition(100));
    refreshAkitData();
    Timer.delay(WAIT_TIME);

    var posPub = m_table.getDoubleTopic("position").publish();
    var voltagePub = m_table.getDoubleTopic("voltage").publish();
    var currentPub = m_table.getDoubleTopic("current").publish();

    for (int i = 0; i < 25; i++) {
      m_arm.periodic();
      updateSimArm();
      posPub.set(Units.radiansToDegrees(m_arm.getAngleDegrees()));
      //            voltagePub.set(m_testModule.getSteerMotor().getMotorVoltage().getValue());
      //            currentPub.set(m_testModule.getSteerMotor().getStatorCurrent().getValue());
      m_nt.flush();
    }

    var testPos = m_arm.getAngleDegrees();
    assertTrue(testPos > 0.0);
  }

  @Test
  public void testArmSetpoint() {
    m_arm.setDesiredSetpointRotations(ARM.ARM_SETPOINT.FORWARD.get());
    refreshAkitData();
    Timer.delay(WAIT_TIME);

    var posPub = m_table.getDoubleTopic("position").publish();
    var voltagePub = m_table.getDoubleTopic("voltage").publish();
    var currentPub = m_table.getDoubleTopic("current").publish();

    for (int i = 0; i < 25; i++) {
      m_robotTime.periodic();
      m_arm.periodic();
      refreshAkitData();
      Timer.delay(WAIT_TIME);
      updateSimArm();
      posPub.set(Units.radiansToDegrees(m_arm.getAngleDegrees()));
      //            voltagePub.set(m_testModule.getSteerMotor().getMotorVoltage().getValue());
      //            currentPub.set(m_testModule.getSteerMotor().getStatorCurrent().getValue());
      m_nt.flush();
    }

    var testPos = m_arm.getAngleDegrees();
    assertTrue(testPos > 0.0);
  }

  @AfterEach
  void shutdown() throws Exception {}
}
