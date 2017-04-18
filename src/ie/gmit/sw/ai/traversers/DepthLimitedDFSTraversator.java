package ie.gmit.sw.ai.traversers;

/*
Taken from AI-MAZE_ALGOS project from moodle
 */

import ie.gmit.sw.ai.node.*;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

public class DepthLimitedDFSTraversator implements Traversator{
	private Node[][] maze;
	private int limit;
	private boolean keepRunning = true;
	private long time = System.currentTimeMillis();
	private int visitCount = 0;
    private Node goal;
    private Node start;
    private Set<Node> isVisited = null;
    private LinkedList<Node> pathToGoal = null;
	
	public DepthLimitedDFSTraversator(int limit,Node goal){
		this.limit = limit;
		this.goal = goal;
	}
	
	public void traverse(Node[][] maze, Node node) {

	    pathToGoal = new LinkedList<>();
		this.maze = maze;

		start = node;

		// create new hashset to keep track of visited nodes
		isVisited = new HashSet<>();

        // System.out.println("Search with limit " + limit);
		if(dfs(node, 1) == true){

		    pathToGoal.addFirst(node);
        }

		//System.out.println("Finished Search: " + isVisited.size() + " Visit count: " + visitCount);

		// clear visited nodes
		isVisited = null;

        System.out.println("Path size: " + pathToGoal.size());
    }
	
	private boolean dfs(Node node, int depth){
		if (!keepRunning || depth > limit) return false;

        //node.setVisited(true);
		isVisited.add(node);

		visitCount++;
		
		if (node.equals(goal)){
		    pathToGoal.addFirst(node);
            System.out.println("Goal Found by: " + start.hashCode());
            time = System.currentTimeMillis() - time; //Stop the clock
	        TraversatorStats.printStats(node, time, visitCount);
	        keepRunning = false;
			return true;
		}
		
		Node[] children = node.adjacentNodes(maze);
		for (int i = 0; i < children.length; i++) {
			if (children[i] != null && !isVisited.contains(children[i])){
				children[i].setParent(node);
				if(dfs(children[i], depth + 1) == true) {
                    pathToGoal.addFirst(node);
                    return true;
                } // if
			}
		}
        return false;
	}
}