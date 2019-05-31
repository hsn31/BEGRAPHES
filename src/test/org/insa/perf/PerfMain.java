package org.insa.perf;

import org.insa.algo.ArcInspector;
import org.insa.algo.ArcInspectorFactory;
import org.insa.algo.shortestpath.ShortestPathData;
import org.insa.graph.Graph;
import org.insa.graph.io.BinaryGraphReader;
import org.insa.graph.io.GraphReader;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;

import static org.insa.algo.shortestpath.ShortestPathAlgorithmTest.wallGraph;
import static org.insa.algo.shortestpath.ShortestPathAlgorithmTest.wallWidth;
import static org.insa.algo.shortestpath.ShortestPathAlgorithmTest.wallHeight;


public class PerfMain {

	public static void evaluate(String mapName,int origin,int destination,String comment) throws IOException {
		GraphReader reader = new BinaryGraphReader(
				new DataInputStream(new BufferedInputStream(new FileInputStream(mapName))));

		Graph graph = reader.read();
		evaluate(graph,origin,destination,comment);

	}
	public static void evaluate(Graph graph,int origin,int destination,String comment) {
		String mapName = graph.getMapName();
		ArcInspector arcInspector = ArcInspectorFactory.getAllFilters().get(0);
		ShortestPathData data = new ShortestPathData(graph, graph.get(origin), graph.get(destination), arcInspector);
		ShortestPathPerformanceTest test = new DijkstraPerformanceTest(data);
		System.out.println("DIJKSTRA TEST ON MAP : "+mapName);
		System.out.println("ORIGIN : "+origin+" DESTINATION : "+destination);
		if(!comment.equals("")) {
			System.out.println(comment);
		}
		System.out.println("SHORTEST MODE");
		System.out.println("EXECUTION TIME (ms) :"+test.evaluate());
		arcInspector = ArcInspectorFactory.getAllFilters().get(2);
		data = new ShortestPathData(graph, graph.get(origin), graph.get(destination), arcInspector);
		test = new DijkstraPerformanceTest(data);
		System.out.println("FASTEST MODE");
		System.out.println("TIME (ms) : "+test.evaluate()+"\n");



		arcInspector = ArcInspectorFactory.getAllFilters().get(0);
		data = new ShortestPathData(graph, graph.get(origin), graph.get(destination), arcInspector);
		test = new AStarPerformanceTest(data);
		System.out.println("AStar TEST ON MAP : "+mapName);
		System.out.println("ORIGIN : "+origin+" DESTINATION : "+destination);
		if(!comment.equals("")) {
			System.out.println(comment);
		}
		System.out.println("SHORTEST MODE");
		System.out.println("EXECUTION TIME (ms) :"+test.evaluate());
		arcInspector = ArcInspectorFactory.getAllFilters().get(2);
		data = new ShortestPathData(graph, graph.get(origin), graph.get(destination), arcInspector);
		test = new AStarPerformanceTest(data);
		System.out.println("FASTEST MODE");
		System.out.println("TIME (ms) : "+test.evaluate()+"\n");



	}
	public static void evaluate(String mapName,int origin,int destination) throws IOException{
		evaluate(mapName,origin,destination,"");
	}



	public static void main(String[] args) throws IOException {
		String maps = "/home/decaeste/Bureau/commetud/3eme Annee MIC/Graphes-et-Algorithmes/Maps/";
		//String maps = "C:\\Users\\Brice\\Documents\\Cours 3A MIC\\Graphe\\Maps\\";
		String midi_pyrenees= maps+"midi-pyrenees.mapgr";
		String carre_dense = maps+"carre-dense.mapgr";
		String fractale = maps+"fractal-spiral.mapgr";
		String toulouse = maps+"toulouse.mapgr";
		String newZealand = maps+"new-zealand.mapgr";
		String california = maps+"california.mapgr";

		evaluate(fractale,844529,889773,"Opposite");
		evaluate(fractale,68836,818567,"Center");

		evaluate(newZealand,230743,250006,"Reachable");
		evaluate(newZealand,204261,250006,"Unreachable");


		evaluate(carre_dense,343341,42714,"Opposite");
		evaluate(carre_dense,196673,156393);
		evaluate(carre_dense,276013,3423,"Close");
		evaluate(carre_dense,239886,3423);
		evaluate(carre_dense,169473,131823);

		evaluate(midi_pyrenees,265855,491131,"Opposite");
		evaluate(midi_pyrenees,610190,28680,"Close center");
		evaluate(midi_pyrenees,343586,76028,"Mandatory curve");
		
		evaluate(california,286422,841476,"Huge map, origin near border destination near middle");
		evaluate(california,841476,286422,"Huge map, origin near center destination near border");
		
		
		
		
		

	}
}
