package org.insa.algo.shortestpath;

import org.insa.algo.AbstractSolution;

public class DijsktraAlgorithmTest extends ShortestPathAlgorithmTest {
	@Override
	protected ShortestPathAlgorithm instanciateAlgorithm(ShortestPathData data) {
		return new DijkstraAlgorithm(data);
	}

	@Override
	protected AbstractSolution.Status statusWhenNoMoverequired() {
		return AbstractSolution.Status.OPTIMAL;
	}
}
