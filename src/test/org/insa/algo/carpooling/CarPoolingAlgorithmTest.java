package org.insa.algo.carpooling;

import org.insa.algo.AbstractInputData;
import org.insa.algo.AbstractSolution;
import org.insa.algo.ArcInspector;
import org.insa.algo.ArcInspectorFactory;
import org.insa.algo.shortestpath.AStarAlgorithm;
import org.insa.algo.shortestpath.ShortestPathData;
import org.insa.algo.shortestpath.ShortestPathSolution;
import org.insa.graph.*;
import org.insa.graph.io.BinaryGraphReader;
import org.insa.graph.io.GraphReader;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

public abstract class CarPoolingAlgorithmTest {
	// Simple Test graph from subject
	protected static Graph simpleGraph;

	//Square Map
	protected static Graph squareMapGraph;


	private static String maps = "/home/decaeste/Bureau/commetud/3eme Annee MIC/Graphes-et-Algorithmes/Maps/";
	//private static String maps = "C:\\Users\\Brice\\Documents\\Cours 3A MIC\\Graphe\\Maps\\";
	private static String midi_pyrenees = maps + "midi-pyrenees.mapgr";
	private static String carre_dense = maps + "carre-dense.mapgr";
	private static String fractale = maps + "fractal-spiral.mapgr";
	private static String toulouse = maps + "toulouse.mapgr";
	private static String newZealand = maps + "new-zealand.mapgr";
	private static String california = maps + "california.mapgr";
	private static String belgium = maps + "belgium.mapgr";
	private static String squareMapName = maps+"carre.mapgr";

	//HG Map


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
			nodes[i] = new Node(i, new Point(0, 0));
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


		simpleGraph = new Graph("ID", "", Arrays.asList(nodes), null);
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


//		// Create a graph reader.
		GraphReader reader = new BinaryGraphReader(
				new DataInputStream(new BufferedInputStream(new FileInputStream(squareMapName))));

		//Read the graph.
		squareMapGraph = reader.read();
		mapArcInspector = ArcInspectorFactory.getAllFilters().get(0);


	}

	protected abstract CarPoolingAlgorithm instanciateAlgorithm(CarPoolingData data);

	protected CarPoolingAlgorithm instanciateOracle(CarPoolingData data) {
		return new MergeAlgorithm(data);
	}

	protected void upperBoundTest(Graph graph, CarPoolingSolution solution) {

	}

	protected void segmentOptimalityTest(Graph graph, CarPoolingSolution solution) {
		if (solution.getStatus() != AbstractSolution.Status.INFEASIBLE) {
			Assert.assertEquals("Algorithm end status incorrect, should be OPTIMAL", AbstractSolution.Status.OPTIMAL, solution.getStatus());
			Node A = solution.getPath_A().getOrigin();
			Node B = solution.getPath_B().getOrigin();
			Node O = solution.getCommonPath().getOrigin();
			Node T = solution.getCommonPath().getDestination();


			double distAO = new AStarAlgorithm(new ShortestPathData(graph, A, O, solution.getInputData().getArcInspector())).run().getCost();
			double distBO = new AStarAlgorithm(new ShortestPathData(graph, B, O, solution.getInputData().getArcInspector())).run().getCost();
			double distOT = new AStarAlgorithm(new ShortestPathData(graph, O, T, solution.getInputData().getArcInspector())).run().getCost();


			Assert.assertTrue("AO path is not the shortest possible, should be " + distAO + " is " + solution.getCostA(), Math.abs(distAO - solution.getCostA()) <= distAO / 100);
			Assert.assertTrue("BO path is not the shortest possible, should be " + distBO + " is " + solution.getCostB(), Math.abs(distBO - solution.getCostB()) <= distBO / 100);
			Assert.assertTrue("OT path is not the shortest possible, should be " + distOT + " is " + solution.getCostAB(), Math.abs(distOT - solution.getCostAB()) <= distOT / 100);
		}
	}


	protected void validityTest(Graph graph, CarPoolingSolution solution) {
		ShortestPathSolution oracleAT = new AStarAlgorithm(new ShortestPathData(graph, solution.getInputData().getUser_A(), solution.getInputData().getDestination(), ArcInspectorFactory.getAllFilters().get(0))).run();
		ShortestPathSolution oracleBT = new AStarAlgorithm(new ShortestPathData(graph, solution.getInputData().getUser_B(), solution.getInputData().getDestination(), ArcInspectorFactory.getAllFilters().get(0))).run();
		if (oracleAT.getStatus() == AbstractSolution.Status.OPTIMAL && oracleBT.getStatus() == AbstractSolution.Status.OPTIMAL) {
			double upperBound = oracleAT.getCost() + oracleBT.getCost();
			System.out.println("Upper bound : " + upperBound + " Solution cost : " + solution.getCost());
			Assert.assertTrue("Path cost" + solution.getCost() + " is superior to upper bound AT+BT " + upperBound, Math.round(solution.getCost()) <= Math.round(upperBound));
			Assert.assertEquals("Origin node of AO differs from userA", solution.getInputData().getUser_A(), solution.getPath_A().getOrigin());
			Assert.assertEquals("Origin node of BO differs from userB", solution.getInputData().getUser_B(), solution.getPath_B().getOrigin());
			Assert.assertEquals("Incorrect end node of path OT,should be destination", solution.getInputData().getDestination(), solution.getCommonPath().getDestination());
			Assert.assertEquals("Destination nodes of AO and BO differs", solution.getPath_A().getDestination(), solution.getPath_B().getDestination());
		} else {
			Assert.assertEquals("Algorithm end status incorrect, should be INFEASIBLE", AbstractSolution.Status.INFEASIBLE, solution.getStatus());
		}


	}


	protected void carPoolingTest(Graph graph, Node userA, Node userB, Node destination) {
		System.out.println("\nTEST START");
		System.out.println("A : " + userA.getId());
		System.out.println("B : " + userB.getId());
		System.out.println("T : " + destination.getId());
		ArcInspector defaultInspector = ArcInspectorFactory.getAllFilters().get(0);
		CarPoolingData data = new CarPoolingData(graph, userA, userB, destination, defaultInspector);
		CarPoolingSolution solution = instanciateAlgorithm(data).run();
		System.out.println("O : " + ((solution.getJunctionNode() != null) ? solution.getJunctionNode().getId() : null));
		validityTest(graph, solution);
		segmentOptimalityTest(graph, solution);


	}

	protected void carPoolingTest(String mapName, int userA, int userB, int destination) throws IOException {
		Graph graph = new BinaryGraphReader(
				new DataInputStream(new BufferedInputStream(new FileInputStream(mapName)))).read();
		Node a = graph.get(userA);
		Node b = graph.get(userB);
		Node t = graph.get(destination);
		carPoolingTest(graph, a, b, t);

	}

	@Test
	public void simpleGraphTest() {
		for (int i = 0; i < nodes.length; i++) {
			for (int j = 0; j < nodes.length; j++) {
				for (int k = 0; k < nodes.length; k++) {
					carPoolingTest(simpleGraph, nodes[i], nodes[j], nodes[k]);
				}
			}
		}

	}

	@Test
	public void squareGraphTest() {
		for (int i = 0; i < 15; i++) {
			for (int j = 0; j < 15; j++) {
				for (int k = 0; k < 15; k++) {
					int i1 = new Random().nextInt(squareMapGraph.size());
					int j1 = new Random().nextInt(squareMapGraph.size());
					int k1 = new Random().nextInt(squareMapGraph.size());
					carPoolingTest(squareMapGraph, squareMapGraph.getNodes().get(i1), squareMapGraph.getNodes().get(j1), squareMapGraph.getNodes().get(k1));
				}
			}
		}
	}

	public void oracleTest(Graph graph, Node user_a, Node user_b, Node target) {
		System.out.println("TESTING A " + user_a.getId() + " B " + user_b.getId() + " T " + target.getId());
		ArcInspector arcInspector = ArcInspectorFactory.getAllFilters().get(0);
		CarPoolingData data = new CarPoolingData(graph, user_a, user_b, target, arcInspector);
		CarPoolingSolution oracle = instanciateOracle(data).run();
		CarPoolingSolution result = instanciateAlgorithm(data).run();

		Assert.assertEquals("Oracle and Algorithm finished with different status", oracle.getStatus(), result.getStatus());
		Assert.assertTrue("Oracle and algorithm have different path length. Should be " + oracle.getCost() + " is " + result.getCost(), oracle.getCost() == result.getCost());

	}


	public void oracleAllPointsTest(Graph graph) {
		for (int i = 0; i < graph.size(); i++) {
			for (int j = 0; j < graph.size(); j++) {
				for (int k = 0; k < graph.size(); k++) {

					oracleTest(graph, graph.getNodes().get(i), graph.getNodes().get(j), graph.getNodes().get(k));

				}
			}
		}

	}


	@Test
	public void simpleGraphOracleTest() {
		oracleAllPointsTest(simpleGraph);
	}

	//Test cases on various maps, see performance tests for scenarii details


	@Test
	public void midiPyrennesTest() {

		try {
			System.out.println("MIDI PYRENNEES");
			carPoolingTest(midi_pyrenees, 315603, 243724, 109237);
			carPoolingTest(midi_pyrenees, 447547, 361305, 76760);
			carPoolingTest(midi_pyrenees, 496160, 406219, 613829);
			carPoolingTest(midi_pyrenees, 496160, 406219, 613829);
			carPoolingTest(midi_pyrenees, 225084, 345135, 480971);
		} catch (IOException e) {
			Assume.assumeTrue(false); //If map files are not present on the computer, ignore the test
		}

	}

	@Test
	public void californiaTest() {
		try {
			System.out.println("CALIFORNIA");
			;
			carPoolingTest(california, 1417789, 288028, 1020192);
			carPoolingTest(california, 792971, 766918, 782081);
		} catch (IOException e) {
			Assume.assumeTrue(false); //If map files are not present on the computer, ignore the test
		}

	}

	@Test
	public void belgiumTest() {
		try {
			System.out.println("BELGIUM");
			carPoolingTest(belgium, 516403, 882812, 84661);
			carPoolingTest(belgium, 396597, 461420, 532320);
		} catch (IOException e) {
			Assume.assumeTrue(false); //If map files are not present on the computer, ignore the test
		}

	}

	@Test
	public void newZealandTest() {
		try {
			System.out.println("NEW ZEALAND");
			carPoolingTest(newZealand, 230743, 204261, 250006);
		} catch (IOException e) {
			Assume.assumeTrue(false); //If map files are not present on the computer, ignore the test
		}
	}

}
