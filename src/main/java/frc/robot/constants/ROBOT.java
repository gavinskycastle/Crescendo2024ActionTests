package frc.robot.constants;

import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.RobotController;

public class ROBOT {
  public static String robotName = "";
  public static boolean disableLogging = false;
  public static boolean disableVisualization = false;
  public static boolean useSysID = false;

  public enum CONTROL_MODE {
    OPEN_LOOP,
    CLOSED_LOOP
  }

  public enum ROBOT_ID {
    // Robot Serial Numbers
    GRIDLOCK("0306ce62"),
    BOBOT("030e6a97"),
    SIM("");

    private final String value;

    ROBOT_ID(final String value) {
      this.value = value;
    }

    public String getSerial() {
      return value;
    }

    public String getName() {
      return name();
    }
  }

  public enum SETPOINT {
    // Units are in Radians
    STOWED(Units.degreesToRadians(0.0));

    private final double value;

    SETPOINT(final double value) {
      this.value = value;
    }

    public double get() {
      return value;
    }
  }

  public static void initGridlock() {}

  public static void initBobot() {
    SWERVE.DRIVE.frontLeftCANCoderOffset = 95.27328;
    SWERVE.DRIVE.frontRightCANCoderOffset = 34.18956;
    SWERVE.DRIVE.backLeftCANCoderOffset = 77.51952;
    SWERVE.DRIVE.backRightCANCoderOffset = 330.55668;
  }

  public static void initSim() {}

  public static void initConstants() {
    if (RobotController.getSerialNumber().equals(ROBOT_ID.GRIDLOCK.getSerial())) {
      initGridlock();
    } else if (RobotController.getSerialNumber().equals(ROBOT_ID.BOBOT.getSerial())) {
      initBobot();
    } else if (RobotController.getSerialNumber().equals(ROBOT_ID.SIM.getSerial())) {
      initSim();
    } else {
      System.out.printf(
          "WARN: Robot Serial Not Recognized! Current roboRIO Serial: %s\n",
          RobotController.getSerialNumber());
    }
  }
}
