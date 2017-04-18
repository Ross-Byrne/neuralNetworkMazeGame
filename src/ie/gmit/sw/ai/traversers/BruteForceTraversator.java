package ie.gmit.sw.ai.traversers;

import java.util.*;
import ie.gmit.sw.ai.node.*;
public class BruteForceTraversator implements Traversator{
	private boolean dfs = false;
	
	public BruteForceTraversator(boolean depthFirst){
		this.dfs = depthFirst;

	}
	
	public void traverse(Node[][] maze, Node node) {
        long time = System.currentTimeMillis();
    	int visitCount = 0;
    	
		Deque<Node> queue = new LinkedList<Node>();
		queue.offer(node);

        System.out.println("Starting queue");
		
		while (!queue.isEmpty()){
			node = queue.poll();
			node.setVisited(true);
			visitCount++;

            System.out.println("Checking Goal");

            if (node.isGoalNode()){
		        time = System.currentTimeMillis() - time; //Stop the clock
		        TraversatorStats.printStats(node, time, visitCount);
				break;
			}
			
			try { //Simulate processing each expanded node				
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			Node[] children = node.children(maze);

            //System.out.println(node.children(maze).length);

			for (int i = 0; i < children.length; i++) {
				if (children[i] != null && !children[i].isVisited()){
                    System.out.println(children[i].toString());
					children[i].setParent(node);
					if (dfs){
						queue.addFirst(children[i]);
					}else{
						queue.addLast(children[i]);
					}
				}									
			}			
		}
	}
}