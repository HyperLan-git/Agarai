package com.hyper.agarai.game;

import java.util.ArrayList;

import org.joml.Vector2f;
import org.joml.Vector2fc;

public class World {
	public static final float FOOD_LOSS_BY_WALL = 0.5f,
			FOOD_GAIN_PER_KILL = 40,
			FOOD_SPAWN_RADIUS_AROUND_PLAYER = 500;

	private ArrayList<Blob> blobs = new ArrayList<>();
	private ArrayList<Food> food = new ArrayList<>();

	private int width, height, generation = 0;

	private float foodAbundance, fps;

	public World(int width, int height, float foodAbundance, float fps) {
		this.width = width;
		this.height = height;
		this.foodAbundance = foodAbundance;
		this.fps = fps;
	}

	public synchronized void update() {
		int players = blobs.size();
		for(int i = 0; i < players; i++) {
			float currFood = foodAbundance/fps;
			while(Math.random() < currFood--) {
				/*Vector2f position = Maths.fromAngle((float) (Math.random()*Math.PI*2))
						.mul((float) (Math.random()*FOOD_SPAWN_RADIUS_AROUND_PLAYER)).add(blobs.get(i).getPosition());
				if(position.x < width/4) position.x = width/4;
				if(position.x > this.width*3/4) position.x = this.width*3/4;
				if(position.y < height/4) position.y = height/4;
				if(position.y > this.height*3/4) position.y = this.height*3/4;*/
				Vector2f position = new Vector2f((float)Math.random()*width, (float)Math.random()*height);
				food.add(new Food(position));
			}
		}
		for(Blob blob : blobs) {
			blob.update();
			if(blob.getPosition().x() < 0) {
				blob.teleport(0, blob.getPosition().y());
				blob.setMass(blob.getMass()-FOOD_LOSS_BY_WALL);
			}
			if(blob.getPosition().x() > this.width) {
				blob.teleport(this.width, blob.getPosition().y());
				blob.setMass(blob.getMass()-FOOD_LOSS_BY_WALL);
			}
			if(blob.getPosition().y() < 0) {
				blob.teleport(blob.getPosition().x(), 0);
				blob.setMass(blob.getMass()-FOOD_LOSS_BY_WALL);
			}
			if(blob.getPosition().y() > this.height) {
				blob.teleport(blob.getPosition().x(), this.height);
				blob.setMass(blob.getMass()-FOOD_LOSS_BY_WALL);
			}

			for(int i = food.size() - 1; i >= 0; i--) {
				Food f = food.get(i);
				Vector2f v = new Vector2f();
				v.add(f.getPosition()).sub(blob.getPosition());

				if(v.length() < Blob.INIT_MASS * 10 / blob.getMass() + blob.getMass()) {
					food.remove(i);
					blob.eat(1);
				}
			}
		}
	}

	public void nextGeneration() {
		food = new ArrayList<>();
		generation++;
	}

	public synchronized Blob getNearestBlob(Vector2fc pos) {
		Blob result = null;
		for(Blob b : blobs)
			if(result == null || b.getPosition().distanceSquared(pos) < result.getPosition().distanceSquared(pos))
				result = b;
		return result;
	}
	public synchronized Blob getNearestBlob(Blob blob) {
		Blob result = null;
		for(Blob b : blobs) if(b != blob)
			if(result == null || b.getPosition().distanceSquared(blob.getPosition()) < result.getPosition().distanceSquared(blob.getPosition()))
				result = b;
		return result;
	}


	public synchronized Food getNearestFood(Vector2fc vector2fc) {
		Food result = null;
		for(Food f : food)
			if(result == null || f.getPosition().distance(vector2fc) < result.getPosition().distance(vector2fc))
				result = f;
		return result;
	}

	public synchronized Blob[] getBlobs() {
		return this.blobs.toArray(new Blob[this.blobs.size()]);
	}

	public synchronized Food[] getFood() {
		return this.food.toArray(new Food[this.food.size()]);
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public synchronized void spawn(Blob blob) {
		this.blobs.add(blob);
	}

	public synchronized void kill(Blob blob) {
		this.blobs.remove(blob);
	}

	public Vector2f getVector2Wall(Vector2fc position) {
		if(position.x() <= position.y() && position.x() <= this.width/2)
			return new Vector2f(-position.x(), 0);
		else if(position.x() >= position.y() && position.x() > this.width/2)
			return new Vector2f(this.width-position.x(), 0);
		else if(position.y() <= position.x() && position.y() <= this.height/2)
			return new Vector2f(0, -position.y());
		else if(position.y() >= position.x() && position.y() > this.height/2)
			return new Vector2f(0, this.height-position.y());
		return null;
	}

	public int getGeneration() {
		return generation;
	}
}
