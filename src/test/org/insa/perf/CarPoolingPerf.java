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
		System.out.println("USER A : "+originA+" USER B :"+originB+" DESTINATION : "+destination);
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
		//String maps = "C:\\Users\\Brice\\Documents\\Cours 3A MIC\\Graphe\\Maps\\";
		String midi_pyrenees= maps+"midi-pyrenees.mapgr";
		String carre_dense = maps+"carre-dense.mapgr";
		String fractale = maps+"fractal-spiral.mapgr";
		String toulouse = maps+"toulouse.mapgr";
		String newZealand = maps+"new-zealand.mapgr";
		String california = maps+"california.mapgr";
		String belgium = maps+"belgium.mapgr";
		
		System.out.println("MIDI PYRENNEES");;

		evaluate(midi_pyrenees,315603,243724,109237,"Close starts, far destination");
		System.out.println("***********************************");
		evaluate(midi_pyrenees,447547,361305,76760,"Equilateralish");
		System.out.println("***********************************");
		evaluate(midi_pyrenees,496160,406219,613829,"Three points aligned");
		System.out.println("***********************************");
		evaluate(midi_pyrenees,496160,406219,613829,"Three points aligned\nDestination at the middle");
		System.out.println("***********************************");
		evaluate(midi_pyrenees,225084,345135,480971,"Three points aligned\nUser at the middle");
		System.out.println("***********************************");
		
		System.out.println("CALIFORNIA");;
		evaluate(california,1417789,288028,1020192,"Close starts, far destination");
		System.out.println("***********************************");
		evaluate(california,792971,766918,782081,"Curve required");
		System.out.println("***********************************");
		
		System.out.println("BELGIUM");
		evaluate(belgium,516403,882812,84661,"Almost equilateral");
		System.out.println("***********************************");
		evaluate(belgium,396597,461420,532320,"Close starts,far destination");
		System.out.println("***********************************");
		
		System.out.println("NEW ZEALAND");
		evaluate(newZealand,230743,204261,250006,"Impossible");
		System.out.println("***********************************");
		
		
		
		
	
		
		
		
		
		
		
		
		
		

		
		




	}
}
