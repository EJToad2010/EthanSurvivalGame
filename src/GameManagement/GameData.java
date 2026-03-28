package src.GameManagement;

import java.util.Scanner;

import src.Teams.EnemyTeam;
import src.Teams.PlayerTeam;

// Stores all of the attributes kept track of during the game
public class GameData {
// Manually set this to true during repeated playtesting
  public static boolean skipTutorial = false;
  // Arrays used to track enemy difficulty progression through their class types.
  // Influences the probability that each class will be chosen
  private String[] earlyGameEnemies = new String[]{"Goblin", "Goblin", "DartGoblin"};
  // Enemy behaviors change as the game progresses, adding more complex roles.
  // This simulates the AI getting smarter.
  private String[] earlyGameEnemyBehaviors = new String[]{"RANDOM", "RANDOM", "RANDOM", "AGGRESSIVE", "DEFENSIVE"};
  // Enemy items are basic early on
  private String[] earlyGameEnemyItems = new String[]{"HealthPotion", "HealthPotion", "HealthPool"};
  
  // Attributes needed for Player and Enemy classes outside of battle
  private int playerBattleCapacity = 2;
  private int enemyBattleCapacity = 2;
  
  // Attributes used to keep track of game progression
  private int dayNum = 1;
  private int turnNum;
  
  // Attributes used within battles, which are the core part of the gameplay loop
  private int playerActionPoints = playerBattleCapacity;
  private int actionPointsLeft;
  private int currentActionPoints;
  private PlayerTeam playerTeam;
  private EnemyTeam enemyTeam;

  public GameData(){
    this.playerTeam = new PlayerTeam();
    this.enemyTeam = new EnemyTeam();
  }
}
