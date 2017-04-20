package ie.gmit.sw.ai.node;

import ie.gmit.sw.ai.fuzzyLogic.FuzzyHealthClassifier;
import ie.gmit.sw.ai.neuralNetwork.CombatDecisionNN;

/**
 * Created by Martin Coleman on 19/04/2017.
 */
public class PlayerNode extends Node {
    private int health = 100;
    private int bombs;
    private boolean sword;
    private int noOfEnemies;
    private CombatDecisionNN combatNet = null;
    private FuzzyHealthClassifier fuzzyHealthClassifier = null;

    public PlayerNode(int row, int col, int id) {

        super(row, col, id);

        // instantiate neural network that decides whether to fight, panic, heal or run away
        combatNet = new CombatDecisionNN();
        fuzzyHealthClassifier = new FuzzyHealthClassifier();

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
            fuzzyHealthClassifier.setInputVariable("health", getHealth());

            // get the health stat from fuzzy health classifier
            String injuriesStatus = fuzzyHealthClassifier.getWinningMembership("injuries");

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

            // get enemy status

            // get sword status
            if(isSword())
                swordStatus = 1;


            // get bomb status
            if(getBombs() > 0)
                bombStatus = 1;

            // get combat decision
            combatNet.action(healthStatus, swordStatus, bombStatus, 0);

        }catch (Exception ex){

        } // try


    } // startCombat()

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public int getBombs() {
        return bombs;
    }

    public void setBombs(int bombs) {
        this.bombs = bombs;
    }

    public boolean isSword() {
        return sword;
    }

    public void setSword(boolean sword) {
        this.sword = sword;
    }

    public int getNoOfEnemies() {
        return noOfEnemies;
    }

    public void setNoOfEnemies(int noOfEnemies) {
        this.noOfEnemies = noOfEnemies;
    }
}
