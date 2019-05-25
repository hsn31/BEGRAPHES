package org.insa.perf;

import org.insa.algo.AbstractInputData;
import org.insa.algo.shortestpath.DijkstraAlgorithm;
import org.insa.algo.shortestpath.ShortestPathData;

public class DijkstraPerformanceTest extends ShortestPathPerformanceTest {
	public DijkstraPerformanceTest(AbstractInputData data) {
		super(data);
	}

	@Override
	public DijkstraAlgorithm instanciateAlgorithm(AbstractInputData data) {
		return new DijkstraAlgorithm((ShortestPathData) data);
	}
}
