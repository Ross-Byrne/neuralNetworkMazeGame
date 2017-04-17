package ie.gmit.sw.ai.node;

import java.util.concurrent.*;

/**
 * Created by Ross Byrne on 17/04/17.
 * An extension of Node class, to represent the spiders
 */
public class SpiderNode extends Node {

    private Object lock = null;
    private Node[][] maze = null;
    private ExecutorService executor = Executors.newFixedThreadPool(1);

    public SpiderNode(int row, int col, int id, Object lock, Node[][] maze) {

        // setup constructor
        super(row, col, id);

        // set variables
        this.lock = lock;
        this.maze = maze;

        // print out the children
        System.out.println("Start");
        System.out.println(adjacentNodes(maze).length);
        System.out.println("Finish\n");

    }








} // class
