package ie.gmit.sw.ai.node;

import ie.gmit.sw.ai.fuzzyLogic.FuzzyEnemyStatusClassifier;
import ie.gmit.sw.ai.fuzzyLogic.FuzzyHealthClassifier;
import ie.gmit.sw.ai.neuralNetwork.CombatDecisionNN;
import ie.gmit.sw.ai.traversers.AStarTraversator;
import ie.gmit.sw.ai.traversers.PlayerDepthLimitedDFSTraverser;
import ie.gmit.sw.ai.traversers.Traversator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
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
    private PlayerDepthLimitedDFSTraverser depthLimitedDFSTraverser;
    private CombatDecisionNN combatNet = null;
    private FuzzyHealthClassifier healthClassifier = null;
    private FuzzyEnemyStatusClassifier enemyStatusClassifier = null;
    private Random rand = new Random();
    public boolean isDead = false;

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
        depthLimitedDFSTraverser = new PlayerDepthLimitedDFSTraverser();
        Traversator aStar = new AStarTraversator(maze[3][3]);


        aStar.traverse(maze,this);

        System.out.println(aStar.getNextNode() +" current node: "+this);

        // start player thread
        executor.submit(() -> {


            while (true) {
                try {

                    // check that player is dead
                    if(getHealth() <= 0){
                        // player is dead
                        isDead = true;

                        System.out.println("\n===============================================");
                        System.out.println("Player is Dead!");
                        System.out.println("===============================================\n");
                    } // if

                    // sleep thread to simulate a movement pace
                    Thread.sleep(movementSpeed);

                    checkForPickup();




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
        spider.setInCombat(true);

        // flag self as in combat
        this.inCombat = true;

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

            // scan for enemies
            depthLimitedDFSTraverser.traverseForEnemies(maze, maze[getRow()][getCol()], 4);

            // get number of enemies
            setNoOfEnemies(depthLimitedDFSTraverser.traverseForEnemies(maze, maze[getRow()][getCol()], 4).size());

            System.out.println("Enemies Nearby: " + getNoOfEnemies());

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

            // get sword status
            if(getSwords() > 0)
                swordStatus = 1;

            // get bomb status
            if(getBombs() > 0)
                bombStatus = 1;

            // get combat decision
            int result = combatNet.action(healthStatus, swordStatus, bombStatus, enemyStatus);

            System.out.println("\n===============================================");
            System.out.println("Stats");
            System.out.println("HealthStat: " + healthStatus);
            System.out.println("SwordStat: " + swordStatus);
            System.out.println("BombStat: " + bombStatus);
            System.out.println("enemyStatus: " + enemyStatus);
            System.out.println("Actual Health: " + getHealth());
            System.out.println("===============================================");

            // execute decision
            switch (result){
                case 1: // attack
                    attack(spider);
                    break;
                case 2: // panic
                    panic(spider);
                    break;
                case 3: // heal
                    heal(spider);
                    break;
                default: // run away
                    runAway(spider);

            } // switch

        }catch (Exception ex){

            // tell spider combat is over
            spider.setInCombat(false);

            // flag self as not in combat
            this.inCombat = false;

        } // try

    } // startCombat()

    private void attack(SpiderNode spider){
        System.out.println("===============================================");
        System.out.println("Attacking!");

        // get spider health
        int spiderHealth = spider.getHealth();

        System.out.println("Spider Health: " + spiderHealth);
        System.out.println("Player Health: " + getHealth());
        System.out.println("Player Damage: " + getDamage());

        //green spider - Heal player
        if(spider.getId()==9){
            increaseHealth(20);
        }

        // use bomb if have one and can't one hit spider
        if(spiderHealth > getDamage() && getBombs() > 0){

            // use bomb and normal attack
            spider.decreaseHealth(getDamage() + bombDamage);

            // you have used a bomb
            decreaseBombs();

            System.out.println("Player Use A Bomb");

        } else {

            // use normal attack
            spider.decreaseHealth(getDamage());

        } // if

        // check spiders health
        spiderHealth = spider.getHealth();

        // check if dead
        if(spiderHealth <= 0){
            // spider is dead

            // not in combat
            inCombat = false;

            //if attack a yellow spider all yellow turn hostile
            if(spider.getId()==13){
                SpiderNode.setYellowhostile(true);
            }

            // remove spider
            maze[spider.getRow()][spider.getCol()] = new Node(spider.getRow(), spider.getCol(), -1);
        } else {

            System.out.println("Damage Taken: " + spiderHealth);

            // player takes remaining spider health as damage
            decreaseHealth(spiderHealth);

            // spider dies

            //if attack a yellow spider all yellow turn hostile
            if(spider.getId()==13){
                SpiderNode.setYellowhostile(true);
            }

            // not in combat
            inCombat = false;

            // remove spider
            maze[spider.getRow()][spider.getCol()] = new Node(spider.getRow(), spider.getCol(), -1);

        } // if

        System.out.println("Player's Health: " + getHealth());
        System.out.println("===============================================\n");

    } // attack()

    private void panic(SpiderNode spider){
        System.out.println("===============================================");
        System.out.println("Starting to Panic!");

        // 50% chance to take damage
        if(rand.nextInt(100) > 49){

            System.out.println("Took Damage While Panicking!");
            System.out.println("Player Health: " + getHealth());

            // take small damage
            decreaseHealth(10);

            // check that player is dead
            if(getHealth() <= 0){
                // player is dead
                isDead = true;
                System.out.println("\n===============================================");
                System.out.println("Player is Dead!");
                System.out.println("===============================================\n");
            } // if
        } // if

        System.out.println("===============================================");

        // go into attack
        attack(spider);

    } // panic()

    private void heal(SpiderNode spider){
        System.out.println("===============================================");
        System.out.println("Trying to Heal!");

        // 50% chance to heal
        if(rand.nextInt(100) > 49) {

            System.out.println("Healing!");

            // try and flee
            flee(spider);

            // heal
            increaseHealth(10);

            // flag spider as in combat
            spider.setInCombat(false);

            // flag self as in combat
            this.inCombat = false;

            System.out.println("===============================================");

        } else {

            System.out.println("Heal Failed!");
            System.out.println("===============================================");

            // failed, attack
            attack(spider);
        } // if

    } // heal()

    private void runAway(SpiderNode spider){
        System.out.println("===============================================");
        System.out.println("Trying to Run Away!");

        // 50% chance to heal
        if(rand.nextInt(100) > 49) {

            System.out.println("Running Away!");

            // try and flee
            flee(spider);

            // flag spider as not in combat
            spider.setInCombat(false);

            // flag self as not in combat
            this.inCombat = false;

            System.out.println("===============================================");

        } else { // run failed!

            System.out.println("Running Away Failed!");
            System.out.println("===============================================");

            // attack!
            attack(spider);
        }

    } // runAway()

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

    public void flee(Node enemy){

        Node[] adjacentNodes = adjacentNodes(maze);
        Node move = null;
        int lowestHeurstic=0;

        for (Node n : adjacentNodes) {

            if(n.getHeuristic(enemy)>lowestHeurstic){
                move=n;
            }

        } // for

        // save next move
        nextMove=move;

        // if there is a place to move
        if(nextMove != null) {
            // move the player
            swapNodes(this, nextMove);
        } // if

    } // flee()

    public void checkForPickup(){
        Node[] adjacentNodes = adjacentNodes(maze);

        for (Node n : adjacentNodes) {
            if(n.getId()==1){
                //sword - increase swords by one
                increaseSwords();
                //replaces pickup with hedge
                n.setId(0);
                System.out.println("Picked up a sword");
                System.out.println("Sword count "+getSwords());
            }
            if(n.getId()==2){
                //random Pickup
                int  randNum = rand.nextInt(3) + 1;

                switch (randNum){
                    case 1:
                        increaseHealth(20);
                        System.out.println("Random Pickup was +20 Health");
                        break;
                    case 2:
                        increaseSwords();
                        System.out.println("Random Pickup was a sword");
                        break;
                    case 3:
                        increaseBombs();
                        System.out.println("Random Pickup was a bomb");
                        break;
                }
                //replaces pickup with hedge
                n.setId(0);

            }
            if(n.getId()==3){
                //regular bomb - increase bombs by 1
                increaseBombs();
                //replaces pickup with hedge
                n.setId(0);
                System.out.println("Picked up a bomb");
            }
            if(n.getId()==4){
                // hydrogen bomb - gives 2 bombs
                increaseBombs();
                increaseBombs();
                //replaces pickup with hedge
                n.setId(0);
                System.out.println("Picked up a Hydrogen Bomb");
            }

        } // for

    } // flee()

    private void swapNodes(Node x, Node y){

        int newX, newY, oldX, oldY;

        // save indexes
        oldX = x.getRow();
        oldY = x.getCol();
        newX = y.getRow();
        newY = y.getCol();

        // update X and Y
        x.setRow(newX);
        x.setCol(newY);
        y.setRow(oldX);
        y.setCol(oldY);

        // randomMove to that node
        maze[newX][newY] = x;

        // remove self from original spot
        maze[oldX][oldY] = y;

    } // swapNodes()

    public int getHealth() {
        return health;
    }

    public void increaseHealth(int health) {
        this.health += health;

        if(this.health>100)
            this.health=100;

    }

    public void decreaseHealth(int health) {
        this.health -= health;
    }

    public int getBombs() {
        return bombs;
    }

    public void increaseBombs() {
        this.bombs++;
    }

    public void decreaseBombs() {

        this.bombs -= bombs;

        if(bombs < 0)
            bombs = 0;
    }

    public int getSwords() {
        return swords;
    }

    public void increaseSwords() {
        this.swords++;
    }

    public void decreaseSwords() {
        this.swords -= swords;
    }

    public int getNoOfEnemies() {
        return noOfEnemies;
    }

    public void setNoOfEnemies(int noOfEnemies){
        this.noOfEnemies = noOfEnemies;
    }

    // gets the players damage
    // includes sword if player has one
    public int getDamage(){

        int totalDamage = this.damage;

        if(swords > 0)
            totalDamage += this.swordDamage;

        return totalDamage;
    } // getDamage()
}
