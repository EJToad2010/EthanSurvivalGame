package src.GameManagement.Mechanics;

import src.GameManagement.Game;
import src.GameManagement.GameState.CharacterSelectState;
import src.GameManagement.GameState.TournamentState;
import src.GameManagement.GameState.BattleState;

// Controls the flow of a singular Day
public class DayManager {
    private Game game;
    private int currentPhase = 0;
    // Constants used for each phase
    private final int NEW_DAY = 0;
    private final int CHARACTER_SELECT = 1;
    private final int TOURNAMENT = 2;
    private final int BATTLE = 3;
    private final int SHOP = 4;
    private final int CAMPFIRE = 5;
    private final int DAY_END = 6;
    
    public DayManager(Game game){
        this.game = game;
    }

    // Reset all attributes, starting a new day
    public void startDay(){
        currentPhase = 0;
    }

    // Called by GameState classes whenever it is ready to move to the next phase
    public void nextPhase(){
        currentPhase++;
        // Character select phases can only happen if the player's battle capacity has not been reached
        if(currentPhase == CHARACTER_SELECT && game.getGameData().getPlayerTeamArr().size() >= game.getGameData().getPlayerBattleCapacity()){
            currentPhase++;
        }
        // Tournament phases can only happen once every two days
        /*if(currentPhase == TOURNAMENT && game.getGameData().getDayNum() % 2 != 0){
            currentPhase++;
        }*/
        // If on the last phase, automatically start the next day
        if(currentPhase == TOURNAMENT){
            currentPhase++;
        }
        if(currentPhase > DAY_END){
            game.getGameData().nextDay();
            currentPhase = NEW_DAY;
        }
        // Load the next phase automatically
        loadPhase();
    }

    // Open the correct GameState depending on the current phase
    public void loadPhase(){
        if(currentPhase == NEW_DAY){
            // Not implemented yet
            nextPhase();
        } else if(currentPhase == CHARACTER_SELECT){
            game.setCurrentGameState(new CharacterSelectState(game, this));
        } else if(currentPhase == TOURNAMENT){
            //game.setCurrentGameState(new TournamentState(game, this));
        } else if(currentPhase == BATTLE){
            game.setCurrentGameState(new BattleState(game, this));
        } else if(currentPhase == SHOP){
            // Not implemented yet
            nextPhase();
        } else if(currentPhase == CAMPFIRE){
            // Not implemented yet
            nextPhase();
        } else if(currentPhase == DAY_END){
            // Not implemented yet
            nextPhase();
        }
    }
}
