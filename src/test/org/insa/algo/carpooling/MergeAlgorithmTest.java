package org.insa.algo.carpooling;

import org.insa.graph.Graph;
import org.insa.graph.Node;

public class MergeAlgorithmTest extends CarPoolingAlgorithmTest {
	@Override
	protected CarPoolingAlgorithm instanciateAlgorithm(CarPoolingData data) {
		return new MergeAlgorithm(data);
	}

	@Override
	public void oracleTest(Graph graph, Node user_a, Node user_b, Node target) {

	}
}
