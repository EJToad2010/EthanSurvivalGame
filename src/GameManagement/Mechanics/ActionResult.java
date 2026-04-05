package src.GameManagement.Mechanics;
import java.util.ArrayList;
// Acts as the transition between a Character's action and the displayed result
// Contains a list of messages to display as well as a corresponding list containing signals for the UI to handle
/*
   CURRENTLY IMPLEMENTED SIGNALS:
   ATTACK_PERFORMED: Used when a Character attacks another enemy.
   DEFENSE_RECEIVED: Used when a target receives defense strength from a teammate.
   DEFENSE_PERFORMED: Used when a target's damage is calculated and printed
*/
public class ActionResult {
    private ArrayList<String> messages;
    private ArrayList<String> signals;
    
    public ActionResult(){
        messages = new ArrayList<String>();
        signals = new ArrayList<String>();
    }
    
    // Getters
    public ArrayList<String> getMessages(){
        return messages;
    }
    public ArrayList<String> getSignals(){
        return signals;
    }

    public String getMessage(int index){
        if(index < 0 || index >= messages.size()){
            return "";
        }
        return messages.get(index);
    }

    public String getSignal(int index){
        if(index < 0 || index >= signals.size()){
            return "";
        }
        return signals.get(index);
    }

    // Add a new value to both ArrayLists
    public void add(String message, String signal){
        messages.add(message);
        signals.add(signal);
    }

    // Add a regular message and a blank signal
    public void add(String message){
        messages.add(message);
        signals.add("");
    }

    public void clear(){
        messages.clear();
        signals.clear();
    }
}
