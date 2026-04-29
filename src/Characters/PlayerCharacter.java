package src.Characters;
import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

import src.GameManagement.Game;
import src.GameManagement.Mechanics.ActionResult;
import src.GameManagement.Mechanics.Signals;
import src.GameManagement.UI.DialogManager;
import src.GameManagement.UI.UIManager;
import src.Teams.PlayerTeam;
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
  // The instance of Game is obtained alongside the playerTeam
  protected Game game;

  // Synced with PlayerTeam
  private boolean isDrawingXP;
  
  // Constructor that requires all attributes
  public PlayerCharacter(String name, double xpToNextLevel, double attackStrength, double defenseStrength, double speed){
    super(name, xpToNextLevel, attackStrength, defenseStrength, speed);
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

  public boolean getIsDrawingXP(){
    return isDrawingXP;
  }

  public void setIsDrawingXP(boolean isDrawingXP){
    this.isDrawingXP = isDrawingXP;
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
  public ActionResult increaseXP(int amount){
    ActionResult output = new ActionResult();
    //System.out.println(getName() + " gained " + amount + " XP!");
    output.add(getName() + " gained " + amount + " XP!", Signals.XP_GAINED, amount);
    updateXPToNextLevel();
    xp += amount;
    while (xp >= xpToNextLevel) {
      int prevBasic = getHighestIndexBasic();
      int prevSpecial = getHighestIndexSpecial();
      level++;
      updateXPToNextLevel();
      // System.out.println(getName() + " leveled up to level " + level + "!");
      output.add(getName() + " leveled up to level " + level + "!", Signals.LEVEL_UP, 1);
      output.add(updatePlayerStats());
      output.add(checkUnlockedAbilities(prevBasic, prevSpecial));
    }
	  //System.out.println(getName() + "'s XP to next level: " + (xpToNextLevel - xp) + "\n");
    //output.add(getName() + "'s XP to next level: " + (xpToNextLevel - xp));
    return output;
  }
  
  //Attributes that upgrade every time a PlayerCharacter levels up
  private ActionResult updatePlayerStats(){
    ActionResult output = new ActionResult();
    output.add("+5 MAX HP, +2 ATK, +2 DEF, +2 SPD");
    //System.out.println("+5 MAX HP");
    changeMaxHP(5);
    changeCurrentHP(5);
    //System.out.println("+2 ATK");
    changeAttackStrength(2);
    //System.out.println("+2 DEF");
    changeDefenseStrength(2);
    //System.out.println("+2 SPD");
    changeSpeed(2);
    return output;
  }
  
  // Check if a new basic or special ability was unlocked after levelling up
  private ActionResult checkUnlockedAbilities(int prevBasic, int prevSpecial){
    ActionResult output = new ActionResult();
    for(int i = prevBasic + 1; i < basicAbilityUnlockLevels.size(); i++){
      if(basicAbilityUnlockLevels.get(i) <= level){
        //System.out.println("New basic ability unlocked!");
        output.add("New basic ability unlocked! " + getBasicAbilityNames().get(i));
        //System.out.print(getBasicAbilityNames().get(i) + ": ");
        //System.out.println(getBasicAbilityDescriptions().get(i));
        output.add(getBasicAbilityDescriptions().get(i));
        //System.out.println("");
      }
    }
    
    for(int i = prevSpecial + 1; i < specialAbilityUnlockLevels.size(); i++){
      if(specialAbilityUnlockLevels.get(i) == level){
        //System.out.println("New special ability unlocked!");
        output.add("New special ability unlocked! " + getSpecialAbilityNames().get(i));
        //System.out.print(getSpecialAbilityNames().get(i) + ": ");
        //System.out.println(getSpecialAbilityDescriptions().get(i));
        output.add(getSpecialAbilityDescriptions().get(i));
        //System.out.println("");
      }
    }
    return output;
  }

  public void drawXPBar(Graphics graphics, int localX, int localY){
    updateXPToNextLevel();
    double xpRatio = xp / xpToNextLevel;
    int xpSize = (int)((double)(getWidth()-(getLostSpacing()*2)) * xpRatio);
    //System.out.println(xp);
    graphics.setColor(Color.BLACK);
    graphics.fillRect(localX+getLostSpacing(), localY+getHeight()+10, getWidth()-(getLostSpacing()*2), 10);
    graphics.setColor(Color.CYAN);
    graphics.fillRect(localX+getLostSpacing(), localY+getHeight()+10, xpSize, 10);
    // Draw Character's XP point ratio below the bar
    UIManager.findMaxFontSize(xp + "/" + xpToNextLevel, graphics, (getWidth()-getLostSpacing()*2) / 2, 12, true, true);
    UIManager.setTextColor(graphics, Color.BLACK);
    UIManager.drawCenteredStringInBox(graphics, xp + "/" + xpToNextLevel, localX+getLostSpacing(), localY+getHeight()+32, (getWidth()-getLostSpacing()*2), 12);
  }
}
