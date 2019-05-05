/**
 * 
 */
package afm.functions;

import java.util.HashSet;


/**
 * @author Eric
 *
 */
public class Function
{
    public Function[] inputNodes = null;
    public int[] inputSelect = null;
    public String[] inputTypes = null;
    public String[] outputTypes = null;
    public String type = "";
    public int id = -1;
    public HashSet<Integer> dependencies;
    public boolean dependenciesComputed = false;
    public Function[] output = null;
    public boolean computed = false;
    
    /**
     * 
     */
    public void compute()
    {
       computed = true;
    }
    
    public void resetComputed()
    {
        computed = false;
    }
    
    public void resetDependencies()
    {
        dependenciesComputed = false;
    }

    /**
     * 
     * @param n - the index of the output to be retrieved
     * @return the output
     */
    public Function getOutput(int n)
    {
        return null;
    }
    
    public HashSet<Integer> getDependencies()
    {
    	if(dependenciesComputed)
    	{
    		return dependencies;
    	}else
    	{
    		dependencies = new HashSet<>();
    		HashSet<Integer> inputNodeDependencies;
            for(int i=0;i<inputNodes.length;i++)
            {
                if(!inputNodes[i].type.equals("relay"))
                {
                    dependencies.add(inputNodes[i].id);
                    inputNodeDependencies = inputNodes[i].getDependencies();
                    dependencies.addAll(inputNodeDependencies);
                }
            }
            dependencies.add(this.id);
            dependenciesComputed = true;
            
            return dependencies;
    	}
    }
    
    public HashSet<Integer> getContributionDependencies()
    {
        if(!dependenciesComputed)
        {
            dependencies = new HashSet<>();
            for(int i=0;i<inputNodes.length;i++)
            {
                if(inputNodes[i].id>=0)
                {
                    dependencies.add(inputNodes[i].id);
                    HashSet<Integer> inputNodeDependencies = inputNodes[i].getContributionDependencies();
                    dependencies.addAll(inputNodeDependencies);
                }
            }
            dependencies.add(this.id);
            dependenciesComputed = true;
        }
        return dependencies;
    }
}
