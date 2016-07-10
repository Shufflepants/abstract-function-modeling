package afm.buildingAparatus;

import java.util.ArrayList;

import afm.tools.PropertyTensor;

public class ADFDataEntry
{
    String name;
    String encoding;
    PropertyTensor[] trainingData;
    String[] subFunctions;
    double cdfWeight;
    double weight;
    double[] diffVect;
    public double originalFitness;
    public double fitness;
    
    
    /**
     * For the most part, do not use this constructor
     */
    public ADFDataEntry()
    {
    	name = "";
    	encoding = "";
    	trainingData = null;
    	subFunctions = null;
    }
    
    /**
     * 
     * @param name
     * @param encoding
     * @param trainingData
     * @param subFunctions
     * @param originalFitness
     */
    public ADFDataEntry(String name, String encoding, PropertyTensor[] trainingData, String[] subFunctions, double originalFitness)
    {
        this.name = name;
        this.encoding = encoding;
        this.trainingData = trainingData;
        this.subFunctions = subFunctions;
        
        System.out.println("ADF " + name);
        
        trainingData[1].calcAttributes(trainingData[0]);
        
        this.originalFitness = originalFitness;
        
    }
    
    public PropertyTensor[] getTrainingData()
    {
    	return trainingData;
    }
    
    public void setTrainingData(PropertyTensor[] td)
    {
        trainingData = td;
    }
    
    public String getFullEncoding()
    {
        String fullEncoding ="";
        
        // Add in structure encoding
        fullEncoding = fullEncoding + encoding + "\n";
        
        
        // Add in training data dimensions
        for(int i=0;i<trainingData[0].size.length;i++)
        {
            fullEncoding = fullEncoding + trainingData[0].size[i] + ",";
        }
        fullEncoding = fullEncoding + "\n";
        
        // Add in training data domain
        for(int i=0;i<trainingData[0].data.length;i++)
        {
            ArrayList<Double> point = trainingData[0].data[i];
            
            for(int j=0;j<point.size();j++)
            {
                fullEncoding = fullEncoding + point.get(j) + ",";
            }
            
            fullEncoding = fullEncoding + ";";
            
        }
        fullEncoding = fullEncoding + "\n";
        
        // Add in training data range
        for(int i=0;i<trainingData[1].data.length;i++)
        {
            ArrayList<Double> point = trainingData[1].data[i];
            
            for(int j=0;j<point.size();j++)
            {
                fullEncoding = fullEncoding + point.get(j) + ",";
            }
            
            fullEncoding = fullEncoding + ";";
            
        }
        fullEncoding = fullEncoding + "\n";
        
        
        // Add in internal subfunctions
        for(int i=0;i<subFunctions.length;i++)
        {
            fullEncoding = fullEncoding + subFunctions[i] + ";";
        }
        fullEncoding = fullEncoding + "\n";
        
        //Add in original fitness
        fullEncoding = fullEncoding + originalFitness + "\n\n";
        
        
        
        
        return fullEncoding;
    }
    
}
