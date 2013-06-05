package org.example.projectunknown.gamelogic;

import org.example.projectunknown.R;
import org.example.projectunknown.gameactivities.ProjectUnknown;
import org.example.projectunknown.media.Music;

import android.content.Context;
import android.graphics.Canvas;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.SurfaceHolder;

public class MainThread extends Thread
{
	private static final String TAG = MainThread.class.getSimpleName();

	MediaPlayer mp;

	Context context;

	// flag to hold game state
	public boolean running;

	// private boolean paused;

	private SurfaceHolder surfaceHolder;

	private MainGamePanel gamePanel;

	public MainThread(SurfaceHolder surfaceHolder, MainGamePanel gamePanel)
	{
		super();
		this.surfaceHolder = surfaceHolder;
		this.gamePanel = gamePanel;
	}

	public void setRunning(boolean running)
	{
		this.running = running;
	}

	private final static int MAX_FPS = 50;

	private final static int MAX_FRAME_SKIPS = 5;

	private final static int FRAME_PERIOD = 1000 / MAX_FPS;

	public static GameStates gameState;

	@Override
	public void run()
	{
		Canvas canvas;
		Log.d(TAG, "Starting game loop");

		long beginTime; // the time when the cycle begun
		long timeDiff; // the time it took for the cycle to execute
		int sleepTime; // ms to sleep (<0 if we're behind)
		int framesSkipped; // number of frames being skipped

		sleepTime = 0;

		while (running)
		{

			canvas = null;
			// try locking the canvas for exclusive pixel editing in the surface
			try
			{
				canvas = this.surfaceHolder.lockCanvas();
				synchronized (surfaceHolder)
				{

					beginTime = System.currentTimeMillis();
					framesSkipped = 0;

					if (gameState == GameStates.RUNNING)
					{
						this.gamePanel.update();
						this.gamePanel.render(canvas);

						// calculate how long did the cycle take
						timeDiff = System.currentTimeMillis() - beginTime;

						// calculate sleep time
						sleepTime = (int) (FRAME_PERIOD - timeDiff);

						if (sleepTime > 0)
						{
							// if sleepTime > 0 we're OK
							try
							{
								Thread.sleep(sleepTime);
							}
							catch (InterruptedException e)
							{
							}
						}

						while (sleepTime < 0 && framesSkipped < MAX_FRAME_SKIPS)
						{
							// update without rendering
							this.gamePanel.update();

							// add frame period to check if in next frame
							sleepTime += FRAME_PERIOD;
							framesSkipped++;
						}
					}
					else if (gameState == GameStates.PAUSED)
					{
						this.gamePanel.render(canvas);

						// calculate how long did the cycle take
						timeDiff = System.currentTimeMillis() - beginTime;

						// calculate sleep time
						sleepTime = (int) (FRAME_PERIOD - timeDiff);

						if (sleepTime > 0)
						{
							// if sleepTime > 0 we're OK
							try
							{
								Thread.sleep(sleepTime);
							}
							catch (InterruptedException e)
							{
							}
						}

						while (sleepTime < 0 && framesSkipped < MAX_FRAME_SKIPS)
						{
							// update without rendering
							// this.gamePanel.update();

							// add frame period to check if in next frame
							sleepTime += FRAME_PERIOD;
							framesSkipped++;
						}
					}
					else if (gameState == GameStates.GAME_OVER)
					{

						this.gamePanel.render(canvas);

						if (ProjectUnknown.prefMusic.getBoolean("SOUNDS", false) == true)
						{
							if (ProjectUnknown.prefMusic.getBoolean("MUSIC", false) == true)
							{
								Music.stop(gamePanel.getContext());
								MediaPlayer.create(gamePanel.getContext(), R.raw.gameover).start();
							}
							else
							{
								MediaPlayer.create(gamePanel.getContext(), R.raw.gameover).start();
							}
							Thread.sleep(2000);
						}

						setRunning(false);
					}
				}
			}
			catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			finally
			{
				// in case of an exception the surface is not left in
				// an inconsistent state
				if (canvas != null)
				{
					surfaceHolder.unlockCanvasAndPost(canvas);
				}
			}
		} // end while

	}
}
