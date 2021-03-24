package com.hyper.agarai.ai;

public class MultilayeredPerceptron {
	public static final int COEFFICIENT_MAX_VALUE = 1;

	private Perceptron[][] structure;

	private int inputs, outputs, perLayer, layers;

	private float[] lastInputs = new float[0];

	public MultilayeredPerceptron(int inputs, int outputs, int hiddenLayers, int perLayer) {
		this.inputs = inputs;
		this.outputs = outputs;
		this.perLayer = perLayer;
		this.layers = hiddenLayers + 1;
		this.structure = new Perceptron[layers][perLayer];

		//First layer
		for(int i = 0; i < perLayer; i++) {
			float[] inputWeights = new float[inputs];
			for(int index = 0; index < inputs; index++)
				inputWeights[index] = random()*COEFFICIENT_MAX_VALUE;
			this.structure[0][i] = new Perceptron(inputWeights);
		}

		//Middle layers
		for(int layer = 1; layer < this.layers-1; layer++) {
			for(int i = 0; i < perLayer; i++) {
				float[] inputWeights = new float[perLayer];
				for(int index = 0; index < perLayer; index++)
					inputWeights[index] = random()*COEFFICIENT_MAX_VALUE;
				this.structure[layer][i] = new Perceptron(inputWeights);
			}
		}

		//Output layer
		this.structure[this.layers-1] = new Perceptron[outputs];
		for(int i = 0; i < outputs; i++) {
			float[] inputWeights = new float[perLayer];
			for(int index = 0; index < perLayer; index++)
				inputWeights[index] = random()*COEFFICIENT_MAX_VALUE;
			this.structure[layers-1][i] = new Perceptron(inputWeights);
		}
	}

	private MultilayeredPerceptron(MultilayeredPerceptron clone) {
		this.inputs = clone.inputs;
		this.outputs = clone.outputs;
		this.layers = clone.layers;
		this.perLayer = clone.perLayer;
		this.structure = clone.structure.clone();
		for(int layer = 0; layer < structure.length; layer++)
			for(int i = 0; i < clone.structure[layer].length; i++)
				this.structure[layer][i] = new Perceptron(clone.structure[layer][i].getWeights());
	}

	public MultilayeredPerceptron(float[][][] data) {
		int layer = 0, perceptron = 0, coefficient = 0;
		this.inputs = data[layer][perceptron].length;
		this.outputs = data[data.length-1].length;
		this.layers = data.length;
		this.perLayer = data[0].length;
		this.structure = new Perceptron[layers][perLayer];

		//First layer
		for(perceptron = 0; perceptron < perLayer; perceptron++) {
			float[] inputWeights = new float[perLayer];
			for(coefficient = 0; coefficient < inputs; coefficient++)
				inputWeights[coefficient] = data[layer][perceptron][coefficient];
			this.structure[layer][perceptron] = new Perceptron(inputWeights);
		}

		//Middle layers
		for(layer = 1; layer < this.layers-1; layer++) {
			for(perceptron = 0; perceptron < perLayer; perceptron++) {
				float[] inputWeights = new float[perLayer];
				for(coefficient = 0; coefficient < perLayer; coefficient++)
					inputWeights[coefficient] = data[layer][perceptron][coefficient];
				this.structure[layer][perceptron] = new Perceptron(inputWeights);
			}
		}

		//Output layer
		this.structure[this.layers-1] = new Perceptron[outputs];
		for(perceptron = 0; perceptron < outputs; perceptron++) {
			float[] inputWeights = new float[perLayer];
			for(coefficient = 0; coefficient < perLayer; coefficient++)
				inputWeights[coefficient] = data[layer][perceptron][coefficient];
			this.structure[layers-1][perceptron] = new Perceptron(inputWeights);
		}
	}

	public float[] run(float[] inputs) {
		if(inputs.length != this.inputs)
			throw new IllegalArgumentException("Wrong number of inputs !");
		for(float f : inputs)
			if(!Float.isFinite(f))
				throw new IllegalStateException();
		this.lastInputs = inputs;

		//First layer
		float[] in = new float[this.perLayer];
		for(int i = 0; i < this.perLayer; i++) {
			in[i] = this.structure[0][i].run(inputs);
		}

		//Middle layers
		for(int layer = 1; layer < this.layers-1; layer++) {
			float[] nextInputs = new float[this.perLayer];
			for(int i = 0; i < this.perLayer - 1; i++)
				nextInputs[i] = this.structure[layer][i].run(in);
			in = nextInputs;
		}

		//Last layer
		float[] result = new float[this.outputs];
		for(int i = 0; i < this.outputs; i++)
			result[i] = this.structure[this.layers-1][i].run(in);
		return result;
	}

	public MultilayeredPerceptron createChild(float mutationRate) {
		MultilayeredPerceptron child = new MultilayeredPerceptron(this);
		child.mutate(mutationRate);
		return child;
	}

	public void mutate(float rate) {
		int layer = (int) (Math.random()*(this.structure.length-1));
		int i = (int) (Math.random()*(this.structure[layer].length-1));
		this.structure[layer][i].mutate(rate);
	}

	public static float random() {
		return (float) (Math.random()*2-1);
	}

	public float[] getLastInputs() {
		return lastInputs;
	}

	public Perceptron[][] getStructure() {
		return structure;
	}

	@Override
	public String toString() {
		String str = super.toString() + "\nweights : ";
		for(int layer = 0; layer < this.layers; layer++) {
			str += "{\n";
			for(int i = 0; i < this.structure[layer].length; i++) {
				Perceptron p = structure[layer][i];
				str += "	[";
				for(int weight = 0; weight < p.getWeights().length; weight++) {
					str += p.getWeights()[weight];
					if(weight < p.getWeights().length-1)
						str += ", ";
				}
				str += "]";
				if(i < this.structure[layer].length-1) str += ",\n";
			}
			str += "\n}";
			if(layer < this.layers-1)
				str += ",\n";
		}
		return str;
	}
}
