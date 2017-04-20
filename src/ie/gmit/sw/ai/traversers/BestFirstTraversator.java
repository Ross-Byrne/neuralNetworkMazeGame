package ie.gmit.sw.ai.traversers;

/*
Taken from AI-MAZE_ALGOS project from moodle
 */

import ie.gmit.sw.ai.node.*;
import java.util.*;

public class BestFirstTraversator implements Traversator{
	private Node goal;
    private Node start;
    private boolean keepRunning=true;
    private Set<Node> isVisited = null;
    private LinkedList<Node> pathToGoal = null;
    private Node[][] maze;
    private int visitCount;
    private long time = System.currentTimeMillis();
	
	public BestFirstTraversator(Node goal){
		this.goal = goal;
	}
	
	public void traverse(Node[][] maze, Node node) {

        pathToGoal = new LinkedList<>();
        this.maze = maze;

        start = node;

        // create new hashset to keep track of visited nodes
        isVisited = new HashSet<>();


        for(int i=0; i<10; i++){
            search(node);
        }

        //Sort the whole queue. Effectively a priority queue, first in, best out
        Collections.sort(pathToGoal, (Node current, Node next) -> current.getHeuristic(goal) - next.getHeuristic(goal));

        // clear visited nodes
        isVisited = null;

	}

    private void search(Node node){
        isVisited.add(node);

        visitCount++;

        if (node.equals(goal)) {
            pathToGoal.addFirst(node);
            time = System.currentTimeMillis() - time; //Stop the clock
            TraversatorStats.printStats(node, time, visitCount);
            keepRunning = false;
        }

        Node[] children = node.adjacentNodes(maze);
        for (int i = 0; i < children.length; i++) {
            if (children[i] != null && !isVisited.contains(children[i])) {
                children[i].setParent(node);
                pathToGoal.addFirst(children[i]);
            }
        }

    }

    public Node getNextNode() {
            if(pathToGoal.size() > 0){
                return pathToGoal.getFirst();
            }
            else
            {
                return null;
            } // else
    }
}