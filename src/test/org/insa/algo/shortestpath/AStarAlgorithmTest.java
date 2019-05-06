package org.insa.algo.shortestpath;

import org.junit.Assume;

public class AStarAlgorithmTest extends ShortestPathAlgorithmTest {
	@Override
	public ShortestPathAlgorithm instanciateAlgorithm(ShortestPathData shortestPathData) {
		return new AStarAlgorithm(shortestPathData);
	}

	@Override
	public void simpleGraphWithOraclePathTest(int from, int to) {
		Assume.assumeTrue("Test graph not relevant for AStar Algorithm\n(distance between nodes can be inferior to their 'geographic' distance), thus the heuristic is an overestimation",false);
	}
}
