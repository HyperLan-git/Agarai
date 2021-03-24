package com.hyper.agarai.render;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPanel;

import org.joml.Vector2f;
import org.joml.Vector2i;

import com.hyper.agarai.game.Blob;
import com.hyper.agarai.game.Food;
import com.hyper.agarai.game.World;

public class Canvas extends JPanel {
	private World world;
	private Camera camera = new FreeCamera(this);

	private AIPanel panel = null;

	public Canvas(World world) {
		this.world = world;
		this.addMouseListener(new MouseListener() {
			@Override
			public void mousePressed(MouseEvent e) {
				Vector2i mousePos = new Vector2i(e.getX(), e.getY());
				for(Blob blob : world.getBlobs()) {
					Vector2i coords = toGraphicalCoordinates(blob.getPosition().x(), blob.getPosition().y());
					if(mousePos.distance(coords) < 2*blob.getMass()*camera.getWorldSize() && blob.getAI() != null)
						panel = new AIPanel(blob.getAI());
				}
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {	}

			@Override
			public void mouseExited(MouseEvent arg0) {		}

			@Override
			public void mouseEntered(MouseEvent arg0) {		}

			@Override
			public void mouseClicked(MouseEvent arg0) {		}
		});
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		float worldSize = camera.getWorldSize();
		Rectangle window = g.getClipBounds();

		//Background outside the world
		g.setColor(Color.GRAY);
		g.fillRect(0, 0, window.width, window.height);

		//Background inside the world
		g.setColor(Color.WHITE);
		Vector2i origin = toGraphicalCoordinates(0, 0);
		g.fillRect(origin.x, origin.y,
				(int) (this.world.getWidth()*worldSize), (int) (this.world.getHeight()*worldSize));

		//Food
		for(Food food : world.getFood()) {
			g.setColor(food.getColor());
			Vector2i coords = toGraphicalCoordinates(food.getPosition().x() - 10 * worldSize,
					food.getPosition().y() - 10 * worldSize);
			if(coords.x > window.width + 10 * worldSize || coords.x + 10*worldSize < 0 ||
					coords.y > window.height + 10*worldSize || coords.y + 10*worldSize < 0)
				continue;
			g.fillOval(coords.x, coords.y, (int) (20 * worldSize), (int) (20 * worldSize));
		}

		//Blobs
		for(Blob blob : world.getBlobs()) {
			g.setColor(blob.getColor());
			Vector2i coords = toGraphicalCoordinates(blob.getPosition().x() - blob.getMass(),
					blob.getPosition().y() - blob.getMass());
			if(coords.x > window.width + blob.getMass()*worldSize || coords.x + blob.getMass()*worldSize < 0 ||
					coords.y > window.height + blob.getMass()*worldSize || coords.y + blob.getMass()*worldSize < 0)
				continue;
			g.fillOval(coords.x, coords.y,
					(int) (blob.getMass()*2*worldSize), (int) (blob.getMass()*2*worldSize));
			g.setColor(Color.black);
			g.drawOval(coords.x, coords.y,
					(int) (blob.getMass()*2 * worldSize), (int)(blob.getMass()*2 * worldSize));
		}

		if(panel != null)
			panel.paint(g);

		g.setColor(Color.BLACK);
		g.drawString("Current generation : " + this.world.getGeneration(), window.width-130, 15);
	}

	private Vector2i toGraphicalCoordinates(float x, float y) {
		Vector2f actualCameraPos = new Vector2f(camera.getCameraPosition()).mul(camera.getWorldSize());
		return new Vector2i((int)(x*camera.getWorldSize() + this.getWidth()/2.0f - actualCameraPos.x),
				(int)(y*camera.getWorldSize() + this.getHeight()/2.0f - actualCameraPos.y));
	}

	public void updateCamera() {
		this.camera.update();
	}

	public AIPanel getAIPanel() {
		return panel;
	}

	public void setCamera(Camera camera) {
		this.camera = camera;
	}
}
