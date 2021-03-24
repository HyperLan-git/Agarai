package com.hyper.agarai.render;

import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import org.joml.Vector2f;
import org.joml.Vector2fc;
import org.joml.Vector2i;

public class FreeCamera implements Camera, MouseWheelListener, MouseListener {
	private float worldSize = 1;
	private Vector2f cameraPos = new Vector2f();
	private boolean clicked = false;
	private Vector2i lastMouseLocation = new Vector2i();

	public FreeCamera(Canvas canvas) {
		canvas.addMouseListener(this);
		canvas.addMouseWheelListener(this);
	}

	public FreeCamera(Canvas canvas, Vector2fc cameraPos, float worldSize) {
		
	}

	@Override
	public void update() {
		Point loc = MouseInfo.getPointerInfo().getLocation();
		if(clicked)
			cameraPos.add((lastMouseLocation.x-loc.x)/worldSize, (lastMouseLocation.y-loc.y)/worldSize);
		lastMouseLocation = new Vector2i(loc.x, loc.y);
	}

	@Override
	public float getWorldSize() {
		return worldSize;
	}

	@Override
	public Vector2f getCameraPosition() {
		return cameraPos;
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		double scrollAmount = e.getPreciseWheelRotation();
		this.worldSize -= 0.01*scrollAmount;
	}

	@Override
	public void mousePressed(MouseEvent e) {
		clicked = true;
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		clicked = false;
	}

	@Override
	public void mouseClicked(MouseEvent e) {	}

	@Override
	public void mouseEntered(MouseEvent e) {	}

	@Override
	public void mouseExited(MouseEvent e) {		}
}
