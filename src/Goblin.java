package src;

import java.util.ArrayList;

class Goblin extends EnemyCharacter{
  // Items that the Goblin steals from the player. They are not used by the Goblin and will be returned on death.
  public Goblin(String name, String behaviorType){
    super(name, 70.0, 14.0, 4.0, 30.0, 50, 20, behaviorType);
    setDescription("A weak enemy focused on annoying the player and causing chaos.");
    addToArrayList(getBasicAbilityNames(), new String[]{"Rusty Dagger", "Nimble Dodge"});
    addToArrayList(getBasicAbilityDescriptions(), new String[]{"Deals a small amount of damage onto a single target. 10% chance to inflict bleed.",
                                                             "During the player's turn, all attacks toward the Goblin deal 25% less damage."});
    addToArrayList(getBasicAbilityTypes(), new String[]{"Offensive", "Offensive"});
    addToArrayList(getBasicAbilityEnemyCounts(), new Integer[]{1, 0});
    addToArrayList(getSpecialAbilityNames(), new String[]{"Taunt", "Pickpocket"});
    addToArrayList(getSpecialAbilityDescriptions(), new String[]{"Target a single character. The target cannot use an ability next turn.",
                                                               "Attack a single character for moderate damage. The Goblin has a 25% to steal gold from the player. The Goblin consumes the gold to heal."});
    addToArrayList(getSpecialAbilityTypes(), new String[]{"Offensive", "Offensive"});
    addToArrayList(getSpecialAbilityEnemyCounts(), new Integer[]{1, 1});
    addToArrayList(getSpecialAbilityCooldowns(), new Integer[]{3, 3});
    addToArrayList(getCurrentSpecialAbilityCooldowns(), new Integer[]{3, 3});
  }
  
  // Overrided getType method
  public String getType(){
    return "Goblin";
  }

  public void basicAbilityAI(PlayerTeam playerTeam, EnemyTeam enemyTeam) throws InterruptedException{
    // System.out.println("DEBUG: Goblin Basic Ability AI");
    int basicAbilityLimit = getBasicAbilityLimit();
    if(basicAbilityLimit >= 1){
      // Guaranteed to use Nimble Dodge if under 25% HP
      if(getCurrentHP() / getMaxHP() < 0.25){
        basicAbility(1, this, playerTeam, enemyTeam);
        return;
      } // 50% chance to use Nimble Dodge if under 50% HP
      if(getCurrentHP() / getMaxHP() < 0.5 && (int)(Math.random() * 100) < 50){
        basicAbility(1, this, playerTeam, enemyTeam);
        return;
      }
      // 25% chance to use Nimble Dodge otherwise
      if((int)(Math.random() * 100) < 25){
        basicAbility(1, this, playerTeam, enemyTeam);
        return;
      }
    }
    // Use Rusty Dagger on a random Character if all other options are exhausted
    basicAbility(0, playerTeam.getPlayerTeam().get((int)(Math.random() * playerTeam.getPlayerTeam().size())), playerTeam, enemyTeam);
  }

  public void basicAbilityAI(BasicCharacter preferredCharacter, PlayerTeam playerTeam, EnemyTeam enemyTeam) throws InterruptedException{
    // System.out.println("DEBUG: Goblin aggressive AI");
    // Likely has aggressive AI behavior, so automatically use Rusty Dagger
    basicAbility(0, preferredCharacter, playerTeam, enemyTeam);
  }

  // AI for deciding a special ability to use
  public void specialAbilityAI(ArrayList<Integer> availableSpecialAbilityIndices, PlayerTeam playerTeam, EnemyTeam enemyTeam) throws InterruptedException{
    int randomAbilityIndex = availableSpecialAbilityIndices.get((int)(Math.random() * availableSpecialAbilityIndices.size()));
    if(randomAbilityIndex == 0){
      // Taunt ability
      // Choose either the player with the highest HP remaining or the highest attackStrength
      double highestHP = playerTeam.getPlayerTeam().get(0).getCurrentHP();
      int highestHPIndex = 0;
      double highestAttack = playerTeam.getPlayerTeam().get(0).getAttackStrength();
      int highestAttackIndex = 0;
      if((Math.random() * 100) < 50){
        // Choose highest HP
        for(int i = 0; i < playerTeam.getPlayerTeam().size(); i++){
          if(playerTeam.getPlayerTeam().get(i).getCurrentHP() > highestHP){
            highestHP = playerTeam.getPlayerTeam().get(i).getCurrentHP();
            highestHPIndex = i;
          }
        }
        specialAbility(0, playerTeam.getPlayerTeam().get(highestHPIndex), playerTeam, enemyTeam);
      } else{
        // Choose highest Attack Strength
        for(int i = 0; i < playerTeam.getPlayerTeam().size(); i++){
          if(playerTeam.getPlayerTeam().get(i).getAttackStrength() > highestAttack){
            highestAttack = playerTeam.getPlayerTeam().get(i).getAttackStrength();
            highestAttackIndex = i;
          }
        }
        specialAbility(0, playerTeam.getPlayerTeam().get(highestAttackIndex), playerTeam, enemyTeam);
      }
    } else if(randomAbilityIndex == 1){
      // Pickpocket ability
      // Target random player
      specialAbility(1, playerTeam.getPlayerTeam().get((int)(Math.random() * playerTeam.getPlayerTeam().size())), playerTeam, enemyTeam);
    }
  }

  // Overrided battle methods
  // basicAbilityAI handles the decision making, while basicAbility handles the outcome of an ability itself.
  // Same for specialAbility()
  public void basicAbility(int basicAbilityIndex, BasicCharacter target, PlayerTeam playerTeam, EnemyTeam enemyTeam) throws InterruptedException{
    if(basicAbilityIndex == 0){
      System.out.println(getName() + " stabbed " + target.getName() + " with their dagger for " + getAttackStrength() + " HP!");
      boolean wasEnemyHit = handleEnemyDefense(target, getAttackStrength(), playerTeam, enemyTeam);
      System.out.println(target.getSimpleOutput());
      if((int)(Math.random() * 100) < 10 && wasEnemyHit){
        StatusEffect.addStatusEffect(target, "Bleed", 2);
      }
    } else if(basicAbilityIndex == 1){
      // Nimble Dodge (shared with Goblin class since they are both speedy Goblin types)
      System.out.println(getName() + " prepared Nimble Dodge for the player's next turn!");
      StatusEffect.addStatusEffect(this, "Nimble", 1);
    }
  }
  
  public void specialAbility(int specialAbilityIndex, BasicCharacter target, PlayerTeam playerTeam, EnemyTeam enemyTeam) throws InterruptedException{
    resetSpecialAbilityCooldowns();
    if(specialAbilityIndex == 0){
      // Taunt
      System.out.println(getName() + " taunted " + target.getName() + "!");
      Thread.sleep(1000);
      System.out.println(target.getName() + " cannot use an ability next turn!");
      StatusEffect.addStatusEffect(target, "Taunt", 1);
    } else if(specialAbilityIndex == 1){
      // Pickpocket
      System.out.println(getName() + " swiped at " + target.getName() + " for " + getAttackStrength()+5 + " HP!");
      handleEnemyDefense(target, getAttackStrength()+5, playerTeam, enemyTeam);
      if(playerTeam.getCoinBalance() > 0 && (int)(Math.random() * 100) < 25){
        int stolenCoins = (int)(Math.random() * 50) + 25;
        stolenCoins = Math.min(stolenCoins, playerTeam.getCoinBalance());
        System.out.println(getName() + " pickpocketed " + stolenCoins + "g from your team!");
        playerTeam.increaseCoinBalance(-stolenCoins);
        Thread.sleep(1000);
        System.out.println("Balance: "+playerTeam.getCoinBalance());
        Thread.sleep(1000);
        System.out.println(getName() + " ate the coins to heal " + (double)(stolenCoins)/2 + " HP!");
        changeCurrentHP((double)(stolenCoins)/2);
        System.out.println(getSimpleOutput());
      }
    }
    System.out.println(target.getSimpleOutput());
  }
  
  // Defense function which is called when an enemy targets the Knight
  public void defend(BasicCharacter target, double actualDamage){
    if(actualDamage == 0){
      System.out.println(getName() + " successfully hid from " + target.getName() + "'s attack!");
    }else if(getIsDefending()){
      System.out.println(getName() + " partially hid from " + target.getName() + "'s attack for " + getDefenseStrength() * 2 + " HP!");
    } else{
      System.out.println(getName() + " tried to hide from " + target.getName() + "'s attack for " + getDefenseStrength() + " HP!");
    }
  }
}
