package org.insa.algo.carpooling;

import org.insa.graph.Node;
import org.insa.graphics.drawing.Drawing;
import org.insa.graphics.drawing.overlays.PointSetOverlay;

import java.awt.*;

public class CarPoolingGraphicObserver implements CarPoolingObserver {
	// Drawing and Graph drawing
	protected Drawing drawing;
	protected PointSetOverlay psOverlay1, psOverlay2;

	public CarPoolingGraphicObserver(Drawing drawing) {
		this.drawing = drawing;
		psOverlay1 = drawing.createPointSetOverlay(1, Color.CYAN);
		psOverlay2 = drawing.createPointSetOverlay(1, Color.BLUE);
	}
	@Override
	public void notifyOriginProcessed(Node node) {
		// drawing.drawMarker(node.getPoint(), Color.RED);
	}

	@Override
	public void notifyNodeReached(Node node) {
		psOverlay1.addPoint(node.getPoint());
	}

	@Override
	public void notifyNodeMarked(Node node) {
		psOverlay2.addPoint(node.getPoint());
	}

	@Override
	public void notifyDestinationReached(Node node) {
		// drawing.drawMarker(node.getPoint(), Color.RED);
	}

}
