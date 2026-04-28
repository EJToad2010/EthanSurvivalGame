package src.ItemManager.Items;
import src.Characters.BasicCharacter;
import src.GameManagement.GameManager;
import src.GameManagement.Mechanics.ActionResult;
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
    public ActionResult useItem(BasicCharacter c, PlayerTeam playerTeam, EnemyTeam enemyTeam){
        ActionResult output = new ActionResult();
        System.out.println(c.getName() + " used " + getName() + " to recover from poison!");
        if(StatusEffect.hasStatusEffect(c, "Poison")){
            StatusEffect.removeStatusEffect(c, "Poison");
        } else{
            System.out.println("But nothing happened!");
        }
        return output;
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