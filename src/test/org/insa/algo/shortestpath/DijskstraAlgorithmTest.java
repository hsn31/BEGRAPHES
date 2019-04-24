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
	private static Graph squareMapGraph;
	//private static String squareMapName = "C:\\Users\\Brice\\Desktop\\carre.mapgr";
	private static String squareMapName ="D:\\Téléchargements\\carre.mapgr";


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
				new DataInputStream(new BufferedInputStream(new FileInputStream(squareMapName))));

		//Read the graph.
		squareMapGraph = reader.read();
		mapArcInspector = ArcInspectorFactory.getAllFilters().get(0);


	}


	public void DjikstraGraphWithOraclePathTest(int from, int to) {

		ShortestPathData shortestPathData = new ShortestPathData(graph, nodes[from], nodes[to], defaultArcInspector);
		BellmanFordAlgorithm bellmanFordAlgorithm = new BellmanFordAlgorithm(shortestPathData);
		DijkstraAlgorithm dijkstraAlgorithm = new DijkstraAlgorithm(shortestPathData);


		ShortestPathSolution bellmanFordSolution = bellmanFordAlgorithm.doRun();
		ShortestPathSolution dijkstraSolution = dijkstraAlgorithm.doRun();
		
		if (from !=to) {
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


	}

	@Test
	public void dijkstraAlgorithmSimpleGraphWithOracleTest() {
		for (int from = 0; from < nodes.length; from++) {
			for (int to = 0; to < nodes.length; to++) {
				DjikstraGraphWithOraclePathTest(from, to);
			}
		}

	}

	@Test
	public void dijkstraAlgorithmMapWithOracleTest() {
		Random random = new Random();
		for (int i = 0; i < 10; i++) {
			int from = random.nextInt(squareMapGraph.size());
			int to = random.nextInt(squareMapGraph.size());
			System.out.println("FROM: " + from + " TO " + to);
			ShortestPathData shortestPathData = new ShortestPathData(squareMapGraph, squareMapGraph.get(from), squareMapGraph.get(to), mapArcInspector);
			BellmanFordAlgorithm bellmanFordAlgorithm = new BellmanFordAlgorithm(shortestPathData);
			DijkstraAlgorithm dijkstraAlgorithm = new DijkstraAlgorithm(shortestPathData);

			ShortestPathSolution bellmanFordSolution = bellmanFordAlgorithm.doRun();
			ShortestPathSolution dijkstraSolution = dijkstraAlgorithm.doRun();
			
			if (from!=to) {
			assertEquals("Bellman Ford and Dijkstra finished with different status on map " + squareMapName, bellmanFordSolution.getStatus(), dijkstraSolution.getStatus());
			assertTrue("End status incorrect,should be INFEASIBLE or OPTIMAL, is "+dijkstraSolution.getStatus().toString(), AbstractSolution.Status.OPTIMAL == dijkstraSolution.getStatus() || dijkstraSolution.getStatus() == AbstractSolution.Status.INFEASIBLE);
			
			//Assume.assumeTrue(dijkstraSolution.getStatus() != AbstractSolution.Status.INFEASIBLE);
			if (dijkstraSolution.getStatus()!=AbstractSolution.Status.INFEASIBLE) {
				assertTrue(bellmanFordSolution.getPath().getLength() == dijkstraSolution.getPath().getLength());
			}

			}
		}


	}

	//Test de la distance et du temps avec Oracle
	
	public void dijkstraAlgorithmMapWithOracleTestDistanceOrTime(String mapName, int typeEvaluation, int origine, int destination) throws Exception {
		//Soit temps =0, soit distance =1.
		GraphReader reader = new BinaryGraphReader(
				new DataInputStream(new BufferedInputStream(new FileInputStream(mapName))));

		Graph graph = reader.read();

		ArcInspector arcInspectorDijkstra;

		if (typeEvaluation == 0) { 
			System.out.println("Mode : Temps");
			arcInspectorDijkstra = ArcInspectorFactory.getAllFilters().get(2);
		} else {
			System.out.println("Mode : Distance");
			arcInspectorDijkstra = ArcInspectorFactory.getAllFilters().get(0);
		}

		System.out.println("Origine : " + origine);
		System.out.println("Destination : " + destination);

		ShortestPathData data = new ShortestPathData(graph, graph.get(origine), graph.get(destination), arcInspectorDijkstra);

		BellmanFordAlgorithm Bellman = new BellmanFordAlgorithm(data);
		DijkstraAlgorithm Dijkstra = new DijkstraAlgorithm(data);


		ShortestPathSolution solution = Dijkstra.doRun();
		ShortestPathSolution expected = Bellman.doRun();

		if (origine < 0 || destination < 0 || origine >= (graph.size() - 1) || destination >= (graph.size() - 1)) { 
			//Hors du graph
			
			System.out.println("ERREUR : Paramètres invalides ");
			assertTrue("End status incorrect,should be INFEASIBLE is " + solution.getStatus().toString(), solution.getStatus() == AbstractSolution.Status.INFEASIBLE);

		} else if (origine == destination) {
			System.out.println("Origine et Destination identiques"); //cf conv messenger du 24/04/2019
			assertTrue(AbstractSolution.Status.OPTIMAL == solution.getStatus());
			assertTrue("error"+solution.getPath().getArcs().size(), solution.getPath().getArcs().size()==0);

		} else if (solution.getPath() == null) {
			assertEquals(expected.getPath(), solution.getPath());

		} else {
			double costSolution;
			double costExpected;
			if (typeEvaluation == 0) { 
		//Time
				costSolution = solution.getPath().getMinimumTravelTime();
				costExpected = expected.getPath().getMinimumTravelTime();
				
			} else {
				costSolution = solution.getPath().getLength();
				costExpected = expected.getPath().getLength();
			}
			
			assertTrue("expected"+costExpected+ "and was" +costSolution, costExpected==costSolution);
			assertEquals("BellmanFord and Dijkstra solution give differrent number of nodes in path", expected.getPath().size(), solution.getPath().size());
			assertTrue("Different lenghth for BellmanFord solution and Dijkstra solution", expected.getPath().getLength() == solution.getPath().getLength());
			assertEquals("Different arcs founded for Dijkstra and Bellman_Ford solutions", expected.getPath().getArcs(), solution.getPath().getArcs());
			
		}

		System.out.println();
		System.out.println();
	}
	
	//Exactement le meme Test visant la distance et le temps mais sans Oracle
	
	public void dijkstraAlgorithmMapWithoutOracleTest(String mapName, int origine, int destination) throws Exception {

		double costFastestSolutionInTime = Double.POSITIVE_INFINITY;
		double costFastestSolutionInDistance = Double.POSITIVE_INFINITY;
		double costShortestSolutionInTime = Double.POSITIVE_INFINITY;
		double costShortestSolutionInDistance = Double.POSITIVE_INFINITY;

		GraphReader reader = new BinaryGraphReader(
				new DataInputStream(new BufferedInputStream(new FileInputStream(mapName))));

		Graph graph = reader.read();

		/** Recherche du chemin le plus rapide **/
		ArcInspector arcInspectorDijkstra = ArcInspectorFactory.getAllFilters().get(2);

		ShortestPathData data = new ShortestPathData(graph, graph.get(origine), graph.get(destination),
				arcInspectorDijkstra);

		DijkstraAlgorithm Dijkstra = new DijkstraAlgorithm(data);

		/* Recuperation de la solution de Dijkstra */
		ShortestPathSolution solution = Dijkstra.doRun();
		
		if (origine < 0 || destination < 0 || origine >= (graph.size() - 1) || destination >= (graph.size() - 1)) { 
			//Hors du graph
			
			System.out.println("ERREUR : Paramètres invalides ");
			assertTrue("End status incorrect,should be INFEASIBLE or OPTIMAL, is " + solution.getStatus().toString(), solution.getStatus() == AbstractSolution.Status.INFEASIBLE);

		} else if (origine == destination) {
			System.out.println("Origine et Destination identiques"); //cf conv messenger du 24/04/2019
			assertTrue(AbstractSolution.Status.OPTIMAL == solution.getStatus());
			assertEquals(solution.getPath().getArcs().size(),0);
			

		} else if (solution.getPath() == null) {
			assertTrue(solution.getPath() == null);
		}

		else {

			costFastestSolutionInTime = solution.getPath().getMinimumTravelTime();
			costFastestSolutionInDistance = solution.getPath().getLength();

		
		arcInspectorDijkstra = ArcInspectorFactory.getAllFilters().get(0);

		data = new ShortestPathData(graph, graph.get(origine), graph.get(destination), arcInspectorDijkstra);

		Dijkstra = new DijkstraAlgorithm(data);

		solution = Dijkstra.doRun();


		if (solution.getPath() == null) {
			assertTrue(solution.getPath() == null);
		}
	
		else {
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

			String mapName = "D:\\Téléchargements\\haute-garonne.mapgr";
			
			int origine;
			int destination;
			System.out.println("*****************************************************");
			System.out.println("----- Test de validité avec oracle sur une carte-----");
			System.out.println("----- Carte : Haute-Garonne -------------------------");
			System.out.println("----- Mode : DISTANCE -------------------------------");
			System.out.println();
			
			System.out.println("----- Cas d'un chemin nul ------");
			origine = 0 ;
			destination = 0;
			dijkstraAlgorithmMapWithOracleTestDistanceOrTime(mapName,1,origine,destination);    
			
			System.out.println("----- Cas d'un chemin simple ------");
			origine = 38926;
			destination = 59015;
			dijkstraAlgorithmMapWithOracleTestDistanceOrTime(mapName,1,origine,destination);    	
		
			
			System.out.println("----- Cas de sommets inexistants ------");
			System.out.println("----- Origine : N'existe pas ----------");
			System.out.println("----- Destination : Existe ------------");
			origine = -1;
			destination = 59015;
			dijkstraAlgorithmMapWithOracleTestDistanceOrTime(mapName,1,origine,destination);    	

			System.out.println("----- Cas de sommets inexistants ------");
			System.out.println("----- Origine : Existe ----------------");
			System.out.println("----- Destination : N'existe pas ------");
			origine = 38926;
			destination = 200000;
			dijkstraAlgorithmMapWithOracleTestDistanceOrTime(mapName,1,origine,destination);    	
			
			System.out.println("----- Cas de sommets inexistants ------");
			System.out.println("----- Origine : N'existe pas ----------");
			System.out.println("----- Destination : N'existe pas ------");
			origine = -1;
			destination = 200000;
			dijkstraAlgorithmMapWithOracleTestDistanceOrTime(mapName,1,origine,destination);    	
		}

		
		@Test
		public void testScenarioTempsHG() throws Exception {
		
			String mapName = "D:\\Téléchargements\\haute-garonne.mapgr";

			int origine;
			int destination;
			System.out.println("*****************************************************");
			System.out.println("----- Test de validité avec oracle sur une carte-----");
			System.out.println("----- Carte : Haute-Garonne -------------------------");
			System.out.println("----- Mode : TEMPS ----------------------------------");
			System.out.println();
			
			System.out.println("----- Cas d'un chemin nul ------");
			origine = 0 ;
			destination = 0;
			dijkstraAlgorithmMapWithOracleTestDistanceOrTime(mapName,0,origine,destination);    
			
			System.out.println("----- Cas d'un chemin simple ------");
			origine = 99490;
			destination = 85265;
			dijkstraAlgorithmMapWithOracleTestDistanceOrTime(mapName,0,origine,destination);    	
		
			System.out.println("----- Cas de sommets inexistants ------");
			System.out.println("----- Origine : N'existe pas ----------");
			System.out.println("----- Destination : Existe ------------");
			origine = -1;
			destination = 85265;
			dijkstraAlgorithmMapWithOracleTestDistanceOrTime(mapName,0,origine,destination);    	

			System.out.println("----- Cas de sommets inexistants ------");
			System.out.println("----- Origine : Existe ----------------");
			System.out.println("----- Destination : N'existe pas ------");
			origine = 38926;
			destination = 300000;
			dijkstraAlgorithmMapWithOracleTestDistanceOrTime(mapName,0,origine,destination);    	
			
			System.out.println("----- Cas de sommets inexistants ------");
			System.out.println("----- Origine : N'existe pas ----------");
			System.out.println("----- Destination : N'existe pas ------");
			origine = -1;
			destination = 200000;
			dijkstraAlgorithmMapWithOracleTestDistanceOrTime(mapName,0,origine,destination);    	
		}

		@Test
		public void testScenarioDistanceINSA() throws Exception {

			String mapName = "D:\\Téléchargements\\insa.mapgr";


			int origine;
			int destination;
			System.out.println("*****************************************************");
			System.out.println("----- Test de validité avec oracle sur une carte-----");
			System.out.println("----- Carte : INSA ----------------------------------");
			System.out.println("----- Mode : DISTANCE -------------------------------");
			System.out.println();
			
			System.out.println("----- Cas d'un chemin nul ------");
			origine = 300 ;
			destination = 300;
			dijkstraAlgorithmMapWithOracleTestDistanceOrTime(mapName,1,origine,destination);    
			
			System.out.println("----- Cas d'un chemin simple ------");
			origine = 607;
			destination = 857;
			dijkstraAlgorithmMapWithOracleTestDistanceOrTime(mapName,1,origine,destination);    	
		
			System.out.println("----- Cas de sommets inexistants ------");
			System.out.println("----- Origine : N'existe pas ----------");
			System.out.println("----- Destination : Existe ------------");
			origine = 2000;
			destination = 857;
			dijkstraAlgorithmMapWithOracleTestDistanceOrTime(mapName,1,origine,destination);    	

			System.out.println("----- Cas de sommets inexistants ------");
			System.out.println("----- Origine : Existe ----------------");
			System.out.println("----- Destination : N'existe pas ------");
			origine = 607;
			destination = 200000;
			dijkstraAlgorithmMapWithOracleTestDistanceOrTime(mapName,1,origine,destination);    	
			
			System.out.println("----- Cas de sommets inexistants ------");
			System.out.println("----- Origine : N'existe pas ----------");
			System.out.println("----- Destination : N'existe pas ------");
			origine = 2000;
			destination = 2000;
			dijkstraAlgorithmMapWithOracleTestDistanceOrTime(mapName,1,origine,destination);   
		}

		@Test
		public void testScenarioTempsINSA() throws Exception {

			String mapName = "D:\\Téléchargements\\insa.mapgr";


			int origine;
			int destination;
			System.out.println("*****************************************************");
			System.out.println("----- Test de validité avec oracle sur une carte-----");
			System.out.println("----- Carte : INSA ----------------------------------");
			System.out.println("----- Mode : TEMPS ----------------------------------");
			System.out.println();
			
			System.out.println("----- Cas d'un chemin nul ------");
			origine = 300 ;
			destination = 300;
			dijkstraAlgorithmMapWithOracleTestDistanceOrTime(mapName,0,origine,destination);    
			
			System.out.println("----- Cas d'un chemin simple ------");
			origine = 44;
			destination = 541;
			dijkstraAlgorithmMapWithOracleTestDistanceOrTime(mapName,0,origine,destination);    	
		
			System.out.println("----- Cas de sommets inexistants ------");
			System.out.println("----- Origine : N'existe pas ----------");
			System.out.println("----- Destination : Existe ------------");
			origine = 2000;
			destination = 857;
			dijkstraAlgorithmMapWithOracleTestDistanceOrTime(mapName,0,origine,destination);    	

			System.out.println("----- Cas de sommets inexistants ------");
			System.out.println("----- Origine : Existe ----------------");
			System.out.println("----- Destination : N'existe pas ------");
			origine = 607;
			destination = 200000;
			dijkstraAlgorithmMapWithOracleTestDistanceOrTime(mapName,0,origine,destination);    	
			
			System.out.println("----- Cas de sommets inexistants ------");
			System.out.println("----- Origine : N'existe pas ----------");
			System.out.println("----- Destination : N'existe pas ------");
			origine = 2000;
			destination = 2000;
			dijkstraAlgorithmMapWithOracleTestDistanceOrTime(mapName,0,origine,destination);   
		}
		
		@Test
		public void testScenarioDistanceCarre() throws Exception {

			String mapName = "D:\\Téléchargements\\carre.mapgr";


			int origine;
			int destination;
			System.out.println("*****************************************************");
			System.out.println("----- Test de validité avec oracle sur une carte-----");
			System.out.println("----- Carte : CARRE ---------------------------------");
			System.out.println("----- Mode : DISTANCE -------------------------------");
			System.out.println();
			
			System.out.println("----- Cas d'un chemin simple ------");
			origine = 0;
			destination = 20;
			dijkstraAlgorithmMapWithOracleTestDistanceOrTime(mapName,1,origine,destination);    		
		}

		@Test
		public void testScenarioTempsCarre() throws Exception {
			
			String mapName = "D:\\Téléchargements\\carre.mapgr";


			int origine;
			int destination;
			System.out.println("*****************************************************");
			System.out.println("----- Test de validité avec oracle sur une carte-----");
			System.out.println("----- Carte : CARRE ---------------------------------");
			System.out.println("----- Mode : TEMPS ----------------------------------");
			System.out.println();

			System.out.println("----- Cas d'un chemin simple ------");
			origine = 0;
			destination = 12;
			dijkstraAlgorithmMapWithOracleTestDistanceOrTime(mapName,0,origine,destination);    			
		}
		
		
		@Test
		public void testScenarioDistanceGuadeloupe() throws Exception {
	
			String mapName = "D:\\Téléchargements\\guadeloupe.mapgr";

		
			int origine;
			int destination;
			System.out.println("*****************************************************");
			System.out.println("----- Test de validité avec oracle sur une carte-----");
			System.out.println("----- Carte : GUADELOUPE ----------------------------");
			System.out.println("----- Mode : DISTANCE -------------------------------");
			System.out.println();
		
			System.out.println("----- Cas d'un chemin simple ------");
			origine = 9922;
			destination = 34328;
			dijkstraAlgorithmMapWithOracleTestDistanceOrTime(mapName,1,origine,destination);    	
		
			System.out.println("----- Cas de sommets non connexes ------");
			origine = 9950;
			destination = 15860;
			dijkstraAlgorithmMapWithOracleTestDistanceOrTime(mapName,1,origine,destination);    	

		}
		
		@Test
		public void testScenarioTempsGuadeloupe() throws Exception {
			
			String mapName = "D:\\Téléchargements\\guadeloupe.mapgr";

			
			int origine;
			int destination;
			System.out.println("*****************************************************");
			System.out.println("----- Test de validité avec oracle sur une carte-----");
			System.out.println("----- Carte : GUADELOUPE ----------------------------");
			System.out.println("----- Mode : TEMPS ----------------------------------");
			System.out.println();
		
			System.out.println("----- Cas d'un chemin simple ------");
			origine = 9922;
			destination = 34328;
			dijkstraAlgorithmMapWithOracleTestDistanceOrTime(mapName,10,origine,destination);    	
		
			System.out.println("----- Cas de sommets non connexes ------");
			origine = 9950;
			destination = 15860;
			dijkstraAlgorithmMapWithOracleTestDistanceOrTime(mapName,0,origine,destination);    	

		}

		
		@Test
		public void testScenarioMinTempsDistHG() throws Exception {

			String mapName = "D:\\Téléchargements\\haute-garonne.mapgr";
			
	
			int origine;
			int destination;
			System.out.println("*****************************************************");
			System.out.println("----- Test de validité sans oracle sur une carte-----");
			System.out.println("----- Carte : Haute-Garonne -------------------------");
			System.out.println();

			System.out.println("----- Cas d'un chemin nul ------");
			origine = 0 ;
			destination = 0;
			dijkstraAlgorithmMapWithoutOracleTest(mapName,origine,destination);
			
			System.out.println("----- Cas d'un chemin nul ------");
			origine = 4 ;
			destination = 4;
			dijkstraAlgorithmMapWithoutOracleTest(mapName,origine,destination);
			
			System.out.println("----- Cas d'un chemin simple ------");
			origine = 38926;
			destination = 59015;
			dijkstraAlgorithmMapWithoutOracleTest(mapName,origine,destination);    	
		
			
			System.out.println("----- Cas de sommets inexistants ------");
			System.out.println("----- Origine : N'existe pas ----------");
			System.out.println("----- Destination : Existe ------------");
			origine = -1;
			destination = 59015;
			dijkstraAlgorithmMapWithoutOracleTest(mapName,origine,destination);   	

			System.out.println("----- Cas de sommets inexistants ------");
			System.out.println("----- Origine : Existe ----------------");
			System.out.println("----- Destination : N'existe pas ------");
			origine = 38926;
			destination = 200000;
			dijkstraAlgorithmMapWithoutOracleTest(mapName,origine,destination);    	
			
			System.out.println("----- Cas de sommets inexistants ------");
			System.out.println("----- Origine : N'existe pas ----------");
			System.out.println("----- Destination : N'existe pas ------");
			origine = -1;
			destination = 200000; 
			dijkstraAlgorithmMapWithoutOracleTest(mapName,origine,destination);   
		}

		@Test
		public void testScenarioMinTempsDistCarreDense() throws Exception {

			String mapName = "D:\\Téléchargements\\carre.mapgr";
			
	
			int origine;
			int destination;
			System.out.println("*****************************************************");
			System.out.println("----- Test de validité sans oracle sur une carte-----");
			System.out.println("----- Carte : CARRE ---------------------------");
			System.out.println();

			System.out.println("----- Cas d'un chemin simple ------");
			origine = 0;
			destination = 10;
			dijkstraAlgorithmMapWithoutOracleTest(mapName,origine,destination);    
		}
		
		@Test
		public void testScenarioMinTempsDistGuadeloupe() throws Exception {

			String mapName = "D:\\Téléchargements\\guadeloupe.mapgr";


			int origine;
			int destination;
			System.out.println("*****************************************************");
			System.out.println("----- Test de validité sans oracle sur une carte-----");
			System.out.println("----- Carte : GUADELOUPE ----------------------------");
			System.out.println();
		
			System.out.println("----- Cas d'un chemin simple ------");
			origine = 9922;
			destination = 3428;
			dijkstraAlgorithmMapWithoutOracleTest(mapName,origine,destination);    	
		
			System.out.println("----- Cas de sommets non connexes ------");
			origine = 9950;
			destination = 15860;
			dijkstraAlgorithmMapWithoutOracleTest(mapName,origine,destination);    
		}
	
}
