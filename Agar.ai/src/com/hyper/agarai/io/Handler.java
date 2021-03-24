package com.hyper.agarai.io;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import com.hyper.agarai.Agarai;

public class Handler implements KeyListener {
	private Agarai instance;

	public Handler(Agarai instance) {
		this.instance = instance;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		switch(e.getKeyCode()) {
		case KeyEvent.VK_SPACE:
			this.instance.setRunning(!instance.isRunning());
			break;
		default:
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}
}