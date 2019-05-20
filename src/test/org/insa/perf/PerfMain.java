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

public class PerfMain {
	public static void evaluate(String mapName,int origin,int destination) throws IOException {
		GraphReader reader = new BinaryGraphReader(
				new DataInputStream(new BufferedInputStream(new FileInputStream(mapName))));

		Graph graph = reader.read();
		ArcInspector arcInspector = ArcInspectorFactory.getAllFilters().get(0);
		ShortestPathData data = new ShortestPathData(graph, graph.get(origin), graph.get(destination), arcInspector);
		DijkstraPerformanceTest test = new DijkstraPerformanceTest(data);
		System.out.println("DIJKSTRA TEST ON MAP : "+mapName);
		System.out.println("ORIGIN : "+origin+" DESTINATION : "+destination);
		System.out.println("TIME (ms) :"+test.evaluate());
	}

	public static void main(String[] args) throws IOException {
		String maps = "/home/decaeste/Bureau/commetud/3eme Annee MIC/Graphes-et-Algorithmes/Maps/";
		String midi_pyrenees= maps+"midi-pyrenees.mapgr";
		String carre_dense = maps+"carre-dense.mapgr";
		String fractale = maps+"fractal-spiral.mapgr";
		String toulouse = maps+"toulouse.mapgr";
		evaluate(carre_dense,343341,42714);

	}
}
