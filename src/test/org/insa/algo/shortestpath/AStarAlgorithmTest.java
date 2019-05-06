package org.insa.algo.shortestpath;

public class AStarAlgorithmTest extends ShortestPathAlgorithmTest {
	@Override
	public ShortestPathAlgorithm instanciateAlgorithm(ShortestPathData shortestPathData) {
		return new AStarAlgorithm(shortestPathData);
	}
}
