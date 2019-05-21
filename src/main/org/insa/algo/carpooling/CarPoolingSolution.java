package org.insa.algo.carpooling;

import org.insa.algo.AbstractInputData;
import org.insa.algo.AbstractSolution;
import org.insa.graph.Arc;
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

    @Override
    public CarPoolingData getInputData() {
        return (CarPoolingData) super.getInputData();
    }

    @Override
    public String toString() {
        String info = null;

        if (!isFeasible()) {
            info = String.format("No path found from node #%d and #%d to node #%d",
                    getInputData().getUser_A().getId(), getInputData().getUser_B().getId(),getInputData().getDestination().getId());
        }
        else {
            double costA=0;
            double costB=0;
            double costAB=0;
            double cost = 0;
            for (Arc arc: getPath_A().getArcs()) {
                cost += getInputData().getCost(arc);
                costA+=getInputData().getCost(arc);
            }
            for (Arc arc: getPath_B().getArcs()) {
                cost += getInputData().getCost(arc);
                costB += getInputData().getCost(arc);
            }
            for (Arc arc: getCommonPath().getArcs()) {
                cost+=getInputData().getCost(arc);
                costAB += getInputData().getCost(arc);
            }
            info = String.format("Found a path from node #%d and #%d to node #%d",
                    getInputData().getUser_A().getId(), getInputData().getUser_B().getId(),getInputData().getDestination().getId());
            if (getInputData().getMode() == AbstractInputData.Mode.LENGTH) {
                info = String.format("%s, AO : %.4f km BO : %.4f km AB : %.4f km TOTAL : %.4f km", info, costA / 1000.0,costB / 1000.0,costAB/1000.0,cost / 1000.0);
            }
            else {
                info = String.format("%s, AO : %.4f mn BO : %.4f mn AB : %.4f mn TOTAL : %.4f mn", info, costA / 60.0,costB / 60.0,costAB/60.0,cost / 60.0);
            }
        }
        info += " in " + getSolvingTime().getSeconds() + " seconds.";
        return info;
    }

    public Path getPath_A() {
        return path_A;
    }

    public Path getPath_B() {
        return path_B;
    }

    public Path getCommonPath() {
        return commonPath;
    }
}
