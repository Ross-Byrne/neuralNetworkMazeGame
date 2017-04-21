package ie.gmit.sw.ai.traversers;

import ie.gmit.sw.ai.node.Node;
import ie.gmit.sw.ai.node.SpiderNode;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

/*
    Adapted from AI-MAZE_ALGOS project from moodle
 */

// Handles the scan searching that the player needs
public class PlayerDepthLimitedDFSTraverser {

    private Node[][] maze;
    private int limit;
    private boolean keepRunning = true;
    private int visitCount = 0;
    private Node start;
    private Set<Node> isVisited = null;
    private LinkedList<Node> pathToGoal = null;
    private Set<Node> enemies = new HashSet<>();

    public PlayerDepthLimitedDFSTraverser(){
        this.maze = maze;
    }

    // returns set of nodes which are near by enemies
    public Set<Node> traverseForEnemies(Node[][] maze, Node node, int limit) {

        visitCount = 0;

        // set the limit
        this.limit = limit;

        // create hash set to track enemies
        enemies = new HashSet<>();
        this.maze = maze;

        // set start node
        start = node;

        // create new hashset to keep track of visited nodes
        isVisited = new HashSet<>();

        // start DFS
        countEnemiesdfs(start,0);

        // clear visited nodes
        isVisited = null;

        return enemies;
    }

    //returns the number of found enemies
    public int getEnemyCount(){
        return this.enemies.size();

    } // getNextNode()

    // dfs search to count enemies
    private void countEnemiesdfs(Node node, int depth){
        if (depth > limit)
            return;

        // flag node as visited
        isVisited.add(node);
        visitCount++;

        // if node is spider
        if (node instanceof SpiderNode) {

            // save enemy
            enemies.add(node);

        } // if

        // get nodes children
        Node[] children = node.adjacentNodes(maze);
        for (int i = 0; i < children.length; i++) {

            // only visit children on node if they haven't been visited
            if (children[i] != null && !isVisited.contains(children[i])){

                // visit child node
                countEnemiesdfs(children[i], depth + 1);

            } // if
        } // for
    } // dfs()

} // class