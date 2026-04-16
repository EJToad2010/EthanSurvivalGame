package src.Characters.EnemyCharacters;

import java.util.ArrayList;

import src.Characters.BasicCharacter;
import src.Characters.EnemyCharacter;
import src.Characters.PlayerCharacter;
import src.GameManagement.Mechanics.ActionResult;
import src.GameManagement.Mechanics.Signals;
import src.Misc.StatusEffect;
import src.Teams.EnemyTeam;
import src.Teams.PlayerTeam;

public class DartGoblin extends EnemyCharacter{
  public DartGoblin(String name, String behaviorType){
    // Set the names, descriptions, and cooldowns of the Dart Goblin.
    super(name, 50.0, 8.0, 3.0, 25.0, 50, 20, behaviorType);
    setDescription("A fast, ranged, weak enemy who focuses on dealing poison damage to the player's team.");
    addToArrayList(getBasicAbilityNames(), new String[]{"Poison Dart", "Nimble Dodge"});
    addToArrayList(getBasicAbilityDescriptions(), new String[]{"Deals a small amount of damage onto one target, ignoring their defense. 25% to poison the character for two turns.",
                                                             "During the player's turn, all attacks toward the Goblin deal 25% less damage."});
    addToArrayList(getBasicAbilityTypes(), new String[]{"Offensive", "Offensive"});
    addToArrayList(getBasicAbilityEnemyCounts(), new Integer[]{1, 0});
    addToArrayList(getSpecialAbilityNames(), new String[]{"Poison Cloud", "Poison Mark"});
    addToArrayList(getSpecialAbilityDescriptions(), new String[]{"Shoot slightly weaker poison clouds at all targets. 33% chance for each target to be poisoned for 2 turns.",
                                                               "Shoot a poison dart at a target. This target gets a 75% chance to be poisoned for 3 turns."});
    addToArrayList(getSpecialAbilityTypes(), new String[]{"Offensive", "Offensive"});
    addToArrayList(getSpecialAbilityEnemyCounts(), new Integer[]{999, 1});
    addToArrayList(getSpecialAbilityCooldowns(), new Integer[]{2, 3});
    addToArrayList(getCurrentSpecialAbilityCooldowns(), new Integer[]{2, 3});
    setCharacterImage("src/Images/dartgoblin.png");
  }
  
  // Overrided getType method
  public String getType(){
    return "Dart Goblin";
  }

  // AI for deciding what basic ability to use
  public ActionResult basicAbilityAI(PlayerTeam playerTeam, EnemyTeam enemyTeam){
    // Fetch alive Characters from playerTeam
    ArrayList<PlayerCharacter> aliveCharacters = playerTeam.getAliveCharacters();
    // System.out.println("DEBUG: Dart Goblin basic ability AI");
    int basicAbilityLimit = getBasicAbilityLimit();
    if(basicAbilityLimit >= 1){
      // Guaranteed to use Nimble Dodge if under 25% HP
      if(getCurrentHP() / getMaxHP() < 0.25){
        return basicAbility(1, this, playerTeam, enemyTeam);
      } // 50% chance to use Nimble Dodge if under 50% HP
      if(getCurrentHP() / getMaxHP() < 0.5 && (int)(Math.random() * 100) < 50){
        return basicAbility(1, this, playerTeam, enemyTeam);
      }
      // 25% chance to use Nimble Dodge otherwise
      if((int)(Math.random() * 100) < 25){
        return basicAbility(1, this, playerTeam, enemyTeam);
      }
    }
    // Use Poison Dart on a random Character if all other options are exhausted
    return basicAbility(0, aliveCharacters.get((int)(Math.random() * aliveCharacters.size())), playerTeam, enemyTeam);
  }

  // This method is used instead of the original if the EnemyCharacter AI finds a specific Character that is prioritized most
  public ActionResult basicAbilityAI(BasicCharacter preferredCharacter, PlayerTeam playerTeam, EnemyTeam enemyTeam){
    // System.out.println("DEBUG: Dart Goblin aggressive AI");
    // Likely has aggressive AI behavior, so automatically use Poison Dart
    return basicAbility(0, preferredCharacter, playerTeam, enemyTeam);
  }

  // AI for deciding a special ability to use
  public ActionResult specialAbilityAI(ArrayList<Integer> availableSpecialAbilityIndices, PlayerTeam playerTeam, EnemyTeam enemyTeam){
    // Fetch alive Characters from playerTeam
    ArrayList<PlayerCharacter> aliveCharacters = playerTeam.getAliveCharacters();
    resetSpecialAbilityCooldowns();
    int randomAbilityIndex = availableSpecialAbilityIndices.get((int)(Math.random() * availableSpecialAbilityIndices.size()));
    if(randomAbilityIndex == 0){
      // Poison Dart Volley automatically targets all characters. No special decision making needed.
      for(PlayerCharacter c : aliveCharacters){
        return specialAbility(0, c, playerTeam, enemyTeam);
      }
    } else if(randomAbilityIndex == 1){
      // Pickpocket ability
      return new ActionResult();
    }
    return new ActionResult();
  }
  
  // Overrided battle methods
  // basicAbilityAI handles the decision making, while basicAbility handles the outcome of an ability itself.
  // Same for specialAbility()
  public ActionResult basicAbility(int basicAbilityIndex, BasicCharacter target, PlayerTeam playerTeam, EnemyTeam enemyTeam){
    ActionResult output = new ActionResult();
    output.add(Integer.toString(target.getID()), Signals.TARGET_OBJECT);
    if(basicAbilityIndex == 0){
      // Poison Dart
      //System.out.println(getName() + " shot a poison dart at " + target.getName() + " for " + getAttackStrength() + " HP!");
      output.add(getName() + " shot a poison dart at " + target.getName() + " for " + getAttackStrength() + " HP!", Signals.ATTACK_PERFORMED, getAttackStrength());
      // Check for how much damage the attack did to the enemy
      ActionResult defenseResult = handleEnemyDefense(target, getAttackStrength(), playerTeam, enemyTeam);
      Double enemyHPChange = getAttackStrength() - defenseResult.getAmount(Signals.DEFENSE_PERFORMED);
      output.add(defenseResult);
      if((int)(Math.random() * 100) < 25 && enemyHPChange > 0){
        StatusEffect.addStatusEffect(target, "Poison", 2);
      }
      //System.out.println(target.getSimpleOutput());
    } else if(basicAbilityIndex == 1){
      // Nimble Dodge (shared with Goblin class since they are both speedy Goblin types)
      //System.out.println(getName() + " prepared Nimble Dodge for the player's next turn!");
      output.add(getName() + " prepared Nimble Dodge for the player's next turn!");
      StatusEffect.addStatusEffect(this, "Nimble", 1);
    }
    return output;
  }
  
  public ActionResult specialAbility(int specialAbilityIndex, BasicCharacter target, PlayerTeam playerTeam, EnemyTeam enemyTeam){
    ActionResult output = new ActionResult();
    output.add(Integer.toString(target.getID()), Signals.TARGET_OBJECT);
    if(specialAbilityIndex == 0){
      // Poison Cloud
      //System.out.println(getName() + " shot a poison cloud at " + target.getName() + " for " + (getAttackStrength()-5) + " HP!");
      output.add(getName() + " shot a poison cloud at " + target.getName() + " for " + (getAttackStrength()-5) + " HP!", Signals.ATTACK_PERFORMED, getAttackStrength()-5);
      output.add(handleEnemyDefense(target, getAttackStrength()-5, playerTeam, enemyTeam));
      //Thread.sleep(1000);
      // 33% chance of Poison for 2 turns
      if((int)(Math.random() * 100) < 33){
        StatusEffect.addStatusEffect(target, "Poison", 2);
      }
    } else if(specialAbilityIndex == 1){
      // Poison Mark
      //System.out.println(getName() + " marked " + target.getName() + " with poison for " + getAttackStrength() + " HP!");
      output.add(getName() + " marked " + target.getName() + " with poison for " + getAttackStrength() + " HP!", Signals.ATTACK_PERFORMED, getAttackStrength());
      ActionResult defenseResult = handleEnemyDefense(target, getAttackStrength(), playerTeam, enemyTeam);
      Double enemyHPChange = getAttackStrength() - defenseResult.getAmount(Signals.DEFENSE_PERFORMED);
      output.add(defenseResult);
      if((int)(Math.random() * 100) < 75 && enemyHPChange > 0){
        StatusEffect.addStatusEffect(target, "Poison", 3);
      }
    }
    //System.out.println(target.getSimpleOutput());
    return output;
  }
  
  // Defense function which is called when an enemy targets the Knight
  public ActionResult defend(BasicCharacter target, double actualDamage){
    ActionResult output = new ActionResult();
    if(actualDamage == 0){
      //System.out.println(getName() + " successfully hid from " + target.getName() + "'s attack!");
      output.add(getName() + " successfully hid from " + target.getName() + "'s attack!", Signals.DEFENSE_PERFORMED, 999.0);
    }else if(getIsDefending()){
      //System.out.println(getName() + " partially hid from " + target.getName() + "'s attack for " + getDefenseStrength() * 2 + " HP!");
      output.add(getName() + " partially hid from " + target.getName() + "'s attack for " + getDefenseStrength() * 2 + " HP!", Signals.DEFENSE_PERFORMED, getDefenseStrength()*2);
    } else{
      //System.out.println(getName() + " tried to hide from " + target.getName() + "'s attack for " + getDefenseStrength() + " HP!");
      output.add(getName() + " tried to hide from " + target.getName() + "'s attack for " + getDefenseStrength() + " HP!", Signals.DEFENSE_PERFORMED, getDefenseStrength());
    }
    return output;
  }
}
