package src.GameManagement.GameState;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
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
import src.GameManagement.Mechanics.Signals;
import src.GameManagement.UI.GamePanel;
import src.GameManagement.UI.ImageManager;
import src.GameManagement.UI.InputHandler;
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
  private final int ENEMY_PERFORM_ABILITY = 7;
  private final int SELECT_ITEM = 8;
  private final int SELECT_ITEM_TARGET = 9;
  private final int SELECT_DEFENSE_TARGET = 10;
  private final int HELP = 11;
  private final int PLAYER_VICTORY = 12;
  private final int ENEMY_VICTORY = 13;
  private final int ENEMY_REWARDS = 14;

  // Variables stored between steps
  private PlayerCharacter selectedCharacter;
  private String selectedActionType;
  private int selectedAbilityIndex;
  private String selectedAbilityName;
  private int targetAmount;
  private ArrayList<EnemyCharacter> targets = new ArrayList<EnemyCharacter>();
  private int targetIndex;
  private EnemyCharacter currentTarget;
  private boolean isTargetingAllEnemies = false;
  private PlayerCharacter selectedDefenseTarget;

  private int enemyRewardIndex;

  // Variables tracked during a battle
  private int turnNum;
  private int turnHalf;
  private String turnOwner;
  private int playerActionPoints;
  private int actionPointsLeft;
  // The list of every enemy that can perform an action in enemyTeam
  private ArrayList<EnemyCharacter> actionableEnemies = new ArrayList<EnemyCharacter>();
  private int actionableEnemyIndex;
  private PlayerTeam playerTeam;
  private EnemyTeam enemyTeam;

  // Variables used to track UI
  private int buttonFontSize = 40;

  // Graphics variables
  private BufferedImage sky;
  private BufferedImage ground;

  public BattleState(Game g, DayManager dayManager){
      super(g, dayManager);
      playerTeam = g.getGameData().getPlayerTeamObj();
      enemyTeam = g.getGameData().getEnemyTeamObj();
      playerActionPoints = g.getGameData().getPlayerActionPoints();
  }

  // Handle tick when applicable
  public void update(){
    runDialogSignalChecks();
    int exitCode = runEmptyDialogCheck();
    if(exitCode == 0){
      handleDialogEndedLogic(currentStep);
    }
    if(currentStep > 0){
      checkWinConditions();
    }
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

    // Handle any dialog that exists
    int exitCode = runDialog(keyCode, false);
    if(exitCode == -1 && (dialogManager.getIsActive() || dialogManager.size() == 0)){
      return;
    }
    if(exitCode == 0){
      handleDialogEndedLogic(step);
    }

    // Handle any inputHandler actions that exist
    if(inputHandler.getButtons().size() > 0 && !dialogManager.getIsActive() && exitCode == -1){
      int input = inputHandler.keyPressed(keyCode);
      if(input == -1){
        return;
      }
      if(step == SELECT_CHARACTER){
        selectedCharacter = playerTeam.getPlayerTeam().get(input);
        playerTeam.setSelectedCharacterIndex(input);
        nextStep();
      } else if(step == SELECT_ACTION){
        selectedActionType = inputHandler.getButtons().get(input).getText();
        if(selectedActionType.equals("Basic ability") || selectedActionType.equals("Special ability")){
          nextStep();
        } else if(selectedActionType.equals("Defend")){
          setStep(SELECT_DEFENSE_TARGET);
        }else if(selectedActionType.equals("Use item")){
          if(playerTeam.getPlayerInventory().getInventory().size() > 0){
            setStep(SELECT_ITEM);
          } else{
            dialogManager.add("Your inventory is empty.");
          }
        }else{
          System.out.println("THIS FEATURE ISN'T OUT YET HELEN KELLER");
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
          if(targetAmount == 999){
            dialogManager.add(selectedAbilityName + " will target all enemies!");
          } else{
            setStep(SELECT_TARGET);
          }
        }
      } else if(step == SELECT_TARGET){
        targetIndex = input;
        targets.add(enemyTeam.getEnemyTeam().get(targetIndex));
        currentTarget = enemyTeam.getEnemyTeam().get(targetIndex);
        setStep(PERFORM_ABILITY);
      } else if(step == SELECT_DEFENSE_TARGET){
        selectedDefenseTarget = playerTeam.getPlayerTeam().get(input);
        if(selectedDefenseTarget == selectedCharacter){
          dialogManager.add(selectedDefenseTarget.setIsDefending(true));
        } else{
          playerTeam.getProtectedCharacters().add(selectedDefenseTarget);
          playerTeam.getProtectedCharacterAmounts().add(selectedCharacter.getDefenseStrength());
          dialogManager.add(selectedDefenseTarget.getName() + " will receive " + selectedCharacter.getDefenseStrength() + " defense strength from " + selectedCharacter.getName() + "!");
        }
      }
    }
  }

  private void handleDialogEndedLogic(int step){
    // Dialog ended
    if(step == INTRO_ANIM){
      // After INTRO_ANIM, trigger the event that starts either the enemy or player's turn
      if(turnOwner.equals("Player")){
        playerTeam.resetSelectedCharacterIndex();
        initializePlayerTurn();
        setStep(SELECT_CHARACTER);
      } else{
        playerTeam.resetSelectedCharacterIndex();
        initializeEnemyTurn();
        setStep(ENEMY_TURN);
      }
    } else if(step == SELECT_ABILITY){
      if(targetAmount == 999){
        setStep(PERFORM_ABILITY);
      }
    }else if(step == PERFORM_ABILITY){
      // Return to selecting the next target if more targets are left
      // Return to next action if no targets are left
      // Go to enemy turn if no actions are left
      if(targets.size() < targetAmount || (targetAmount == 999 && targetIndex < targets.size())){
        dialogManager.clear();
        if(!isTargetingAllEnemies && targetAmount != 999){
          dialogManager.add("You may select " + (targetAmount-targets.size()) + " more targets.");
        }
        if(targetAmount != 999){
          setStep(SELECT_TARGET);
          System.out.println("Next target");
        } else{
         findNextTargetAllEnemies(true);
        }
        
      } else if(actionPointsLeft > 1){
        nextAction();
      } else{
        nextTurnHalfActions();
      }
    } else if(step == ENEMY_TURN){
      // Move automatically to an enemy ability
      setStep(ENEMY_PERFORM_ABILITY);
    } else if(step == ENEMY_PERFORM_ABILITY){
      // Make the next enemy perform an action
      // Move on to player turn if no enemies are left
      actionableEnemyIndex++;
      if(actionableEnemyIndex == -1 || actionableEnemyIndex >= actionableEnemies.size()){
        System.out.println("Enemy turn over");
        initializePlayerTurn();
        nextTurnHalf();
        setStep(SELECT_CHARACTER);
      }else{
        setStep(ENEMY_TURN);
      }
    } else if(step == PLAYER_VICTORY){
      setStep(ENEMY_REWARDS);
    } else if(step == ENEMY_REWARDS){
      dayManager.nextPhase();
    } else if(step == SELECT_DEFENSE_TARGET){
      if(actionPointsLeft > 1){
        nextAction();
      } else{
        nextTurnHalfActions();
      }
    }
  }

  // If targeting all enemies, automatically find the next relevant target
  private void findNextTargetAllEnemies(boolean changeStep){
     while(true){
      targetIndex++;
      System.out.println(targetIndex);
      if(targetIndex >= enemyTeam.getEnemyTeam().size()){
        break;
      }
      if(!enemyTeam.getEnemyTeam().get(targetIndex).getIsDead()){
        break;
      }
    }
    if(targetIndex >= enemyTeam.getEnemyTeam().size()){
      if(actionPointsLeft > 1){
        System.out.println("next action path");
        nextAction();
      } else{
        System.out.println("next turn half path");
        nextTurnHalfActions();
      }
    } else{
      System.out.println("perform ability path");
      currentTarget = enemyTeam.getEnemyTeam().get(targetIndex);
      dialogManager.clear();
      if(changeStep){
        setStep(PERFORM_ABILITY);
      }
    }
  }

  // Move on to the next action
  private void nextAction(){
    actionPointsLeft--;
    playerTeam.resetSelectedCharacterIndex();
    dialogManager.clear();
    dialogManager.add("You have " + actionPointsLeft + " actions left this turn.");
    isTargetingAllEnemies = false;
    System.out.println("Next action");
    setStep(SELECT_CHARACTER);
  }

  // Move on to enemy turn after actions run out
  private void nextTurnHalfActions(){
    System.out.println("Player turn over");
    nextTurnHalf();
    initializeEnemyTurn();
    setStep(ENEMY_TURN);
  }

  // Used when a GameState makes use of dialog that contains interaction
  // (Remember to set isHandlingSignal to FALSE when done handling)
  protected void handleSignal(String signal, double amount){
    //System.out.println(signal+ " " + calculateCurrentTurnOwner());
    // Signals that take one tick to perform
    if((calculateCurrentTurnOwner().equals("Player") && signal.equals(Signals.HEALTH_GAINED))||(calculateCurrentTurnOwner().equals("Enemy") && signal.equals(Signals.TARGET_HEALTH_GAINED))){
      System.out.println("Player health gained");
      selectedCharacter.changeCurrentHP(amount);
      isHandlingSignal = false;
    } else if((calculateCurrentTurnOwner().equals("Player") && signal.equals(Signals.HEALTH_LOST))||(calculateCurrentTurnOwner().equals("Enemy") && signal.equals(Signals.TARGET_HEALTH_LOST))){
      System.out.println("Player health lost");
      selectedCharacter.changeCurrentHP(-amount);
      isHandlingSignal = false;
    } else if((calculateCurrentTurnOwner().equals("Player") && signal.equals(Signals.TARGET_HEALTH_GAINED))||(calculateCurrentTurnOwner().equals("Enemy") && signal.equals(Signals.HEALTH_GAINED))){
      System.out.println("Target health gained");
      currentTarget.changeCurrentHP(amount);
      isHandlingSignal = false;
    } else if((calculateCurrentTurnOwner().equals("Player") && signal.equals(Signals.TARGET_HEALTH_LOST))||(calculateCurrentTurnOwner().equals("Enemy") && signal.equals(Signals.HEALTH_LOST))){
      System.out.println("Target health lost");
      currentTarget.changeCurrentHP(-amount);
      isHandlingSignal = false;
    } else if((calculateCurrentTurnOwner().equals("Enemy")) && signal.equals(Signals.TARGET_OBJECT)){
      selectedCharacter = playerTeam.findCharWithID((int)amount);
      isHandlingSignal = false;
    } else if((calculateCurrentTurnOwner().equals("Player")) && signal.equals(Signals.TARGET_OBJECT)){
      currentTarget = enemyTeam.findCharWithID((int)amount);
      isHandlingSignal = false;
    }else if(signal.equals(Signals.COINS_GAINED)){
      System.out.println("Coins gained");
      playerTeam.increaseCoinBalance((int)amount);
    } else if(signal.equals(Signals.ATTACK_PERFORMED)){

    } 
    else{
      System.out.println("No valid signal handler found.");
      isHandlingSignal = false;
    }
    if(!isHandlingSignal){
      System.out.println("Signal concluded");
      isOnSignalCooldown = true;
    }
  }

  // Graphical content of a GameState's signal
  // (Remember to set isHandlingSignal to FALSE when done handling)
  protected void drawSignal(String signal, Graphics graphics, int tick){
    if(signal.equals(Signals.ATTACK_PERFORMED) && calculateCurrentTurnOwner().equals("Player")){
      selectedCharacter.setIsAnimating(true);
      if(tick > selectedCharacter.getAttackAnimationLength()){
        isHandlingSignal = false;
        selectedCharacter.setIsAnimating(false);
      } else{
        selectedCharacter.drawAttackAnimation(graphics, tick);
      }
    }
  }

  // Graphics drawn for each step
  protected void drawStep(int step, Graphics graphics){
    // Draw the background componentsx that are always rendered
    graphics.drawImage(sky, 0, 0, null);
    graphics.drawImage(ground, 0, 360, null);
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
      UIManager.drawCenteredStringInBox(graphics, "(" + (targetAmount - targets.size()) + " targets left)", 0, 700, 1280, 20);
    } else if(step == ENEMY_REWARDS){
      if(enemyRewardIndex < enemyTeam.getEnemyTeam().size()){
        enemyRewardIndex++;
        addDialogForEnemyReward();
      }
    } else if(step == SELECT_DEFENSE_TARGET){
      dialogManager.drawDialogBox(graphics);
      inputHandler.spaceButtons(graphics, buttonFontSize, 900, 550);
      // Prompt the user to select a Character from their team to defend
      UIManager.setTextColor(graphics, Color.WHITE);
      UIManager.setFontSize(40);
      UIManager.refreshText(graphics);
      UIManager.drawCenteredStringInBox(graphics, "Select a Character to defend.", 0, 650, 1280, 100);
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
    if(currentStep != INTRO_ANIM){
      return;
    }
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
      // Init the character the player can choose from
      inputHandler = createCharacterOptions(playerTeam.getPlayerTeam(), new ArrayList<BasicCharacter>());
    } else if(step == SELECT_ACTION){
      // Init the possible choice the player can make
      inputHandler = createOptions(new String[]{"Basic ability", "Special ability", "Use item", "Defend"});
    } else if(step == SELECT_ABILITY){
      // Init the possible abilities the player can pick from
      if(selectedActionType.equals("Basic ability")){
        inputHandler = createOptions(playerTeam.getUnlockedBasicAbilityNames(selectedCharacter));
      } else{
        inputHandler = createOptions(playerTeam.getUnlockedSpecialAbilityNames(selectedCharacter));
      }
    } else if(step == SELECT_TARGET){
      refreshAvailableTargets();
      // If an ability can target all available enemies, move on automatically without user input
      if(targetAmount == 999){
        System.out.println("targeting all enemies");
        isTargetingAllEnemies = true;
        findNextTargetAllEnemies(false);
      }
    } else if(step == PERFORM_ABILITY){
      inputHandler = new InputHandler();
      // Get the ActionResult of the Character's ability function
      ActionResult output;
      dialogManager.clear();
      if(selectedActionType.equals("Basic ability")){
        output = selectedCharacter.basicAbility(selectedAbilityIndex, currentTarget, playerTeam, enemyTeam);
      } else{
        output = selectedCharacter.specialAbility(selectedAbilityIndex, currentTarget, playerTeam, enemyTeam);
      }
      dialogManager.add(output);
    } else if(step== ENEMY_PERFORM_ABILITY){
      inputHandler = new InputHandler();
      // Get the ActionResult of the Enemy's ability function
      ActionResult output;
      dialogManager.clear();
      // Emergency break
      if(actionableEnemyIndex >= actionableEnemies.size()){
        nextTurnHalf();
        initializePlayerTurn();
        return;
      }
      output = actionableEnemies.get(actionableEnemyIndex).takeTurn(playerTeam, enemyTeam);
      dialogManager.add(output);
    } else if(step == SELECT_DEFENSE_TARGET){
      // Init all characters in player team
      inputHandler = createCharacterOptions(playerTeam.getPlayerTeam(), new ArrayList<BasicCharacter>());
      selectedDefenseTarget = new PlayerCharacter("");
    }else if(step == PLAYER_VICTORY){
      // Update dialog
      dialogManager.clear();
      dialogManager.add("Congratulations! All enemies have been defeated.");
    } else if(step == ENEMY_VICTORY){
      // Update dialog
      dialogManager.clear();
      dialogManager.add("Oh no! All your characters have died.");
    } else if(step == ENEMY_REWARDS){
      // Init variables to track enemy rewards
      playerTeam.setIsDrawingXP(true);
      dialogManager.clear();
      enemyRewardIndex = -1;
    }
  }
  // Calls once when the previous step exits
  protected void onExitStep(int step){
    // Between steps, check if a victory condition has been reached
    // Exit the battle if so
    if(step == SELECT_CHARACTER){
      targetIndex = 0;
    } else if(step == SELECT_ABILITY){
      targets.clear();
    } else if(step == ENEMY_PERFORM_ABILITY){
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
      playerTeam.setIsDrawingXP(false);
      resetScene();
      currentStep = INTRO_ANIM;
      dialogManager.clear();
      dialogManager.setIsActive(false);
      sky = ImageManager.loadImage("src/Images/sky.png");
      ground = ImageManager.loadImage("src/Images/ground.png");
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

  // Add the proper messages and signals for DialogManager within Enemy loot
  private void addDialogForEnemyReward(){
    System.out.println("dialog for enemy reward added");
    if(enemyRewardIndex >= enemyTeam.getEnemyTeam().size()){
      return;
    }
    EnemyCharacter currentRewardEnemy = enemyTeam.getEnemyTeam().get(enemyRewardIndex);
    dialogManager.add(currentRewardEnemy.getName() + " dropped " + currentRewardEnemy.getCoinReward() + "g!", Signals.COINS_GAINED, currentRewardEnemy.getCoinReward());
    for(PlayerCharacter p : playerTeam.getPlayerTeam()){
      dialogManager.add(p.increaseXP(currentRewardEnemy.getXPReward()));
    }
    System.out.println(dialogManager.getDialogSequence());
  }

  // Based on the combined speed of the player's team and the enemy's team, decide who will go first
  private void decideTurnPriority(){
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
    dialogManager.add("It is your turn!");
    dialogManager.add("You may perform " + actionPointsLeft + " actions during your turn.");
    playerTeam.decreasePlayerCooldowns();
    playerTeam.resetSelectedCharacterIndex();
    playerTeam.resetProtectedCharacters();
    playerTeam.resetPlayerDefense();
    playerTeam.resetIsAnimating();
  }

  // Find every Enemy able to perform an action and announce that the Enemy's turn has been reached
  private void initializeEnemyTurn(){
    System.out.println("Enemy turn initialized");
    dialogManager.add("It is the enemy's turn!");
    actionableEnemies.clear();
    for(EnemyCharacter e : enemyTeam.getEnemyTeam()){
      if(!e.getIsDead() && !StatusEffect.hasStatusEffect(e, "Stun")){
        actionableEnemies.add(e);
      }
    }
    if(actionableEnemies.isEmpty()){
      actionableEnemyIndex = -1;
    } else{
      actionableEnemyIndex = 0;
    }
    if(turnNum > 1){
      enemyTeam.decreaseEnemyCooldowns();
    }
  }

  // Increment turn half
  // If turnOwner returned to original, increment turnNum
  private void nextTurnHalf(){
    turnHalf++;
    if(turnHalf > 1){
      turnHalf = 0;
      turnNum++;
    }
  }

  // Calculate who's turn it currently is based on turnHalf and turnOwner
  private String calculateCurrentTurnOwner(){
    if(turnOwner.equals("Player")){
      if(turnHalf == 0){
        return "Player";
      } else{
        return "Enemy";
      }
    } else{
      if(turnHalf == 0){
        return "Enemy";
      } else{
        return "Player";
      }
    }
  }

  // Check for win/lose conditions during battle
  // If either all of the player all of the enemy's characters die
  private boolean hasPlayerWon(){
    for(EnemyCharacter c : enemyTeam.getEnemyTeam()){
      if(!c.getIsDead()){
        return false;
      }
    }
    return true;
  }
  private boolean hasEnemyWon(){
    for(PlayerCharacter c : playerTeam.getPlayerTeam()){
      if(!c.getIsDead()){
        return false;
      }
    }
    return true;
  }

  // Use conditional methods to automatically redirect the battle to the victory step
  private void checkWinConditions(){
    if(currentStep == PLAYER_VICTORY || currentStep == ENEMY_VICTORY || currentStep == ENEMY_REWARDS){
      return;
    }
    if(hasPlayerWon()){
      setStep(PLAYER_VICTORY);
    } else if(hasEnemyWon()){
      setStep(ENEMY_VICTORY);
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
