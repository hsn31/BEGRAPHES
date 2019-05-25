package org.insa.algo.shortestpath;

import org.insa.graph.Node;

public class AStarAlgorithm extends DijkstraAlgorithm {

    public AStarAlgorithm(ShortestPathData data) {
        super(data);
    }
    @Override
    protected Label newLabel(Node node, double cost) {
		return new LabelStar(node, cost, getInputData());
	}


}
