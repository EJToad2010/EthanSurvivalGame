package src.Characters;
import java.util.ArrayList;

import src.GameManagement.Game;
import src.GameManagement.Mechanics.ActionResult;
import src.GameManagement.UI.DialogManager;
import src.ItemManager.Inventory;
import src.ItemManager.Items.HealthPool;
import src.ItemManager.Items.HealthPotion;
import src.Teams.EnemyTeam;
import src.Teams.PlayerTeam;
// EnemyCharacters have an xpReward and coinReward parameter and have adjusted behaviors when attacking players.
// This object should never be created on its own. Only its subclasses will ever be used.
// TODO: Implement GoblinTank EnemyCharacter subclass
public class EnemyCharacter extends BasicCharacter{
  // Define EnemyCharacter specific attributes
  private int xpReward;
  private int coinReward;
  private int basicAbilityLimit = 0;
  private int specialAbilityLimit = 0;
  private boolean hasTakenTurn;
  
  // Possible behaviorTypes:
  // RANDOM, AGGRESSIVE, DEFENSIVE
  private String behaviorType;

  // All of these variables are defined in the same method
  // The instance of Game is obtained alongside the enemyTeam
  protected Game game;
   // The instance of dialog used by the current game state that is open
  protected DialogManager dialogManager;
  
  // Constructor used if all parameters are given (used by subclasses)
  public EnemyCharacter(String name, double maxHP, double attackStrength, double defenseStrength, double speed, int xpReward, int coinReward, String behaviorType){
    super(name, maxHP, attackStrength, defenseStrength, speed);
    this.xpReward = xpReward;
    this.coinReward = coinReward;
    this.behaviorType = behaviorType;
    setDescription("A basic enemy character.");
  }
  
  // Constructor used if only a name is provided
  public EnemyCharacter(String name){
    this(name, 100, 25.0, 5.0, 5.0, 50, 20, "RANDOM");
  }
  
  // Constructor used if only a name and behaviorType are provided
  public EnemyCharacter(String name, String behaviorType){
    this(name, 100, 25.0, 5.0, 5.0, 50, 20, behaviorType);
  }
  
  // Getter methods

  public int getXPReward(){
    return xpReward;
  }
  
  public int getCoinReward(){
    return coinReward;
  }

  protected int getBasicAbilityLimit(){
    return basicAbilityLimit;
  }

  protected int getSpecialAbilityLimit(){
    return specialAbilityLimit;
  }

  // Update if an EnemyCharacter has taken a turn yet
  protected void setHasTakenTurn(boolean newValue){
    hasTakenTurn = newValue;
  }
  
  // Instead of creating a unique leveling and xp system for each enemy,
  // adjust its base stats to a similar value as the player's level
  // Saves time, processing power, and improves simplicity
  public void inflateStats(int playerLevel){
    // +5 MAX HP, +3 ATK, +2 DEF, +2 SPD per playerLevel
    changeMaxHP(5 * playerLevel);
    changeCurrentHP(5 * playerLevel);
    changeAttackStrength(2 * playerLevel);
    changeDefenseStrength(2 * playerLevel);
    changeSpeed(2 * playerLevel);
    xpReward *= (int) Math.pow(1.25, Math.max(playerLevel-1, 0));
    coinReward *= (int) Math.pow(1.5, Math.max(playerLevel-1, 0));
    basicAbilityLimit += Math.min(getBasicAbilityNames().size(), playerLevel / 2);
    specialAbilityLimit += Math.min(getBasicAbilityNames().size(), playerLevel / 2);
  }
  
  // Main function helping one enemy decide what action to perform in one turn
  public ActionResult takeTurn(PlayerTeam playerTeam, EnemyTeam enemyTeam){
    ActionResult output = new ActionResult();
    hasTakenTurn = false;
    // Prioritize using an item
    output.add(itemAI(playerTeam, enemyTeam));
    // Prioritize using an available special attack
    ArrayList<Integer> availableSpecialAbilityIndices = getAvailableSpecialAbilityIndices();
    if(availableSpecialAbilityIndices.size() > 0 && (Math.random() * 100) < 50){
      output.add(specialAbilityAI(availableSpecialAbilityIndices, playerTeam, enemyTeam));
      // System.out.println("DEBUG: Special Ability Used");
      hasTakenTurn = true;
      resetSpecialAbilityCooldowns();
    }
    
    // Use different behavior patterns for an enemy's defense and basic abilities
    if(!hasTakenTurn){
      if(behaviorType.equals("AGGRESSIVE")){
        output.add(aggressive(playerTeam, enemyTeam));
      } else if(behaviorType.equals("DEFENSIVE")){
        output.add(defensive(playerTeam, enemyTeam));
      } else if(behaviorType.equals("RANDOM")){
        output.add(random(playerTeam, enemyTeam));
      } else{
        System.out.println("Invalid behavior type.");
      }
    }
    return output;
  }
  
  // Check the EnemyCharacter's shared inventory for situational items
  private ActionResult itemAI(PlayerTeam playerTeam, EnemyTeam enemyTeam){
    ActionResult output = new ActionResult();
    Inventory enemyInventory = enemyTeam.getEnemyInventory();
    // Selfish AI prioritizes self-heal over team heal
    // 50% chance to use either item if remaining HP is under 50%
    for(int i = 0; i < enemyInventory.getInventory().size(); i++){
      if(enemyInventory.get(i).getItem() instanceof HealthPotion){
        if(getCurrentHP() / getMaxHP() < 0.5 && (int)(Math.random()) * 100 < 50){
          output.add(enemyTeam.useItem(i, this, playerTeam));
          hasTakenTurn = true;
          // System.out.println("DEBUG: Health Potion Used");
          break;
        }
      } else if(enemyInventory.get(i).getItem() instanceof HealthPool){
        if(enemyTeam.getTotalHPPercentage() < 0.5 && (int)(Math.random()) * 100 < 50){
          output.add(enemyTeam.useItem(i, this, playerTeam));
          hasTakenTurn = true;
          // System.out.println("DEBUG: Health Pool Used");
          break;
        }
      }
    }
    return output;
  }
  
  // Intended to be overridden by subclasses since each class has unique basic / special abilities
  public ActionResult basicAbilityAI(PlayerTeam playerTeam, EnemyTeam enemyTeam){return null;}
  // Perform decision-making, provided a target is clearly defined
  public ActionResult basicAbilityAI(BasicCharacter preferredCharacter, PlayerTeam playerTeam, EnemyTeam enemyTeam) {return null;}
  // Perform decision-making on special abilities
  public ActionResult specialAbilityAI(ArrayList<Integer> availableSpecialAbilityIndices, PlayerTeam playerTeam, EnemyTeam enemyTeam) {return null;}
  
  // Helper class that returns an ArrayList of special abilities that can be used in the current turn (cooldown of 0)
  private ArrayList<Integer> getAvailableSpecialAbilityIndices(){
    ArrayList<Integer> availableSpecialAbilityIndices = new ArrayList<Integer>();
    for(int i = 0; i < Math.min(specialAbilityLimit+1, getCurrentSpecialAbilityCooldowns().size()); i++){
      if(getCurrentSpecialAbilityCooldowns().get(i) == 0){
        availableSpecialAbilityIndices.add(i);
      }
    }
    return availableSpecialAbilityIndices;
  }
  
  // Prioritizes killing weaker units.
  // Uses maximal aggression and no defense
  private ActionResult aggressive(PlayerTeam playerTeam, EnemyTeam enemyTeam)  {
    // Fetch alive Characters from playerTeam
    ArrayList<PlayerCharacter> aliveCharacters = playerTeam.getAliveCharacters();
    // Attack the member of playerTeam with the lowest health
    PlayerCharacter lowestPlayer = aliveCharacters.get(0);
    double lowestHealth = lowestPlayer.getCurrentHP();
    for(PlayerCharacter pc : aliveCharacters){
      if(pc.getCurrentHP() < lowestHealth){
        lowestPlayer = pc;
        lowestHealth = pc.getCurrentHP();
      }
    }
    return basicAbilityAI(lowestPlayer, playerTeam, enemyTeam);
  }
  
  // Prioritize staying alive and safe over offensive abilities
  private ActionResult defensive(PlayerTeam playerTeam, EnemyTeam enemyTeam) {
    // Focus on staying defended / healing
    if((int)(Math.random() * 100) < 20){
      // Attack a random player
      return basicAbilityAI(playerTeam, enemyTeam);
    } else{
      if((int)(Math.random() * 100) < 60){
        setIsDefending(true);
        return new ActionResult();
      } else{
        return basicAbilityAI(playerTeam, enemyTeam);
      }
    }
  }
  
  // Randomly attack or defend with no "smart" decision-making
  private ActionResult random(PlayerTeam playerTeam, EnemyTeam enemyTeam) {
    if((int)(Math.random() * 100) < 60){
      return basicAbilityAI(playerTeam, enemyTeam);
    } else{
      setIsDefending(true);
      return new ActionResult();
    }
  }
}
