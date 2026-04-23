package src.Characters;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.util.ArrayList;

import src.GameManagement.UI.ImageManager;
import src.GameManagement.UI.UIManager;
import src.Misc.StatusEffect;
import src.Teams.EnemyTeam;
import src.Teams.PlayerTeam;
import src.GameManagement.Mechanics.ActionResult;
import src.GameManagement.Mechanics.Signals;

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
  private Image characterImage;
  private Image deadImage = ImageManager.loadImage("src/Images/dead.png");
  private boolean isSelected = false;
  // Location on screen
  private int x = 0;
  private int y = 0;
  // Size of character image
  protected int width = 160;
  protected int height = 160;
  // The amount of space the Character loses when displayed on screen
  private int lostSpacing = 0;
  private boolean isAnimating = false;
  protected int attackAnimationLength = 0;
  // Integer value used to uniquely identify a Character
  // (Used within signals)
  private int id;

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
    // Create an id from 9 randomly generated numbers
    id = generateRandomNum(9);
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

  // Create n numbers in sequence randomly
  private int generateRandomNum(int n){
    String strOutput = "";
    for(int i = 0; i < n; i++){
      strOutput += (int)(Math.random() * 10);
    }
    return Integer.parseInt(strOutput);
  }
  
  // Getter methods

  public int getID(){
    return id;
  }
  
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

  public int getX(){
    return x;
  }
  
  public int getY(){
    return y;
  }

  public int getWidth(){
    return width;
  }

  public int getHeight(){
    return height;
  }

  public int getLostSpacing(){
    return lostSpacing;
  }

  public boolean getIsAnimating(){
    return isAnimating;
  }

  public int getAttackAnimationLength(){
    return attackAnimationLength;
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
    // If already dead, cannot heal
    if(currentHP <= 0.0){
      return;
    }
    
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
  
  public ActionResult setIsDefending(boolean newValue){
    ActionResult output = new ActionResult();
    isDefending = newValue;
    if(isDefending){
      //System.out.println(name + " will defend against opponent attacks for one turn.");
      output.add(name + " will defend against opponent attacks for one turn.");
    }
    return output;
  }

  public void setCharacterImage(String path){
    characterImage = ImageManager.loadImage(path);
  }

  public void setPosition(int x, int y){
    this.x = x;
    this.y = y;
  }

  public void setSize(int width, int height){
    this.width = width;
    this.height = height;
  }

  public void setLostSpacing(int lostSpacing){
    this.lostSpacing = lostSpacing;
  }

  public void setIsSelected(boolean isSelected){
    this.isSelected = isSelected;
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

  public void setIsAnimating(boolean isAnimating){
    this.isAnimating = isAnimating;
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
  
  // Convert an ArrayList<String> of a Character to a String[] to be used in other class methods
  public String[] convertArrToList(ArrayList<String> charArr){
    String[] output = new String[charArr.size()];
    for(int i = 0; i < charArr.size(); i++){
      output[i] = charArr.get(i);
    }
    return output;
  }
  
  // Specialized methods to interact with battle
  
  // Target parameter is the character that the action is done to / against
  // Take appropriate amount of damage as the enemy and print relevant information
  // This function is almost completely overriden by the basicAbility and specialAbility functions,
  // but it is still usable in case specific situations happen
  public void attack(BasicCharacter target, PlayerTeam playerTeam, EnemyTeam enemyTeam) throws InterruptedException{
    ActionResult output = new ActionResult();
    System.out.println(name + " attacked " + target.getName() + " for " + attackStrength + " HP!");
    output.add(name + " attacked " + target.getName() + " for " + attackStrength + " HP!",Signals.ATTACK_PERFORMED);
    handleEnemyDefense(target, attackStrength, playerTeam, enemyTeam);
    System.out.println(target.getSimpleOutput());
  }
  
  // Calls the enemy target's defend function against the attacker
  // Reduce enemy HP by the appropriate amount
  protected ActionResult handleEnemyDefense(BasicCharacter target, double attack, PlayerTeam playerTeam, EnemyTeam enemyTeam){
    ActionResult output = new ActionResult();
    double actualDamage;
    if(StatusEffect.hasStatusEffect(target, "Nimble")){
      System.out.println("Attack was reduced by 25% from " + target.getName() + " being NIMBLE!");
      output.add("Attack was reduced by 25% from " + target.getName() + " being NIMBLE!");
      actualDamage = calculateActualDamage(target, attack*0.75, playerTeam, enemyTeam, output);
    } else{
      actualDamage = calculateActualDamage(target, attack, playerTeam, enemyTeam, output);
    } 
    output.add(target.defend(this, actualDamage));
    output.add(Signals.TARGET_HEALTH_LOST, actualDamage);
    return output;
  }
  
  // If a character did not actively defend, their normal defenseStrength is applied
  // When a character actively defends, their defenseStrength is doubled
  private double calculateActualDamage(BasicCharacter target, double attack, PlayerTeam playerTeam, EnemyTeam enemyTeam,ActionResult output){
    double actualDamage;
    if(target.isDefending){
      actualDamage = attack - (target.defenseStrength * 2);
    } else{
      actualDamage = attack - target.defenseStrength;
    }
    // Avoid negative defensive strength which can happen from status effects
    if(target.defenseStrength <= 0){
      actualDamage = attack;
    }
    if(playerTeam.getIndexOfProtectedCharacter(target) != -1){
      int index = playerTeam.getIndexOfProtectedCharacter(target);
      // System.out.println(target.getName() + " received " + playerTeam.getProtectedCharacterAmounts().get(index) + " defensive strength from a teammate!");
      output.add(target.getName() + " received " + playerTeam.getProtectedCharacterAmounts().get(index) + " defensive strength from a teammate!", Signals.DEFENSE_RECEIVED, playerTeam.getProtectedCharacterAmounts().get(index));
      actualDamage -= playerTeam.getProtectedCharacterAmounts().get(index);
    }
    return Math.max(actualDamage, 0);
  }
  
  // Handle the print statements shown when a character defends.
  // Damage calculations and accessing enemy functions are managed by helper functions
  // to make subclass overloading easier.
  public ActionResult defend(BasicCharacter target, double actualDamage){
    ActionResult output = new ActionResult();
    if(actualDamage == 0){
      System.out.println(name + " fully defended against " + target.getName() + "!");
      output.add(name + " fully defended against " + target.getName() + "!",Signals.DEFENSE_PERFORMED, defenseStrength);
    }else if(isDefending){
      System.out.println(name + " defended against " + target.getName() + " for " + defenseStrength * 2 + " HP!");
      output.add(name + " defended against " + target.getName() + " for " + defenseStrength * 2 + " HP!",Signals.DEFENSE_PERFORMED, defenseStrength*2);
    } else{
      System.out.println(name + " lightly defended against " + target.getName() + " for " + defenseStrength + " HP!");
      output.add(name + " lightly defended against " + target.getName() + " for " + defenseStrength + " HP!",Signals.DEFENSE_PERFORMED, defenseStrength);
    }
    return output;
  }
  
  // These methods are intended to be overriden by superclasses
  // basicAttacks and specialAttacks are directly correlated to the user
  // Inside these, subclasses may call the helper functions such as attack() or handleEnemyDefense()
  // Return an ActionResult, which tells the dialog system how to react to the Character's ability
  public ActionResult basicAbility(int basicAbilityIndex, BasicCharacter target, PlayerTeam playerTeam, EnemyTeam enemyTeam){
    System.out.println("This character does not have a basicAbility function implemented.");
    return new ActionResult();
  }
  
  public ActionResult specialAbility(int specialAbilityIndex, BasicCharacter target, PlayerTeam playerTeam, EnemyTeam enemyTeam){
    System.out.println("This character does not have a specialAbility function implemented.");
    return new ActionResult();
  }
  
  // Overrided toString method
  public String toString(){
    String output = "";
    output += getSimpleOutput();
    output += "\nATK: " + attackStrength + ", DEF: " + defenseStrength + ", SPD: " + speed;
    return output;
  }

  // Animation methods to override
  // Called every time the Character conducts an offensive attack
  public void drawAttackAnimation(Graphics graphics, int tick){}

  // Draw the given Character sprite at top left corner (x, y)
  public void drawCharacter(Graphics graphics){
    if(isSelected){
      graphics.setColor(Color.YELLOW);
      graphics.fillRect(x, y, width, height);
    }
    drawCharImage(graphics, x, y);
    drawHPBar(graphics, x, y);
    if(this instanceof PlayerCharacter){
      if(((PlayerCharacter) this).getIsDrawingXP()){
        ((PlayerCharacter) this).drawXPBar(graphics, x, y);
      }
    }
    drawCharText(graphics, x, y);
  }

  protected void drawCharImage(Graphics graphics, int localX, int localY){
    if(getIsDead()){
      graphics.drawImage(deadImage, localX, localY, null);
    } else{
      graphics.drawImage(characterImage, localX, localY, null);
    }
  }

  protected void drawCharText(Graphics graphics, int localX, int localY){
    StatusEffect.drawStatusEffects(graphics, this);
    // Draw Character's HP points left above the text
    UIManager.findMaxFontSize(currentHP + "/" + maxHP, graphics, (width-lostSpacing*2) / 2, 12, true, true);
    int hpFontSize = UIManager.getFont().getSize();
    UIManager.setTextColor(graphics, Color.BLACK);
    UIManager.drawCenteredStringInBox(graphics, currentHP + "/" + maxHP, localX+lostSpacing, localY-12, (width-lostSpacing*2), 12);
    // Draw Character's name
    UIManager.findMaxFontSize(name, graphics, width-(lostSpacing*2), 20, true, true);
    UIManager.drawCenteredStringInBox(graphics, name, localX+lostSpacing, localY-20-hpFontSize, (width-lostSpacing*2), 20);
  }

  // Draw the HP bar of the Character
  // Positioned 10 pixels above the Character, spanning the exact width of the Character
  protected void drawHPBar(Graphics graphics, int localX, int localY){
    double hpRatio = currentHP / maxHP;
    int hpSize = (int)((double)(width-(lostSpacing*2)) * hpRatio);
    graphics.setColor(Color.BLACK);
    graphics.fillRect(localX+lostSpacing, localY-10, width-(lostSpacing*2), 10);
    graphics.setColor(Color.GREEN);
    graphics.fillRect(localX+lostSpacing, localY-10, hpSize, 10);
  }
}

