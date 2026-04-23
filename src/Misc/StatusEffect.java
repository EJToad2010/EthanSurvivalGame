package src.Misc;
import java.util.ArrayList;

import java.awt.Color;
import java.awt.Graphics;

import src.Characters.BasicCharacter;
import src.GameManagement.Mechanics.ActionResult;
import src.GameManagement.UI.UIManager;
// Handles all status effects that both players and enemies experience
// Static class that can be used flexibly across all other classes
public class StatusEffect {
  // All types of status effects in the game
  private static String[] effectTypes = new String[]{"Soft", "Bleed", "Poison", "Slow", "Stun", "Nimble", "Taunt", "Burn", "Pierce"};
  // How each status effect affects a target
  private static String[] applyTypes = new String[]{"One-time", "Passive", "Passive", "One-time", "One-time", "Other", "Other", "Passive", "One-time"};
  // The color of each status effect when displayed in-game
  private static Color[] colorTypes = new Color[]{Color.DARK_GRAY, new Color(110, 23, 23), new Color(17, 92, 29), new Color(53, 56, 99), new Color(66, 66, 66),
    new Color(10, 130, 20), new Color(0, 186, 16), new Color(173, 16, 16), new Color(9, 100, 128)
  };
  
  // These three lists all have corresponding index values
  private static ArrayList<BasicCharacter> affectedCharacters = new ArrayList<BasicCharacter>();
  private static ArrayList<String> affectedTypes = new ArrayList<String>();
  private static ArrayList<Double> affectedTurnsLeft = new ArrayList<Double>();
  // Allow each status effect to store one Double value, which can be used in more complex operations
  private static ArrayList<Double> affectedRandomStats = new ArrayList<Double>();
  public StatusEffect(){}
  
  // Add a status effect for a specific character for the specified amount of turns
  public static ActionResult addStatusEffect (BasicCharacter c, String type, int totalTurns){
    ActionResult output = new ActionResult();
    if(hasStatusEffect(c, type)){
      // Avoiding stacking multiple of the same status effect.
      // If this happens, only update the turns left to the new value
      // Get index of Character's status effect that already exists
      int index = 0;
      for(int i = 0; i < affectedCharacters.size(); i++){
        if(affectedCharacters.get(i).equals(c)){
          if(affectedTypes.get(i).equals(type)){
            index = i;
            break;
          }
        }
      }
      affectedTurnsLeft.set(index, (double)totalTurns);
    } else{
      // Add the new StatusEffect info if no other StatusEffect of that name and character are found
      affectedCharacters.add(c);
      affectedTypes.add(type);
      affectedTurnsLeft.add((double)totalTurns);
      affectedRandomStats.add(0.0);
    }
    // Message to confirm addition of StatusEffect
    //System.out.println(c.getName() + " has gained [" + type.toUpperCase() + "] for " + totalTurns + " turns!");
    output.add(c.getName() + " has gained [" + type.toUpperCase() + "] for " + totalTurns + " turns!");
    // One-time effect apply immediately after a StatusEffect is added
    if(applyTypes[indexOfType(type)].equalsIgnoreCase("One-time")){
      applyOneTimeEffect(c, type);
    }
    return output;
  }

  // Search for an exact combination of Character and type in the ArrayLists
  // If found, remove them
  // If not found, do nothing
  public static ActionResult removeStatusEffect(BasicCharacter c, String type){
    ActionResult output = new ActionResult();
    for(int i = 0; i < affectedCharacters.size(); i++){
      if(affectedCharacters.get(i) == c && affectedTypes.get(i).equals(type)){
        //System.out.println(affectedCharacters.get(i).getName() + " lost the [" + affectedTypes.get(i).toUpperCase() + "] effect!");
        output.add(affectedCharacters.get(i).getName() + " lost the [" + affectedTypes.get(i).toUpperCase() + "] effect!");
        if(applyTypes[indexOfType(affectedTypes.get(i))].equals("One-time")){
          reverseOneTimeEffect(affectedCharacters.get(i), affectedTypes.get(i));
        }
        affectedCharacters.remove(i);
        affectedTypes.remove(i);
        affectedTurnsLeft.remove(i);
        affectedRandomStats.remove(i);
        i--;
        break;
      }
    }
    return output;
  }
  
  // Remove a character's status effect
  // One-time effects are reverted to their original state
  public static void resetStatusEffects(){
    for(int i = 0; i < affectedCharacters.size(); i++){
      if(applyTypes[indexOfType(affectedTypes.get(i))].equals("One-time")){
        reverseOneTimeEffect(affectedCharacters.get(i), affectedTypes.get(i));
      }
      affectedCharacters.remove(i);
      affectedTypes.remove(i);
      affectedTurnsLeft.remove(i);
      affectedRandomStats.remove(i);
      i--;
    }
  }
  
  // Find the corresponding index of an item in effectTypes
  private static int indexOfType(String type){
    for(int i = 0; i < effectTypes.length; i++){
      if(effectTypes[i].equalsIgnoreCase(type)){
        return i;
      }
    }
    return -1;
  }
  
  // Runs right after a status effect is added
  private static void applyOneTimeEffect(BasicCharacter c, String type){
    if(type.equals("Slow")){
      c.changeSpeed(-15);
    } else if(type.equals("Soft")){
      c.changeAttackStrength(-10);
    } else if(type.equals("Pierce")){
      c.changeDefenseStrength(-15);
    }
  }
  
  // Runs right after a status effect is removed
  private static void reverseOneTimeEffect(BasicCharacter c, String type){
    if(type.equals("Slow")){
      c.changeSpeed(15);
    } else if(type.equals("Soft")){
      c.changeAttackStrength(10);
    } else if(type.equals("Pierce")){
      c.changeDefenseStrength(15);
    }
  }
  
  // Runs once every turn
  private static ActionResult applyPassiveEffect(BasicCharacter c, String type){
    ActionResult output = new ActionResult();
    if(type.equals("Poison")){
      //System.out.println(c.getName() + " lost 5 HP from poison!");
      output.add(c.getName() + " lost 5 HP from poison!");
      c.changeCurrentHP(-5);
    } else if (type.equals("Bleed")){
      //System.out.println(c.getName() + " lost 5 HP from bleeding!");
      output.add(c.getName() + " lost 5 HP from bleeding!");
      c.changeCurrentHP(-5);
    } else if(type.equals("Burn")){
      //System.out.println(c.getName() + " lost 5 HP from burning!");
      output.add(c.getName() + " lost 5 HP from burning!");
      c.changeCurrentHP(-5);
    }
    return output;
  }
  
  // Return a formatted list of all of a Character's status effects.
  // Used in a Character's getSimpleOutput() method
  public static String getStatusOfCharacter(BasicCharacter c){
    if(affectedCharacters.contains(c)){
      String output = "";
      ArrayList<String> statusTypesOfCharacter = statusEffectListOf(c);
      
      for(int i = 0; i < statusTypesOfCharacter.size(); i++){
        output += "[" + statusTypesOfCharacter.get(i).toUpperCase() + "] ";
      }
      return output;
    } else{
      return "";
    }
  }
  
  // Return an ArrayList of all status effects a given character has
  private static ArrayList<String> statusEffectListOf(BasicCharacter c){
    ArrayList<String> statusTypesOfCharacter = new ArrayList<String>();
    for(int i = 0; i < affectedCharacters.size(); i++){
      if(affectedCharacters.get(i) == c){
        statusTypesOfCharacter.add(affectedTypes.get(i));
      }
    }
    return statusTypesOfCharacter;
  }
  
  // Check if a character has a specific status effect
  public static boolean hasStatusEffect(BasicCharacter c, String type){
    ArrayList<String> statusTypesOfCharacter = statusEffectListOf(c);
    return statusTypesOfCharacter.contains(type);
  }
  
  // Runs every time the turn switches from player to enemy and the other way around
  public static ActionResult handleStatusTurn(){
    ActionResult output = new ActionResult();
    for(int i = 0; i < affectedCharacters.size(); i++){
      // This method is called twice per turn. Decrease by -0.5 to offset this difference.
      affectedTurnsLeft.set(i, affectedTurnsLeft.get(i) - 0.5);
      // Make sure a passive effect is only applied once per turn
      if(applyTypes[indexOfType(affectedTypes.get(i))].equals("Passive") && affectedTurnsLeft.get(i) % 1 == 0){
          output.add(applyPassiveEffect(affectedCharacters.get(i), affectedTypes.get(i)));
      }

      // Remove a status effect if a Character is dead
      if(affectedCharacters.get(i).getIsDead()){
        affectedCharacters.remove(i);
        affectedTypes.remove(i);
        affectedTurnsLeft.remove(i);
        affectedRandomStats.remove(i);
        i--;
        continue;
      }

      // Remove a status effect if its turn timer runs out
      if(affectedTurnsLeft.get(i) <= 0){
        output.add(removeStatusEffect(affectedCharacters.get(i), affectedTypes.get(i)));
        //System.out.println(affectedCharacters.get(i).getName() + " lost the [" + affectedTypes.get(i).toUpperCase() + "] effect!");
        /*output.add(affectedCharacters.get(i).getName() + " lost the [" + affectedTypes.get(i).toUpperCase() + "] effect!");
        if(applyTypes[indexOfType(affectedTypes.get(i))].equals("One-time")){
          reverseOneTimeEffect(affectedCharacters.get(i), affectedTypes.get(i));
        }
        affectedCharacters.remove(i);
        affectedTypes.remove(i);
        affectedTurnsLeft.remove(i);
        affectedRandomStats.remove(i);*/
        i--;
      }
    }
    return output;
  }
  // Draw the text containing all status effects of a Character
  public static void drawStatusEffects(Graphics graphics, BasicCharacter c){
    ArrayList<String> statusTypesOfCharacter = statusEffectListOf(c);
    int heightOffset = 50;
    for(String statusType : statusTypesOfCharacter){
      // Find corresponding color for status type
      int statusIndex = 0;
      for(int i = 0; i < effectTypes.length; i++){
        if(effectTypes[i].equals(statusType)){
          statusIndex = i;
          break;
        }
      }
      UIManager.setTextColor(graphics, colorTypes[statusIndex]);
      UIManager.findMaxFontSize("["+statusType+"]", graphics, c.getWidth()-(c.getLostSpacing()*2), 20, true, true);
      UIManager.drawCenteredStringInBox(graphics, "["+statusType+"]", c.getX()+c.getLostSpacing(), c.getY()-heightOffset, c.getWidth()-(c.getLostSpacing()*2), 20);
      heightOffset -= 20;
    }
  }
}
