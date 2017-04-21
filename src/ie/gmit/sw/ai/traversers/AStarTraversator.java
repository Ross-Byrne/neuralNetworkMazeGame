package ie.gmit.sw.ai.traversers;

import ie.gmit.sw.ai.node.*;
import java.util.*;

/**
	A* search adapted from class. Changes made to make it work with out project.
	It is now a little more streamlined then original.
 */
public class AStarTraversator implements Traversator{

	private Node goal;
	private Node start;
	
	public AStarTraversator(Node goal){
		this.goal = goal;
	}
	
	public void traverse(Node[][] maze, Node node) {

	    // save the start node
    	this.start=node;

    	// create open priority queue and closed queue
		PriorityQueue<Node> open = new PriorityQueue<Node>(20, (Node current, Node next)-> (current.getPathCost() + current.getHeuristic(goal)) - (next.getPathCost() + next.getHeuristic(goal)));
		List<Node> closed = new ArrayList<Node>();

		// add start node to open queue
		open.offer(node);

		// set the start nodes cost as 0
		node.setPathCost(0);

		// loop while there are nodes in the open queue
		while(!open.isEmpty()){

		    // get node from open queue
			node = open.poll();

			// add node to closed queue
			closed.add(node);

			// if goal node is found
            if (node.equals(goal)){

                // break out of loop, path to goal has been set
				break;
			} // if

			//Process adjacent nodes
			Node[] children = node.adjacentNodes(maze);

            // loop through node's children
			for (int i = 0; i < children.length; i++) {

			    // get child
				Node child = children[i];

				// if already in queue
				if ((open.contains(child) || closed.contains(child)) ){
					continue; // ignore
				}else{
				    // remove from open and closed queues
					open.remove(child);
					closed.remove(child);

					// set the parent node of child
					child.setParent(node);

					// set the path cost (parent + 1)
					child.setPathCost(node.getPathCost() + 1);

					// add to open queue for processing
					open.add(child);
				} // if
			} // for
		} // while
    } // traverse()

    // gets the next node to move to in path
	// back following the path back from the
	// goal node
    public Node getNextNode() {

        Node last=null;
        Node current=null;

        // get parent of goal node
        current = goal.getParent();

        // loop while not at start node
        do {

        	// if not start node
            if(!current.equals(start)){

            	// save node
                last = current;
            } // if

			// get the next nodes parent
            current = current.getParent();

        }while(!current.equals(start));

        // last node visited is next move
        return last;
    } // getNextNode()
} // class
