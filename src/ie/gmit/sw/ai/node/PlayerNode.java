package ie.gmit.sw.ai.node;

import ie.gmit.sw.ai.fuzzyLogic.FuzzyHealthClassifier;
import ie.gmit.sw.ai.neuralNetwork.CombatDecisionNN;

/**
 * Created by Martin Coleman on 19/04/2017.
 */
public class PlayerNode extends Node {
    private int health;
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

            combatNet.action(1, 1, 0, 0);

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
