package ie.gmit.sw.ai.node;

import java.awt.Color;

/*
Taken from AI-MAZE_ALGOS project from moodle
 */

public class Node {

    // id number to represent which sprite node is
    private int id = -1;

	public enum Direction {North, South, East, West};
	private Node parent;
	private Color color = Color.BLACK;
	private Direction[] paths = null;
	public boolean visited =  false;
	public boolean goal;
	private int row = -1;
	private int col = -1;
	private int distance;
	
	public Node(int row, int col, int id) {
		this.row = row;
		this.col = col;
		this.id = id;
	}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRow() {
		return row;
	}

	public int getCol() {
		return col;
	}

	public Node getParent() {
		return parent;
	}

	public void setParent(Node parent) {
		this.parent = parent;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	
	public boolean hasDirection(Direction direction){	
		for (int i = 0; i < paths.length; i++) {
			if (paths[i] == direction) return true;
		}
		return false;
	}
	
	public Node[] children(Node[][] maze){		
		java.util.List<Node> children = new java.util.ArrayList<Node>();
				
		if (row > 0 && maze[row - 1][col].hasDirection(Direction.South)) children.add(maze[row - 1][col]); //Add North
		if (row < maze.length - 1 && maze[row + 1][col].hasDirection(Direction.North)) children.add(maze[row + 1][col]); //Add South
		if (col > 0 && maze[row][col - 1].hasDirection(Direction.East)) children.add(maze[row][col - 1]); //Add West
		if (col < maze[row].length - 1 && maze[row][col + 1].hasDirection(Direction.West)) children.add(maze[row][col + 1]); //Add East
		
		return (Node[]) children.toArray(new Node[children.size()]);
	}

	public Node[] adjacentNodes(Node[][] maze){
		java.util.List<Node> adjacents = new java.util.ArrayList<Node>();

		if (row > 0) {
			if(maze[row - 1][col].getId() != 0) // only add if not a hedge
				adjacents.add(maze[row - 1][col]); //Add North
		}
		if (row < maze.length - 1) {
			if(maze[row + 1][col].getId() != 0) // only add if not a hedge
				adjacents.add(maze[row + 1][col]); //Add South
		}
		if (col > 0) {
			if(maze[row][col - 1].getId() != 0) // only add if not a hedge
				adjacents.add(maze[row][col - 1]); //Add West
		}
		if (col < maze[row].length - 1) {
			if(maze[row][col + 1].getId() != 0) // only add if not a hedge
				adjacents.add(maze[row][col + 1]); //Add East
		}
		
		return (Node[]) adjacents.toArray(new Node[adjacents.size()]);
	}
	
	public Direction[] getPaths() {
		return paths;
	}

	public void addPath(Direction direction) {
		int index = 0;
		if (paths == null){
			paths = new Direction[index + 1];		
		}else{
			index = paths.length;
			Direction[] temp = new Direction[index + 1];
			for (int i = 0; i < paths.length; i++) temp[i] = paths[i];
			paths = temp;
		}
		
		paths[index] = direction;
	}

	public boolean isVisited() {
		return visited;
	}

	public void setVisited(boolean visited) {
		this.color = Color.BLUE;
		this.visited = visited;
	}

	public boolean isGoalNode() {
		return goal;
	}

	public void setGoalNode(boolean goal) {
		this.goal = goal;
	}
	
	public int getHeuristic(Node goal){
		double x1 = this.col;
		double y1 = this.row;
		double x2 = goal.getCol();
		double y2 = goal.getRow();
		return (int) Math.sqrt(Math.pow((x2 - x1), 2) + Math.pow((y2 - y1), 2));
	}
	
	public int getPathCost() {
		return distance;
	}

	public void setPathCost(int distance) {
		this.distance = distance;
	}

	public String toString() {
		return "[" + row + "/" + col + "]";
	}
}