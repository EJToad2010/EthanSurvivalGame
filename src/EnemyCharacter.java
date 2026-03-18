package src;
import java.util.ArrayList;
// EnemyCharacters have an xpReward and coinReward parameter and have adjusted behaviors when attacking players.
// This object should never be created on its own. Only its subclasses will ever be used.
class EnemyCharacter extends BasicCharacter{
  // Define EnemyCharacter specific attributes
  private int xpReward;
  private int coinReward;
  private int basicAbilityLimit = 0;
  private int specialAbilityLimit = 0;
  private boolean hasTakenTurn;
  
  // Possible behaviorTypes:
  // RANDOM, AGGRESSIVE, DEFENSIVE
  private String behaviorType;
  
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
    changeAttackStrength(5 * playerLevel);
    changeDefenseStrength(2 * playerLevel);
    changeSpeed(2 * playerLevel);
    xpReward *= (int) Math.pow(1.25, Math.max(playerLevel-1, 0));
    coinReward *= (int) Math.pow(2, Math.max(playerLevel-1, 0));
    basicAbilityLimit += Math.min(getBasicAbilityNames().size(), playerLevel / 2);
    specialAbilityLimit += Math.min(getBasicAbilityNames().size(), playerLevel / 2);
  }
  
  // Main function helping one enemy decide what action to perform in one turn
  public void takeTurn(PlayerTeam playerTeam, EnemyTeam enemyTeam) throws InterruptedException{
    hasTakenTurn = false;
    // Prioritize using an item
    itemAI(playerTeam, enemyTeam);
    // Prioritize using an available special attack
    ArrayList<Integer> availableSpecialAbilityIndices = getAvailableSpecialAbilityIndices();
    if(availableSpecialAbilityIndices.size() > 0 && (Math.random() * 100) < 50){
      specialAbilityAI(availableSpecialAbilityIndices, playerTeam, enemyTeam);
      // System.out.println("DEBUG: Special Ability Used");
      hasTakenTurn = true;
      resetSpecialAbilityCooldowns();
    }
    
    // Use different behavior patterns for an enemy's defense and basic abilities
    if(!hasTakenTurn){
      if(behaviorType.equals("AGGRESSIVE")){
        aggressive(playerTeam, enemyTeam);
      } else if(behaviorType.equals("DEFENSIVE")){
        defensive(playerTeam, enemyTeam);
      } else if(behaviorType.equals("RANDOM")){
        random(playerTeam, enemyTeam);
      } else{
        System.out.println("Invalid behavior type.");
      }
    }
  }
  
  // Check the EnemyCharacter's shared inventory for situational items
  private void itemAI(PlayerTeam playerTeam, EnemyTeam enemyTeam){
    Inventory enemyInventory = enemyTeam.getEnemyInventory();
    // Selfish AI prioritizes self-heal over team heal
    // 50% chance to use either item if remaining HP is under 50%
    for(int i = 0; i < enemyInventory.getInventory().size(); i++){
      if(enemyInventory.get(i).getItem() instanceof HealthPotion){
        if(getCurrentHP() / getMaxHP() < 0.5 && (int)(Math.random()) * 100 < 50){
          enemyTeam.useItem(i, this, playerTeam);
          hasTakenTurn = true;
          // System.out.println("DEBUG: Health Potion Used");
          break;
        }
      } else if(enemyInventory.get(i).getItem() instanceof HealthPool){
        if(enemyTeam.getTotalHPPercentage() < 0.5 && (int)(Math.random()) * 100 < 50){
          enemyTeam.useItem(i, this, playerTeam);
          hasTakenTurn = true;
          // System.out.println("DEBUG: Health Pool Used");
          break;
        }
      }
    }
  }
  
  // Intended to be overridden by subclasses since each class has unique basic / special abilities
  public void basicAbilityAI(PlayerTeam playerTeam, EnemyTeam enemyTeam) throws InterruptedException{}
  // Perform decision-making, provided a target is clearly defined
  public void basicAbilityAI(BasicCharacter preferredCharacter, PlayerTeam playerTeam, EnemyTeam enemyTeam) throws InterruptedException{}
  // Perform decision-making on special abilities
  public void specialAbilityAI(ArrayList<Integer> availableSpecialAbilityIndices, PlayerTeam playerTeam, EnemyTeam enemyTeam) throws InterruptedException{}
  
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
  private void aggressive(PlayerTeam playerTeam, EnemyTeam enemyTeam) throws InterruptedException {
    // Attack the member of playerTeam with the lowest health
    PlayerCharacter lowestPlayer = playerTeam.getPlayerTeam().get(0);
    double lowestHealth = lowestPlayer.getCurrentHP();
    for(PlayerCharacter pc : playerTeam.getPlayerTeam()){
      if(pc.getCurrentHP() < lowestHealth){
        lowestPlayer = pc;
        lowestHealth = pc.getCurrentHP();
      }
    }
    basicAbilityAI(lowestPlayer, playerTeam, enemyTeam);
  }
  
  // Prioritize staying alive and safe over offensive abilities
  private void defensive(PlayerTeam playerTeam, EnemyTeam enemyTeam) throws InterruptedException{
    // Focus on staying defended / healing
    if((int)(Math.random() * 100) < 20){
      // Attack a random player
      basicAbilityAI(playerTeam, enemyTeam);
    } else{
      if((int)(Math.random() * 100) < 60){
        setIsDefending(true);
      } else{
        basicAbilityAI(playerTeam, enemyTeam);
      }
    }
  }
  
  // Randomly attack or defend with no "smart" decision-making
  private void random(PlayerTeam playerTeam, EnemyTeam enemyTeam) throws InterruptedException{
    if((int)(Math.random() * 100) < 60){
      basicAbilityAI(playerTeam, enemyTeam);
    } else{
      setIsDefending(true);
    }
  }
}
