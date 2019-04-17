package afm.buildingAparatus;

import java.util.HashMap;

import afm.functions.Function;
import afm.functions.GenericFunction;

/**
 * 
 * @author Eric
 * 
 * A Candidate is a candidate solution function for a given problem.
 * 
 */
public class Candidate extends GenericFunction
{
    /**
     * nodes <id, function nodes>
     * variables <id, input and output nodes>
     * constants <id, constant number nodes>
     * 
     * node is a list of all the nodes in this function
     * variables are the input and output nodes of this function
     * constants are the number nodes that getVectorAtIndex tuned each round
     * 
     */
    HashMap<Integer, Function> nodes;
    HashMap<Integer, Function> variables;
    HashMap<Integer, Function> constants;
    
    
    
    public Candidate(String graphStructure, FunctionPool funPool)
    {
        super(graphStructure,funPool);
    }

    
}
