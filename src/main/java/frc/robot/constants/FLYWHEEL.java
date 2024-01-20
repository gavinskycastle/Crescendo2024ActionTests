package frc.robot.constants;

public final class FLYWHEEL {
  public static double gearRatio = 1.0;
  public static final double kP_1 = 0.0; //TODO: Change with SysID
  public static final double kI_1 = 0.0;
  public static final double kD_1 = 0.0;
  public static final double ksDriveVoltSecondsPerMeter = 0.0;
  public static final double kvDriveVoltSecondsSquaredPerMeter = 0.0;
  public static final double kaDriveVoltSecondsSquaredPerMeter = 0.0;

  // Motor 2 (2nd flywheel)
  public static final double kP_2 = 0.0; //TODO: Change with SysID
  public static final double kI_2 = 0.0;
  public static final double kD_2 = 0.0;

  public enum WAIT {
    WAIT_FOR_FLYWHEEL_SETPOINT(0.8),
    WAIT_FOR_AMP_SCORE(0.8);

    private final double value;

    WAIT(double value) {
      this.value = value;
    }

    public double get() {
      return value;
    }
  }

  public enum FLYWHEEL_STATE {
    NONE(0),
    SPEAKER(1);

    private final double value;

    FLYWHEEL_STATE(final double value) {
      this.value = value;
    }

    public double get() {
      return value;
    }
  }
}
