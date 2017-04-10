package ie.gmit.sw.ai;

import javax.imageio.*;
import java.awt.image.*;

public class Sprite {
	private String name; //The name of this sprite
	private BufferedImage[] frames; //The set of image frames to animate
 	private int index = 0; //Initial starting index in array

	//private double lifeForce = 500;

	/*

	Sprite will contain the movement code.
	Added vars to keep track of row and col in Maze.
	Change maze to AtomicReferenceArray (it's concurrent)
	Make Maze.java Singleton? so the reference can be obtained
    Thread movement stuff.

    To move, search maze array to decide where to move.
    Once a spot is picked, take self out of Maze array and place self into new spot.
    Sprite can't move while attacking or when attacked.

	 */
 	
	public Sprite(String name, String... images) throws Exception{
		this.name = name;
		this.index = 0; //Initialise the starting index to zero
		this.frames = new BufferedImage[images.length]; //Initialise the image frames
		
		for (int i = 0; i < images.length; i++){
			frames[i] = ImageIO.read(new java.io.File(images[i])); //Read in each image as a BufferedImage
		}
	}

	// public double engage(double weapon, double anger){

	// 	NeuralNetwork nn = NeuralNetworkFactory.getInstance().getNetwork("engage");
	// 	double[] inputs = {weapon, anger};
	// 	double result = nn.process(inputs);
	// 	lifeForce += result;

	// 	FIS fis = FuzzyLogicFactory.getInstance().getFIS("engage");
	// 	double result = fis.evaluate(weapon, anger);
	// 	lifeForce += result;

	// }

	// public boolean isAlive(){

	// 	return lifeForce > 0;
	// }
	
	public BufferedImage getNext(){ //Returns the next image frame
		int idx = index;
		if (index < frames.length - 1){
			index++;
		}else{
			index = 0; //Circle back to the start of the array
		}
		return frames[idx]; 
	}
	
	public String getName(){
		return this.name;
	}
}
