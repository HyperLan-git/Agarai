package com.hyper.agarai.game;

import java.awt.Color;

import org.joml.Vector2f;
import org.joml.Vector2fc;

public class Food {
	private final Vector2f position;
	private Color color;

	public Food(Vector2f position) {
		this(position.x, position.y);
	}

	public Food(float x, float y) {
		this.position = new Vector2f(x, y);
		generateColor();
	}

	private void generateColor() {
		do {
			this.color = new Color((int)(Math.random()*255), (int)(Math.random()*255), (int)(Math.random()*255));
		} while ((color.getBlue() & color.getGreen() & color.getRed()) >= 100);
	}

	public Vector2fc getPosition() {
		return new Vector2f(this.position);
	}

	public Color getColor() {
		return color;
	}
}
