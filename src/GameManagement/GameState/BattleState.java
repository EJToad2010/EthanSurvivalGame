package src.GameManagement.GameState;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

import src.Characters.EnemyCharacter;
import src.Characters.PlayerCharacter;
import src.Characters.EnemyCharacters.DartGoblin;
import src.Characters.EnemyCharacters.Goblin;
import src.GameManagement.Game;
import src.GameManagement.GameData;
import src.GameManagement.Mechanics.DayManager;
import src.GameManagement.UI.GamePanel;
import src.ItemManager.Items.HealthPool;
import src.ItemManager.Items.HealthPotion;
import src.Teams.EnemyTeam;
import src.Teams.PlayerTeam;

public class BattleState extends GameState{
    // Constants used to define each major step
    private final int INTRO_ANIM = 0;
    private final int SELECT_CHARACTER = 1;
    private final int SELECT_ACTION = 2;
    private final int SELECT_ABILITY = 3;
    private final int SELECT_TARGET = 4;
    private final int ANIMATE_COMBAT = 5;
    private final int ENEMY_TURN = 6;

    // Variables stored between steps
    private PlayerCharacter selectedCharacter;
    private String selectedActionType;
    private int abilityIndex;
    private ArrayList<EnemyCharacter> targets;

    // Variables tracked during a battle
    private int turnNum;
    private int turnHalf;
    private String turnOwner;
    private int playerActionPoints;
    private int actionPointsLeft;
    private PlayerTeam playerTeam;
    private EnemyTeam enemyTeam;

    public BattleState(Game g, DayManager dayManager){
        super(g, dayManager);
        playerTeam = g.getGameData().getPlayerTeamObj();
        enemyTeam = g.getGameData().getEnemyTeamObj();
    }

    // Handle tick when applicable
    public void update(){
        if(currentStep == INTRO_ANIM){
            nextTick();
        }
    }

    // Logic used for each step
    protected void handleStep(int step, int keyCode){

    }

    // Graphics drawn for each step
    protected void drawStep(int step, Graphics graphics){
        playerTeam.drawPlayerTeam(graphics, 100, 100, 500);
    }

    // Calls once when panel is first loaded
    public void onEnter(GamePanel panel){
        panel.setBackground(new Color(81, 126, 184));
        isAnimating = true;
        turnNum = 1;
        turnHalf = 0;
    }
    // Calls once when panel is unloaded
    public void onExit(GamePanel panel){
        panel.setBackground(Color.BLACK);
    }

  // Create a new set of EnemyCharacters equal to enemyBattleCapacity
  /*private void initializeEnemy(){
    GameData data = game.getGameData();
    // Clear previous encounter's enemy team data
    enemyTeam.clearEnemyTeam();
    enemyTeam.getEnemyInventory().clear();
    // enemyBattleCapacity can be between playerBattleCapacity - 1 and playerBattleCapacity + 1
    enemyBattleCapacity = playerBattleCapacity;
    enemyBattleCapacity += (int)(Math.random() * 3) - 1;
    // There will be one additional enemy added approximately every three days
    // dayNum is an int so it will automatically round down
	  enemyBattleCapacity += dayNum/3;
    // On the first day, it is guaranteed that there will only be one enemy to act as a tutorial
    // On the second day, it is guaranteed that there will be two enemies to force difficulty progression
    if(dayNum == 1 || dayNum == 2){
      enemyBattleCapacity = dayNum;
    }
    for(int i = 0; i < enemyBattleCapacity; i++){
      // Currently limited to the earlyGameEnemy set until more difficult enemy types are added into the game.
      String chosenEnemyType = earlyGameEnemies[(int)(Math.random() * earlyGameEnemies.length)];
      String chosenEnemyBehavior = earlyGameEnemyBehaviors[(int)(Math.random() * earlyGameEnemyBehaviors.length)];
      if(chosenEnemyType.equals("Goblin")){
        enemyTeam.addEnemy(new Goblin(nameEnemy("Goblin"), chosenEnemyBehavior), playerTeam.getHighestLevel(), playerTeam.getAvgLevel());
      } else if(chosenEnemyType.equals("DartGoblin")){
        enemyTeam.addEnemy(new DartGoblin(nameEnemy("Dart Goblin"), chosenEnemyBehavior), playerTeam.getHighestLevel(), playerTeam.getAvgLevel());
      }
    }
    // Add randomly generated items to the enemy team's inventory
    // Currently unlocks items starting from day 3
    if(dayNum >=3){
      // 1 allowed item from day 3 and on
      int allowedItems = (dayNum-2);
      for(int i = 0; i < allowedItems; i++){
        // Choose a random item from the predetermined rates defined in GameManager
        String randomItemStr = earlyGameEnemyItems[(int)(Math.random() * earlyGameEnemyItems.length)];
        if(randomItemStr.equals("HealthPotion")){
          // Health Potion can be 20, 30, 40, or 50
          enemyTeam.getEnemyInventory().add(new HealthPotion(((int)(Math.random() * 4) + 2) * 10), 1);
        } else if(randomItemStr.equals("HealthPool")){
          // Health Pool can be 10, 20, 30, or 40
          enemyTeam.getEnemyInventory().add(new HealthPool(((int)(Math.random() * 3) + 1) * 10), 1);
        }
      }
    }
  }*/
}
