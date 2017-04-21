package ie.gmit.sw.ai.traversers;

import ie.gmit.sw.ai.node.*;
import java.util.*;

public class AStarTraversator implements Traversator{
	private Node goal;
    private Set<Node> isVisited = null;
    private Map<Node,Integer> nodeCost = null;
	
	public AStarTraversator(Node goal){
		this.goal = goal;
	}
	
	public void traverse(Node[][] maze, Node node) {
        long time = System.currentTimeMillis();
    	int visitCount = 0;
    	
		PriorityQueue<Node> open = new PriorityQueue<Node>(20, (Node current, Node next)-> (current.getPathCost() + current.getHeuristic(goal)) - (next.getPathCost() + next.getHeuristic(goal)));
		List<Node> closed = new ArrayList<Node>();

        nodeCost = new HashMap<>();


        isVisited = new HashSet<>();

		open.offer(node);
		node.setPathCost(0);

		//nodeCost.putIfAbsent(node,0);

        System.out.println(goal.getId());

		while(!open.isEmpty()){
			node = open.poll();		
			closed.add(node);
			//
            // node.setVisited(true);
            //isVisited.add(node);
			visitCount++;

            System.out.println(node);

            if (node.equals(goal)){
		        time = System.currentTimeMillis() - time; //Stop the clock
		        TraversatorStats.printStats(node, time, visitCount);
				break;
			}


			
			//Process adjacent nodes
			Node[] children = node.adjacentNodes(maze);
			for (int i = 0; i < children.length; i++) {
				Node child = children[i];

                //child.setPathCost(node.getPathCost()+1);


				//int score = node.getPathCost() + child.getHeuristic(goal);
				//int existing = child.getPathCost() + child.getHeuristic(goal);

                //System.out.println("Score: "+score+" Existing: "+existing);

				if ((open.contains(child) || closed.contains(child)) ){//&& existing < score
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
        return null;
    }
}
