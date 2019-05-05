package afm.buildingAparatus;

import afm.functions.GenericFunction;
import afm.functions.Number;
import afm.functions.TunedConstant;
import afm.tools.PropertyDoubleTensor;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

public class EvolverTest {

    public Evolver evolver;
    public ADFDataEntry adfEntry;
    public ADFDataEntry[] targets;

    @Before
    public void setUp() {
        FunctionPool funpool = new FunctionPool("testADFs.txt");

        ArrayList<Double> t;

        PropertyDoubleTensor s = new PropertyDoubleTensor(new int[]{11,11});
        PropertyDoubleTensor p = new PropertyDoubleTensor(new int[]{11,11});

        for (int j = -5; j < 6; j++) {
            for (int i = -5; i < 6; i++) {
                t = new ArrayList<>();
                t.add((double) j);
                t.add((double) i);
                s.set(new int[]{j + 5, i + 5}, t);
                t = new ArrayList<>();
                t.add((double) (i * i));
                p.set(new int[]{j + 5, i + 5}, t);
            }
        }

        p.calcAttributes(s);

        PropertyDoubleTensor[] target = new PropertyDoubleTensor[2];
        target[0] = s;
        target[1] = p;

        double[] alphas = new double[7];
        double[] weights = new double[7];
        for(int i=0;i<alphas.length;i++)
        {
            alphas[i] = 1;
            weights[i] = 1;
        }

        targets = new ADFDataEntry[1];
        targets[0] = new ADFDataEntry( "target", "blarg", target, new String[0], 0);

        GenericFunction genfunTarget = (GenericFunction)funpool.getNewFunction("blah3");
        ((TunedConstant)genfunTarget.nodes.get(3)).value = 3.141592;

        // noinspection unchecked
        ArrayList<Double>[] range = new ArrayList[target[0].data.length];

        ArrayList<Double> temp1;

        for (int i = 0; i < genfunTarget.inputNodes.length; i++) {
            genfunTarget.inputNodes[i] = new Number();
        }
        for (int i = 0; i < target[0].data.length; i++) {
            genfunTarget.resetComputed();

            for (int j = 0; j < genfunTarget.inputNodes.length; j++) {
                ((Number) genfunTarget.inputNodes[j]).value = target[0].data[i].get(j);
            }

            genfunTarget.compute();

            temp1 = new ArrayList<>();
            temp1.add(((Number) genfunTarget.getOutput(0)).value);

            range[i] = temp1;
        }

        // Create the sizes array for the candidate

        int[] size = new int[target[0].size.length];
        for (int i = 0; i < size.length; i++) {
            size[i] = target[0].size[i];
        }

        // Create the PropertyDoubleTensor for the candidate
        PropertyDoubleTensor candidateOutput = new PropertyDoubleTensor(size,range);
        candidateOutput.calcAttributes(target[0]);

        target[1] = candidateOutput;

        targets[0] = new ADFDataEntry( "target", "blarg", target, new String[0], 0);


        double[][] testGen = new double[2][2];

        for (int i = 0; i < testGen.length; i++) {
            testGen[i][0] = -5;
            testGen[i][1] = 5;
        }

        PropertyDoubleTensor domain = DataGenerator.genDomain(testGen, 11);

        GenericFunction genFun1 = (GenericFunction)funpool.getNewFunction("blah1");

        PropertyDoubleTensor rangeData1 = DataGenerator.generateRange(genFun1, domain);

        rangeData1.calcAttributes(domain);


        // test defaultPrimitives file

        FunctionPool primPool = new FunctionPool("testADFs.txt");
        PropertyDoubleTensor[] newTarget = new PropertyDoubleTensor[2];
        double[][] ranges = new double[2][2];
        for (int i = 0; i < ranges.length; i++) {
            ranges[i] = new double[2];
            ranges[i][0] = -5;
            ranges[i][1] = 5;
        }
        newTarget[0] = DataGenerator.genDomain(ranges,11);
        newTarget[1] = rangeData1;

        primPool.newProblem(new SimilarityCalculator(), newTarget);

        // Test mutations:

        evolver = new Evolver(primPool, 100, alphas, weights, targets, true, 10,10,1.5,2);

        GenericFunction evGF = new GenericFunction(2,1,primPool);

        for (int i = 0; i < 5; i++) {
            evolver.insertion(evGF);
        }

        evolver.reconnect(evGF, evGF.producers[0], 0);

        for (int i = 0; i < 30; i++) {
            evolver.mutate(evGF, targets[0]);
        }

        Number[] variables = new Number[2];
        for (int i = 0; i < variables.length; i++) {
            variables[i] = new Number();
            variables[i].value = i + 1;
            evGF.inputNodes[i] = variables[i];
        }
        evGF.compute();
        evGF.resetComputed();
        adfEntry = evolver.convertToADF(evGF, newTarget, 0.5);
    }

    @Test
    public void flexGeneratePopulation() {

        evolver.generatePopulation(adfEntry, 10);
    }

    @Test
    public void test() {

    }
}
