package org.insa.algo.carpooling;

import org.insa.algo.AbstractInputData;
import org.insa.algo.AbstractSolution;
import org.insa.graph.Arc;
import org.insa.graph.Path;

import java.util.ArrayList;

public class CarPoolingSolution extends AbstractSolution {
    private Path commonPath;

    private double costA;
    private double costB;
    private double costAB;
    private double cost;

    private ArrayList<Path> pathUO;


    protected CarPoolingSolution(CarPoolingData data, Status status) {
        super(data, status);
    }
    protected CarPoolingSolution(CarPoolingData data,Status status,ArrayList<Path> pathUO,Path commonPath){
        super(data,status);
        this.pathUO=pathUO;
        this.commonPath=commonPath;

    }
    protected CarPoolingSolution(CarPoolingData data,Status status,Path path_A,Path path_B,Path commonPath) {
        super(data, status);
        this.pathUO=new ArrayList<>();
        this.pathUO.add(path_A);
        this.pathUO.add(path_B);
        this.commonPath = commonPath;
        this.costA=0;
        this.costB=0;
        this.costAB=0;
        this.cost=0;

        if (isFeasible()) {
            for (Arc arc : getIndividualPaths().get(0).getArcs()) {

                this.costA += getInputData().getCost(arc);
            }
            for (Arc arc : getIndividualPaths().get(1).getArcs()) {

                this.costB += getInputData().getCost(arc);
            }
            for (Arc arc : getCommonPath().getArcs()) {

                this.costAB += getInputData().getCost(arc);
            }
            this.cost=this.costA+this.costB+this.costAB;
        }
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
            info = String.format("Found a path from node #%d and #%d to node #%d",
                    getInputData().getUser_A().getId(), getInputData().getUser_B().getId(),getInputData().getDestination().getId());
            if (getInputData().getMode() == AbstractInputData.Mode.LENGTH) {
                info = String.format("%s, AO : %.4f km BO : %.4f km AB : %.4f km TOTAL : %.4f km", info, getCostA()/ 1000.0,getCostB() / 1000.0,getCommonCost()/1000.0,getCost() / 1000.0);
            }
            else {
                info = String.format("%s, AO : %.4f mn BO : %.4f mn AB : %.4f mn TOTAL : %.4f mn", info,getCostA() / 60.0,getCostB() / 60.0,getCommonCost()/60.0,getCost() / 60.0);
            }
        }
        info += " in " + getSolvingTime().getSeconds() + " seconds.";
        return info;
    }

    public Path getPath_A() {
        return pathUO.get(0);
    }

    public Path getPath_B() {
        return pathUO.get(1);
    }

    public Path getCommonPath() {
        return commonPath;
    }

    public double getCostA() {
        if(getInputData().getMode()==AbstractInputData.Mode.LENGTH){
            return pathUO.get(0).getLength();
        }
        else{
            return pathUO.get(0).getMinimumTravelTime();
        }
    }

    public double getCostB() {
        if(getInputData().getMode()==AbstractInputData.Mode.LENGTH){
            return pathUO.get(1).getLength();
        }
        else{
            return pathUO.get(1).getMinimumTravelTime();
        }
    }

    public double getCostAB(){
        if(getInputData().getMode()==AbstractInputData.Mode.LENGTH){
            return commonPath.getLength();
        }
        else{
            return commonPath.getMinimumTravelTime();
        }

    }

    public double getCommonCost() {
        return costAB;
    }

    public double getCost() {
        return cost;
    }

    public ArrayList<Path> getIndividualPaths() {
        return pathUO;
    }
}
