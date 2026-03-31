package src.GameManagement.GameState;

import java.awt.Graphics;
import java.util.ArrayList;

import src.Characters.EnemyCharacter;
import src.Characters.PlayerCharacter;
import src.GameManagement.Game;
import src.GameManagement.Mechanics.DayManager;
import src.GameManagement.UI.GamePanel;
import src.Teams.EnemyTeam;
import src.Teams.PlayerTeam;

public class BattleState extends GameState{
    // Constants used to define each major step
    private final int SELECT_CHARACTER = 0;
    private final int SELECT_ACTION = 1;
    private final int SELECT_ABILITY = 2;
    private final int SELECT_TARGET = 3;
    private final int ANIMATION = 4;
    private final int ENEMY_TURN = 5;

    // Variables stored between steps
    private PlayerCharacter selectedCharacter;
    private String selectedActionType;
    private int abilityIndex;
    private ArrayList<EnemyCharacter> targets;

    // Variables tracked during a battle
    private int playerActionPoints;
    private int actionPointsLeft;
    private PlayerTeam playerTeam;
    private EnemyTeam enemyTeam;

    public BattleState(Game g, DayManager dayManager){
        super(g, dayManager);
    }

    // Logic used for each step
    protected void handleStep(int step, int keyCode){

    }

    // Graphics drawn for each step
    protected void drawStep(int step, Graphics graphics){
        
    }

    // Calls once when panel is first loaded
    public void onEnter(GamePanel panel){}
    // Calls once when panel is unloaded
    public void onExit(GamePanel panel){}
}
