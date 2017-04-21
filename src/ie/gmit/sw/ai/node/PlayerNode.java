package ie.gmit.sw.ai.node;

import ie.gmit.sw.ai.*;
import ie.gmit.sw.ai.fuzzyLogic.FuzzyEnemyStatusClassifier;
import ie.gmit.sw.ai.fuzzyLogic.FuzzyHealthClassifier;
import ie.gmit.sw.ai.neuralNetwork.CombatDecisionNN;
import ie.gmit.sw.ai.traversers.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * Created by Martin Coleman on 19/04/2017.
 *
 * The Node that represents the player.
 * Contains all of the player logic.
 */
public class PlayerNode extends Node {

    private Object lock;
    private int health = 100;
    private int bombs;
    private int swords;
    private int noOfEnemies;
    private int damage = 10;
    private int bombDamage = 20;
    private int swordDamage = 10;
    private Node[][] maze = null;
    private Node nextMove = null;
    private Node lastNode;
    private boolean hasNextMove = false;
    private boolean inCombat=false;
    private long movementSpeed = 1000;
    private ExecutorService executor = Executors.newFixedThreadPool(1);
    private PlayerDepthLimitedDFSTraverser depthLimitedDFSTraverser = null;
    private CombatDecisionNN combatNet = null;
    private FuzzyHealthClassifier healthClassifier = null;
    private FuzzyEnemyStatusClassifier enemyStatusClassifier = null;
    private Random rand = new Random();
    private boolean hasTarget = false;
    private Node target;


    public PlayerNode(int row, int col, Node[][] maze, Object lock) {

        super(row, col, 5);

        //Init Variables
        this.maze = maze;
        this.lock = lock;
        this.health=100;
        this.bombs=0;
        this.swords=0;
        this.noOfEnemies=0;

        // instantiate neural network that decides whether to fight, panic, heal or run away
        combatNet = new CombatDecisionNN();
        healthClassifier = new FuzzyHealthClassifier();
        enemyStatusClassifier = new FuzzyEnemyStatusClassifier();
        depthLimitedDFSTraverser = new PlayerDepthLimitedDFSTraverser();

        // start player thread
        executor.submit(() -> {

            while (true) {
                try {

                    if(GameRunner.isGameOver()){

                        // do nothing, game is over

                    } else if(getHealth() <= 0){ // check if the player is dead

                        System.out.println("\n===============================================");
                        System.out.println("Player is Dead. Game Over!");
                        System.out.println("===============================================\n");

                        // player is dead, game is over
                        GameRunner.gameOver(true);

                    } else {

                        // sleep thread to simulate a movement pace
                        Thread.sleep(movementSpeed);

                        // don't do anything if in combat
                        if(!inCombat) {

                            // check for enemies right next to the player
                            checkForEnemies();

                            // check for picks right next to player
                            checkForPickup();
                            
                            // place move code here
                            if(GameRunner.AI_CONTROLLED){ // only move if AI controlled

                                // only go for target if one does not exist
                                if(hasTarget == false || target == null) {

                                    // scan for pickups and enemies farther way
                                    checkForPicksAndEnemies();
                                }
                                else
                                {
                                    // move in on target
                                    moveInOnTarget();

                                }

                                // start moving the player
                                if (hasNextMove) {          // if player has next move
                                    moveToNextNode();     // move to next node
                                } else {                    // otherwise
                                    randomMove();           // move randomly
                                }
                            } // if

                        } // if
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

        // wait for a little bit
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
        }

        // get spider health
        int spiderHealth = spider.getHealth();

        System.out.println("Spider Health: " + spiderHealth);
        System.out.println("Player Health: " + getHealth());
        System.out.println("Player Damage: " + getDamage());

        //green spider - Heal player
        if(spider.getId() == 9){
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

        // check if spider was boss
        if(getHealth() > 0 && spider.getId() == 12){ // still alive and is boss

            System.out.println("\n===============================================");
            System.out.println("Boss is Dead. Game Over!");
            System.out.println("===============================================\n");

            // boss is dead, game is over
            GameRunner.gameOver(false);

        } // if

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
                // player is dead, game over
                GameRunner.GAME_OVER = true;

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

    // checks for pickups and enemies and then
    // decides whether to attack or get pickups
    private void checkForPicksAndEnemies(){

        target = null;
        Set<Node> enemyNodes;
        Set<Node> pickupNodes;
        int last = 100000;
        int heuistic = 0;

        // scan for pickups and enemies farther way
        enemyNodes = depthLimitedDFSTraverser.traverseForEnemies(maze, this, 60);
        pickupNodes = depthLimitedDFSTraverser.traverseForPickups(maze, this, 60);

        if(getHealth() < 60 || getSwords() < 0 && getBombs() < 5 || getBombs() < 5){

            // go get closest pickup
            for (Node n : pickupNodes){

                // get distance from node
                heuistic = this.getHeuristic(n);

                // save the closest node
                if(heuistic < last) {

                    last = heuistic;
                    target = n;
                } // if
            } // for

        } else {
            // kill the closes spider
            // go get closest pickup
            for (Node n : enemyNodes){

                // get distance from node
                heuistic = this.getHeuristic(n);

                // save the closest node
                if(heuistic < last) {

                    last = heuistic;
                    target = n;
                } // if
            } // for

        } // if

        System.out.println("Target: " + target);

        // if target is obtained, move to it
        if(target != null){

            hasTarget = true;

        } // if

    } // checkForPicksAndEnemies()

    private void moveInOnTarget(){

        if(hasTarget && target != null){

            // use A* to get next move

            Traversator aStar = new AStarTraversator(target);
            aStar.traverse(maze,this);
            nextMove = aStar.getNextNode();
            hasNextMove = true;
            System.out.println("Next Move" + nextMove +" current node: "+this + " Target: " + target);

        }
        else
        {
            hasTarget = false;
            target = null;
        } // if

    } // moveInOnTarget

    private void moveToNextNode(){

        if(nextMove != null){

            synchronized (lock) {

                for(Node n : adjacentNodes(maze)){

                    // check if next move is an adjacent node
                    if(nextMove.equals(n)&& n.getId() == -1){

                        // save last node
                        lastNode = nextMove;

                        // if at target, finish
                        if(nextMove.equals(target)){

                            target = null;
                            hasTarget = false;
                            System.out.println("Got target");

                        }


                        // swap nodes to move
                        swapNodes(this, nextMove);

                        // reset nextMove
                        nextMove = null;
                        hasNextMove = false;

                        // go for target if player has one
                        if(hasTarget && target != null)
                            moveInOnTarget();

                        // return
                        return;
                    } // if
                } // for

                // if not returned, didn't have next move
                // move randomly instead
                randomMove();

                // reset next move
                nextMove = null;
                hasNextMove = false;
                return;
            } // synchronized

        } else { // if nextMove is null

            // move randomly
            randomMove();

            // flag as not having next move
            hasNextMove = false;

        } // if

    } // moveToNextNode()

    // Method for randomly moving the player
    // run in a thread
    private void randomMove(){

        synchronized (lock) {

            Node[] adjacentNodes = null;
            List<Node> canMoveTo = new ArrayList<>();

            // get the players adjacent nodes
            adjacentNodes = adjacentNodes(maze);

            for (Node n : adjacentNodes) {

                // check that the node is empty space
                if (n.getId() == -1 && !n.equals(lastNode)) {

                    // add node to list of available nodes
                    canMoveTo.add(n);
                } // if
            } // if

            if (canMoveTo.size() > 0) {

                // pick a random index to randomMove to
                int index = rand.nextInt(canMoveTo.size());

                // save last node
                lastNode = canMoveTo.get(index);

                // move player to selected adjacent node
                swapNodes(this, canMoveTo.get(index));

            } else if(canMoveTo.size() < 1 && lastNode != null){ // if moved into a corner, go back to last node

                System.out.println("No Moves");
                // randomMove to last node
                swapNodes(this, lastNode);

            } // if

        } // synchronized()

    } // randomMove()


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

            // check if node is target
            if(n.equals(target)){
                target = null;
                hasTarget = false;
            }

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
