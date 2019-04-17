package afm.buildingAparatus;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Scanner;
import java.util.SortedMap;
import java.util.TreeMap;

import afm.functions.*;
import afm.tools.PropertyDoubleTensor;


public class FunctionPool
{
    
    public HashMap<String,ADFDataEntry> hashedADFs; 
    public TreeMap<Double,ADFDataEntry> treeADFs;
    public ArrayList<String> functionNames = new ArrayList<String>();
    final int NUMPRIMFUN = 6;
    int numFun;
    double CDFSum;
    int nextID;
    
    /**
     * File data format :
     * <encoded function>
     * <independent values> //(values comma delimited)
     * <dependant values>   //(values comma delimited)
     * <sub-functions>      //(values comma delimited)
     * 
     * @param ADFFile
     */
    public FunctionPool(String ADFFile)
    {
        hashedADFs = new HashMap<>();
        try
        {
            Scanner scan = new Scanner(new FileReader(ADFFile));
            
            while(scan.hasNext())
            {
            	// Get the structure encoding
                String encoding = scan.nextLine();
                
                int endOfName = encoding.indexOf(' ',2);
                
                // Extract the function name
                String name = encoding.substring(2, endOfName);
                
                functionNames.add(name);
                
                int[] index;
                String[] indexStrings;
                String[] independentStrings;
                String[] dependentStrings;
                ArrayList<Double>[] independent;
                ArrayList<Double>[] dependent;
                ArrayList<Double> coordinates;
                
                // getVectorAtIndex the size of the training data
                indexStrings = scan.nextLine().split(",");
                index = new int[indexStrings.length];
                
                for(int i=0;i<index.length;i++)
                {
                	index[i] = Integer.parseInt(indexStrings[i]);
                }
                
                // read in and convert the independent coordinates of the training data
                independentStrings = scan.nextLine().split(";");
                independent = new ArrayList[independentStrings.length];
                
                
                for(int i=0;i<independent.length;i++)
                {
                	String[] coordinateStrings = independentStrings[i].split(",");
                	coordinates = new ArrayList<Double>();
                	
                	for(int j=0;j<coordinateStrings.length;j++)
                	{
                		coordinates.add(Double.parseDouble(coordinateStrings[j]));
                	}
                	
                	independent[i] = coordinates;
                }
                
                // read in and convert the dependent coordinates of the training data
                dependentStrings = scan.nextLine().split(";");
                dependent = new ArrayList[dependentStrings.length];
                
                
                for(int i=0;i<dependent.length;i++)
                {
                	String[] coordinateStrings = dependentStrings[i].split(",");
                	coordinates = new ArrayList<Double>();
                	
                	for(int j=0;j<coordinateStrings.length;j++)
                	{
                		coordinates.add(Double.parseDouble(coordinateStrings[j]));
                	}
                	
                	dependent[i] = coordinates;
                }
                
                // Merge independent and dependent data into trainingData array
                PropertyDoubleTensor[] trainingData = new PropertyDoubleTensor[2];
                trainingData[0] = new PropertyDoubleTensor(index,independent);
                trainingData[1] = new PropertyDoubleTensor(index,dependent);
                
                
                                
                String[] subFun = scan.nextLine().split(";");
                
                if(subFun.length==1)
                {
                    subFun[0] = subFun[0].trim();
                    if(subFun[0].compareTo("")==0)
                    {
                        subFun = new String[0];
                    }
                        
                }
                    
                
                double fitness = Double.parseDouble(scan.nextLine());
                
                ADFDataEntry temp = new ADFDataEntry(name,encoding,trainingData,subFun,fitness);
                
                hashedADFs.put(name, temp);
                
                scan.nextLine();
            }
            
            scan.close();
        
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        
        numFun = NUMPRIMFUN + hashedADFs.size();
        
        nextID = 0;
    }
    
    public Function getRandomFunction()
    {
        int s = (int)Math.floor(Math.random()*numFun);
        Function fun = null;
        switch(s)
        {
            case 0: fun = new Addition();
                    break;
            case 1: fun = new Multiplication();
                    break;
            case 2: fun = new Division();
                    break;
            case 3: fun = new Subtraction();
                    break;
            case 4: fun = new Modulus();
                    break;
            case 5: fun = new TunedConstant();
                    break;
            default: 
            	s = s - NUMPRIMFUN;
            	String tempName = functionNames.get(s);
            	String tempEncoding = hashedADFs.get(tempName).encoding;
            	fun = new GenericFunction(tempEncoding,this);
            	break;  	
        }
        
        fun.id = nextID;
        nextID++;
        
        return fun;
    }
    
    public Function getNewFunction(String name)
    {
        Function fun;
        switch(name)
        {
            case "addition": fun = new Addition();
                break;
            case "subtraction": fun = new Subtraction();
                break;
            case "division": fun = new Division();
                break;
            case "multiplication": fun = new Multiplication();
                break;
            case "modulus": fun = new Modulus();
                break;
            case "relay": fun = new Relay();
                fun.id = nextID;
                nextID++;
                break;
            case "tunedConstant": fun = new TunedConstant();
                break;
            default:
            	fun = new GenericFunction(hashedADFs.get(name).encoding,this);
                break;
        }
        
    	return fun;
    }
    
    public Function getNewFunctionWithID(String name)
    {
        Function fun;
        switch(name)
        {
            case "addition": fun = new Addition();
                break;
            case "subtraction": fun = new Subtraction();
                break;
            case "division": fun = new Division();
                break;
            case "multiplication": fun = new Multiplication();
                break;
            case "modulus": fun = new Modulus();
                break;
            case "relay": fun = new Relay();
                break;
            case "tunedConstant": fun = new TunedConstant();
                break;
            default:
                fun = new GenericFunction(hashedADFs.get(name).encoding,this);
                break;
        }
        
        fun.id = nextID;
        nextID++;
        return fun;
    }
    
    public Function getWeightedRandomFunction()
    {
    	double rand = Math.random()*CDFSum;
    	SortedMap<Double,ADFDataEntry> subMap = treeADFs.tailMap(rand);
    	ADFDataEntry tempADF = subMap.get(subMap.firstKey());
    	//System.out.println("Rand: " + rand + " ADF Chosen: " + tempADF.name + " Fitness: " + tempADF.fitness);
        
    	Function selectedFun = getNewFunction(tempADF.name);
    	
    	selectedFun.id = nextID;
    	nextID++;
    	
    	return selectedFun;
    }
    
    
    public void newProblem(SimilarityCalculator simCalc, PropertyDoubleTensor[] target)
    {
        treeADFs = new TreeMap<Double,ADFDataEntry>();
        
        
        
        Collection<ADFDataEntry> adfsCollection = hashedADFs.values();
        
        // Reset score
        for(ADFDataEntry adf : adfsCollection)
        {
            adf.fitness = 0;
        }
        
        // Calculate Base score
        for(ADFDataEntry adf : adfsCollection)
        {
            double score = simCalc.getSimilarityMeasure(target,adf);
            
            adf.fitness = score;
            System.out.println("newProblem Fitness: " + adf.fitness);
        }
        
        // Give attribution scores
        for(ADFDataEntry adf : adfsCollection)
        {
            
            for(String subFunction : adf.subFunctions)
            {
                ADFDataEntry temp = hashedADFs.get(subFunction);
                
                if(adf.fitness>temp.fitness)
                {
                    temp.fitness = adf.fitness;
                    System.out.println("Attribution score: " + temp.fitness);
                }
            }
            
        }
        
        // Assign cumulative scores and insert into treeMap
        double total=0;
        for(ADFDataEntry adf : adfsCollection)
        {
            total = total + adf.fitness;
            adf.fitness = total;
            treeADFs.put(total, adf);
            System.out.println("CDF Partial Sum: " + total);
        }

        CDFSum = total;
        nextID = 0;
        
    }
    
    
    public void addFunction(ADFDataEntry newADF)
    {
        hashedADFs.put(newADF.name,newADF);
        functionNames.add(newADF.name);
        numFun++;
    }
    
    
}
