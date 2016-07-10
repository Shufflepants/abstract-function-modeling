package afm.functions;


public class Multiplication extends Function
{
    
    
    
    
    public Multiplication()
    {
        inputTypes = new String[]{"number","number"};
        outputTypes = new String[]{"number"};
        type = "multiplication";
        inputNodes = new Function[2];
        inputSelect = new int[2];
        output = new Function[1];
    }
    
    public void compute()
    {
        
        double product = ((Number)inputNodes[0].getOutput(inputSelect[0])).value 
                       * ((Number)inputNodes[1].getOutput(inputSelect[1])).value;
        Number result = new Number();
        result.value = cap(product);
        output[0] = result;
        computed = true;
    }
    
    public Function getOutput(int n)
    {
        if(!computed)
        {
            compute();
        }
        
        if(n<output.length&&n>=0)
        {
            return output[n];
        }else
        {
            return null;
        }
        
    }
    
    private double cap(double d)
    {
        if(((Double)d).isInfinite())
        {
            return Math.signum(d)*Double.MAX_VALUE;
        }else
        {
            return d;
        }
        
    }
}