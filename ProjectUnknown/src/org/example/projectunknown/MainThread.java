package org.example.projectunknown;

import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;

public class MainThread extends Thread {

	private static final String TAG = MainThread.class.getSimpleName();
	
	// flag to hold game state
	private boolean running;
 
	private SurfaceHolder surfaceHolder;
	private MainGamePanel gamePanel;

	public MainThread(SurfaceHolder surfaceHolder, MainGamePanel gamePanel) {
	 
		super();
		this.surfaceHolder = surfaceHolder;
		this.gamePanel = gamePanel;
	 
	}
 
	 public void setRunning(boolean running) {
	 
		 this.running = running;
	 
	 }

//	 @Override
//	 public void run() {
//		 
//		 Canvas canvas;
//		 Log.d(TAG, "Starting game loop");
//		 
//		while (running) {
//			canvas = null;
//			// try locking the canvas for exclusive pixel editing on the surface
//			try {				
//				canvas = this.surfaceHolder.lockCanvas();
//				synchronized (surfaceHolder) {
//					// update game state
//					this.gamePanel.update();
//					// render state on the screen
//					// draws the canvas on the panel
//					this.gamePanel.render(canvas);
//				}
//			} finally {				
//				// in case of an exception the surface is not left
//				// in an inconsistent state
//				if (canvas != null) {
//					surfaceHolder.unlockCanvasAndPost(canvas);
//				}
//			} // end finally
//		}
//	}
	 

	 private final static int 	MAX_FPS = 50;
	 private final static int	MAX_FRAME_SKIPS = 5;
	 private final static int	FRAME_PERIOD = 1000 / MAX_FPS;	

	 @Override
	 public void run() {
	 	Canvas canvas;
	 	Log.d(TAG, "Starting game loop");

	 	long beginTime;		// the time when the cycle begun
	 	long timeDiff;		// the time it took for the cycle to execute
	 	int sleepTime;		// ms to sleep (<0 if we're behind)
	 	int framesSkipped;	// number of frames being skipped 

	 	sleepTime = 0;

	 	while (running) {
	 		canvas = null;
	 		// try locking the canvas for exclusive pixel editing in the surface
	 		try {
	 			canvas = this.surfaceHolder.lockCanvas();
	 			synchronized (surfaceHolder) {
	 				beginTime = System.currentTimeMillis();
	 				framesSkipped = 0;
	 				// update game state
	 				this.gamePanel.update();
	 				// render state to the screen
	 				// draws the canvas on the panel
	 				this.gamePanel.render(canvas);
	 				// calculate how long did the cycle take
	 				timeDiff = System.currentTimeMillis() - beginTime;
	 				// calculate sleep time
	 				sleepTime = (int)(FRAME_PERIOD - timeDiff);

	 				if (sleepTime > 0) {
	 					// if sleepTime > 0 we're OK
	 					try {
	 						Thread.sleep(sleepTime);
	 					} catch (InterruptedException e) {}
	 				}

	 				while (sleepTime < 0 && framesSkipped < MAX_FRAME_SKIPS) {
	 					// update without rendering
	 					this.gamePanel.update();
	 					// add frame period to check if in next frame
	 					sleepTime += FRAME_PERIOD;
	 					framesSkipped++;
	 				}
	 			}
	 		} finally {
	 			// in case of an exception the surface is not left in
	 			// an inconsistent state
	 			if (canvas != null) {
	 				surfaceHolder.unlockCanvasAndPost(canvas);
	 			}
	 		}	// end finally
	 	}
	 }


 
 
}
