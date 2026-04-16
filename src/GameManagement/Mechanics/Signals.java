package src.GameManagement.Mechanics;

// Contains a list of static, constant signal values
// To be used by ActionResult and DialogManager to control the flow of triggers / animations
public class Signals {
    public static final String ATTACK_PERFORMED = "ATTACK_PERFORMED";
    public static final String DEFENSE_RECEIVED = "DEFENSE_RECEIVED";
    public static final String DEFENSE_PERFORMED = "DEFENSE_PERFORMED";
    // Used to control any HP changes besides pure attack / defense
    public static final String HEALTH_LOST = "HEALTH_LOST";
    public static final String HEALTH_GAINED = "HEALTH_GAINED";
    // Same as above but explicitly at the selected target
    public static final String TARGET_HEALTH_LOST = "TARGET_HEALTH_LOST";
    public static final String TARGET_HEALTH_GAINED = "TARGET_HEALTH_GAINED";
    // Used for sending information
    public static final String PLAYER_CHARACTER_OBJECT = "PLAYER_CHARACTER_OBJECT";
}
