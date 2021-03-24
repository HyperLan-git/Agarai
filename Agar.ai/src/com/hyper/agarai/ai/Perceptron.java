package com.hyper.agarai.ai;

public class Perceptron {
	private float[] inputWeights;

	private float lastValue = 0;

	public Perceptron(float[] inputWeights) {
		this.inputWeights = inputWeights;
	}

	public float run(float[] inputs) {
		if(inputs.length != this.inputWeights.length)
			throw new IllegalArgumentException("Wrong number of inputs !");
		
		float value = 0;
		for(int i = 0; i < inputs.length; i++)
			value += this.inputWeights[i]*inputs[i];
		//XXX
		this.lastValue = (float) Math.tanh(value);
		//this.lastValue = value;
		if(lastValue > 1) lastValue = 1;
		if(lastValue < -1) lastValue = -1;
		
		return lastValue;
	}

	public void mutate(float rate) {
		for(int i = 0; i < this.inputWeights.length; i++)
			this.inputWeights[i] += (Math.random()*rate*2)-rate;
	}

	public float getLastValue() {
		return lastValue;
	}

	public float[] getWeights() {
		return inputWeights;
	}
}
