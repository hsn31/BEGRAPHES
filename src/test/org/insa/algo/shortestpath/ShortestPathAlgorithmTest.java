package org.insa.algo.shortestpath;

import org.insa.algo.AbstractInputData;
import org.insa.algo.AbstractSolution;
import org.insa.algo.ArcInspector;
import org.insa.algo.ArcInspectorFactory;
import org.insa.exception.NodeOutOfGraphException;
import org.insa.graph.*;
import org.insa.graph.io.BinaryGraphReader;
import org.insa.graph.io.GraphReader;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.internal.runners.statements.Fail;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;



public abstract class ShortestPathAlgorithmTest {
	// Simple Test graph from subject
	private static Graph graph;

	//Square Map
	private static Graph squareMapGraph;
	private static String squareMapName = "/home/decaeste/Bureau/commetud/3eme Annee MIC/Graphes-et-Algorithmes/Maps/carre.mapgr";
	//private static String squareMapName = "C:\\Users\\Brice\\Desktop\\carre.mapgr";
	
	//private static String squareMapName ="D:\\T�l�chargements\\carre.mapgr";


	//Guadeloup Map
	private static String guadeloupMap = "/home/decaeste/Bureau/commetud/3eme Annee MIC/Graphes-et-Algorithmes/Maps/guadeloupe.mapgr";

	//Toulouse Map
	private static String toulouseMapName = "/home/decaeste/Bureau/commetud/3eme Annee MIC/Graphes-et-Algorithmes/Maps/toulouse.mapgr";
	

	//INSA Map
	private static String insaMapName = "/home/decaeste/Bureau/commetud/3eme Annee MIC/Graphes-et-Algorithmes/Maps/insa.mapgr";
	
	//HG Map
	private static String hgMapName = "/home/decaeste/Bureau/commetud/3eme Annee MIC/Graphes-et-Algorithmes/Maps/haute-garonne.mapgr";

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
			nodes[i] = new Node(i, new Point(0,0));
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


//		// Create a graph reader.
		GraphReader reader = new BinaryGraphReader(
				new DataInputStream(new BufferedInputStream(new FileInputStream(squareMapName))));

		//Read the graph.
		squareMapGraph = reader.read();
		mapArcInspector = ArcInspectorFactory.getAllFilters().get(0);


	}
	public abstract ShortestPathAlgorithm instanciateAlgorithm(ShortestPathData shortestPathData) throws NodeOutOfGraphException;


	public ShortestPathAlgorithm instanciateOracle(ShortestPathData shortestPathData) throws NodeOutOfGraphException{
		return new BellmanFordAlgorithm(shortestPathData);
	}


	public void simpleGraphWithOraclePathTest(int from, int to) throws NodeOutOfGraphException {

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

	@Test
	public void algorithmSimpleGraphWithOracleTest() {
		for (int from = 0; from < nodes.length; from++) {
			for (int to = 0; to < nodes.length; to++) {
				try {
				simpleGraphWithOraclePathTest(from, to);
				}
				catch (NodeOutOfGraphException e) {
					// TODO: handle exception
				}
			}
		}

	}

	@Test
	public void algorithmMapWithOracleTest() throws NodeOutOfGraphException{
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

				//Assume.assumeTrue(solution.getStatus() != AbstractSolution.Status.INFEASIBLE);
				if (solution.getStatus() != AbstractSolution.Status.INFEASIBLE) {
					assertTrue(oracleSolution.getPath().getLength() == solution.getPath().getLength());
				}

			}
		}


	}

	//Test de la distance et du temps avec Oracle

	public void algorithmMapWithOracleTestDistanceOrTime(String mapName, int typeEvaluation, int origine, int destination) throws Exception {
		//Soit temps =0, soit distance =1.
		GraphReader reader = new BinaryGraphReader(
				new DataInputStream(new BufferedInputStream(new FileInputStream(mapName))));

		Graph graph = reader.read();

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
			assertTrue("End status incorrect,should be INFEASIBLE is " + solution.getStatus().toString(), solution.getStatus() == AbstractSolution.Status.INFEASIBLE);

		} else if (origine == destination) {
			System.out.println("Origine et Destination identiques"); //cf conv messenger du 24/04/2019
			assertTrue(AbstractSolution.Status.OPTIMAL == solution.getStatus());
			assertTrue("error" + solution.getPath().getArcs().size(), solution.getPath().getArcs().size() == 0);

		} else if (solution.getPath() == null) {
			assertEquals(oracleSolution.getPath(), solution.getPath());

		} else {
			assertEquals("Algorithm and oracle path have different number of nodes",oracleSolution.getPath().size(),solution.getPath().size());
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

			assertTrue("expected" + costExpected + "and was" + costSolution, costExpected == costSolution);
			assertEquals("Oracle and Algorithm solution give differrent number of nodes in path", oracleSolution.getPath().size(), solution.getPath().size());
			assertTrue("Different lenghth for Oracle solution and Algorithm solution", oracleSolution.getPath().getLength() == solution.getPath().getLength());
			assertEquals("Different arcs founded for Algorithm and Oracle solutions", oracleSolution.getPath().getArcs(), solution.getPath().getArcs());

		}

		System.out.println();
		System.out.println();
	}
	

	public void algorithmOutOfGrapheTest(String mapName, int origine, int destination) throws IOException{
		GraphReader reader = new BinaryGraphReader(
				new DataInputStream(new BufferedInputStream(new FileInputStream(mapName))));

		Graph graph = reader.read();
		ArcInspector arcInspector = ArcInspectorFactory.getAllFilters().get(2);
		try {
		ShortestPathData data = new ShortestPathData(graph, graph.get(origine), graph.get(destination),
				arcInspector);
		fail();
		}
		catch(IndexOutOfBoundsException e) {
			
		}
		
	}
	
	//Exactement le meme Test visant la distance et le temps mais sans Oracle

	public void algorithmMapWithoutOracleTest(String mapName, int origine, int destination) throws Exception {

		double costFastestSolutionInTime = Double.POSITIVE_INFINITY;
		double costFastestSolutionInDistance = Double.POSITIVE_INFINITY;
		double costShortestSolutionInTime = Double.POSITIVE_INFINITY;
		double costShortestSolutionInDistance = Double.POSITIVE_INFINITY;

		GraphReader reader = new BinaryGraphReader(
				new DataInputStream(new BufferedInputStream(new FileInputStream(mapName))));

		Graph graph = reader.read();

		/** Recherche du chemin le plus rapide **/
		ArcInspector arcInspector = ArcInspectorFactory.getAllFilters().get(2);

		ShortestPathData data = new ShortestPathData(graph, graph.get(origine), graph.get(destination),
				arcInspector);

		ShortestPathAlgorithm algorithm = instanciateAlgorithm(data);

		/* Recuperation de la solution de Algorithm */
		ShortestPathSolution solution = algorithm.doRun();

		if (origine < 0 || destination < 0 || origine > (graph.size() - 1) || destination > (graph.size() - 1)) {
			//Hors du graph

			System.out.println("ERREUR : Param�tres invalides ");
			assertTrue("End status incorrect,should be INFEASIBLE or OPTIMAL, is " + solution.getStatus().toString(), solution.getStatus() == AbstractSolution.Status.INFEASIBLE);

		} else if (origine == destination) {
			System.out.println("Origine et Destination identiques"); //cf conv messenger du 24/04/2019
			assertTrue(AbstractSolution.Status.OPTIMAL == solution.getStatus());
			assertEquals(solution.getPath().getArcs().size(), 0);


		} else if (solution.getPath() == null) {
			assertTrue(solution.getPath() == null);
		} else {

			costFastestSolutionInTime = solution.getPath().getMinimumTravelTime();
			costFastestSolutionInDistance = solution.getPath().getLength();


			arcInspector = ArcInspectorFactory.getAllFilters().get(0);

			data = new ShortestPathData(graph, graph.get(origine), graph.get(destination), arcInspector);

			algorithm = instanciateAlgorithm(data);

			solution = algorithm.doRun();


			if (solution.getPath() == null) {
				assertTrue(solution.getPath() == null);
			} else {
				costShortestSolutionInTime = solution.getPath().getMinimumTravelTime();
				costShortestSolutionInDistance = solution.getPath().getLength();
			}

			assertTrue(costFastestSolutionInTime <= costShortestSolutionInTime);

			assertTrue(costFastestSolutionInDistance >= costShortestSolutionInDistance);

		}
		System.out.println();
		System.out.println();
	}

	//DEBUT DES TESTS

	@Test
	public void testScenarioDistanceHG() throws Exception {

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


		System.out.println("----- Cas de sommets inexistants ------");
		System.out.println("----- Origine : N'existe pas ----------");
		System.out.println("----- Destination : Existe ------------");
		origine = -1;
		destination = 59015;
		algorithmOutOfGrapheTest(mapName, origine, destination);
	

		System.out.println("----- Cas de sommets inexistants ------");
		System.out.println("----- Origine : Existe ----------------");
		System.out.println("----- Destination : N'existe pas ------");
		origine = 38926;
		destination = 200000;
		algorithmOutOfGrapheTest(mapName, origine, destination);

		System.out.println("----- Cas de sommets inexistants ------");
		System.out.println("----- Origine : N'existe pas ----------");
		System.out.println("----- Destination : N'existe pas ------");
		origine = -1;
		destination = 200000;
		algorithmOutOfGrapheTest(mapName, origine, destination);
	}


	@Test
	public void testScenarioTempsHG() throws Exception {

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

		System.out.println("----- Cas de sommets inexistants ------");
		System.out.println("----- Origine : N'existe pas ----------");
		System.out.println("----- Destination : Existe ------------");
		origine = -1;
		destination = 85265;
		algorithmOutOfGrapheTest(mapName, origine, destination);

		System.out.println("----- Cas de sommets inexistants ------");
		System.out.println("----- Origine : Existe ----------------");
		System.out.println("----- Destination : N'existe pas ------");
		origine = 38926;
		destination = 300000;
		algorithmOutOfGrapheTest(mapName, origine, destination);

		System.out.println("----- Cas de sommets inexistants ------");
		System.out.println("----- Origine : N'existe pas ----------");
		System.out.println("----- Destination : N'existe pas ------");
		origine = -1;
		destination = 200000;
		algorithmOutOfGrapheTest(mapName, origine, destination);
	}

	@Test
	public void testScenarioDistanceINSA() throws Exception {

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

		System.out.println("----- Cas de sommets inexistants ------");
		System.out.println("----- Origine : N'existe pas ----------");
		System.out.println("----- Destination : Existe ------------");
		origine = 2000;
		destination = 857;
		algorithmOutOfGrapheTest(mapName, origine, destination);

		System.out.println("----- Cas de sommets inexistants ------");
		System.out.println("----- Origine : Existe ----------------");
		System.out.println("----- Destination : N'existe pas ------");
		origine = 607;
		destination = 200000;
		algorithmOutOfGrapheTest(mapName, origine, destination);

		System.out.println("----- Cas de sommets inexistants ------");
		System.out.println("----- Origine : N'existe pas ----------");
		System.out.println("----- Destination : N'existe pas ------");
		origine = 2000;
		destination = 2000;
		algorithmOutOfGrapheTest(mapName, origine, destination);
	}

	@Test
	public void testScenarioTempsINSA() throws Exception {

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

		System.out.println("----- Cas de sommets inexistants ------");
		System.out.println("----- Origine : N'existe pas ----------");
		System.out.println("----- Destination : Existe ------------");
		origine = 2000;
		destination = 857;
		algorithmOutOfGrapheTest(mapName, origine, destination);

		System.out.println("----- Cas de sommets inexistants ------");
		System.out.println("----- Origine : Existe ----------------");
		System.out.println("----- Destination : N'existe pas ------");
		origine = 607;
		destination = 200000;
		algorithmOutOfGrapheTest(mapName, origine, destination);

		System.out.println("----- Cas de sommets inexistants ------");
		System.out.println("----- Origine : N'existe pas ----------");
		System.out.println("----- Destination : N'existe pas ------");
		origine = 2000;
		destination = 2000;
		algorithmOutOfGrapheTest(mapName, origine, destination);
	}

	@Test
	public void testScenarioDistanceCarre() throws Exception {

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
	}
	

	@Test
	public void testScenarioTempsCarre() throws Exception {

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
	}


	@Test
	public void testScenarioDistanceGuadeloupe() throws Exception {

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

	}

	@Test
	public void testScenarioTempsGuadeloupe() throws Exception {

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

	}


	@Test
	public void testScenarioMinTempsDistHG() throws Exception {

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


		System.out.println("----- Cas de sommets inexistants ------");
		System.out.println("----- Origine : N'existe pas ----------");
		System.out.println("----- Destination : Existe ------------");
		origine = -1;
		destination = 59015;
		algorithmOutOfGrapheTest(mapName, origine, destination);

		System.out.println("----- Cas de sommets inexistants ------");
		System.out.println("----- Origine : Existe ----------------");
		System.out.println("----- Destination : N'existe pas ------");
		origine = 38926;
		destination = 200000;
		algorithmOutOfGrapheTest(mapName, origine, destination);

		System.out.println("----- Cas de sommets inexistants ------");
		System.out.println("----- Origine : N'existe pas ----------");
		System.out.println("----- Destination : N'existe pas ------");
		origine = -1;
		destination = 200000;
		algorithmOutOfGrapheTest(mapName, origine, destination);
	}

	@Test
	public void testScenarioMinTempsDistCarreDense() throws Exception {

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
	}

	@Test
	public void testScenarioMinTempsDistGuadeloupe() throws Exception {

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
	}

}
