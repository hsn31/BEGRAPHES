package org.insa.algo.shortestpath;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import org.insa.algo.AbstractSolution.Status;
import org.insa.graph.Arc;
import org.insa.graph.Graph;
import org.insa.graph.Node;
import org.insa.graph.Path;

public class DijkstraAlgorithm extends ShortestPathAlgorithm {

    public DijkstraAlgorithm(ShortestPathData data) {
        super(data);
    }

    @Override
    protected ShortestPathSolution doRun() {
        ShortestPathData data = getInputData();
        Graph graph=data.getGraph();
        Label[] labels=new Label[graph.getNodes().size()];
        double[] distance=new double[graph.getNodes().size()];
        //On travaille avec les IDs définis par le graphe donné en input
        for(int i=0;i<graph.getNodes().size();i++) {
        	int id=graph.getNodes().get(i).getId();
        	labels[id].current=id;
        	distance[id]=Double.POSITIVE_INFINITY;
        }
        
        
        
        
        
        
        
        
        
        ShortestPathSolution solution = null;
        // TODO:
        return solution;
    }

}
