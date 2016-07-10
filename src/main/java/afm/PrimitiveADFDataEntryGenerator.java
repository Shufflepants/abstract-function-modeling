package afm;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import afm.buildingAparatus.ADFDataEntry;
import afm.buildingAparatus.DataGenerator;
import afm.buildingAparatus.FunctionPool;
import afm.functions.Function;
import afm.functions.GenericFunction;
import afm.functions.TunedConstant;
import afm.tools.PropertyTensor;

public class PrimitiveADFDataEntryGenerator
{

    public static void main(String[] args)
    {
        // TODO Auto-generated method stub

        double min = -5;
        double max = 5;
        int numSamples = 51;
        
        ADFDataEntry[] primitives = new ADFDataEntry[6];
        
        PropertyTensor[] trainingData;
        
        FunctionPool funPool = new FunctionPool("testADFs.txt");
        Function temp;
        
        PropertyTensor domain;
        PropertyTensor range;
        
        
        double[][] domain2D = new double[2][2];
        domain2D[0][0] = min;
        domain2D[0][1] = max;
        domain2D[1][0] = min;
        domain2D[1][1] = max;
        double[][] domain1D = new double[1][2];
        domain1D[0][0] = min;
        domain1D[0][1] = max;
        
        domain = DataGenerator.genDomain(domain2D, numSamples);
        
        String[] types = new String[] {"addition","multiplication","division","subtraction","modulus"};
        
        for(int i=0;i<types.length;i++)
        {
            GenericFunction genfun = new GenericFunction(2,1,funPool);
            
            temp = funPool.getNewFunctionWithID(types[i]);
            
            temp.inputNodes[0] = genfun.receivers[0];
            temp.inputNodes[1] = genfun.receivers[1];
            genfun.producers[0].inputNodes[0] = temp;
            genfun.nodes.add(temp);
            genfun.type = types[i];
            range = DataGenerator.genRange(genfun, domain);
            
            trainingData = new PropertyTensor[2];
            trainingData[0] = domain;
            trainingData[1] = range;
            
            primitives[i] = new ADFDataEntry(types[i],genfun.encode(),trainingData,new String[0],1.0);
        }
        
        
        
        
        // fix for tuned constant
        domain = DataGenerator.genDomain(domain1D, numSamples);
        
        GenericFunction tc = new GenericFunction(0,1,funPool);
        
        temp = funPool.getNewFunctionWithID("tunedConstant");
        ((TunedConstant)temp).value = 0;
        
        tc.producers[0].inputNodes[0] = temp;
        tc.nodes.add(temp);
        tc.type = "tunedConstant";
        range = DataGenerator.genRange(tc, domain);
        
        trainingData = new PropertyTensor[2];
        trainingData[0] = domain;
        trainingData[1] = range;
        
        primitives[5] = new ADFDataEntry("tunedConstant",tc.encode(),trainingData,new String[0],1.0);
        
        
        
        
    
        try  
        {
            BufferedWriter writer = new BufferedWriter(new PrintWriter(new FileWriter("defaultPrimitives.txt")));
            for(int i=0;i<primitives.length;i++)
            {
                String s = primitives[i].getFullEncoding();
                writer.write(s, 0, s.length());
            
            }
            writer.close();
            
        } catch (IOException x) {
            System.err.format("IOException: %s%n", x);
        }
            
        
    }
    
    

}
