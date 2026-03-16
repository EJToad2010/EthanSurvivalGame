package src;
import java.util.ArrayList;
// Handles all status effects that both players and enemies experience
class StatusEffect {
  // All types of status effects in the game
  private static String[] effectTypes = new String[]{"Soft", "Bleed", "Poison", "Slow", "Stun", "Nimble", "Taunt"};
  // How each status effect affects a target
  private static String[] applyTypes = new String[]{"One-time", "Passive", "Passive", "One-time", "One-time", "Other", "Other"};
  
  private static ArrayList<BasicCharacter> affectedCharacters = new ArrayList<BasicCharacter>();
  private static ArrayList<String> affectedTypes = new ArrayList<String>();
  private static ArrayList<Double> affectedTurnsLeft = new ArrayList<Double>();
  
  public StatusEffect(){}
  
  public static void addStatusEffect (BasicCharacter c, String type, int totalTurns) throws InterruptedException {
    if(hasStatusEffect(c, type)){
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
      affectedCharacters.add(c);
      affectedTypes.add(type);
      affectedTurnsLeft.add((double)totalTurns);
    }
    System.out.println(c.getName() + " has gained [" + type.toUpperCase() + "] for " + totalTurns + " turns!");
    Thread.sleep(1000);
    if(indexOfType(type) == -1){
      System.out.println("DEBUG: " + type + " was not found in effectTypes.");
      return;
    }
    if(applyTypes[indexOfType(type)].equalsIgnoreCase("One-time")){
      applyOneTimeEffect(c, type);
    }
  }
  
  public static void resetStatusEffects(){
    for(int i = 0; i < affectedCharacters.size(); i++){
      if(applyTypes[indexOfType(affectedTypes.get(i))].equals("One-time")){
        reverseOneTimeEffect(affectedCharacters.get(i), affectedTypes.get(i));
      }
      affectedCharacters.remove(i);
      affectedTypes.remove(i);
      affectedTurnsLeft.remove(i);
      i--;
    }
  }
  
  private static int indexOfType(String type){
    for(int i = 0; i < effectTypes.length; i++){
      if(effectTypes[i].equalsIgnoreCase(type)){
        return i;
      }
    }
    return -1;
  }
  
  private static void applyOneTimeEffect(BasicCharacter c, String type){
    if(type.equals("Slow")){
      c.changeSpeed(-10);
    } else if(type.equals("Soft")){
      c.changeAttackStrength(-10);
    }
  }
  
  private static void reverseOneTimeEffect(BasicCharacter c, String type){
    if(type.equals("Slow")){
      c.changeSpeed(10);
    } else if(type.equals("Soft")){
      c.changeAttackStrength(10);
    }
  }
  
  private static void applyPassiveEffect(BasicCharacter c, String type) throws InterruptedException{
    if(type.equals("Poison")){
      System.out.println(c.getName() + " lost 5 HP from poison!");
      c.changeCurrentHP(-5);
    } else if (type.equals("Bleed")){
      System.out.println(c.getName() + " lost 5 HP from bleeding!");
      c.changeCurrentHP(-5);
    }
    Thread.sleep(1000);
  }
  
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
  
  private static ArrayList<String> statusEffectListOf(BasicCharacter c){
    ArrayList<String> statusTypesOfCharacter = new ArrayList<String>();
    for(int i = 0; i < affectedCharacters.size(); i++){
      if(affectedCharacters.get(i) == c){
        statusTypesOfCharacter.add(affectedTypes.get(i));
      }
    }
    return statusTypesOfCharacter;
  }
  
  public static boolean hasStatusEffect(BasicCharacter c, String type){
    ArrayList<String> statusTypesOfCharacter = statusEffectListOf(c);
    return statusTypesOfCharacter.contains(type);
  }
  
  public static void handleStatusTurn() throws InterruptedException{
    for(int i = 0; i < affectedCharacters.size(); i++){
      affectedTurnsLeft.set(i, affectedTurnsLeft.get(i) - 0.5);
      if(applyTypes[indexOfType(affectedTypes.get(i))].equals("Passive")){
          applyPassiveEffect(affectedCharacters.get(i), affectedTypes.get(i));
      }

      if(affectedTurnsLeft.get(i) <= 0){
        System.out.println(affectedCharacters.get(i).getName() + " lost the [" + affectedTypes.get(i).toUpperCase() + "] effect!");
        if(applyTypes[indexOfType(affectedTypes.get(i))].equals("One-time")){
          reverseOneTimeEffect(affectedCharacters.get(i), affectedTypes.get(i));
        }
        affectedCharacters.remove(i);
        affectedTypes.remove(i);
        affectedTurnsLeft.remove(i);
        i--;
      }
    }
  }
  
  /* 
  public static void handleEnemyStatusTurn() throws InterruptedException{
    for(int i = 0; i < affectedCharacters.size(); i++){
      if(affectedCharacters.get(i).getIsEnemyCharacter()){
        affectedTurnsLeft.set(i, affectedTurnsLeft.get(i) - 1);
        if(applyTypes[indexOfType(affectedTypes.get(i))].equals("Passive")){
            applyPassiveEffect(affectedCharacters.get(i), affectedTypes.get(i));
        }

        if(affectedTurnsLeft.get(i) <= 0){
          System.out.println(affectedCharacters.get(i).getName() + " lost the [" + affectedTypes.get(i).toUpperCase() + "] effect!");
          Thread.sleep(1000);
          if(applyTypes[indexOfType(affectedTypes.get(i))].equals("One-time")){
            reverseOneTimeEffect(affectedCharacters.get(i), affectedTypes.get(i));
          }
          affectedCharacters.remove(i);
          affectedTypes.remove(i);
          affectedTurnsLeft.remove(i);
          i--;
        }
      }
    }
  }*/
}
