package ie.gmit.sw.ai.neuralNetwork;

import ie.gmit.sw.ai.neuralNetwork.activator.Activator;

/**
 * Created by Ross Byrne on 19/04/17.
 */
public class CombatDecisionNN {

    /*
        1 = Health (2 = Healthy, 1 = Minor Injuries, 0 = Serious Injuries)
        2 = Has Sword (1 = Yes, 0 = No)
        3 = Has Gun (1 = Yes, 0 = No)
        4 = Number of Enemies
     */

    private double[][] data = { //Health, Sword, Gun, Enemies
            { 2, 0, 0, 0 }, { 2, 0, 0, 1 }, { 2, 0, 1, 1 }, { 2, 0, 1, 2 }, { 2, 1, 0, 2 },
            { 2, 1, 0, 1 }, { 1, 0, 0, 0 }, { 1, 0, 0, 1 }, { 1, 0, 1, 1 }, { 1, 0, 1, 2 },
            { 1, 1, 0, 2 }, { 1, 1, 0, 1 }, { 0, 0, 0, 0 }, { 0, 0, 0, 1 }, { 0, 0, 1, 1 },
            { 0, 0, 1, 2 }, { 0, 1, 0, 2 }, { 0, 1, 0, 1 } };

    private double[][] expected = { //Panic, Attack, Hide, Run
            { 0.0, 0.0, 1.0, 0.0 }, { 0.0, 0.0, 1.0, 0.0 }, { 1.0, 0.0, 0.0, 0.0 }, { 1.0, 0.0, 0.0, 0.0 },
            { 0.0, 0.0, 0.0, 1.0 }, { 1.0, 0.0, 0.0, 0.0 }, { 0.0, 0.0, 1.0, 0.0 }, { 0.0, 0.0, 0.0, 1.0 },
            { 1.0, 0.0, 0.0, 0.0 }, { 0.0, 0.0, 0.0, 1.0 }, { 0.0, 0.0, 0.0, 1.0 }, { 0.0, 0.0, 0.0, 1.0 },
            { 0.0, 0.0, 1.0, 0.0 }, { 0.0, 0.0, 0.0, 1.0 }, { 0.0, 0.0, 0.0, 1.0 }, { 0.0, 1.0, 0.0, 0.0 },
            { 0.0, 1.0, 0.0, 0.0 }, { 0.0, 0.0, 0.0, 1.0 } };

    public void panic(){
        System.out.println("Panic");
    }

    public void attack(){
        System.out.println("Attack");
    }

    public void hide(){
        System.out.println("Hide");
    }

    public void runAway(){
        System.out.println("Run Away");
    }

    public void action(double health, double sword, double gun, double enemies) throws Exception{

        double[] params = {health, sword, gun, enemies};

        NeuralNetwork nn = new NeuralNetwork(Activator.ActivationFunction.Sigmoid, 4, 3, 4);
        Trainator trainer = new BackpropagationTrainer(nn);
        trainer.train(data, expected, 0.01, 100000);

        double[] result = nn.process(params);

        int output = (Utils.getMaxIndex(result) + 1);

        switch(output){
            case 1:
                panic();
                break;
            case 2:
                attack();
                break;
            case 3:
                hide();
                break;
            default:
                runAway();
        }
    }

} // class
