package ie.gmit.sw.ai.node;

import ie.gmit.sw.ai.fuzzyLogic.FuzzyEnemyStatusClassifier;
import ie.gmit.sw.ai.fuzzyLogic.FuzzyHealthClassifier;
import ie.gmit.sw.ai.neuralNetwork.CombatDecisionNN;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by Martin Coleman on 19/04/2017.
 */
public class PlayerNode extends Node {
    private int health = 100;
    private int bombs;
    private int swords;
    private int noOfEnemies;
    private int damage = 10;
    private int bombDamage = 20;
    private int swordDamage = 10;
    private Node[][] maze = null;
    private Node nextMove = null;
    private boolean hasNextMove = false;
    private boolean inCombat=false;
    private long movementSpeed = 3000;
    private ExecutorService executor = Executors.newFixedThreadPool(1);
    private CombatDecisionNN combatNet = null;
    private FuzzyHealthClassifier healthClassifier = null;
    private FuzzyEnemyStatusClassifier enemyStatusClassifier = null;

    public PlayerNode(int row, int col, Node[][] maze) {

        super(row, col, 5);

        //Init Variables
        this.maze = maze;
        this.health=100;
        this.bombs=0;
        this.swords=0;
        this.noOfEnemies=0;

        // instantiate neural network that decides whether to fight, panic, heal or run away
        combatNet = new CombatDecisionNN();
        healthClassifier = new FuzzyHealthClassifier();
        enemyStatusClassifier = new FuzzyEnemyStatusClassifier();

        // start player thread
        executor.submit(() -> {

            while (true) {
                try {

                    // sleep thread to simulate a movement pace
                    Thread.sleep(movementSpeed);

                    // don't do anything if in combat
                    if(!inCombat) {

                        // check for enemies right next to the player
                        checkForEnemies();

                    } // if

                } catch (Exception ex) {

                } // try catch
            } // while

        });

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
                    attack(spider);
                    break;
                case 2: // panic
                    panic();
                    break;
                case 3: // heal
                    heal();
                    break;
                default: // run away
                    runAway();

            } // switch

        }catch (Exception ex){

        } // try

    } // startCombat()

    private void attack(SpiderNode spider){
        System.out.println("Attack!");

        // flag spider as in combat
        spider.setInCombat(true);

        // flag self as in combat
        this.inCombat = true;

        // do damage to spider
        int spiderHealth = spider.getHealth();

        // check if dead
        if(spiderHealth <= 0){
            // spider is dead

            // not in combat
            inCombat = false;

            // remove spider
            maze[spider.getRow()][spider.getCol()] = new Node(spider.getRow(), spider.getCol(), -1);
        } // if
    } // attack()

    private void panic(){
        System.out.println("Panic!");

    }

    private void heal(){
        System.out.println("Heal!");
    }

    private void runAway(){
        System.out.println("Run Away!");

    }

    // checks for enemies that are right next to the player
    private void checkForEnemies(){

        Node[] adjacentNodes = null;
        List<Node> enemies = new ArrayList<>();

        // get the spiders adjacent nodes
        adjacentNodes = adjacentNodes(maze);

        for (Node n : adjacentNodes) {

            // check that the node is an enemy
            if (n.getId() > 5) {

                // add node to list of enemies
                enemies.add(n);
            } // if
        } // if

        // check if there are enemies
        if(enemies.size() > 0){

            // start combat with first enemy
            startCombat((SpiderNode) enemies.get(0));
        } // if

    } // checkForEnemies()

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
