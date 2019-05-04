package org.insa.algo.shortestpath;

import org.insa.graph.Arc;
import org.insa.graph.Point;
import org.insa.graph.Node;

public class AStarAlgorithm extends DijkstraAlgorithm {

    public AStarAlgorithm(ShortestPathData data) {
        super(data);
    }
    @Override
    protected double heuristic(Label from, Label to, Arc arc) {
        return super.heuristic(from, to, arc)+ Point.distance(getInputData().getDestination().getPoint(),to.getNode().getPoint());
    } // A mons sens ça suffit sans redéfinir de Label

    
	protected Label newLabel(Node node, ShortestPathData data) {
		return new LabelStar(node, data);
	}
}
