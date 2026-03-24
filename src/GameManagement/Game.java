package src.GameManagement;
import src.GameManagement.GameState.GameState;
// Top level class that stores the current state and panel
// Runs required actions every frame
// THIS CLASS SHOULD EVENTUALLY REPLACE GAMEMANAGER WHEN I FINISH RESTRUCTURING
public class Game {
    private GameState currentGameState;

    public Game(){

    }

    // Master function for the project
    public void run(){
        while(true){
            currentGameState.update();
        }
    }

    // Get the currentState
    public GameState getCurrentGameState(){
        return currentGameState;
    }

    // Set the currentState to a new state
    public void setCurrentGameState(GameState newGameState){
        currentGameState = newGameState;
    }
}
