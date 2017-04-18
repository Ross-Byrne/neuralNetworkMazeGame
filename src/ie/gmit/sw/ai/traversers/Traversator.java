package ie.gmit.sw.ai.traversers;

/*
Taken from AI-MAZE_ALGOS project from moodle
 */

import ie.gmit.sw.ai.node.*;

public interface Traversator {
	public void traverse(Node[][] maze, Node start);
}
