package ie.gmit.sw.ai.node;

import ie.gmit.sw.ai.traversers.BestFirstTraversator;
import ie.gmit.sw.ai.traversers.DepthLimitedDFSTraversator;
import ie.gmit.sw.ai.traversers.PlayerDepthLimitedDFSTraverser;
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

    public void flee(Node enemy){

        Node[] adjacentNodes = adjacentNodes(maze);
        Node move = null;
        int lowestHeurstic=0;

        for (Node n : adjacentNodes) {

            if(n.getHeuristic(enemy)>lowestHeurstic){
                move=n;
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
        Traversator bestFirst = new BestFirstTraversator(player);

        switch(id){

            case 6:
                //System.out.println("Black Spider");

                //transverse from sprites location using Depth Limited DFS
                dlDFS.traverse(maze, maze[row][col]);
                break;

            case 7:
                //System.out.println("Blue Spider");

                //transverse from sprites location using bestFirstTraverser
                //bestFirst.traverse(maze, maze[row][col]);
                //System.out.println("Finished travversing");
                break;

            case 8:
                //System.out.println("Brown Spider");

                //transverse from sprites location using Depth Limited DFS
                dlDFS.traverse(maze, maze[row][col]);
                break;

            case 9:
                //System.out.println("Green Spider");

                //transverse from sprites location using Depth Limited DFS
                dlDFS.traverse(maze, maze[row][col]);
                break;

            case 10:
                //System.out.println("Grey Spider");

                //transverse from sprites location using Depth Limited DFS
                dlDFS.traverse(maze, maze[row][col]);
                break;

            case 11:
                //System.out.println("Orange Spider");

                //transverse from sprites location using Depth Limited DFS
                dlDFS.traverse(maze, maze[row][col]);
                break;

            case 12:
                //System.out.println("Red Spider");

                //transverse from sprites location using Depth Limited DFS
                //dlDFS.traverse(maze, maze[row][col]);


                //checks how many enemies //move to playerNode class
                //PlayerDepthLimitedDFSTraverser enemy = new PlayerDepthLimitedDFSTraverser();
                //enemy.traverseForEnemies(maze, maze[row][col], 10);
                //System.out.println("Number of Enemies:  "+enemy.getEnemyCount());

                break;

            case 13:
                //System.out.println("Yellow Spider");

                //transverse from sprites location using Depth Limited DFS
                dlDFS.traverse(maze, maze[row][col]);
                break;
            default:
                System.out.println("Not a Spider");
                break;


        }

        // get the next node to move to
        nextMove = dlDFS.getNextNode();

        // flag as having a next move
        if(nextMove != null){
            //use best first to find best path
            bestFirst.traverse(maze, maze[row][col]);
            nextMove = bestFirst.getNextNode();
            hasNextMove = true;
        } else {
            hasNextMove = false;
            //System.out.println("has no next Move");
        }

    }

    public boolean isInCombat() {
        return inCombat;
    }

    public void setInCombat(boolean inCombat) {
        this.inCombat = inCombat;
    }
} // class

