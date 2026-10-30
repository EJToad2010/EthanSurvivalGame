package src.Characters.EnemyCharacters;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.awt.Graphics;

import src.Characters.BasicCharacter;
import src.Characters.EnemyCharacter;
import src.Characters.PlayerCharacter;
import src.GameManagement.Mechanics.ActionResult;
import src.GameManagement.Mechanics.Signals;
import src.GameManagement.UI.ImageManager;
import src.Misc.StatusEffect;
import src.Teams.EnemyTeam;
import src.Teams.PlayerTeam;

public class Skeleton extends EnemyCharacter{
  // Rendered during character animations

  public Skeleton(String name, String behaviorType){
    // Set the names, descriptions, and cooldowns of the Dart Goblin.
    super(name, 70.0, 9.0, 6.0, 8.0, 50, 20, behaviorType);
    setDescription("An undead creature that lost its muscular strength but is tough as bone.");
    addToArrayList(getBasicAbilityNames(), new String[]{"Bone Chuck", ""});
    addToArrayList(getBasicAbilityDescriptions(), new String[]{"Deals a small amount of damage to a single target.",
                                                             ""});
    addToArrayList(getBasicAbilityTypes(), new String[]{"Offensive", "Offensive"});
    addToArrayList(getBasicAbilityEnemyCounts(), new Integer[]{1, 0});
    addToArrayList(getBasicAbilityAnimationLengths(), new Integer[]{0, 0});
    addToArrayList(getSpecialAbilityNames(), new String[]{"Calcify", "Poison Mark"});
    addToArrayList(getSpecialAbilityDescriptions(), new String[]{"Reduce 33% of all incoming danage for the next 2 turns.",
                                                               ""});
    addToArrayList(getSpecialAbilityTypes(), new String[]{"Offensive", "Offensive"});
    addToArrayList(getSpecialAbilityEnemyCounts(), new Integer[]{0, 1});
    addToArrayList(getSpecialAbilityCooldowns(), new Integer[]{4, 3});
    addToArrayList(getCurrentSpecialAbilityCooldowns(), new Integer[]{2, 3});
    addToArrayList(getSpecialAbilityAnimationLengths(), new Integer[]{0, 0});
    setCharacterImage("src/Images/skeleton.png");
    setSize(160,200);
  }
  
  // Overrided getType method
  public String getType(){
    return "Skeleton";
  }

  // AI for deciding what basic ability to use
  public ActionResult basicAbilityAI(PlayerTeam playerTeam, EnemyTeam enemyTeam){
    // Fetch alive Characters from playerTeam
    ArrayList<PlayerCharacter> aliveCharacters = playerTeam.getAliveCharacters();
    // System.out.println("DEBUG: Dart Goblin basic ability AI");
    int basicAbilityLimit = getBasicAbilityLimit();
    if(basicAbilityLimit >= 1){
      
    }
    // Use Bone Chuck on a random Character if all other options are exhausted
    return basicAbility(0, aliveCharacters.get((int)(Math.random() * aliveCharacters.size())), playerTeam, enemyTeam);
  }

  // This method is used instead of the original if the EnemyCharacter AI finds a specific Character that is prioritized most
  public ActionResult basicAbilityAI(BasicCharacter preferredCharacter, PlayerTeam playerTeam, EnemyTeam enemyTeam){
    // Likely has defensive AI behavior, so automatically use Bone Chuck
    return basicAbility(0, preferredCharacter, playerTeam, enemyTeam);
  }

  // AI for deciding a special ability to use
  public ActionResult specialAbilityAI(ArrayList<Integer> availableSpecialAbilityIndices, PlayerTeam playerTeam, EnemyTeam enemyTeam){
    ActionResult output = new ActionResult();
    // Fetch alive Characters from playerTeam
    ArrayList<PlayerCharacter> aliveCharacters = playerTeam.getAliveCharacters();
    resetSpecialAbilityCooldowns();
    int randomAbilityIndex = availableSpecialAbilityIndices.get((int)(Math.random() * availableSpecialAbilityIndices.size()));
    if(randomAbilityIndex == 0){
      // Calcify automatically targets itself. No special decision making needed.
      output.add(specialAbility(0, this, playerTeam, enemyTeam));
    } else if(randomAbilityIndex == 1){
      // Poison mark
      output.add(specialAbility(1, aliveCharacters.get((int)(Math.random() * aliveCharacters.size())), playerTeam, enemyTeam));
    }
    return output;
  }
  
  // Overrided battle methods
  // basicAbilityAI handles the decision making, while basicAbility handles the outcome of an ability itself.
  // Same for specialAbility()
  public ActionResult basicAbility(int basicAbilityIndex, BasicCharacter target, PlayerTeam playerTeam, EnemyTeam enemyTeam){
    ActionResult output = new ActionResult();
    output.add(getCharInfoSignals(target, 0, basicAbilityIndex));
    if(basicAbilityIndex == 0){
      // Bone Chuck
      output.add(getName() + " chucked a bone at " + target.getName() + " for " + getAttackStrength() + " HP!", Signals.ATTACK_PERFORMED, getAttackStrength());
      // Check for how much damage the attack did to the enemy
      ActionResult defenseResult = handleEnemyDefense(target, getAttackStrength(), playerTeam, enemyTeam);
      output.add(defenseResult);
      //System.out.println(target.getSimpleOutput());
    } else if(basicAbilityIndex == 1){
      // Nimble Dodge (shared with Goblin class since they are both speedy Goblin types)
      
    }
    return output;
  }
  
  public ActionResult specialAbility(int specialAbilityIndex, BasicCharacter target, PlayerTeam playerTeam, EnemyTeam enemyTeam){
    ActionResult output = new ActionResult();
    output.add(getCharInfoSignals(target, 1, specialAbilityIndex));
    if(specialAbilityIndex == 0){
      // Calcify
      output.add(getName() + " is calcifying its bones for the next 2 turns!");
      StatusEffect.addStatusEffect(this, "Calcified", 2);
    } else if(specialAbilityIndex == 1){
      // Poison Mark
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

  // Called every time the Character conducts an offensive attack
  public void drawAttackAnimation(BasicCharacter target, String abilityType, int abilityIndex, Graphics graphics, int tick){
    int localX = getX();
    if(abilityType.equals("Basic ability")){
      if(abilityIndex == 0){
        // 
      } else if(abilityIndex == 1){
        // 
      }
    } else{
      if(abilityIndex == 0){
        //
      } else if(abilityIndex == 1){
        //
      }
    }
    drawCharImage(graphics, localX, getY());
    drawHPBar(graphics, localX, getY());
    drawCharText(graphics, localX, getY());
  }
}
