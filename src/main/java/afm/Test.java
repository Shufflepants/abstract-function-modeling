package afm;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;

import afm.functions.*;
import afm.functions.Number;
import afm.buildingAparatus.*;
import afm.tools.*;

public class Test
{

    /**
     * @param args
     */
    public static void main(String[] args)
    {
        
        FunctionPool funpool = new FunctionPool("testADFs.txt");
        
        //((PropertyDoubleTensor) funpool.ADFs.getVectorAtIndex("blah2").getTrainingData()[1]).calcAttributes(((PropertyDoubleTensor) funpool.ADFs.getVectorAtIndex("blah2").getTrainingData()[0]));
        
        GenericFunction genfun = (GenericFunction)funpool.getNewFunction("blah3");
        
        
        
        Number four = new Number();
        Number five = new Number();
        four.id = -1;
        five.id = -2;
        four.value = 1;
        five.value = 1;
        genfun.inputNodes[0] = four;
        genfun.inputNodes[1] = five;
        
        genfun.compute();
        
        System.out.println(((Number)genfun.getOutput(0)).value);
        System.out.println(genfun.encode());
        System.out.println(genfun.graphVisEncode());
        
        Function temp;
        for(int i =0; i<10;i++)
        {
        	temp = funpool.getRandomFunction();
        	System.out.println(temp.type);
        }
        
        int n = 100;
        double[] func1 = new double[n];
        double[] func2 = new double[n];
        double[] func3 = new double[n];
        double[] ex = new double[n];
        double[] dft = new double[n*2];
        double[] dft2 = new double[n*2];
        double[] dft3 = new double[n*2];
        for(int i=0;i<n;i++)
        {
            ex[i] = ((double)i/10.0)-5;
            
            func1[i] = Math.pow(ex[i],2);
            dft[i*2] = func1[i];
            dft[i*2+1] = 0;
            
            func2[i] = Math.sin(ex[i]);
            dft2[i*2] = func2[i];
            dft2[i*2+1] = 0;
            
            func3[i] = 1;
            dft3[i*2] = func3[i];
            dft3[i*2+1] = 0;
        }
        
        
        try
        {
            BufferedWriter writer = new BufferedWriter(new FileWriter("test.txt"));
            
            for(int i=0;i<n;i++)
            {
               
                writer.write(ex[i]+","+dft[i]+","+dft2[i]+","+dft3[i]);
                writer.newLine();
            }
            writer.close();
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        System.out.println("hmmm");
        
        
        
        ArrayList<Double> t;

        
        PropertyDoubleTensor s = new PropertyDoubleTensor(new int[]{11,11});
        PropertyDoubleTensor p = new PropertyDoubleTensor(new int[]{11,11});
        
        
        
        for(int j= -5;j<6;j++)
    	{
        	for(int i= -5;i<6;i++)
        	{
        		t = new ArrayList<>();
        		t.add((double)j);
        		t.add((double)i);
        		s.set(new int[]{j+5,i+5},t);
        		t = new ArrayList<>();
        		t.add((double) (i*i));
        		p.set(new int[]{j+5,i+5},t);
        		
        		
        		 
        	}
    		
    	}
        
        System.out.println("CalcAttributes of target");/////////////
        p.calcAttributes(s);
        
        
        // Similarity Calculator Tests
        SimilarityCalculator sc = new SimilarityCalculator(new double[]{1,1,1,1,1,1,1},new double[]{1,1,1,1,1,1,1});
        
        //System.out.println("Sim: " + sc.getSimilarityMeasure(p, genfun, s));
        
        ADFDataEntry adf = funpool.hashedADFs.get("blah3");
        
        PropertyDoubleTensor[] td = new PropertyDoubleTensor[2];
        td[0] = s;
        td[1] = adf.getTrainingData()[1];
              
        System.out.println("CalcAttributes of td");
        
        td[1].calcAttributes(td[0]);///////////////////////
        
        adf.setTrainingData(td);
        
        PropertyDoubleTensor[] target = new PropertyDoubleTensor[2];
        target[0] = s;
        target[1] = p;
        
        
        System.out.println("Sim ADF: " + sc.getSimilarityMeasure(target, adf));
        
        System.out.println(adf.getFullEncoding());
        
        double[] alphas = new double[7];
        double[] weights = new double[7];
        for(int i=0;i<alphas.length;i++)
        {
            alphas[i] = 1;
            weights[i] = 1;
        }
        
        //////////////////////////////////////////////////////////////////////
        ADFDataEntry[] targets = new ADFDataEntry[1];
        targets[0] = new ADFDataEntry( "target", "blarg", target, new String[0], 0);
        
        Evolver e = new Evolver(funpool, 100, alphas, weights, targets, true, 10,10,1.5,2);
        
        for(int i=0;i<10;i++)
        {
            //e.tuneConstant(targets[0], genfun, (TunedConstant)genfun.nodes.getVectorAtIndex(3));
            //System.out.println("Tuned Constant: " + ((TunedConstant)genfun.nodes.getVectorAtIndex(3)).value);
            //System.out.println("Sim: " + targets[0].fitness);
        }
        
        /////////////////
        
        ((TunedConstant)genfun.nodes.get(3)).value = Math.random()*20-10; 
        
        GenericFunction genfunTarget = (GenericFunction)funpool.getNewFunction("blah3");
        ((TunedConstant)genfunTarget.nodes.get(3)).value = 3.141592;

        // noinspection unchecked
        ArrayList<Double>[] range = new ArrayList[target[0].data.length];
        
        ArrayList<Double> temp1;
        
        for(int i=0;i<genfunTarget.inputNodes.length;i++)
        {
            genfunTarget.inputNodes[i] = new Number();
        }
        for(int i=0;i<target[0].data.length;i++)
        {
            genfunTarget.resetComputed();
            
            for(int j=0;j<genfunTarget.inputNodes.length;j++)
            {
                ((Number)genfunTarget.inputNodes[j]).value = target[0].data[i].get(j);
            }
            
            genfunTarget.compute();
            
            temp1 = new ArrayList<>();
            temp1.add(((Number)genfunTarget.getOutput(0)).value);
            
                        
            range[i] = temp1;
            
        }
        
        // Create the sizes array for the candidate
        
        int[] size = new int[target[0].size.length];
        for(int i=0;i<size.length;i++)
        {
            size[i] = target[0].size[i];
        }
        
        // Create the PropertyDoubleTensor for the candidate
        PropertyDoubleTensor candidateOutput = new PropertyDoubleTensor(size,range);
        //genfunTarget.resultantData = candidateOutput;
        candidateOutput.calcAttributes(target[0]);
        
        //target[0] = s;
        target[1] = candidateOutput;
        
        targets[0] = new ADFDataEntry( "target", "blarg", target, new String[0], 0);
        
        for(int i=0;i<10;i++)
        {
            e.tuneConstant(targets[0], genfun, (TunedConstant)genfun.nodes.get(3));
            System.out.println("Tuned Constant: " + ((TunedConstant)genfun.nodes.get(3)).value);
            System.out.println("Sim: " + targets[0].fitness);
        }
        
        /*
        for(int i=0;i<s.data.length;i++)
        {
            ArrayList<Double> tempList = s.data[i];
            for(int j=0;j<tempList.size();j++)
            {
                System.out.print(tempList.getVectorAtIndex(j) + ",");
            }
            System.out.print(";");
        }
        System.out.println("\n");
        */
        
        funpool.newProblem(sc, target);
        
        
        double[][] testGen = new double[2][2];
        
        for(int i=0;i<testGen.length;i++)
        {
            testGen[i][0]= -5;
            testGen[i][1]=5;
        }
        
        PropertyDoubleTensor domain = DataGenerator.genDomain(testGen, 51);
        
        GenericFunction genFun1 = (GenericFunction)funpool.getNewFunction("blah1");
        GenericFunction genFun2 = (GenericFunction)funpool.getNewFunction("blah2");
        
        PropertyDoubleTensor rangeData1 = DataGenerator.genRange(genFun1, domain);
        PropertyDoubleTensor rangeData2 = DataGenerator.genRange(genFun2, domain);
        
        rangeData1.calcAttributes(domain);
        System.out.println("Sim: " + sc.getSimilarityMeasure(rangeData1, genFun2, domain));
        
        for(int i=0;i<30;i++)
        {
            funpool.getWeightedRandomFunction();
        }
        
        
        // test defaultPrimitives file
        
        FunctionPool primPool = new FunctionPool("defaultPrimitives.txt");
        PropertyDoubleTensor[] newTarget = new PropertyDoubleTensor[2];
        double[][] ranges = new double[2][2];
        for(int i=0;i<ranges.length;i++)
        {
            ranges[i] = new double[2];
            ranges[i][0] = -5;
            ranges[i][1] = 5;
        }
        newTarget[0] = DataGenerator.genDomain(ranges,51);
        newTarget[1] = rangeData1;
        
        primPool.newProblem(new SimilarityCalculator(), newTarget);
        
        // Test mutations:
        
        Evolver ev = new Evolver(primPool, 100, alphas, weights, targets, true, 10,10,1.5,2);
        
        GenericFunction evGF = new GenericFunction(2,1,primPool);
        
        for(int i=0;i<5;i++)
        {
            ev.insertion(evGF);
        }
        
        ev.reconnect(evGF, evGF.producers[0], 0);
        
        for(int i=0;i<30;i++)
        {
            ev.mutate(evGF, targets[0]);
        }
        
        Number[] variables = new Number[2];
        for(int i=0;i<variables.length;i++)
        {
            variables[i] = new Number();
            variables[i].value = i+1;
            evGF.inputNodes[i] = variables[i];
        }
        evGF.resetComputed();
        Number result = (Number)evGF.getOutput(0);
        System.out.println("Result: " + result.value);  
        System.out.println(evGF.graphVisEncode());
        ADFDataEntry adfEntry = ev.convertToADF(evGF, newTarget, 0.5);
        
        System.out.println(adfEntry.getFullEncoding());
        System.out.println(evGF.graphVisEncode());
        
        ev.generatePopulation(adfEntry, 10);
    }

}
