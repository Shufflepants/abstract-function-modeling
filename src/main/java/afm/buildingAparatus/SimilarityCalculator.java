package afm.buildingAparatus;


import java.util.ArrayList;

import afm.tools.PropertyDoubleTensor;
import afm.functions.GenericFunction;

public class SimilarityCalculator
{
	private final int NUMPROP = 7;
	public double[] alphas;
	public double[] weights;
	
	double normalization;
	
	/**
	 * 
	 */
	public SimilarityCalculator()
	{
		alphas = new double[NUMPROP];
		
		for(int i=0;i<NUMPROP;i++)
		{
			alphas[i] = 1;
		}
		
		weights = new double[NUMPROP];
		
		for(int i=0;i<NUMPROP;i++)
		{
			weights[i] = 1;
		}
	}
	
	/**
	 * 
	 * @param alphas
	 */
	public SimilarityCalculator(double[] alphas)
	{
		this.alphas = alphas;
		
		weights = new double[alphas.length];
		
		for(int i=0;i<weights.length;i++)
		{
			weights[i] = 1;
		}
	}
	
	/**
	 * 
	 * @param alphas
	 * @param weights
	 */
	public SimilarityCalculator(double[] alphas, double[] weights)
	{
		this.alphas = alphas;
		this.weights = weights;
	}


    /**
     *
     * @param target
     * @param candidate
     * @return
     */
    public double[] getDifferenceVector(PropertyDoubleTensor target, PropertyDoubleTensor candidate)
    {
    	double[] diff = new double[NUMPROP];
            	
        diff[0] = this.rmsd(target.data,candidate.data);
        diff[1] = this.rmsd(target.grad.data, candidate.grad.data);
        diff[2] = Math.abs(target.relMinCount-candidate.relMinCount);
        diff[3] = Math.abs(target.relMaxCount-candidate.relMaxCount);
        diff[4] = Math.abs(target.saddleCount-candidate.saddleCount);
        diff[5] = Math.abs(target.surfaceArea-candidate.surfaceArea);
        
        
        double total = 0;
        for(int i=0;i<Math.min(target.centerOfMass.length, candidate.centerOfMass.length);i++)
        {
        	total = total + (target.centerOfMass[i]-candidate.centerOfMass[i])*(target.centerOfMass[i]-candidate.centerOfMass[i]);
        }
               
        diff[6] = Math.sqrt(total);
        
        for(int i=0;i<NUMPROP;i++)
        {
            //System.out.println(diff[i]);
        }
                
        return diff;
    }
    
    // FINISH THE REST OF THIS (maybe this is finished?? check)
    public double getSimilarityMeasure(PropertyDoubleTensor[] target, ADFDataEntry adf)
    {
        PropertyDoubleTensor trainingData = adf.getTrainingData()[1];
        
        double[] diffVector;
        
    	if(trainingData.dim>target[1].dim)
    	{
    	    
    	    ArrayList<Double>[] targetValues = target[1].data;
    	    ArrayList<Double>[] projectedTargetValues = new ArrayList[trainingData.data.length];
    	    
    	    int numberOfCopies=1;
    	    for(int i=0;i<trainingData.dim-target[1].dim;i++)
    	    {
    	        numberOfCopies = numberOfCopies * trainingData.size[target[1].size.length+i];
    	    }
    	    
    	    for(int i=0;i<numberOfCopies;i++)
    	    {
    	        for(int j=0;j<targetValues.length;j++)
    	        {
    	            projectedTargetValues[i*j] = targetValues[j];
    	        }
    	    }
    	    
    	    PropertyDoubleTensor inverseProjection = new PropertyDoubleTensor(trainingData.size,projectedTargetValues);
    	    
    	    inverseProjection.calcAttributes(adf.getTrainingData()[0]);
    	    
    	    // Get the difference vector between the target function and the candidate
            diffVector = getDifferenceVector(inverseProjection,trainingData);
            
    	    
    	}else if(trainingData.dim==target[1].dim)
    	{
    	    // Get the difference vector between the target function and the candidate
            diffVector = getDifferenceVector(target[1],trainingData);
            
                	    
    	}else
    	{
    	    ArrayList<Double>[] trainingValues = trainingData.data;
            ArrayList<Double>[] projectedTrainingValues = new ArrayList[target[1].data.length];
            
            int numberOfCopies=1;
            
            for(int i=0;i<target[1].dim-trainingData.dim;i++)
            {
                numberOfCopies = numberOfCopies * target[1].size[trainingData.size.length+i];
            }
            
            //numberOfCopies = (int)Math.pow(trainingData.dim+1, target[1].dim-trainingData.dim);
            
            for(int i=0;i<numberOfCopies;i++)
            {
                for(int j=0;j<trainingValues.length;j++)
                {
                    projectedTrainingValues[i*trainingValues.length+j] = trainingValues[j];
                }
            }
            
            PropertyDoubleTensor inverseProjection = new PropertyDoubleTensor(target[1].size,projectedTrainingValues);
            
            inverseProjection.calcAttributes(target[0]);
            
            // Get the difference vector between the target function and the candidate
            diffVector = getDifferenceVector(target[1],inverseProjection);
            
    	    
    	}
    	
    	// Combine difference vector values into similarity measure
        double sim = 0;
        
        for(int i=0;i<NUMPROP;i++)
        {
            sim = sim + cap(weights[i]/(Math.pow(diffVector[i], alphas[i])+1));
        }
        
        return sim;
    }
    
    
    
    public double getSimilarityMeasure(PropertyDoubleTensor target, GenericFunction candidate, PropertyDoubleTensor domain)
    {
        /*
    	// Create output data for the candidate 
    	
		ArrayList<Double>[] range = new ArrayList[domain.data.length];
    	
    	ArrayList<Double> temp;
    	
    	for(int i=0;i<candidate.inputNodes.length;i++)
    	{
    		candidate.inputNodes[i] = new Number();
    	}
    	for(int i=0;i<domain.data.length;i++)
    	{
    		candidate.resetComputed();
    		
    		for(int j=0;j<candidate.inputNodes.length;j++)
    		{
    			((Number)candidate.inputNodes[j]).value = domain.data[i].getVectorAtIndex(j);
    		}
    		
    		candidate.compute();
    		
    		temp = new ArrayList<Double>();
    		temp.add(((Number)candidate.getOutput(0)).value);
    		
    		    		
    		range[i] = temp;
    		
    	}
    	
    	// Create the sizes array for the candidate
    	
    	int[] size = new int[domain.size.length];
    	for(int i=0;i<size.length;i++)
    	{
    		size[i] = domain.size[i];
    	}
    	*/
    	// Create the PropertyDoubleTensor for the candidate
    	PropertyDoubleTensor candidateOutput = DataGenerator.generateRange(candidate, domain);
    	candidate.resultantData = candidateOutput;
    	candidateOutput.calcAttributes(domain);
    	
    	// Get the difference vector between the target function and the candidate
    	double[] diffVector = getDifferenceVector(target,candidateOutput);
    	
    	
    	// Combine difference vector values into similarity measure
    	double sim = 0;
    	
    	for(int i=0;i<NUMPROP;i++)
    	{
    	    
    		sim = sim + cap(weights[i]/(Math.pow(diffVector[i], alphas[i])+1));
    		
    		//System.out.println("Attribute"+i+": " + (weights[i]/(Math.pow(diffVector[i], alphas[i])+1)));
    	}
    	
    	// add in parsimony
    	int nodeCount = candidate.nodes.size() - candidate.inputNodes.length - 1;
    	sim = sim + weights[weights.length-1]/(Math.pow(Math.sqrt(nodeCount), alphas[alphas.length-1])+1);
    	
    	//System.out.println("Parsimony: " + nodeCount + "  " + weights[weights.length-1]/(Math.pow(Math.sqrt(nodeCount), alphas[alphas.length-1])+1));

    	return sim;
    }
   
    
    /**
     * 
     * @param values
     * @param target
     */
    /*
    public void getWeightedADFCDF(HashMap<String,ADFDataEntry> values, ADFDataEntry target)
    {
        
        double tempWeightTotal = 0;
        ADFDataEntry[] v =values.values().toArray(new ADFDataEntry[0]);
        
        for(ADFDataEntry adf : v)
        {
            double[] tData1 = stringToDouble(adf.trainingData[1]);
            double[] tData2 = stringToDouble(target.trainingData[1]);
            adf.diffVect = getDifferenceVector(tData1,tData2);
            adf.weight = getFitness(adf.diffVect);
            tempWeightTotal += adf.weight;
            adf.cdfWeight = tempWeightTotal;
            // add in code to assign 
        }
        
                
    }
    */
     
    /**
     * Computes Normalize Root Mean Squared Distance, normalizes based on a's range
     * @param a
     * @param b
     * @return
     */
    public double rmsd(ArrayList<Double>[] a, ArrayList<Double>[] b)
    {
        double total = 0;
        double min;
        double max;
        ArrayList<Double> a_;
        ArrayList<Double> b_;

        for(int i=0;i<a.length;i++)
        {
            a_ = a[i];
            b_ = b[i];
            
            for(int j=0;j<a_.size();j++)
            {
                
                
                total = total + (a_.get(j)-b_.get(j))*(a_.get(j)-b_.get(j));
            }
        }
        
        total = Math.sqrt(total/a.length);
        
        
        return total;
        
    }
    
    /**
     * Computes Normalize Root Mean Squared Distance, normalizes based on a's range
     * @param a
     * @param b
     * @return
     */
    public double nrmsd(ArrayList<Double>[] a, ArrayList<Double>[] b)
    {
        double total = 0;
        double min;
        double max;
        ArrayList<Double> a_;
        ArrayList<Double> b_;
        
        double normA = 0;
        
        for(int j=0;j<a[0].size();j++)
        {
            normA = normA + a[0].get(j)*a[0].get(j);
        }
        
        normA = Math.sqrt(normA);
        
        min = normA;
        max = normA;
        
        for(int i=0;i<a.length;i++)
        {
            normA = 0;
            
            a_ = a[i];
            b_ = b[i];
            
            for(int j=0;j<a_.size();j++)
            {
                normA = normA + a_.get(j)*a_.get(j);
                
                total = total + (a_.get(j)-b_.get(j))*(a_.get(j)-b_.get(j));
            }
            
            normA = Math.sqrt(normA);
            

            if(normA < min)
            {
                min = normA;
            }else if(normA > max)
            {
                max = normA;
            }
        }
        if(max-min!=0)
        {
            System.out.println(max-min);
            total = Math.sqrt(total/a.length)/(max-min);
        }else
        {
            System.out.println("weird");
            total = Math.sqrt(total/a.length);
        }
        
        System.out.println("NRMSD: " + total);
        
        return total;
    }
    
    
    
    public double[] stringToDouble(String[] arr)
    {
        double[] nums = new double[arr.length];
        for(int i=0;i<arr.length;i++)
        {
            nums[i] = Double.parseDouble(arr[i]);
        }
        return nums;
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
