package org.example.projectunknown.model;

import org.example.projectunknown.MainGamePanel;
import org.example.projectunknown.model.components.Velocity;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class PlayerHero
{

	@SuppressWarnings("unused")
	private static final String TAG = MainGamePanel.class.getSimpleName();

	private Bitmap bitmap; // the actual bitmap

	private float x; // the X coordinate

	private float y; // the Y coordinate

	private boolean touched; // is the playerHero touched

	private Velocity velocity; // the speed with its directions

	private float destinationY;

	public float currentY = 315;

	public PlayerHero(Bitmap bitmap, int x, int y)
	{
		this.bitmap = bitmap;
		this.x = x;
		this.y = y;
		this.velocity = new Velocity(0f, 140f, 0f, 3.5f); // xv, yv, sx , sy
		this.destinationY = this.y + (velocity.getYv() * velocity.getyDirection());

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

	public void setY(int y)
	{
		this.y = y;
	}

	public float getDestinationY()
	{
		return destinationY;
	}

	public void setDestinationY(float destinationY)
	{
		this.destinationY = destinationY;
	}

	public Velocity getSpeed()
	{
		return this.velocity;
	}

	public void setSpeed(Velocity speed)
	{
		this.velocity = speed;
	}

	public void draw(Canvas canvas)
	{

		canvas.drawBitmap(bitmap, x - (bitmap.getWidth() / 2), y - (bitmap.getHeight() / 2), null);

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

			// Sensors + Physics
			if (velocity.getSx() != 0)
			{
				if (velocity.getSx() < 0)
				{
					velocity.setxDirection(Velocity.DIRECTION_LEFT);
				}
				else
				{
					velocity.setxDirection(Velocity.DIRECTION_RIGHT);
				}

				x += (velocity.getSx()) * 10;
				velocity.setXv(x);
			}

			// Adding the jumping speed.
			if (this.getY() != this.destinationY)
			{
				y += (velocity.getSy() * velocity.getyDirection());

				// TODO Adding gravity
				if (velocity.getSy() > 0)
				{
					if (velocity.getyDirection() == Velocity.DIRECTION_UP)
					{
						velocity.setSy(velocity.getSy() - 0.01f);
						// System.out.println("DIRECTION UP");
					}
					else
					{
						velocity.setSy(velocity.getSy() + 0.01f);
						// System.out.println("Should be 1: " + velocity.getyDirection());
					}
				}
			}
			else
			{
				velocity.toggleYDirection();
				this.destinationY += (velocity.getYv() * velocity.getyDirection());
			}

			// Jump = ?px Up collision
			if (velocity.getyDirection() == Velocity.DIRECTION_UP && this.y <= (currentY - velocity.getYv()))
			{
				velocity.toggleYDirection();
			}

			// Log.d(TAG, "PlayerHero Coords: x=" + this.getX() + ",y=" + this.getY() + ", Yv= " + velocity.getYv());

		}
	}

	public void handleActionDown(int eventX, int eventY)
	{

	}

}
