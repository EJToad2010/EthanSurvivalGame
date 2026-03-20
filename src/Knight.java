package src;
// High health and ability, low defense and healing
class Knight extends PlayerCharacter {
  public Knight(String name){
    super(name, 100.0, 30.0, 15.0, 5.0);
    // Set the names, descriptions, and cooldowns of all the Knight's abilities.
    setDescription("An offensive tank who can withstand the front lines of battle, at the cost of speed.");
    addToArrayList(getBasicAbilityNames(), new String[]{"Sword Swing", "Cautious Attack"});
    addToArrayList(getBasicAbilityDescriptions(), new String[]{"A melee attack. The enemy counterattacks for a small amount of damage.",
                                                             "Deals less damage than Sword Swing, but the enemy cannot counterattack."});
    addToArrayList(getBasicAbilityTypes(), new String[]{"Offensive", "Offensive"});
    addToArrayList(getBasicAbilityUnlockLevels(), new Integer[]{0, 3});
    addToArrayList(getBasicAbilityEnemyCounts(), new Integer[]{1, 1});
    addToArrayList(getSpecialAbilityNames(), new String[]{"Last Push", "Rage Strike"});
    addToArrayList(getSpecialAbilityDescriptions(), new String[]{"Deals +10 damage from Sword Swing. Deals an additional 50% damage if the enemy is below 50% HP.",
                                                               "Attack power is doubled from Sword Swing, but lose HP equal to 25% of your doubled attack power."});
    addToArrayList(getSpecialAbilityTypes(), new String[]{"Offensive", "Offensive"});
    addToArrayList(getSpecialAbilityUnlockLevels(), new Integer[]{0, 4});
    addToArrayList(getSpecialAbilityEnemyCounts(), new Integer[]{1, 1});
    addToArrayList(getSpecialAbilityCooldowns(), new Integer[]{2, 3});
    addToArrayList(getCurrentSpecialAbilityCooldowns(), new Integer[]{2, 3});
  }
  
  // Overrided getType method
  public String getType(){
    return "Knight";
  }
  
  // Overrided battle methods
  public void basicAbility(int basicAbilityIndex, BasicCharacter target, PlayerTeam playerTeam, EnemyTeam enemyTeam) throws InterruptedException{
    if(basicAbilityIndex == 0){
      // Sword Swing
      System.out.println(getName() + " swung their sword at " + target.getName() + " for " + getAttackStrength() + " HP!");
      handleEnemyDefense(target, getAttackStrength(), playerTeam, enemyTeam);
      System.out.println(target.getSimpleOutput());
      
      if(!target.getIsDead()){
        int counterAttackDamage = (int)(Math.random() * 3) + 5;
        Thread.sleep(1000);
        System.out.println(target.getName() + " counterattacked for " + (double) counterAttackDamage + " HP!");
        changeCurrentHP(-(double) counterAttackDamage);
        Thread.sleep(1000);
        System.out.println(getSimpleOutput());
      }
    } else if(basicAbilityIndex == 1){
      // Cautious Attack
      System.out.println(getName() + " cautiously swung their sword at " + target.getName() + " for " + (getAttackStrength() - 8) + " HP!");
      handleEnemyDefense(target, getAttackStrength() - 8, playerTeam, enemyTeam);
      System.out.println(target.getSimpleOutput());
    }
  }
  
  // Knight has two special attacks to choose from
  public void specialAbility(int specialAbilityIndex, BasicCharacter target, PlayerTeam playerTeam, EnemyTeam enemyTeam) throws InterruptedException{
    if(specialAbilityIndex == 0){
      // Last Push
      if(target.getCurrentHP() / target.getMaxHP() < 0.5){
        System.out.println(getName() + " had a last push against " + target.getName() + " for " + (getAttackStrength() + 10) * 1.5 + " HP!");
        handleEnemyDefense(target, (getAttackStrength() + 10) * 1.5, playerTeam, enemyTeam);
      } else{
        System.out.println(getName() + " pushed against " + target.getName() + " for " + (getAttackStrength() + 10) + " HP!");
        handleEnemyDefense(target, getAttackStrength() + 10, playerTeam, enemyTeam);
      }
      
    } else if(specialAbilityIndex == 1){
      // Rage Strike
      System.out.println(getName() + " furiously attacked " + target.getName() + " for " + (getAttackStrength() * 2) + " HP!");
      handleEnemyDefense(target, (getAttackStrength() * 2), playerTeam, enemyTeam);
      System.out.println(getName() + "'s rage made them lose " + (getAttackStrength() * 0.5) + " HP!");
      changeCurrentHP(-(getAttackStrength() * 0.5));
      System.out.println(getSimpleOutput());
    }
    System.out.println(target.getSimpleOutput());
  }
  
  // Defense function which is called when an enemy targets the Knight
  public void defend(BasicCharacter target, double actualDamage){
    if(actualDamage == 0){
      System.out.println(getName() + " fully countered " + target.getName() + " with their shield!");
    }else if(getIsDefending()){
      System.out.println(getName() + " partially blocked " + target.getName() + "'s attack with their shield for " + getDefenseStrength() * 2 + " HP!");
    } else{
      System.out.println(getName() + " lightly blocked " + target.getName() + "'s attack with their shield for " + getDefenseStrength() + " HP!");
    }
  }

  // The Trial of Strength minigame that appears in the Tournament
  // Returns then amount of damage that the Knight did on the target
  public double chargeAttackMinigame() throws InterruptedException{
    // Hidden variable that decreases every time the attack is charged.
    // If at zero or less, the attack will miss.
    int stamina = 100;
    boolean isFirstLoop = true;
    while(true){
      GameManager.clearScreen();
      if(!isFirstLoop){
        System.out.println("You have decided to charge your attack further.");
      }
      Thread.sleep(500);
      if(stamina > 30){
        System.out.println(getName() + " looks at the punching bag with excited rage.");
      } else if(stamina > 10){
        System.out.println(getName() + " looks at the puncing bag with tired excitement.");
      } else if(stamina > 0){
        System.out.println(getName() + " looks at the punching bag with weary eyes.");
      } else{
        System.out.println(getName() + " collapses with pure exhaustion.");
        return 0.0;
      }
      String message = "What would you like to do?\n1: Charge Attack\n2: Release Attack";
      int decisionInput = GameManager.obtainInput(message, 1, 2, false);
      if(decisionInput == 1){
        // Decrease stamina by a random amount
        System.out.println(getName() + " charges their attack.");
        stamina -= (int)(Math.random() * 10) + 5;
      } else{
        // Randomly decrease stamina so that the printed messages can be misleading
        System.out.println(getName() + " releases their attack!");
        stamina -= (int)(Math.random() * 15) + 5;
        break;
      }
      isFirstLoop = false;
    }
    double charge = (100 - stamina) / 100.0;
    // Exponential multiplier of power
    double multiplier = 1 + (charge * charge * 3.5);
    return getAttackStrength() * multiplier;
  }
}
