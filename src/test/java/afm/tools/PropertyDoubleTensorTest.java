package afm.tools;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class PropertyDoubleTensorTest {

    @Test
    public void flexCalcAttributes() {

        PropertyDoubleTensor s = new PropertyDoubleTensor(new int[]{11,11});
        PropertyDoubleTensor p = new PropertyDoubleTensor(new int[]{11,11});

        for(int j= -5;j<6;j++)
        {
            for(int i= -5;i<6;i++)
            {
                ArrayList<Double> t = new ArrayList<>();
                t.add((double)j);
                t.add((double)i);
                s.set(new int[]{j+5,i+5},t);
                t = new ArrayList<>();
                t.add((double) (i*i));
                p.set(new int[]{j+5,i+5},t);
            }
        }
        p.calcAttributes(s);
    }
}
