package afm.tools;

import java.util.ArrayList;

public class PropertyTensor extends Tensor<Double>
{
	public int relMinCount;
	public int relMaxCount;
	public int saddleCount;
	public double[] centerOfMass;
	public double surfaceArea;
	
	public Tensor<Double> grad;
	
	public PropertyTensor(int[] sizes)
	{
		super(sizes);
		
	}
	
	public PropertyTensor(int[] sizes, ArrayList<Double>[] d)
	{
		super(sizes,d);
	}
	
	
	public Tensor<Double> calcAttributes(Tensor<Double>  independent)
	{
		
		grad = new Tensor<Double>(this.size);
		
		centerOfMass = new double[this.size.length+1];
		
		surfaceArea = 0;
		double partialDerivative;
		double domainArea = 1;
				
		
		ArrayList<Double> partialDerivatives;
		ArrayList<Double> currentIndVector;
		ArrayList<Double> currentDepVector;
		
		
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
		
		
		for(int i=0;i<centerOfMass.length;i++)
		{
			centerOfMass[i] = 0;
		}
		
		
		int partialMinCount;
		int partialMaxCount;
		
		relMinCount = 0;
		relMaxCount = 0;
		saddleCount = 0;
		
		// Calculate the domain surface area element
		currentIndVector = independent.data[0];
		for(int i=0;i<this.dim;i++)
		{
			index[i]++;
			domainArea = domainArea*(independent.get(index).get(i)-currentIndVector.get(i));
			index[i]--;
		}
		
		
		
		for(int i=0;i<data.length;i++)
		{
			partialDerivatives = new ArrayList<Double>();
			
			currentIndVector = independent.data[i];
			currentDepVector = (ArrayList<Double>) this.data[i];
			
			partialMinCount = 0;
			partialMaxCount = 0;
			
			for(int j=0;j<dim;j++)
			{
				x1 = currentIndVector.get(j);
				y1 = currentDepVector.get(0);
				
				if(index[j]+1 < size[j])
				{
					if(index[j]-1 >= 0)
					{
						index[j]--;
						x0 = independent.get(index).get(j);
						y0 = this.get(index).get(0);
						
						index[j] += 2;
						x2 = independent.get(index).get(j);
						y2 = this.get(index).get(0);
											
						index[j]--;
						
						m1 = (y1 - y0) / (x1 - x0);
						m2 = (y2 - y1) / (x2 - x1);
						
						partialDerivative = (m1+m2)/2;
						
												
						if(m1>0 && m2<=0)
						{
							partialMaxCount++;
							
						}else if(m1<0 && m2>=0)
						{
							partialMinCount++;
						}
						
						//System.out.println("df/dx" + j + ": " + partialDerivative);
						
					}else
					{
						index[j]++;
						x2 = independent.get(index).get(j);
						y2 = ((ArrayList<Double>) this.get(index)).get(0);
						
						index[j]--;
						
						partialDerivative = (y2 - y1) / (x2 - x1);
						
						
						
						//System.out.println("df/dx" + j + ": " + partialDerivative);
					}
					
										
				}else
				{
					if(index[j]-1 >= 0)
					{
						
						index[j]--;
						x0 = independent.get(index).get(j);
						y0 = ((ArrayList<Double>) this.get(index)).get(0);
						
						index[j]++;
						
						partialDerivative = (y1 - y0) / (x1 - x0);
						
						
						
						//System.out.println("df/dx" + j + ": " + partialDerivative);
						
					}else
					{
						partialDerivative = 0.0;
												
						//System.out.println(0);
					}
				}
				
				partialDerivatives.add(partialDerivative);
			}
			
			// Calculate the contribution of surface area around a single point and add it to the total
			double tempGradTotal=0;
			for(int j=0; j<partialDerivatives.size();j++)
			{
				tempGradTotal = tempGradTotal + Math.pow(partialDerivatives.get(j),2);
			}
			tempGradTotal = cap(Math.sqrt(tempGradTotal + 1)*domainArea);
			surfaceArea = surfaceArea + tempGradTotal;
			
			//System.out.println("surface area patch: " + tempGradTotal);
			
			
			// Calculate the contribution
			//System.out.print("Current Coordinates: ( ");
			for(int j=0;j<centerOfMass.length-1;j++)
			{
				centerOfMass[j] = cap(centerOfMass[j] + currentIndVector.get(j)*tempGradTotal);
				
				//System.out.print(currentIndVector.get(j) + " ");
			}
			
			centerOfMass[centerOfMass.length-1] = cap(centerOfMass[centerOfMass.length-1] + currentDepVector.get(0)*tempGradTotal);
			
			//System.out.print(currentDepVector.get(0) + ")\n");
			
			
			// Check for and add relative minima, maxima, and saddle points
			if(partialMinCount==dim)
			{
				relMinCount++;
				
			}else if(partialMaxCount==dim)
			{
				relMaxCount++;
				
			}else if(partialMinCount + partialMaxCount == dim)
			{
				saddleCount++;
			}
			
			
			
			grad.set(index, partialDerivatives);
			
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
		
		surfaceArea = cap(surfaceArea);
				
		//System.out.print("Center of Mass: ( ");
		
		for(int i=0;i<centerOfMass.length;i++)
		{
			centerOfMass[i] = centerOfMass[i]/surfaceArea;
			
			//System.out.print(centerOfMass[i] + " ");
		}
		
		//System.out.print(")\n");
		
		//System.out.println("relMin: " + relMinCount);
		//System.out.println("relMax: " + relMaxCount);
		//System.out.println("saddle: " + saddleCount);
		//System.out.println("Total Surface Area: " + surfaceArea);
		
		
		
		return grad;
	}
	
	private double cap(double d)
    {
        if(((Double)d).isInfinite())
        {
            return Math.signum(d)*Double.MAX_VALUE;
        }else if(((Double)d).isNaN())
        {
            return Math.signum(d);
        }else
        {
            return d;
        }
        
    }
}


