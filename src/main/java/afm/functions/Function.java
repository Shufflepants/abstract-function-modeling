/**
 * 
 */
package afm.functions;

import java.util.HashMap;


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
    public HashMap<Integer,Boolean> dependencies;
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
    
    public HashMap<Integer,Boolean> getDependencies()
    {
    	
    	if(dependenciesComputed)
    	{
    		return dependencies;
    	}else
    	{
    		dependencies = new HashMap<Integer,Boolean>();
    		HashMap<Integer,Boolean> inputNodeDependencies;
            for(int i=0;i<inputNodes.length;i++)
            {
                if(!inputNodes[i].type.equals("relay"))
                {
                    dependencies.put(inputNodes[i].id,null);
                    inputNodeDependencies = inputNodes[i].getDependencies();
                    for(int j : inputNodeDependencies.keySet())
                    {
                        
                        dependencies.put(j,null);
                    }
                }
            	
            }
            
            
            dependencies.put(this.id, null);
            dependenciesComputed = true;
            
            return dependencies;
    	}
    	
    }
    
    public HashMap<Integer,Boolean> getContributionDependencies()
    {
        
        if(dependenciesComputed)
        {
            
        }else
        {
            dependencies = new HashMap<Integer,Boolean>();
            HashMap<Integer,Boolean> inputNodeDependencies;
            for(int i=0;i<inputNodes.length;i++)
            {
                if(inputNodes[i].id>=0)
                {
                    dependencies.put(inputNodes[i].id,null);
                    inputNodeDependencies = inputNodes[i].getContributionDependencies();
                    for(int j : inputNodeDependencies.keySet())
                    {
                        System.out.println("Included: " +j);
                        dependencies.put(j,null);
                    }
                }
                    
                
                
            }
            
            
            dependencies.put(this.id, null);
            dependenciesComputed = true;
            
            
        }
        return dependencies;
    }
}
