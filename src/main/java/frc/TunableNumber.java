package frc;

// import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
// import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;

/**
 * Gets value from dashboard in tuning mode, returns default if value not in
 * dashboard.
 */
public class TunableNumber {
  private static final String tableKey = "TunableNumbers";

  private String key;
  private double defaultValue;
  private double lastHasChangedValue = defaultValue;

  /**
   * Create a new TunableNumber
   * 
   * @param dashboardKey Key on dashboard
   */
  public TunableNumber(String tableString, String dashboardKey) {
    this.key = tableString + "/" + dashboardKey;
  }

  /**
   * Create a new TunableNumber with the default value
   * 
   * @param dashboardKey Key on dashboard
   * @param defaultValue Default value
   */
  public TunableNumber(String dashboardKey, double defaultValue) {
    this(tableKey, dashboardKey);
    setDefault(defaultValue);
  }

  public TunableNumber(String dashboardKey, double defaultValue, String tableKey) {
    this(tableKey, dashboardKey);
    setDefault(defaultValue);
  }

  /**
   * Get the default value for the number that has been set
   * 
   * @return The default value
   */
  public double getDefault() {
    return defaultValue;
  }

  /**
   * Set the default value of the number
   * 
   * @param defaultValue The default value
   */
  public void setDefault(double defaultValue) {
    this.defaultValue = defaultValue;
    // This makes sure the data is on NetworkTables but will not change it
    SmartDashboard.putNumber(key,
        SmartDashboard.getNumber(key, defaultValue));
  }

  /**
   * Get the current value, from dashboard if available and in tuning mode
   * 
   * @return The current value
   */
  public double get() {
    return SmartDashboard.getNumber(key, defaultValue);
  }

  /**
   * Checks whether the number has changed since our last check
   * 
   * @return True if the number has changed since the last time this method was
   *         called, false
   *         otherwise
   */
  public boolean hasChanged() {
    double currentValue = get();
    if (currentValue != lastHasChangedValue) {
      lastHasChangedValue = currentValue;
      return true;
    }

    return false;
  }
}