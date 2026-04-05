package src.GameManagement;

import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;

import src.Characters.EnemyCharacter;
import src.Characters.PlayerCharacter;
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
  // Used in random name generation
  private String[] adjectives;
  private String[] nouns;
  
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
    fillAdjectives();
    fillNouns();
  }

  // Getters
  public PlayerTeam getPlayerTeamObj(){
    return playerTeam;
  }

  public ArrayList<PlayerCharacter> getPlayerTeamArr(){
    return playerTeam.getPlayerTeam();
  }

  public EnemyTeam getEnemyTeamObj(){
    return enemyTeam;
  }

  public ArrayList<EnemyCharacter> getEnemyTeamArr(){
    return enemyTeam.getEnemyTeam();
  }

  public int getPlayerBattleCapacity(){
    return playerBattleCapacity;
  }

  public int getEnemyBattleCapacity(){
    return enemyBattleCapacity;
  }
  
  public int getDayNum(){
    return dayNum;
  }

  public int getTurnNum(){
    return turnNum;
  }

  public String[] getAdjectives(){
    return adjectives;
  }
  
  public String[] getNouns(){
    return nouns;
  }
  
  public String[] getEarlyGameEnemies(){
    return earlyGameEnemies;
  }

  public String[] getEarlyGameEnemyBehaviors(){
    return earlyGameEnemyBehaviors;
  }

  public String[] getEarlyGameEnemyItems(){
    return earlyGameEnemyItems;
  }

  // Setters
  public void nextDay(){
    dayNum++;
  }

  public void increaseEnemyBattleCapacity(int amount){
    enemyBattleCapacity += amount;
  }

  public void setEnemyBattleCapacity(int enemyBattleCapacity){
    this.enemyBattleCapacity = enemyBattleCapacity;
  }

  // Read .txt file of adjectives
  private void fillAdjectives(){
    Scanner s;
    try{
      s = new Scanner(new File("src/Misc/adjectives.txt"));
    } catch(FileNotFoundException e){
      System.out.println("adjectives.txt failed to load.");
      return;
    }
    ArrayList<String> lines = readLines(s);
    adjectives = lines.toArray(new String[0]);
    s.close();
  }

  // Read .txt file of nouns
  private void fillNouns(){
    Scanner s;
    try{
      s = new Scanner(new File("src/Misc/nouns.txt"));
    } catch(FileNotFoundException e){
      System.out.println("nouns.txt failed to load.");
      return;
    }
    ArrayList<String> lines = readLines(s);
    nouns = lines.toArray(new String[0]);
    s.close();
  }

  private ArrayList<String> readLines(Scanner s){
    ArrayList<String> lines = new ArrayList<String>();
    while(s.hasNextLine()){
      lines.add(s.nextLine());
    }
    return lines;
  }
}
