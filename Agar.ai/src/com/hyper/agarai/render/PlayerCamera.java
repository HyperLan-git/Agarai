package com.hyper.agarai.render;

import org.joml.Vector2f;

import com.hyper.agarai.game.Blob;

public class PlayerCamera implements Camera {
	private Blob player;
	
	public PlayerCamera(Blob playerEntity) {
		this.player = playerEntity;
	}

	@Override
	public float getWorldSize() {
		return Blob.INIT_MASS/player.getMass();
	}

	@Override
	public Vector2f getCameraPosition() {
		return new Vector2f(player.getPosition());
	}

	@Override
	public void update() {	}

}
