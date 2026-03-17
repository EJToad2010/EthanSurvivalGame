package src;
class Archer extends PlayerCharacter {
  public Archer(String name){
    super(name, 90.0, 20.0, 10.0, 30.0);
    // Set the names, descriptions, and cooldowns of all the Archer's abilities.
    setDescription("A quick fighter who can attack multiple enemies and control the battlefield.");
    addToArrayList(getBasicAbilityNames(), new String[]{"Softening Arrow", "Double Shot"});
    addToArrayList(getBasicAbilityDescriptions(), new String[]{"A single ranged attack that has a 25% chance to reduce the enemy's attack strength for 2 turns.",
                                                             "A slightly weaker attack that targets two enemies."});
    addToArrayList(getBasicAbilityTypes(), new String[]{"Offensive", "Offensive"});
    addToArrayList(getBasicAbilityUnlockLevels(), new Integer[]{0, 3});
    addToArrayList(getBasicAbilityEnemyCounts(), new Integer[]{1, 2});
    addToArrayList(getSpecialAbilityNames(), new String[]{"Volley", "Crippling Arrow"});
    addToArrayList(getSpecialAbilityDescriptions(), new String[]{"Fires an arrow at every enemy, dealing moderate damage. Each enemy has a 10% chance of being burned for 1 turn.",
                                                               "Deals moderate damage to a single target. The target has a 50% chance of being slowed for 2 turns."});
    addToArrayList(getSpecialAbilityTypes(), new String[]{"Offensive", "Offensive"});
    addToArrayList(getSpecialAbilityUnlockLevels(), new Integer[]{0, 3});
    addToArrayList(getSpecialAbilityEnemyCounts(), new Integer[]{999, 1});
    addToArrayList(getSpecialAbilityCooldowns(), new Integer[]{2, 2});
    addToArrayList(getCurrentSpecialAbilityCooldowns(), new Integer[]{2, 2});
  }
  
  // Overrided getType method
  public String getType(){
    return "Archer";
  }
  
  // Overrided battle methods
  public void basicAbility(int basicAbilityIndex, BasicCharacter target, PlayerTeam playerTeam, EnemyTeam enemyTeam) throws InterruptedException{
    if(basicAbilityIndex == 0){
      // Softening Arrow
      System.out.println(getName() + " fired a softening arrow at " + target.getName() + " for " + getAttackStrength() + " HP!");
      boolean wasEnemyHit = handleEnemyDefense(target, getAttackStrength());
      if((int)(Math.random() * 100) < 25 && wasEnemyHit){
        StatusEffect.addStatusEffect(target, "Soft", 2);
      }
      System.out.println(target.getSimpleOutput());
    } else if(basicAbilityIndex == 1){
      // Double Shot
      System.out.println(getName() + " fired an arrow at " + target.getName() + " for " + (getAttackStrength() - 5) + " HP!");
      handleEnemyDefense(target, getAttackStrength() - 5);
      System.out.println(target.getSimpleOutput());
    }
  }
  
  // Archer has two special attacks to choose from
  public void specialAbility(int specialAbilityIndex, BasicCharacter target, PlayerTeam playerTeam, EnemyTeam enemyTeam) throws InterruptedException{
    if(specialAbilityIndex == 0){
      // Volley
      System.out.println(getName() + " fired an arrow at " + target.getName() + " for " + getAttackStrength() + " HP!");
      boolean wasEnemyHit = handleEnemyDefense(target, getAttackStrength());
      if((int)(Math.random() * 100) < 10 && wasEnemyHit){
        StatusEffect.addStatusEffect(target, "Burn", 1);
      }
    } else if(specialAbilityIndex == 1){
      // Crippling Arrow
      System.out.println(getName() + " fired a crippling arrow at " + target.getName() + " for " + (getAttackStrength()+5) + " HP!");
      boolean wasEnemyHit = handleEnemyDefense(target, getAttackStrength());
      if((int)(Math.random() * 100) < 50 && wasEnemyHit){
        StatusEffect.addStatusEffect(target, "Slow", 2);
      }
    }
    System.out.println(target.getSimpleOutput());
  }
  
  // Defense function which is called when an enemy targets the Archer
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
