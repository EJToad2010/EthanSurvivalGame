package src.GameManagement.GameState;

import src.GameManagement.Game;
import src.GameManagement.Mechanics.DayManager;
import src.Teams.EnemyTeam;
import src.Teams.PlayerTeam;

public class BattleState extends GameState{
    // Constants used to define each individual step
    private final int INVALID_INPUT = -1;
    private final int SELECT_CHARACTER = 0;

    // Variables tracked during a battle
    private int playerActionPoints;
    private PlayerTeam playerTeam;
    private EnemyTeam enemyTeam;

    public BattleState(Game g, DayManager dayManager){
        super(g, dayManager);
    }
}
