package src.Characters.PlayerCharacters;
import java.io.IOException;
import java.util.Scanner;

import src.Characters.BasicCharacter;
import src.Characters.PlayerCharacter;
import src.GameManagement.GameManager;
import src.Misc.StatusEffect;
import src.Teams.EnemyTeam;
import src.Teams.PlayerTeam;
public class Archer extends PlayerCharacter {
  // Used in a precision minigame
  private Scanner s = new Scanner(System.in);
  public Archer(String name){
    super(name, 90.0, 20.0, 10.0, 30.0);
    // Set the names, descriptions, and cooldowns of all the Archer's abilities.
    setDescription("A quick fighter who can attack multiple enemies and control the battlefield.");
    addToArrayList(getBasicAbilityNames(), new String[]{"Softening Arrow", "Double Shot"});
    addToArrayList(getBasicAbilityDescriptions(), new String[]{"A single ranged attack that has a 33% chance to reduce the enemy's attack strength for 2 turns.",
                                                             "A slightly weaker attack that targets two enemies."});
    addToArrayList(getBasicAbilityTypes(), new String[]{"Offensive", "Offensive"});
    addToArrayList(getBasicAbilityUnlockLevels(), new Integer[]{0, 3});
    addToArrayList(getBasicAbilityEnemyCounts(), new Integer[]{1, 2});
    addToArrayList(getSpecialAbilityNames(), new String[]{"Volley", "Armor Piercer"});
    addToArrayList(getSpecialAbilityDescriptions(), new String[]{"Fires an arrow at every enemy, dealing moderate damage. Each enemy has a 10% chance of being burned for 1 turn.",
                                                               "Deals moderate damage to a single target. The enemy has a 50% chance of receiving massively reduced defensive strength for 2 turns."});
    addToArrayList(getSpecialAbilityTypes(), new String[]{"Offensive", "Offensive"});
    addToArrayList(getSpecialAbilityUnlockLevels(), new Integer[]{0, 4});
    addToArrayList(getSpecialAbilityEnemyCounts(), new Integer[]{999, 1});
    addToArrayList(getSpecialAbilityCooldowns(), new Integer[]{2, 2});
    addToArrayList(getCurrentSpecialAbilityCooldowns(), new Integer[]{2, 2});
    setCharacterImage("src/Images/archer.png");
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
      boolean wasEnemyHit = handleEnemyDefense(target, getAttackStrength(), playerTeam, enemyTeam);
      if((int)(Math.random() * 100) < 33 && wasEnemyHit){
        StatusEffect.addStatusEffect(target, "Soft", 2);
      }
      System.out.println(target.getSimpleOutput());
    } else if(basicAbilityIndex == 1){
      // Double Shot
      System.out.println(getName() + " fired an arrow at " + target.getName() + " for " + (getAttackStrength() - 3) + " HP!");
      handleEnemyDefense(target, getAttackStrength() - 3, playerTeam, enemyTeam);
      System.out.println(target.getSimpleOutput());
    }
  }
  
  // Archer has two special attacks to choose from
  public void specialAbility(int specialAbilityIndex, BasicCharacter target, PlayerTeam playerTeam, EnemyTeam enemyTeam) throws InterruptedException{
    if(specialAbilityIndex == 0){
      // Volley
      System.out.println(getName() + " fired an arrow at " + target.getName() + " for " + getAttackStrength() + " HP!");
      boolean wasEnemyHit = handleEnemyDefense(target, getAttackStrength(), playerTeam, enemyTeam);
      if((int)(Math.random() * 100) < 10 && wasEnemyHit){
        StatusEffect.addStatusEffect(target, "Burn", 1);
      }
    } else if(specialAbilityIndex == 1){
      // Crippling Arrow
      // 50% chance of receiving PIERCE status effect regardless of if they received any damage
      System.out.println(getName() + " fired an armor piercing arrow at " + target.getName() + " for " + (getAttackStrength()+5) + " HP!");
      handleEnemyDefense(target, getAttackStrength()+5, playerTeam, enemyTeam);
      if((int)(Math.random() * 100) < 50){
        StatusEffect.addStatusEffect(target, "Pierce", 2);
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

  // A precision minigame that is not implemented yet
  // The user must aim their shot towards the center of the bar
  // Returns how many spaces the user was away from the "perfect" spot, the center of the bar
  public int aimMinigame(int barSize) throws IOException, InterruptedException{
    int pos = 0;
    boolean isIncreasing = true;
    while(true){
      if(isIncreasing){
        pos++;
      }else{
        pos--;
      }
      if(pos>barSize-1){
        pos=barSize-1;
        pos--;
        isIncreasing = false;
      } else if(pos < 0){
        pos = 0;
        pos++;
        isIncreasing = true;
      }
      GameManager.clearScreen();
      System.out.println("Aim for the center of the bar!");
      System.out.println("Press ENTER to shoot!");
      System.out.println(printBar(pos, barSize));
      if(System.in.available() > 0){
        s.nextLine();
        return Math.abs(pos-(barSize/2));
      }
      Thread.sleep(50);
    }
  }
  
  // Prints the bar, as well as where the user's aimpoint currently is
  // EX: [---o-|-|-|-----]
  private String printBar(int pos, int barSize){
    int center = (barSize/2);
    String output = "";
    output += "[";
    for(int i = 0; i < barSize; i++){
      if(i == pos){
        output += "o";
      } else if(i == center){
        output += "|";
      } else if(i == center-2 || i == center+2){
        output += "|";
      } else{
        output += "-";
      }
    }
    output += "]";
    return output;
  }
}
