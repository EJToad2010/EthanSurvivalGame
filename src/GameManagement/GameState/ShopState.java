package src.GameManagement.GameState;

import src.GameManagement.Game;
import src.GameManagement.Mechanics.DayManager;

public class ShopState extends GameState{
    // Constants used to define each major step
    private final int PROMPT_ENTER = 0;
    private final int ENTER_SHOP = 1;
    private final int SELECT_ACTION = 2;
    private final int SELECT_ITEM_BUY = 3;
    
    public ShopState(Game g, DayManager dayManager){
        super(g, dayManager);
    }
}

