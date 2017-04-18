package ie.gmit.sw.ai.fuzzyLogic;

import net.sourceforge.jFuzzyLogic.*;
import net.sourceforge.jFuzzyLogic.plot.*;
import net.sourceforge.jFuzzyLogic.rule.*;

import java.util.List;

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
        Variable risk = fb.getVariable("risk");
        System.out.println("Value: " + risk.getValue());

        // can see that normal membership function wins
        System.out.println("low: " + risk.getMembership("low"));
        System.out.println("normal: " + risk.getMembership("normal"));
        System.out.println("high: " + risk.getMembership("high"));

        JFuzzyChart.get().chart(risk, risk.getDefuzzifier(), true);
        System.out.println("Risk: " + risk.defuzzify());
    }
}
