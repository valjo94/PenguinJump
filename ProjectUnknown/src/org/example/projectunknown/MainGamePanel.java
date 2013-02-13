package org.example.projectunknown;

import org.example.projectunknown.model.Block;
import org.example.projectunknown.model.PlayerHero;
import org.example.projectunknown.model.components.Velocity;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class MainGamePanel extends SurfaceView implements
	SurfaceHolder.Callback, SensorEventListener {

	private static final String TAG = MainGamePanel.class.getSimpleName();

	private MainThread thread;
	private PlayerHero playerHero;
	private Velocity velocity;
	
	//Sensors data
	private SensorManager mSensorManager;
	private float[] mAccelerometerReading;
	private float[] mMagneticFieldReading;
	private float[] mRotationMatrix = new float[16];
	private float[] mRemapedRotationMatrix = new float[16];
	public float[] mOrientation = new float[3];
	
	
	//TODO floors
	private Block block;
	Block[] blocksArray = new Block[6];
	
	public MainGamePanel(Context context) {
  
		super(context);
  
		//Handling the events happening on the actual surface.
		getHolder().addCallback(this);
  		playerHero = new PlayerHero(BitmapFactory.decodeResource(getResources(), R.drawable.smiley), 120 , 440 );
  		
  		int startPointY = 300;
  		int startPointX = 0;
  	
  		for(int i = 0; i< blocksArray.length; i++) {
  			startPointY -=40;
  			startPointX +=50;
  			block = new Block(BitmapFactory.decodeResource(getResources(), R.drawable.floor), startPointX, startPointY);
  			blocksArray[i] = block;
  		
  		}
		// create the game loop thread
		thread = new MainThread(getHolder(), this);
  
		setFocusable(true);
		

		mSensorManager = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
		mSensorManager.registerListener(this,
		mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
		SensorManager.SENSOR_DELAY_GAME);
		mSensorManager.registerListener(this,
		mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
		SensorManager.SENSOR_DELAY_GAME);
		
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
	
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		thread.setRunning(true);
		thread.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
	Log.d(TAG, "The surface is being destroyed..");
		// tell the thread to shut down and wait for it to finish
		boolean retry = true;
	
		while (retry) {
			try {
				thread.join();
				retry = false;
			} catch (InterruptedException e) {
				// try again shutting down the thread
			}
		}
		Log.d(TAG, "Thread was shut down cleanly");
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
//			 delegating event handling to the playerHero
			 //TODO playerHero.handleActionDown((int)event.getX(), (int)event.getY());
			
			// if in the lower part of the screen, then exit
			if (event.getY() > getHeight() - 50) {
				thread.setRunning(false);
				((Activity)getContext()).finish();
			} else {
				Log.d(TAG, "Coords: x=" + event.getX() + ",y=" + event.getY());
			}
		} 
//TODO		if (event.getAction() == MotionEvent.ACTION_MOVE) {
//			
//			if(playerHero.isTouched()) {
//				// picked up and being dragged
//				playerHero.setX((int)event.getX());
//				playerHero.setY((int)event.getY());
//			}
//		} if (event.getAction() == MotionEvent.ACTION_UP) {
//			if(playerHero.isTouched()) {
//				playerHero.setTouched(false);
//			}
//		}
		
		return true;
	}
	
	@Override
	public void onSensorChanged(SensorEvent event) {
		
		switch (event.sensor.getType()) {
		case Sensor.TYPE_ACCELEROMETER:
		{
			mAccelerometerReading = event.values.clone();
			break;
		}
		case Sensor.TYPE_MAGNETIC_FIELD:
		{
			mMagneticFieldReading = event.values.clone();
			break;
		}
		}
		if(mAccelerometerReading != null && mMagneticFieldReading != null &&
				SensorManager.getRotationMatrix(mRotationMatrix, null, mAccelerometerReading, mMagneticFieldReading))
		{
			SensorManager.remapCoordinateSystem(mRotationMatrix,
			SensorManager.AXIS_Y, SensorManager.AXIS_MINUS_X, mRemapedRotationMatrix);
			SensorManager.getOrientation(mRemapedRotationMatrix, mOrientation);
		}	
		
	}	
	
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}
	
	// Check for Collisions with walls
	public void update() {		
		
//		if(mOrientation[2] < 0){
//			velocity.setxDirection(Velocity.DIRECTION_LEFT);
//			Log.d(TAG, "Direction changed to LEFT");
//		}
//		if(mOrientation[2] >= 0) {
//			velocity.setxDirection(Velocity.DIRECTION_RIGHT);
//			Log.d(TAG, "Direction changed to RIGHT");
//		}
		
		
		
		if (playerHero.getSpeed().getxDirection() == Velocity.DIRECTION_RIGHT 
				&& playerHero.getX() + playerHero.getBitmap().getWidth() / 2 >= getWidth()) {
			playerHero.setX(getLeft());
			playerHero.setX((playerHero.getX() + (mOrientation[1]))*Velocity.DIRECTION_RIGHT);
			
		}
		
		
		if (playerHero.getSpeed().getxDirection() == Velocity.DIRECTION_LEFT
				&& playerHero.getX() - playerHero.getBitmap().getWidth() / 2 <= 0) {
			playerHero.setX(getRight());
			
			playerHero.setX((playerHero.getX() - (mOrientation[1]))*Velocity.DIRECTION_LEFT);
		}
		

		
		if (playerHero.getSpeed().getyDirection() == Velocity.DIRECTION_DOWN
				&& playerHero.getY() + playerHero.getBitmap().getHeight() / 2 >= getHeight()) {
			playerHero.getSpeed().toggleYDirection();			
			playerHero.currentY = playerHero.getY();
		}

		if (playerHero.getSpeed().getyDirection() == Velocity.DIRECTION_UP
				&& playerHero.getY() - playerHero.getBitmap().getHeight() / 2 <= 0) {
				playerHero.getSpeed().toggleYDirection();
		}

		
		if(playerHero.getSpeed().getyDirection() == Velocity.DIRECTION_DOWN) {
			for(int i = 0;i < blocksArray.length; i++) {
				if(playerHero.getY() + playerHero.getBitmap().getHeight() >= blocksArray[i].getY() 
					&& playerHero.getY() + playerHero.getBitmap().getHeight() <= blocksArray[i].getY() + blocksArray[i].getBitmap().getHeight() /2
					&& playerHero.getX() >= blocksArray[i].getX() - blocksArray[i].getBitmap().getWidth() /2
					&& playerHero.getX() <= blocksArray[i].getX() + blocksArray[i].getBitmap().getWidth() /2 ) {
			playerHero.getSpeed().toggleYDirection();
			playerHero.currentY = playerHero.getY();
				}
			}
		}
			
		// Update the playerHero
		playerHero.update(mOrientation[1]);
		for(int i = 0;i < blocksArray.length; i++) {
			blocksArray[i].update();
			//blocksArray[i].update(thread.timeDiff);
		}
			
	}
	
	protected void render(Canvas canvas) {
		canvas.drawColor(Color.BLACK);
		playerHero.draw(canvas);	
		for(int i = 0; i < blocksArray.length; i++) {
			blocksArray[i].draw(canvas);
			}
	}





}
