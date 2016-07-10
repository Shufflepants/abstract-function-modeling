package afm.functions;

public class Variable extends Number
{
    public double value;
        
    
    public Variable()
    {
        value = 1;
        id=-1;
        type = "variable";
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
