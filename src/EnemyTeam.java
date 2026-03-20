package src;
import java.util.ArrayList;

// Represents the main opponent, who controls multiple enemies and performs their actions
// This class and PlayerTeam share a lot of similar logic, but they are kept in separate classes
// because they deal with separate object types.
class EnemyTeam {
  // This ArrayList contains the raw enemy characters that are a part of the team.
  private ArrayList<EnemyCharacter> enemyTeam;
  // The EnemyTeam has one shared inventory.
  private Inventory enemyInventory;
  
  // Constructor that takes in an exisiting ArrayList and modifies its components into an enemy
  public EnemyTeam(ArrayList<EnemyCharacter> enemyTeam, Inventory enemyInventory){
    this.enemyTeam = enemyTeam;
    if(enemyTeam.size() > 0){
      convertAllToEnemy();
    }
    this.enemyInventory = enemyInventory;
  }
  
  // Constructor that creates an empty team
  public EnemyTeam(){
    this(new ArrayList<EnemyCharacter>(), new Inventory());
  }
  
  // Add an enemy
  // Convert the isEnemyCharacter automatically to true to prevent confusion
  // Manually keeping track of each enemy's xp and levels are too complicated.
  // Their stats and ability limits are artificially inflated based on the player team's highest level character
  public void addEnemy(EnemyCharacter c, int highestPlayerLevel, double avgPlayerLevel){
    enemyTeam.add(c);
    // Math.max prevents negative inflation of stats in the unlikely case that the highest player level is 0
    if(Math.random() < 0.5){
      // 50% chance of spawning near highest level
      c.inflateStats((int)(Math.random() * 2) + Math.max(0, highestPlayerLevel-1));
    } else{
      // 50% chance of spawning near average level
      c.inflateStats((int)(Math.random() * 2) + Math.max(0, (int)avgPlayerLevel));
    }
    convertAllToEnemy();
  }
  
  // Return the ArrayList
  public ArrayList<EnemyCharacter> getEnemyTeam(){
    return enemyTeam;
  }
  
  // Return the enemy inventory
  public Inventory getEnemyInventory(){
    return enemyInventory;
  }
  
  // The total of each player character's speed value
  // Used at the start of battle to decide which team goes first
  public double getTotalSpeed(){
    double sum = 0;
    for(EnemyCharacter e : enemyTeam){
      sum += e.getSpeed();
    }
    return sum;
  }
  
  // Return enemy's combined currentHP / maxHP as a double from 0.0 to 1.0
  public double getTotalHPPercentage(){
    double currentHPSum = 0;
    double maxHPSum = 0;
    for(EnemyCharacter e : enemyTeam){
      currentHPSum += e.getCurrentHP();
      maxHPSum += e.getMaxHP();
    }
    return currentHPSum / maxHPSum;
  }
  
  // Used in battle messages
  public String getEnemyTeamNumFormat(){
    String output = "";
    for(int i = 1; i <= enemyTeam.size(); i++){
      output += i + ": " + enemyTeam.get(i-1).getSimpleOutput();
      if(enemyTeam.get(i-1).getIsDead()){
        output += " [UNAVAILABLE]";
      }
      output += "\n";
    }
    return output;
  }
  
  // Used in battle messages
  // Has a blacklist of enemies in case they are already selected or they cannot be targeteds
  public String getEnemyTeamNumFormat(ArrayList<Integer> unacceptableInputs){
    String output = "";
    for(int i = 1; i <= enemyTeam.size(); i++){
      output += i + ": " + enemyTeam.get(i-1).getSimpleOutput();
      if(unacceptableInputs.contains(i-1)){
        output += " [UNAVAILABLE]";
      }
      output += "\n";
    }
    return output;
  }
  
  // ArrayList.get method
  public EnemyCharacter getCharacterAt(int index){
    return enemyTeam.get(index);
  }

  // Use THIS method for using an item as it handles quantity decreasing and
  // automatic removal when the ItemStack becomes empty.
  public void useItem(int itemIndex, BasicCharacter c, PlayerTeam playerTeam){
    ItemStack stack = enemyInventory.get(itemIndex);
    Item item = stack.getItem();
    item.useItem(c, playerTeam, this);
    stack.remove(1);
    if(stack.getQuantity() <= 0){
        enemyInventory.remove(stack);
    }
  }
  
  // Automatically convert all Characters to isEnemyCharacter = true to prevent future issues
  private void convertAllToEnemy(){
    for(EnemyCharacter c : enemyTeam){
      c.setIsEnemyCharacter(true);
    }
  }
  
  // Reset each Enemy's defense to passive defense whenever a new turn begins
  public void resetEnemyDefense(){
    for(EnemyCharacter c : enemyTeam){
      c.setIsDefending(false);
    }
  }

  // Decrease each Enemy's special ability cooldown timers whenever a new turn begins
  public void decreaseEnemyCooldowns(){
    for(EnemyCharacter c : enemyTeam){
      c.decreaseSpecialAbilityCooldowns();
    }
  }
  
  // Reset each Enemy's special ability cooldown timer to their default value
  public void resetEnemyCooldowns(){
    for(EnemyCharacter c : enemyTeam){
      c.resetSpecialAbilityCooldowns();
    }
  }
  
  // Erase the enemy team
  public void clearEnemyTeam(){
    enemyTeam.clear();
    enemyInventory.clear();
  }
  
  // Get the number of Enemy Characters that are alive
  public int getNumAlive(){
    int alive = 0;
    for(EnemyCharacter c : enemyTeam){
      if(!c.getIsDead()){
        alive++;
      }
    }
    return alive;
  }
  
  // To String method prints all character objects the enemy controls
  public String toString(){
    String output = "ENEMY'S TEAM:\n";
    for(EnemyCharacter c : enemyTeam){
      output += c.toString();
      output += "\n";
    }
    return output;
  }
}
