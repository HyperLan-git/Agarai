package com.hyper.agarai.game;

import java.awt.Color;

import org.joml.Vector2f;
import org.joml.Vector2fc;

import com.hyper.agarai.ai.AI;

public class Blob {
	public static final float MAX_SPEED = 10, INIT_MASS = 40, DEATH_MASS = 20;

	private World world;

	private Vector2f position, velocity = new Vector2f();

	private float mass;

	private Color color;

	private AI ai = null;

	public Blob(Vector2f position, float mass, World world) {
		this(position.x, position.y, mass, world);
	}

	public Blob(float x, float y, float initMass, World world) {
		this(x, y, initMass, world, new Color((int)(255*Math.random()), (int)(Math.random()*255.0f), (int)(Math.random()*255.0f)));
	}

	public Blob(float x, float y, float initMass, World world, Color c) {
		this.position = new Vector2f(x, y);
		this.mass = initMass;
		this.world = world;
		this.color = c;
	}

	public void update() {
		if(this.velocity.length() > MAX_SPEED)
			this.velocity.normalize().mul(MAX_SPEED);
		this.position.add(this.velocity);
	}

	public void teleport(Vector2f location) {
		this.position = location;
	}

	public void setVelocity(Vector2f velocity) {
		this.velocity = velocity;
	}

	public void setMass(float mass) {
		this.mass = mass;
	}

	public float getMass() {
		return mass;
	}

	public Vector2fc getPosition() {
		return new Vector2f(this.position);
	}

	public World getWorld() {
		return world;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public void setAi(AI ai) {
		this.ai = ai;
		this.color = ai.getColor();
	}

	public void teleport(float x, float y) {
		this.position = new Vector2f(x, y);
	}

	public void eat(float mass) {
		//A = PI * R * R <=> R = sqrt(A/PI)
		this.setMass((float) (this.getMass() + Math.sqrt(mass / Math.PI)));
	}

	public AI getAI() {
		return this.ai;
	}
}
