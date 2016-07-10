package afm.functions;

public class Number extends Function
{
    public double value;
    
    
    
    public Number()
    {
        inputNodes = new Function[0];
        value = 1;
        id=-1;
        type = "number";
    }
        
    public void compute()
    {
        computed = true;
    }
    
    public Function getOutput(int n)
    {
        compute();
        return this;
    }
}
