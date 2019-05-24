package org.insa.perf;

import org.insa.algo.AbstractInputData;
import org.insa.algo.carpooling.CarPoolingAlgorithm;
import org.insa.algo.carpooling.CarPoolingData;
import org.insa.algo.carpooling.GuidedMergeAlgorithm;

public class GuidedMergePerformanceTest extends CarPoolingPerformanceTest {
	public GuidedMergePerformanceTest(AbstractInputData data) {
		super(data);
	}
	@Override
	public CarPoolingAlgorithm instanciateAlgorithm(AbstractInputData data) {
		return new GuidedMergeAlgorithm((CarPoolingData) data);
	}
}
