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
    private volatile int moveNum = 0;
    private Node playerLoc = null;

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
                    search(row,col);

                    // start moving the spider
                    move();

                } catch (Exception ex) {

                } // try catch
            } // while

        });

    } // constructor


    // Method for moving the spider
    // run in a thread
    private void move(){

        synchronized (lock) {

            //System.out.println("Moving number: " + moveNum);

            Node[] adjacentNodes = null;
            List<Node> canMoveTo = new ArrayList<>();

            // get the spiders adjacent nodes
            adjacentNodes = adjacentNodes(maze);

            for (Node n : adjacentNodes) {

                // check that the node is empty space
                if (n.getId() == -1) {

                    // add node to list of available nodes
                    canMoveTo.add(n);
                } // if
            } // if

            if (canMoveTo.size() > 0) {
                int newX, newY, oldX, oldY;

                oldX = getRow();
                oldY = getCol();

                // pick a random index to move to
                int index = rand.nextInt(canMoveTo.size());

                newX = canMoveTo.get(index).getRow();
                newY = canMoveTo.get(index).getCol();

                // update X and Y
                setRow(newX);
                setCol(newY);
                canMoveTo.get(index).setRow(oldX);
                canMoveTo.get(index).setCol(oldY);

                // move to that node
                maze[newX][newY] = (SpiderNode)this;

                // remove self from original spot
                maze[oldX][oldY] = canMoveTo.get(index);

            } // if

        } // synchronized()

        //moveNum++;
    } // move()

    private void search(int row, int col){

        //traverses using Best First Traversator to find player
        //Traversator t = new BestFirstTraversator(playerLoc);

        //traverses using Depth Limited DFS Traversator to find player at a given limit
        Traversator t = new DepthLimitedDFSTraversator(10,playerLoc);

        //transverse from node 0 0 //can change 0 0 to sprites location to search from their location
        t.traverse(maze, maze[row][col]);
    }




} // class
