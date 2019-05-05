package afm;


import afm.buildingAparatus.DataGenerator;
import afm.buildingAparatus.Evolver;
import afm.buildingAparatus.EvolverTest;
import afm.buildingAparatus.FunctionPool;
import afm.functions.Function;
import afm.functions.GenericFunction;
import afm.functions.Number;
import org.jzy3d.analysis.AbstractAnalysis;
import org.jzy3d.analysis.AnalysisLauncher;
import org.jzy3d.chart.factories.AWTChartComponentFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapRainbow;
import org.jzy3d.maths.Range;
import org.jzy3d.plot3d.builder.Builder;
import org.jzy3d.plot3d.builder.Mapper;
import org.jzy3d.plot3d.builder.concrete.OrthonormalGrid;
import org.jzy3d.plot3d.primitives.Shape;
import org.jzy3d.plot3d.rendering.canvas.Quality;

public class jzy3dTest extends AbstractAnalysis {
    public static void main(String[] args) throws Exception {
        AnalysisLauncher.open(new jzy3dTest());
    }

    @Override
    public void init() {

        final GenericFunction function = getRandomFunction();
        System.out.println(function.encode());
        System.out.println(function.graphVisEncode());
        // Define a function to plot
        Mapper mapper = new Mapper() {
            @Override
            public double f(double x, double y) {
                return DataGenerator.getValue(function, new double[]{x, y});
            }
        };

        // Define range and precision for the function to plot
        Range range = new Range(-50, 50);
        int steps = 80;

        // Create the object to represent the function over the given range.
        final Shape surface = Builder.buildOrthonormal(new OrthonormalGrid(range, steps, range, steps), mapper);
        surface.setColorMapper(new ColorMapper(new ColorMapRainbow(), surface.getBounds().getZmin(), surface.getBounds().getZmax(), new Color(1, 1, 1, .5f)));
        surface.setFaceDisplayed(true);
        surface.setWireframeDisplayed(false);

        // Create a chart
        chart = AWTChartComponentFactory.chart(Quality.Advanced, getCanvasType());
        chart.getScene().getGraph().add(surface);
    }

    private GenericFunction getRandomFunction() {

        EvolverTest evolverTest = new EvolverTest();
        evolverTest.setUp();

        Evolver evolver = evolverTest.evolver;

        GenericFunction evGF = new GenericFunction(2,1,new FunctionPool("defaultPrimitives.txt"));

        for(int i=0;i<20;i++)
        {
            evolver.insertion(evGF);
        }

        evolver.reconnect(evGF, evGF.producers[0], 0);

        for(int i=0;i<100;i++)
        {
            evolver.mutate(evGF, evolverTest.targets[0]);
        }

        Number[] variables = new Number[2];
        for(int i=0;i<variables.length;i++)
        {
            variables[i] = new Number();
            variables[i].value = i+1;
            evGF.inputNodes[i] = variables[i];
        }
        evGF.compute();
        evGF.resetComputed();
        evolver.stripNonContributors(evGF);
        return evGF;
    }
}