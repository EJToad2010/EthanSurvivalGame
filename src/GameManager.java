package src;
import java.util.*;
// Controls the gameplay loop including win/lose conditions
class GameManager {
  static Scanner inputScanner = new Scanner(System.in);
  // Manually set this to ture during repeated playtesting
  private boolean skipTutorial = true;
  
  private Scanner s = new Scanner(System.in);
  // Arrays used to track enemy difficulty progression through their class types.
  // Influences the probability that each class will be chosen
  private String[] earlyGameEnemies = new String[]{"Goblin", "Goblin", "DartGoblin"};
  // Enemy behaviors change as the game progresses, adding more complex roles.
  // This simulates the AI getting smarter.
  private String[] earlyGameEnemyBehaviors = new String[]{"RANDOM", "RANDOM", "RANDOM", "AGGRESSIVE", "DEFENSIVE"};
  
  // Attributes needed for Player and Enemy classes outside of battle
  private int playerBattleCapacity = 2;
  private int enemyBattleCapacity = 2;
  
  // Attributes used to keep track of game progression
  private int dayNum = 1;
  private int turnNum;
  private String currentPlayer;
  
  // Attributes used within battles, which are the core part of the gameplay loop
  private int playerActionPoints = playerBattleCapacity;
  private int actionPointsLeft;
  private int currentActionPoints;
  private int enemyActionPoints = enemyBattleCapacity;
  private PlayerTeam playerTeam;
  private EnemyTeam enemyTeam;
  
  private ArrayList<String> adjectives = new ArrayList<String>();
  
  // Constructors
  public GameManager(PlayerTeam playerTeam, EnemyTeam enemyTeam){
    this.playerTeam = playerTeam;
    this.enemyTeam = enemyTeam;
    fillAdjectives();
  }
  
  public GameManager(){
    this(new PlayerTeam(), new EnemyTeam());
  }
  
  // Initialize file ArrayLists in constructor
  private void fillAdjectives(){
    String[] adjectivesStr = Data.getAdjectives();
    for(String s : adjectivesStr){
      adjectives.add(s);
    }
  }
  
  // Getter methods
  public PlayerTeam getPlayerTeam(){
    return playerTeam;
  }
  
  public EnemyTeam getEnemyTeam(){
    return enemyTeam;
  }
  
  /* Gameplay loop
  // Increase turnNum by 1
  // currentPlayer = player
  // Prompt player for actions between their characters
  // Handle character actions
  // Check end conditions
  // currentPlayer = enemy
  // Enemy AI decides best action
  // Handle enemy actions
  // Check end conditions */
  
  // Broad method that contains the entire gameplay
  public void run() throws InterruptedException {
    clearScreen();
    playerTeam.clearPlayerTeam();
    if(!skipTutorial){
      System.out.println("You have been hired to defeat the enemies attacking your kingdom.");
      Thread.sleep(1000);
      System.out.println("Train skilled fighters and survive as long as possible!");
      Thread.sleep(1000);
      anythingToContinue();
    }
    
    clearScreen();
    while(true){
      StatusEffect.resetStatusEffects();
      System.out.println("\nDay " + dayNum);
      Thread.sleep(1000);
      initialize();

      // Exploration phase

      // Battle phase
      System.out.println("You have encountered an enemy team!\n");
      Thread.sleep(1000);
      runBattleLoop();
      StatusEffect.resetStatusEffects();
      if(hasPlayerWon()){
        System.out.println("\nCongratulations! You have defeated the enemy team.\n");
      } else{
        System.out.println("You have lost... restart the game to try again.");
        break;
      }
      anythingToContinue();
      handleEnemyRewards();
      clearScreen();
      handleShop();
      
      System.out.println("It is now the evening.");
      Thread.sleep(1000);
      System.out.println("Your characters have constructed a small campsite to stay for the night.");
      Thread.sleep(1000);
      anythingToContinue();
      dayNum++;
    }
  }
  
  // Set up the characters in Player and Enemy before gameplay starts
  private void initialize() throws InterruptedException{
    initializePlayer();
    initializeEnemy();
  }
  
  private void initializePlayer() throws InterruptedException{
    int allowedCharacters = playerBattleCapacity - playerTeam.getPlayerTeam().size();
    if(allowedCharacters > 0){
      if(allowedCharacters == 1){
        System.out.println("You may add 1 new character to your team!");
      } else{
        System.out.println("You may add " + allowedCharacters + " new characters to your team!");
      }
      anythingToContinue();
    }
    boolean isFirstLoop = true;
    while(playerTeam.getPlayerTeam().size() < playerBattleCapacity){
      if(!isFirstLoop){
        System.out.println("You may create another character.");
      }
      String message = "Choose a class for your character:\n";
      message += "1: Knight\n";
      message += "2: Archer\n";
      message += "3: Wizard\n";
      message += "4: HELP";
      int classInput = GameManager.obtainInput(message, 1, 4, false);
      if(classInput == 4){
        System.out.println("\nKNIGHT:");
        printCharacterInfo(new Knight("Knight"), false);
        Thread.sleep(1000);
        anythingToContinue();
        System.out.println("\nARCHER:");
        printCharacterInfo(new Archer("Archer"), false);
        Thread.sleep(1000);
        anythingToContinue();
        System.out.println("\nWIZARD:");
        printCharacterInfo(new Wizard("Wizard"), false);
        Thread.sleep(1000);
        anythingToContinue();
        System.out.println("\nHEALER:");
        Thread.sleep(1000);
        anythingToContinue();
        continue;
      }
      
      if(classInput == 1){
        System.out.println("Selected class: Knight");
      } else if(classInput == 2){
        System.out.println("Selected class: Archer");
      } else if(classInput == 3){
        System.out.println("Selected class: Wizard");
      }
      System.out.print("Provide a name for your character: >>> ");
      String name = s.nextLine();
      
      if(classInput == 1){
        playerTeam.addCharacter(new Knight(name));
      } else if(classInput == 2){
        playerTeam.addCharacter(new Archer(name));
      } else if(classInput == 3){
        playerTeam.addCharacter(new Wizard(name));
      }
      System.out.println(playerTeam);
      Thread.sleep(1000);
      if(dayNum == 1 && playerTeam.getPlayerTeam().size() == 2){
        System.out.println("Congratulations! You have trained your first fighters.");
        Thread.sleep(1000);
        anythingToContinue();
      }
      isFirstLoop = false;
    }
  }
  
  private void initializeEnemy(){
    enemyTeam.clearEnemyTeam();
    // enemyBattleCapacity can be between playerBattleCapacity - 1 and playerBattleCapacity + 1
    enemyBattleCapacity = playerBattleCapacity;
    enemyBattleCapacity += (int)(Math.random() * 3) - 1;
    // There will be one additional enemy added approximately every three days
    // dayNum is an int so it will automatically round down
	enemyBattleCapacity += dayNum/3;
    // On the first day, it is guaranteed that there will only be one enemy to act as a tutorial
    if(dayNum == 1){
      enemyBattleCapacity = 1;
    }
    for(int i = 0; i < enemyBattleCapacity; i++){
      String chosenEnemyType = earlyGameEnemies[(int)(Math.random() * earlyGameEnemies.length)];
      String chosenEnemyBehavior = earlyGameEnemyBehaviors[(int)(Math.random() * earlyGameEnemyBehaviors.length)];
      if(chosenEnemyType.equals("Goblin")){
        enemyTeam.addEnemy(new Goblin(nameEnemy("Goblin"), chosenEnemyBehavior), playerTeam.getHighestLevel());
      } else if(chosenEnemyType.equals("DartGoblin")){
        enemyTeam.addEnemy(new DartGoblin(nameEnemy("Dart Goblin"), chosenEnemyBehavior), playerTeam.getHighestLevel());
      }
    }
  }
  
  private String nameEnemy(String type){
    return adjectives.get((int)(Math.random() * adjectives.size())) + " " + type;
  }
  
  // xp and coin rewards
  private void handleEnemyRewards() throws InterruptedException{
    for(EnemyCharacter e : enemyTeam.getEnemyTeam()){
      // Drop an enemy's coin reward
      System.out.println(e.getName() + " dropped " + e.getCoinReward() + " coins!");
      playerTeam.increaseCoinBalance(e.getCoinReward());
      Thread.sleep(1000);
      System.out.println("Balance: " + playerTeam.getCoinBalance() + "g");
      Thread.sleep(2000);
      // Drop an enemy's xp reward and apply it to all player characters
      for(PlayerCharacter p : playerTeam.getPlayerTeam()){
        p.increaseXP(e.getXPReward());
      }
      anythingToContinue();
    }
  }
  
  private void handleShop() throws InterruptedException{
    String message = "You have encountered a shop! Would you like to visit?\n";
    message += "1: YES\n";
    message += "2: NO";
    int input = GameManager.obtainInput(message, 1, 2, false);
    
    if(input==1){
      System.out.println("You have decided to enter the shop.");
      Thread.sleep(1000);
      Shop shop = new Shop();
      shop.handleShopLoop(playerTeam);
    } else{
      System.out.println("You have decided to avoid the shop.");
      Thread.sleep(1000);
    }
  }
  
  private void runBattleLoop() throws InterruptedException{
    playerActionPoints = playerBattleCapacity;
    enemyActionPoints = enemyBattleCapacity;
    System.out.println(playerTeam);
    Thread.sleep(1500);
    System.out.println("VS\n");
    Thread.sleep(500);
    System.out.println(enemyTeam);
    Thread.sleep(1500);
    anythingToContinue();

    turnNum = 0;
    String turnPriority;
    if(playerTeam.getTotalSpeed() >= enemyTeam.getTotalSpeed()){
      System.out.println("The player's team has the higher combined speed and will go first!");
      turnPriority = "Player";
    } else{
      System.out.println("The enemy's team has the higher combined speed and will go first!");
      turnPriority = "Enemy";
    }
    Thread.sleep(1000);
    if(dayNum == 1 && !skipTutorial){
      System.out.println("\nBattle tutorial:");
      Thread.sleep(1000);
      System.out.println("You and the enemy will take alternating turns.");
      Thread.sleep(1000);
      System.out.println("During your turn, you may perform several actions between your characters.");
      Thread.sleep(1000);
      System.out.println("Each character must choose from several possible actions: basic ability, special ability, item, or defense.");
      Thread.sleep(2000);
      System.out.println("Basic abilities are standard abilities that may be performed as often as you want.");
      Thread.sleep(2000);
      System.out.println("Special abilities can only be used once every few turns, so you must use them wisely.");
      Thread.sleep(2000);
      System.out.println("You may also choose to use an item or increase a character's defense for that turn.");
      Thread.sleep(2000);
      System.out.println("Enter HELP at any time if you want to view a character's stats and abilities.");
      anythingToContinue();
    }
    
    while(!hasPlayerWon() && !hasEnemyWon()){
      turnNum++;
      System.out.println("\nTurn " + turnNum + ":");
      if(turnPriority.equals("Player")){
        StatusEffect.handleStatusTurn();
        beforePlayerTurn();
        handlePlayerTurn();
        if(hasPlayerWon() || hasEnemyWon()){
          break;
        }
        StatusEffect.handleStatusTurn();
        beforeEnemyTurn();
        handleEnemyTurn();
      } else if(turnPriority.equals("Enemy")){
        StatusEffect.handleStatusTurn();
        beforeEnemyTurn();
        handleEnemyTurn();
        if(hasPlayerWon() || hasEnemyWon()){
          break;
        }
        StatusEffect.handleStatusTurn();
        beforePlayerTurn();
        handlePlayerTurn();
      }
      
    }
  }
  
  private void beforePlayerTurn() throws InterruptedException {
    if(turnNum > 1){
      playerTeam.decreasePlayerCooldowns();
    }
    playerTeam.resetPlayerDefense();
    currentPlayer = "Player";
    Thread.sleep(1000);
    System.out.println("\nIt is your turn!\n");
    Thread.sleep(1000);
    playerActionPoints = playerTeam.getNumAlive();
    if(playerActionPoints == 1){
      System.out.println("You may perform 1 action during your turn.\n");
    } else{
      System.out.println("You may perform " + playerActionPoints + " actions during your turn.\n");
    }
  }
  
  private void beforeEnemyTurn() throws InterruptedException {
    if(turnNum > 1){
      enemyTeam.decreaseEnemyCooldowns();
    }
    currentPlayer = "Enemy";
    System.out.println("\nIt is the enemy's turn!\n");
    Thread.sleep(1000);
  }
  
  private void handleEnemyTurn() throws InterruptedException {
    for(EnemyCharacter e : enemyTeam.getEnemyTeam()){
      if(!e.getIsDead()){
        e.takeTurn(playerTeam, enemyTeam);
      	Thread.sleep(1000);
        anythingToContinue();
      }
    }
  }
  
  private void handlePlayerTurn() throws InterruptedException {
      actionPointsLeft = playerActionPoints;
      while(actionPointsLeft > 0){
        Thread.sleep(1000);
        if(actionPointsLeft < playerActionPoints){
          if(actionPointsLeft == 1){
            System.out.println("\nYou have 1 action left this turn.");
          } else{
            System.out.println("\nYou have " + actionPointsLeft + " actions left this turn.");
          }
        }
        Thread.sleep(1000);
        // Obtain a character for the user to select
        String message = "\nSelect a character to perform an action with: \n" + playerTeam.getPlayerTeamNumFormat();
        int selectedCharacterIndex;
        PlayerCharacter selectedCharacter;
        while(true){
          selectedCharacterIndex = GameManager.obtainInput(message, 1, playerBattleCapacity, true);
          selectedCharacter = playerTeam.getCharacterAt(selectedCharacterIndex);
          if(selectedCharacter.getIsDead()){
            System.out.println("Invalid input. This character is dead!");
          } else{
            break;
          }
        }
        
        // currentActionPoints is initialized to avoid confusion when characters take multiple actions (speed advantage)
        currentActionPoints = actionPointsLeft;
        boolean canGainAnotherAction;
        while(currentActionPoints >= actionPointsLeft){
          canGainAnotherAction = true;
          System.out.println("\nSelected character: " + selectedCharacter.getName());
          // Obtain an action from the chosen character
          message = "What would you like to do?\n";
          if(StatusEffect.hasStatusEffect(selectedCharacter, "Taunt")){
            message += "1: Basic ability [UNAVAILABLE]\n";
            message += "2: Special ability [UNAVAILABLE]\n";
          } else{
            message += "1: Basic ability\n";
            message += "2: Special ability\n";
          }
          message += "3: Use item\n";
          message += "4: Defend\n";
          message += "5: HELP";
          int selectedAction = GameManager.obtainInput(message, 1, 5, false);

          // Check if a status effect prevents the player from using an ability
          if(selectedAction == 1 || selectedAction == 2){
            if(StatusEffect.hasStatusEffect(selectedCharacter, "Taunt")){
              System.out.println("You cannot use an ability because you are taunted!");
              anythingToContinue();
              canGainAnotherAction = false;
              continue;
            }
          }

          if(selectedAction == 1){

            // Basic ability
            int limit = findBasicAbilityLimit(selectedCharacter, false);
            int basicAbilityIndex = GameManager.obtainInputWithCancel("Select a basic ability:\n" + playerTeam.getBasicAbilityNamesNumFormat(selectedCharacter, false), 1, limit, true);
            if(basicAbilityIndex != -1){
              conductOffensiveAttack(selectedCharacter, basicAbilityIndex, "basic");
              anythingToContinue();
            } else{
              currentActionPoints++;
              canGainAnotherAction = false;
            }

          } else if(selectedAction == 2){
            // Special ability
			      int limit = findSpecialAbilityLimit(selectedCharacter, false);
            if(limit != -1){
              int specialAbilityIndex = GameManager.obtainInputWithCancel("Select a special ability:\n" + playerTeam.getSpecialAbilityNamesNumFormat(selectedCharacter, false), 1, limit, true);
              if(specialAbilityIndex != -1){
                if(selectedCharacter.getCurrentSpecialAbilityCooldowns().get(specialAbilityIndex) > 0){
                  System.out.println("Invalid ability. Cooldown must be 0!");
                  currentActionPoints++;
                  canGainAnotherAction = false;
                  anythingToContinue();
                } else{
                  conductOffensiveAttack(selectedCharacter, specialAbilityIndex, "special");
                  anythingToContinue();
                }
              } else{
                currentActionPoints++;
                canGainAnotherAction = false;
              }
            }

          } else if(selectedAction == 3){

            // Use item
            
            if(playerTeam.getPlayerInventory().isEmpty()){
              System.out.println("Your inventory is empty.");
              Thread.sleep(1000);
              anythingToContinue();
              currentActionPoints++;
              canGainAnotherAction = false;
            } else{
              int limit = playerTeam.getPlayerInventory().getInventory().size();
              int itemIndex = GameManager.obtainInputWithCancel("Select an item to use: \n" + playerTeam.getPlayerInventory().getInventoryNumFormat(), 1, limit, true);
              if(itemIndex != -1){
                playerTeam.useItem(itemIndex, selectedCharacter, enemyTeam);
              	anythingToContinue();
              } else{
                currentActionPoints++;
                canGainAnotherAction = false;
              }
            }

          } else if(selectedAction == 4){

            // Defend
            selectedCharacter.setIsDefending(true);
            anythingToContinue();

          } else if(selectedAction == 5){
            // HELP
            printCharacterInfo(selectedCharacter, false);
            anythingToContinue();
            currentActionPoints++;
            canGainAnotherAction = false;
          }
          
          // Chance for the character to earn another action based on their speed
          // Capped at 50% because any higher could cause effectively infinite actions from one character
          if((Math.random() * 100) < Math.min(selectedCharacter.getSpeed()/2, 50) && canGainAnotherAction){
            Thread.sleep(1000);
            System.out.println(selectedCharacter.getName() + " has earned another action due to their speed!");
            Thread.sleep(500);
            System.out.println("(" + Math.min(selectedCharacter.getSpeed()/2, 50) + "% chance)");
            Thread.sleep(1000);
            currentActionPoints++;
          }
	      
          currentActionPoints--;
          if(hasPlayerWon() || hasEnemyWon()){
            break;
          }
        }
        actionPointsLeft--;
        
        if(hasPlayerWon() || hasEnemyWon()){
          break;
        }
      }
  }
  
  // Prompt the user to select a basic ability from a list of possible basic abilities
  // Return the index returned by the user, or -1 if none was selected
  private int findBasicAbilityLimit(PlayerCharacter selectedCharacter, boolean includeLockedAbilities){
    int limit;
    if(includeLockedAbilities){
      limit = selectedCharacter.getBasicAbilityNames().size();
    } else{
      limit = selectedCharacter.getHighestIndexBasic() + 1;
    }
    return limit;
  }
  
  // Prompt the user to select a specific ability from a list of possible specific abilities
  // Return the index returned by the user, or -1 if none was selected
  private int findSpecialAbilityLimit(PlayerCharacter selectedCharacter, boolean includeLockedAbilities){
    int limit;
    if(includeLockedAbilities){
      limit = selectedCharacter.getSpecialAbilityNames().size();
    } else{
      limit = selectedCharacter.getHighestIndexSpecial() + 1;
    }
    if(limit <= 0){
      System.out.println("You have no available special abilities.");
      currentActionPoints++;
      return -1;
    }
    return limit;
 }
  
  // Obtain the enemy/enemies the user chooses to target and immediately apply their ability to them
  private void conductOffensiveAttack(BasicCharacter selectedCharacter, int index, String abilityType) throws InterruptedException{
    if(abilityType.equals("basic")){
      // Detect if the user entered a real basic ability or "Cancel"
      if(index < selectedCharacter.getBasicAbilityNames().size()){  
        // Basic abilities target a different number of enemies depending on the behavior of the Character.
        // Obtain the number of enemies the chosen basic ability targets
        if(selectedCharacter.getBasicAbilityEnemyCount(index) > 0){
          System.out.println(selectedCharacter.getBasicAbilityNames().get(index) + " may target up to " + selectedCharacter.getBasicAbilityEnemyCount(index) + " enemies.");
          if(selectedCharacter.getBasicAbilityEnemyCount(index) <= enemyTeam.getNumAlive()){
            // There are enough enemies to fully utilize the basic ability
            // The user must choose which enemies to target.
            // Prompt the user for their enemy choices.
            ArrayList<Integer> selectedEnemyIndices = new ArrayList<Integer>();
            for(int i = 0; i < selectedCharacter.getBasicAbilityEnemyCount(index); i++){
              String message = "Select an enemy to attack:\n";
              message += enemyTeam.getEnemyTeamNumFormat(selectedEnemyIndices);
              int selectedEnemyIndex;
              while(true){
                selectedEnemyIndex = GameManager.obtainInput(message, 1, enemyTeam.getEnemyTeam().size(), true);
                if(enemyTeam.getEnemyTeam().get(selectedEnemyIndex).getIsDead()){
                  System.out.println("Invalid input. This character is dead!");
                } else{
                  break;
                }
              }
              selectedEnemyIndices.add(selectedEnemyIndex);
            }

            for(int selectedEnemyIndex : selectedEnemyIndices){
              selectedCharacter.basicAbility(index, enemyTeam.getCharacterAt(selectedEnemyIndex), playerTeam, enemyTeam);
            }
          } else{
            // There are NOT enough enemies to fully utilize the basic ability.
            // The user would have no choice but to use the basic ability on the remaining enemies.
            // Do NOT prompt the user to select enemies, since they have no choice.
            if(selectedCharacter.getBasicAbilityEnemyCount(index) == 999){
              // 999 is read as "all" enemies regardless of enemy team size. Using this arbitrary number
              // is useful since in real gameplay, there will not be close to 999 enemies.
              System.out.println(selectedCharacter.getBasicAbilityNames().get(index) + " will target all enemies!");
            } else{
              System.out.println("Since there are only " + enemyTeam.getNumAlive() + " alive enemies, " + selectedCharacter.getBasicAbilityNames().get(index) + " will automatically target all of them.");
            }
            Thread.sleep(1500);
            // Apply the basic ability once to all enemies
            for(EnemyCharacter e : enemyTeam.getEnemyTeam()){
              if(!e.getIsDead()){
                selectedCharacter.basicAbility(index, e, playerTeam, enemyTeam);
              }
            }
          }
        } else{
          selectedCharacter.basicAbility(index, enemyTeam.getEnemyTeam().get(0), playerTeam, enemyTeam);
        }
               
      } else{
        // If "Cancel" was selected, refund the user's action and return to the first screen
        currentActionPoints++;
      }
    } else if(abilityType.equals("special")){
      // Detect if the user entered a real special ability or "Cancel"
      if(index < selectedCharacter.getSpecialAbilityNames().size()){  
        // Special abilities target a different number of enemies depending on the behavior of the Character.
        // Obtain the number of enemies the chosen special ability targets
        if(selectedCharacter.getSpecialAbilityEnemyCount(index) != 999){
          System.out.println(selectedCharacter.getSpecialAbilityNames().get(index) + " may target up to " + selectedCharacter.getSpecialAbilityEnemyCount(index) + " enemies.");
        }
        if(selectedCharacter.getSpecialAbilityEnemyCount(index) <= enemyTeam.getNumAlive()){
          // There are enough enemies to fully utilize the special ability
          // The user must choose which enemies to target.
          // Prompt the user for their enemy choices.
          ArrayList<Integer> selectedEnemyIndices = new ArrayList<Integer>();
          for(int i = 0; i < selectedCharacter.getSpecialAbilityEnemyCount(index); i++){
            String message = "Select an enemy to attack:\n";
          	message += enemyTeam.getEnemyTeamNumFormat(selectedEnemyIndices);
            int selectedEnemyIndex;
            while(true){
              selectedEnemyIndex = GameManager.obtainInput(message, 1, enemyTeam.getEnemyTeam().size(), true);
              if(enemyTeam.getEnemyTeam().get(selectedEnemyIndex).getIsDead()){
                System.out.println("Invalid input. This character is dead!");
              } else{
                break;
              }
            }
            selectedEnemyIndices.add(selectedEnemyIndex);
          }

          for(int selectedEnemyIndex : selectedEnemyIndices){
            selectedCharacter.specialAbility(index, enemyTeam.getCharacterAt(selectedEnemyIndex), playerTeam, enemyTeam);
          }
          selectedCharacter.resetSpecialAbilityCooldowns();
        } else{
          // There are NOT enough enemies to fully utilize the special ability.
          // The user would have no choice but to use the special ability on the remaining enemies.
          // Do NOT prompt the user to select enemies, since they have no choice.
          if(selectedCharacter.getSpecialAbilityEnemyCount(index) == 999){
            // 999 is read as "all" enemies regardless of enemy team size. Using this arbitrary number
            // is useful since in real gameplay, there will not be close to 999 enemies.
            System.out.println(selectedCharacter.getSpecialAbilityNames().get(index) + " will target all enemies!");
          } else{
            System.out.println("Since there are only " + enemyTeam.getNumAlive() + " alive enemies, " + selectedCharacter.getSpecialAbilityNames().get(index) + " will automatically target all of them.");
          }
          Thread.sleep(1500);
          // Apply the special attack once to all enemies
          for(EnemyCharacter e : enemyTeam.getEnemyTeam()){
            if(!e.getIsDead()){
            	selectedCharacter.specialAbility(index, e, playerTeam, enemyTeam);
            }
          }
          selectedCharacter.resetSpecialAbilityCooldowns();
        }         
      } else{
        // If "Cancel" was selected, refund the user's action and return to the first screen
        currentActionPoints++;
      }
    }
  }
  
  // Print a character's core stats, descriptions, and all applicable attacks
  // If includeLockedAttacks is true, this method will display all of a character's abilities regardless of its level.
  // If false, it will only include attacks that are unlocked at or below the character's current level.
  private void printCharacterInfo(PlayerCharacter selectedCharacter, boolean includeLockedAttacks) throws InterruptedException{
    int listLimit;
    if(includeLockedAttacks){
      listLimit = selectedCharacter.getBasicAbilityNames().size();
    } else{
      listLimit = selectedCharacter.getHighestIndexBasic() + 1;
    }
    System.out.println(selectedCharacter);
    System.out.println(selectedCharacter.getDescription());
    Thread.sleep(1000);
    System.out.println("\nBasic abilities: ");
    for(int i = 0; i < listLimit; i++){
      System.out.print(selectedCharacter.getBasicAbilityNames().get(i) + ": ");
      System.out.println(selectedCharacter.getBasicAbilityDescriptions().get(i));
      System.out.println("");
      Thread.sleep(1000);
    }
    
    // Special message if the character does not have all the basic abilities
    if(listLimit < selectedCharacter.getBasicAbilityNames().size()){
      if(listLimit <= 0){
        System.out.println("None");
      }
      System.out.println("Unlock more basic abilities by leveling up!");
      Thread.sleep(1000);
    }
    
    if(includeLockedAttacks){
      listLimit = selectedCharacter.getSpecialAbilityNames().size();
    } else{
      listLimit = selectedCharacter.getHighestIndexSpecial() + 1;
    }
    Thread.sleep(1000);
    System.out.println("\nSpecial abilities: ");
    for(int i = 0; i < listLimit; i++){
      System.out.print(selectedCharacter.getSpecialAbilityNames().get(i) + ": ");
      System.out.println(selectedCharacter.getSpecialAbilityDescriptions().get(i));
      System.out.println("Cooldown: " + selectedCharacter.getSpecialAbilityCooldowns().get(i) + " turns");
      System.out.println("");
      Thread.sleep(1000);
    }
    
    // Special message if the character does not have all the special abilities
    if(listLimit < selectedCharacter.getSpecialAbilityNames().size()){
      if(listLimit <= 0){
        System.out.println("None");
      }
      System.out.println("Unlock more special abilities by leveling up!");
      Thread.sleep(1000);
    }
    
    System.out.println("");
  }
  
  // Min and max are inclusive
  // Check if a string input entered by the user is a number within an acceptable range
  private static boolean isNumInputAcceptable(String input, int min, int max){
    try{
      Integer.parseInt(input);
    } catch(NumberFormatException e){
      return false;
    }
    int num = Integer.parseInt(input);
	return(num >= min && num <= max);
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
  
  // Used to obtain an input for general messages
  public static int obtainInput(String message, int min, int max, boolean applyIndexOffset){
    String input = "";
    //message += "\n";
    while(true){
      System.out.println(message);
      System.out.print(">>> ");
      input = inputScanner.nextLine();
      if(!isNumInputAcceptable(input, min, max)){
        System.out.println("Invalid input.");
      } else{
        break;
      }
    }
    if(applyIndexOffset){
      return Integer.parseInt(input) - 1;
    } else{
      return Integer.parseInt(input);
    }
  }
  
  public static int obtainInputWithCancel(String message, int min, int max, boolean applyIndexOffset){
    String input = "";
    //message += "\n";
    while(true){
      System.out.print(message);
      System.out.println(max + 1 + ": Cancel");
      System.out.print(">>> ");
      input = inputScanner.nextLine();
      if(!isNumInputAcceptable(input, min, max+1)){
        System.out.println("Invalid input.");
      } else{
        break;
      }
    }
    
    int output = Integer.parseInt(input) - 1;
    if(output < max){
      if(applyIndexOffset){
        return output;
      } else{
        return output + 1;
      }
    }
    return -1;
  }
  
  public static void anythingToContinue(){
    System.out.print("Press ENTER to continue. >>> ");
    inputScanner.nextLine();
  }

  public static void clearScreen(){
    for(int i = 0; i < 100; i++){
      System.out.println();
    }
  }
  
  public String toString(){
    return playerTeam + "\n" + enemyTeam;
  }
}
