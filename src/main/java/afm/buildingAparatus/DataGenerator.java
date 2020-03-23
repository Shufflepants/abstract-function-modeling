package afm.buildingAparatus;

import java.util.ArrayList;

import afm.functions.Function;
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

        
        PropertyDoubleTensor domain = new PropertyDoubleTensor(size);
        
        for(int i=0;i<Math.pow(numDimSamples, size.length);i++)
        {
            
            ArrayList<Double> point = new ArrayList<>();
            
            for(int j=0;j<size.length;j++)
            {
                point.add(index[j]*(ranges[j][1]-ranges[j][0])/(numDimSamples-1)+ranges[j][0]);
            }
            
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
    public static PropertyDoubleTensor generateRange(GenericFunction genFun, PropertyDoubleTensor domain)
    {
        ArrayList<Double>[] rangeData = new ArrayList[domain.data.length];
        
        int[] size = new int[domain.size.length];

        System.arraycopy(domain.size, 0, size, 0, size.length);
        
        if(genFun.inputNodes.length==0)
        {
            for(int i=0;i<domain.data.length;i++)
            {
                rangeData[i] = new ArrayList<>();
                rangeData[i].add(((Number)genFun.getOutput(0)).value);
                
            }
            
        }else if(genFun.inputNodes.length!=domain.size.length)
        {
            throw new IllegalArgumentException("Dimension of generic function = " + genFun.inputNodes.length
                    + " does not match dimension of domain data = " + domain.size.length
                    + " in DataGenerator.generateRange.");
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
                rangeData[i] = new ArrayList<>();
                
                genFun.resetComputed();
                for(int j=0;j<point.size();j++)
                {
                    ((Number)genFun.inputNodes[j]).value = point.get(j);
                }
                
                genFun.compute();
                
                rangeData[i].add(((Number)genFun.getOutput(0)).value);
                
            }
        }
        
        PropertyDoubleTensor range = new PropertyDoubleTensor(size,rangeData);
        
        return range;
    }

    public static double getValue3d(Function function, double x, double y) {

        if (function.inputNodes.length != 2) {
            throw new IllegalArgumentException("getValue3d can only be called on a function with 2 independent variables. "
                    + "Function passed in has " + function.inputNodes.length + " input nodes.");
        }
        if (function.output.length != 1) {
            throw new IllegalArgumentException("getValue3d can only be called on a function with 1 dependent variable. "
                    + "Function passed in has " + function.output.length + " outputs.");
        }

        double[] input = new double[]{x,y};

        return getValue(function, input);
    }

    /**
     * Takes a multivariate input (x_1, x_2, ...) and returns the output of the provided function. If the number of
     * inputs on the function is greater than the number of inputs provided, the function will be evaluated with the
     * later inputs set to 0. If the inputs provided is greater than the number of input variables on the function, only
     * the first n inputs will be used where n is the number of inputs the function takes.
     * For function f(x_1,x_2,...,x_n) and input (y_1,y_2,...,y_m)
     * if n>m, returns f(y_1,y_2,...,y_m,0,...)
     * if m>=n, returns f(y_1,y_2,...,y_n)
     * @param function function to be evaluated
     * @param input an array containing the input values to evaluate the multi-dimensional function at
     * @return the output of the function evaluated at the input
     */
    public static double getValue(Function function, double[] input) {
        for (int i=0; i<function.inputNodes.length; i++) {
            if (i< input.length) {
                function.inputNodes[i] = new Number(input[i]);
            } else {
                function.inputNodes[i] = new Number(0);
            }
        }
        function.resetComputed();
        function.compute();
        return ((Number)function.getOutput(0)).value;
    }
}
