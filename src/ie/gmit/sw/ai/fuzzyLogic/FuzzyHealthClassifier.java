package ie.gmit.sw.ai.fuzzyLogic;

import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.FunctionBlock;
import net.sourceforge.jFuzzyLogic.rule.Variable;

/**
 * Created by Ross Byrne on 20/04/17.
 *
 * A Fuzzy logic classifier, that classifies the player's
 * Health between 3 categories, Low, normal and high.
 * This classification is then passed to the player's combat neural network
 * which decides what happens in combat.
 */
public class FuzzyHealthClassifier {

    private FIS fis = null;
    private FunctionBlock fb = null;

    public FuzzyHealthClassifier(){

        // setup the fuzzy logic system
        fis = FIS.load("./fcl/health.fcl", true);
        fb = fis.getFunctionBlock("Project");

        // set the variables
        fis.setVariable("health", 50);

        fis.evaluate();

        // Show output variable's chart
        Variable injuries = fb.getVariable("injuries");
        System.out.println("Value: " + injuries.getValue());

        // can see that normal membership function wins
        System.out.println("Low Health: " + injuries.getMembership("serious"));
        System.out.println("Normal Health: " + injuries.getMembership("minor"));
        System.out.println("High Health: " + injuries.getMembership("none"));

        //JFuzzyChart.get().chart(injuries, injuries.getDefuzzifier(), true);
        System.out.println("Injuries: " + injuries.defuzzify());

    }

    public void setFuzzyVariable(String vName, int value){


    }
} // class
