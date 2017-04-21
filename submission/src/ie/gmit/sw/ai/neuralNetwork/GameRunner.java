package ie.gmit.sw.ai.neuralNetwork;

/**
 * Tests the Combat Neural Network
 */
public class GameRunner {


    public static void main(String[] args) throws Exception{


        // Tests for the Neural Network
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