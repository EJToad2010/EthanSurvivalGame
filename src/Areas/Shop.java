package src.Areas;

import src.GameManagement.GameManager;
import src.ItemManager.Inventory;
import src.ItemManager.Item;
import src.ItemManager.ItemStack;
import src.ItemManager.Items.Antidote;
import src.ItemManager.Items.HealthPool;
import src.ItemManager.Items.HealthPotion;
import src.ItemManager.Items.Antidote;
import src.Teams.PlayerTeam;

// A shop is a special designated area where the Player can buy Items
// with the coins that they have earned.
// It has a special type of inventory.
public class Shop {
  // All the items that are available in the shop
  private Inventory shopInventory;
  // Types: Adventure, Offense, Defense, Rare
  // Currently, only Adventure is implemented
  private String shopType;
  // Constructors
  public Shop(Inventory shopInventory, String shopType){
    this.shopInventory = shopInventory;
    this.shopType = shopType;
  }
  
  public Shop(){
    this(new Inventory(), "Adventure");
  }
  
  // Defines what items are placed into each type of shop
  public void resetStock(){
    // TODO: Implement shop types for other shop variants
    // TODO: Implement new item types to be placed in those new shops
    shopInventory.clear();
    if(shopType.equals("Adventure")){
      // Adventure type shops contain healing items.
      shopInventory.add(new HealthPotion(20, 20.0), 3);
      shopInventory.add(new HealthPotion(45, 50.0), 1);
      shopInventory.add(new HealthPool(30, 10.0), 3);
      shopInventory.add(new HealthPool(50, 25.0), 1);
      shopInventory.add(new Antidote(20), 3);
    }
  }
  
  // Handle all user's decision making while they are in the shop
  public void handleShopLoop(PlayerTeam playerTeam) throws InterruptedException {
    resetStock();
    // Welcome message
    System.out.print("A sign is on the wall. it says: WELCOME TO ");
    if(shopType.equals("Adventure")){
      System.out.println("RASCAL'S HIDEOUT");
    } else if(shopType.equals("Offense")){
      System.out.println("EXECUTIONER'S KITCHEN");
    } else if(shopType.equals("Defense")){
      System.out.println("SERENITY PEAK");
    }
    Thread.sleep(1000);
    // Have multiple possible reactions from the merchant simply to make it more visually interesting
    int merchantReaction = (int)(Math.random() * 5);
    if(merchantReaction == 0){
      System.out.println("The merchant frowns when he sees you.");
    } else if(merchantReaction == 1){
      System.out.println("The merchant doesn't even bother to look at you.");
    } else if(merchantReaction == 2){
      System.out.println("The merchant looks at you but does not say anything.");
    } else if(merchantReaction == 3){
      System.out.println("The merchant smiles slightly when he sees you.");
    } else{
      System.out.println("The merchant greets you warmly.");
    }
    
    Thread.sleep(1000);
    
    while(true){
      // Prompt the user for one action
      String message = "What would you like to do?\n";
      message += "1: Check stock\n";
      message += "2: Buy item\n";
      message += "3: Get balance\n";
      message += "4: Check inventory\n";
      message += "5: Leave\n";
      int input = GameManager.obtainInput(message, 1, 5, false);
      
      if(input == 1){
        // Check stock
        System.out.println(this);
        GameManager.anythingToContinue();
      } else if(input == 2){
        // Buy item
        message = "What would you like to buy?\n";
        int itemNum = GameManager.obtainInputWithCancel(message, 1, shopInventory.getInventory().size(), true);
      	if(itemNum != -1){
          ItemStack i = shopInventory.get(itemNum);
          // Check if the player has enough coins to buy the item
          if(i.getItem().getPrice() > playerTeam.getCoinBalance()){
            System.out.println("You cannot afford this item.");
          } else{
            // Avoid duplicating an object reference when an item is added
            Item item = i.getItem();
            System.out.println("You have bought " + item.getName() + " for " + item.getPrice() + "g.");
            playerTeam.increaseCoinBalance(-item.getPrice());
            playerTeam.getPlayerInventory().add(item, 1);
            // Update the shop's stock for that item
            i.remove(1);
            if(i.getQuantity() <= 0){
              shopInventory.remove(i);
            }
          }
          GameManager.anythingToContinue();
        }
      } else if(input == 3){
        // Get balance
        System.out.println("You have " + playerTeam.getCoinBalance() + "g");
        GameManager.anythingToContinue();
      } else if(input == 4){
        // Check inventory
        System.out.println(playerTeam.getPlayerInventory());
      } else{
        // Leave shop
        break;
      }
    }
    // Randomly generated exit message
    if(merchantReaction == 0){
      System.out.println("The merchant sighs in relief as you head towards the exit.");
    } else if(merchantReaction == 1){
      System.out.println("The merchant doesn't notice your departure.");
    } else if(merchantReaction == 2){
      System.out.println("The merchant says goodbye.");
    } else if(merchantReaction == 3){
      System.out.println("The merchant tells you to come back another day.");
    } else{
      System.out.println("The merchant thanks you for coming.");
    }
    System.out.println("You have left the shop.");
    GameManager.anythingToContinue();
  }
  
  // Prints all ItemStacks the shop contains
  public String toString(){
    String output = "";
    output += "SHOP STOCK:\n";
    for(ItemStack i : shopInventory.getInventory()){
      output += i.getItem();//.shopDisplay(i);
      output += "\n";
    }
    return output;
  }
}
