package src.ItemManager;

// Stores a stack of identical Items
public class ItemStack {
    // Item object stored in a stack
    private Item item;
    // How many copies of that item exists
    private int  quantity;

    // Constructor requires both parameters
    public ItemStack(Item item, int quantity){
        this.item = item;
        this.quantity = quantity;
    }

    // Getter methods
    public Item getItem(){
        return item;
    }

    public int getQuantity(){
        return quantity;
    }

    // Setter methods modifying quantity
    public void add(int amount){
        quantity += amount;
    }

    public void remove(int amount){
        quantity -= amount;
    }
}
