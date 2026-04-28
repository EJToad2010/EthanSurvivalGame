package src.ItemManager;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;

import src.Characters.BasicCharacter;
import src.GameManagement.Mechanics.ActionResult;
import src.GameManagement.UI.ImageManager;
import src.GameManagement.UI.UIManager;
import src.Teams.EnemyTeam;
import src.Teams.PlayerTeam;

// A consumable item that can be used by both Players and Enemies with no differences
// Stores a singular item
// TODO: Add offensive items to add to an offensive Shop
public class Item {
  // Attributes for an Item
  private String name;
  private String description;
  private int price;
  // How many Characters the Item targets per usage
  protected int targetAmount = 1;
  // Heal, Offense, Defense
  private String usageType;
  // Location on screen
  private int x = 0;
  private int y = 0;
  // Used to draw graphics
  protected int width = 80;
  protected int height = 80;
  private Image itemImage;
  
  // Constructor that requires all parameters
  public Item(String name, String description, int price){
    this.name = name;
    this.description = description;
    this.price = price;
  }
  
  public Item(){
    this("Item", "An item.", 20);
  }
  
  // Getter methods
  public String getName(){
    return name;
  }
  
  public String getDescription(){
    return description;
  }
  
  public int getPrice(){
    return price;
  }
  
  public String getUsageType(){
    return usageType;
  }

  public int getX(){
    return x;
  }

  public int getY(){
    return y;
  }

  public int getWidth(){
    return width;
  }

  public int getHeight(){
    return height;
  }

  // Setter methods
  public void setPrice(int price){
    this.price = price;
  }
  
  public void setUsageType(String usageType){
    this.usageType = usageType;
  }

  public void setSize(int width, int height){
    this.width = width;
    this.height = height;
  }

  public void setPosition(int x, int y){
    this.x = x;
    this.y = y;
  }

  public void setImage(String path){
    itemImage = ImageManager.loadImage(path);
  }

  public void setTargetAmount(int targetAmount){
    this.targetAmount = targetAmount;
  }

  public ActionResult useItem(BasicCharacter c, PlayerTeam playerTeam, EnemyTeam enemyTeam){
    return null;
  }
  
  // Two Items are equal if they have the same name and description
  // Overrided equals method
  public boolean equals(Object obj){
    if(this == obj){
      return true;
    }
    
    if(obj == null || !(obj instanceof Item)){
      return false;
    }
    
    Item other = (Item) obj;
    return other.name.equals(name) && other.description.equals(description);
  }
  
  /* Overrided toString method
  public String toString(){
    String output = "";
    output += name + ": " + description;
    return output;
  }
  
  // Print message used when a shop displays its stock
  public String shopDisplay(ItemStack i){
    String output = "";
    output += getName() + ": " + getDescription();
    output += "\nPrice: " + getPrice() + "g   Stock: " + i.getQuantity();
    return output;
  }*/
 // Draw the given Item sprite
 public void drawItem(Graphics graphics, int quantity){
  graphics.drawImage(itemImage, x, y, null);
  UIManager.setTextColor(graphics, Color.WHITE);
  UIManager.findMaxFontSize(name + ": x" + quantity, graphics, width, 20, true, true);
  UIManager.drawCenteredStringInBox(graphics, name + ": x" + quantity, x, y-getHeight()-20, width, 20);
 }
}
