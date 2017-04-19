package ie.gmit.sw.ai.neuralNetwork;

import ie.gmit.sw.ai.neuralNetwork.activator.*;

public class GameRunner {

    /*
        1 = Health (2 = Healthy, 1 = Minor Injuries, 0 = Serious Injuries)
        2 = Has Sword (1 = Yes, 0 = No)
        3 = Has Bomb (1 = Yes, 0 = No)
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

    public void action(double health, double sword, double bomb, double enemies) throws Exception{

        double[] params = {health, sword, bomb, enemies};

        NeuralNetwork nn = new NeuralNetwork(Activator.ActivationFunction.Sigmoid, 4, 3, 4);
        Trainator trainer = new BackpropagationTrainer(nn);
        trainer.train(data, expected, 0.01, 100000);
        
        double[] result = nn.process(params);

        for(double val : result){
            System.out.println(val);
        }

        System.out.println("==>" + (Utils.getMaxIndex(result) + 1));

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

    public static void main(String[] args) throws Exception{
//        double health = 2;
//        double sword = 1;
//        double gun = 0;
//        double enemies = 2;
//        double[] v = {1, 2, 3, enemies};
//
//        double[] result = null;
//
//        result = Utils.normalize(v, 0.0, 2.0);
//
//        System.out.println(result[0]);
        //new GameRunner().action(health, sword, gun, result[0]);

        CombatDecisionNN combatNet = new CombatDecisionNN();

        combatNet.action(0.5, 1, 1, 0.5); // attack
        combatNet.action(0, 1, 1, 0);
        combatNet.action(0, 1, 1, 0);
        combatNet.action(0, 1, 1, 0);
        combatNet.action(0.5, 0, 0, 0.5); // panic
        combatNet.action(0.5, 0, 0, 0.5);
        combatNet.action(0.5, 0, 0, 0.5);
        combatNet.action(0.5, 0, 0, 0.5);
        combatNet.action(0, 0, 1, 1);
        combatNet.action(0, 0, 1, 1); // run
        combatNet.action(0, 0, 1, 1);
        combatNet.action(0, 0, 1, 1);
        combatNet.action(0, 1, 0, 0); // heal
        combatNet.action(0, 1, 0, 0);
        combatNet.action(0, 1, 0, 0);
        combatNet.action(0, 1, 0, 0);

    }
}