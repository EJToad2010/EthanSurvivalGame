package src.Characters.PlayerCharacters;
import java.util.Scanner;

import src.Characters.BasicCharacter;
import src.Characters.PlayerCharacter;
import src.GameManagement.GameManager;
import src.GameManagement.Mechanics.ActionResult;
import src.GameManagement.Mechanics.Signals;
import src.Misc.StatusEffect;
import src.Teams.EnemyTeam;
import src.Teams.PlayerTeam;

public class Wizard extends PlayerCharacter {
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
    addToArrayList(getSpecialAbilityUnlockLevels(), new Integer[]{0, 4});
    addToArrayList(getSpecialAbilityEnemyCounts(), new Integer[]{1, 1});
    addToArrayList(getSpecialAbilityCooldowns(), new Integer[]{2, 3});
    addToArrayList(getCurrentSpecialAbilityCooldowns(), new Integer[]{2, 3});
    setCharacterImage("src/Images/wizard.png");
  }
  
  // Overrided getType method
  public String getType(){
    return "Wizard";
  }
  
  // Overrided battle methods
  public ActionResult basicAbility(int basicAbilityIndex, BasicCharacter target, PlayerTeam playerTeam, EnemyTeam enemyTeam){
    ActionResult output = new ActionResult();
    if(basicAbilityIndex == 0){
      // Magic Zap
      //System.out.println(getName() + " zapped " + target.getName() + " for " + (getAttackStrength()) + " HP!");
      output.add(getName() + " zapped " + target.getName() + " for " + (getAttackStrength()) + " HP!", Signals.ATTACK_PERFORMED, getAttackStrength());
      // Check for how much damage the attack did to the enemy
      ActionResult defenseResult = handleEnemyDefense(target, getAttackStrength(), playerTeam, enemyTeam);
      Double enemyHPChange = getAttackStrength() - defenseResult.getAmount(Signals.DEFENSE_PERFORMED);
      output.add(defenseResult);
      if((int)(Math.random() * 100) < 10 && enemyHPChange > 0){
        StatusEffect.addStatusEffect(target, "Stun", 1);
      }
      //System.out.println(target.getSimpleOutput());
    } else if(basicAbilityIndex == 1){
      // Electro Spirit
      //System.out.println(getName() + "'s Electro Spirit zapped " + target.getName() + " for " + (getAttackStrength() - 15) + " HP!");
      output.add(getName() + "'s Electro Spirit zapped " + target.getName() + " for " + (getAttackStrength() - 15) + " HP!", Signals.ATTACK_PERFORMED, getAttackStrength()-15);
      output.add(handleEnemyDefense(target, getAttackStrength() - 15, playerTeam, enemyTeam));
      //System.out.println(target.getSimpleOutput());
    }
    return output;
  }
  
  // Wizard has two special attacks to choose from
  public ActionResult specialAbility(int specialAbilityIndex, BasicCharacter target, PlayerTeam playerTeam, EnemyTeam enemyTeam){
    ActionResult output = new ActionResult();
    if(specialAbilityIndex == 0){
      // Fireball
      //System.out.println(getName() + " launched a fireball at " + target.getName() + " for " + getAttackStrength()+5 + " HP!");
      output.add(getName() + " launched a fireball at " + target.getName() + " for " + getAttackStrength()+5 + " HP!",Signals.ATTACK_PERFORMED, getAttackStrength()+5);
      // Check for how much damage the attack did to the enemy
      ActionResult defenseResult = handleEnemyDefense(target, getAttackStrength(), playerTeam, enemyTeam);
      Double enemyHPChange = getAttackStrength() - defenseResult.getAmount(Signals.DEFENSE_PERFORMED);
      output.add(defenseResult);
      if((int)(Math.random() * 100) < 25 && enemyHPChange > 0){
        StatusEffect.addStatusEffect(target, "Burn", 2);
      }
    } else if(specialAbilityIndex == 1){
      // Spirit Incantation
      int correctDigits = promptMemorizationCode(7);
      if(correctDigits == 7){
        System.out.println("The spirits have answered your call!");
        System.out.println(getName() + " casted the ancient spell at " + target.getName() + " for " + (getAttackStrength() * 2) + " HP!");
        output.add(handleEnemyDefense(target, (getAttackStrength() * 2), playerTeam, enemyTeam));
      } else{
        System.out.println("The spirits have rejected your call.");
        System.out.println(getName() + " casted a weak spell at " + target.getName() + " for " + (getAttackStrength() * 0.75) + " HP.");
        output.add(handleEnemyDefense(target, (getAttackStrength() * 0.75), playerTeam, enemyTeam));
      }
    }
    //System.out.println(target.getSimpleOutput());
    return output;
  }
  
  // Defense function which is called when an enemy targets the Wizard
  public ActionResult defend(BasicCharacter target, double actualDamage){
    ActionResult output = new ActionResult();
    if(actualDamage == 0){
      //System.out.println(getName() + " cancelled " + target.getName() + "'s attack!");
      output.add(getName() + " cancelled " + target.getName() + "'s attack!",Signals.DEFENSE_PERFORMED, 999.0);
    }else if(getIsDefending()){
      //System.out.println(getName() + " partially cancelled " + target.getName() + "'s attack for " + getDefenseStrength() * 2 + " HP!");
      output.add(getName() + " partially cancelled " + target.getName() + "'s attack for " + getDefenseStrength() * 2 + " HP!",Signals.DEFENSE_PERFORMED, getDefenseStrength()*2);
    } else{
      //System.out.println(getName() + " lightly cancelled " + target.getName() + "'s attack for " + getDefenseStrength() + " HP!");
      output.add(getName() + " lightly cancelled " + target.getName() + "'s attack for " + getDefenseStrength() + " HP!",Signals.DEFENSE_PERFORMED, getDefenseStrength());
    }
    return output;
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
