package src.ItemManager;
import java.util.ArrayList;
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
  public String getInventoryNumFormat(){
    String output = "";
    for(int i = 1; i <= inventory.size(); i++){
      output += i + ": " + inventory.get(i-1).getItem() + " x" + inventory.get(i-1).getQuantity();
      output += "\n";
    }
    return output;
  }
  
  // Print each item without the numbers in NumFormat
  public String toString(){
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
  }
}
