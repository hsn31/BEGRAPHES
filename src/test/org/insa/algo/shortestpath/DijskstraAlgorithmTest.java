package org.insa.algo.shortestpath;

import org.insa.algo.AbstractInputData;
import org.insa.algo.AbstractSolution;
import org.insa.algo.ArcInspector;
import org.insa.algo.ArcInspectorFactory;
import org.insa.exception.NodeOutOfGraphException;
import org.insa.graph.Arc;
import org.insa.graph.Graph;
import org.insa.graph.Node;
import org.insa.graph.RoadInformation;
import org.insa.graph.io.BinaryGraphReader;
import org.insa.graph.io.GraphReader;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class DijskstraAlgorithmTest extends ShortestPathAlgorithmTest {
	@Override
	public ShortestPathAlgorithm instanciateAlgorithm(ShortestPathData shortestPathData) throws NodeOutOfGraphException {
		return new DijkstraAlgorithm(shortestPathData);
	}
}
