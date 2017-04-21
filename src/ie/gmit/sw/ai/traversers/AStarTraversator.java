package ie.gmit.sw.ai.traversers;

import ie.gmit.sw.ai.node.*;
import java.util.*;

public class AStarTraversator implements Traversator{
	private Node goal;
	private Node start;
	
	public AStarTraversator(Node goal){
		this.goal = goal;
	}
	
	public void traverse(Node[][] maze, Node node) {
        long time = System.currentTimeMillis();
    	int visitCount = 0;
    	this.start=node;
    	
		PriorityQueue<Node> open = new PriorityQueue<Node>(20, (Node current, Node next)-> (current.getPathCost() + current.getHeuristic(goal)) - (next.getPathCost() + next.getHeuristic(goal)));
		List<Node> closed = new ArrayList<Node>();

		open.offer(node);
		node.setPathCost(0);

        //System.out.println(goal.getId());

		while(!open.isEmpty()){
			node = open.poll();		
			closed.add(node);

			visitCount++;

            if (node.equals(goal)){
		        time = System.currentTimeMillis() - time; //Stop the clock
		        //TraversatorStats.printStats(node, time, visitCount);




				break;
			}
			//Process adjacent nodes
			Node[] children = node.adjacentNodes(maze);
			for (int i = 0; i < children.length; i++) {
				Node child = children[i];

				if ((open.contains(child) || closed.contains(child)) ){
					continue;
				}else{
					open.remove(child);
					closed.remove(child);
					child.setParent(node);
					child.setPathCost(node.getPathCost() + 1);
					open.add(child);
				}
			}									
		}
    }

    public Node getNextNode() {
        Node last=null;
        Node current=null;

        current=goal.getParent();

        do {


            //System.out.println();

            if(!current.equals(start)){
                last=current;
            }
            current=current.getParent();
        }while(!current.equals(start));


        return last;
    }
}
