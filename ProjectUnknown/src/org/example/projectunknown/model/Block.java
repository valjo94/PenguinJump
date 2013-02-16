package org.example.projectunknown.model;

import org.example.projectunknown.MainGamePanel;
import org.example.projectunknown.model.components.Velocity;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;

public class Block {

	private static final String TAG = MainGamePanel.class.getSimpleName();
	
	public static final float BLOCK_WIDTH = 2;
	public static final float BLOCK_HEIGHT = 0.5f;
	public static final boolean BLOCK_TYPE_STATIC = false;
	public static final int BLOCK_TYPE_MOVING = 1;
	public static final int BLOCK_STATE_NORMAL = 0;
//	public static final int BLOCK_STATE_PULVERIZING = 1;
//	public static final float BLOCK_PULVERIZE_TIME = 0.2f * 4;
	public static final float BLOCK_VELOCITY = 2;

	private Bitmap bitmap; // the actual bitmap
	private float x;   // the X coordinate
	private float y;   // the Y coordinate
	private Velocity velocity; // the speed with its directions
	
	int type;
	int state;
	float stateTime;
	
	public Block(Bitmap bitmap, int x, int y) {
		this.bitmap = bitmap;
		this.x = x;
		this.y = y;
		
		this.state = BLOCK_STATE_NORMAL;
		this.stateTime = 0;
		
		if(type == BLOCK_TYPE_MOVING) {
//			velocity.getXv() = BLOCK_VELOCITY;
		}
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
	
	
	public void draw(Canvas canvas) {
		
		canvas.drawBitmap(bitmap, x - (bitmap.getWidth() / 2), y - (bitmap.getHeight() / 2), null);
		
	}
	
	//TODO Blocks Collisions
	public void update() {
		
		if (true) { 
			
			//Log.d(TAG, "Coords: x=" + this.getX() + ",y=" + this.getY());
			
		}
	}
	
}
