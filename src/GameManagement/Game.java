package src.GameManagement;
import src.GameManagement.GameState.GameState;
import src.GameManagement.GameState.StartScreenState;
import src.GameManagement.Mechanics.DayManager;
import src.GameManagement.UI.GamePanel;
// Top level class that stores the current state and panel
// Runs required actions every frame
// THIS CLASS SHOULD EVENTUALLY REPLACE GAMEMANAGER WHEN I FINISH RESTRUCTURING
public class Game {
    private GameData gameData = new GameData();
    private GameState currentGameState;
    private GamePanel panel;
    private DayManager dayManager = new DayManager(this);

    public Game(GamePanel panel){
        this.panel = panel;
        currentGameState = new StartScreenState(this, dayManager);
        currentGameState.onEnter(panel);
    }

    // Master function for the project
    public void run(){
        while(true){
            currentGameState.update();
            panel.repaint();
            try{
                // 16 ms is close to 60 fps
                Thread.sleep(16);
            } catch(InterruptedException e){
                e.printStackTrace();
            }
        }
    }

    // Get the currentState
    public GameState getCurrentGameState(){
        return currentGameState;
    }

    // Get the object storing game memory
    public GameData getGameData(){
        return gameData;
    }

    // Get the object storing the game flow during a day
    public DayManager getDayManager(){
        return dayManager;
    }

    // Set the currentState to a new state
    public void setCurrentGameState(GameState newGameState){
        currentGameState.onExit(panel);
        currentGameState = newGameState;
        currentGameState.onEnter(panel);
    }
}
