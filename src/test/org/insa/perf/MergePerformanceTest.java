package org.insa.perf;

import org.insa.algo.AbstractInputData;
import org.insa.algo.carpooling.CarPoolingAlgorithm;
import org.insa.algo.carpooling.CarPoolingData;
import org.insa.algo.carpooling.MergeAlgorithm;

public class MergePerformanceTest extends CarPoolingPerformanceTest {
	public MergePerformanceTest(AbstractInputData data) {
		super(data);
	}
	@Override
	public CarPoolingAlgorithm instanciateAlgorithm(AbstractInputData data) {
		return new MergeAlgorithm((CarPoolingData) data);
	}
}
