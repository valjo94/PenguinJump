package org.example.projectunknown.model.components;

public class Velocity
{

	// TODO PUSH INTO AN ENUM
	public static final int DIRECTION_RIGHT = 1;

	public static final int DIRECTION_LEFT = -1;

	public static final int DIRECTION_UP = -1;

	public static final int DIRECTION_DOWN = 1;

	private float xv; // velocity value on the X axis

	private float yv; // velocity value on the Y axis

	private float sx;

	private float sy;

	private int xDirection = DIRECTION_RIGHT;

	private int yDirection = DIRECTION_UP;

	public Velocity(float xv, float yv, float sx, float sy)
	{
		this.xv = xv;
		this.yv = yv;
		this.sx = sx;
		this.sy = sy;
	}

	public float getXv()
	{
		return xv;
	}

	public void setXv(float xv)
	{
		this.xv = xv;
	}

	public float getYv()
	{
		return yv;
	}

	public void setYv(float yv)
	{
		this.yv = yv;
	}

	public int getxDirection()
	{
		return xDirection;
	}

	public void setxDirection(int xDirection)
	{
		this.xDirection = xDirection;
	}

	public int getyDirection()
	{
		return yDirection;
	}

	public float getSx()
	{
		return sx;
	}

	public void setSx(float sx)
	{
		this.sx = sx;
	}

	public float getSy()
	{
		return sy;
	}

	public void setSy(float sy)
	{
		this.sy = sy;
	}

	public void setyDirection(int yDirection)
	{
		this.yDirection = yDirection;
	}

	// changes the direction on the X axis
	public void toggleXDirection()
	{
		if (xDirection == DIRECTION_RIGHT)
		{
			xDirection = DIRECTION_LEFT;
		}
		else if (xDirection == DIRECTION_LEFT)
		{
			xDirection = DIRECTION_RIGHT;
		}
	}

	// changes the direction on the Y axis
	public void toggleYDirection()
	{
		// yDirection = yDirection * -1;
		if (yDirection == DIRECTION_DOWN)
		{
			yDirection = DIRECTION_UP;
		}
		else if (yDirection == DIRECTION_UP)
		{
			yDirection = DIRECTION_DOWN;
		}
	}

}
