package org.insa.algo.shortestpath;

import java.util.ArrayList;
import java.util.HashMap;

import org.insa.algo.utils.BinaryHeap;
import org.insa.exception.NodeOutOfGraphException;
import org.insa.graph.Graph;
import org.insa.graph.Node;
import org.insa.graph.Path;

public class CovoitAlgorithm extends ShortestPathAlgorithm{
	public CovoitAlgorithm(ShortestPathData data) throws NodeOutOfGraphException {
		super(data);
		
	}
	@Override
	public ShortestPathSolution doRun() {
		
		ShortestPathSolution res;
		BinaryHeap<Label> binaryHeap=new BinaryHeap<>();
		Path solutionPath;
		ArrayList<Node> nodePath = new ArrayList<>();
		Graph graph = data.getGraph();
		HashMap<Node, Label> labels = new HashMap<>();
		
		

		
		
		
		
		
		
		
		
		
		return null;
		
	}

}
