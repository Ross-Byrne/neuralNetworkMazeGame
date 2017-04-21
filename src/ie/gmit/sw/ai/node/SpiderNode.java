package ie.gmit.sw.ai.node;

import ie.gmit.sw.ai.traversers.BestFirstTraversator;
import ie.gmit.sw.ai.traversers.DepthLimitedDFSTraversator;
import ie.gmit.sw.ai.traversers.Traversator;
import java.util.*;
import java.util.concurrent.*;

/**
 * Created by Ross Byrne on 17/04/17.
 * An extension of Node class, to represent the spiders
 */
public class SpiderNode extends Node {

    private long movementSpeed = 3000;
    private Random rand = new Random();
    private Object lock = null;
    private Node[][] maze = null;
    private ExecutorService executor = Executors.newFixedThreadPool(1);
    private Node lastNode = null;
    private PlayerNode player = null;
    private int id;
    private Node nextMove = null;
    private boolean hasNextMove = false;
    private boolean inCombat=false;
    private static boolean yellowHostile=false;

    //Variables for spider
    private int health;

    public SpiderNode(int row, int col, int id, Object lock, Node[][] maze ,PlayerNode player,int health) {

        // setup constructor
        super(row, col, id);

        // set variables
        this.lock = lock;
        this.maze = maze;
        this.player = player;
        this.id = id;
        this.health=health;

        // start moving the spider
        executor.submit(() -> {

            while (true) {
                try {

                    // sleep thread to simulate a movement pace
                    Thread.sleep(movementSpeed);

                    // don't do anything
                    if(!inCombat) {

                        //search
                        search(getRow(), getCol());

                        // start moving the spider
                        if (hasNextMove) {        // if spider has next move
                            moveToNextNode();   // move to next node
                        } else {                // otherwise
                            randomMove();       // move randomly
                        }
                    } // if

                } catch (Exception ex) {

                } // try catch
            } // while

        });

    } // constructor

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public void increaseHealth(int health) {
        this.health += health;
    }

    public void decreaseHealth(int health) {
        this.health -= health;
    }

    private void moveToNextNode(){

        if(nextMove != null){

            synchronized (lock) {

                for(Node n : adjacentNodes(maze)){

                    // check if next move is an adjacent node
                    if(nextMove.equals(n)&& n.getId()!=5){

                        // swap nodes to move
                        swapNodes(this, nextMove);

                        // reset nextMove
                        nextMove = null;
                        hasNextMove = false;

                        // return
                        return;
                    } // if
                } // for

                // if not returned, didn't have next move
                // move randomly instead
                //randomMove();

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

    // Method for randomly moving the spider
    // run in a thread
    private void randomMove(){

        synchronized (lock) {

            //System.out.println("Moving number: " + moveNum);

            Node[] adjacentNodes = null;
            List<Node> canMoveTo = new ArrayList<>();

            // get the spiders adjacent nodes
            adjacentNodes = adjacentNodes(maze);

            for (Node n : adjacentNodes) {

                // check that the node is empty space
                if (n.getId() == -1 && !n.equals(lastNode)&& n.getId()!=5) {

                    // add node to list of available nodes
                    canMoveTo.add(n);
                } // if
            } // if

            if (canMoveTo.size() > 0) {

                // pick a random index to randomMove to
                int index = rand.nextInt(canMoveTo.size());

                // move player to selected adjacent node
                swapNodes(this, canMoveTo.get(index));

            } else if(canMoveTo.size() < 1 && lastNode != null){ // if moved into a corner, go back to last node

                // randomMove to last node
                swapNodes(this, lastNode);

            } // if

        } // synchronized()

    } // randomMove()

    public void flee(Node player){

        Node[] adjacentNodes = adjacentNodes(maze);
        Node move = null;
        int lowestHeurstic=0;

        for (Node n : adjacentNodes) {

            if(n.getHeuristic(player)>lowestHeurstic){
                move=n;
                lowestHeurstic=n.getHeuristic(player);
            }

        } // for

        nextMove=move;
    }

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

        // save last node
        lastNode = y;

        // randomMove to that node
        maze[newX][newY] = x;

        // remove self from original spot
        maze[oldX][oldY] = lastNode;

    } // swapNodes()

    private void search(int row, int col){
        Traversator dlDFS = new DepthLimitedDFSTraversator(10,player);
        Traversator bestFirst = new BestFirstTraversator(player); //best path to player

        switch(id){

            case 6:
                //Black Spider

                //transverse from sprites location using Depth Limited DFS
                dlDFS.traverse(maze, maze[row][col]);
                // get the next node to move to
                nextMove = dlDFS.getNextNode();

                //if found player use best first for find best path
                if(nextMove != null){
                    //use best first to find best path
                    bestFirst.traverse(maze, maze[row][col]);
                    nextMove = bestFirst.getNextNode();
                }
                break;

            case 7:
                //Blue Spider

                // Go straight for player if he has a sword
                if(player.getSwords()>0){
                    //transverse from sprites location using bestFirstTraverser
                    bestFirst.traverse(maze, maze[row][col]);
                    nextMove = bestFirst.getNextNode();
                }

                break;

            case 8:
                //Brown Spider

                //brown spiders flee from player
                flee(player);
                break;

            case 9:
                //Green Spider

                //transverse from sprites location using Depth Limited DFS
                dlDFS.traverse(maze, maze[row][col]);
                // get the next node to move to
                nextMove = dlDFS.getNextNode();

                //if found player use best first for find best path
                if(nextMove != null){
                    //use best first to find best path
                    bestFirst.traverse(maze, maze[row][col]);
                    nextMove = bestFirst.getNextNode();
                }

                break;

            case 10:
                //Grey Spider

                dlDFS.traverse(maze, maze[row][col]);
                // get the next node to move to
                nextMove = dlDFS.getNextNode();

                break;

            case 11:
                //Orange Spider

                //transverse from sprites location using Depth Limited DFS
                dlDFS.traverse(maze, maze[row][col]);
                // get the next node to move to
                nextMove = dlDFS.getNextNode();

                //if found player use best first for find best path
                if(nextMove != null){
                    //use best first to find best path
                    bestFirst.traverse(maze, maze[row][col]);
                    nextMove = bestFirst.getNextNode();
                }

                break;

            case 12:
                //Red Spider - Boss spider

                //searches for player if players has 3 or more bombs and a sword
                if(player.getBombs()>2&&player.getSwords()>0){
                    bestFirst.traverse(maze, maze[row][col]);
                    nextMove = bestFirst.getNextNode();
                }
                
                break;

            case 13:
                //Yellow Spider

                if(SpiderNode.isYellowhostile()){
                    //transverse from sprites location using Depth Limited DFS
                    dlDFS.traverse(maze, maze[row][col]);

                    // get the next node to move to
                    nextMove = dlDFS.getNextNode();

                    //if found player use best first for find best path
                    if(nextMove != null){
                        //use best first to find best path
                        bestFirst.traverse(maze, maze[row][col]);
                        nextMove = bestFirst.getNextNode();
                    }
                }
                else
                    flee(player);

                break;
            default:
                System.out.println("Not a Spider");
                break;

        }

        // flag as having a next move
        if(nextMove != null){
            hasNextMove = true;
        } else {
            hasNextMove = false;
            //System.out.println("has no next Move");
        }

    }

    public static boolean isYellowhostile() {
        return yellowHostile;
    }

    public static void setYellowhostile(boolean yellowHostile) {
        SpiderNode.yellowHostile = yellowHostile;
    }

    public boolean isInCombat() {
        return inCombat;
    }

    public void setInCombat(boolean inCombat) {
        this.inCombat = inCombat;
    }
} // class

