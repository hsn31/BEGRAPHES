package org.insa.algo.shortestpath;


import org.insa.exception.NodeOutOfGraphException;

import org.junit.Assume;


public class AStarAlgorithmTest extends ShortestPathAlgorithmTest {
	@Override
	public ShortestPathAlgorithm instanciateAlgorithm(ShortestPathData shortestPathData) throws NodeOutOfGraphException{
		return new AStarAlgorithm(shortestPathData);
	}
}
