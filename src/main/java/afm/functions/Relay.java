package afm.functions;

public class Relay extends Function
{
    
    
    public Relay()
    {
        inputTypes = new String[]{"number"}; //make more generic
        outputTypes = new String[]{"number"}; // make more generic
        type = "relay";
        inputNodes = new Function[1];
        inputSelect = new int[1];
        output = new Function[1];
        id = -1;
    }
    
    public void compute()
    {
        output[0] = inputNodes[0].getOutput(inputSelect[0]);
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
    
    

}
