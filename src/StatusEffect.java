package src;
import java.util.ArrayList;
// Handles all status effects that both players and enemies experience
// Static class that can be used flexibly across all other classes
class StatusEffect {
  // All types of status effects in the game
  private static String[] effectTypes = new String[]{"Soft", "Bleed", "Poison", "Slow", "Stun", "Nimble", "Taunt", "Burn", "Pierce"};
  // How each status effect affects a target
  private static String[] applyTypes = new String[]{"One-time", "Passive", "Passive", "One-time", "One-time", "Other", "Other", "Passive"};
  
  // These three lists all have corresponding index values
  private static ArrayList<BasicCharacter> affectedCharacters = new ArrayList<BasicCharacter>();
  private static ArrayList<String> affectedTypes = new ArrayList<String>();
  private static ArrayList<Double> affectedTurnsLeft = new ArrayList<Double>();
  // Allow each status effect to store one Double value, which can be used in more complex operations
  private static ArrayList<Double> affectedRandomStats = new ArrayList<Double>();
  public StatusEffect(){}
  
  // Add a status effect for a specific character for the specified amount of turns
  public static void addStatusEffect (BasicCharacter c, String type, int totalTurns) throws InterruptedException {
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
    System.out.println(c.getName() + " has gained [" + type.toUpperCase() + "] for " + totalTurns + " turns!");
    Thread.sleep(1000);
    if(indexOfType(type) == -1){
      // System.out.println("DEBUG: " + type + " was not found in effectTypes.");
      return;
    }
    // One-time effect apply immediately after a StatusEffect is added
    if(applyTypes[indexOfType(type)].equalsIgnoreCase("One-time")){
      applyOneTimeEffect(c, type);
    }
  }

  // Search for an exact combination of Character and type in the ArrayLists
  // If found, remove them
  // If not found, do nothing
  public static void removeStatusEffect(BasicCharacter c, String type){
    for(int i = 0; i < affectedCharacters.size(); i++){
      if(affectedCharacters.get(i) == c && affectedTypes.get(i).equals(type)){
        System.out.println(affectedCharacters.get(i).getName() + " lost the [" + affectedTypes.get(i).toUpperCase() + "] effect!");
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
      c.changeDefenseStrength(-10);
    }
  }
  
  // Runs right after a status effect is removed
  private static void reverseOneTimeEffect(BasicCharacter c, String type){
    if(type.equals("Slow")){
      c.changeSpeed(15);
    } else if(type.equals("Soft")){
      c.changeAttackStrength(10);
    } else if(type.equals("Pierce")){
      c.changeDefenseStrength(10);
    }
  }
  
  // Runs once every turn
  private static void applyPassiveEffect(BasicCharacter c, String type) throws InterruptedException{
    if(type.equals("Poison")){
      System.out.println(c.getName() + " lost 5 HP from poison!");
      c.changeCurrentHP(-5);
    } else if (type.equals("Bleed")){
      System.out.println(c.getName() + " lost 5 HP from bleeding!");
      c.changeCurrentHP(-5);
    } else if(type.equals("Burn")){
      System.out.println(c.getName() + " lost 5 HP from burning!");
      c.changeCurrentHP(-5);
    }
    Thread.sleep(1000);
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
  public static void handleStatusTurn() throws InterruptedException{
    for(int i = 0; i < affectedCharacters.size(); i++){
      // This method is called twice per turn. Decrease by -0.5 to offset this difference.
      affectedTurnsLeft.set(i, affectedTurnsLeft.get(i) - 0.5);
      // Make sure a passive effect is only applied once per turn
      if(applyTypes[indexOfType(affectedTypes.get(i))].equals("Passive") && affectedTurnsLeft.get(i) % 1 == 0){
          applyPassiveEffect(affectedCharacters.get(i), affectedTypes.get(i));
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
        System.out.println(affectedCharacters.get(i).getName() + " lost the [" + affectedTypes.get(i).toUpperCase() + "] effect!");
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
  }
}
