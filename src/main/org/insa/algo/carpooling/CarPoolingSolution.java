package org.insa.algo.carpooling;

import org.insa.algo.AbstractSolution;
import org.insa.graph.Path;

public class CarPoolingSolution extends AbstractSolution {
    private Path path_A;
    private Path path_B;
    private Path commonPath;


    protected CarPoolingSolution(CarPoolingData data, Status status) {
        super(data, status);
    }
    protected CarPoolingSolution(CarPoolingData data,Status status,Path path_A,Path path_B,Path commonPath){
        super(data,status);
        this.path_A=path_A;
        this.path_B=path_B;
        this.commonPath=commonPath;
    }
}
