package src.ItemManager.Items;
import src.Characters.BasicCharacter;
import src.Characters.PlayerCharacter;
import src.GameManagement.GameManager;
import src.ItemManager.Item;
import src.Misc.StatusEffect;
import src.Teams.EnemyTeam;
import src.Teams.PlayerTeam;
// Removes the poison status effect from a Character
public class Antidote extends Item{
    // Constructor
    public Antidote(int price){
        super("Antidote", "Heals a Character from poison.", price);
        // Technically a "healing" item?
        setUsageType("Heal");
    }

    // Unfinished
    public void useItem(BasicCharacter c, PlayerTeam playerTeam, EnemyTeam enemyTeam){
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

        System.out.println(c.getName() + " used " + getName() + " to recover from poison!");
        if(StatusEffect.hasStatusEffect(c, "Poison")){
            StatusEffect.removeStatusEffect(c, "Poison");
        } else{
            System.out.println("But nothing happened!");
        }
    }

    // Antidotes are equal if they are part of the same class
    public boolean equals(Object obj){
        if(this == obj){
        return true;
        }
        
        if(obj == null || !(obj instanceof Antidote)){
        return false;
        }
        
        return true;
    }
}
