package afm.functions;


public class Division extends Function
{
    
    
    
    
    public Division()
    {
        inputTypes = new String[]{"number","number"};
        outputTypes = new String[]{"number"};
        type = "division";
        inputNodes = new Function[2];
        inputSelect = new int[2];
        output = new Function[1];
    }
    
    public void compute()
    {
        double quotient;
        if(((Number)inputNodes[1].getOutput(inputSelect[1])).value==0)
        {
            quotient = Math.signum(((Number)inputNodes[0].getOutput(inputSelect[0])).value);
        }else
        {
            quotient = ((Number)inputNodes[0].getOutput(inputSelect[0])).value 
                     / ((Number)inputNodes[1].getOutput(inputSelect[1])).value;
        }
        
        Number result = new Number();
        result.value = cap(quotient);
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