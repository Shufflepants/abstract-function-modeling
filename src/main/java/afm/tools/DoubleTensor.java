package afm.tools;

import java.util.ArrayList;

public class DoubleTensor implements Tensor<Double>
{
    public ArrayList<Double>[] data;
    public int dim;
    public int[] size;

    /**
     *
     * @param sizes
     */
    public DoubleTensor(int[] sizes)
    {
        size = new int[sizes.length];
        int product = 1;
        for(int i=0; i<sizes.length;i++)
        {
            product*=sizes[i];
        }

        for(int i=0;i<sizes.length;i++)
        {
            size[i] = sizes[i];
        }

        dim = sizes.length;
        // noinspection unchecked
        data = new ArrayList[product];
    }

    /**
     *
     * @param sizes
     * @param d
     */
    public DoubleTensor(int[] sizes, ArrayList<Double>[] d)
    {

        size = new int[sizes.length];
        for(int i=0;i<sizes.length;i++)
        {
            size[i] = sizes[i];
        }

        dim = sizes.length;

        data = d;
    }

    @Override
    public ArrayList<Double> getVectorAtIndex(int[] index)
    {

        int ind = 0;
        int multiplier=1;
        for(int i=0;i<index.length;i++)
        {
            ind = ind + index[i]*multiplier;
            multiplier = multiplier*size[i];
        }

        //System.out.println(index[0] + " " + index[1] + " " + ind);

        return data[ind];
    }

    public void set(int[] index, ArrayList<Double> value)
    {
        int ind = 0;
        int multiplier=1;
        for(int i=0;i<index.length;i++)
        {
            ind = ind + index[i]*multiplier;
            multiplier = multiplier*size[i];
        }

        data[ind] = value;
    }


    /**
     *
     * @param dependent - A DoubleTensor that contains the dependant variables
     * @return grad - a tensor that contains the point-wise computed gradient
     */
    public DoubleTensor gradient(DoubleTensor dependent)
    {

        DoubleTensor grad = new DoubleTensor(this.size);

        int[] index = new int[dim];

        for(int i=0;i<dim;i++)
        {
            index[i] = 0;
        }

        double x0;
        double x2;
        double y0;
        double y2;
        double x1;
        double y1;
        double m1;
        double m2;

        ArrayList<Double> d;
        ArrayList<Double> currentIndVector;
        ArrayList<Double> currentDepVector;

        for(int i=0;i<data.length;i++)
        {
            d = new ArrayList<>();

            currentIndVector = this.data[i];
            currentDepVector = dependent.data[i];

            for(int j=0;j<dim;j++)
            {
                x1 = currentIndVector.get(j);
                y1 = currentDepVector.get(0);

                if(index[j]+2 < size[j])
                {
                    if(index[j]-1 >= 0)
                    {
                        index[j]--;
                        x0 = getVectorAtIndex(index).get(j);
                        y0 = dependent.getVectorAtIndex(index).get(0);

                        index[j] += 2;
                        x2 = getVectorAtIndex(index).get(j);
                        y2 = dependent.getVectorAtIndex(index).get(0);

                        index[j]--;

                        m1 = (y1 - y0) / (x1 - x0);
                        m2 = (y2 - y1) / (x2 - x1);

                        d.add((m1+m2)/2);

                        System.out.println(" x0 " + x0 + " x1 " + x1 + " x2 " + x2 + " y0 " + y0 + " y1 " + y1 + " y2 " + y2 + " m1 " + m1 + " m2 " + m2 );

                    }else
                    {
                        index[j]++;
                        x2 = getVectorAtIndex(index).get(j);
                        y2 = dependent.getVectorAtIndex(index).get(0);

                        index[j]--;

                        d.add((y2 - y1) / (x2 - x1));

                        System.out.println(" x1 " + x1 + " x2 " + x2 + " y1 " + y1 + " y2 " + y2);
                    }
                }else
                {
                    if(index[j]-1 >= 0)
                    {

                        index[j]--;
                        x0 = getVectorAtIndex(index).get(j);
                        y0 = dependent.getVectorAtIndex(index).get(0);

                        index[j]++;

                        d.add((y1 - y0) / (x1 - x0));

                        System.out.println(" x0 " + x0 + " x1 " + x1 + " y0 " + y0 + " y1 " + y1 );

                    }else
                    {
                        d.add(0.0);

                        System.out.println(0);
                    }
                }
            }

            grad.set(index, d);

            index[0]++;

            for(int k=0;k<dim-1;k++)
            {
                if(index[k]>=size[k])
                {
                    index[k] = 0;
                    index[k+1]++;
                }else
                {
                    break;
                }
            }
        }
        return grad;
    }

    public DoubleTensor jacobian(DoubleTensor b)
    {

        return null;
    }
}
