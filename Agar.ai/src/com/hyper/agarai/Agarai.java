package com.hyper.agarai;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.MouseInfo;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JSlider;

import org.joml.Vector2f;

import com.hyper.agarai.ai.AI;
import com.hyper.agarai.game.Blob;
import com.hyper.agarai.game.World;
import com.hyper.agarai.io.ConsoleHandler;
import com.hyper.agarai.io.Handler;
import com.hyper.agarai.render.Canvas;
import com.hyper.agarai.render.PlayerCamera;

public class Agarai {
	public static final float WINNERS = 1/3.0f;
	public static final int HIDDEN_LAYERS = 1,
			PER_LAYER = 4;

	private final float fps;
	private JFrame window;
	private JSlider speedSlider = new JSlider() {
		protected void paintComponent(java.awt.Graphics g) {
			super.paintComponent(g);
			g.setColor(Color.BLACK);
			g.drawString("Animation speed", 40, 25);
		};
	};
	private Canvas renderer;

	private World world;
	private int population;
	private float foodLoss, mutationRate;
	private ArrayList<AI> ais = new ArrayList<>(), winners = new ArrayList<>();

	private Blob player;

	private boolean loop = true;

	public Agarai(int framesPerSecond) {
		this.fps = framesPerSecond;
		this.window = new JFrame("Agar.ai");
		setup();
	}

	public void run() {
		double lastFrameTime = getTimeSeconds();

		while(true) {
			while(lastFrameTime + 1/(fps*this.speedSlider.getValue()) <= getTimeSeconds()) {
				this.update();
				lastFrameTime += 1/(fps*this.speedSlider.getValue());
			}
			if(this.window.isShowing())
				this.window.repaint();
		}
	}

	private void setup() {
		String response = null;
		while(response == null) {
			response = JOptionPane.showInputDialog(this.window, "Veuillez entrer le nombre de blobs", "Paramétrage", JOptionPane.QUESTION_MESSAGE);
			try {
				this.population = Integer.valueOf(response);
			} catch (Exception e) {response = null;}
		}

		response = null;
		int size = 0;
		while(response == null) {
			response = JOptionPane.showInputDialog(null, "Veuillez entrer la taille du monde", "Paramétrage", JOptionPane.QUESTION_MESSAGE);
			try {
				size = Integer.valueOf(response);
			} catch (Exception e) {response = null;}
		}

		response = null;
		float foodAbundance = 0;
		while(response == null) {
			response = JOptionPane.showInputDialog(null, "Veuillez entrer la quantité d'apparition de nourriture toutes les secondes par blob", "Paramétrage", JOptionPane.QUESTION_MESSAGE);
			try {
				foodAbundance = Float.valueOf(response);
			} catch (Exception e) {response = null;}
		}

		response = null;
		while(response == null) {
			response = JOptionPane.showInputDialog(null, "Veuillez entrer la perte de masse par secondes (Les blobs commencent à 40)", "Paramétrage", JOptionPane.QUESTION_MESSAGE);
			try {
				foodLoss = Float.valueOf(response);
			} catch (Exception e) {response = null;}
		}

		response = null;
		while(response == null) {
			response = JOptionPane.showInputDialog(null, "Veuillez entrer le taux de mutation entre chaque génération", "Paramétrage", JOptionPane.QUESTION_MESSAGE);
			try {
				mutationRate = Float.valueOf(response);
			} catch (Exception e) {response = null;}
		}

		//Generate world and ais with settings chosen
		this.world = new World(size, size, foodAbundance, fps);
		spawnAis();

		this.speedSlider.setMinimum(1);
		this.speedSlider.setMaximum(100);
		this.speedSlider.setValue(1);
		this.speedSlider.setMinimumSize(new Dimension(0, 50));
		this.speedSlider.setPaintTicks(true);
		this.speedSlider.setPaintLabels(true);

		this.renderer = new Canvas(world);
		this.window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.window.setContentPane(renderer);
		this.window.setMinimumSize(new Dimension(500, 500));
		FlowLayout manager = new FlowLayout();
		this.window.setLayout(manager);
		this.window.add(speedSlider);
		this.window.revalidate();
		this.window.setVisible(true);
		Handler h = new Handler(this);
		this.speedSlider.addKeyListener(h);
		this.window.addKeyListener(h);

		new ConsoleHandler(this).start();
	}

	private void update() {
		this.renderer.updateCamera();
		if(!loop)
			return;
		if(this.player != null) {
			Vector2f loc = new Vector2f(MouseInfo.getPointerInfo().getLocation().x, MouseInfo.getPointerInfo().getLocation().y);
			loc.sub(this.window.getX() + this.window.getWidth()/2, this.window.getY() + this.window.getHeight()/2).mul(1/5.0f);
			if(loc.lengthSquared() > Blob.MAX_SPEED*Blob.MAX_SPEED) loc.normalize(Blob.MAX_SPEED);
			this.player.setVelocity(loc);
		}

		for(int i = this.ais.size()-1; i >= 0; i--) {
			AI ai = this.ais.get(i);
			Blob blob = ai.getBlob();
			blob.setMass(blob.getMass()-(foodLoss)*1.0f/fps);
			for(Blob b : world.getBlobs())
				if(blob.getMass() < 0.8 * b.getMass() &&
						blob.getPosition().distance(b.getPosition()) < (blob.getMass()+b.getMass())/2) {
					b.eat(blob.getMass()+World.FOOD_GAIN_PER_KILL);
					killBlob(blob);
					if(world.getBlobs().length <= WINNERS*this.population)
						this.winners.add(ai);
				}
			if(blob.getMass() < Blob.DEATH_MASS) {
				killBlob(blob);
			} else
				ai.update(world.getNearestBlob(blob), world.getNearestFood(blob.getPosition()));
		}

		if(world.getBlobs().length == 1) {
			killBlob(this.world.getBlobs()[0]);
		}
		if(world.getBlobs().length == 0) {
			spawnAis();
			world.nextGeneration();
		}

		this.world.update();
	}

	private void spawnAis() {
		for(int index = 0; index < winners.size(); index++) for(int i = 0; i < population/winners.size(); i++) {
			Blob blob = new Blob((float)(Math.random()*world.getWidth()), (float)(Math.random()*world.getHeight()),
					Blob.INIT_MASS, this.world);
			AI ai = winners.get(index).createChild(blob, mutationRate);
			ais.add(ai);
			blob.setAi(ai);
			this.world.spawn(blob);
		}
		while(this.world.getBlobs().length < population) {
			Blob blob = new Blob((float)(Math.random()*world.getWidth()), (float)(Math.random()*world.getHeight()),
					Blob.INIT_MASS, this.world);
			int i1 = (int) (Math.random()*4+1), i2 = (int) (Math.random()*6+1);
			AI ai = new AI(blob, i1, i2);
			blob.setAi(ai);
			this.ais.add(ai);
			this.world.spawn(blob);
		}
		winners.clear();
		if(this.player != null) {
			spawnPlayer();
		}
	}

	private void killBlob(Blob blob) {
		ais.remove(blob.getAI());
		world.kill(blob);
		if(world.getBlobs().length <= WINNERS*this.population)
			this.winners.add(blob.getAI());
	}

	public void spawnPlayer() {
		player = new Blob(world.getWidth()/2, world.getHeight()/2, Blob.INIT_MASS, world);
		this.world.spawn(player);
		this.renderer.setCamera(new PlayerCamera(player));
	}

	public static double getTimeSeconds() {
		return (double)System.nanoTime() / 1000000000;
	}

	public boolean isRunning() {
		return loop;
	}

	public void setRunning(boolean running) {
		this.loop = running;
	}

	public Canvas getRenderer() {
		return renderer;
	}

	public World getWorld() {
		return world;
	}
}
