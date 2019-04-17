package afm.tools;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class DoubleTensorTest {

    @Test
    public void flexGradient() {
        DoubleTensor m = new DoubleTensor(new int[]{11,11});
        DoubleTensor o = new DoubleTensor(new int[]{11,11});

        for(int i=-5;i<6;i++)
        {
        	for(int j=-5;j<6;j++)
        	{
                ArrayList<Double> t = new ArrayList<>();
        		t.add((double)j);
        		t.add((double)i);
        		m.set(new int[]{j+5, i+5},t);
        		t = new ArrayList<>();
        		t.add((double)i*j);
        		o.set(new int[]{j+5, i+5},t);
        	}
        }
        m.gradient(o);
    }
}
