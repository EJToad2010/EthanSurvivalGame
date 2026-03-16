package src;
// A shop is a special designated area where the Player can buy Items
// with the coins that they have earned.
// It has a special type of inventory.
class Shop {
  // All the items that are available in the shop
  private Inventory shopInventory;
  // Types: Adventure, Offense, Defense, Rare
  private String shopType;
  // Constructors
  public Shop(Inventory shopInventory, String shopType){
    this.shopInventory = shopInventory;
    this.shopType = shopType;
  }
  
  public Shop(){
    this(new Inventory(), "Adventure");
  }
  
  public void resetStock(){
    shopInventory.clear();
    if(shopType.equals("Adventure")){
      // Adventure type shops contain healing items and items used outside of battle.
      shopInventory.add(new HealthPotion(20, 20.0), 3);
      shopInventory.add(new HealthPotion(45, 50.0), 1);
      shopInventory.add(new HealthPool(30, 10.0), 3);
      shopInventory.add(new HealthPool(65, 25.0), 1);
    }
  }
  
  public void handleShopLoop(PlayerTeam playerTeam) throws InterruptedException {
    resetStock();
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
      String message = "What would you like to do?\n";
      message += "1: Check stock\n";
      message += "2: Buy item\n";
      message += "3: Get balance\n";
      message += "4: Check inventory\n";
      message += "5: Leave\n";
      int input = GameManager.obtainInput(message, 1, 5, false);
      
      if(input == 1){
        System.out.println(this);
        GameManager.anythingToContinue();
      } else if(input == 2){
        message = "What would you like to buy?\n";
        int itemNum = GameManager.obtainInputWithCancel(message + shopInventory.getInventoryNumFormat(), 1, shopInventory.getInventory().size(), true);
      	if(itemNum != -1){
          ItemStack i = shopInventory.get(itemNum);
          if(i.getItem().getPrice() > playerTeam.getCoinBalance()){
            System.out.println("You cannot afford this item.");
          } else{
            Item item = i.getItem();
            System.out.println("You have bought " + item.getName() + " for " + item.getPrice() + "g.");
            playerTeam.increaseCoinBalance(-item.getPrice());
            playerTeam.getPlayerInventory().add(item, 1);
            i.remove(1);
            if(i.getQuantity() <= 0){
              shopInventory.remove(i);
            }
          }
          GameManager.anythingToContinue();
        }
      } else if(input == 3){
        System.out.println("You have " + playerTeam.getCoinBalance() + "g");
        GameManager.anythingToContinue();
      } else if(input == 4){
        System.out.println(playerTeam.getPlayerInventory());
      } else{
        break;
      }
    }
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
  
  public String toString(){
    String output = "";
    output += "SHOP STOCK:\n";
    for(ItemStack i : shopInventory.getInventory()){
      output += i.getItem().shopDisplay(i);
      output += "\n";
    }
    return output;
  }
}
