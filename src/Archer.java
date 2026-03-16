package src;
class Archer extends PlayerCharacter {
  public Archer(String name){
    super(name, 90.0, 20.0, 10.0, 30.0);
    // Set the names, descriptions, and cooldowns of all the Knight's abilities.
    setDescription("A quick fighter who can attack multiple enemies and control the battlefield.");
    addToArrayList(getBasicAbilityNames(), new String[]{"Softening Arrow", "Double Shot"});
    addToArrayList(getBasicAbilityDescriptions(), new String[]{"A single ranged attack that has a 25% chance to reduce the enemy's attack strength for 2 turns.",
                                                             "A slightly weaker attack that targets two enemies."});
    addToArrayList(getBasicAbilityTypes(), new String[]{"Offensive", "Offensive"});
    addToArrayList(getBasicAbilityUnlockLevels(), new Integer[]{0, 3});
    addToArrayList(getBasicAbilityEnemyCounts(), new Integer[]{1, 2});
    addToArrayList(getSpecialAbilityNames(), new String[]{"Volley", "Explosive Arrow"});
    addToArrayList(getSpecialAbilityDescriptions(), new String[]{"Fires an arrow at every enemy, dealing moderate damage. Each enemy has a 10% chance of being burned for 2 turns.",
                                                               "Deals high single target damage to one unit, as well as reduced splash damage on other enemies."});
    addToArrayList(getSpecialAbilityTypes(), new String[]{"Offensive", "Offensive"});
    addToArrayList(getSpecialAbilityUnlockLevels(), new Integer[]{0, 3});
    addToArrayList(getSpecialAbilityEnemyCounts(), new Integer[]{999, 1});
    addToArrayList(getSpecialAbilityCooldowns(), new Integer[]{2, 4});
    addToArrayList(getCurrentSpecialAbilityCooldowns(), new Integer[]{2, 4});
  }
  
  // Overrided getType method
  public String getType(){
    return "Archer";
  }
  
  // Overrided battle methods
  public void basicAbility(int basicAbilityIndex, BasicCharacter target, PlayerTeam playerTeam, EnemyTeam enemyTeam) throws InterruptedException{
    if(basicAbilityIndex == 0){
      System.out.println(getName() + " fired a softening arrow at " + target.getName() + " for " + getAttackStrength() + " HP!");
      boolean wasEnemyHit = handleEnemyDefense(target, getAttackStrength());
      if((int)(Math.random() * 100) < 25 && wasEnemyHit){
        StatusEffect.addStatusEffect(target, "Soft", 2);
      }
      System.out.println(target.getSimpleOutput());
    } else if(basicAbilityIndex == 1){
      System.out.println(getName() + " cautiously swung his sword at " + target.getName() + " for " + (getAttackStrength() - 10) + " HP!");
      handleEnemyDefense(target, getAttackStrength() - 10);
      System.out.println(target.getSimpleOutput());
    }
  }
  
  // Knight has two special attacks to choose from
  public void specialAbility(int specialAbilityIndex, BasicCharacter target, PlayerTeam playerTeam, EnemyTeam enemyTeam) throws InterruptedException{
    if(specialAbilityIndex == 0){
      System.out.println(getName() + " fired an arrow at " + target.getName() + " for " + getAttackStrength() + " HP!");
      boolean wasEnemyHit = handleEnemyDefense(target, getAttackStrength());
      if((int)(Math.random() * 100) < 10 && wasEnemyHit){
        StatusEffect.addStatusEffect(target, "Burn", 2);
      }
    } else if(specialAbilityIndex == 1){
      System.out.println(getName() + " furiously attacked " + target.getName() + " for " + (getAttackStrength() * 2) + " HP!");
      handleEnemyDefense(target, (getAttackStrength() * 2));
      System.out.println(getName() + "'s rage made him lose " + (getAttackStrength() * 0.5) + " HP!");
      changeCurrentHP(-(getAttackStrength() * 0.5));
      System.out.println(getSimpleOutput());
    }
    System.out.println(target.getSimpleOutput());
  }
  
  // Defense function which is called when an enemy targets the Knight
  public void defend(BasicCharacter target, double actualDamage){
    if(actualDamage == 0){
      System.out.println(getName() + " evaded " + target.getName() + "'s attack!");
    }else if(getIsDefending()){
      System.out.println(getName() + " partially dodged " + target.getName() + "'s attack for " + getDefenseStrength() * 2 + " HP!");
    } else{
      System.out.println(getName() + " lightly dodged " + target.getName() + "'s attack for " + getDefenseStrength() + " HP!");
    }
  }
}
