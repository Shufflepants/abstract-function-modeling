package afm.functions;

public class TunedConstant extends Function
{

    public double value;
    
    
    
    public TunedConstant()
    {
        inputTypes = new String[0];
        outputTypes = new String[]{"number"};
        type = "tunedConstant";
        inputNodes = new Function[0];
        inputSelect = new int[0];
        output = new Function[1];
        
        value = Math.random()*2-1;        
    }
        
    public void compute()
    {
        
        Number result = new Number();
        result.value = cap(value);
        output[0] = result;
        computed = true;
    }
    
    public Function getOutput(int n)
    {
        compute();
        return output[n];
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
