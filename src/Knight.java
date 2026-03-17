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
    addToArrayList(getSpecialAbilityUnlockLevels(), new Integer[]{0, 3});
    addToArrayList(getSpecialAbilityEnemyCounts(), new Integer[]{1, 1});
    addToArrayList(getSpecialAbilityCooldowns(), new Integer[]{2, 2});
    addToArrayList(getCurrentSpecialAbilityCooldowns(), new Integer[]{2, 2});
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
      handleEnemyDefense(target, getAttackStrength());
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
      System.out.println(getName() + " cautiously swung their sword at " + target.getName() + " for " + (getAttackStrength() - 5) + " HP!");
      handleEnemyDefense(target, getAttackStrength() - 5);
      System.out.println(target.getSimpleOutput());
    }
  }
  
  // Knight has two special attacks to choose from
  public void specialAbility(int specialAbilityIndex, BasicCharacter target, PlayerTeam playerTeam, EnemyTeam enemyTeam) throws InterruptedException{
    if(specialAbilityIndex == 0){
      // Last Push
      if(target.getCurrentHP() / target.getMaxHP() < 0.5){
        System.out.println(getName() + " had a last push against " + target.getName() + " for " + (getAttackStrength() + 10) * 1.5 + " HP!");
        handleEnemyDefense(target, (getAttackStrength() + 10) * 1.5);
      } else{
        System.out.println(getName() + " pushed against " + target.getName() + " for " + (getAttackStrength() + 10) + " HP!");
        handleEnemyDefense(target, getAttackStrength() + 10);
      }
      
    } else if(specialAbilityIndex == 1){
      // Rage Strike
      System.out.println(getName() + " furiously attacked " + target.getName() + " for " + (getAttackStrength() * 2) + " HP!");
      handleEnemyDefense(target, (getAttackStrength() * 2));
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
}
