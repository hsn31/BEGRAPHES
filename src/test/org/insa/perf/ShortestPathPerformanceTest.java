package org.insa.perf;

import org.insa.algo.AbstractAlgorithm;
import org.insa.algo.AbstractInputData;
import org.insa.algo.shortestpath.ShortestPathAlgorithm;

public abstract class ShortestPathPerformanceTest extends PerformanceTest {
	public ShortestPathPerformanceTest(AbstractInputData data) {
		super(data);
	}
	@Override
	public abstract ShortestPathAlgorithm instanciateAlgorithm(AbstractInputData data);
}
