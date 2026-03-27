package src.ItemManager.Items;
import src.Characters.BasicCharacter;
<<<<<<< HEAD
import src.Characters.PlayerCharacter;
import src.GameManagement.GameManager;
=======
>>>>>>> 7cb65a3c0f30918ced8ebae0e59c39d14a9b5049
import src.ItemManager.Item;
import src.Misc.StatusEffect;
import src.Teams.EnemyTeam;
import src.Teams.PlayerTeam;
// Brings a dead Character back to half health
// A Character that is alive must use this
public class RevivalPotion extends Item{
    // Constructor
    public RevivalPotion(int price){
        super("Revival Potion", "Brings a dead Character back to life at half HP. Must be used by an alive Character.", price);
        setUsageType("Heal");
    }

    public void useItem(BasicCharacter c, PlayerTeam playerTeam, EnemyTeam enemyTeam){
<<<<<<< HEAD
        // Select a Character to heal
        String message = "\nSelect a character to heal with: \n" + playerTeam.getPlayerTeamNumFormat();
        int selectedCharacterIndex;
        PlayerCharacter selectedCharacter;
        // Ensure the character is not dead
        while(true){
          selectedCharacterIndex = GameManager.obtainInput(message, 1, playerTeam.getPlayerTeam().size(), true);
          selectedCharacter = playerTeam.getCharacterAt(selectedCharacterIndex);
          if(!selectedCharacter.getIsDead()){
            System.out.println("Invalid input. This character is alive!");
          } else{
            break;
          }
        }
        
=======
>>>>>>> 7cb65a3c0f30918ced8ebae0e59c39d14a9b5049
        System.out.println(c.getName() + " used " + getName() + " to recover from poison!");
        if(StatusEffect.hasStatusEffect(c, "Poison")){
            StatusEffect.removeStatusEffect(c, "Poison");
        } else{
            System.out.println("But nothing happened!");
        }
    }

    // Revival Potions are equal if they are part of the same class
    public boolean equals(Object obj){
        if(this == obj){
        return true;
        }
        
        if(obj == null || !(obj instanceof RevivalPotion)){
        return false;
        }
        
        return true;
    }
}
