package src.GameManagement.GameState;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

import src.Characters.BasicCharacter;
import src.Characters.EnemyCharacter;
import src.Characters.PlayerCharacter;
import src.Characters.EnemyCharacters.DartGoblin;
import src.Characters.EnemyCharacters.Goblin;
import src.GameManagement.Game;
import src.GameManagement.GameData;
import src.GameManagement.Mechanics.ActionResult;
import src.GameManagement.Mechanics.DayManager;
import src.GameManagement.UI.GamePanel;
import src.GameManagement.UI.UIManager;
import src.ItemManager.Items.HealthPool;
import src.ItemManager.Items.HealthPotion;
import src.Misc.StatusEffect;
import src.Teams.EnemyTeam;
import src.Teams.PlayerTeam;

public class BattleState extends GameState{
  // Constants used to define each major step
  private final int INPUT_ERROR = -1;
  private final int INTRO_ANIM = 0;
  private final int SELECT_CHARACTER = 1;
  private final int SELECT_ACTION = 2;
  private final int SELECT_ABILITY = 3;
  private final int SELECT_TARGET = 4;
  private final int PERFORM_ABILITY = 5;
  private final int ENEMY_TURN = 6;

  // Variables stored between steps
  private PlayerCharacter selectedCharacter;
  private String selectedActionType;
  private int selectedAbilityIndex;
  private String selectedAbilityName;
  private int targetAmount;
  private ArrayList<EnemyCharacter> targets = new ArrayList<EnemyCharacter>();
  private int targetIndex;

  // Variables tracked during a battle
  private int turnNum;
  private int turnHalf;
  private String turnOwner;
  private int playerActionPoints;
  private int actionPointsLeft;
  // The list of every enemy that can perform an action in enemyTeam
  private ArrayList<EnemyCharacter> actionableEnemies = new ArrayList<EnemyCharacter>();
  // The enemy character that is currently performing their turn
  private ArrayList<EnemyCharacter> currentActionEnemy;
  private PlayerTeam playerTeam;
  private EnemyTeam enemyTeam;

  // Variables used to track UI
  private int buttonFontSize = 40;

  public BattleState(Game g, DayManager dayManager){
      super(g, dayManager);
      playerTeam = g.getGameData().getPlayerTeamObj();
      enemyTeam = g.getGameData().getEnemyTeamObj();
      playerActionPoints = g.getGameData().getPlayerActionPoints();
  }

  // Handle tick when applicable
  public void update(){
    if(currentStep == INTRO_ANIM){
          nextTick();
          if(frame == 0){
            if(animationTick % 60 == 0){
              nextFrame();
            }
          }
          else if(frame == 1 || frame == 2){
            // Dedicate 75 ticks for each frame
            if(animationTick % 75 == 0){
              nextFrame();
            }
          } else{
            isAnimating = false;
          }
    }
  }

  // Logic used for each step
  protected void handleStep(int step, int keyCode){
    // Handle any misc logic that exists
    if(step == SELECT_CHARACTER){
      if(actionPointsLeft < 1){
        initializeEnemyTurn();
        setStep(ENEMY_TURN);
      }
    } else if(step == ENEMY_TURN){
      if(actionableEnemies.isEmpty()){
        initializePlayerTurn();
        setStep(SELECT_CHARACTER);
      }
    }

    // Handle any dialog that exists
    int exitCode = runDialog(keyCode, false);
    // Dialog ended
    if(step == INTRO_ANIM){
      // After INTRO_ANIM, trigger the event that starts either the enemy or player's turn
      if(turnOwner.equals("Player")){
        initializePlayerTurn();
        setStep(SELECT_CHARACTER);
      } else{
        initializeEnemyTurn();
        setStep(ENEMY_TURN);
      }
    }

    // Handle any inputHandler actions that exist
    if(inputHandler.getButtons().size() > 0 && !dialogManager.getIsActive() && exitCode == -1){
      int input = inputHandler.keyPressed(keyCode);
      if(input == -1){
        return;
      }
      if(step == SELECT_CHARACTER){
        selectedCharacter = playerTeam.getPlayerTeam().get(input);
        nextStep();
      } else if(step == SELECT_ACTION){
        selectedActionType = inputHandler.getButtons().get(input).getText();
        if(selectedActionType.equals("Basic ability") || selectedActionType.equals("Special ability")){
          nextStep();
        } else{
          System.out.println("THIS FEATURE ISN'T OUT YET");
        }
      } else if(step == SELECT_ABILITY){
        selectedAbilityIndex = input;
        selectedAbilityName = selectedCharacter.getSpecialAbilityNames().get(selectedAbilityIndex);
        if(inputHandler.getButtons().get(selectedAbilityIndex).getText().equals("Cancel")){
          setStep(SELECT_ACTION);
        } else if(selectedActionType.equals("Special ability") && selectedCharacter.getCurrentSpecialAbilityCooldowns().get(selectedAbilityIndex) > 0){
          dialogManager.add(selectedAbilityName + " is on a cooldown for " + selectedCharacter.getCurrentSpecialAbilityCooldowns().get(selectedAbilityIndex) + " more turns!");
        } else{
          if(selectedActionType.equals("Basic ability")){
            targetAmount = selectedCharacter.getBasicAbilityEnemyCount(selectedAbilityIndex);
          } else{
            targetAmount = selectedCharacter.getSpecialAbilityEnemyCount(selectedAbilityIndex);
          }
          targetIndex =0;
          nextStep();
        }
      } else if(step == SELECT_TARGET){
        targetIndex = input;
        targets.add(enemyTeam.getEnemyTeam().get(targetIndex));
        setStep(PERFORM_ABILITY);
      } else if(step == PERFORM_ABILITY){

      }else if(step == ENEMY_TURN){

      }
    }
  }

  // Used when a GameState makes use of dialog that contains interaction
  // (Remember to set isHandlingSignal to FALSE when done handling)
  protected void handleSignal(String signal){
    isHandlingSignal = false;
  }

  // Graphics drawn for each step
  protected void drawStep(int step, Graphics graphics){
    if(step == INTRO_ANIM){
      drawScene(scene, graphics);
    } else{
      enemyTeam.drawEnemyTeam(graphics, 680, 200, 500);
      playerTeam.drawPlayerTeam(graphics, 100, 200, 500);
    }
    if(step == SELECT_CHARACTER && !dialogManager.getIsActive()){
      dialogManager.drawDialogBox(graphics);
      inputHandler.spaceButtons(graphics, buttonFontSize, 900, 550);
      // Prompt the user to select a Character from their team
        UIManager.setTextColor(graphics, Color.WHITE);
        UIManager.setFontSize(40);
        UIManager.refreshText(graphics);
        UIManager.drawCenteredStringInBox(graphics, "Select a Character to use.", 0, 650, 1280, 100);
    }
    if(step == SELECT_ACTION){
      dialogManager.drawDialogBox(graphics);
      inputHandler.spaceButtons(graphics, buttonFontSize, 1100, 550);
      // Prompt the user to select an action for their Character
      UIManager.setTextColor(graphics, Color.WHITE);
      UIManager.setFontSize(40);
      UIManager.refreshText(graphics);
      UIManager.drawCenteredStringInBox(graphics, "Select an action to perform.", 0, 650, 1280, 100);
    }
    else if(step == SELECT_ABILITY){
      dialogManager.drawDialogBox(graphics);
      inputHandler.spaceButtons(graphics, buttonFontSize, 1100, 550);
      // Prompt the user to select an action for their Character
      UIManager.setTextColor(graphics, Color.WHITE);
      UIManager.setFontSize(40);
      UIManager.refreshText(graphics);
      UIManager.drawCenteredStringInBox(graphics, "Select an ability to perform.", 0, 650, 1280, 100);
    } else if(step == SELECT_TARGET){
      dialogManager.drawDialogBox(graphics);
      inputHandler.spaceButtons(graphics, buttonFontSize, 1100, 550);
      // Prompt the user to select an enemy target
      UIManager.setTextColor(graphics, Color.WHITE);
      UIManager.setFontSize(40);
      UIManager.refreshText(graphics);
      UIManager.drawCenteredStringInBox(graphics, "Select an enemy to target.", 0, 650, 1280, 100);
      UIManager.setTextColor(graphics, Color.GRAY);
      UIManager.setFontSize(20);
      UIManager.refreshText(graphics);
      UIManager.drawCenteredStringInBox(graphics, "(" + (targets.size() - targetAmount) + ") targets left", 0, 680, 1280, 40);
    }
    dialogManager.draw(graphics);
    if(inputHandler.getButtons().size() > 0 && !dialogManager.getIsActive()){
      inputHandler.draw(graphics);
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
    if(scene == 0){
      if(frame == 0){
        // Intro text in black background
        graphics.setColor(Color.BLACK);
        graphics.fillRect(0, 0, 1280, 720);
        UIManager.setTextColor(graphics, Color.WHITE);
        UIManager.setFontSize(40);
        UIManager.refreshText(graphics);
        UIManager.drawCenteredStringInBox(graphics, "You have encountered an enemy team!", 0, 300, 1280, 100);
      } else if(frame == 1){
        // The Player's Team flys in from the left side of the screen
        playerTeam.drawPlayerTeam(graphics, -500 + animationTick*8, 200, 500);
      } else if(frame == 2){
        playerTeam.drawPlayerTeam(graphics, 100, 200, 500);
        // The Enemy's Team flys in from the right side of the screen
        enemyTeam.drawEnemyTeam(graphics, 1280-animationTick*8, 200, 500);
      } else{
        enemyTeam.drawEnemyTeam(graphics, 680, 200, 500);
        playerTeam.drawPlayerTeam(graphics, 100, 200, 500);
      }
    }
  }

  // Calls once when a new step is first loaded
  protected void onEnterStep(int step){
    if(step == SELECT_CHARACTER){
      inputHandler = createCharacterOptions(playerTeam.getPlayerTeam(), new ArrayList<BasicCharacter>());
    } else if(step == SELECT_ACTION){
      inputHandler = createOptions(new String[]{"Basic ability", "Special ability", "Use item", "Defend", "HELP"});
    } else if(step == SELECT_ABILITY){
      if(selectedActionType.equals("Basic ability")){
        inputHandler = createOptions(playerTeam.getUnlockedBasicAbilityNames(selectedCharacter));
      } else{
        inputHandler = createOptions(playerTeam.getUnlockedSpecialAbilityNames(selectedCharacter));
      }
    } else if(step == SELECT_TARGET){
      refreshAvailableTargets();
    } else if(step == PERFORM_ABILITY){
      // Get the ActionResult of the Character's ability function
      dialogManager.clear();
      ActionResult output;
      if(selectedActionType.equals("Basic abiltiy")){
        try{
          output = selectedCharacter.basicAbility(selectedAbilityIndex, targets.get(targetAmount), playerTeam, enemyTeam);
        } catch(Exception e){
          output = new ActionResult();
        }
      } else{
        try{
          output = selectedCharacter.specialAbility(selectedAbilityIndex, targets.get(targetAmount), playerTeam, enemyTeam);
        } catch(Exception e){
          output = new ActionResult();
        }
      }
      dialogManager.add(output);
    }
  }
  // Calls once when the previous step exits
  protected void onExitStep(int step){
    // Between steps, check if a victory condition has been reached
    // Exit the battle if so
    if(step == SELECT_CHARACTER){
      playerTeam.setSelectedCharacter(selectedCharacter);
    } else if(step == SELECT_ABILITY){
      targets.clear();
    }
  }

  // Calls once when a new frame is first loaded
  protected void onEnterFrame(int scene, int frame){}
  // Calls once when the previous frame is unloaded
  protected void onExitFrame(int scene, int frame){
    if(scene == 0 && frame == 2){
      decideTurnPriority();
    }
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

  // Update the inputHandler to select all available enemy characters
  private void refreshAvailableTargets(){
    inputHandler = createCharacterOptions(enemyTeam.getEnemyTeam(), targets);
  }

  // Based on the combined speed of the player's team and the enemy's team, decide who will go first
  private void decideTurnPriority(){
    dialogManager.clear();
    if(playerTeam.getTotalSpeed() >= enemyTeam.getTotalSpeed()){
      dialogManager.add("The player's team has the higher combined speed and will go first!");
      turnOwner = "Player";
    } else{
      dialogManager.add("The enemy's team has the higher combined speed and will go first!");
      turnOwner = "Enemy";
    }
  }

  // Reset the Player's action points and announce that the Player's turn has been reached
  private void initializePlayerTurn(){
    actionPointsLeft = playerActionPoints;
    dialogManager.clear();
    dialogManager.add("It is your turn!");
    dialogManager.add("You may perform " + actionPointsLeft + " actions during your turn.");
  }

  // Find every Enemy able to perform an action and announce that the Enemy's turn has been reached
  private void initializeEnemyTurn(){
    dialogManager.clear();
    dialogManager.add("It is the enemy's turn!");
    actionableEnemies.clear();
    for(EnemyCharacter e : enemyTeam.getEnemyTeam()){
      if(!e.getIsDead() && !StatusEffect.hasStatusEffect(e, "Stun")){
        actionableEnemies.add(e);
      }
    }
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
    enemyBattleCapacity = data.getEnemyBattleCapacity();
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
    String[] nouns = data.getNouns();
    return adjectives[(int)(Math.random() * adjectives.length)] + " " + nouns[(int)(Math.random() * nouns.length)];
  }
}
