package ie.gmit.sw.ai.fuzzyLogic;

import net.sourceforge.jFuzzyLogic.*;
import net.sourceforge.jFuzzyLogic.plot.*;
import net.sourceforge.jFuzzyLogic.rule.*;

/**
 * Created by Ross Byrne on 30/01/17.
 */
public class Runner {

    public static void main(String[] args) {

        FIS fis = FIS.load("./fcl/funding.fcl", true);
        FunctionBlock fb = fis.getFunctionBlock("Project");
        JFuzzyChart.get().chart(fb);
        fis.setVariable("funding", 60);
        fis.setVariable("staffing", 15);
        fis.evaluate();

        // Show output variable's chart
        Variable tip = fb.getVariable("risk");
        JFuzzyChart.get().chart(tip, tip.getDefuzzifier(), true);
        System.out.println("Risk: " + tip.defuzzify());
    }
}
