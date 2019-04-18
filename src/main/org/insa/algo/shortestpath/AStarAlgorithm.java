package org.insa.algo.shortestpath;

import org.insa.graph.Arc;
import org.insa.graph.Point;


public class AStarAlgorithm extends DijkstraAlgorithm {

    public AStarAlgorithm(ShortestPathData data) {
        super(data);
    }
    @Override
    protected double heuristic(Label from, Label to, Arc arc) {
        return super.heuristic(from, to, arc)+ Point.distance(from.getNode().getPoint(),to.getNode().getPoint());
    }
}
