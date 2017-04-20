package ie.gmit.sw.ai.fuzzyLogic;

import net.sourceforge.jFuzzyLogic.*;
import net.sourceforge.jFuzzyLogic.rule.*;

/**
 * Adapted from fuzzyLogic lab from moodle
 *
 * 
 */
public class Runner {

    public static void main(String[] args) {

        FIS fis = FIS.load("./fcl/health.fcl", true);
        FunctionBlock fb = fis.getFunctionBlock("Project");
        //JFuzzyChart.get().chart(fb);
        fis.setVariable("health", 50);
        //fis.setVariable("health", 90);
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
}


