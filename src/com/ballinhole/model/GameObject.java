package com.ballinhole.model;

import android.graphics.Rect;

public class GameObject {

	private int x;
	private int y;
	private int size;
	
	public GameObject(int x, int y, int size) {
		this.x = x;
		this.y = y;
		this.size = size;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}
	
	public Rect getBounds(int totalWidth, int totalHeight) {
		return new Rect(x, y, totalWidth - (x + size), totalHeight - (y + size));
	}
	
	public boolean collides(GameObject other, int totalWidth, int totalHeight) {
		return getBounds(totalWidth, totalHeight).contains(other.getBounds(totalWidth, totalHeight));
	}
	
}
