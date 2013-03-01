package org.example.projectunknown.model;

import org.example.projectunknown.model.components.Velocity;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Enemy
{
		private Bitmap bitmap; // the actual bitmap

		private float x; // the X coordinate

		private float y; // the Y coordinate

		float stateTime;

		private Velocity velocity;

		public Enemy(Bitmap bitmap, float startPointX, float startPointY)
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

		// Enemy movement
		public void update()
		{

				x += (velocity.getXv() * velocity.getxDirection());

		}


}
