package org.insa.algo.shortestpath;

import org.insa.exception.NodeOutOfGraphException;
import org.insa.graph.Arc;
import org.insa.graph.Point;
import org.insa.graph.Node;

public class AStarAlgorithm extends DijkstraAlgorithm {

    public AStarAlgorithm(ShortestPathData data) throws NodeOutOfGraphException {
        super(data);
    }
    @Override
    protected Label newLabel(Node node, double cost) {
		return new LabelStar(node, cost, getInputData());
	}
}
