package src;
// A tournament is an area where Characters can gamble coins for a chance to earn XP and more money.
// Tournaments are skill-based, with each Character type having a unique mini-game.
public class Tournament {
    // Determines how difficult it is to win the tournament and earn money
    // 0 (easiest) - 3 (hardest)
    private int difficulty;

    // Constructor that takes no parameters
    public Tournament(){
    }

    // Handles all of the user's decision making while they are in a Tournament
    public void handleTournamentLoop(PlayerTeam playerTeam) throws InterruptedException{
        System.out.println("You enter a large, open field.");
        Thread.sleep(1000);
        System.out.println("An announcer says: WELCOME TO THE TRIAL OF THE FIGHTERS!");
        Thread.sleep(1000);
        GameManager.anythingToContinue();
        System.out.println("A worker asks you to select a division to compete in:");
        System.out.println("A worker tells you to select one Character to compete in the tournament:");
        String message = "\nSelect a character: \n" + playerTeam.getPlayerTeamNumFormat();
        int selectedCharacterIndex = GameManager.obtainInput(message, 1, playerTeam.getPlayerTeam().size(), true);
    }
}
