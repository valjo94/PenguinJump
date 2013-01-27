package org.example.projectunknown;

import org.example.projectunknown.model.PlayerHero;
import org.example.projectunknown.model.components.Velocity;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class MainGamePanel extends SurfaceView implements
	SurfaceHolder.Callback {

	private static final String TAG = MainGamePanel.class.getSimpleName();

//	private static final float MAX_JUMP_HEIGHT = ;
	
	private MainThread thread;
	private PlayerHero playerHero;
	
	public MainGamePanel(Context context) {
  
		super(context);
  
		//Handling the events happening on the actual surface.
		getHolder().addCallback(this);
  
		playerHero = new PlayerHero(BitmapFactory.decodeResource(getResources(), R.drawable.smiley), 120 , 450 );
		
		// create the game loop thread
		thread = new MainThread(getHolder(), this);
  
		setFocusable(true);
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
			// delegating event handling to the playerHero
			playerHero.handleActionDown((int)event.getX(), (int)event.getY());
			
			// if in the lower part of the screen, then exit
			if (event.getY() > getHeight() - 50) {
				thread.setRunning(false);
				((Activity)getContext()).finish();
			} else {
				Log.d(TAG, "Coords: x=" + event.getX() + ",y=" + event.getY());
			}
		} if (event.getAction() == MotionEvent.ACTION_MOVE) {
			
			if(playerHero.isTouched()) {
				// picked up and being dragged
				playerHero.setX((int)event.getX());
				playerHero.setY((int)event.getY());
			}
		} if (event.getAction() == MotionEvent.ACTION_UP) {
			if(playerHero.isTouched()) {
				playerHero.setTouched(false);
			}
		}
		
		return true;
	}
		// Check for Collisions
	public void update() {		
		
		if (playerHero.getSpeed().getxDirection() == Velocity.DIRECTION_RIGHT
				&& playerHero.getX() + playerHero.getBitmap().getWidth() / 2 >= getWidth()) {
		//	playerHero.getSpeed().toggleXDirection();
			playerHero.setX(getLeft());
		}
		
		
		if (playerHero.getSpeed().getxDirection() == Velocity.DIRECTION_LEFT
				&& playerHero.getX() - playerHero.getBitmap().getWidth() / 2 <= 0) {
		//	playerHero.getSpeed().toggleXDirection();
			playerHero.setX(getRight());
		}

		
		if (playerHero.getSpeed().getyDirection() == Velocity.DIRECTION_DOWN
				&& playerHero.getY() + playerHero.getBitmap().getHeight() / 2 >= getHeight()) {
			playerHero.getSpeed().toggleYDirection();			

		}

		if (playerHero.getSpeed().getyDirection() == Velocity.DIRECTION_UP
				&& playerHero.getY() - playerHero.getBitmap().getHeight() / 2 <= 0) {
				playerHero.getSpeed().toggleYDirection();
		}

		// Update the lone playerHero
		playerHero.update();
		
	}

	
	protected void render(Canvas canvas) {
		canvas.drawColor(Color.BLACK);
		playerHero.draw(canvas);
	
	}




}
