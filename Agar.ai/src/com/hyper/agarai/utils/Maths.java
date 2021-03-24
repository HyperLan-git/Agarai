package com.hyper.agarai.utils;

import org.joml.Vector2f;
import org.joml.Vector2fc;

public abstract class Maths {
	/**
	 * @param x value to pass in
	 * @return the sigmoid of that value
	 */
	public static final float sigmoid(float x) {
		return (float) (1/(1+Math.exp(-x)));
	}
	
	/**
	 * @return angle in radians for polar coordinates
	 */
	public static final float getAngle(Vector2fc vec) {
		float result = (float) Math.atan2(vec.y(), vec.x());
		if(result < 0) result += 2*Math.PI;
		return result;
	}

	/**
	 * @param angle an angle in radians
	 * @return a normalized vector with that angle
	 */
	public static final Vector2f fromAngle(float angle) {
		return new Vector2f((float)Math.cos(angle), (float)Math.sin(angle));
	}
}
