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
    private ArrayList<Double> amounts;
    
    public ActionResult(){
        messages = new ArrayList<String>();
        signals = new ArrayList<String>();
        amounts = new ArrayList<Double>();
    }
    
    // Getters
    public ArrayList<String> getMessages(){
        return messages;
    }
    public ArrayList<String> getSignals(){
        return signals;
    }
    public ArrayList<Double> getAmounts(){
        return amounts;
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

    public double getAmount(int index){
        if(index < 0 || index >= amounts.size()){
            return 0.0;
        }
        return amounts.get(index);
    }

    // Get the amount corresponding to the first occurrence of a specific signal
    public Double getAmount(String signal){
        int foundIndex = 0;
        for(int i =0; i < messages.size(); i++){
            if(signals.get(i).equals(signal)){
                foundIndex = i;
                break;
            }
        }
        return amounts.get(foundIndex);
    }

    // Add the values of another ActionResult object to this one
    public void add(ActionResult actionResult){
        for(int i = 0; i < actionResult.getMessages().size(); i++){
            messages.add(actionResult.getMessage(i));
            signals.add(actionResult.getSignal(i));
            amounts.add(actionResult.getAmount(i));
        }
    }

    // Add a new value to all ArrayLists
    public void add(String message, String signal, double amount){
        messages.add(message);
        signals.add(signal);
        amounts.add(amount);
    }

    public void add(String message, String signal){
        messages.add(message);
        signals.add(signal);
        amounts.add(0.0);
    }

    public void add(String signal, double amount){
        messages.add("");
        signals.add(signal);
        amounts.add(amount);
    }

    // Add a regular message and a blank signal
    public void add(String message){
        messages.add(message);
        signals.add("");
        amounts.add(0.0);
    }

    public void clear(){
        messages.clear();
        signals.clear();
        amounts.clear();
    }

    // use for debugging
    public String toString(){
        String output = "";
        for(int i = 0; i < messages.size(); i++){
            output += messages.get(i);
            output += " " + signals.get(i);
            output += " " + amounts.get(i);
            output += "\n";
        }
        return output;
    }
}
