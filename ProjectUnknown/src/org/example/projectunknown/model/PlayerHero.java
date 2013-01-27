package org.example.projectunknown.model;


import org.example.projectunknown.MainGamePanel;
import org.example.projectunknown.model.components.Velocity;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class PlayerHero {

	private static final String TAG = MainGamePanel.class.getSimpleName();

	private Bitmap bitmap; // the actual bitmap
	private float x;   // the X coordinate
	private float y;   // the Y coordinate
	private boolean touched; // is the playerHero toched
	private Velocity speed; // the speed with its directions
	private float destinationX;
	private float destinationY;
	
	public PlayerHero(Bitmap bitmap, int x, int y) {
		this.bitmap = bitmap;
		this.x = x;
		this.y = y;
		this.speed = new Velocity(0f , 200f, 0f, 10f);
		this.destinationX = this.x + speed.getXv();
		this.destinationY = this.y + (speed.getYv() * speed.getyDirection()) ;
		
	}
	
	public Bitmap getBitmap() {
		return bitmap;
	}
	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}
	public float getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public float getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	
	public boolean isTouched() {
		return touched;
	}
	
	public void setTouched(boolean touched) {
		this.touched = touched;
	}
	
	public Velocity getSpeed() {
		return this.speed;
	}

	public void setSpeed(Velocity speed) {
		this.speed = speed;
	}

	
	
	public void draw(Canvas canvas) {
		
		canvas.drawBitmap(bitmap, x - (bitmap.getWidth() / 2), y - (bitmap.getHeight() / 2), null);
		
	}
	
	/**
	 * Method which updates the playerHero internal state every tick
	 */
	public void update() {
		//this.getX() <= (this.getX() + this.getSpeed().getXv()) && 
		if (!touched) { 

			if(this.getY() != this.destinationY) {
			
				x += (speed.getSx() * speed.getxDirection());
				y += (speed.getSy() * speed.getyDirection());
			
			} else {
				speed.toggleYDirection();
				this.destinationY += (speed.getYv() * speed.getyDirection());
			} 
			
//			Log.d(TAG, "Coords: x=" + this.getX() + ",y=" + this.getY() + ", Yv= " + speed.getYv());
			
		}
	}

	
	public void handleActionDown(int eventX, int eventY) {
		if (eventX >= (x - bitmap.getWidth() / 2) && (eventX <= (x + bitmap.getWidth()/2))) {
			if (eventY >= (y - bitmap.getHeight() / 2) && (y <= (y + bitmap.getHeight() / 2))) {
				// playerHero touched
			    setTouched(true);
			} else {
				setTouched(false);
		   }
		} else {
			setTouched(false);
		}
	}
	
}


