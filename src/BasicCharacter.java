package src;
import java.util.ArrayList;

// Represents a default Character with generic attributes, acting as a superclass for all character types
// Used by both the user's characters and the enemy's characters
// Contains default behaviors within battles

// This object should never be created on its own. Only its subclasses will ever be used.
public class BasicCharacter {
  // Initialize generic attributes for a Character that can be placed into battle
  // Parameters that must be provided and appended manually by the user or subclasses
  private String name;
  private String description;
  private String passiveAbilityName = "";
  private String passiveAbilityDescription = "";
  // Names and descriptions that allow for a Character to have multiple basic abilities
  private ArrayList<String> basicAbilityNames = new ArrayList<String>();
  private ArrayList<String> basicAbilityDescriptions = new ArrayList<String>();
  // How many enemies each basic ability targets. This must be known in the main gameplay loop and user prompts.
  private ArrayList<Integer> basicAbilityEnemyCounts = new ArrayList<Integer>();
  // The type of basic ability (EX: offensive, defensive)
  private ArrayList<String> basicAbilityTypes = new ArrayList<String>();
  // Names and descriptions that allow for a Character to have multiple special abilities
  private ArrayList<String> specialAbilityNames = new ArrayList<String>();
  private ArrayList<String> specialAbilityDescriptions = new ArrayList<String>();
  // How many enemies each special ability targets. This must be known in the main gameplay loop and user prompts.
  private ArrayList<Integer> specialAbilityEnemyCounts = new ArrayList<Integer>();
  // Allows for each initialized special ability to have a different cooldown,
  // which impacts how often it can be used
  private ArrayList<Integer> specialAbilityCooldowns = new ArrayList<Integer>();
  private ArrayList<Integer> currentSpecialAbilityCooldowns = new ArrayList<Integer>();
  private ArrayList<String> specialAbilityTypes = new ArrayList<String>();
  
  // Core parameters related to essential interactions in battle
  private double maxHP;
  private double currentHP;
  private double attackStrength;
  private double defenseStrength;
  // Change healStrength to speed
  private double speed;
  
  // This variable is automatically modified if a Character is added to either
  // a Player team or an Enemy team
  private boolean isEnemyCharacter = false;
  
  // Defense works differently because it can either be passive or active depending on the character's actions that turn
  private boolean isDefending = false;
  
  // Use overloaded Constructors to create flexible types of default Characters
  
  // Constructor used when all necessary attributes are provided
  // Constructor with less parameters will redirect to here
  public BasicCharacter(String name, double maxHP, double attackStrength, double defenseStrength, double speed){
    this.name = name;
    this.description = "A basic character.";
    this.maxHP = maxHP;
    this.currentHP = maxHP;
    this.attackStrength = attackStrength;
    this.defenseStrength = defenseStrength;
    this.speed = speed;
  }
  
  // If only the maxHP is provided, default values will be given for battle-related stats
  public BasicCharacter(double maxHP){
    this("Unnamed Character", maxHP, 25.0, 5.0, 5.0);
  }
  
  // If only battle-related stats are provided, a default value will be assigned for max health
  public BasicCharacter(double attackStrength, double defenseStrength, double speed){
    this("Unnamed Character", 100.0, attackStrength, defenseStrength, speed);
  }
  
  // If only name is provided, all other values wwill be given defaults
  public BasicCharacter(String name){
    this(name, 100.0, 25.0, 5.0, 5.0);
  }
  
  // Getter methods
  
  public String getName(){
    return name;
  }
  
  public String getType(){
    return "Character";
  }
  
  // Used to briefly display a character's stats within dialogue and prompts
  // EX: Unnamed (PLAYER Character) 100.0/100.0
  public String getSimpleOutput(){
    String output = "";
    String characterOwner = "";
    if(getIsEnemyCharacter()){
      characterOwner = "ENEMY";
    } else{
      characterOwner = "PLAYER";
    }
    if(getIsDead()){
      output += "[DEAD] ";
    }
    output += StatusEffect.getStatusOfCharacter(this);
    output += name + " (" + characterOwner + " " + getType() + ") " + currentHP + "/" + maxHP;
    return output;
  }
  
  // Overloaded method that allows the Character class itself to generate simplified info of any Character object
  public static String getSimpleOutput(BasicCharacter target){
    String output = "";
    String characterOwner = "";
    if(target.getIsEnemyCharacter()){
      characterOwner = "ENEMY";
    } else{
      characterOwner = "PLAYER";
    }
    if(target.getIsDead()){
      output += "[DEAD] ";
    }
    output += StatusEffect.getStatusOfCharacter(target);
    output += target.getName() + " (" + characterOwner + " " + target.getType() + ") " + target.getCurrentHP() + "/" + target.getMaxHP();
    return output;
  }
  
  // Misc getter methods

  public double getMaxHP(){
    return maxHP;
  }
  
  public double getCurrentHP(){
    return currentHP;
  }
  
  public boolean getIsDead(){
    return currentHP <= 0;
  }
  
  public double getAttackStrength(){
    return attackStrength;
  }
  
  public double getDefenseStrength(){
    return defenseStrength;
  }
  
  public double getSpeed(){
    return speed;
  }
  
  public boolean getIsEnemyCharacter(){
    return isEnemyCharacter;
  }
  
  public boolean getIsDefending(){
    return isDefending;
  }
  
  public String getDescription(){
    return description;
  }

  public String getPassiveAbilityName(){
    return passiveAbilityName;
  }

  public String getPassiveAbilityDescription(){
    return passiveAbilityDescription;
  }
  
  public ArrayList<String> getBasicAbilityNames(){
    return basicAbilityNames;
  }
  
  public ArrayList<String> getBasicAbilityDescriptions(){
    return basicAbilityDescriptions;
  }
  
  public ArrayList<Integer> getBasicAbilityEnemyCounts(){
    return basicAbilityEnemyCounts;
  }
  
  public int getBasicAbilityEnemyCount(int index){
    return basicAbilityEnemyCounts.get(index);
  }
  
  public ArrayList<String> getBasicAbilityTypes(){
    return basicAbilityTypes;
  }
  
  public ArrayList<String> getSpecialAbilityNames(){
    return specialAbilityNames;
  }
  
  public ArrayList<String> getSpecialAbilityDescriptions(){
    return specialAbilityDescriptions;
  }
  
  public ArrayList<Integer> getSpecialAbilityEnemyCounts(){
    return specialAbilityEnemyCounts;
  }
  
  public int getSpecialAbilityEnemyCount(int index){
    return specialAbilityEnemyCounts.get(index);
  }
  
  public ArrayList<Integer> getSpecialAbilityCooldowns(){
    return specialAbilityCooldowns;
  }
  
  public ArrayList<Integer> getCurrentSpecialAbilityCooldowns(){
    return currentSpecialAbilityCooldowns;
  }
  
  public ArrayList<String> getSpecialAbilityTypes(){
    return specialAbilityTypes;
  }
  
  // Setter methods
  // Battle-related double values work with both increasing and decreasing
  
  public void changeMaxHP(double amount){
    maxHP += amount;
    if(maxHP < 0.0){
      maxHP = 0.0;
    }
  }
  
  // changeCurrentHP will be used the most often
  // Other setter methods are more niche, but can be used for special interactions
  public void changeCurrentHP(double amount){
    currentHP += amount;
    if(currentHP < 0.0){
      currentHP = 0.0;
      System.out.println(name + " was knocked out in battle!");
    }
    if(currentHP > maxHP){
      currentHP = maxHP;
    }
  }
  
  public void changeAttackStrength(double amount){
    attackStrength += amount;
    if(attackStrength < 0.0){
      attackStrength = 0.0;
    }
  }
  
  public void changeDefenseStrength(double amount){
    defenseStrength += amount;
    if(defenseStrength < 0.0){
      defenseStrength = 0.0;
    }
  }
  
  public void changeSpeed(double amount){
    speed += amount;
    if(speed < 0.0){
      speed = 0.0;
    }
  }
  
  public void setIsEnemyCharacter(boolean newValue){
    isEnemyCharacter = newValue;
  }
  
  public void setIsDefending(boolean newValue){
    isDefending = newValue;
    if(isDefending){
      System.out.println(name + " will defend against opponent attacks for one turn.");
    }
  }
  
  // Decrease each special attack's cooldown timer by one whenever a new turn happens
  public void decreaseSpecialAbilityCooldowns(){
    for(int i = 0; i < currentSpecialAbilityCooldowns.size(); i++){
      currentSpecialAbilityCooldowns.set(i, Math.max(0, currentSpecialAbilityCooldowns.get(i) - 1));
    }
  }
  
  // Reset each special attack's cooldown timer to their originally instantiated cooldowns
  public void resetSpecialAbilityCooldowns(){
    for(int i = 0; i < currentSpecialAbilityCooldowns.size(); i++){
      currentSpecialAbilityCooldowns.set(i, specialAbilityCooldowns.get(i));
    }
  }
  
  // Allow subclasses to change a character's descriptions but don't allow other classes to do so
  // I searched up how this access level works (not AI)
  protected void setDescription(String newValue){
  	description = newValue;
  }

  protected void setPassiveAbilityName(String newValue){
    passiveAbilityName = newValue;
  }

  protected void setPassiveAbilityDescription(String newValue){
    passiveAbilityDescription = newValue;
  }
  
  // Even though overloading methods like this may be discouraged, I found that Java is good at
  // distinguishing between Strings and Integers
  // Used for basicAbilitykNames, basicAbilityDescriptions, specialAbilityNames, specialAbilityDescriptions
  protected void addToArrayList(ArrayList<String> arrayList, String[] values){
    for(String s : values){
      arrayList.add(s);
    }
  }
  
  // Used for specialAbilityCooldowns and currentSpecialAbilityCooldowns
  protected void addToArrayList(ArrayList<Integer> arrayList, Integer[] values){
    for(Integer i : values){
      arrayList.add(i);
    }
  }
  
  // Specialized methods to interact with battle
  
  // Target parameter is the character that the action is done to / against
  // Take appropriate amount of damage as the enemy and print relevant information
  // This function is almost completely overriden by the basicAbility and specialAbility functions,
  // but it is still usable in case specific situations happen
  public void attack(BasicCharacter target, PlayerTeam playerTeam, EnemyTeam enemyTeam) throws InterruptedException{
    System.out.println(name + " attacked " + target.getName() + " for " + attackStrength + " HP!");
    handleEnemyDefense(target, attackStrength, playerTeam, enemyTeam);
    System.out.println(target.getSimpleOutput());
  }
  
  // Calls the enemy target's defend function against the attacker
  // Reduce enemy HP by the appropriate amount
  // Boolean returns true if the enemy received any damage, false if not
  protected boolean handleEnemyDefense(BasicCharacter target, double attack, PlayerTeam playerTeam, EnemyTeam enemyTeam) throws InterruptedException{
    Thread.sleep(1000);
    double actualDamage;
    if(StatusEffect.hasStatusEffect(target, "Nimble")){
      System.out.println("Attack was reduced by 25% from " + target.getName() + " being NIMBLE!");
      Thread.sleep(1000);
      actualDamage = calculateActualDamage(target, attack*0.75, playerTeam, enemyTeam);
    } else{
      actualDamage = calculateActualDamage(target, attack, playerTeam, enemyTeam);
    } 
    target.defend(this, actualDamage);
    Thread.sleep(1000);
    target.changeCurrentHP(-actualDamage);
    if(actualDamage > 0){
      return true;
    }
    return false;
  }
  
  // If a character did not actively defend, their normal defenseStrength is applied
  // When a character actively defends, their defenseStrength is doubled
  private double calculateActualDamage(BasicCharacter target, double attack, PlayerTeam playerTeam, EnemyTeam enemyTeam){
    double actualDamage;
    if(target.isDefending){
      actualDamage = attack - (target.defenseStrength * 2);
    } else{
      actualDamage = attack - target.defenseStrength;
    }
    if(playerTeam.getIndexOfProtectedCharacter(target) != -1){
      int index = playerTeam.getIndexOfProtectedCharacter(target);
      System.out.println(target.getName() + " received " + playerTeam.getProtectedCharacterAmounts().get(index) + " defensive strength from a teammate!");
      actualDamage -= playerTeam.getProtectedCharacterAmounts().get(index);
    }
    return Math.max(actualDamage, 0);
  }
  
  // Handle the print statements shown when a character defends.
  // Damage calculations and accessing enemy functions are managed by helper functions
  // to make subclass overloading easier.
  public void defend(BasicCharacter target, double actualDamage){
    if(actualDamage == 0){
      System.out.println(name + " fully defended against " + target.getName() + "!");
    }else if(isDefending){
      System.out.println(name + " defended against " + target.getName() + " for " + defenseStrength * 2 + " HP!");
    } else{
      System.out.println(name + " lightly defended against " + target.getName() + " for " + defenseStrength + " HP!");
    }
  }
  
  // These methods are intended to be overriden by superclasses
  // basicAttacks and specialAttacks are directly correlated to the user
  // Inside these, subclasses may call the helper functions such as attack() or handleEnemyDefense()
  public void basicAbility(int basicAbilityIndex, BasicCharacter target, PlayerTeam playerTeam, EnemyTeam enemyTeam) throws InterruptedException{
    System.out.println("This character does not have a basicAbility function implemented.");
  }
  
  public void specialAbility(int specialAbilityIndex, BasicCharacter target, PlayerTeam playerTeam, EnemyTeam enemyTeam) throws InterruptedException{
    System.out.println("This character does not have a specialAbility function implemented.");
  }
  
  // Overrided toString method
  public String toString(){
    String output = "";
    output += getSimpleOutput();
    output += "\nATK: " + attackStrength + ", DEF: " + defenseStrength + ", SPD: " + speed;
    return output;
  }
}

