package ie.gmit.sw.ai.neuralNetwork;

import ie.gmit.sw.ai.neuralNetwork.activator.Activator;

/**
 * Created by Ross Byrne on 19/04/17.
 */
public class CombatDecisionNN {

    /*
        1 = Health (2 = Healthy, 1 = Minor Injuries, 0 = Serious Injuries)
        2 = Has Sword (1 = Yes, 0 = No)
        3 = Has Bomb (1 = Yes, 0 = No)
        4 = Enemies Status (0 = One, 1 = Two, 2 = Three or More)
     */

    private double[][] data =

    {           //Health, Sword, Bomb, Enemies
        // No Sword, No Bomb
        { 2, 0, 0, 0 }, { 2, 0, 0, 1 }, { 2, 0, 0, 2 }, // full health, enemies covered
        { 1, 0, 0, 0 }, { 1, 0, 0, 1 }, { 1, 0, 0, 2 }, // minior injuries, enemies covered
        { 0, 0, 0, 0 }, { 0, 0, 0, 1 }, { 0, 0, 0, 2 }, // serious injuries, enemies covered

        // Sword, No Bomb
        { 2, 1, 0, 0 }, { 2, 1, 0, 1 }, { 2, 1, 0, 2 }, // full health, enemies covered
        { 1, 1, 0, 0 }, { 1, 1, 0, 1 }, { 1, 1, 0, 2 }, // minior injuries, enemies covered
        { 0, 1, 0, 0 }, { 0, 1, 0, 1 }, { 0, 1, 0, 2 }, // serious injuries, enemies covered

        // No Sword, Bomb
        { 2, 0, 1, 0 }, { 2, 0, 1, 1 }, { 2, 0, 1, 2 }, // full health, enemies covered
        { 1, 0, 1, 0 }, { 1, 0, 1, 1 }, { 1, 0, 1, 2 }, // minior injuries, enemies covered
        { 0, 0, 1, 0 }, { 0, 0, 1, 1 }, { 0, 0, 1, 2 }, // serious injuries, enemies covered

        // Sword, Bomb
        { 2, 1, 1, 0 }, { 2, 1, 1, 1 }, { 2, 1, 1, 2 }, // full health, enemies covered
        { 1, 1, 1, 0 }, { 1, 1, 1, 1 }, { 1, 1, 1, 2 }, // minior injuries, enemies covered
        { 0, 1, 1, 0 }, { 0, 1, 1, 1 }, { 0, 1, 1, 2 } // serious injuries, enemies covered
    };

    private double[][] expected =

    { // Attack, Panic, Run
            { 2, 0, 0 }, { 2, 0, 1 }, { 2, 0, 2 }, // full health, enemies covered
            { 1, 0, 0 }, { 1, 0, 1 }, { 1, 0, 2 }, // minior injuries, enemies covered
            { 0,  0, 0 }, { 0, 0, 0, 1 }, { 0, 0, 0, 2 }, // serious injuries, enemies covered

            // Sword, No Bomb
            { 2, 0, 0 }, { 2, 0, 1 }, { 2, 0, 2 }, // full health, enemies covered
            { 1, 0, 0 }, { 1, 0, 1 }, { 1, 0, 2 }, // minior injuries, enemies covered
            { 0, 0, 0 }, { 0, 0, 1 }, { 0, 0, 2 }, // serious injuries, enemies covered

            // No Sword, Bomb
            { 2, 1, 0 }, { 2, 0, 1 }, { 2, 1, 2 }, // full health, enemies covered
            { 1, 1, 0 }, { 1, 0, 1 }, { 1, 1, 2 }, // minior injuries, enemies covered
            { 0, 1, 0 }, { 0, 1, 1 }, { 0, 1, 2 }, // serious injuries, enemies covered

            // Sword, Bomb
            { 2, 1, 0 }, { 2, 1, 1 }, { 2, 1, 2 }, // full health, enemies covered
            { 1, 1, 0 }, { 1, 1, 1 }, { 1, 1, 2 }, // minior injuries, enemies covered
            { 0, 1, 0 }, { 0, 1, 1 }, { 0, 1, 2 } // serious injuries, enemies covered
    };

    public void panic(){
        System.out.println("Panic");
    }

    public void attack(){
        System.out.println("Attack");
    }

    public void runAway(){
        System.out.println("Run Away");
    }

    public void action(double health, double sword, double bomb, double enemies) throws Exception{

        double[] params = {health, sword, bomb, enemies};

        NeuralNetwork nn = new NeuralNetwork(Activator.ActivationFunction.Sigmoid, 4, 3, 3);
        Trainator trainer = new BackpropagationTrainer(nn);
        trainer.train(data, expected, 0.01, 100000);

        double[] result = nn.process(params);

        int output = (Utils.getMaxIndex(result) + 1);

        switch(output){
            case 1:
                attack();
                break;
            case 2:
                panic();
                break;
            default:
                runAway();
        }
    }

} // class
