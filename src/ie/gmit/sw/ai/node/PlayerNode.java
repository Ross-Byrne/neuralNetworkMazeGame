package ie.gmit.sw.ai.node;

import ie.gmit.sw.ai.fuzzyLogic.FuzzyEnemyStatusClassifier;
import ie.gmit.sw.ai.fuzzyLogic.FuzzyHealthClassifier;
import ie.gmit.sw.ai.neuralNetwork.CombatDecisionNN;

/**
 * Created by Martin Coleman on 19/04/2017.
 */
public class PlayerNode extends Node {
    private int health = 100;
    private int bombs;
    private int swords;
    private int noOfEnemies;
    private CombatDecisionNN combatNet = null;
    private FuzzyHealthClassifier healthClassifier = null;
    private FuzzyEnemyStatusClassifier enemyStatusClassifier = null;

    public PlayerNode(int row, int col, int id) {

        super(row, col, id);

        //Init Variables
        this.health=100;
        this.bombs=0;
        this.swords=0;
        this.noOfEnemies=0;

        // instantiate neural network that decides whether to fight, panic, heal or run away
        combatNet = new CombatDecisionNN();
        healthClassifier = new FuzzyHealthClassifier();
        enemyStatusClassifier = new FuzzyEnemyStatusClassifier();

        // just to test
        startCombat(null);

    }

    public void startCombat(SpiderNode spider){

        // initialise combat with selected spider
        // tell spider combat is starting

        // get number of enemies

        // decide what to do
        try {

            /*
                1 = Health (1 = Healthy, 0.5 = Minor Injuries, 0 = Serious Injuries)
                2 = Has Sword (1 = Yes, 0 = No)
                3 = Has Bomb (1 = Yes, 0 = No)
                4 = Enemies Status (0 = One, 0.5 = Two, 1 = Three or More)
             */

            double healthStatus = 1;
            double swordStatus = 0;
            double bombStatus = 0;
            double enemyStatus = 0;

            // set health in health classifier
            healthClassifier.setInputVariable("health", getHealth());

            // get the health stat from fuzzy health classifier
            String injuriesStatus = healthClassifier.getWinningMembership("injuries");

            // get health status
            switch (injuriesStatus){
                case "none":
                    healthStatus = 1;
                    break;
                case "minor":
                    healthStatus = 0.5;
                    break;
                case "serious":
                    healthStatus = 0;
                    break;
            } // switch

            System.out.println(injuriesStatus + " = " + healthStatus);

            // set the number of enemies in fuzzy logic classifier
            enemyStatusClassifier.setInputVariable("enemies", getNoOfEnemies());

            // get enemy status
            String enemyStat = enemyStatusClassifier.getWinningMembership("enemyStatus");

            // get enemy status
            switch (enemyStat){
                case "ok":
                    enemyStatus = 0;
                    break;
                case "risky":
                    enemyStatus = 0.5;
                    break;
                case "tooMany":
                    enemyStatus = 1;
                    break;
            } // switch

            System.out.println(enemyStat + " = " + enemyStatus);

            // get sword status
            if(getSwords() > 0)
                swordStatus = 1;

            // get bomb status
            if(getBombs() > 0)
                bombStatus = 1;

            // get combat decision
            int result = combatNet.action(healthStatus, swordStatus, bombStatus, enemyStatus);

            // execute decision
            switch (result){
                case 1: // attack
                    System.out.println("Attack!");
                    break;
                case 2:
                    System.out.println("Panic!");
                    break;
                case 3:
                    System.out.println("Heal!");
                    break;
                    default:
                        System.out.println("Run Away!");

            } // switch

        }catch (Exception ex){

        } // try

    } // startCombat()

    public int getHealth() {
        return health;
    }

    public void increaseHealth(int health) {
        this.health += health;
    }

    public void decreaseHealth(int health) {
        this.health -= health;
    }

    public int getBombs() {
        return bombs;
    }

    public void increaseBombs(int bombs) {
        this.bombs += bombs;
    }

    public void decreaseBombs(int bombs) {

        this.bombs -= bombs;
    }

    public int getSwords() {
        return swords;
    }

    public void increaseSwords(int swords) {
        this.swords += swords;
    }

    public void decreaseSwords(int swords) {
        this.swords -= swords;
    }

    public int getNoOfEnemies() {
        return noOfEnemies;
    }

    public void increaseNoOfEnemies(int noOfEnemies) {
        this.noOfEnemies += noOfEnemies;
    }

    public void decreaseNoOfEnemies(int noOfEnemies) {
        this.noOfEnemies -= noOfEnemies;
    }
}
