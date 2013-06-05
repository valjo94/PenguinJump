package org.example.projectunknown.model;

import org.example.projectunknown.gamelogic.MainGamePanel;
import org.example.projectunknown.model.components.Velocity;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class PlayerHero
{

	private Bitmap bitmap; // the actual bitmap

	private float x; // the X coordinate

	private float y; // the Y coordinate

	private boolean touched; // is the playerHero touched

	private Velocity velocity; // the speed with its directions

	private float destinationY; // the destination Y coordinate

	public PlayerHero(Bitmap bitmap, int x, int y)
	{
		this.bitmap = bitmap;
		this.x = x;
		this.y = y;
		this.velocity = new Velocity(0f, 100f, 0f, 3f); // xv, yv, sx , sy
		this.destinationY = this.y + (this.velocity.getYv() * this.velocity.getyDirection());

	}

	public Bitmap getBitmap()
	{
		return bitmap;
	}

	public void setBitmap(Bitmap bitmap)
	{
		this.bitmap = bitmap;
	}

	public float getX()
	{
		return x;
	}

	public void setX(float f)
	{
		this.x = f;
	}

	public float getY()
	{
		return y;
	}

	public void setY(float f)
	{
		this.y = f;
	}

	public float getDestinationY()
	{
		return destinationY;
	}

	public void setDestinationY(float destinationY)
	{
		this.destinationY = destinationY;
	}

	public Velocity getVelocity()
	{
		return this.velocity;
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

	/**
	 * Method which updates the playerHero internal state every tick
	 * 
	 * @param mOrientation
	 */
	public void update(float mOrientation)
	{

		if (!touched)
		{
			velocity.setSx(mOrientation);

			// Sensors + Physics (X axis movement)
			if (velocity.getSx() != 0)
			{
				if (velocity.getSx() < 0)
				{
					velocity.setxDirection(Velocity.DIRECTION_LEFT);
				} else
				{
					velocity.setxDirection(Velocity.DIRECTION_RIGHT);
				}

				x += (velocity.getSx()) * 20;
				velocity.setXv(x);
			}

			// Jumping (Y axis movement)
			if (this.getY() > this.destinationY && this.velocity.getyDirection() == Velocity.DIRECTION_UP)
			{
				y += (this.velocity.getSy() * this.velocity.getyDirection());
			} else if (this.velocity.getyDirection() == Velocity.DIRECTION_UP && this.getY() <= this.destinationY)
			{
				velocity.toggleYDirection();
			} else if (this.velocity.getyDirection() == Velocity.DIRECTION_DOWN)
			{
				y += (this.velocity.getSy() * this.velocity.getyDirection());
			}

		}
	}

	public void handleActionDown(int eventX, int eventY)
	{

	}

}
