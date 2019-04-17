package afm.tools;

import java.util.ArrayList;

public interface Tensor<T> {

    ArrayList<T> getVectorAtIndex(int[] index);


}
