package afm.functions;

import java.util.ArrayList;
import java.util.HashMap;

import afm.buildingAparatus.FunctionPool;
import afm.tools.PropertyDoubleTensor;

public class GenericFunction extends Function
{
    public HashMap<String,Boolean> types;
    
    public PropertyDoubleTensor resultantData;
	
	/*
	 * receivers - 
	 */
	public Function[] receivers;
	
	/*
	 * producers - the array that contains the resultant output of the already
	 * 			   defined (as opposed to when it's a candidate) GenericFunction
	 * 
	 */
	public Function[] producers;
	
	/*
	 * nodes - a complete listing of all nodes in this GenericFunction
	 * 		   though not including nodes within other GenericFunctions if
	 * 		   this function contains another GenericFunction as an internal node
	 */
	public ArrayList<Function> nodes;
	
	// Nodes whose inputs can be reconnected
	public ArrayList<Function> hookyNodes; // Named after the hooks side of velcro
	
	// Nodes that can have their outputs connected to
	public ArrayList<Function> loopyNodes; // Named after the loops side of velcro
	
	public ArrayList<Function> nonRelays;

	public GenericFunction()
	{
		
	}
	
	// Probably done
	/**
	 * Use this constructor for using a GenericFunction as a candidate function to be evolved
	 * @param numInNodes
	 * @param numOutNodes
	 */
    public GenericFunction(int numInNodes, int numOutNodes, FunctionPool funPool)
    {
        receivers = new Function[numInNodes];
        producers = new Function[numOutNodes];
        inputNodes = new Function[numInNodes];
        output = new Function[numOutNodes];
        
        inputTypes = new String[numInNodes];
        for(int i=0;i<numInNodes;i++)
        {
            inputTypes[i] = "number";
        }
        
        outputTypes = new String[numOutNodes];
        for(int i=0;i<numOutNodes;i++)
        {
            outputTypes[i] = "number";
        }
        
        
        nodes = new ArrayList<Function>();
        hookyNodes = new ArrayList<Function>();
        loopyNodes = new ArrayList<Function>();
        nonRelays = new ArrayList<Function>();
        
        for(int i=0;i<numInNodes;i++)
        {
            receivers[i] = funPool.getNewFunctionWithID("relay");
            nodes.add(receivers[i]);
            loopyNodes.add(receivers[i]);
        }
        
        for(int i=0;i<numOutNodes;i++)
        {
            producers[i] = funPool.getNewFunctionWithID("relay");
            nodes.add(producers[i]);
            hookyNodes.add(producers[i]);
        }
        
        
    }
	
	/**
	 * 
	 * 
	 * @param graphStructure
	 * 
	 * Syntax of graphStructure (all actually on same line):
     * 
     * @ <typeOfFunction> 
     * # <receiverId> <receiverType> <receiverId> <receiverType> ... <receiverId> <receiverType> // all receivers
     * # <producerId> <producerType> <producerId> <producerType> ... <producerId> <producerType> // all producers
     * * <nodeId> <nodeType> <inputNodeId> <inputNodeSelect> <inputNodeId> <inputNodeSelect> ... // node in graph
     * * <nodeId> <nodeType> <inputNodeId> <inputNodeSelect> <inputNodeId> <inputNodeSelect> ... // node in graph
     * ...
     * * <nodeId> <nodeType> <inputNodeId> <inputNodeSelect> <inputNodeId> <inputNodeSelect> ... // node in graph
     * 
     * 
     * A nodeId of -1 indicates that this edge takes input from output outside this GenericFunction 
     * 
	 * @param pool
	 */
	public GenericFunction(String graphStructure, FunctionPool pool)
	{
		/*
		 * To be initialized:
		 *  
         * type 
		 * inputTypes 
         * outputTypes
         * inputNodes 
         * inputSelect 
         * output 
		*/
		
		ArrayList<Entry> tempNodes = new ArrayList<Entry>();
		String delims = "\\s+";
		String[] tokens = graphStructure.split(delims);

		ArrayList<String> tempReceivers = new ArrayList<String>();
		ArrayList<String> tempProducers = new ArrayList<String>();

		ArrayList<String> tempInputTypes = new ArrayList<String>();
		ArrayList<String> tempOutputTypes = new ArrayList<String>();
		
		// Initialize type
		type = tokens[1];
		
		int i = 3;

		while (tokens[i].compareTo("#") != 0)
		{
			tempReceivers.add(tokens[i]);
			i++;
			
			tempInputTypes.add(tokens[i]);
			i++;
						
		}
		i++;

		while (tokens[i].compareTo("*") != 0)
		{
			tempProducers.add(tokens[i]);
			i++;
			
			tempOutputTypes.add(tokens[i]);
			i++;
		}

		Entry newEntry = null;

		// Collects all of the information for each node
		while (i < tokens.length)
		{
			if (tokens[i].compareTo("*") == 0)
			{
				newEntry = new Entry();
				newEntry.id = Integer.parseInt(tokens[i + 1]);
				newEntry.type = tokens[i + 2];

				tempNodes.add(newEntry);

				i = i + 3;
			} else
			{
				ReceivingEdge ed = new ReceivingEdge();
				ed.id = Integer.parseInt(tokens[i]);
				ed.select = Integer.parseInt(tokens[i + 1]);
				
				if(ed.id != 0)
				{
				    newEntry.receivingEdges.add(ed);
				}
				
				

				i = i + 2;

			}
		}
		
		// initialize the Function sets with their determined sizes
		nodes = new ArrayList<Function>(tempNodes.size());
		producers = new Function[tempProducers.size()];
		receivers = new Function[tempReceivers.size()];
				
				
		Entry tempEntry;
		
		// instantiates all nodes in the function
		for(int j=0;j<tempNodes.size();j++)
		{
		    tempEntry = tempNodes.get(j);
		    
		    nodes.add(pool.getNewFunction(tempEntry.type));
		    nodes.get(j).id = tempEntry.id;
		    
		}
		
		int nextReceiverSlot=0;
        int nextProducerSlot=0;
		
		// connects all nodes to their inputs
		for(int j=0; j<nodes.size();j++)
		{
		    tempEntry = tempNodes.get(j);
		    nodes.get(j).inputNodes = new Function[tempEntry.receivingEdges.size()];
		    
		    ReceivingEdge tempEdge = null;
		    
		    for(int k=0; k<tempEntry.receivingEdges.size(); k++)
		    {
		        tempEdge = tempEntry.receivingEdges.get(k);
		        
		        if(tempEdge.id != -1)
		        {
		            nodes.get(j).inputNodes[k] = getNode(nodes,tempEdge.id);
		        }
		        
		        nodes.get(j).inputSelect[k] = tempEdge.select;
		    }
		    
		    if(nodes.get(j).type.equalsIgnoreCase("relay"))
		    {
		        if(tempEdge.id==-1)
		        {
		            receivers[nextReceiverSlot] = nodes.get(j);
		            nextReceiverSlot++;
		        }else
		        {
		            producers[nextProducerSlot] = nodes.get(j);
		            nextProducerSlot++;
		        }
		    }
		}
		
		//initialize inputTypes
		inputTypes = new String[tempInputTypes.size()];
		
		for(i=0;i<tempInputTypes.size();i++)
		{
		    inputTypes[i] = tempInputTypes.get(i);
		}
		
		//initialize outputTypes
		
		outputTypes = new String[tempOutputTypes.size()];
		
		for(i=0;i<tempOutputTypes.size();i++)
		{
			outputTypes[i] = tempOutputTypes.get(i);
		}
		
		//initialize inputNodes
        inputNodes = new Function[inputTypes.length];
		
        //initialize inputSelect
        inputSelect = new int[inputTypes.length];
		
		
		//initialize output
		output = new Function[outputTypes.length];
		
		
        
	}
	
	/**
	 * 
	 * @param array
	 * @param id
	 * @return returns the node with the given id or null if it does not exist
	 */
	private Function getNode(ArrayList<Function> array, int id)
	{
	    for(Function f : array)
	    {
	        if(f.id == id)
	        {
	            return f;
	        }
	    }
	    return null;
	}

	
	public void compute()
	{
		for(int i=0;i<receivers.length;i++)
		{
			receivers[i].inputNodes[0] = inputNodes[i];
		}
	    for(int i=0;i<producers.length;i++)
	    {
	        producers[i].compute();
	        output[i] = producers[i].getOutput(0);
	    }
	    computed = true;
	}
	
	
	public Function getOutput(int n)
	{
	    if(!computed)
	    {
	        compute();
	    }
		return output[n];
	}

	/**
	 * Syntax encoding (all actually on same line):
     * 
     * @ <typeOfFunction> 
     * # <receiverId> <receiverType> <receiverId> <receiverType> ... <receiverId> <receiverType> // all receivers
     * # <producerId> <producerType> <producerId> <producerType> ... <producerId> <producerType> // all producers
     * * <nodeId> <nodeType> <inputNodeId> <inputNodeSelect> <inputNodeId> <inputNodeSelect> ... // node in graph
     * * <nodeId> <nodeType> <inputNodeId> <inputNodeSelect> <inputNodeId> <inputNodeSelect> ... // node in graph
     * ...
     * * <nodeId> <nodeType> <inputNodeId> <inputNodeSelect> <inputNodeId> <inputNodeSelect> ... // node in graph
	 * @return
	 */
	public String encode()
	{
		
		
		String encoding = "";
		encoding = encoding + "@ " + type + " ";

		encoding = encoding + "# ";
		for (int i = 0; i < receivers.length; i++)
		{

			encoding = encoding + receivers[i].id + " ";
			encoding = encoding + inputTypes[i] + " "; 
		}
		//encoding = encoding + "\r\n";
		encoding = encoding + "# ";

		for (int i = 0; i < producers.length; i++)
		{

			encoding = encoding + producers[i].id + " ";
			encoding = encoding + outputTypes[i] + " ";
		}
		//encoding = encoding + "\r\n";

		for (int i = 0; i < nodes.size(); i++)
		{
			encoding = encoding + "* ";
			encoding = encoding + nodes.get(i).id + " ";
			encoding = encoding + nodes.get(i).type + " ";

			for (int j = 0; j < nodes.get(i).inputNodes.length; j++)
			{
				encoding = encoding + nodes.get(i).inputNodes[j].id + " ";
				encoding = encoding + nodes.get(i).inputSelect[j] + " ";

			}
			//encoding = encoding + "\r\n";
		}
		
		types = new HashMap<String,Boolean>();
		
		for(int i=0;i<nodes.size();i++)
		{
			if(nodes.get(i).type.compareTo("relay")!=0)
			{
				if(!types.containsKey(nodes.get(i).type))
				{
					types.put(nodes.get(i).type,false);
				}
				
			}
		}

		return encoding;
	}
	
	public String graphVisEncode()
	{
		String encoding = "digraph G {\n";
		
		
		for (int i = 0; i < nodes.size(); i++)
		{
			
			for (int j = 0; j < nodes.get(i).inputNodes.length; j++)
			{
				if(!(nodes.get(i).inputNodes[j].type.compareTo("number")==0))
				{
				    encoding = encoding + "\"" + nodes.get(i).inputNodes[j].type + "_" + nodes.get(i).inputNodes[j].id + "\"" + " -> " + "\"" + nodes.get(i).type + "_" + nodes.get(i).id + "\"" + "\n";
				}
				
			}
		}
		
		encoding = encoding + "}";
		
		return encoding;
	}

	private class Entry
	{
		int id;
		String type;
		ArrayList<ReceivingEdge> receivingEdges;

		public Entry()
		{
			receivingEdges = new ArrayList<ReceivingEdge>();
		}
	}

	private class ReceivingEdge
	{
		public int id;
		public int select;
	}
	
	public void resetComputed()
	{
	    for(int i=0; i<nodes.size();i++)
	    {
	        nodes.get(i).resetComputed();
	    }
        computed = false;
    }
	
	

}
