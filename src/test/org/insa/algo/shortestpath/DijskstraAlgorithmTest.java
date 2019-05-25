package org.insa.algo.shortestpath;

public class DijskstraAlgorithmTest extends ShortestPathAlgorithmTest {
	@Override
	public ShortestPathAlgorithm instanciateAlgorithm(ShortestPathData shortestPathData) {
		return new DijkstraAlgorithm(shortestPathData);
	}
}
