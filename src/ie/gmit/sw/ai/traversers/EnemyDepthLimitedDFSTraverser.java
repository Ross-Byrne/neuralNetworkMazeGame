package ie.gmit.sw.ai.traversers;

import ie.gmit.sw.ai.node.Node;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

/*
    Adapted from AI-MAZE_ALGOS project from moodle
 */

public class EnemyDepthLimitedDFSTraverser  extends DepthLimitedDFSTraversator {
    private Node[][] maze;
    private int limit;
    private boolean keepRunning = true;
    private long time = System.currentTimeMillis();
    private int visitCount = 0;
    private Node start;
    private Set<Node> isVisited = null;
    private LinkedList<Node> pathToGoal = null;
    private int enemies=0;

    public EnemyDepthLimitedDFSTraverser(int limit){
        super(limit,new Node(0,0,0));
        this.maze = maze;
        this.limit= limit;
    }

    public void traverseForEnemies(Node[][] maze, Node node) {

        enemies = 0;
        pathToGoal = new LinkedList<>();
        this.maze = maze;

        start = node;

        // create new hashset to keep track of visited nodes
        isVisited = new HashSet<>();

        pathToGoal.addFirst(node);

        dfs(start,limit);

        // clear visited nodes
        isVisited = null;
    }

    //returns the number of found enemies
    public int getEnemies(){
        return this.enemies;

    } // getNextNode()

    private void dfs(Node node, int depth){
        while(depth >= 0) {
            isVisited.add(node);

            visitCount++;

            if (node.isHostile()) {
                pathToGoal.addFirst(node);
                //System.out.println("Enemy Found by: " + start.hashCode());
                time = System.currentTimeMillis() - time; //Stop the clock
                //TraversatorStats.printStats(node, time, visitCount);
                enemies++;
            }

            Node[] children = node.adjacentNodes(maze);
            for (int i = 0; i < children.length; i++) {
                if (children[i] != null && !isVisited.contains(children[i])) {
                    children[i].setParent(node);
                    depth--;
                    pathToGoal.addFirst(node);
                }
            }

        }
    }
}