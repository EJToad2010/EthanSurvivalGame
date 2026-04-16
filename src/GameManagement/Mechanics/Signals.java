package src.GameManagement.Mechanics;

// Contains a list of static, constant signal values
// To be used by ActionResult and DialogManager to control the flow of triggers / animations
public class Signals {
    // Used to inform DialogManager that an action has been performed
    public static final String ATTACK_PERFORMED = "ATTACK_PERFORMED";
    public static final String DEFENSE_RECEIVED = "DEFENSE_RECEIVED";
    public static final String DEFENSE_PERFORMED = "DEFENSE_PERFORMED";
    // Stat changes
    // Used to control any HP changes besides pure attack / defense
    public static final String HEALTH_LOST = "HEALTH_LOST";
    public static final String HEALTH_GAINED = "HEALTH_GAINED";
    // Same as above but explicitly at the selected target
    public static final String TARGET_HEALTH_LOST = "TARGET_HEALTH_LOST";
    public static final String TARGET_HEALTH_GAINED = "TARGET_HEALTH_GAINED";
    // Used to control leveling / XP changes
    public static final String XP_GAINED = "XP_GAINED";
    public static final String LEVEL_UP = "LEVEL_UP";
    // Used to control coin balance
    public static final String COINS_GAINED = "COINS_GAINED";
    public static final String COINS_LOST = "COINS_LOST";
    // Used for sending information (null message)
    public static final String TARGET_OBJECT = "TARGET_OBJECT";
    public static final String CURRENT_OBJECT = "CURRENT_OBJECT";
}
