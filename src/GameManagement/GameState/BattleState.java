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
import src.GameManagement.Mechanics.ActionResult;
import src.GameManagement.UI.GamePanel;
import src.GameManagement.UI.UIManager;
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
            if(frame == 0 && animationTick % 120 == 0){
              nextFrame();
            } else if(frame == 1 || frame == 2){
              // Dedicate 250 ticks for each frame
              if(animationTick % 250 == 0){
                nextFrame();
              }
            } else{
              isAnimating = false;
              nextStep();
            }
      }
    }

    // Logic used for each step
    protected void handleStep(int step, int keyCode){
      
    }

    // Graphics drawn for each step
    protected void drawStep(int step, Graphics graphics){
        if(step == INTRO_ANIM){
          drawScene(scene, graphics);
        } else{
          enemyTeam.drawEnemyTeam(graphics, 1280-250, 100, 500);
        }
    }

    // Call in drawStep if a panel wants to make use of animations
    protected void drawScene(int scene, Graphics graphics){
      if(scene == 0){
        drawFrame(scene, frame, graphics);
      }
    }
    // Call in drawScene to make use of individual frames
    protected void drawFrame(int scene, int frame, Graphics graphics){
      System.out.println(frame);
      if(frame == 0){
        // Intro text in black background
        graphics.setColor(Color.BLACK);
        graphics.fillRect(0, 0, 1280, 720);
        UIManager.setTextColor(graphics, Color.WHITE);
        UIManager.setFontSize(40);
        UIManager.refreshText(graphics);
        UIManager.drawCenteredStringInBox(graphics, "You have encountered an enemy team!", 0, 400, 1280, 100);
      } else if(frame == 1){
        // The Player's Team flys in from the left side of the screen
        playerTeam.drawPlayerTeam(graphics, animationTick, 100, 500);
      } else if(frame == 2){
        playerTeam.drawPlayerTeam(graphics, 500, 100, 500);
        // The Enemy's Team flys in from the right side of the screen
        enemyTeam.drawEnemyTeam(graphics, 1280-animationTick, 100, 500);
      }
    }

    // Calls once when a new step is first loaded
    protected void onEnterStep(int step){}
    // Calls once when the previous step exits
    protected void onExitStep(int step){
      // Between steps, check if a victory condition has been reached
      // Exit the battle if so
    }

    // Calls once when panel is first loaded
    public void onEnter(GamePanel panel){
        panel.setBackground(new Color(81, 126, 184));
        isAnimating = true;
        turnNum = 1;
        turnHalf = 0;
        initializeEnemy();
        resetScene();
        currentStep = INTRO_ANIM;
        panel.repaint();
    }
    // Calls once when panel is unloaded
    public void onExit(GamePanel panel){
        panel.setBackground(Color.BLACK);
    }

  // Create a new set of EnemyCharacters equal to enemyBattleCapacity
  // Generate proper names, types, and items for the team
  private void initializeEnemy(){
    GameData data = game.getGameData();
    // Retrieve necessary variables from GameData
    int enemyBattleCapacity = data.getEnemyBattleCapacity();
    int dayNum = data.getDayNum();
    String[] earlyGameEnemies = data.getEarlyGameEnemies();
    String[] earlyGameEnemyBehaviors = data.getEarlyGameEnemyBehaviors();
    String[] earlyGameEnemyItems = data.getEarlyGameEnemyItems();
    // Clear previous encounter's enemy team data
    enemyTeam.clearEnemyTeam();
    enemyTeam.getEnemyInventory().clear();
    // enemyBattleCapacity can be between playerBattleCapacity - 1 and playerBattleCapacity + 1
    data.setEnemyBattleCapacity(data.getPlayerBattleCapacity());
    data.increaseEnemyBattleCapacity((int)(Math.random() * 3) - 1);
    // There will be one additional enemy added approximately every three days
    // dayNum is an int so it will automatically round down
	  data.increaseEnemyBattleCapacity(dayNum/3);
    // On the first day, it is guaranteed that there will only be one enemy to act as a tutorial
    // On the second day, it is guaranteed that there will be two enemies to force difficulty progression
    if(dayNum == 1 || dayNum == 2){
      data.setEnemyBattleCapacity(dayNum);
    }
    for(int i = 0; i < enemyBattleCapacity; i++){
      // Currently limited to the earlyGameEnemy set until more difficult enemy types are added into the game.
      String chosenEnemyType = earlyGameEnemies[(int)(Math.random() * earlyGameEnemies.length)];
      String chosenEnemyBehavior = earlyGameEnemyBehaviors[(int)(Math.random() * earlyGameEnemyBehaviors.length)];
      if(chosenEnemyType.equals("Goblin")){
        enemyTeam.addEnemy(new Goblin(nameEnemy(data, "Goblin"), chosenEnemyBehavior), playerTeam.getHighestLevel(), playerTeam.getAvgLevel());
      } else if(chosenEnemyType.equals("DartGoblin")){
        enemyTeam.addEnemy(new DartGoblin(nameEnemy(data, "Dart Goblin"), chosenEnemyBehavior), playerTeam.getHighestLevel(), playerTeam.getAvgLevel());
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
    enemyTeam = data.getEnemyTeamObj();
  }

  // Given a list from adjectives.txt, generate a name containing a random adjective + the enemy type
  private String nameEnemy(GameData data, String enemyType){
    String output = "";
    String[] adjectives = data.getAdjectives();
    return adjectives[(int)(Math.random() * adjectives.length)] + " " + enemyType;
  }
}
