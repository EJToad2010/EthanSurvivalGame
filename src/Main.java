package src;
import java.io.IOException;
import java.io.FileNotFoundException;
public class Main {
  // Game architecture
  // Superclass: Character
  // Subclasses: PlayerCharacter, EnemyCharacter
  // PlayerCharacter subclasses: Knight
  // EnemyCharacter subclasses:
  
  // Class: PlayerTeam
  // Class: EnemyTeam
  // Class: GameManager
  public static void main(String[] args) throws InterruptedException, FileNotFoundException, IOException{
    GameManager gm = new GameManager();
    GameManager.clearScreen();
    System.out.println("Welcome to Ethan's AP CSA Survival Game!");
    GameManager.anythingToContinue();
    System.out.println("Starting game...");
    Thread.sleep(1500);
    System.out.println("");
    gm.run();
  }
}
