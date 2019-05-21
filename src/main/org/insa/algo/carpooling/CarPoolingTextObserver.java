package org.insa.algo.carpooling;

import org.insa.graph.Node;

import java.io.PrintStream;

public class CarPoolingTextObserver implements CarPoolingObserver {
	private final PrintStream stream;

	public CarPoolingTextObserver(PrintStream stream) {
		this.stream = stream;
	}

	@Override
	public void notifyOriginProcessed(Node node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void notifyNodeReached(Node node) {
		stream.println("Node " + node.getId() + " reached.");
	}

	@Override
	public void notifyNodeMarked(Node node) {
		stream.println("Node " + node.getId() + " marked.");
	}
	@Override
	public void notifyNodeMerged(Node node) {
		stream.println("Node " + node.getId() + " merged.");
	}
	@Override
	public void notifyMergedNodeMarked(Node node) {
		stream.println("Node " + node.getId() + " marked.");
	}

	@Override
	public void notifyDestinationReached(Node node) {
		// TODO Auto-generated method stub

	}

}
