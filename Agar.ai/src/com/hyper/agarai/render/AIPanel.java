package com.hyper.agarai.render;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

import com.hyper.agarai.ai.AI;
import com.hyper.agarai.ai.Perceptron;

public class AIPanel extends JPanel {
	private AI ai;

	public AIPanel(AI ai) {
		this.ai = ai;
	}

	@Override
	public void paint(Graphics g) {
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
		g.setColor(Color.BLACK);
		g.drawRect(0, 0, this.getWidth(), this.getHeight());
		float[] lastInputs = ai.getMind().getLastInputs();
		Perceptron[][] structure = ai.getMind().getStructure();
		int yIncrement = (this.getHeight()-20)/lastInputs.length,
				xIncrement = (this.getWidth()-20)/(structure.length+1);
		for(int i = 0; i < lastInputs.length; i++) for(int index = 0; index < structure[1].length; index++) {
			Perceptron p = structure[1][index];
			for(int weight = 0; weight < p.getWeights().length; weight++) {
				int color = (int) (p.getWeights()[weight]*255);
				color = (color > 255)?255:(color < 0)?0:color;
				g.setColor(new Color(color, color, color));
				g.drawLine(25, 25+i*yIncrement, 25 + xIncrement, (int) (25+(index+0.25)*(this.getHeight()-25)/structure[1].length));
			}
		}

		//Draw lines between input and first layer
		yIncrement = (this.getHeight()-25)/structure[0].length;
		int lastYIncrement = (this.getHeight()-25)/structure[0][0].getWeights().length;
		for(int i = 0; i < structure[0].length; i++) {
			Perceptron p = structure[0][i];
			for(int weight = 0; weight < p.getWeights().length; weight++) {
				int color = (int) (p.getWeights()[weight]*255);
				color = (color > 255)?255:(color < 0)?0:color;
				g.setColor(new Color(color, color, color));
				g.drawLine(25, (int) ((weight+0.75)*lastYIncrement),
						25 + xIncrement, (int) (13 + (i+0.5)*yIncrement));
			}
		}
		//Draw lines between other layers
		for(int layer = 1; layer < structure.length; layer++) {
			yIncrement = (this.getHeight()-25)/structure[layer].length;
			lastYIncrement = (this.getHeight()-25)/structure[layer-1].length;
			for(int i = 0; i < structure[layer].length; i++) {
				Perceptron p = structure[layer][i];
				for(int weight = 0; weight < p.getWeights().length; weight++) {
					int color = (int) (p.getWeights()[weight]*255);
					color = (color > 255)?255:(color < 0)?0:color;
					g.setColor(new Color(color, color, color));
					g.drawLine(25 + layer*xIncrement, (int) ((weight+0.75)*lastYIncrement),
							25 + (layer+1)*xIncrement, (int) (13 + (i+0.5)*yIncrement));
				}
			}
		}

		//Draw the inputs
		yIncrement = (this.getHeight()-25)/structure[0][0].getWeights().length;
		for(int i = 0; i < lastInputs.length; i++) {
			int color = (int) (lastInputs[i]*64.0f+128);
			color = (color>255)?255:(color<0)?0:color;
			Color c = new Color(color, color, color);
			g.setColor(c);
			g.fillOval(10, 10+i*yIncrement, 30, 30);
			g.setColor(Color.BLACK);
			g.drawOval(10, 10+i*yIncrement, 30, 30);
			g.setColor(c.getRed() > 128?Color.BLACK:Color.WHITE);
			drawCenteredString("" + Math.floor(lastInputs[i]*100)/100.0f, g, 25, 30+i*yIncrement);
		}
		//Draw the perceptrons
		for(int layer = 0; layer < structure.length; layer++) {
			Perceptron[] l = structure[layer];
			yIncrement = (this.getHeight()-25)/l.length;
			for(int i = 0; i < l.length; i++) {
				Perceptron toDraw = l[i];
				int color = (int) (toDraw.getLastValue()*64.0f+128);
				color = (color>255)?255:(color<0)?0:color;
				Color c = new Color(color, color, color);
				g.setColor(c);
				g.fillOval(10 + (layer+1)*xIncrement, (int) ((i+0.5) * yIncrement), 30, 30);
				g.setColor(Color.BLACK);
				g.drawOval(10 + (layer+1)*xIncrement, (int) ((i+0.5) * yIncrement), 30, 30);
				g.setColor(c.getRed() > 128?Color.BLACK:Color.WHITE);
				drawCenteredString("" + Math.floor(toDraw.getLastValue()*100)/100.0f, g, 25 + (layer+1)*xIncrement, (int) ((0.5+i)*yIncrement) + 15);
			}
		}
	}

	@Override
	public int getWidth() {
		return 200;
	}

	@Override
	public int getHeight() {
		return 350;
	}

	private void drawCenteredString(String str, Graphics g, int x, int y) {
		int width = g.getFontMetrics().stringWidth(str);
		g.drawString(str, x - width/2, y);
	}

	public AI getAI() {
		return ai;
	}
}
