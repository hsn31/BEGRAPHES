package org.insa.perf;

import org.insa.algo.AbstractAlgorithm;
import org.insa.algo.AbstractInputData;

public abstract class PerformanceTest {
	private AbstractInputData data;
	public PerformanceTest(AbstractInputData data){
		this.data = data;
	}
	public abstract AbstractAlgorithm instanciateAlgorithm(AbstractInputData data);
	public long evaluate(){
		AbstractAlgorithm algorithm = instanciateAlgorithm(this.data);
		long start = System.currentTimeMillis();
		algorithm.run();
		return System.currentTimeMillis()-start;
	}
}
