package src;
import java.util.Scanner;

class Wizard extends PlayerCharacter {
  private Scanner s = new Scanner(System.in);
  public Wizard(String name){
    super(name, 80.0, 40.0, 5.0, 15.0);
    // Set the names, descriptions, and cooldowns of all the Wizard's abilities.
    setDescription("A fighter who uses magic for very high damage output, while sacrificing health and defense.");
    addToArrayList(getBasicAbilityNames(), new String[]{"Magic Zap", "Electro Spirit"});
    addToArrayList(getBasicAbilityDescriptions(), new String[]{"A single ranged attack that deals moderately high damage and has a 10% chance to stun the enemy.",
                                                             "Summons an Electro Spirit, which deals moderate damage to all enemies."});
    addToArrayList(getBasicAbilityTypes(), new String[]{"Offensive", "Offensive"});
    addToArrayList(getBasicAbilityUnlockLevels(), new Integer[]{0, 3});
    addToArrayList(getBasicAbilityEnemyCounts(), new Integer[]{1, 999});
    addToArrayList(getSpecialAbilityNames(), new String[]{"Fireball", "Spirit Incantation"});
    addToArrayList(getSpecialAbilityDescriptions(), new String[]{"Deals heavy damage to a single target and has a 25% chance to burn the enemy.",
                                                               "The Wizard calls on the spirits to unleash a powerful attack. The player must memorize a seven digit code and retype it. If they correctly memorized the code, attack power will be doubled towards a target. Otherwise, attack power will be reduced."});
    addToArrayList(getSpecialAbilityTypes(), new String[]{"Offensive", "Offensive"});
    addToArrayList(getSpecialAbilityUnlockLevels(), new Integer[]{0, 3});
    addToArrayList(getSpecialAbilityEnemyCounts(), new Integer[]{1, 1});
    addToArrayList(getSpecialAbilityCooldowns(), new Integer[]{2, 3});
    addToArrayList(getCurrentSpecialAbilityCooldowns(), new Integer[]{2, 3});
  }
  
  // Overrided getType method
  public String getType(){
    return "Wizard";
  }
  
  // Overrided battle methods
  public void basicAbility(int basicAbilityIndex, BasicCharacter target, PlayerTeam playerTeam, EnemyTeam enemyTeam) throws InterruptedException{
    if(basicAbilityIndex == 0){
      // Magic Zap
      System.out.println(getName() + " zapped " + target.getName() + " for " + (getAttackStrength()-5) + " HP!");
      boolean wasEnemyHit = handleEnemyDefense(target, (getAttackStrength()-5), playerTeam, enemyTeam);
      if((int)(Math.random() * 100) < 10 && wasEnemyHit){
        StatusEffect.addStatusEffect(target, "Stun", 1);
      }
      System.out.println(target.getSimpleOutput());
    } else if(basicAbilityIndex == 1){
      // Electro Spirit
      System.out.println(getName() + "'s Electro Spirit zapped " + target.getName() + " for " + (getAttackStrength() - 15) + " HP!");
      handleEnemyDefense(target, getAttackStrength() - 15, playerTeam, enemyTeam);
      System.out.println(target.getSimpleOutput());
    }
  }
  
  // Wizard has two special attacks to choose from
  public void specialAbility(int specialAbilityIndex, BasicCharacter target, PlayerTeam playerTeam, EnemyTeam enemyTeam) throws InterruptedException{
    if(specialAbilityIndex == 0){
      // Fireball
      System.out.println(getName() + " launched a fireball at " + target.getName() + " for " + getAttackStrength() + " HP!");
      boolean wasEnemyHit = handleEnemyDefense(target, getAttackStrength(), playerTeam, enemyTeam);
      if((int)(Math.random() * 100) < 25 && wasEnemyHit){
        StatusEffect.addStatusEffect(target, "Burn", 2);
      }
    } else if(specialAbilityIndex == 1){
      // Spirit Incantation
      int correctDigits = promptMemorizationCode(7);
      if(correctDigits == 7){
        System.out.println("The spirits have answered your call!");
        Thread.sleep(1000);
        System.out.println(getName() + " casted the ancient spell at " + target.getName() + " for " + (getAttackStrength() * 2) + " HP!");
        handleEnemyDefense(target, (getAttackStrength() * 2), playerTeam, enemyTeam);
      } else{
        System.out.println("The spirits have rejected your call.");
        Thread.sleep(1000);
        System.out.println(getName() + " casted a weak spell at " + target.getName() + " for " + (getAttackStrength() * 0.75) + " HP.");
        handleEnemyDefense(target, (getAttackStrength() * 0.75), playerTeam, enemyTeam);
      }
    }
    System.out.println(target.getSimpleOutput());
  }
  
  // Defense function which is called when an enemy targets the Wizard
  public void defend(BasicCharacter target, double actualDamage){
    if(actualDamage == 0){
      System.out.println(getName() + " cancelled " + target.getName() + "'s attack!");
    }else if(getIsDefending()){
      System.out.println(getName() + " partially cancelled " + target.getName() + "'s attack for " + getDefenseStrength() * 2 + " HP!");
    } else{
      System.out.println(getName() + " lightly cancelled " + target.getName() + "'s attack for " + getDefenseStrength() + " HP!");
    }
  }

  // Generate a random numerical code with length digits.
  // Prompt the user to memorize the code and reproduce it from memory.
  // Return the number of digits that are correct and placed in the right location. (<=length)
  public int promptMemorizationCode(int length){
      String code = "";
      for(int i = 0; i < length; i++){
        int random_digit = (int)(Math.random() * 10);
        code += Integer.toString(random_digit);
      }
      System.out.println("You must memorize the numbers needed to cast the spell:");
      System.out.println(code);
      GameManager.anythingToContinue();
      GameManager.clearScreen();
      System.out.print("Retype the code you saw earlier: \n>>> ");
      String inputCode = s.nextLine();
      int correctDigits = 0;
      for(int i = 0; i < length; i++){
        if(code.charAt(i) == inputCode.charAt(i)){
          correctDigits++;
        }
      }
      return correctDigits;
  }
}
