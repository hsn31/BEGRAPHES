package org.insa.algo.carpooling;

import org.junit.Test;

public class GuidedMergeAlgorithmTest extends CarPoolingAlgorithmTest {
	@Override
	protected CarPoolingAlgorithm instanciateAlgorithm(CarPoolingData data) {
		return new GuidedMergeAlgorithm(data);
	}


}
