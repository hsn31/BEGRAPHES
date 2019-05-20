package org.insa.perf;

import org.insa.algo.AbstractInputData;
import org.insa.algo.shortestpath.AStarAlgorithm;
import org.insa.algo.shortestpath.ShortestPathAlgorithm;
import org.insa.algo.shortestpath.ShortestPathData;

public class AStarPerformanceTest extends ShortestPathPerformanceTest {
	public AStarPerformanceTest(AbstractInputData data) {
		super(data);
	}

	@Override
	public ShortestPathAlgorithm instanciateAlgorithm(AbstractInputData data) {
		return new AStarAlgorithm((ShortestPathData) data);
	}
}
