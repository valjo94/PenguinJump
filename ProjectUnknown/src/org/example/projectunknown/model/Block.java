package org.example.projectunknown.model;

import org.example.projectunknown.MainGamePanel;
import org.example.projectunknown.model.components.Velocity;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Block
{

	public static final float BLOCK_WIDTH = 2;

	public static final float BLOCK_HEIGHT = 0.5f;

	public static final boolean BLOCK_TYPE_STATIC = false;

	public static final int BLOCK_TYPE_MOVING = 1;

	public static final int BLOCK_STATE_NORMAL = 0;

	// public static final int BLOCK_STATE_PULVERIZING = 1;
	// public static final float BLOCK_PULVERIZE_TIME = 0.2f * 4;
	
	public static final float BLOCK_VELOCITY = 2;

	private static final int BLOCK_TYPE_NORMAL = 0;

	private Bitmap bitmap; // the actual bitmap

	private float x; // the X coordinate

	private float y; // the Y coordinate

	int type;

	int state;

	float stateTime;

	private Velocity velocity;

	private int blockType = BLOCK_TYPE_NORMAL;

	MainGamePanel panel;

	public Block(Bitmap bitmap, float startPointX, float startPointY)
	{
		this.bitmap = bitmap;
		this.x = startPointX;
		this.y = startPointY;
		this.velocity = new Velocity(2f, 0f, 0f, 0f);
	}

	public Bitmap getBitmap()
	{
		return bitmap;
	}

	public void setBitmap(Bitmap bitmap)
	{
		this.bitmap = bitmap;
	}

	public void setType(int blockType)
	{
		this.blockType = blockType;
	}

	public int getType()
	{
		return blockType;
	}

	public float getX()
	{
		return x;
	}

	public void setX(int x)
	{
		this.x = x;
	}

	public float getY()
	{
		return y;
	}

	public void setY(float currentY)
	{
		this.y = currentY;
	}

	public Velocity getVelocity()
	{
		return velocity;
	}

	public void setVelocity(Velocity velocity)
	{
		this.velocity = velocity;
	}

	public void draw(Canvas canvas)
	{

		canvas.drawBitmap(bitmap, x - (bitmap.getWidth() / 2), y - (bitmap.getHeight() / 2), null);

	}

	// Blocks movement
	public void update()
	{

		if (blockType == BLOCK_TYPE_MOVING)
		{
			x += (velocity.getXv() * velocity.getxDirection());

		}
	}

}
