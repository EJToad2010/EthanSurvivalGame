package src.ItemManager.Items;

import src.Characters.BasicCharacter;
import src.Characters.EnemyCharacter;
import src.Characters.PlayerCharacter;
import src.GameManagement.Mechanics.ActionResult;
import src.ItemManager.Item;
import src.ItemManager.ItemStack;
import src.Teams.EnemyTeam;
import src.Teams.PlayerTeam;

// An item that heals all members of a Player's team by a given amount
public class HealthPool extends Item{
  // Attributes needed just for a Health Pool
  private double healStrength;
  
  // Constructor
  // Name and description will always be default. Only price, quantity, and healStrength
  // can be manually modified.
  public HealthPool(int price, double healStrength){
    super("Health Pool", "Heals all characters in your team.", price);
    this.healStrength = healStrength;
    setUsageType("Heal");
    setTargetAmount(999);
    setImage("src/Images/healthpool.png");
  }
  public HealthPool(double healStrength){
    this(30, healStrength);
  }
  
  public HealthPool(){
    this(45, 15.0);
  }
  
  // Getter method
  public double getHealStrength(){
    return healStrength;
  }
  
  // Heal all members of the character's team
  public ActionResult useItem(BasicCharacter c, PlayerTeam playerTeam, EnemyTeam enemyTeam){
    ActionResult output = new ActionResult();
    System.out.println(c.getName() + " used " + getName() + " to heal their team!");
    if(c.getIsEnemyCharacter()){
      for(EnemyCharacter e : enemyTeam.getEnemyTeam()){
        System.out.println(e.getName() + " was healed for " + Math.min(healStrength, e.getMaxHP() - e.getCurrentHP()) + " HP!");
        e.changeCurrentHP(healStrength);
      }
    } else{
      for(PlayerCharacter p : playerTeam.getPlayerTeam()){
        System.out.println(p.getName() + " was healed for " + Math.min(healStrength, p.getMaxHP() - p.getCurrentHP()) + " HP!");
        p.changeCurrentHP(healStrength);
      }
    }
    return output;
  }
  
  // HealthPotions are equal if they have the same name, description, and heal strength.
  public boolean equals(Object obj){
    if(this == obj){
      return true;
    }
    
    if(obj == null || !(obj instanceof HealthPool)){
      return false;
    }
    
    HealthPool other = (HealthPool) obj;
    return other.getName().equals(getName()) && other.getDescription().equals(getDescription()) && other.healStrength == healStrength;
  }
  
  // Overrided toString method
  public String toString(){
    String output = "";
    output += getName() + ": (+" + healStrength + " HP)";
    return output;
  }
  
  // Print message used when a shop displays its stock
  public String shopDisplay(ItemStack i){
    String output = "";
    output += getName() + ": (+" + healStrength + " HP) " + getDescription();
    output += "\nPrice: " + getPrice() + "g   Stock: " + i.getQuantity();
    return output;
  }
}
