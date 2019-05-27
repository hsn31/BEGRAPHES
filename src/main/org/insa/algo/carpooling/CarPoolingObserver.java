package org.insa.algo.carpooling;

import org.insa.graph.Node;

public interface CarPoolingObserver {
	/**
	 * Notify the observer that the origin has been processed.
	 *
	 * @param node Origin.
	 */
	public void notifyOriginProcessed(Node node);

	/**
	 * Notify the observer that a node has been reached for the first
	 * time.
	 *
	 * @param node Node that has been reached.
	 */
	public void notifyNodeReached(Node node);

	/**
	 * Notify the observer that a node has been marked, i.e. its final
	 * value has been set.
	 *
	 * @param node Node that has been marked.
	 */
	public void notifyNodeMarked(Node node);

	/**
	 * Notify the observer that the destination has been reached.
	 *
	 * @param node Destination.
	 */
	public void notifyDestinationReached(Node node);

	//Notify the observer that a node has been reached by A and B and a Merged Label has been inserted

	public void notifyNodeMerged(Node node);

	//Notify the observer that a merged label has been marked
	
	public void notifyMergedNodeMarked(Node node);

}
