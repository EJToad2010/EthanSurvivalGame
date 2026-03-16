package src;

// Stores a stack of identical Items
public class ItemStack {
    private Item item;
    private int  quantity;

    public ItemStack(Item item, int quantity){
        this.item = item;
        this.quantity = quantity;
    }

    public Item getItem(){
        return item;
    }

    public int getQuantity(){
        return quantity;
    }

    public void add(int amount){
        quantity += amount;
    }

    public void remove(int amount){
        quantity -= amount;
    }
}
