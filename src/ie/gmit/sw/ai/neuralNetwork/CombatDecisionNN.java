package ie.gmit.sw.ai.neuralNetwork;

import ie.gmit.sw.ai.neuralNetwork.activator.Activator;

/**
 * Created by Ross Byrne on 19/04/17.
 *
 * A Neural Network to decide what happens when a player
 * engages in combat with a spider.
 * The Neural net is trained in the constructor when the object is created.
 * The network takes 4 inputs.
 * Health, which is classified by a Fuzzy logic system,
 * Sword, which is whether or not the player has one,
 * Bomb, which is whether or not the player has any
 * Enemies, which is classified using a Fuzzy logic system.
 * The result is either Attack, panic, heal or run.
 * The logic for this functionality is in PlayerNode.java.
 */
public class CombatDecisionNN {

    private NeuralNetwork nn = null;

    public CombatDecisionNN(){

        nn = new NeuralNetwork(Activator.ActivationFunction.Sigmoid, 4, 3, 4);
        Trainator trainer = new BackpropagationTrainer(nn);
        trainer.train(data, expected, 0.2, 1000000);
    }

    /*
        1 = Health (1 = Healthy, 0.5 = Minor Injuries, 0 = Serious Injuries)
        2 = Has Sword (1 = Yes, 0 = No)
        3 = Has Bomb (1 = Yes, 0 = No)
        4 = Enemies Status (0 = One, 0.5 = Two, 1 = Three or More)
     */

    private double[][] data =

    { //Health, Sword, Bomb, Enemies

            // No Sword, No Bomb
            { 1, 0, 0, 0 }, { 1, 0, 0, 0.5 }, { 1, 0, 0, 1 }, // full health, enemies covered
            { 0.5, 0, 0, 0 }, { 0.5, 0, 0, 0.5 }, { 0.5, 0, 0, 1 }, // minior injuries, enemies covered
            { 0, 0, 0, 0 }, { 0, 0, 0, 0.5 }, { 0, 0, 0, 1 }, // serious injuries, enemies covered

            // Sword, No Bomb
            { 1, 1, 0, 0 }, { 1, 1, 0, 0.5 }, { 1, 1, 0, 1 }, // full health, enemies covered
            { 0.5, 1, 0, 0 }, { 0.5, 1, 0, 0.5 }, { 0.5, 1, 0, 1 }, // minior injuries, enemies covered
            { 0, 1, 0, 0 }, { 0, 1, 0, 0.5 }, { 0, 1, 0, 1 }, // serious injuries, enemies covered

            // No Sword, Bomb
            { 1, 0, 1, 0 }, { 1, 0, 1, 0.5 }, { 1, 0, 1, 1 }, // full health, enemies covered
            { 0.5, 0, 1, 0 }, { 0.5, 0, 1, 0.5 }, { 0.5, 0, 1, 1 }, // minior injuries, enemies covered
            { 0, 0, 1, 0 }, { 0, 0, 1, 0.5 }, { 0, 0, 1, 1 }, // serious injuries, enemies covered

            // Sword, Bomb
            { 1, 1, 1, 0 }, { 1, 1, 1, 0.5 }, { 1, 1, 1, 1 }, // full health, enemies covered
            { 0.5, 1, 1, 0 }, { 0.5, 1, 1, 0.5 }, { 0.5, 1, 1, 1 }, // minior injuries, enemies covered
            { 0, 1, 1, 0 }, { 0, 1, 1, 0.5 }, { 0, 1, 1, 1 } // serious injuries, enemies covered
    };

    private double[][] expected =

    { // Attack, Panic, Heal,  Run

            // No Sword, No Bomb
            { 1, 0, 0, 0}, { 1, 0, 0, 0 }, { 0, 0, 1, 0 }, // full health, enemies covered
            { 0, 1, 0, 0 }, { 0, 0, 1, 0 }, { 0, 0, 0, 1 }, // minior injuries, enemies covered
            { 0, 0, 1, 0 }, { 0, 0, 0, 1 }, { 0, 0, 0, 1 }, // serious injuries, enemies covered

            // Sword, No Bomb
            { 1, 0, 0, 0 }, { 1, 0, 0, 0 }, { 1, 0, 0, 0 }, // full health, enemies covered
            { 1, 0, 0, 0 }, { 0, 0, 1, 0 }, { 0, 1, 0, 0 }, // minior injuries, enemies covered
            { 0, 0, 1, 0 }, { 0, 0, 0, 1 }, { 0, 0, 0, 1 }, // serious injuries, enemies covered

            // No Sword, Bomb
            { 1, 0, 0, 0 }, { 1, 0, 0, 0 }, { 1, 0, 0, 0 }, // full health, enemies covered
            { 1, 0, 0, 0 }, { 0, 1, 0, 0 }, { 0, 1, 0, 0 }, // minior injuries, enemies covered
            { 0, 0, 1, 0 }, { 0, 0, 1, 0 }, { 0, 0, 0, 1 }, // serious injuries, enemies covered

            // Sword, Bomb
            { 1, 0, 0, 0 }, { 1, 0, 0, 0 }, { 1, 0, 0, 0 }, // full health, enemies covered
            { 1, 0, 0, 0 }, { 1, 0, 0, 0 }, { 0, 1, 0, 0 }, // minior injuries, enemies covered
            { 1, 0, 0, 0 }, { 0, 0, 1, 0 }, { 0, 0, 1, 0 } // serious injuries, enemies covered
    };


    // using provided inputs, returns a result from the trained neural network
    public int action(double health, double sword, double bomb, double enemies) throws Exception{

        double[] params = {health, sword, bomb, enemies};

        double[] result = nn.process(params);

        for(double val : result){
            System.out.println(val);
        }

        int output = (Utils.getMaxIndex(result) + 1);

        switch(output){
            case 1:
                // attack
                return 1;
            case 2:
                // panic
                return 2;
            case 3:
                // heal
                return  3;
            default:
                // run away
                return 4;
        } // switch

    } // action()

} // class
