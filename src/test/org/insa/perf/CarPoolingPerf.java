package org.insa.perf;

import org.insa.algo.ArcInspector;
import org.insa.algo.ArcInspectorFactory;
import org.insa.algo.carpooling.CarPoolingData;
import org.insa.graph.Graph;
import org.insa.graph.io.BinaryGraphReader;
import org.insa.graph.io.GraphReader;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;

public class CarPoolingPerf {
	public static void evaluate(String mapName,int originA,int originB,int destination,String comment) throws IOException {
		GraphReader reader = new BinaryGraphReader(
				new DataInputStream(new BufferedInputStream(new FileInputStream(mapName))));

		Graph graph = reader.read();

		ArcInspector arcInspectorLength = ArcInspectorFactory.getAllFilters().get(0);
		ArcInspector arcInspectorTime =ArcInspectorFactory.getAllFilters().get(2);

		CarPoolingData dataLength = new CarPoolingData(graph,graph.get(originA), graph.get(originB), graph.get(destination),arcInspectorLength);
		CarPoolingData dataTime = new CarPoolingData(graph,graph.get(originA), graph.get(originB), graph.get(destination),arcInspectorTime);

		PerformanceTest testMergeLength=new MergePerformanceTest(dataLength);
		PerformanceTest testMergeTime=new MergePerformanceTest(dataTime);
		PerformanceTest testGuidedMergeLength=new GuidedMergePerformanceTest(dataLength);
		PerformanceTest testGuidedMergeTime=new GuidedMergePerformanceTest(dataTime);

		System.out.println("TEST ON MAP : "+mapName);
		System.out.println("USER A : "+originA+"USER B :"+originB+" DESTINATION : "+destination);
		if(!comment.equals("")) {
			System.out.println(comment);
		}
		System.out.println("--------------------------");
		System.out.println("MERGE ALGORITHM");
		System.out.println("SHORTEST MODE");
		System.out.println("EXECUTION TIME (ms) :"+testMergeLength.evaluate());
		System.out.println("FASTEST MODE");
		System.out.println("EXECUTION TIME (ms) :"+testMergeTime.evaluate()+"\n");
		System.out.println("--------------------------");
		System.out.println("GUIDED MERGE ALGORITHM");
		System.out.println("SHORTEST MODE");
		System.out.println("EXECUTION TIME (ms) :"+testGuidedMergeLength.evaluate());
		System.out.println("FASTEST MODE");
		System.out.println("EXECUTION TIME (ms) :"+testGuidedMergeTime.evaluate()+"\n");




	}
	public static void evaluate(String mapName,int originA,int originB,int destination) throws IOException{
		evaluate(mapName,originA,originB,destination,"");
	}

	public static void main(String[] args) throws IOException{
		String maps = "/home/decaeste/Bureau/commetud/3eme Annee MIC/Graphes-et-Algorithmes/Maps/";
		String midi_pyrenees= maps+"midi-pyrenees.mapgr";
		String carre_dense = maps+"carre-dense.mapgr";
		String fractale = maps+"fractal-spiral.mapgr";
		String toulouse = maps+"toulouse.mapgr";
		String newZealand = maps+"new-zealand.mapgr";

		evaluate(midi_pyrenees,315603,243724,109237,"Close starts, far destination");
		//evaluate(midi_pyrenees,265855,491131,0,"Equilateralish");




	}
}
