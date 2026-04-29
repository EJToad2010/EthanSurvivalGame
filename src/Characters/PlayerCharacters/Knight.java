package src.Characters.PlayerCharacters;

import java.awt.Graphics;

import src.Characters.BasicCharacter;
import src.Characters.PlayerCharacter;
import src.GameManagement.GameManager;
import src.GameManagement.Mechanics.ActionResult;
import src.GameManagement.Mechanics.Signals;
import src.Teams.EnemyTeam;
import src.Teams.PlayerTeam;

// High health and ability, low defense and healing
public class Knight extends PlayerCharacter {
  public Knight(String name){
    super(name, 100.0, 30.0, 12.0, 5.0);
    // Set the names, descriptions, and cooldowns of all the Knight's abilities.
    setDescription("An offensive tank who can withstand the front lines of battle, at the cost of speed.");
    addToArrayList(getBasicAbilityNames(), new String[]{"Sword Swing", "Cautious Attack"});
    addToArrayList(getBasicAbilityDescriptions(), new String[]{"A melee attack. The enemy counterattacks for a small amount of damage.",
                                                             "Deals less damage than Sword Swing, but the enemy cannot counterattack."});
    addToArrayList(getBasicAbilityTypes(), new String[]{"Offensive", "Offensive"});
    addToArrayList(getBasicAbilityUnlockLevels(), new Integer[]{0, 3});
    addToArrayList(getBasicAbilityEnemyCounts(), new Integer[]{1, 1});
    addToArrayList(getBasicAbilityAnimationLengths(), new Integer[]{20, 20});
    addToArrayList(getSpecialAbilityNames(), new String[]{"Last Push", "Rage Strike"});
    addToArrayList(getSpecialAbilityDescriptions(), new String[]{"Deals +10 damage from Sword Swing. Deals an additional 50% damage if the enemy is below 50% HP.",
                                                               "Attack power is doubled from Sword Swing, but lose HP equal to 25% of your doubled attack power."});
    addToArrayList(getSpecialAbilityTypes(), new String[]{"Offensive", "Offensive"});
    addToArrayList(getSpecialAbilityUnlockLevels(), new Integer[]{0, 4});
    addToArrayList(getSpecialAbilityEnemyCounts(), new Integer[]{1, 1});
    addToArrayList(getSpecialAbilityCooldowns(), new Integer[]{2, 3});
    addToArrayList(getCurrentSpecialAbilityCooldowns(), new Integer[]{2, 3});
    addToArrayList(getSpecialAbilityAnimationLengths(), new Integer[]{0, 0});
    setCharacterImage("src/Images/knight.png");
  }
  
  // Overrided getType method
  public String getType(){
    return "Knight";
  }
  
  // Overrided battle methods
  public ActionResult basicAbility(int basicAbilityIndex, BasicCharacter target, PlayerTeam playerTeam, EnemyTeam enemyTeam){
    ActionResult output = new ActionResult();
    output.add(Signals.TARGET_OBJECT, target.getID());
    if(basicAbilityIndex == 0){
      // Sword Swing
      //System.out.println(getName() + " swung their sword at " + target.getName() + " for " + getAttackStrength() + " HP!");
      output.add(getName() + " swung their sword at " + target.getName() + " for " + getAttackStrength() + " HP!", Signals.ATTACK_PERFORMED, getAttackStrength());
      output.add(handleEnemyDefense(target, getAttackStrength(), playerTeam, enemyTeam));
      //System.out.println(target.getSimpleOutput());
      
      if(!target.getIsDead()){
        int counterAttackDamage = (int)(Math.random() * 3) + 5;
        //Thread.sleep(1000);
        //System.out.println(target.getName() + " counterattacked for " + (double) counterAttackDamage + " HP!");
        output.add(target.getName() + " counterattacked for " + (double) counterAttackDamage + " HP!", Signals.HEALTH_LOST, (double)counterAttackDamage);
        //changeCurrentHP(-(double) counterAttackDamage);
        //Thread.sleep(1000);
        //System.out.println(getSimpleOutput());
      }
    } else if(basicAbilityIndex == 1){
      // Cautious Attack
      //System.out.println(getName() + " cautiously swung their sword at " + target.getName() + " for " + (getAttackStrength() - 8) + " HP!");
      output.add(getName() + " cautiously swung their sword at " + target.getName() + " for " + (getAttackStrength() - 8) + " HP!", Signals.ATTACK_PERFORMED, getAttackStrength() - 8);
      output.add(handleEnemyDefense(target, getAttackStrength() - 8, playerTeam, enemyTeam));
      //System.out.println(target.getSimpleOutput());
    }
    return output;
  }
  
  // Knight has two special attacks to choose from
  public ActionResult specialAbility(int specialAbilityIndex, BasicCharacter target, PlayerTeam playerTeam, EnemyTeam enemyTeam){
    ActionResult output = new ActionResult();
    output.add(Signals.TARGET_OBJECT, target.getID());
    if(specialAbilityIndex == 0){
      // Last Push
      if(target.getCurrentHP() / target.getMaxHP() < 0.5){
        //System.out.println(getName() + " had a last push against " + target.getName() + " for " + (getAttackStrength() + 10) * 1.5 + " HP!");
        output.add(getName() + " had a last push against " + target.getName() + " for " + (getAttackStrength() + 10) * 1.5 + " HP!", Signals.ATTACK_PERFORMED, (getAttackStrength() + 10)*1.5);
        output.add(handleEnemyDefense(target, (getAttackStrength() + 10) * 1.5, playerTeam, enemyTeam));
      } else{
        //System.out.println(getName() + " pushed against " + target.getName() + " for " + (getAttackStrength() + 10) + " HP!");
        output.add(getName() + " pushed against " + target.getName() + " for " + (getAttackStrength() + 10) + " HP!", Signals.ATTACK_PERFORMED, (getAttackStrength() + 10));
        output.add(handleEnemyDefense(target, getAttackStrength() + 10, playerTeam, enemyTeam));
      }
      
    } else if(specialAbilityIndex == 1){
      // Rage Strike
      //System.out.println(getName() + " furiously attacked " + target.getName() + " for " + (getAttackStrength() * 2) + " HP!");
      output.add(getName() + " furiously attacked " + target.getName() + " for " + (getAttackStrength() * 2) + " HP!", Signals.ATTACK_PERFORMED, getAttackStrength()*2);
      output.add(handleEnemyDefense(target, (getAttackStrength() * 2), playerTeam, enemyTeam));
      //System.out.println(getName() + "'s rage made them lose " + (getAttackStrength() * 0.5) + " HP!");
      output.add(getName() + "'s rage made them lose " + (getAttackStrength() * 0.5) + " HP!", Signals.HEALTH_LOST, getAttackStrength()*0.5);
      //changeCurrentHP(-(getAttackStrength() * 0.5));
      //System.out.println(getSimpleOutput());
    }
    //System.out.println(target.getSimpleOutput());
    return output;
  }
  
  // Defense function which is called when an enemy targets the Knight
  public ActionResult defend(BasicCharacter target, double actualDamage){
    ActionResult output = new ActionResult();
    if(actualDamage == 0){
      //System.out.println(getName() + " fully countered " + target.getName() + " with their shield!");
      output.add(getName() + " fully countered " + target.getName() + " with their shield!",Signals.DEFENSE_PERFORMED, 999.0);
    }else if(getIsDefending()){
      //System.out.println(getName() + " partially blocked " + target.getName() + "'s attack with their shield for " + getDefenseStrength() * 2 + " HP!");
      output.add(getName() + " defended against " + target.getName() + " for " + getDefenseStrength() * 2 + " HP!",Signals.DEFENSE_PERFORMED, getDefenseStrength()*2);
    } else{
      //System.out.println(getName() + " lightly blocked " + target.getName() + "'s attack with their shield for " + getDefenseStrength() + " HP!");
      output.add(getName() + " lightly blocked " + target.getName() + "'s attack with their shield for " + getDefenseStrength() + " HP!",Signals.DEFENSE_PERFORMED, getDefenseStrength());
    }
    return output;
  }

  // Called every time the Character conducts an offensive attack
  public void drawAttackAnimation(String abilityType, int abilityIndex, Graphics graphics, int tick){
    // Move right for 10 ticks, move left back to start for 10 ticks
    int localX = getX() + 75 - 15 * Math.abs(tick - 10);
    drawCharImage(graphics, localX, getY());
    drawHPBar(graphics, localX, getY());
    drawCharText(graphics, localX, getY());
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
