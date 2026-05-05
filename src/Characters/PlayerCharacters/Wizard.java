package src.Characters.PlayerCharacters;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Scanner;

import src.Characters.BasicCharacter;
import src.Characters.PlayerCharacter;
import src.GameManagement.GameManager;
import src.GameManagement.Mechanics.ActionResult;
import src.GameManagement.Mechanics.Signals;
import src.GameManagement.UI.ImageManager;
import src.Misc.StatusEffect;
import src.Teams.EnemyTeam;
import src.Teams.PlayerTeam;

public class Wizard extends PlayerCharacter {
  private Scanner s = new Scanner(System.in);
  // Rendered during character animations
  BufferedImage zap = ImageManager.loadImage("src/Images/zap.png");
  public Wizard(String name){
    super(name, 80.0, 40.0, 5.0, 15.0);
    // Set the names, descriptions, and cooldowns of all the Wizard's abilities.
    setDescription("A fighter who uses magic for very high damage output, while sacrificing health and defense.");
    addToArrayList(getBasicAbilityNames(), new String[]{"Magic Zap", "Shielding Zap"});
    addToArrayList(getBasicAbilityDescriptions(), new String[]{"A single ranged attack that deals moderately high damage and has a 10% chance to stun the enemy.",
                                                             "A single ranged attack that deals moderate damage and grants temporary defensive strength equal to 25% of the damage dealt."});
    addToArrayList(getBasicAbilityTypes(), new String[]{"Offensive", "Offensive"});
    addToArrayList(getBasicAbilityUnlockLevels(), new Integer[]{0, 3});
    addToArrayList(getBasicAbilityEnemyCounts(), new Integer[]{1, 1});
    addToArrayList(getBasicAbilityAnimationLengths(), new Integer[]{10, 0});
    addToArrayList(getSpecialAbilityNames(), new String[]{"Fireball", "Spirit Calling"});
    addToArrayList(getSpecialAbilityDescriptions(), new String[]{"Deals heavy damage to a single target and has a 25% chance to burn the enemy.",
                                                               "The Wizard calls on the spirits to unleash a powerful attack. There is a 50% for attack power to be doubled, but a 50% for attack power to be halved."});
    addToArrayList(getSpecialAbilityTypes(), new String[]{"Offensive", "Offensive"});
    addToArrayList(getSpecialAbilityUnlockLevels(), new Integer[]{0, 4});
    addToArrayList(getSpecialAbilityEnemyCounts(), new Integer[]{1, 1});
    addToArrayList(getSpecialAbilityCooldowns(), new Integer[]{2, 3});
    addToArrayList(getCurrentSpecialAbilityCooldowns(), new Integer[]{2, 3});
    addToArrayList(getSpecialAbilityAnimationLengths(), new Integer[]{0, 0});
    setCharacterImage("src/Images/wizard.png");
  }
  
  // Overrided getType method
  public String getType(){
    return "Wizard";
  }
  
  // Overrided battle methods
  public ActionResult basicAbility(int basicAbilityIndex, BasicCharacter target, PlayerTeam playerTeam, EnemyTeam enemyTeam){
    ActionResult output = new ActionResult();
    output.add(getCharInfoSignals(target, 0, basicAbilityIndex));
    if(basicAbilityIndex == 0){
      // Magic Zap
      //System.out.println(getName() + " zapped " + target.getName() + " for " + (getAttackStrength()) + " HP!");
      output.add(getName() + " zapped " + target.getName() + " for " + (getAttackStrength()) + " HP!", Signals.ATTACK_PERFORMED, getAttackStrength());
      // Check for how much damage the attack did to the enemy
      ActionResult defenseResult = handleEnemyDefense(target, getAttackStrength(), playerTeam, enemyTeam);
      Double enemyHPChange = getAttackStrength() - defenseResult.getAmount(Signals.DEFENSE_PERFORMED);
      output.add(defenseResult);
      if((int)(Math.random() * 100) < 10 && enemyHPChange > 0){
        output.add(StatusEffect.addStatusEffect(target, "Stun", 1));
      }
      //System.out.println(target.getSimpleOutput());
    } else if(basicAbilityIndex == 1){
      // Shielding Zap
      output.add(getName() + " fired a shielding zap at " + target.getName() + " for " + (getAttackStrength() - 10) + " HP!", Signals.ATTACK_PERFORMED, getAttackStrength()-20);
      output.add(handleEnemyDefense(target, getAttackStrength() - 10, playerTeam, enemyTeam));
      playerTeam.getProtectedCharacters().add(this);
      playerTeam.getProtectedCharacterAmounts().add((getAttackStrength() - 10) / 4);
      output.add(getName() + "'s force field gained " + (getAttackStrength() - 10) / 4 + " defensive strength!");
      //System.out.println(target.getSimpleOutput());
    }
    return output;
  }
  
  // Wizard has two special attacks to choose from
  public ActionResult specialAbility(int specialAbilityIndex, BasicCharacter target, PlayerTeam playerTeam, EnemyTeam enemyTeam){
    ActionResult output = new ActionResult();
    output.add(getCharInfoSignals(target, 1, specialAbilityIndex));
    if(specialAbilityIndex == 0){
      // Fireball
      //System.out.println(getName() + " launched a fireball at " + target.getName() + " for " + getAttackStrength()+5 + " HP!");
      output.add(getName() + " launched a fireball at " + target.getName() + " for " + getAttackStrength()+5 + " HP!",Signals.ATTACK_PERFORMED, getAttackStrength()+5);
      // Check for how much damage the attack did to the enemy
      ActionResult defenseResult = handleEnemyDefense(target, getAttackStrength(), playerTeam, enemyTeam);
      Double enemyHPChange = getAttackStrength() - defenseResult.getAmount(Signals.DEFENSE_PERFORMED);
      output.add(defenseResult);
      if((int)(Math.random() * 100) < 25 && enemyHPChange > 0){
        output.add(StatusEffect.addStatusEffect(target, "Burn", 2));
      }
    } else if(specialAbilityIndex == 1){
      // Spirit Incantation
      if(Math.random() < 0.5){
        output.add("The spirits have answered your call!");
        output.add(getName() + " casted an ancient spell at " + target.getName() + " for " + (getAttackStrength() * 2) + " HP!", Signals.ATTACK_PERFORMED, (getAttackStrength() * 2));
        output.add(handleEnemyDefense(target, (getAttackStrength() * 2), playerTeam, enemyTeam));
      } else{
        output.add("The spirits have not answered your call...");
        output.add(getName() + " casted a weak spell at " + target.getName() + " for " + (getAttackStrength() /2) + " HP!", Signals.ATTACK_PERFORMED, (getAttackStrength() / 2));
        output.add(handleEnemyDefense(target, (getAttackStrength() / 2), playerTeam, enemyTeam));
      }
      /*int correctDigits = promptMemorizationCode(7);
      if(correctDigits == 7){
        System.out.println("The spirits have answered your call!");
        System.out.println(getName() + " casted the ancient spell at " + target.getName() + " for " + (getAttackStrength() * 2) + " HP!");
        output.add(handleEnemyDefense(target, (getAttackStrength() * 2), playerTeam, enemyTeam));
      } else{
        System.out.println("The spirits have rejected your call.");
        System.out.println(getName() + " casted a weak spell at " + target.getName() + " for " + (getAttackStrength() * 0.75) + " HP.");
        output.add(handleEnemyDefense(target, (getAttackStrength() * 0.75), playerTeam, enemyTeam));
      }*/
    }
    //System.out.println(target.getSimpleOutput());
    return output;
  }
  
  // Defense function which is called when an enemy targets the Wizard
  public ActionResult defend(BasicCharacter target, double actualDamage){
    ActionResult output = new ActionResult();
    if(actualDamage == 0){
      //System.out.println(getName() + " cancelled " + target.getName() + "'s attack!");
      output.add(getName() + "'s force field cancelled " + target.getName() + "'s attack!",Signals.DEFENSE_PERFORMED, 999.0);
    }else if(getIsDefending()){
      //System.out.println(getName() + " partially cancelled " + target.getName() + "'s attack for " + getDefenseStrength() * 2 + " HP!");
      output.add(getName() + "'s force field partially cancelled " + target.getName() + "'s attack for " + getDefenseStrength() * 2 + " HP!",Signals.DEFENSE_PERFORMED, getDefenseStrength()*2);
    } else{
      //System.out.println(getName() + " lightly cancelled " + target.getName() + "'s attack for " + getDefenseStrength() + " HP!");
      output.add(getName() + "'s force field lightly cancelled " + target.getName() + "'s attack for " + getDefenseStrength() + " HP!",Signals.DEFENSE_PERFORMED, getDefenseStrength());
    }
    return output;
  }

  // Called every time the Character conducts an offensive attack
  public void drawAttackAnimation(BasicCharacter target, String abilityType, int abilityIndex, Graphics graphics, int tick){
    int localX = getX();
    if(abilityType.equals("Basic ability")){
      if(abilityIndex == 0){
        // Magic Zap
        // Render a zap image at the enemy location for 10 ticks
        graphics.drawImage(zap, target.getX(), 0, null);
      } else if(abilityIndex == 1){
        // Electro Spirit
      }
    } else{
      if(abilityIndex == 0){
        // Fireball
        
      } else if(abilityIndex == 1){
        // Spirit Calling
      }
    }
    drawCharImage(graphics, localX, getY());
    drawHPBar(graphics, localX, getY());
    drawCharText(graphics, localX, getY());
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
