package org.insa.perf;

import org.insa.algo.AbstractInputData;
import org.insa.algo.shortestpath.BellmanFordAlgorithm;
import org.insa.algo.shortestpath.ShortestPathAlgorithm;
import org.insa.algo.shortestpath.ShortestPathData;

public class BellmanFordPerformanceTest extends ShortestPathPerformanceTest {
	public BellmanFordPerformanceTest(AbstractInputData data) {
		super(data);
	}

	@Override
	public ShortestPathAlgorithm instanciateAlgorithm(AbstractInputData data) {
		return new BellmanFordAlgorithm((ShortestPathData) data);
	}
}
