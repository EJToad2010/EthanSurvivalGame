package src.ItemManager.Items;

import src.Characters.BasicCharacter;
import src.ItemManager.Item;
import src.ItemManager.ItemStack;
import src.Teams.EnemyTeam;
import src.Teams.PlayerTeam;

// An item that heals one character by a given amount
public class HealthPotion extends Item {
  // Attributes needed just for a Health Potion
  private double healStrength;
  
  // Constructor
  // Name and description will always be default. Only price, quantity, and healStrength
  // can be manually modified.
  public HealthPotion(int price, double healStrength){
    super("Health Potion", "Heals one character.", price);
    this.healStrength = healStrength;
    setUsageType("Heal");
  }
  public HealthPotion(double healStrength){
    this(20, healStrength);
  }
  
  public HealthPotion(){
    this(20, 20.0);
  }
  
  // Getter method
  public double getHealStrength(){
    return healStrength;
  }
  
  public void useItem(BasicCharacter c, PlayerTeam playerTeam, EnemyTeam enemyTeam){
    c.changeCurrentHP(healStrength);
    System.out.println(c.getName() + " used " + getName() + " to heal " + healStrength + " HP!");
  }
  
  // HealthPotions are equal if they have the same name, description, and heal strength.
  public boolean equals(Object obj){
    if(this == obj){
      return true;
    }
    
    if(obj == null || !(obj instanceof HealthPotion)){
      return false;
    }
    
    HealthPotion other = (HealthPotion) obj;
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
