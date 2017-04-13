package ie.gmit.sw.ai;


/*
Make a Singleton, so the maze array can be updated from Sprite.
Make maze array AtomicReferenceArray<Character> so it is concurrent.
 */

public class Maze {

	private Object lock = new Object();	// used for locking when threads try to update the maze
	private char[][] maze;
	    
	public Maze(int dimension){

		maze = new char[dimension][dimension];
		init();
		buildMaze();
		
		int featureNumber = (int)((dimension * dimension) * 0.01);
		addFeature('\u0031', '0', featureNumber); //1 is a sword, 0 is a hedge
		addFeature('\u0032', '0', featureNumber); //2 is help, 0 is a hedge
		addFeature('\u0033', '0', featureNumber); //3 is a bomb, 0 is a hedge
		addFeature('\u0034', '0', featureNumber); //4 is a hydrogen bomb, 0 is a hedge
		
		featureNumber = (int)((dimension * dimension) * 0.01);
		addFeature('\u0036', '\u0020', featureNumber); //6 is a Black Spider, 0 is a hedge
		addFeature('\u0037', '\u0020', featureNumber); //7 is a Blue Spider, 0 is a hedge
		addFeature('\u0038', '\u0020', featureNumber); //8 is a Brown Spider, 0 is a hedge
		addFeature('\u0039', '\u0020', featureNumber); //9 is a Green Spider, 0 is a hedge
		addFeature('\u003A', '\u0020', featureNumber); //: is a Grey Spider, 0 is a hedge
		addFeature('\u003B', '\u0020', featureNumber); //; is a Orange Spider, 0 is a hedge
		addFeature('\u003C', '\u0020', featureNumber); //< is a Red Spider, 0 is a hedge
		addFeature('\u003D', '\u0020', featureNumber); //= is a Yellow Spider, 0 is a hedge
	}
	
	private void init(){
		for (int row = 0; row < maze.length; row++){
			for (int col = 0; col < maze[row].length; col++){
				maze[row][col] = '0'; //Index 0 is a hedge...
			}
		}
	}
	
	private void addFeature(char feature, char replace, int number){
		int counter = 0;
		while (counter < feature){
			int row = (int) (maze.length * Math.random());
			int col = (int) (maze[0].length * Math.random());
			
			if (maze[row][col] == replace){
				maze[row][col] = feature;
				counter++;
			}
		}
	}
	
	private void buildMaze(){ 
		for (int row = 1; row < maze.length - 1; row++){
			for (int col = 1; col < maze[row].length - 1; col++){
				int num = (int) (Math.random() * 10);
				if (num > 5 && col + 1 < maze[row].length - 1){
					maze[row][col + 1] = '\u0020'; //\u0020 = 0x20 = 32 (base 10) = SPACE
				}else{
					if (row + 1 < maze.length - 1)maze[row + 1][col] = '\u0020';
				}
			}
		}		
	}
	
	public char[][] getMaze(){
		return this.maze;
	}
	
	public char get(int row, int col){
		return this.maze[row][col];
	}
	
	public void set(int row, int col, char c){
		this.maze[row][col] = c;
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