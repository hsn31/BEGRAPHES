package org.insa.algo.shortestpath;

import org.insa.algo.AbstractInputData;
import org.insa.algo.AbstractSolution;
import org.insa.algo.ArcInspector;
import org.insa.algo.ArcInspectorFactory;
import org.insa.graph.Arc;
import org.insa.graph.Graph;
import org.insa.graph.Node;
import org.insa.graph.RoadInformation;
import org.insa.graph.io.BinaryGraphReader;
import org.insa.graph.io.GraphReader;
import org.junit.Assume;
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

public class DijskstraAlgorithmTest {
	// Simple Test graph from subject
	private static Graph graph;

	//Graph from map
	private static Graph mapGraph;
	private static String mapName = "C:\\Users\\Brice\\Desktop\\carre.mapgr";


	// List of nodes
	private static Node[] nodes;

	// List of arcs in the graph, x1_x2 is the arc from node x1 (0) to x2 (1).
	@SuppressWarnings("unused")
	private static Arc x1_x2, x1_x3, x2_x4, x2_x5, x2_x6, x3_x1, x3_x6, x3_x2, x6_x5, x5_x3, x5_x4, x5_x6;

	private static ArcInspector defaultArcInspector;
	private static ArcInspector mapArcInspector;

	@BeforeClass
	public static void initAll() throws IOException {

		// 10 and 20 meters per seconds
		RoadInformation speed10 = new RoadInformation(RoadInformation.RoadType.MOTORWAY, null, true, 1, "");
		// Create nodes
		nodes = new Node[6];
		for (int i = 0; i < nodes.length; ++i) {
			nodes[i] = new Node(i, null);
		}

		// Add arcs...

		x1_x2 = Node.linkNodes(nodes[0], nodes[1], 7, speed10, null);
		x1_x3 = Node.linkNodes(nodes[0], nodes[2], 8, speed10, null);


		x2_x4 = Node.linkNodes(nodes[1], nodes[3], 4, speed10, null);
		x2_x5 = Node.linkNodes(nodes[1], nodes[4], 1, speed10, null);
		x2_x6 = Node.linkNodes(nodes[1], nodes[5], 5, speed10, null);

		x3_x1 = Node.linkNodes(nodes[2], nodes[0], 7, speed10, null);
		x3_x2 = Node.linkNodes(nodes[2], nodes[1], 2, speed10, null);
		x3_x6 = Node.linkNodes(nodes[2], nodes[5], 2, speed10, null);

		x5_x3 = Node.linkNodes(nodes[4], nodes[2], 2, speed10, null);
		x5_x4 = Node.linkNodes(nodes[4], nodes[3], 2, speed10, null);
		x5_x6 = Node.linkNodes(nodes[4], nodes[5], 3, speed10, null);
		x6_x5 = Node.linkNodes(nodes[5], nodes[4], 3, speed10, null);


		graph = new Graph("ID", "", Arrays.asList(nodes), null);
		defaultArcInspector = new ArcInspector() {
			@Override
			public boolean isAllowed(Arc arc) {
				return true;
			}

			@Override
			public double getCost(Arc arc) {
				return arc.getLength();
			}

			@Override
			public int getMaximumSpeed() {
				return 0;
			}

			@Override
			public AbstractInputData.Mode getMode() {
				return null;
			}
		};


		// Create a graph reader.
		GraphReader reader = new BinaryGraphReader(
				new DataInputStream(new BufferedInputStream(new FileInputStream(mapName))));

		//Read the graph.
		mapGraph = reader.read();
		mapArcInspector = ArcInspectorFactory.getAllFilters().get(0);


	}


	public void dijkstraAlgorithmSimpleGraphWithOraclePathTest(int from, int to) {

		ShortestPathData shortestPathData = new ShortestPathData(graph, nodes[from], nodes[to], defaultArcInspector);
		BellmanFordAlgorithm bellmanFordAlgorithm = new BellmanFordAlgorithm(shortestPathData);
		DijkstraAlgorithm dijkstraAlgorithm = new DijkstraAlgorithm(shortestPathData);


		ShortestPathSolution bellmanFordSolution = bellmanFordAlgorithm.doRun();
		ShortestPathSolution dijkstraSolution = dijkstraAlgorithm.doRun();

		assertEquals("Algorithms finished with different status", bellmanFordSolution.getStatus(), dijkstraSolution.getStatus());
		assertTrue("End status incorrect", AbstractSolution.Status.OPTIMAL == dijkstraSolution.getStatus() || dijkstraSolution.getStatus() == AbstractSolution.Status.INFEASIBLE);


		//Assume.assumeTrue(dijkstraSolution.getStatus() != AbstractSolution.Status.INFEASIBLE);
		if (dijkstraSolution.getStatus()!=AbstractSolution.Status.INFEASIBLE) {
			assertEquals("BellmanFord and Dijkstra solution give differrent number of nodes in path", bellmanFordSolution.getPath().size(), dijkstraSolution.getPath().size());
			assertTrue("Different lenghth for BellmanFord solution and Dijkstra solution", bellmanFordSolution.getPath().getLength() == dijkstraSolution.getPath().getLength());
			for (int i = 0; i < bellmanFordSolution.getPath().getArcs().size(); i++) {
				assertEquals("Different arcs founded for Dijkstra and Bellman_Ford solutions", bellmanFordSolution.getPath().getArcs().get(i), dijkstraSolution.getPath().getArcs().get(i));
			}
		}


	}

	@Test
	public void dijkstraAlgorithmSimpleGraphWithOracleTest() {
		for (int from = 0; from < nodes.length; from++) {
			for (int to = 0; to < nodes.length; to++) {
				dijkstraAlgorithmSimpleGraphWithOraclePathTest(from, to);
			}
		}

	}

	@Test
	public void dijkstraAlgorithmMapWithOracleTest() {
		Random random = new Random();
		for (int i = 0; i < 10; i++) {
			int from = random.nextInt(mapGraph.size());
			int to = random.nextInt(mapGraph.size());
			System.out.println("FROM: " + from + " TO " + to);
			ShortestPathData shortestPathData = new ShortestPathData(mapGraph, mapGraph.get(from), mapGraph.get(to), mapArcInspector);
			BellmanFordAlgorithm bellmanFordAlgorithm = new BellmanFordAlgorithm(shortestPathData);
			DijkstraAlgorithm dijkstraAlgorithm = new DijkstraAlgorithm(shortestPathData);

			ShortestPathSolution bellmanFordSolution = bellmanFordAlgorithm.doRun();
			ShortestPathSolution dijkstraSolution = dijkstraAlgorithm.doRun();

			assertEquals("Bellman Ford and Dijkstra finished with different status on map " + mapName, bellmanFordSolution.getStatus(), dijkstraSolution.getStatus());
			assertTrue("End status incorrect,should be INFEASIBLE or OPTIMAL, is "+dijkstraSolution.getStatus().toString(), AbstractSolution.Status.OPTIMAL == dijkstraSolution.getStatus() || dijkstraSolution.getStatus() == AbstractSolution.Status.INFEASIBLE);

			//Assume.assumeTrue(dijkstraSolution.getStatus() != AbstractSolution.Status.INFEASIBLE);
			if (dijkstraSolution.getStatus()!=AbstractSolution.Status.INFEASIBLE) {
				assertTrue(bellmanFordSolution.getPath().getLength() == dijkstraSolution.getPath().getLength());
			}


		}


	}

}
