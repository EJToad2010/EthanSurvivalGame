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

public class Goblin extends EnemyCharacter{
  // Items that the Goblin steals from the player. They are not used by the Goblin and will be returned on death.
  public Goblin(String name, String behaviorType){
    super(name, 60.0, 10.0, 4.0, 20.0, 50, 20, behaviorType);
    setDescription("A weak enemy focused on annoying the player and causing chaos.");
    addToArrayList(getBasicAbilityNames(), new String[]{"Rusty Dagger", "Nimble Dodge"});
    addToArrayList(getBasicAbilityDescriptions(), new String[]{"Deals a small amount of damage onto a single target. 10% chance to inflict bleed.",
                                                             "During the player's turn, all attacks toward the Goblin deal 25% less damage."});
    addToArrayList(getBasicAbilityTypes(), new String[]{"Offensive", "Offensive"});
    addToArrayList(getBasicAbilityEnemyCounts(), new Integer[]{1, 0});
    addToArrayList(getSpecialAbilityNames(), new String[]{"Taunt", "Pickpocket"});
    addToArrayList(getSpecialAbilityDescriptions(), new String[]{"Target a single character. The target cannot use an ability next turn.",
                                                               "Attack a single character for moderate damage. The Goblin has a 25% to steal gold from the player. The Goblin consumes the gold to heal."});
    addToArrayList(getSpecialAbilityTypes(), new String[]{"Offensive", "Offensive"});
    addToArrayList(getSpecialAbilityEnemyCounts(), new Integer[]{1, 1});
    addToArrayList(getSpecialAbilityCooldowns(), new Integer[]{3, 3});
    addToArrayList(getCurrentSpecialAbilityCooldowns(), new Integer[]{3, 3});
    setCharacterImage("src/Images/goblin.png");
  }
  
  // Overrided getType method
  public String getType(){
    return "Goblin";
  }

  public ActionResult basicAbilityAI(PlayerTeam playerTeam, EnemyTeam enemyTeam){
    // Fetch alive Characters from playerTeam
    ArrayList<PlayerCharacter> aliveCharacters = playerTeam.getAliveCharacters();
    // System.out.println("DEBUG: Goblin Basic Ability AI");
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
    // Use Rusty Dagger on a random Character if all other options are exhausted
    return basicAbility(0, aliveCharacters.get((int)(Math.random() * aliveCharacters.size())), playerTeam, enemyTeam);
  }

  public ActionResult basicAbilityAI(BasicCharacter preferredCharacter, PlayerTeam playerTeam, EnemyTeam enemyTeam){
    // System.out.println("DEBUG: Goblin aggressive AI");
    // Likely has aggressive AI behavior, so automatically use Rusty Dagger
    return basicAbility(0, preferredCharacter, playerTeam, enemyTeam);
  }

  // AI for deciding a special ability to use
  public ActionResult specialAbilityAI(ArrayList<Integer> availableSpecialAbilityIndices, PlayerTeam playerTeam, EnemyTeam enemyTeam){
    // Fetch alive Characters from playerTeam
    ArrayList<PlayerCharacter> aliveCharacters = playerTeam.getAliveCharacters();
    int randomAbilityIndex = availableSpecialAbilityIndices.get((int)(Math.random() * availableSpecialAbilityIndices.size()));
    if(randomAbilityIndex == 0){
      // Taunt ability
      // Choose either the player with the highest HP remaining or the highest attackStrength
      double highestHP = aliveCharacters.get(0).getCurrentHP();
      int highestHPIndex = 0;
      double highestAttack = aliveCharacters.get(0).getAttackStrength();
      int highestAttackIndex = 0;
      if((Math.random() * 100) < 50){
        // Choose highest HP
        for(int i = 0; i < aliveCharacters.size(); i++){
          if(aliveCharacters.get(i).getCurrentHP() > highestHP){
            highestHP = aliveCharacters.get(i).getCurrentHP();
            highestHPIndex = i;
          }
        }
        return specialAbility(0, aliveCharacters.get(highestHPIndex), playerTeam, enemyTeam);
      } else{
        // Choose highest Attack Strength
        for(int i = 0; i < aliveCharacters.size(); i++){
          if(aliveCharacters.get(i).getAttackStrength() > highestAttack){
            highestAttack = aliveCharacters.get(i).getAttackStrength();
            highestAttackIndex = i;
          }
        }
        return specialAbility(0, aliveCharacters.get(highestAttackIndex), playerTeam, enemyTeam);
      }
    } else if(randomAbilityIndex == 1){
      // Pickpocket ability
      // Target random player
      return specialAbility(1, aliveCharacters.get((int)(Math.random() * aliveCharacters.size())), playerTeam, enemyTeam);
    }
    return new ActionResult();
  }

  // Overrided battle methods
  // basicAbilityAI handles the decision making, while basicAbility handles the outcome of an ability itself.
  // Same for specialAbility()
  public ActionResult basicAbility(int basicAbilityIndex, BasicCharacter target, PlayerTeam playerTeam, EnemyTeam enemyTeam){
    ActionResult output = new ActionResult();
    if(basicAbilityIndex == 0){
      //System.out.println(getName() + " stabbed " + target.getName() + " with their dagger for " + getAttackStrength() + " HP!");
      output.add(getName() + " stabbed " + target.getName() + " with their dagger for " + getAttackStrength() + " HP!", Signals.ATTACK_PERFORMED, getAttackStrength());
      // Check for how much damage the attack did to the enemy
      ActionResult defenseResult = handleEnemyDefense(target, getAttackStrength(), playerTeam, enemyTeam);
      Double enemyHPChange = getAttackStrength() - defenseResult.getAmount(Signals.DEFENSE_PERFORMED);
      output.add(defenseResult);
      //System.out.println(target.getSimpleOutput());
      if((int)(Math.random() * 100) < 10 && enemyHPChange > 0){
        StatusEffect.addStatusEffect(target, "Bleed", 2);
      }
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
    if(specialAbilityIndex == 0){
      // Taunt
      //System.out.println(getName() + " taunted " + target.getName() + "!");
      output.add(getName() + " taunted " + target.getName() + "!");
      //Thread.sleep(1000);
      //System.out.println(target.getName() + " cannot use an ability next turn!");
      StatusEffect.addStatusEffect(target, "Taunt", 1);
    } else if(specialAbilityIndex == 1){
      // Pickpocket
      //System.out.println(getName() + " swiped at " + target.getName() + " for " + getAttackStrength()+5 + " HP!");
      output.add(getName() + " swiped at " + target.getName() + " for " + getAttackStrength()+5 + " HP!", Signals.ATTACK_PERFORMED, getAttackStrength()+5);
      output.add(handleEnemyDefense(target, getAttackStrength()+5, playerTeam, enemyTeam));
      if(playerTeam.getCoinBalance() > 0 && (int)(Math.random() * 100) < 25){
        int stolenCoins = (int)(Math.random() * 50) + 25;
        stolenCoins = Math.min(stolenCoins, playerTeam.getCoinBalance());
        //System.out.println(getName() + " pickpocketed " + stolenCoins + "g from your team!");
        output.add(getName() + " pickpocketed " + stolenCoins + "g from your team!");
        playerTeam.increaseCoinBalance(-stolenCoins);
        //Thread.sleep(1000);
        //System.out.println("Balance: "+playerTeam.getCoinBalance());
        //Thread.sleep(1000);
        //System.out.println(getName() + " ate the gold to heal " + (double)(stolenCoins)/2 + " HP!");
        output.add(getName() + " ate the gold to heal " + (double)(stolenCoins)/2 + " HP!", Signals.HEALTH_GAINED, (double)(stolenCoins)/2);
        //changeCurrentHP((double)(stolenCoins)/2);
        //System.out.println(getSimpleOutput());
      }
    }
    //ystem.out.println(target.getSimpleOutput());
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
