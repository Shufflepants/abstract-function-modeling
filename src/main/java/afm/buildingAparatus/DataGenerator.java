package afm.buildingAparatus;

import java.util.ArrayList;

import afm.functions.GenericFunction;
import afm.tools.PropertyDoubleTensor;
import afm.functions.Number;

public class DataGenerator
{
    /**
     * 
     * @param ranges - an n by 2 array that contains the min and max points for each of the n dimensions on which a sampling domain is desired
     * @param numDimSamples - the number of samples along an edge including the end points of the n-cube
     * @return
     */
    public static PropertyDoubleTensor genDomain(double[][] ranges, int numDimSamples)
    {
        int[] size = new int[ranges.length];
        int[] index = new int[ranges.length];
        
        for(int i=0;i<size.length;i++)
        {
            size[i] = numDimSamples;
            index[i] = 0;
        }
        
        /*
        if(size.length==0)
        {
            size = new int[];
        }
        */
        
        PropertyDoubleTensor domain = new PropertyDoubleTensor(size);
        
        for(int i=0;i<Math.pow(numDimSamples, size.length);i++)
        {
            
            ArrayList<Double> point = new ArrayList<Double>();
            
            for(int j=0;j<size.length;j++)
            {
                point.add(index[j]*(ranges[j][1]-ranges[j][0])/(numDimSamples-1)+ranges[j][0]);
                //System.out.print(point.getVectorAtIndex(j) + ",");
            }
            //System.out.println("\n");
            
            domain.data[i] = point;
            
            index[0]++;
            
            for(int k=0;k<size.length-1;k++)
            {
                if(index[k]>=size[k])
                {
                    index[k] = 0;
                    index[k+1]++;
                }else
                {
                    break;
                }
            }
        }
        
        
        return domain;
    }
    
    
    /**
     * 
     * @param genFun - a Generic function that you want the output from
     * @param domain - the points over which you want the generic function evaluated
     * @return
     */
    public static PropertyDoubleTensor genRange(GenericFunction genFun, PropertyDoubleTensor domain)
    {
        ArrayList<Double>[] rangeData = new ArrayList[domain.data.length];
        
        int[] size = new int[domain.size.length];
        
        for(int i=0;i<size.length;i++)
        {
            size[i] = domain.size[i];
        }
        
        if(genFun.inputNodes.length==0)
        {
            for(int i=0;i<domain.data.length;i++)
            {
                rangeData[i] = new ArrayList<Double>();
                rangeData[i].add(((Number)genFun.getOutput(0)).value);
                
            }
            
        }else if(genFun.inputNodes.length!=domain.size.length)
        {
            System.out.println("Dimension of generic function = " + genFun.inputNodes.length 
                    + " does not match dimension of domain data = " + domain.size.length 
                    + " in DataGenerator.genRange.");
            System.exit(1);
            return null;
        }else
        {
            
            
            afm.functions.Number[] input = new Number[domain.data[0].size()];
            
            for(int i=0;i<input.length;i++)
            {
                genFun.inputNodes[i] = new Number();
            }
            
            for(int i=0;i<domain.data.length;i++)
            {
                ArrayList<Double> point = domain.data[i];
                rangeData[i] = new ArrayList<Double>();
                
                genFun.resetComputed();
                
                //System.out.print("f( ");
                for(int j=0;j<point.size();j++)
                {
                    ((Number)genFun.inputNodes[j]).value = point.get(j);
                    
                   // System.out.print(  ((Number)genFun.inputNodes[j]).value + ", ");
                }
                
                genFun.compute();
                
                rangeData[i].add(((Number)genFun.getOutput(0)).value);
                
                //System.out.print(") = " + rangeData[i].getVectorAtIndex(0) + "\n");
                
            }
        }
        
        PropertyDoubleTensor range = new PropertyDoubleTensor(size,rangeData);
        
        return range;
    }
}
