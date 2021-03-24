package com.hyper.agarai.render;

import org.joml.Vector2fc;

public interface Camera {
	public void update();

	public float getWorldSize();

	public Vector2fc getCameraPosition();
}
