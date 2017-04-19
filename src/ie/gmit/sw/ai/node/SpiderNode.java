package ie.gmit.sw.ai.node;

import ie.gmit.sw.ai.traversers.DepthLimitedDFSTraversator;

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
    private Node playerLoc = null;
    private Node nextMove = null;
    private boolean hasNextMove = false;

    public SpiderNode(int row, int col, int id, Object lock, Node[][] maze ,Node player) {

        // setup constructor
        super(row, col, id);

        // set variables
        this.lock = lock;
        this.maze = maze;
        this.playerLoc = player;

        // start moving the spider
        // run()
        executor.submit(() -> {

            while (true) {
                try {

                    // sleep thread to simulate a movement pace
                    Thread.sleep(movementSpeed);

                    //search
                    search(getRow(), getCol());

                    // start moving the spider
                    randomMove();

                } catch (Exception ex) {

                } // try catch
            } // while

        });

    } // constructor

    private void moveToNextNode(){

//        if(nextMove != null){
//
//            synchronized (lock) {
//
//                for(Node n : adjacentNodes(maze)){
//                    if(nextMove.equals(n)){
//
//
//                    }
//                }
//
//            } // synchronized
//
//        } else {
//
//            randomMove();
//
//            hasNextMove = false;
//
//        } // if

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
                if (n.getId() == -1 && !n.equals(lastNode)) {

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

        //traverses using Best First Traversator to find player
        //Traversator t = new BestFirstTraversator(playerLoc);

        //traverses using Depth Limited DFS Traversator to find player at a given limit
        DepthLimitedDFSTraversator t = new DepthLimitedDFSTraversator(10,playerLoc);

        //transverse from node 0 0 //can change 0 0 to sprites location to search from their location
        t.traverse(maze, maze[row][col]);

        // get the next node to move to
        nextMove = t.getNextNode();

        // flag as having a next move
        if(nextMove != null){
            hasNextMove = true;
        } else {
            hasNextMove = false;
        }

    }




} // class
