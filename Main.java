
import java.io.IOException;

import src.GameManagement.GameManager;
import src.GameManagement.UI.GameFrame;
public class Main {
  // Game architecture
  // Superclass: BasicCharacter
  // Subclasses: PlayerCharacter, EnemyCharacter
  // PlayerCharacter subclasses: Knight, Archer, Wizard
  // EnemyCharacter subclasses: Goblin, Dart Goblin

  // Item-related classes
  // Item, ItemStack, Inventory
  // Item subclasses: Health Potion, Health Pool

  // Class: Shop
  
  // Class: StatusEffect

  // Class: Tournament
  
  // Class: PlayerTeam
  // Class: EnemyTeam
  // Class: GameManager
  public static void main(String[] args) throws IOException, InterruptedException{
    // Running the restructured game
    new GameFrame();

    // Running the original console game
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
