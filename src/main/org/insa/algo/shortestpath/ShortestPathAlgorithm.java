package org.insa.algo.shortestpath;

import org.insa.algo.AbstractAlgorithm;
import org.insa.exception.NodeOutOfGraphException;
import org.insa.graph.Node;

public abstract class ShortestPathAlgorithm extends AbstractAlgorithm<ShortestPathObserver> {

    protected ShortestPathAlgorithm(ShortestPathData data)throws NodeOutOfGraphException {
        super(data);
        if(!data.getGraph().getNodes().contains( data.getOrigin())) {
        	throw new NodeOutOfGraphException();
        }
        if(!data.getGraph().getNodes().contains( data.getDestination())) {
        	throw new NodeOutOfGraphException();
        }
        
    }

    @Override
    public ShortestPathSolution run() {
        return (ShortestPathSolution) super.run();
    }

    @Override
    protected abstract ShortestPathSolution doRun();

    @Override
    public ShortestPathData getInputData() {
        return (ShortestPathData) super.getInputData();
    }

    /**
     * Notify all observers that the origin has been processed.
     * 
     * @param node Origin.
     */
    public void notifyOriginProcessed(Node node) {
        for (ShortestPathObserver obs: getObservers()) {
            obs.notifyOriginProcessed(node);
        }
    }

    /**
     * Notify all observers that a node has been reached for the first time.
     * 
     * @param node Node that has been reached.
     */
    public void notifyNodeReached(Node node) {
        for (ShortestPathObserver obs: getObservers()) {
            obs.notifyNodeReached(node);
        }
    }

    /**
     * Notify all observers that a node has been marked, i.e. its final value has
     * been set.
     * 
     * @param node Node that has been marked.
     */
    public void notifyNodeMarked(Node node) {
        for (ShortestPathObserver obs: getObservers()) {
            obs.notifyNodeMarked(node);
        }
    }

    /**
     * Notify all observers that the destination has been reached.
     * 
     * @param node Destination.
     */
    public void notifyDestinationReached(Node node) {
        for (ShortestPathObserver obs: getObservers()) {
            obs.notifyDestinationReached(node);
        }
    }
}
