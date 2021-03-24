package com.hyper.agarai.io;

import java.util.Scanner;

import com.hyper.agarai.Agarai;
import com.hyper.agarai.ai.AI;
import com.hyper.agarai.ai.Perceptron;
import com.hyper.agarai.render.AIPanel;

public class ConsoleHandler extends Thread {
	private Agarai instance;

	private Scanner sc = new Scanner(System.in);

	public ConsoleHandler(Agarai instance) {
		this.instance = instance;
	}

	@Override
	public void run() {
		while(true) {
			try {
				while(!sc.hasNext());
				String str = sc.next();
				System.out.println("Command : " + str);
				if(str.contentEquals("saveSelectedAI")) {
					AIPanel panel = instance.getRenderer().getAIPanel();
					if(panel != null)
						System.out.println(panel.getAI().toString());
				}
				if(str.contentEquals("spawnPlayer"))
					instance.spawnPlayer();

				if(str.startsWith("spawnAI")) {
					String buildFrom = str.substring(8);
					StringBuilder numberRead = null;
					float[] weights = new float[AI.INPUTS.length];
					Perceptron[][] mind = new Perceptron[Agarai.HIDDEN_LAYERS][Agarai.PER_LAYER];
					int state = -1, //Will be 0 for array of perceptron, 1 for the weights
							weightCounter = 0; 
					for(int i = 0; i < buildFrom.length(); i++) {
						char c = buildFrom.charAt(i);
						if(Character.isDigit(c)) {
							if(numberRead == null) numberRead = new StringBuilder(3);
							numberRead.append(c);
						} else if(c == '.')
							numberRead.append(c);
						else {
							if(numberRead != null) {
								float value = Float.valueOf(numberRead.toString());
								if(state == 1) {
									weights[weightCounter++] = value;
								}
							}
							numberRead = null;

							if(c == '{' || c == '[')
								state++;
							if(c == '}'|| c == ']') {
								if(state == 1) {

								}
								state--;
							}
						}
					}
					instance.getWorld().spawn(null);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void finalize() throws Throwable {
		this.sc.close();
		super.finalize();
	}
}
