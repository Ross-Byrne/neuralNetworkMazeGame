package ie.gmit.sw.ai.fuzzyLogic;

import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.FunctionBlock;
import net.sourceforge.jFuzzyLogic.rule.LinguisticTerm;
import net.sourceforge.jFuzzyLogic.rule.Variable;

import java.util.List;

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

    }

    // sets the value for an input variable
    public void setInputVariable(String vName, double value) throws Exception {

        // set the value
        fis.setVariable(vName, value);

    } // setInputVariable()

    // gets the name of the winning membership function
    // that belongs to the output variable passed to method
    public String getWinningMembership(String outputVar) throws Exception {

        // evaluate the fuzzy logic
        fis.evaluate();

        Variable variable;
        double value = 0;
        String termName = "";

        // get the output variable
        variable = fb.getVariable(outputVar);

        // get th list of linguistic terms
        List<LinguisticTerm> linguisticTerms = variable.linguisticTermsSorted();

        // for each term
        for (LinguisticTerm t : linguisticTerms){

            // check if the value of the membership funciton is greater then last
            if(variable.getMembership(t.getTermName()) > value){

                // if so, save the bigger value and term name
                termName = t.getTermName();
                value = variable.getMembership(termName);

            } // if
        } // for

        // return the term name with largest membership function value, it wins
        return termName;

    } // getWinningMembership()

} // class
