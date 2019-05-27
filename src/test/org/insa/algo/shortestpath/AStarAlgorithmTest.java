package org.insa.algo.shortestpath;


public class AStarAlgorithmTest extends ShortestPathAlgorithmTest {
	@Override
	public ShortestPathAlgorithm instanciateAlgorithm(ShortestPathData shortestPathData){
		return new AStarAlgorithm(shortestPathData);
	}

	@Override
	public ShortestPathAlgorithm instanciateOracle(ShortestPathData shortestPathData) {
		return new BellmanFordAlgorithm(shortestPathData);
	}
}
