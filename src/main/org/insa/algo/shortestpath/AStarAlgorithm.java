package org.insa.algo.shortestpath;

import org.insa.graph.Arc;
import org.insa.graph.Point;
import org.insa.graph.Node;

public class AStarAlgorithm extends DijkstraAlgorithm {

    public AStarAlgorithm(ShortestPathData data) {
        super(data);
    }

	protected Label newLabel(Node node, ShortestPathData data) {
		return new LabelStar(node, data);
	}
}
