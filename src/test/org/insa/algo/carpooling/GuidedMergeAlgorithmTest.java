package org.insa.algo.carpooling;

public class GuidedMergeAlgorithmTest extends CarPoolingAlgorithmTest {
	@Override
	protected CarPoolingAlgorithm instanciateAlgorithm(CarPoolingData data) {
		return new GuidedMergeAlgorithm(data);
	}
}
