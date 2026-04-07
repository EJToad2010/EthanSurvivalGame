package src.Characters.EnemyCharacters;

import java.util.ArrayList;

import src.Characters.BasicCharacter;
import src.Characters.EnemyCharacter;
import src.Characters.PlayerCharacter;
import src.GameManagement.Mechanics.ActionResult;
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
  public void basicAbilityAI(PlayerTeam playerTeam, EnemyTeam enemyTeam) throws InterruptedException{
    // Fetch alive Characters from playerTeam
    ArrayList<PlayerCharacter> aliveCharacters = playerTeam.getAliveCharacters();
    // System.out.println("DEBUG: Dart Goblin basic ability AI");
    int basicAbilityLimit = getBasicAbilityLimit();
    if(basicAbilityLimit >= 1){
      // Guaranteed to use Nimble Dodge if under 25% HP
      if(getCurrentHP() / getMaxHP() < 0.25){
        basicAbility(1, this, playerTeam, enemyTeam);
        return;
      } // 50% chance to use Nimble Dodge if under 50% HP
      if(getCurrentHP() / getMaxHP() < 0.5 && (int)(Math.random() * 100) < 50){
        basicAbility(1, this, playerTeam, enemyTeam);
        return;
      }
      // 25% chance to use Nimble Dodge otherwise
      if((int)(Math.random() * 100) < 25){
        basicAbility(1, this, playerTeam, enemyTeam);
        return;
      }
    }
    // Use Poison Dart on a random Character if all other options are exhausted
    basicAbility(0, aliveCharacters.get((int)(Math.random() * aliveCharacters.size())), playerTeam, enemyTeam);
  }

  // This method is used instead of the original if the EnemyCharacter AI finds a specific Character that is prioritized most
  public void basicAbilityAI(BasicCharacter preferredCharacter, PlayerTeam playerTeam, EnemyTeam enemyTeam) throws InterruptedException{
    // System.out.println("DEBUG: Dart Goblin aggressive AI");
    // Likely has aggressive AI behavior, so automatically use Poison Dart
    basicAbility(0, preferredCharacter, playerTeam, enemyTeam);
  }

  // AI for deciding a special ability to use
  public void specialAbilityAI(ArrayList<Integer> availableSpecialAbilityIndices, PlayerTeam playerTeam, EnemyTeam enemyTeam) throws InterruptedException{
    // Fetch alive Characters from playerTeam
    ArrayList<PlayerCharacter> aliveCharacters = playerTeam.getAliveCharacters();
    resetSpecialAbilityCooldowns();
    int randomAbilityIndex = availableSpecialAbilityIndices.get((int)(Math.random() * availableSpecialAbilityIndices.size()));
    if(randomAbilityIndex == 0){
      // Poison Dart Volley automatically targets all characters. No special decision making needed.
      for(PlayerCharacter c : aliveCharacters){
        specialAbility(0, c, playerTeam, enemyTeam);
      }
    } else if(randomAbilityIndex == 1){
      // Pickpocket ability
    }
  }
  
  // Overrided battle methods
  // basicAbilityAI handles the decision making, while basicAbility handles the outcome of an ability itself.
  // Same for specialAbility()
  public ActionResult basicAbility(int basicAbilityIndex, BasicCharacter target, PlayerTeam playerTeam, EnemyTeam enemyTeam) throws InterruptedException{
    if(basicAbilityIndex == 0){
      // Poison Dart
      System.out.println(getName() + " shot a poison dart at " + target.getName() + " for " + getAttackStrength() + " HP!");
      boolean wasEnemyHit = handleEnemyDefense(target, getAttackStrength(), playerTeam, enemyTeam);
      if((int)(Math.random() * 100) < 25 && wasEnemyHit){
        StatusEffect.addStatusEffect(target, "Poison", 2);
      }
      System.out.println(target.getSimpleOutput());
    } else if(basicAbilityIndex == 1){
      // Nimble Dodge (shared with Goblin class since they are both speedy Goblin types)
      System.out.println(getName() + " prepared Nimble Dodge for the player's next turn!");
      StatusEffect.addStatusEffect(this, "Nimble", 1);
    }
    return getActionResult();
  }
  
  public ActionResult specialAbility(int specialAbilityIndex, BasicCharacter target, PlayerTeam playerTeam, EnemyTeam enemyTeam) throws InterruptedException{
    if(specialAbilityIndex == 0){
      // Poison Cloud
      System.out.println(getName() + " shot a poison cloud at " + target.getName() + " for " + (getAttackStrength()-5) + " HP!");
      handleEnemyDefense(target, getAttackStrength()-5, playerTeam, enemyTeam);
      Thread.sleep(1000);
      // 25% chance of Poison for 2 turns
      if((int)(Math.random() * 100) < 33){
        StatusEffect.addStatusEffect(target, "Poison", 2);
      }
    } else if(specialAbilityIndex == 1){
      // Poison Mark
      System.out.println(getName() + " marked " + target.getName() + " with poison for " + getAttackStrength() + " HP!");
      boolean wasEnemyHit = handleEnemyDefense(target, getAttackStrength(), playerTeam, enemyTeam);
      if((int)(Math.random() * 100) < 75 && wasEnemyHit){
        StatusEffect.addStatusEffect(target, "Poison", 3);
      }
    }
    System.out.println(target.getSimpleOutput());
    return getActionResult();
  }
  
  // Defense function which is called when an enemy targets the Knight
  public void defend(BasicCharacter target, double actualDamage){
    if(actualDamage == 0){
      System.out.println(getName() + " successfully hid from " + target.getName() + "'s attack!");
    }else if(getIsDefending()){
      System.out.println(getName() + " partially hid from " + target.getName() + "'s attack for " + getDefenseStrength() * 2 + " HP!");
    } else{
      System.out.println(getName() + " tried to hide from " + target.getName() + "'s attack for " + getDefenseStrength() + " HP!");
    }
  }
}
