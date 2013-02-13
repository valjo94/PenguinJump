package org.example.projectunknown.model;


import org.example.projectunknown.MainGamePanel;
import org.example.projectunknown.model.components.Velocity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;

public class PlayerHero {
	
	@SuppressWarnings("unused")
	private static final String TAG = MainGamePanel.class.getSimpleName();

	private Bitmap bitmap; // the actual bitmap
	private float x;   // the X coordinate
	private float y;   // the Y coordinate
	private boolean touched; // is the playerHero touched
	private Velocity velocity; // the speed with its directions
	private float destinationX;
	private float destinationY;

	//private Context context;
	private MainGamePanel panel;

	public float currentY = 450;
	
	public PlayerHero(Bitmap bitmap, int x, int y) {
		this.bitmap = bitmap;
		this.x = x;
		this.y = y;
		this.velocity = new Velocity(0f , 100f, 0f, 3f);
		this.destinationX = this.x + velocity.getXv() * velocity.getxDirection();
		this.destinationY = this.y + (velocity.getYv() * velocity.getyDirection()) ;
		
	}
	
	//Constructor

	
	public Bitmap getBitmap() {
		return bitmap;
	}
	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}
	public float getX() {
		return x;
	}
	public void setX(float f) {
		this.x = f;
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
		return this.velocity;
	}

	public void setSpeed(Velocity speed) {
		this.velocity = speed;
	}

		
	public void draw(Canvas canvas) {
		
		canvas.drawBitmap(bitmap, x - (bitmap.getWidth() / 2), y - (bitmap.getHeight() / 2), null);
		
	}
	
	/**
	 * Method which updates the playerHero internal state every tick
	 * @param mOrientation 
	 */
	public void update(float mOrientation) {
		
		if (!touched) { 
						
			if(mOrientation != 0)
			{
				if(mOrientation < 0) {

					x += (this.x * (mOrientation/5));
				}
				x += (this.x * (mOrientation/5));
				velocity.setXv(x);
			}
			
			//TODO
//			if(mOrientation != 0 && this.getX() != this.destinationX)
//			{
//				x += (velocity.getSx() * velocity.getxDirection());
//			} else {
//				velocity.toggleXDirection();
//				this.destinationX += (velocity.getXv() * velocity.getxDirection());
//			}
			
			//TODO Jump = 200px Up
			if(velocity.getyDirection() == Velocity.DIRECTION_UP 
					&& this.y <= (currentY -200)) {
				velocity.toggleYDirection();
			}
			
			if(this.getY() != this.destinationY) {
				y += (velocity.getSy() * velocity.getyDirection());
			} else {
				velocity.toggleYDirection();
				this.destinationY += (velocity.getYv() * velocity.getyDirection());
			} 
			
			Log.d(TAG, "Coords: x=" + this.getX() + ",y=" + this.getY() + ", Yv= " + velocity.getYv());
			
		}
	}
	
	public void handleActionDown(int eventX, int eventY) {
//		if (eventX >= (x - bitmap.getWidth() / 2) && (eventX <= (x + bitmap.getWidth()/2))) {
//			if (eventY >= (y - bitmap.getHeight() / 2) && (y <= (y + bitmap.getHeight() / 2))) {
//				// playerHero touched
//			    setTouched(true);
//			} else {
//				setTouched(false);
//		   }
//		} else {
//			setTouched(false);
//		}
	}

//	@Override
//	public void onAccuracyChanged(Sensor arg0, int arg1) {
//		// TODO Auto-generated method stub
//		
//	}


}


