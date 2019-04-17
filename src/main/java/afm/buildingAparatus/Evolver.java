package afm.buildingAparatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

import afm.functions.Function;
import afm.functions.GenericFunction;
import afm.functions.TunedConstant;
import afm.tools.PropertyDoubleTensor;
import afm.functions.Number;

public class Evolver
{
    FunctionPool funPool;
    int populationSize;
    double[] alphas;
    double[] weights;
    ADFDataEntry[] targets;
    ADFDataEntry[] solutions;
    boolean similarityMeasure;
    SimilarityCalculator simCalc;
    int constantTuneDepth;
    int constantTuneSize;
    double tunerBase, tunerExponent;
    int nextGenFunID;
    ArrayList<GenericFunction> population;
    TreeMap<Double,GenericFunction> sortedPopulation;
    
        
    public Evolver(FunctionPool funPool, int populationSize, double[] alphas, double[] weights, ADFDataEntry[] targets, 
                    boolean similarityMeasure, int constantTuneDepth, int constantTuneSize, double tunerBase, double tunerExponent)
    {
        this.funPool = funPool;
        this.populationSize = populationSize;
        this.alphas = alphas;
        this.weights = weights;
        this.targets = targets;
        this.similarityMeasure = similarityMeasure;   
        this.constantTuneDepth = constantTuneDepth;
        this.constantTuneSize = constantTuneSize;
        this.tunerBase = tunerBase;
        this.tunerExponent = tunerExponent;
        solutions = new ADFDataEntry[targets.length];
        nextGenFunID = 0;
        
        
        simCalc = new SimilarityCalculator(alphas,weights);
    }
    
    public void findSolution()
    {
        
    }
    
    
    public void produceNextGeneration(ADFDataEntry target, double mutationRate)
    {
        
        Number[] targetValues = new Number[target.trainingData[0].dim];
        
        for(Number numFun : targetValues)
        {
            numFun = new Number();
        }
        
        
        for(GenericFunction genFun : population)
        {
            
            
            
        }
    }
    
    
    public void generatePopulation(ADFDataEntry target, int initialInsertions)
    {
        population = new ArrayList<GenericFunction>();
        
        for(int i=0;i<populationSize;i++)
        {
            GenericFunction newFun = new GenericFunction(target.trainingData[0].dim,target.trainingData[1].dim,funPool);
            newFun.type = "genFun_"+nextGenFunID;
            nextGenFunID++;
            population.add(newFun);
            
            for(int j=0;j<initialInsertions;j++)
            {
                insertion(population.get(i));
            }
            reconnect(population.get(i), population.get(i).producers[0],0);
        }
        
        
    }
    
    
    /**
     * 
     * @param genFun
     * @param trainingData
     * @param fitness
     * @return
     */
    public ADFDataEntry convertToADF(GenericFunction genFun, PropertyDoubleTensor[] trainingData, double fitness)
    {
        genFun = stripNonContributors(genFun);
        convertTunedConstants(genFun);  
        
        String name = genFun.type;
        String encoding = genFun.encode(); 
        PropertyDoubleTensor[] trainedData = trainingData;
        
        
        HashMap<Integer,Boolean> dependencies = genFun.producers[0].getDependencies();
        dependencies.remove(genFun.producers[0].id);
        
        String[] subFunctions = (String[]) genFun.types.keySet().toArray(new String[0]);
        
             
        
        return new ADFDataEntry(name,encoding,trainedData,subFunctions,fitness);
    }
    
    /**
     * 
     * @param genFun
     * @return
     */
    public GenericFunction convertTunedConstants(GenericFunction genFun)
    {
        Function fun;
        for(int j=0;j<genFun.nodes.size();)
        {
            fun = genFun.nodes.get(j);
            
            if(fun.type.equals("tunedConstant"))
            {
                Function relay = funPool.getNewFunction("relay");
                relay.id = fun.id;
                Number n = new Number();
                relay.inputNodes[0] = n;
                
                for(Function f : genFun.hookyNodes)
                {
                    for(int i=0;i<f.inputNodes.length;i++)
                    {
                        if(f.inputNodes[i].id==fun.id)
                        {
                            f.inputNodes[i] = relay;
                        }
                    }
                }
                
                Function[] newReceivers = new Function[genFun.receivers.length+1];
                for(int i=0;i<genFun.receivers.length;i++)
                {
                    newReceivers[i] = genFun.receivers[i];
                }
                newReceivers[newReceivers.length-1] = relay;
                
                genFun.receivers = newReceivers;
                
                String[] newInputTypes = new String[genFun.inputTypes.length+1];
                for(int i=0;i<genFun.inputTypes.length;i++)
                {
                    newInputTypes[i] = genFun.inputTypes[i];
                }
                newInputTypes[newInputTypes.length-1] = "number";
                
                genFun.inputTypes = newInputTypes;
                
                genFun.nodes.remove(fun);
                genFun.hookyNodes.remove(fun);
                genFun.nodes.add(relay);
                
                
            }else
            {
                j++;
            }
            
            
            
        }
        
        
        return genFun;
    }
    
    /**
     * 
     * @param genFun
     * @return
     */
    public GenericFunction stripNonContributors(GenericFunction genFun)
    {
        for(Function fun : genFun.nodes)
        {
            fun.resetDependencies();
        }
        HashMap<Integer,Boolean> activeNodes = genFun.producers[0].getContributionDependencies();
        
        for(int i=0;i<genFun.nodes.size();)
        {
            Function temp = genFun.nodes.get(i);
            System.out.println(temp.type + temp.id);
            if(!activeNodes.containsKey(temp.id))
            {
                System.out.println(genFun.nodes.remove(temp));
                if(temp.type.equals("relay"))
                {
                    Function[] newReceivers = new Function[genFun.receivers.length-1];
                    String[] newInputTypes = new String[genFun.inputTypes.length-1];
                    int index = 0;
                    for(int j=0;j<genFun.receivers.length;j++)
                    {
                        if(genFun.receivers[j]==temp)
                        {
                            
                        }else
                        {
                            newReceivers[index] = genFun.receivers[j];
                            newInputTypes[index] = genFun.inputTypes[j];
                            index++;
                        }
                    }
                    genFun.receivers = newReceivers;
                    genFun.inputTypes = newInputTypes;
                }
            }else
            {
                i++;
            }
        }
        
        
        return genFun;
    }
    
    
    
    
    
    
    
    
    
    
    public void tuneConstant(ADFDataEntry target, GenericFunction genfun, TunedConstant constant)
    {
        
        double tempFitness;
        double tempBestFitness = simCalc.getSimilarityMeasure(target.trainingData[1], genfun, target.trainingData[0]);
                
        double startingValue = constant.value;
        double currentBestConstant = constant.value;
        double tempCurrentBestConstant = constant.value;
        
        System.out.println("Starting Constant and Fitnes: " + constant.value + " " + tempBestFitness);
        
        for(int i=0;i<constantTuneDepth;i++)
        {
            currentBestConstant = tempCurrentBestConstant;
            
            for(int j=0;j<constantTuneSize;j++)
            {
                            
                constant.value =(Math.random()*10-5)*(startingValue/Math.pow(tunerBase, tunerExponent*i))+currentBestConstant;
                
                System.out.println("Tested Constant: " + constant.value);
                
                tempFitness = simCalc.getSimilarityMeasure(target.trainingData[1], genfun, target.trainingData[0]);
                
                if(tempFitness > tempBestFitness)
                {
                    tempBestFitness = tempFitness;
                    tempCurrentBestConstant = constant.value;
                }
                
            }
            
            
        }
        
        constant.value = currentBestConstant;
        target.fitness = tempBestFitness;
    }
    
    
    /**
     * 
     * @param genfun
     * @param target
     * @param r
     * @param k
     */
    public void mutate(GenericFunction genfun, ADFDataEntry target)
    {
        int numNodes = genfun.hookyNodes.size();
        
        int choice = (int) (Math.random()*(numNodes+2));
        
        switch(choice)
        {
            case 0:
                insertion(genfun);
                break;
            case 1:
                deletion(genfun);
                break;
            default:
                reconnection(genfun, target);
                break;
                    
         
        }
    }
    
    /**
     * maybe done?
     * @param genfun
     */
    public void insertion(GenericFunction genfun)
    {
        Function newFun;
        
        if(similarityMeasure)
        {
            newFun = funPool.getWeightedRandomFunction();
        }else
        {
            newFun = funPool.getRandomFunction();
        }
        
        int numLoopyNodes = genfun.loopyNodes.size();
        
        
        for(int i=0;i<newFun.inputNodes.length;i++)
        {
            int loopyChoice = (int) (Math.random()*numLoopyNodes);
            
            Function chosenLoopy = genfun.loopyNodes.get(loopyChoice);
            
            newFun.inputNodes[i] = chosenLoopy;
            
        }
        
        genfun.loopyNodes.add(newFun);
        genfun.hookyNodes.add(newFun);
        genfun.nodes.add(newFun);
        genfun.nonRelays.add(newFun);
    }
    
    /**
     * Maybe done?
     * @param genfun
     */
    public void deletion(GenericFunction genfun)
    {
        
        if(genfun.nonRelays.size()>0)
        {
            int deleteChoice = (int) (Math.random()*genfun.nonRelays.size());
            
            Function chosenNode = genfun.nonRelays.get(deleteChoice);
            
            
            genfun.nonRelays.remove(chosenNode);
            genfun.loopyNodes.remove(chosenNode);
            genfun.hookyNodes.remove(chosenNode);
            genfun.nodes.remove(chosenNode);
                    
            for(Function f : genfun.hookyNodes)
            {
                for(int i=0;i<f.inputNodes.length;i++)
                {
                    if(f.inputNodes[i].id==chosenNode.id)
                    {
                        reconnect(genfun,f,i);
                    }
                }
            }
            
        }
        
        
    }
    
    
    
    /**
     * 
     * @param genfun
     */
    public void reconnection(GenericFunction genfun, ADFDataEntry target)
    {
        int funChoice = (int) (Math.random()*genfun.hookyNodes.size());
        
        Function chosenNode = genfun.hookyNodes.get(funChoice);
        
        if(!chosenNode.type.equals("tunedConstant"))
        {
            int inputChoice = (int) (Math.random()*chosenNode.inputNodes.length);
            
            reconnect(genfun,chosenNode,inputChoice);
        }else
        {
            tuneConstant(target, genfun, (TunedConstant)chosenNode);
        }
        
        
        
    }
    
    /**
     * 
     * @param genfun
     * @param fun
     * @param input
     */
    
    public void reconnect(GenericFunction genfun, Function fun, int input)
    {
        ArrayList<Function> potentialReconnectionSites = new ArrayList<Function>();
        
        for(Function f : genfun.hookyNodes)
        {
            f.resetDependencies();
        }
        
        for(Function temp : genfun.nonRelays)
        {
            temp.getDependencies();
            if(!temp.dependencies.containsKey(fun.id))
            {
                potentialReconnectionSites.add(temp);
            }
        }
        
        for(Function temp : genfun.receivers)
        {
            potentialReconnectionSites.add(temp);
        }
        
        int reconnectionChoice = (int) (Math.random()*potentialReconnectionSites.size());
        
        fun.inputNodes[input] = potentialReconnectionSites.get(reconnectionChoice);
        
    }
    
}
