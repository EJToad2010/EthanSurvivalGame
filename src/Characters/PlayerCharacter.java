package src.Characters;
import java.util.ArrayList;
// PlayerCharacters have unique attributes such as a shared inventory, xp, and levels
// This object should never be created on its own. Only its subclasses will ever be used.
public class PlayerCharacter extends BasicCharacter {
  // How many levels are needed to unlock a PlayerCharacter's corresponding basic abilitiess
  // Length must equal all other basic ability ArrayLists in Character.java
  private ArrayList<Integer> basicAbilityUnlockLevels = new ArrayList<Integer>();
  // How many levels are needed to unlock a PlayerCharacter's corresponding special abilities
  // Legth must equal all other special ability ArrayLists in Character.java
  private ArrayList<Integer> specialAbilityUnlockLevels = new ArrayList<Integer>();
  
  // XP-related attributes
  private int xp = 0;
  private int level = 1;
  private int xpToNextLevel;
  
  // Constructor that requires all attributes
  public PlayerCharacter(String name, double maxHP, double attackStrength, double defenseStrength, double speed){
    super(name, maxHP, attackStrength, defenseStrength, speed);
    setDescription("A basic player character.");
  }
  
  // Constructor that only requires a name
  public PlayerCharacter(String name){
    super(name);
    setDescription("A basic player character.");
  }
  
  // Getter methods

  public int getXP(){
    return xp;
  }
  
  public int getLevel(){
    return level;
  }
  
  public ArrayList<Integer> getBasicAbilityUnlockLevels(){
    return basicAbilityUnlockLevels;
  }
  
  public ArrayList<Integer> getSpecialAbilityUnlockLevels(){
    return specialAbilityUnlockLevels;
  }
  
  // Obtain the index of basicAbilityUnlockLevels less than or equal to level
  public int getHighestIndexBasic(){
    int index = 0;
    for(int i = 0; i < basicAbilityUnlockLevels.size(); i++){
      if(basicAbilityUnlockLevels.get(i) <= level){
        index = i;
      }
    }
    return index;
  }
  
  // Obtain the index of specialAbilityUnlockLevels less than or equal to level
  public int getHighestIndexSpecial(){
    int index = -1;
    for(int i = 0; i < specialAbilityUnlockLevels.size(); i++){
      if(specialAbilityUnlockLevels.get(i) <= level){
        index = i;
      }
    }
    return index;
  }
  
  public void updateXPToNextLevel() {
    // 50, 150, 300, 500, etc.
    // +50, +100, +150, +200
    xpToNextLevel =  25 * level * level + 25 * level;
  }
  
  // Increase the Character's total xp by the specified amount
  // Handle detection for levelling up and its logic
  public void increaseXP(int amount) throws InterruptedException {
    System.out.println(getName() + " gained " + amount + " XP!");
    updateXPToNextLevel();
    xp += amount;
    while (xp >= xpToNextLevel) {
      int prevBasic = getHighestIndexBasic();
      int prevSpecial = getHighestIndexSpecial();
      level++;
      updateXPToNextLevel();
      Thread.sleep(1000);
      System.out.println(getName() + " leveled up to level " + level + "!");
      Thread.sleep(1000);
      updatePlayerStats();
      Thread.sleep(1000);
      checkUnlockedAbilities(prevBasic, prevSpecial);
    }
	  System.out.println(getName() + "'s XP to next level: " + (xpToNextLevel - xp) + "\n");
  }
  
  //Attributes that upgrade every time a PlayerCharacter levels up
  private void updatePlayerStats(){
    System.out.println("+5 MAX HP");
    changeMaxHP(5);
    System.out.println("+2 ATK");
    changeAttackStrength(2);
    System.out.println("+2 DEF");
    changeDefenseStrength(2);
    System.out.println("+2 SPD");
    changeSpeed(2);
  }
  
  // Check if a new basic or special ability was unlocked after levelling up
  private void checkUnlockedAbilities(int prevBasic, int prevSpecial) throws InterruptedException{
    for(int i = prevBasic + 1; i < basicAbilityUnlockLevels.size(); i++){
      if(basicAbilityUnlockLevels.get(i) <= level){
        System.out.println("New basic ability unlocked!");
        Thread.sleep(1000);
        System.out.print(getBasicAbilityNames().get(i) + ": ");
        System.out.println(getBasicAbilityDescriptions().get(i));
        System.out.println("");
        Thread.sleep(2000);
      }
    }
    
    for(int i = prevSpecial + 1; i < specialAbilityUnlockLevels.size(); i++){
      if(specialAbilityUnlockLevels.get(i) == level){
        System.out.println("New special ability unlocked!");
        Thread.sleep(1000);
        System.out.print(getSpecialAbilityNames().get(i) + ": ");
        System.out.println(getSpecialAbilityDescriptions().get(i));
        System.out.println("");
        Thread.sleep(2000);
      }
    }
  }
}
