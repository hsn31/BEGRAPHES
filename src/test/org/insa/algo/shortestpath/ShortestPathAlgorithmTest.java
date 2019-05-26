package org.insa.algo.shortestpath;

import org.insa.algo.AbstractInputData;
import org.insa.algo.AbstractSolution;
import org.insa.algo.ArcInspector;
import org.insa.algo.ArcInspectorFactory;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import static org.junit.Assert.*;


public abstract class ShortestPathAlgorithmTest {
	// Simple Test graph from subject
	private static Graph graph;
	private static Node[] nodes;

	// "Wall" graph to mess with AStar
	public static Graph wallGraph;
	private static Node[][] wallGraphNodes;
	public static int wallWidth = 1000;
	public static int wallHeight = 100;

	//"BottleNeck" graph to mess even more
	private static Graph bottleNeckGraph;
	private static Node[][] bottleNeckGraphNodes;


	//Square Map
	private static Graph squareMapGraph;
	//private static String squareMapName = "/home/decaeste/Bureau/commetud/3eme Annee MIC/Graphes-et-Algorithmes/Maps/carre.mapgr";
	private static String squareMapName = "C:\\Users\\Brice\\Desktop\\carre.mapgr";

	//private static String squareMapName ="D:\\T�l�chargements\\carre.mapgr";


	//Guadeloup Map
	private static String guadeloupMap = "/home/decaeste/Bureau/commetud/3eme Annee MIC/Graphes-et-Algorithmes/Maps/guadeloupe.mapgr";

	//Toulouse Map
	private static String toulouseMapName = "/home/decaeste/Bureau/commetud/3eme Annee MIC/Graphes-et-Algorithmes/Maps/toulouse.mapgr";


	//INSA Map
	private static String insaMapName = "/home/decaeste/Bureau/commetud/3eme Annee MIC/Graphes-et-Algorithmes/Maps/insa.mapgr";

	//HG Map
	private static String hgMapName = "/home/decaeste/Bureau/commetud/3eme Annee MIC/Graphes-et-Algorithmes/Maps/haute-garonne.mapgr";

	//NZ Map
	private static String NZMap = "/home/decaeste/Bureau/commetud/3eme Annee MIC/Graphes-et-Algorithmes/Maps/new-zealand.mapgr";


	// List of arcs in the graph, x1_x2 is the arc from node x1 (0) to x2 (1).
	@SuppressWarnings("unused")
	private static Arc x1_x2, x1_x3, x2_x4, x2_x5, x2_x6, x3_x1, x3_x6, x3_x2, x6_x5, x5_x3, x5_x4, x5_x6;

	private static ArcInspector defaultArcInspector;
	private static ArcInspector mapArcInspector;


	//Method to override to define a concrete test class
	public abstract ShortestPathAlgorithm instanciateAlgorithm(ShortestPathData shortestPathData);

	@BeforeClass
	public static void initAll() throws IOException {


		//Init the subject graph
		RoadInformation speed10 = new RoadInformation(RoadInformation.RoadType.MOTORWAY, null, true, 1, "");
		// Create nodes
		nodes = new Node[6];
		// Create graph from subject
		for (int i = 0; i < nodes.length; ++i) {
			nodes[i] = new Node(i, new Point(0, 0));
		}
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

		//Init the square graph
		// Create a graph reader.
		GraphReader reader = new BinaryGraphReader(
				new DataInputStream(new BufferedInputStream(new FileInputStream(squareMapName))));
		//Read the graph.
		squareMapGraph = reader.read();
		mapArcInspector = ArcInspectorFactory.getAllFilters().get(0);

		//Init the wall graph
		wallGraphNodes = new Node[wallWidth][wallHeight];
		ArrayList<Node> toAdd = new ArrayList<>();
		int id = 0;
		for (int x = 0; x < wallWidth; x++) {
			for (int y = 0; y < wallHeight; y++) {
				wallGraphNodes[x][y] = new Node(id++, new Point(x, y));
				toAdd.add(wallGraphNodes[x][y]);
				if (x > 0 && ((x != 1 && x != wallWidth - 2) || x == 0 || x == wallWidth - 1)) {//Link horizontally except on two columns. For this column, only link hozirontally the top and bottom nodes
					Node.linkNodes(wallGraphNodes[x][y], wallGraphNodes[x - 1][y], 1, speed10, null);
					Node.linkNodes(wallGraphNodes[x - 1][y], wallGraphNodes[x][y], 1, speed10, null);
				}
				if (y > 0 && x != wallWidth - 2 && x != 1 && (y != 1 || x == 0 || x == wallWidth - 1)) {
					Node.linkNodes(wallGraphNodes[x][y], wallGraphNodes[x][y - 1], 1, speed10, null);
					Node.linkNodes(wallGraphNodes[x][y - 1], wallGraphNodes[x][y], 1, speed10, null);
				}

			}
		}
		wallGraph = new Graph("WAL", "Wall map", toAdd, new GraphStatistics(null, 0, 0, 130, 2));


		bottleNeckGraphNodes = new Node[wallWidth][wallHeight];
		toAdd = new ArrayList<>();
		id = 0;
		//Init the bottleneck graph
		for (int x = 0; x < wallWidth; x++) {
			for (int y = 0; y < wallHeight; y++) {
				bottleNeckGraphNodes[x][y] = new Node(id++, new Point(x, y));
				toAdd.add(bottleNeckGraphNodes[x][y]);
				if (x > 0 && ((x != 1 && x != wallWidth - 2) || x == 0 || x == wallWidth - 1)) {//Link horizontally except on two columns. For this column, only link hozirontally the top and bottom nodes
					Node.linkNodes(bottleNeckGraphNodes[x][y], bottleNeckGraphNodes[x - 1][y], 1, speed10, null);
					Node.linkNodes(bottleNeckGraphNodes[x - 1][y], bottleNeckGraphNodes[x][y], 1, speed10, null);
				}
				if (y > 0 && x != wallWidth - 2 && x != 1 && (y != 1 || x == 0 || x == wallWidth - 1)) {
					Node.linkNodes(bottleNeckGraphNodes[x][y], bottleNeckGraphNodes[x][y - 1], 1, speed10, null);
					Node.linkNodes(bottleNeckGraphNodes[x][y - 1], bottleNeckGraphNodes[x][y], 1, speed10, null);
				}

			}
		}
		Node.linkNodes(bottleNeckGraphNodes[wallWidth / 2][0], bottleNeckGraphNodes[wallWidth / 2][1], wallHeight * wallWidth, speed10, null);
		bottleNeckGraph = new Graph("BTL", "Bottleneck", toAdd, new GraphStatistics(null, 0, 0, 130, 2));


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
				return AbstractInputData.Mode.LENGTH;
			}
		};


	}


	//Bellman Ford algorithm as Oracle
	public ShortestPathAlgorithm instanciateOracle(ShortestPathData shortestPathData) {
		return new BellmanFordAlgorithm(shortestPathData);
	}


	//Test SHORTEST scenario
	public void simpleGraphWithOraclePathTest(int from, int to) {

		ShortestPathData shortestPathData = new ShortestPathData(graph, nodes[from], nodes[to], defaultArcInspector);
		ShortestPathAlgorithm oracleAlgorithm = instanciateOracle(shortestPathData);
		ShortestPathAlgorithm algorithm = instanciateAlgorithm(shortestPathData);
		ShortestPathSolution oracleSolution = oracleAlgorithm.doRun();
		ShortestPathSolution solution = algorithm.doRun();

		if (from != to) {
			assertEquals("Algorithms finished with different status", oracleSolution.getStatus(), solution.getStatus());
			assertTrue("End status incorrect", AbstractSolution.Status.OPTIMAL == solution.getStatus() || solution.getStatus() == AbstractSolution.Status.INFEASIBLE);


			//Assume.assumeTrue(solution.getStatus() != AbstractSolution.Status.INFEASIBLE);
			if (solution.getStatus() != AbstractSolution.Status.INFEASIBLE) {
				assertEquals("Oracle and Algorithm solution give differrent number of nodes in path", oracleSolution.getPath().size(), solution.getPath().size());
				assertTrue("Different lenghth for Oracle solution and Algorithm solution", oracleSolution.getPath().getLength() == solution.getPath().getLength());
				for (int i = 0; i < oracleSolution.getPath().getArcs().size(); i++) {
					assertEquals("Different arcs founded for Algorithm and Oracle solutions", oracleSolution.getPath().getArcs().get(i), solution.getPath().getArcs().get(i));
				}
			}
		}


	}

	//Test with every pair of nodes in the simple graph
	@Test
	public void algorithmSimpleGraphWithOracleTest() {
		for (int from = 0; from < nodes.length; from++) {
			for (int to = 0; to < nodes.length; to++) {
				simpleGraphWithOraclePathTest(from, to);
			}
		}

	}

	@Test
	public void algorithmSquareMapWithOracleRandomTest() {
		Random random = new Random();
		for (int i = 0; i < 10; i++) {
			int from = random.nextInt(squareMapGraph.size());
			int to = random.nextInt(squareMapGraph.size());
			System.out.println("FROM: " + from + " TO " + to);
			ShortestPathData shortestPathData = new ShortestPathData(squareMapGraph, squareMapGraph.get(from), squareMapGraph.get(to), mapArcInspector);
			ShortestPathAlgorithm oracleAlgorithm = instanciateOracle(shortestPathData);
			ShortestPathAlgorithm algorithm = instanciateAlgorithm(shortestPathData);

			ShortestPathSolution oracleSolution = oracleAlgorithm.doRun();
			ShortestPathSolution solution = algorithm.doRun();

			if (from != to) {
				assertEquals("Oracle and Algorithm finished with different status on map " + squareMapName, oracleSolution.getStatus(), solution.getStatus());
				assertTrue("End status incorrect,should be INFEASIBLE or OPTIMAL, is " + solution.getStatus().toString(), AbstractSolution.Status.OPTIMAL == solution.getStatus() || solution.getStatus() == AbstractSolution.Status.INFEASIBLE);
				if (solution.getStatus() != AbstractSolution.Status.INFEASIBLE) {
					assertTrue("Oracle and algorithm solution have different cost", oracleSolution.getCost() == solution.getCost());
				}

			}
		}


	}

	//Test de la distance et du temps avec Oracle

	public void algorithmMapWithOracleTestDistanceOrTime(Graph graph, int typeEvaluation, int origine, int destination) {
		//Soit temps =0, soit distance =1.


		ArcInspector arcInspector;

		if (typeEvaluation == 0) {
			System.out.println("Mode : Temps");
			arcInspector = ArcInspectorFactory.getAllFilters().get(2);
		} else {
			System.out.println("Mode : Distance");
			arcInspector = ArcInspectorFactory.getAllFilters().get(0);
		}

		System.out.println("Origine : " + origine);
		System.out.println("Destination : " + destination);

		ShortestPathData data = new ShortestPathData(graph, graph.get(origine), graph.get(destination), arcInspector);

		ShortestPathAlgorithm oracleAlgorithm = instanciateOracle(data);
		ShortestPathAlgorithm algorithm = instanciateAlgorithm(data);


		ShortestPathSolution solution = algorithm.doRun();
		ShortestPathSolution oracleSolution = oracleAlgorithm.doRun();

		if (origine < 0 || destination < 0 || origine > (graph.size() - 1) || destination > (graph.size() - 1)) {
			//Hors du graph

			System.out.println("ERREUR : Param�tres invalides ");
			assertEquals("End status incorrect" + solution.getStatus().toString(), solution.getStatus(), AbstractSolution.Status.INFEASIBLE);

		} else if (origine == destination) {
			System.out.println("Origine et Destination identiques");
			assertEquals("Solution status should be OPTIMAL", AbstractSolution.Status.OPTIMAL, solution.getStatus());
			assertEquals("Path size should be 0", 0, solution.getPath().getArcs().size());

		} else if (solution.getPath() == null) {
			assertEquals(oracleSolution.getPath(), solution.getPath());

		} else {
			assertEquals("Algorithm and oracle path have different number of nodes", oracleSolution.getPath().size(), solution.getPath().size());
			double costSolution;
			double costExpected;
			if (typeEvaluation == 0) {
				//Time
				costSolution = solution.getPath().getMinimumTravelTime();
				costExpected = oracleSolution.getPath().getMinimumTravelTime();

			} else {
				costSolution = solution.getPath().getLength();
				costExpected = oracleSolution.getPath().getLength();
			}

			assertTrue("Expected cost was" + costExpected + "actual is" + costSolution, costExpected == costSolution);
			assertTrue("Solution path should be valid", solution.getPath().isValid());

		}
	}

	public void algorithmMapWithOracleTestDistanceOrTime(String mapName, int typeEvaluation, int origine, int destination) throws IOException {
		Graph graph = new BinaryGraphReader(new DataInputStream(new BufferedInputStream(new FileInputStream(mapName)))).read();
		algorithmMapWithOracleTestDistanceOrTime(graph, typeEvaluation, origine, destination);
	}


	public void algorithmOutOfGrapheTest(String mapName, int origine, int destination) throws IOException {
		GraphReader reader = new BinaryGraphReader(
				new DataInputStream(new BufferedInputStream(new FileInputStream(mapName))));

		Graph graph = reader.read();
		ArcInspector arcInspector = ArcInspectorFactory.getAllFilters().get(2);
		try {
			ShortestPathData data = new ShortestPathData(graph, graph.get(origine), graph.get(destination),
					arcInspector);
			fail();
		} catch (IndexOutOfBoundsException e) {

		}

	}

	//Exactement le meme Test visant la distance et le temps mais sans Oracle

	public void algorithmMapWithoutOracleTest(String mapName, int origine, int destination) throws IOException {

		double costFastestSolutionInTime = Double.POSITIVE_INFINITY;
		double costFastestSolutionInDistance = Double.POSITIVE_INFINITY;
		double costShortestSolutionInTime = Double.POSITIVE_INFINITY;
		double costShortestSolutionInDistance = Double.POSITIVE_INFINITY;

		GraphReader reader = new BinaryGraphReader(
				new DataInputStream(new BufferedInputStream(new FileInputStream(mapName))));

		Graph graph = reader.read();

		/* Recuperation de la solution de Algorithm en mode FASTEST*/
		ArcInspector arcInspector = ArcInspectorFactory.getAllFilters().get(2);
		ShortestPathData data = new ShortestPathData(graph, graph.get(origine), graph.get(destination),
				arcInspector);

		ShortestPathAlgorithm algorithm = instanciateAlgorithm(data);

		ShortestPathSolution solutionFastest = algorithm.doRun();


		/* Recuperation de la solution de Algorithm en mode SHORTEST*/
		arcInspector = ArcInspectorFactory.getAllFilters().get(0);
		data = new ShortestPathData(graph, graph.get(origine), graph.get(destination), arcInspector);
		algorithm = instanciateAlgorithm(data);
		ShortestPathSolution solutionShortest = algorithm.doRun();

		Assert.assertEquals("Both fastest and shortest solution shoudl have the same end status", solutionFastest.getStatus(), solutionShortest.getStatus());

		if (origine < 0 || destination < 0 || origine > (graph.size() - 1) || destination > (graph.size() - 1)) {
			//Hors du graph

			System.out.println("ERREUR : Param�tres invalides ");
			assertTrue("End status incorrect,should be INFEASIBLE or OPTIMAL, is " + solutionFastest.getStatus().toString(), solutionFastest.getStatus() == AbstractSolution.Status.INFEASIBLE);

		} else if (origine == destination) {
			System.out.println("Origine et Destination identiques"); //cf conv messenger du 24/04/2019
			assertEquals(AbstractSolution.Status.OPTIMAL, solutionFastest.getStatus());
			assertEquals(solutionFastest.getPath().getArcs().size(), 0);


		} else if (solutionFastest.getPath() == null) {
			assertEquals("No path found, end status should be INFEASIBLE", AbstractSolution.Status.INFEASIBLE, solutionFastest.getStatus());
		} else {

			costFastestSolutionInTime = solutionFastest.getPath().getMinimumTravelTime();
			costFastestSolutionInDistance = solutionFastest.getPath().getLength();
			costShortestSolutionInDistance = solutionShortest.getPath().getLength();
			costShortestSolutionInTime = solutionShortest.getPath().getMinimumTravelTime();
			assertTrue(costFastestSolutionInTime <= costShortestSolutionInTime);
			assertTrue(costFastestSolutionInDistance >= costShortestSolutionInDistance);

		}
		System.out.println();
		System.out.println();
	}

	//DEBUT DES TESTS

	@Test
	public void testScenarioDistanceHG() throws IOException {

		try {
			String mapName = hgMapName;

			int origine;
			int destination;
			System.out.println("*****************************************************");
			System.out.println("----- Test de validit� avec oracle sur une carte-----");
			System.out.println("----- Carte : Haute-Garonne -------------------------");
			System.out.println("----- Mode : DISTANCE -------------------------------");
			System.out.println();

			System.out.println("----- Cas d'un chemin nul ------");
			origine = 0;
			destination = 0;
			algorithmMapWithOracleTestDistanceOrTime(mapName, 1, origine, destination);

			System.out.println("----- Cas d'un chemin simple ------");
			origine = 38926;
			destination = 59015;
			algorithmMapWithOracleTestDistanceOrTime(mapName, 1, origine, destination);
		} catch (IOException e) {
			Assume.assumeTrue(false); //If map files are not present on the computer, ignore the test
		}
	}


	@Test
	public void testScenarioTempsHG() throws Exception {

		try {
			String mapName = hgMapName;

			int origine;
			int destination;
			System.out.println("*****************************************************");
			System.out.println("----- Test de validit� avec oracle sur une carte-----");
			System.out.println("----- Carte : Haute-Garonne -------------------------");
			System.out.println("----- Mode : TEMPS ----------------------------------");
			System.out.println();

			System.out.println("----- Cas d'un chemin nul ------");
			origine = 0;
			destination = 0;
			algorithmMapWithOracleTestDistanceOrTime(mapName, 0, origine, destination);

			System.out.println("----- Cas d'un chemin simple ------");
			origine = 99490;
			destination = 85265;
			algorithmMapWithOracleTestDistanceOrTime(mapName, 0, origine, destination);
		} catch (IOException e) {
			Assume.assumeTrue(false); //If map files are not present on the computer, ignore the test
		}

	}

	@Test
	public void testScenarioDistanceINSA() throws Exception {
		try {
			String mapName = insaMapName;


			int origine;
			int destination;
			System.out.println("*****************************************************");
			System.out.println("----- Test de validit� avec oracle sur une carte-----");
			System.out.println("----- Carte : INSA ----------------------------------");
			System.out.println("----- Mode : DISTANCE -------------------------------");
			System.out.println();

			System.out.println("----- Cas d'un chemin nul ------");
			origine = 300;
			destination = 300;
			algorithmMapWithOracleTestDistanceOrTime(mapName, 1, origine, destination);

			System.out.println("----- Cas d'un chemin simple ------");
			origine = 607;
			destination = 857;
			algorithmMapWithOracleTestDistanceOrTime(mapName, 1, origine, destination);
		} catch (IOException e) {
			Assume.assumeTrue(false); //If map files are not present on the computer, ignore the test
		}

	}

	@Test
	public void testScenarioTempsINSA() throws Exception {
		try {
			String mapName = insaMapName;


			int origine;
			int destination;
			System.out.println("*****************************************************");
			System.out.println("----- Test de validit� avec oracle sur une carte-----");
			System.out.println("----- Carte : INSA ----------------------------------");
			System.out.println("----- Mode : TEMPS ----------------------------------");
			System.out.println();

			System.out.println("----- Cas d'un chemin nul ------");
			origine = 300;
			destination = 300;
			algorithmMapWithOracleTestDistanceOrTime(mapName, 0, origine, destination);

			System.out.println("----- Cas d'un chemin simple ------");
			origine = 44;
			destination = 541;
			algorithmMapWithOracleTestDistanceOrTime(mapName, 0, origine, destination);
		} catch (IOException e) {
			Assume.assumeTrue(false); //If map files are not present on the computer, ignore the test
		}

	}

	@Test
	public void testScenarioDistanceCarre() throws Exception {
		try {
			String mapName = squareMapName;


			int origine;
			int destination;
			System.out.println("*****************************************************");
			System.out.println("----- Test de validit� avec oracle sur une carte-----");
			System.out.println("----- Carte : CARRE ---------------------------------");
			System.out.println("----- Mode : DISTANCE -------------------------------");
			System.out.println();

			System.out.println("----- Cas d'un chemin simple ------");
			origine = 0;
			destination = 20;
			algorithmMapWithOracleTestDistanceOrTime(mapName, 1, origine, destination);
		} catch (IOException e) {
			Assume.assumeTrue(false); //If map files are not present on the computer, ignore the test
		}
	}


	@Test
	public void testScenarioTempsCarre() throws Exception {
		try {
			String mapName = squareMapName;


			int origine;
			int destination;
			System.out.println("*****************************************************");
			System.out.println("----- Test de validit� avec oracle sur une carte-----");
			System.out.println("----- Carte : CARRE ---------------------------------");
			System.out.println("----- Mode : TEMPS ----------------------------------");
			System.out.println();

			System.out.println("----- Cas d'un chemin simple ------");
			origine = 0;
			destination = 12;
			algorithmMapWithOracleTestDistanceOrTime(mapName, 0, origine, destination);
		} catch (IOException e) {
			Assume.assumeTrue(false); //If map files are not present on the computer, ignore the test
		}
	}


	@Test
	public void testScenarioDistanceGuadeloupe() throws Exception {
		try {
			String mapName = guadeloupMap;


			int origine;
			int destination;
			System.out.println("*****************************************************");
			System.out.println("----- Test de validit� avec oracle sur une carte-----");
			System.out.println("----- Carte : GUADELOUPE ----------------------------");
			System.out.println("----- Mode : DISTANCE -------------------------------");
			System.out.println();

			System.out.println("----- Cas d'un chemin simple ------");
			origine = 9922;
			destination = 34328;
			algorithmMapWithOracleTestDistanceOrTime(mapName, 1, origine, destination);

			System.out.println("----- Cas de sommets non connexes ------");
			origine = 9950;
			destination = 15860;
			algorithmMapWithOracleTestDistanceOrTime(mapName, 1, origine, destination);
		} catch (IOException e) {
			Assume.assumeTrue(false); //If map files are not present on the computer, ignore the test
		}

	}

	@Test
	public void testScenarioTempsGuadeloupe() throws Exception {
		try {
			String mapName = guadeloupMap;


			int origine;
			int destination;
			System.out.println("*****************************************************");
			System.out.println("----- Test de validit� avec oracle sur une carte-----");
			System.out.println("----- Carte : GUADELOUPE ----------------------------");
			System.out.println("----- Mode : TEMPS ----------------------------------");
			System.out.println();

			System.out.println("----- Cas d'un chemin simple ------");
			origine = 9922;
			destination = 34328;
			algorithmMapWithOracleTestDistanceOrTime(mapName, 10, origine, destination);

			System.out.println("----- Cas de sommets non connexes ------");
			origine = 9950;
			destination = 15860;
			algorithmMapWithOracleTestDistanceOrTime(mapName, 0, origine, destination);
		} catch (IOException e) {
			Assume.assumeTrue(false); //If map files are not present on the computer, ignore the test
		}

	}


	@Test
	public void testScenarioMinTempsDistHG() throws Exception {
		try {
			String mapName = hgMapName;


			int origine;
			int destination;
			System.out.println("*****************************************************");
			System.out.println("----- Test de validit� sans oracle sur une carte-----");
			System.out.println("----- Carte : Haute-Garonne -------------------------");
			System.out.println();

			System.out.println("----- Cas d'un chemin nul ------");
			origine = 0;
			destination = 0;
			algorithmMapWithoutOracleTest(mapName, origine, destination);

			System.out.println("----- Cas d'un chemin nul ------");
			origine = 4;
			destination = 4;
			algorithmMapWithoutOracleTest(mapName, origine, destination);

			System.out.println("----- Cas d'un chemin simple ------");
			origine = 38926;
			destination = 59015;
			algorithmMapWithoutOracleTest(mapName, origine, destination);
		} catch (IOException e) {
			Assume.assumeTrue(false); //If map files are not present on the computer, ignore the test
		}


	}

	@Test
	public void testScenarioMinTempsDistCarreDense() throws Exception {
		try {
			String mapName = squareMapName;


			int origine;
			int destination;
			System.out.println("*****************************************************");
			System.out.println("----- Test de validit� sans oracle sur une carte-----");
			System.out.println("----- Carte : CARRE ---------------------------");
			System.out.println();

			System.out.println("----- Cas d'un chemin simple ------");
			origine = 0;
			destination = 10;
			algorithmMapWithoutOracleTest(mapName, origine, destination);
		} catch (IOException e) {
			Assume.assumeTrue(false); //If map files are not present on the computer, ignore the test
		}

	}

	@Test
	public void testScenarioToulouse() {
		try {
			String mapName = toulouseMapName;
			int origine;
			int destination;
			System.out.println("*****************************************************");
			System.out.println("----- Test de validité avec oracle sur une carte-----");
			System.out.println("----- Carte : TOULOUSE ---------------------------");
			System.out.println();
			System.out.println("----- Use periph ------");
			origine = 22596;
			destination = 13986;
			algorithmMapWithOracleTestDistanceOrTime(mapName, 0, origine, destination);
			algorithmMapWithOracleTestDistanceOrTime(mapName, 1, origine, destination);

			System.out.println("*****************************************************");
			System.out.println("----- Test de validité avec oracle sur une carte-----");
			System.out.println("----- Carte : TOULOUSE ---------------------------");
			System.out.println();
			System.out.println("----- Opposite ------");
			origine = 9888;
			destination = 32174;
			algorithmMapWithOracleTestDistanceOrTime(mapName, 0, origine, destination);
			algorithmMapWithOracleTestDistanceOrTime(mapName, 1, origine, destination);

			System.out.println("*****************************************************");
			System.out.println("----- Test de validité avec oracle sur une carte-----");
			System.out.println("----- Carte : TOULOUSE ---------------------------");
			System.out.println();
			System.out.println("----- Close to graph center ------");
			origine = 4460;
			destination = 1608;
			algorithmMapWithOracleTestDistanceOrTime(mapName, 0, origine, destination);
			algorithmMapWithOracleTestDistanceOrTime(mapName, 1, origine, destination);

			System.out.println("*****************************************************");
			System.out.println("----- Test de validité avec oracle sur une carte-----");
			System.out.println("----- Carte : TOULOUSE ---------------------------");
			System.out.println();
			System.out.println("----- Mandatory bridge Garonne ------");
			origine = 2767;
			destination = 14179;
			algorithmMapWithOracleTestDistanceOrTime(mapName, 0, origine, destination);
			algorithmMapWithOracleTestDistanceOrTime(mapName, 1, origine, destination);
		} catch (IOException e) {
			Assume.assumeTrue(false); //If map files are not present on the computer, ignore the test
		}
	}

	@Test
	public void testScenarioNewZealand() {
		try {
			String mapName = NZMap;
			int origine;
			int destination;
			System.out.println("*****************************************************");
			System.out.println("----- Test de validité avec oracle sur une carte-----");
			System.out.println("----- Carte : NEW ZEALAND ---------------------------");
			System.out.println();
			System.out.println("----- Reachable ------");
			origine = 230743;
			destination = 250006;
			algorithmMapWithOracleTestDistanceOrTime(mapName, 0, origine, destination);
			algorithmMapWithOracleTestDistanceOrTime(mapName, 1, origine, destination);

			System.out.println("*****************************************************");
			System.out.println("----- Test de validité avec oracle sur une carte-----");
			System.out.println("----- Carte : NEW ZEALAND ---------------------------");
			System.out.println();
			System.out.println("----- Unreachable ------");
			origine = 204261;
			destination = 250006;
			algorithmMapWithOracleTestDistanceOrTime(mapName, 0, origine, destination);
			algorithmMapWithOracleTestDistanceOrTime(mapName, 1, origine, destination);
		} catch (IOException e) {
			Assume.assumeTrue(false); //If map files are not present on the computer, ignore the test
		}
	}

	@Test
	public void testScenarioMinTempsDistGuadeloupe() throws IOException {
		try {
			String mapName = guadeloupMap;


			int origine;
			int destination;
			System.out.println("*****************************************************");
			System.out.println("----- Test de validit� sans oracle sur une carte-----");
			System.out.println("----- Carte : GUADELOUPE ----------------------------");
			System.out.println();

			System.out.println("----- Cas d'un chemin simple ------");
			origine = 9922;
			destination = 3428;
			algorithmMapWithoutOracleTest(mapName, origine, destination);

			System.out.println("----- Cas de sommets non connexes ------");
			origine = 9950;
			destination = 15860;
			algorithmMapWithoutOracleTest(mapName, origine, destination);
		} catch (IOException e) {
			Assume.assumeTrue(false); //If map files are not present on the computer, ignore the test
		}
	}

	@Test
	public void testScenarioMinTempsDistWall() {


		int origine;
		int destination;
		System.out.println("*****************************************************");
		System.out.println("----- Test de validit� sans oracle sur une carte-----");
		System.out.println("----- Carte : WALLS----------------------------");
		System.out.println();

		System.out.println("----- Proche en euclidien, loin en distance ------");
		origine = wallWidth * 2 + wallWidth / 2; //Middle of third row
		destination = wallWidth / 2;//Middle of first row
		algorithmMapWithOracleTestDistanceOrTime(wallGraph, 0, origine, destination);
		algorithmMapWithOracleTestDistanceOrTime(wallGraph, 1, origine, destination);


		System.out.println("----- Départ sur un noeud isolé ------");
		origine = wallWidth + 1;//Second row second column, not linked
		destination = wallWidth * wallHeight / 2 + wallWidth / 2;//Middle of the graph
		algorithmMapWithOracleTestDistanceOrTime(wallGraph, 0, origine, destination);
		algorithmMapWithOracleTestDistanceOrTime(wallGraph, 1, origine, destination);

		System.out.println("----- Destination sur un noeud isolé ------");
		origine = wallWidth / 2;//First row middle
		destination = wallWidth + 1;//Second row second column, not linked
		algorithmMapWithOracleTestDistanceOrTime(wallGraph, 0, origine, destination);
		algorithmMapWithOracleTestDistanceOrTime(wallGraph, 1, origine, destination);


	}
	/*
	@Test
	public void testScenarioMinTempsDistBottleNeck() throws Exception {


		int origine;
		int destination;
		System.out.println("*****************************************************");
		System.out.println("----- Test de validit� sans oracle sur une carte-----");
		System.out.println("----- Carte : WALLS----------------------------");
		System.out.println();

		System.out.println("----- Proche en euclidien, loin en distance ------");
		origine = wallWidth *2+ wallWidth /2; //Middle of third row
		destination = wallWidth /2;//Middle of first row
		algorithmMapWithOracleTestDistanceOrTime(bottleNeckGraph,0, origine, destination);
		algorithmMapWithOracleTestDistanceOrTime(bottleNeckGraph,1, origine, destination);


		System.out.println("----- Départ sur un noeud isolé ------");
		origine = wallWidth +1;//Second row second column, not linked
		destination = wallWidth * wallHeight /2+ wallWidth /2;//Middle of the graph
		algorithmMapWithOracleTestDistanceOrTime(bottleNeckGraph,0, origine, destination);
		algorithmMapWithOracleTestDistanceOrTime(bottleNeckGraph,1, origine, destination);

		System.out.println("----- Destination sur un noeud isolé ------");
		origine = wallWidth/2;//First row middle
		destination = wallWidth+1;//Second row second column, not linked
		algorithmMapWithOracleTestDistanceOrTime(bottleNeckGraph,0, origine, destination);
		algorithmMapWithOracleTestDistanceOrTime(bottleNeckGraph,1, origine, destination);


	}*/

}
