package src;
import java.util.ArrayList;

// Represents the user, who controls multiple characters and performs their actions
// It only has one instance variable, but it is its own class due to the specialized methods needed to modify
// all relevant PlayerCharacters
class PlayerTeam {
  // One main variable, which stores all characters that the player currently controls
  private ArrayList<PlayerCharacter> playerTeam;
  private Inventory playerInventory;
  
  // How many coins the player's team currently has
  private int coinBalance = 0;
  
  // Constructor that takes in an exisiting ArrayList and modifies its components into a player
  public PlayerTeam(ArrayList<PlayerCharacter> playerTeam, Inventory playerInventory){
    this.playerTeam = playerTeam;
    if(playerTeam.size() > 0){
      convertAllToPlayer();
    }
    this.playerInventory = playerInventory;
  }
  
  // Constructor that creates an empty team
  public PlayerTeam(){
    this(new ArrayList<PlayerCharacter>(), new Inventory());
  }
  
  // Add a player
  // Convert the isEnemyCharacter automatically to false to prevent confusion
  public void addCharacter(PlayerCharacter c){
    playerTeam.add(c);
    convertAllToPlayer();
  }
  
  // Return the ArrayList
  public ArrayList<PlayerCharacter> getPlayerTeam(){
    return playerTeam;
  }
  
  // Return the inventory
  public Inventory getPlayerInventory(){
    return playerInventory;
  }
  
  // Balance of player team's combined money
  public int getCoinBalance(){
    return coinBalance;
  }
  
  // The total of each player character's speed value
  // Used at the start of battle to decide which team goes first
  public double getTotalSpeed(){
    double sum = 0;
    for(PlayerCharacter p : playerTeam){
      sum += p.getSpeed();
    }
    return sum;
  }
  
  // Find the PlayerCharacter with the highest level
  public int getHighestLevel(){
    int highest = playerTeam.get(0).getLevel();
    for(PlayerCharacter p : playerTeam){
      if(p.getLevel() > highest){
        highest = p.getLevel();
      }
    }
    return highest;
  }
  
  // Used in battle messages
  public String getPlayerTeamNumFormat(){
    String output = "";
    for(int i = 1; i <= playerTeam.size(); i++){
      output += i + ": " + playerTeam.get(i-1).getSimpleOutput();
      if(playerTeam.get(i-1).getIsDead()){
        output += " [UNAVAILABLE]";
      }
      output += "\n";
    }
    return output;
  }
  
  // Print a list of a given PlayerCharacter's basic abilities
  // EX:
  // 1: Basic Ability 1
  // 2: Basic Ability 2
  // boolean includeLockedAbilities
  public String getBasicAbilityNamesNumFormat(PlayerCharacter p, boolean includeLockedAbilities){
    int limit;
    if(includeLockedAbilities){
      limit = p.getSpecialAbilityNames().size();
    } else{
      limit = p.getHighestIndexBasic() + 1;
    }
    String output = "";
    for(int i = 1; i <= limit; i++){
      output += i + ": " + p.getBasicAbilityNames().get(i-1);
      output += "\n";
    }
    return output;
  }
  
  // boolean includeLockedAbilities
  // Print a list of a given PlayerCharacter's special abilities
  public String getSpecialAbilityNamesNumFormat(PlayerCharacter p, boolean includeLockedAbilities){
    String output = "";
    int limit;
    if(includeLockedAbilities){
      limit = p.getSpecialAbilityNames().size();
    } else{
      limit = p.getHighestIndexSpecial() + 1;
    }
    for(int i = 1; i <= limit; i++){
      output += i + ": " + p.getSpecialAbilityNames().get(i-1);
      output += " (Cooldown: " + p.getCurrentSpecialAbilityCooldowns().get(i-1) + " turns)";
      if(p.getCurrentSpecialAbilityCooldowns().get(i-1) > 0){
        output += " [UNAVAILABLE]";
      }
      output += "\n";
    }
    return output;
  }
  
  // ArrayList .get method
  public PlayerCharacter getCharacterAt(int index){
    return playerTeam.get(index);
  }

  public void useItem(int itemIndex, BasicCharacter c, EnemyTeam enemyTeam){
    ItemStack stack = playerInventory.get(itemIndex);
    Item item = stack.getItem();
    item.useItem(c, this, enemyTeam);
    stack.remove(1);
    if(stack.getQuantity() <= 0){
        playerInventory.remove(stack);
    }
  }
  
  // Automatically convert all Characters to isEnemyCharacter = false to prevent future issues
  private void convertAllToPlayer(){
    for(PlayerCharacter c : playerTeam){
      c.setIsEnemyCharacter(false);
    }
  }
  
  public void increaseCoinBalance(int amount){
    coinBalance += amount;
  }
  
  // Reset each Player's defense to passive defense whenever a new turn begins
  public void resetPlayerDefense(){
    for(PlayerCharacter c : playerTeam){
      c.setIsDefending(false);
    }
  }
  
  // Decrease each Player's special ability cooldown timers whenever a new turn begins
  public void decreasePlayerCooldowns(){
    for(PlayerCharacter c : playerTeam){
      c.decreaseSpecialAbilityCooldowns();
    }
  }
  
  // Reset each Player's special ability cooldown timer to their default value
  public void resetPlayerCooldowns(){
    for(PlayerCharacter c : playerTeam){
      c.resetSpecialAbilityCooldowns();
    }
  }
  
  // Erase the Player's team
  // Likely will not be used
  public void clearPlayerTeam(){
    playerTeam.clear();
  }
  
  // Get the number of Player Characters that are alive
  public int getNumAlive(){
    int alive = 0;
    for(PlayerCharacter c : playerTeam){
      if(!c.getIsDead()){
        alive++;
      }
    }
    return alive;
  }
  
  // To String method prints all character objects the user controls
  public String toString(){
    String output = "PLAYER'S TEAM:\n";
    for(PlayerCharacter c : playerTeam){
      output += c.toString();
      output += "\n";
    }
    return output;
  }
}
