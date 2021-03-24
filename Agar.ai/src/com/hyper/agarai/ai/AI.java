package com.hyper.agarai.ai;

import java.awt.Color;

import org.joml.Vector2f;

import com.hyper.agarai.game.Blob;
import com.hyper.agarai.game.Food;
import com.hyper.agarai.utils.Maths;

public class AI {
	public static final float MAX_RANGE = 500;
	public static final String[] INPUTS = {"mass",
			"nearest_blob_distance", "nearest_blob_mass",
			"nearest_food_distance",
			"memory", "constant"
	},
			OUTPUTS = {
					"choiche", "memory"
	};
	private final Blob entity;
	private final MultilayeredPerceptron mind;
	private float memory = 0;
	private final Color assignedColor;

	public AI(Blob entity, int hiddenLayers, int perLayer) {
		this.entity = entity;
		this.mind = new MultilayeredPerceptron(INPUTS.length, OUTPUTS.length, hiddenLayers, perLayer);
		this.assignedColor = new Color((float)Math.random(), (float)Math.random(), (float)Math.random());
	}

	private AI(AI clone, Blob blob, float mutationRate) {
		this.entity = blob;
		this.mind = clone.mind.createChild(mutationRate);
		this.assignedColor = clone.getColor();
	}

	public AI(Blob entity, float[][][] fs) {
		this.entity = entity;
		this.mind = new MultilayeredPerceptron(fs);
		this.assignedColor = new Color((float)Math.random(), (float)Math.random(), (float)Math.random());
	}

	public void update(Blob b, Food f) {
		Vector2f vBlob = (b == null)?new Vector2f(MAX_RANGE, 0):new Vector2f(b.getPosition()).sub(this.entity.getPosition()), vFood = (f == null)?new Vector2f(MAX_RANGE, 0):new Vector2f(f.getPosition()).sub(this.entity.getPosition());
		float dBlob = vBlob.length(),
				dFood = vFood.length();

		if(dFood > MAX_RANGE) {
			dFood = MAX_RANGE;
			//vFood = Maths.fromAngle((float) (Math.random()*Math.PI*2)).mul(MAX_RANGE);
		}
		if(dBlob > MAX_RANGE) {
			dBlob = MAX_RANGE;
			//vBlob = Maths.fromAngle((float) (Math.random()*Math.PI*2)).mul(MAX_RANGE);
		}

		float[] inputs = new float[] {
				this.entity.getMass()/Blob.INIT_MASS,
				1-dBlob/MAX_RANGE,
				(b == null?0:b.getMass()/Blob.INIT_MASS),
				1-dFood/MAX_RANGE,
				memory,
				1
		},
				outputs = this.mind.run(inputs);
		
		float willToGoTowardsFood = 1-Math.abs(outputs[0]), willToGoTowardsBlob = outputs[0], willToFleeBlob = -outputs[0];
		if(willToGoTowardsBlob < 0) willToGoTowardsBlob = 0;
		if(willToFleeBlob < 0) willToFleeBlob = 0;
		
		float angle = Maths.getAngle(vBlob);
		
		entity.setVelocity(Maths.fromAngle((float) (
				willToGoTowardsFood*Maths.getAngle(vFood) + willToGoTowardsBlob*angle + willToFleeBlob*(angle+Math.PI)
				)).mul(Blob.MAX_SPEED));
		this.memory = outputs[1];
	}

	public AI createChild(Blob entity, float mutationRate) {
		return new AI(this, entity, mutationRate);
	}

	public Blob getBlob() {
		return this.entity;
	}

	public MultilayeredPerceptron getMind() {
		return this.mind;
	}

	@Override
	public String toString() {
		return "AI entity : " + this.entity.toString() + "\nmind : " + this.mind;
	}

	public Color getColor() {
		return assignedColor;
	}
}
