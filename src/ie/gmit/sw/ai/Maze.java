package ie.gmit.sw.ai;


/*
Make a Singleton, so the maze array can be updated from Sprite.
Make maze array AtomicReferenceArray<Character> so it is concurrent.
 */

import ie.gmit.sw.ai.node.Node;
import ie.gmit.sw.ai.node.Node.Direction;
import java.util.Deque;
import java.util.LinkedList;

import ie.gmit.sw.ai.node.PlayerNode;
import ie.gmit.sw.ai.node.SpiderNode;

public class Maze {

	private Object lock = new Object();	// used for locking when threads try to update the maze
	private Node[][] maze;
    private PlayerNode player;

	public Maze(int dimension){

		maze = new Node[dimension][dimension];
		init();
		buildMaze();
		setPaths();
		unvisit();

        // place the player in maze
		addFeature(5, -1, 1);

		int featureNumber = 20;
		addFeature(1, 0, featureNumber); //1 is a sword, 0 is a hedge
		addFeature(2, 0, featureNumber); //2 is help, 0 is a hedge
		addFeature(3, 0, featureNumber); //3 is a bomb, 0 is a hedge
		addFeature(4, 0, featureNumber); //4 is a hydrogen bomb, 0 is a hedge
		
		featureNumber = 30;
		addFeature(6, -1, 20); //6 is a Black Spider, 0 is a hedge
		addFeature(7, -1, 5); //7 is a Blue Spider, 0 is a hedge
		addFeature(8, -1, 20); //8 is a Brown Spider, 0 is a hedge
		addFeature(9, -1, 20); //9 is a Green Spider, 0 is a hedge
		addFeature(10, -1, 20); //: is a Grey Spider, 0 is a hedge
		addFeature(11, -1, 20); //; is a Orange Spider, 0 is a hedge
		addFeature(12, -1, 1); //< is a Red Spider, 0 is a hedge
		addFeature(13, -1, 20); //= is a Yellow Spider, 0 is a hedge
	}
	
	private void init(){
		for (int row = 0; row < maze.length; row++){
			for (int col = 0; col < maze[row].length; col++){
				maze[row][col] = new Node(row, col, 0); //Index 0 is a hedge...
			}
		}
	}

    private void setPaths(){
        Node node = maze[0][0];

        Deque<Node> stack = new LinkedList<Node>();
        stack.addFirst(node);

        while (!stack.isEmpty()){
            node = stack.poll();

            Node[] adjacents = node.adjacentNodes(maze);

            for (int i = 0; i < adjacents.length; i++) {
                if (!adjacents[i].isVisited()){
                    stack.addFirst(adjacents[i]);
                    Direction dir = getDirection(node, adjacents[i]);
                    node.addPath(dir);
                    adjacents[i].addPath(opposite(dir));
                    adjacents[i].setVisited(true);
                }
            }
        }
    }

    protected void unvisit(){
        for (int i = 0; i < maze.length; i++){
            for (int j = 0; j < maze[i].length; j++){
                maze[i][j].setVisited(false);
                maze[i][j].setParent(null);
            }
        }
    }

    protected Direction getDirection(Node current, Node adjacent){
        if (adjacent.getRow() == current.getRow() - 1 && adjacent.getCol() == current.getCol()) return Direction.North;
        if (adjacent.getRow() == current.getRow() + 1 && adjacent.getCol() == current.getCol()) return Direction.South;
        if (adjacent.getRow() == current.getRow() && adjacent.getCol() == current.getCol() - 1) return Direction.West;
        if (adjacent.getRow() == current.getRow() && adjacent.getCol() == current.getCol() + 1) return Direction.East;
        return null;
    }

    protected Direction opposite(Direction dir){
        if (dir == Direction.North) return Direction.South;
        if (dir == Direction.South) return Direction.North;
        if (dir == Direction.East) return Direction.West;
        if (dir == Direction.West) return Direction.East;
        return null;
    }
	
	private void addFeature(int feature, int replace, int number){
		int counter = 0;
		while (counter < number){
			int row = (int) (maze.length * Math.random());
			int col = (int) (maze[0].length * Math.random());
			
			if (maze[row][col].getId() == replace){

				// if it's a spider, create a spider node
				if(feature > 5) {
					switch (feature){
                        case 6:
                            //Black Spider - Common, Weaker attack
                            maze[row][col] = new SpiderNode(row, col, feature, lock, maze, getPlayer(),15);
                            maze[row][col].setHostile(true);
                            break;

                        case 7:
                            //Blue Spider - Strong, hunt the player, only a few in maze
                            maze[row][col] = new SpiderNode(row, col, feature, lock, maze, getPlayer(),55);
                            maze[row][col].setHostile(true);
                            break;

                        case 8:
                            //Brown Spider - run away from player
                            maze[row][col] = new SpiderNode(row, col, feature, lock, maze, getPlayer(),15);
                            maze[row][col].setHostile(false);
                            break;

                        case 9:
                            //Green Spider - passive, give health to player
                            maze[row][col] = new SpiderNode(row, col, feature, lock, maze, getPlayer(),0);
                            maze[row][col].setHostile(false);
                            break;

                        case 10:
                            //Grey Spider - passive wont attack
                            maze[row][col] = new SpiderNode(row, col, feature, lock, maze, getPlayer(),15);
                            maze[row][col].setHostile(false);
                            break;

                        case 11:
                            //Orange Spider - attack within radius
                            maze[row][col] = new SpiderNode(row, col, feature, lock, maze, getPlayer(),25);
                            maze[row][col].setHostile(true);
                            break;

                        case 12:
                            //Red Spider - Boss, only one, strongest spider
                            maze[row][col] = new SpiderNode(row, col, feature, lock, maze, getPlayer(),155);
                            maze[row][col].setHostile(true);
                            break;

                        case 13:
                            //Yellow Spider - Common, wont attack until provoked then all turn hostile
                            maze[row][col] = new SpiderNode(row, col, feature, lock, maze, getPlayer(),25);
                            maze[row][col].setHostile(false);
                            break;
                        default:
                            //System.out.println("Not a Spider");
                            break;
                    }

				}
				else if(feature == 5) { // if player

					//create the player node
					player = new PlayerNode(row,col,maze);

					//set the player node
					maze[row][col] = player;
				}
				else
				{
					maze[row][col].setId(feature);
				} // if

				counter++;
			} // if
		} // while
	} // addFeature()
	
	private void buildMaze(){ 
		for (int row = 1; row < maze.length - 1; row++){
			for (int col = 1; col < maze[row].length - 1; col++){
				int num = (int) (Math.random() * 10);
				if (num > 5 && col + 1 < maze[row].length - 1){
					maze[row][col + 1].setId(-1); // -1 represents the grey floor
				}else{
					if (row + 1 < maze.length - 1)
					    maze[row + 1][col].setId(-1);
				}
			}
		}
	}
	
	public Node[][] getMaze(){
		return this.maze;
	}
	
	public Node get(int row, int col){
		return this.maze[row][col];
	}
	
	public void set(int row, int col, Node n){

	    n.setRow(row);
	    n.setCol(col);
	    this.maze[row][col] = n;
	}

    public PlayerNode getPlayer(){
        return this.player;
    }
	
	public int size(){
		return this.maze.length;
	}
	
	public String toString(){
		StringBuffer sb = new StringBuffer();
		for (int row = 0; row < maze.length; row++){
			for (int col = 0; col < maze[row].length; col++){
				sb.append(maze[row][col]);
				if (col < maze[row].length - 1) sb.append(",");
			}
			sb.append("\n");
		}
		return sb.toString();
	}

}