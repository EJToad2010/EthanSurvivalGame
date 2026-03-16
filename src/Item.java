package src;
// A consumable item that can be used by both Players and Enemies with no differences
// Stores a singular item
class Item {
  // Attributes for an Item
  private String name;
  private String description;
  private int price;
  // Heal, Offense, Defense
  private String usageType;
  
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
  
  // Setter methods
  public void setPrice(int price){
    this.price = price;
  }
  
  public void setUsageType(String usageType){
    this.usageType = usageType;
  }

  public void useItem(BasicCharacter c, PlayerTeam playerTeam, EnemyTeam enemyTeam){
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
  
  // Overrided toString method
  public String toString(){
    String output = "";
    output += name + ": " + description;
    return output;
  }
  
  public String shopDisplay(ItemStack i){
    String output = "";
    output += getName() + ": " + getDescription();
    output += "\nPrice: " + getPrice() + "g   Stock: " + i.getQuantity();
    return output;
  }
}
