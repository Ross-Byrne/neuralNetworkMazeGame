package ie.gmit.sw.ai.node;

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

    public SpiderNode(int row, int col, int id, Object lock, Node[][] maze) {

        // setup constructor
        super(row, col, id);

        // set variables
        this.lock = lock;
        this.maze = maze;

        // start moving the spider
        // run()
        executor.submit(() -> {

            while (true) {
                try {

                    // start moving the spider
                    move();

                    // sleep thread to simulate a movement pace
                    Thread.sleep(movementSpeed);

                } catch (Exception ex) {

                } // try catch
            } // while

        });

    } // constructor


    // Method for moving the spider
    // run in a thread
    private void move(){

        synchronized (lock) {

            System.out.println("Moving number: " + moveNum);

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

                // move to that node
                maze[newX][newY] = (Node)this;

                // remove self from original spot
                maze[oldX][oldY] = canMoveTo.get(index);

            } // if

        } // synchronized()

        moveNum++;
    } // move()





} // class
