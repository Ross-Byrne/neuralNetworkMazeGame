package ie.gmit.sw.ai.traversers;

/*
Taken from AI-MAZE_ALGOS project from moodle
 */

import ie.gmit.sw.ai.node.*;
import java.util.*;

public class BestFirstTraversator implements Traversator{
	private Node goal;
    private Node start;
    private Set<Node> isVisited = null;
    private LinkedList<Node> pathToGoal = null;
    private Node[][] maze;
	
	public BestFirstTraversator(Node goal){
		this.goal = goal;
	}
	
	public void traverse(Node[][] maze, Node node) {

	    // create a new linkedlist for the path to the goal node
        pathToGoal = new LinkedList<>();
        this.maze = maze;

        // save start node
        start = node;

        // create new hashset to keep track of visited nodes
        isVisited = new HashSet<>();

        // search multiple times to get best path
        for(int i=0; i<10; i++){
            search(node);
        } // for

        //Sort the whole queue. Effectively a priority queue, first in, best out
        Collections.sort(pathToGoal, (Node current, Node next) -> current.getHeuristic(goal) - next.getHeuristic(goal));

        // clear visited nodes
        isVisited = null;

	} // traverse()

	// search for the node
    private void search(Node node){

	    // flag node as visited
        isVisited.add(node);

        // check if goal node
        if (node.equals(goal)) {

            // if goal, add to front of path to goal node
            pathToGoal.addFirst(node);
        } // if

        // get nodes children
        Node[] children = node.adjacentNodes(maze);

        // for each child
        for (int i = 0; i < children.length; i++) {

            // if child is not null or if it hasn't been visited
            if (children[i] != null && !isVisited.contains(children[i])) {

                // add child to path to goal
                pathToGoal.addFirst(children[i]);
            } // if
        } // for

    } // search()

    // gets the next node in the path to the goal
    public Node getNextNode() {
        if(pathToGoal.size() > 0){
            return pathToGoal.getFirst();
        }
        else
        {
            return null;
        } // if
    } // getNextNode()
}