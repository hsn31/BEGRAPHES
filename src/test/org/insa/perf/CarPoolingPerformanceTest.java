package org.insa.perf;

import org.insa.algo.AbstractInputData;
import org.insa.algo.carpooling.CarPoolingAlgorithm;

public abstract class CarPoolingPerformanceTest extends PerformanceTest {
	public CarPoolingPerformanceTest(AbstractInputData data) {
		super(data);
	}
	@Override
	public abstract CarPoolingAlgorithm instanciateAlgorithm(AbstractInputData data);
}
