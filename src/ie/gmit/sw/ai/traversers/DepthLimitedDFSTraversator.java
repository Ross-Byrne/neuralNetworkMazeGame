package ie.gmit.sw.ai.traversers;

/*
    Adapted from AI-MAZE_ALGOS project from moodle
 */

import ie.gmit.sw.ai.node.*;
import java.util.*;

public class DepthLimitedDFSTraversator implements Traversator{

	private Node[][] maze;
	private int limit;
	private boolean keepRunning = true;
	private int visitCount = 0;
    private Node goal;
    private Set<Node> isVisited = null;
    private LinkedList<Node> pathToGoal = null;
	
	public DepthLimitedDFSTraversator(int limit,Node goal){
		this.limit = limit;
		this.goal = goal;
	}
	
	public void traverse(Node[][] maze, Node node) {

	    // create new list to store path to goal node
	    pathToGoal = new LinkedList<>();
		this.maze = maze;

		// create new hashset to keep track of visited nodes
		isVisited = new HashSet<>();

        // if returns true, goal was found
		if(dfs(node, 1) == true){

		    // add node to path to goal node
		    pathToGoal.addFirst(node);
        } // if

		// clear visited nodes
		isVisited = null;

    }

    // gets the next node in the path
    // to the goal node if one was found
    // otherwise returns null
    public Node getNextNode(){

	    if(pathToGoal.size() > 0){

	        return pathToGoal.getFirst();
        }
        else
        {
	        return null;
        } // if

    } // getNextNode()

    // returns true if current node is on path to goal
	private boolean dfs(Node node, int depth){
		if (!keepRunning || depth > limit) return false;

		// add node to is visited
		isVisited.add(node);
		visitCount++;

		// check if goal node
		if (node.equals(goal)){

		    // add goal to path to goal
		    pathToGoal.addFirst(node);

		    // stop running
	        keepRunning = false;

	        // return true, goal found
			return true;
		} // if

        // get nodes children
		Node[] children = node.adjacentNodes(maze);
		for (int i = 0; i < children.length; i++) {

		    // only visit node's children if not visited
			if (children[i] != null && !isVisited.contains(children[i])){

			    // if goal was found
				if(dfs(children[i], depth + 1) == true) {

				    // save this node to path to goal
                    pathToGoal.addFirst(node);

                    // return true so next node in path is saved
                    return true;
                } // if
			} // if
		} // for

        // goal not found, return false
        return false;
	} // dfs()

} // class