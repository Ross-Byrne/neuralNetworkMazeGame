package ie.gmit.sw.ai;


import ie.gmit.sw.ai.gui.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class GameRunner implements KeyListener{
	private static final int MAZE_DIMENSION = 100;
	private static final int IMAGE_COUNT = 14;
	public static boolean AI_CONTROLLED;
	public static boolean GAME_OVER = false;
	private GameView view;
	private Maze model;
    private static JFrame f;
	
	public GameRunner() throws Exception{

		model = new Maze(MAZE_DIMENSION);
    	view = new GameView(model);

    	Sprite[] sprites = getSprites();
    	view.setSprites(sprites);
    	
    	Dimension d = new Dimension(GameView.DEFAULT_VIEW_SIZE, GameView.DEFAULT_VIEW_SIZE);
    	view.setPreferredSize(d);
    	view.setMinimumSize(d);
    	view.setMaximumSize(d);
    	
        f = new JFrame("GMIT - B.Sc. in Computing (Software Development)");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.addKeyListener(this);
        f.getContentPane().setLayout(new FlowLayout());
        f.add(view);
        f.setSize(1000,1000);
        f.setLocation(100,100);
        f.pack();
        f.setVisible(true);

	}

	// closes the game window
	private static void closeGame(){

        // close the game
        f.dispatchEvent(new WindowEvent(f, WindowEvent.WINDOW_CLOSING));
    }

	private void updateView(){

		view.setCurrentRow(getCurrentRow());
		view.setCurrentCol(getCurrentCol());
	}

	private int getCurrentRow(){
	    return model.getPlayer().getRow();
    }

    private int getCurrentCol(){
        return model.getPlayer().getCol();
    }

    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_RIGHT && getCurrentCol() < MAZE_DIMENSION - 1) {
            tryMove(getCurrentRow(), getCurrentCol() + 1);
        }else if (e.getKeyCode() == KeyEvent.VK_LEFT && getCurrentCol() > 0) {
        	    tryMove(getCurrentRow(), getCurrentCol() - 1);
        }else if (e.getKeyCode() == KeyEvent.VK_UP && getCurrentRow() > 0) {
        	    tryMove(getCurrentRow() - 1, getCurrentCol());
        }else if (e.getKeyCode() == KeyEvent.VK_DOWN && getCurrentRow() < MAZE_DIMENSION - 1) {
                tryMove(getCurrentRow() + 1, getCurrentCol());
        }else if (e.getKeyCode() == KeyEvent.VK_Z){
        	view.toggleZoom();
        }else{
        	return;
        }
        
        //updateView();
    }
    public void keyReleased(KeyEvent e) {} //Ignore
	public void keyTyped(KeyEvent e) {} //Ignore

    
	private boolean tryMove(int row, int col){

        // can only control the player if game is not AI controlled
        if(!GameRunner.AI_CONTROLLED) {

            if (row <= model.size() - 1 && col <= model.size() - 1 && model.get(row, col).getId() == -1) {

                // move node player is going to move to, to the players place
                model.set(getCurrentRow(), getCurrentCol(), model.get(row, col));

                // move the player to the next position
                model.set(row, col, model.getPlayer());

                return true;
            } else {
                return false; //Can't move
            }
        } // if

        return false;
	} // tryMove()
	
	private Sprite[] getSprites() throws Exception{
		//Read in the images from the resources directory as sprites. Note that each
		//sprite will be referenced by its index in the array, e.g. a 3 implies a Bomb...
		//Ideally, the array should dynamically created from the images... 
		Sprite[] sprites = new Sprite[IMAGE_COUNT];
		sprites[0] = new Sprite("Hedge", "resources/hedge.png");
		sprites[1] = new Sprite("Sword", "resources/sword.png");
		sprites[2] = new Sprite("Help", "resources/help.png");
		sprites[3] = new Sprite("Bomb", "resources/bomb.png");
		sprites[4] = new Sprite("Hydrogen Bomb", "resources/h_bomb.png");
		sprites[5] = new Sprite("Spartan Warrior", "resources/spartan_1.png", "resources/spartan_2.png");
		sprites[6] = new Sprite("Black Spider", "resources/black_spider_1.png", "resources/black_spider_2.png");
		sprites[7] = new Sprite("Blue Spider", "resources/blue_spider_1.png", "resources/blue_spider_2.png");
		sprites[8] = new Sprite("Brown Spider", "resources/brown_spider_1.png", "resources/brown_spider_2.png");
		sprites[9] = new Sprite("Green Spider", "resources/green_spider_1.png", "resources/green_spider_2.png");
		sprites[10] = new Sprite("Grey Spider", "resources/grey_spider_1.png", "resources/grey_spider_2.png");
		sprites[11] = new Sprite("Orange Spider", "resources/orange_spider_1.png", "resources/orange_spider_2.png");
		sprites[12] = new Sprite("Red Spider", "resources/red_spider_1.png", "resources/red_spider_2.png");
		sprites[13] = new Sprite("Yellow Spider", "resources/yellow_spider_1.png", "resources/yellow_spider_2.png");
		return sprites;
	}
	
	public static void main(String[] args) throws Exception{

		// get selected option (0 = AI, 1 = Player)
		int result = StartGameJOptionPane.display();

		// set boolean
        if(result == 0)
            AI_CONTROLLED = true;
        else
            AI_CONTROLLED = false;

        // start the game
		new GameRunner();
	}

    public static boolean isGameOver() {
        return GAME_OVER;
    }

    // sets the game to be over, either you won or lost
    // pass in true if player dead, false if boss died
    public static void gameOver(boolean playerDead) {
        GAME_OVER = true;

        if(playerDead){ // game over, player is dead

            // show player dead message
            GameOverJOptionPane.display("Player Died! Game Over!");

        } else { // game over, boss is dead

            // won game, show message
            GameOverJOptionPane.display("Boss Defeated! Game Over!");
        } // if

        // end game
        closeGame();

    } // setGameOver()
} // class