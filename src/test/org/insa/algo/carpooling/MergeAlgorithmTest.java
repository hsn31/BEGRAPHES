package org.insa.algo.carpooling;

public class MergeAlgorithmTest extends CarPoolingAlgorithmTest {
	@Override
	protected CarPoolingAlgorithm instanciateAlgorithm(CarPoolingData data) {
		return new MergeAlgorithm(data);
	}
}
