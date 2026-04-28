package src.ItemManager;
import java.awt.Graphics;
import java.util.ArrayList;

import src.Characters.PlayerCharacter;
// A container for Item objects with specialized methods
// Contains stacks for Items, which each contain individual items
public class Inventory {
  // The container itself
  private ArrayList<ItemStack> inventory;
  
  // Constructors
  public Inventory(ArrayList<ItemStack> inventory){
    this.inventory = inventory;
  }
  
  public Inventory(){
    this(new ArrayList<ItemStack>());
  }
  
  // Getter method
  public ArrayList<ItemStack> getInventory(){
    return inventory;
  }
  
  // Return true if the inventory has a size of zero
  // Return false if the inventory has a size > 0
  public boolean isEmpty(){
    return inventory.size() == 0;
  }
  
  // Special add method
  // Detect if two items are equal. If so, add the original item's quantity.
  // If it is a unique item, add it to the end of the ArrayList.
  public void add(Item item, int amount){
    for(ItemStack stack : inventory){
      if(stack.getItem().equals(item)){
        stack.add(amount);
        return;
      }
    }
    inventory.add(new ItemStack(item, amount));
  }
  
  // If the item is found, remove it from the ArrayList
  public void remove(ItemStack item){
    deleteQuantityZero();
    inventory.remove(item);
  }

  public ItemStack get(int index){
    return inventory.get(index);
  }
  
  public void clear(){
    inventory.clear();
  }
  
  // Remove all items that have a quantity of zero
  private void deleteQuantityZero(){
    for(int i = 0; i < inventory.size(); i++){
      if(inventory.get(i).getQuantity() == 0){
        inventory.remove(i);
        i--;
      }
    }
  }
  
  // Return all items in the inventory separated by number
  /*public String getInventoryNumFormat(){
    String output = "";
    for(int i = 1; i <= inventory.size(); i++){
      output += i + ": " + inventory.get(i-1).getItem() + " x" + inventory.get(i-1).getQuantity();
      output += "\n";
    }
    return output;
  }*/
  
  // Print each item without the numbers in NumFormat
  /*public String toString(){
    String output = "INVENTORY:\n";
    if(inventory.size() == 0){
      output += "Empty\n";
    }
    for(ItemStack i : inventory){
      output += i.getItem();
      output += " x" + i.getQuantity();
      output += "\n";
    }
    return output;
  }*/

  // Return a String[] containing the name of every ItemStack in the inventory
  // Used for inputHandler
  public String[] getInventoryNames(){
    String[] output = new String[inventory.size()];
    for(int i = 0; i < inventory.size(); i++){
      output[i] = inventory.get(i).getItem().getName();
    }
    return output;
  }

  // Draw each individual ItemStack
  // (x, y) is the top left corner
  public void drawInventory(Graphics graphics, int x, int y, int width){
    spaceItems(x, y, width);
    for(ItemStack i : inventory){
      i.getItem().drawItem(graphics, i.getQuantity());
    }
  }

  // Space all ItemStacks equally to fit width and have a top-left corner of (x, y)
  public void spaceItems(int x, int y, int width){
    if(inventory.size() == 1){
      inventory.get(0).getItem().setPosition(x, y-(inventory.get(0).getItem().getHeight()-80));
      return;
    } else if(inventory.size() < 1){
      return;
    }
    int totalWidth = 0;
    for(ItemStack i : inventory){
      totalWidth += i.getItem().getWidth();
    }
    int totalWidthDiff = width - totalWidth;
    int avgWidthDiff = totalWidthDiff / (inventory.size()-1);
    int currentX = x;
    int j = 0;
    for(ItemStack i : inventory){
      i.getItem().setPosition(currentX, y-(i.getItem().getHeight()-80));
      if(j + 1 < inventory.size()){
        currentX += i.getItem().getWidth() + avgWidthDiff;
      }
      j++;
    }
  }
}
