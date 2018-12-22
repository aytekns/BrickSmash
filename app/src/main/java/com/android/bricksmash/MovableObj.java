package com.android.bricksmash;

public abstract class MovableObj 
{
	protected int m_nCoordX;
	protected int m_nCoordY;

	// Return X position coordinate
	public int getX() { return m_nCoordX; }
	// Return Y position coordinate
	public int getY() { return m_nCoordY; }
	
	// Return Left border of the object
	public abstract int getLeft();
	// Return Right border of the object
	public abstract int getRight();
	// Return Top border of the object
	public abstract int getTop();
	// Return Bottom border of the object
	public abstract int getBottom();
	// Abstract method to move the object
	public abstract void move();
	// Resets object to initial position and direction
	public abstract void restart();
}
