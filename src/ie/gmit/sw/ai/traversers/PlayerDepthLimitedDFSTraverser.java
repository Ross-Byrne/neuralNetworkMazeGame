package ie.gmit.sw.ai.traversers;

import ie.gmit.sw.ai.node.Node;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

/*
    Adapted from AI-MAZE_ALGOS project from moodle
 */

public class PlayerDepthLimitedDFSTraverser extends DepthLimitedDFSTraversator {
    private Node[][] maze;
    private int limit;
    private boolean keepRunning = true;
    private int visitCount = 0;
    private Node start;
    private Set<Node> isVisited = null;
    private LinkedList<Node> pathToGoal = null;
    private Set<Node> enemies = null;

    public PlayerDepthLimitedDFSTraverser(int limit){
        super(limit,new Node(0,0,0));
        this.maze = maze;
        this.limit= limit;
    }

    public void traverseForEnemies(Node[][] maze, Node node) {

        // create hash set to track enemies
        enemies = new HashSet<>();
        this.maze = maze;

        // set start node
        start = node;

        // create new hashset to keep track of visited nodes
        isVisited = new HashSet<>();

        // start DFS
        countEnemiesdfs(start,limit);

        // clear visited nodes
        isVisited = null;
    }

    //returns the number of found enemies
    public int getEnemies(){
        return this.enemies.size();

    } // getNextNode()

    // dfs search to count enemies
    private void countEnemiesdfs(Node node, int depth){
        if (depth > limit)
            return;

        // flag node as visited
        isVisited.add(node);
        visitCount++;

        // if node is hostile
        if (node.isHostile()) {

            // save enemy
            enemies.add(node);

        } // if

        // get nodes children
        Node[] children = node.adjacentNodes(maze);
        for (int i = 0; i < children.length; i++) {

            // only visit children on node if theyt havn't been visited
            if (children[i] != null && !isVisited.contains(children[i])){

                // visit child node
                countEnemiesdfs(children[i], depth + 1);

            } // if
        } // for
    } // dfs()

} // class