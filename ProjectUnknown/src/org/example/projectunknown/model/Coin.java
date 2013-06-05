package org.example.projectunknown.model;

import org.example.projectunknown.gamelogic.MainGamePanel;
import org.example.projectunknown.model.components.Velocity;
import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Coin
{

	public static final int COIN_TYPE_NORMAL = 1;

	public static final int COIN_TYPE_SPECIAL = 0;

	private Bitmap bitmap; // the actual bitmap
	private Velocity velocity;

	private float x; // the X coordinate
	private float y; // the Y coordinate
	private int coinType = COIN_TYPE_NORMAL;

	public Coin(Bitmap bitmap, float startPointX, float startPointY)
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
		this.coinType = blockType;
	}

	public int getType()
	{
		return coinType;
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
		Bitmap resized = Bitmap.createScaledBitmap(	bitmap,
													(int) (bitmap.getWidth() * MainGamePanel.density),
													(int) (bitmap.getHeight() * MainGamePanel.density),
													true);
		canvas.drawBitmap(resized, x - (resized.getWidth() / 2), y - (resized.getHeight() / 2), null);
	}

	// Blocks movement
	public void update()
	{
	}

}
