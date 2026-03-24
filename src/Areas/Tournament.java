package src.Areas;
import java.io.IOException;

import src.Characters.PlayerCharacter;
import src.Characters.PlayerCharacters.Archer;
import src.Characters.PlayerCharacters.Knight;
import src.Characters.PlayerCharacters.Wizard;
import src.GameManagement.GameManager;
import src.Teams.PlayerTeam;
// A tournament is an area where Characters can gamble coins for a chance to earn a large sum of money
// Tournaments are skill-based, with each Character type having a unique mini-game.
public class Tournament {
    // Determines how difficult it is to win the tournament and earn money
    // 1 (easiest) - 4 (hardest)
    private int difficulty;
    private int playersTurn;
    // 4 players total. Keep track of each player's score.
    private double[] allScores = new double[4];
    // True if still in the game, false if has been eliminated
    private boolean[] allEliminationStatus = new boolean[4];
    private String[] contestantNames = new String[4];

    // Constructor that takes no parameters
    public Tournament(){
        for(int i = 0; i < allScores.length; i++){
            allScores[i] = 0.0;
            allEliminationStatus[i] = true;
            contestantNames[i] = "Contestant " + (i+1);
        }
    }

    // Handles all of the user's decision making while they are in a Tournament
    public void handleTournamentLoop(PlayerTeam playerTeam) throws IOException, InterruptedException{
        int entryFee;
        
        System.out.println("You enter a large, open field.");
        Thread.sleep(1000);
        System.out.println("An announcer says: WELCOME TO THE TRIAL OF THE FIGHTERS!");
        Thread.sleep(1000);
        GameManager.anythingToContinue();
        // Select a difficulty
        String message = "\nA worker asks you to select a division to compete in:\n";
        message += "1: Novice\n2: Intermediate\n3: Advanced\n4: Elite\n";
        while(true){
            boolean success;
            int selectedDifficultyIndex = GameManager.obtainInputWithCancel(message, 1, 4, false);
            System.out.println("");
            if(selectedDifficultyIndex == 1){
                entryFee = 25;
                success = promptSpendingCoins(25, playerTeam);
            } else if(selectedDifficultyIndex == 2){
                entryFee = 50;
                success = promptSpendingCoins(50, playerTeam);
            } else if(selectedDifficultyIndex == 3){
                entryFee = 100;
                success = promptSpendingCoins(100, playerTeam);
            } else if(selectedDifficultyIndex == 4){
                entryFee = 250;
                success = promptSpendingCoins(250, playerTeam);
            } else{
                System.out.println("You have chosen to leave the tournament.");
                GameManager.anythingToContinue();
                return;
            }
            if(success){
                difficulty = selectedDifficultyIndex;
                break;
            }
            System.out.println("");
        }
        // Confirm selected difficulty choice
        if(difficulty == 1){
            System.out.println("You have entered the Novice division.");
        } else if(difficulty == 2){
            System.out.println("You have entered the Intermediate division.");
        } else if(difficulty == 3){
            System.out.println("You have entered the Advanced division.");
        } else{
            System.out.println("You have eneterd the Elite division.");
        }
        GameManager.anythingToContinue();
        GameManager.clearScreen();

        // Select a character
        message = "A worker tells you to select one Character to compete in the tournament:\n";
        message += "\nSelect a character: \n" + playerTeam.getPlayerTeamNumFormat();
        int selectedCharacterIndex;
        while(true){
            selectedCharacterIndex = GameManager.obtainInput(message, 1, playerTeam.getPlayerTeam().size(), true);
            if(playerTeam.getPlayerTeam().get(selectedCharacterIndex).getIsDead()){
                System.out.println("Invalid input. This character is dead!");
                GameManager.anythingToContinue();
            } else{
                break;
            }
        }
        GameManager.clearScreen();
        PlayerCharacter selectedCharacter = playerTeam.getPlayerTeam().get(selectedCharacterIndex);
        // Decide what the player's turn number will be (1-4)
        playersTurn = (int)(Math.random() * 4) + 1;
        // Initialize hasWon variable
        int playerRank = -1;
        // Find the type of the given Character
        if(selectedCharacter.getType().equals("Knight")){
            playerRank = knightMinigame((Knight) selectedCharacter, playerTeam);
        } else if(selectedCharacter.getType().equals("Archer")){
            playerRank = archerMinigame((Archer) selectedCharacter, playerTeam);
        } else if(selectedCharacter.getType().equals("Wizard")){
            playerRank = wizardMinigame((Wizard) selectedCharacter, playerTeam);
        }
        // Reward multiplier based on difficulty
        // 1: 1.5, 2: 2, 3: 2.5, 4: 3
        double difficultyMultiplier = 1 + ((double) (difficulty) * 0.25);
        // Reward multiplier based on placement
        double placementMultiplier;
        // Print placement results
        Thread.sleep(1000);
        if(playerRank == 1){
            System.out.println("Congratulations! You have won!");
            placementMultiplier = 2.5;
        } else if(playerRank == 2){
            System.out.println("You have received second.");
            placementMultiplier = 1.5;
        } else if(playerRank == 3){
            System.out.println("You have received third.");
            placementMultiplier = 0.25;
        } else if(playerRank == 4){
            System.out.println("You have received fourth.");
            placementMultiplier = 0;
        } else{
            // Debug
            System.out.println("Calculation error: " + playerRank);
            System.out.println(allScores[playersTurn-1]);
            placementMultiplier = 0;
        }
        System.out.println("");
        Thread.sleep(1000);
        GameManager.anythingToContinue();

        // Handle giving rewards
        int reward = (int)(entryFee * difficultyMultiplier * placementMultiplier);
        if(reward >= entryFee){
            System.out.println("\nCongratulations! You have won " + reward + "g.");
        } else{
            System.out.println("\nYou have walked away with " + reward + "g.");
        }
        playerTeam.increaseCoinBalance(reward);
        GameManager.anythingToContinue();
        GameManager.clearScreen();
        if(playerRank < 3){
            System.out.println("Your team walks away from the tournament excited of their winnings.");
        } else{
            System.out.println("Your team walks away from the tournament disappointed in their performance.");
        }
        GameManager.anythingToContinue();
    }

    // Run Knight minigame and AI
    // Trial of Strength
    private int knightMinigame(Knight selectedKnight, PlayerTeam playerTeam) throws InterruptedException{
        // Tutorial of the Trial of Strength
        if(!GameManager.skipTutorial){
            System.out.println("Welcome to the TRIAL OF STRENGTH.");
            Thread.sleep(2000);
            System.out.println(selectedKnight.getName() + " will be given a punching bag.");
            Thread.sleep(1000);
            System.out.println("They must hit this bag as hard as possible to win.");
            Thread.sleep(1000);
            System.out.println("Every turn, you will be given the chance to either charge " + selectedKnight.getName() + "'s attack or release their strength.");
            Thread.sleep(1000);
            System.out.println("Charging an attack repeatedly increases your Knight's strength. Don't overwork your Knight, or else they will become exhausted.");
            Thread.sleep(1000);
            System.out.println("How far can you push " + selectedKnight.getName() + "'s limits before failing?");
            Thread.sleep(2000);
            System.out.println("There will be three rounds.");
            Thread.sleep(1000);
            System.out.println("There will be three other Knights competing. Be the strongest and most accurate to win.");
            Thread.sleep(1000);
            GameManager.anythingToContinue();
        }

        // Three rounds
        for(int i = 1; i <= 3; i++){
            GameManager.clearScreen();
            System.out.println("ROUND " + i);
            if(i == 1){
                printPlayerTurnNumber();
            }
            for(int j = 0; j < allScores.length; j++){
                GameManager.clearScreen();
                if(j+1 == playersTurn){
                    // Player Turn
                    System.out.println("It is your turn!\n");
                    Thread.sleep(1000);
                    double score = selectedKnight.chargeAttackMinigame();
                    score = GameManager.truncate(score, 2);
                    if(score == 0){
                        System.out.println(selectedKnight.getName() + " missed their attack!");
                    } else{
                        System.out.println(selectedKnight.getName() + " hit the punching bag for " + score + " HP!");
                        allScores[j] += score;
                    }
                } else{
                    // AI Turn
                    System.out.println("It is " + contestantNames[j] + "'s turn!");
                    Thread.sleep(2000);
                    // Difficulty 1 is 30.0 base atk, Diff 2 is 39.0, Diff 3 is 48.0, Diff 4 is 57.0
                    double aiBaseAttackStrength = 30.0 * 1+(0.3 * (difficulty-1));
                    // Chance that an AI misses their attack
                    // In Advanced and Elite, the AI will never miss.
                    double missChance = 0.5 - ((difficulty+2) / 10);

                    if(Math.random() < missChance){
                        // AI missed their attack
                        System.out.println(contestantNames[j] + " missed their attack!");
                    } else{
                        // Use the same damage calculations as the Player based on a randomly generated stamina level
                        int stamina = (int)(Math.random() * (75/difficulty)) + 1;
                        double charge = (100 - stamina) / 100.0;
                        double multiplier = 1 + (charge * charge * 3.5);
                        double score = aiBaseAttackStrength * multiplier;
                        score = GameManager.truncate(score, 2);
                        allScores[j] += score;
                        System.out.println(contestantNames[j] + " hit the punching bag for " + score + " HP!");
                    }
                }
                // Print leaderboard after every turn
                Thread.sleep(1000);
                GameManager.anythingToContinue();
                printStandings();
                GameManager.anythingToContinue();
            }
        }
        return getPlayerRankStandings();
    }

    // Run Archer minigame and AI
    // Trial of Precision
    private int archerMinigame(Archer selectedArcher, PlayerTeam playerTeam) throws IOException, InterruptedException{
        // Tutorial of the Trial of Precision
        if(!GameManager.skipTutorial){
            System.out.println("Welcome to the TRIAL OF PRECISION.");
            Thread.sleep(2000);
            System.out.println(selectedArcher.getName() + " will be given a target to shoot at.");
            Thread.sleep(1000);
            System.out.println("They must aim for the bullseye to score as many points as possible..");
            Thread.sleep(1000);
            System.out.println("Every round, you will be shown a moving target in a bar, representing " + selectedArcher.getName() + "'s bow and arrow.");
            Thread.sleep(1000);
            System.out.println("You must press ENTER when the target is at the center of the bar.");
            Thread.sleep(1000);
            System.out.println("How accurate is " + selectedArcher.getName() + " under pressure?");
            Thread.sleep(2000);
            System.out.println("There will be five rounds.");
            Thread.sleep(1000);
            System.out.println("There will be three other Archers competing. Score the most points to win.");
            Thread.sleep(1000);
            GameManager.anythingToContinue();
        }

        // Seven rounds
        for(int i = 1; i <= 5; i++){
            GameManager.clearScreen();
            System.out.println("ROUND " + i);
            if(i==1){
                printPlayerTurnNumber();
            }
            GameManager.anythingToContinue();
            for(int j = 0; j < allScores.length; j++){
                if(j+1 == playersTurn){
                    // Player turn
                    GameManager.clearScreen();
                    System.out.println("It is your turn!\n");
                    Thread.sleep(1000);
                    int offset = selectedArcher.aimMinigame(15 + (difficulty*6));
                    int score = Math.max(10-offset, 0);
                    System.out.println("You have scored " + score + " points!");
                    allScores[j] += score;
                } else{
                    // AI turn
                    GameManager.clearScreen();
                    System.out.println("It is " + contestantNames[j] + "'s turn!");
                    Thread.sleep(2000);
                    int aiScore = 4+difficulty;
                    aiScore += (int)(Math.random() * 3);
                    aiScore = Math.min(10, aiScore);
                    System.out.println(contestantNames[j] + " has scored " + aiScore + " points!");
                    allScores[j] += aiScore;
                }
                // Print leaderboard after every turn
                Thread.sleep(1000);
                GameManager.anythingToContinue();
                printStandings();
                GameManager.anythingToContinue();
            }
        }
        System.out.println("The game has ended!");
        Thread.sleep(1000);
        return getPlayerRankStandings();
    }

    // Run Wizard minigame and AI
    // Trial of Intellect
    private int wizardMinigame(Wizard selectedWizard, PlayerTeam playerTeam) throws InterruptedException{
        // Tutorial of the Trial of Intellect
        if(!GameManager.skipTutorial){
            System.out.println("Welcome to the TRIAL OF INTELLECT.");
            Thread.sleep(2000);
            System.out.println(selectedWizard.getName() + " will be given an ancient book full of obscure spells.");
            Thread.sleep(1000);
            System.out.println("They must memorize the spells they are given and reproduce them accurately to win.");
            Thread.sleep(1000);
            System.out.println("Every turn, you will be shown a numerical code, representing the spell you must memorize.");
            Thread.sleep(1000);
            System.out.println("After pressing ENTER, the screen will be cleared and you must retype the code you memorized.");
            Thread.sleep(1000);
            System.out.println("How far can you push " + selectedWizard.getName() + "'s intellect before failing?");
            Thread.sleep(2000);
            System.out.println("The game will end when only one Wizard is left standing.");
            Thread.sleep(1000);
            System.out.println("There will be three other Wizards competing. Be the wisest to win.");
            Thread.sleep(1000);
            GameManager.anythingToContinue();
        }
        

        // Technically infinite rounds
        int round = 1;
        while(true){
            GameManager.clearScreen();
            System.out.println("ROUND " + round);
            if(round == 1){
                printPlayerTurnNumber();
            }
            GameManager.anythingToContinue();
            // Handle each contestant's turn, distinguishing between Player and AI
            for(int i = 0; i < allScores.length; i++){
                if((i+1) == playersTurn){
                    // Player's turn
                    GameManager.clearScreen();
                    System.out.println("It is your turn!\n");
                    Thread.sleep(1000);
                    int digits = (difficulty*2)+(round);
                    int correctDigits = selectedWizard.promptMemorizationCode(digits);
                    int minimumCorrect = digits - 1;
                    System.out.println("\nYou got " + correctDigits + "/" + digits +  " digits correct.");
                    Thread.sleep(2000);
                    if(correctDigits >= minimumCorrect){
                        System.out.println("\nYou have advanced to the next round!");
                    } else{
                        System.out.println("\nYou have been eliminated.");
                    }
                    GameManager.anythingToContinue();
                } else if(allEliminationStatus[i]){
                    // Alive AI's turn
                    GameManager.clearScreen();
                    System.out.println("It is " + contestantNames[i] + "'s turn!");
                    // Chance that an AI fails that round
                    // Change the numbers here to change the probability that an AI loses
                    double probabilityOfDeath = 0.3 - ((double)(difficulty)/15);
                    probabilityOfDeath += 0.05 * round;
                    Thread.sleep(2000);
                    if(Math.random() < probabilityOfDeath){
                        // Eliminated
                        System.out.println(contestantNames[i] + " has failed to memorize their code!");
                        Thread.sleep(1000);
                        System.out.println(contestantNames[i] + " has been eliminated!\n");
                        allEliminationStatus[i] = false;
                        Thread.sleep(1000);
                        if(getPlayerRankEliminated(true) == 1){
                            System.out.println("All other contestants have been eliminated!");
                        }
                    } else{
                        // Not eliminated
                        System.out.println(contestantNames[i] + " has successfully memorized their code!");
                        Thread.sleep(1000);
                        System.out.println(contestantNames[i] + " will move on to the next round!\n");
                        Thread.sleep(1000);
                    }
                    GameManager.anythingToContinue();
                }
                if(getPlayerRankEliminated(true) == 1 || !allEliminationStatus[playersTurn-1]){
                    break;
                }
            }
            round++;
            if(getPlayerRankEliminated(true) == 1 || !allEliminationStatus[playersTurn-1]){
                break;
            }
        }
        return getPlayerRankEliminated(allEliminationStatus[playersTurn-1]);
    }

    // Print when a player is performing their turn based on their turn number
    private void printPlayerTurnNumber() throws InterruptedException{
        if(playersTurn == 1){
            System.out.println("You will be going first!");
        } else if(playersTurn == 2){
            System.out.println("You will be going second!");
        } else if(playersTurn == 3){
            System.out.println("You will be going third!");
        } else{
            System.out.println("You will be going fourth!");
        }
        System.out.println("");
        Thread.sleep(1000);
    }

    // Ask a user to pay a given amount of coins.
    // Return true if the transaction was successful. Return false if not.
    private boolean promptSpendingCoins(int amount, PlayerTeam playerTeam){
        System.out.println("You must pay " + amount + "g to enter.");
        if(playerTeam.getCoinBalance() < amount){
            // User does not have enough coins
            System.out.println("You do not have enough gold to enter this division!");
            GameManager.anythingToContinue();
            return false;
        } else{
            // Prompt YES or NO if the user has enough money
            String message = "Would you like to pay " + amount + "g?\n";
            message += "1: YES\n2: NO\n";
            message += "Balance: " + playerTeam.getCoinBalance() + "g";
            int decisionIndex = GameManager.obtainInput(message, 1, 2, false);
            if(decisionIndex == 1){
                // User selected YES
                System.out.println("\nYou have spent " + amount + "g.");
                playerTeam.increaseCoinBalance(-amount);
                System.out.println("Balance: " + playerTeam.getCoinBalance() + "g\n");
                GameManager.anythingToContinue();
                return true;
            } else{
                // User selected NO
                System.out.println("\nYou have decided not to spend " + amount + "g.");
                GameManager.anythingToContinue();
                return false;
            }
        }
    }

    // Get the rank of the Player 1-4 after being eliminated.
    // Achieve this by looking at allEliminationStatus
    private int getPlayerRankEliminated(boolean isPlayerAlive){
        int aliveCount = 0;
        for(int i = 0; i < allEliminationStatus.length; i++){
            if(allEliminationStatus[i]){
                aliveCount++;
            }
        }
        if(isPlayerAlive){
            return aliveCount;
        }
        return aliveCount + 1;
    }

    // Get the rank of the Player 1-4 based on scores
    private int getPlayerRankStandings(){
        int rank = 1;
        double playerScore = allScores[playersTurn - 1];
        for(int i = 0; i < allScores.length; i++){
            if((i+1) != playersTurn){
                if(allScores[i] > playerScore){
                    rank++;
                }
            }
        }
        return rank;
    }

    // Print the leaderboard of allScores
    private void printStandings(){
        System.out.println("STANDINGS:");
        for(int i = 0; i < allScores.length; i++){
            if(i+1 == playersTurn){
                System.out.print("You: ");
            } else{
                System.out.print(contestantNames[i] + ": ");
            }
            System.out.println(allScores[i]);
        }
    }
}
