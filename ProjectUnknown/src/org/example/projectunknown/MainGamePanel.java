package org.example.projectunknown;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.example.projectunknown.model.Block;
import org.example.projectunknown.model.PlayerHero;
import org.example.projectunknown.model.components.Velocity;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class MainGamePanel extends SurfaceView implements SurfaceHolder.Callback, SensorEventListener
{

	private static final String TAG = MainGamePanel.class.getSimpleName();

	private static final float COLLISION_UP = 70;

	private MainThread thread;

	private PlayerHero hero;

	private Paint textPaint;

	// Blocks variables
	private Block block;

	private List<Block> blockList = new ArrayList<Block>();

	private List<Float> array = new ArrayList<Float>();

	private final float BLOCK_Y = 310;

	// Sensors data arrays
	private SensorManager mSensorManager;

	private float[] mAccelerometerReading;

	private float[] mMagneticFieldReading;

	private float[] mRotationMatrix = new float[16];

	private float[] mRemapedRotationMatrix = new float[16];

	public float[] mOrientation = new float[3];

	private Random rand = new Random();

	float blockX;

	float newBlockX;

	float currentY;

	private int blockType;

	public static int score = 0;

	private long beginTime;

	private long currentTime;

	private long timeCount;

	public MainGamePanel(Context context)
	{
		super(context);

		// Handling the events happening on the actual surface.
		getHolder().addCallback(this);
		beginTime = System.currentTimeMillis();

		// Creating blocks.
		currentY = BLOCK_Y;
		for (int i = 0; i < 7; i++)
		{
			currentY -= 50;
			blockX = rand.nextInt(200) + 20;
			block = new Block(BitmapFactory.decodeResource(getResources(), R.drawable.floor), blockX, currentY);
			blockList.add(block);
			array.add(currentY);
		}

		// Creating the player.
		hero = new PlayerHero(BitmapFactory.decodeResource(getResources(), R.drawable.smiley), 120, 310);

		// Creating Texts.
		textPaint = new Paint();

		// Create the game loop thread.
		setThread(new MainThread(getHolder(), this));

		setFocusable(true);

		getSensorData(context);

	}

	// Method for landscape view - Don't touch
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
	{

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder)
	{
		thread.setRunning(true);
		getThread().start();
		MainThread.gameState = GameStates.RUNNING;
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder)
	{
		Log.d(TAG, "The surface is being destroyed..");

		// tell the thread to shut down and wait for it to finish
		boolean retry = true;

		while (retry)
		{
			try
			{
				getThread().join();
				retry = false;
				Log.d(TAG, "Thread was shut down cleanly.");
			}
			catch (InterruptedException e)
			{
				// try again shutting down the thread
				Log.d(TAG, "Thread could not shut down cleanly.");
			}
		}

	}

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{

		if (event.getAction() == MotionEvent.ACTION_DOWN)
		{

			Log.d(TAG, "Coords: x=" + event.getX() + ",y=" + event.getY());
		}

		return true;
	}

	@Override
	public void onSensorChanged(SensorEvent event)
	{

		switch (event.sensor.getType())
		{
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
		if (mAccelerometerReading != null && mMagneticFieldReading != null
				&& SensorManager.getRotationMatrix(mRotationMatrix, null, mAccelerometerReading, mMagneticFieldReading))
		{
			SensorManager.remapCoordinateSystem(mRotationMatrix,
												SensorManager.AXIS_Y,
												SensorManager.AXIS_MINUS_X,
												mRemapedRotationMatrix);
			SensorManager.getOrientation(mRemapedRotationMatrix, mOrientation);
		}

	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy)
	{

	}

	public void update()
	{
		if (hero.getY() <= 320)
		{
			currentTime = System.currentTimeMillis() - beginTime;

			// Collision with RIGHT side of screen.
			if (hero.getVelocity().getxDirection() == Velocity.DIRECTION_RIGHT
					&& hero.getX() + hero.getBitmap().getWidth() / 2 >= getWidth())
			{
				hero.setX(getLeft());
				hero.setX((hero.getX() + (mOrientation[1])) * Velocity.DIRECTION_RIGHT);
			}
			// Collision with LEFT side of screen.
			if (hero.getVelocity().getxDirection() == Velocity.DIRECTION_LEFT
					&& hero.getX() - hero.getBitmap().getWidth() / 2 <= getLeft())
			{
				hero.setX(getRight());
				hero.setX(-(hero.getX() - (mOrientation[1])) * Velocity.DIRECTION_LEFT);
			}
			// Collision with BOTTOM of screen.
			if (hero.getVelocity().getyDirection() == Velocity.DIRECTION_DOWN && hero.getY() + playerHight() / 2 >= 320)
			{
				if (currentTime <= 5000)
				{
					hero.getVelocity().toggleYDirection();
				}
				else
				{
					MainThread.gameState = GameStates.GAME_OVER;
					// setGameOverState(true);
				}
			}
			// Collision with highest point of the screen that the hero can get.
			if (hero.getVelocity().getyDirection() == Velocity.DIRECTION_UP
					&& hero.getY() - playerHight() / 2 <= COLLISION_UP)
			{
				hero.getVelocity().toggleYDirection();
			}
			// Collision of the playerHero with BLOCKS.
			if (hero.getVelocity().getyDirection() == Velocity.DIRECTION_DOWN)
			{
				for (int i = 0; i < blockList.size(); i++)
				{
					if (hero.getY() + playerHight() >= blockList.get(i).getY()
							&& hero.getY() + playerHight() <= blockList.get(i).getY()
									+ blockList.get(i).getBitmap().getHeight() / 2
							&& hero.getX() >= blockList.get(i).getX() - blockList.get(i).getBitmap().getWidth() / 2
							&& hero.getX() <= blockList.get(i).getX() + blockList.get(i).getBitmap().getWidth() / 2)
					{

						hero.getVelocity().toggleYDirection();

						hero.setDestinationY(hero.getY()
								+ (hero.getVelocity().getYv() * hero.getVelocity().getyDirection()));
						score += 10;
					}
				}
			}

			// Scrolling the screen.
			if (hero.getY() <= blockList.get(3).getY())
			{
				if (hero.getDestinationY() < COLLISION_UP)
				{
					hero.setDestinationY(hero.getDestinationY() + 2);
				}
				scrollUp();
			}

			// Collision when block is under the bottom of screen.
			if (blockList.get(0).getY() >= 320)
			{
				blockList.remove(0);
				array.remove(0);
			}
			// Collision when block is deleted.
			if (blockList.size() <= 6)
			{
				blockType = rand.nextInt(10);

				blockX = rand.nextInt(200) + 20;
				currentY = blockList.get(blockList.size() - 1).getY() - 50;

				block = new Block(BitmapFactory.decodeResource(getResources(), R.drawable.floor), blockX, currentY);

				if (blockType <= 2)
				{
					block.setType(Block.BLOCK_TYPE_MOVING);
				}
				blockList.add(block);
				array.add(currentY);
				score += 5;
			}

			// Blocks collisions.
			for (int i = 0; i < blockList.size(); i++)
			{
				// Collision with right side of screen.
				if (blockList.get(i).getX() >= getWidth() - blockList.get(i).getBitmap().getWidth() / 2)
				{
					blockList.get(i).getVelocity().toggleXDirection();
				}
				// Collision with left side of screen.
				if (blockList.get(i).getX() <= 0 + blockList.get(i).getBitmap().getWidth() / 2)
				{
					blockList.get(i).getVelocity().toggleXDirection();
				}

				// Update the current block.
				blockList.get(i).update();
			}

		}// End if(player in screen).

		// Player X Axis update by sensor value.
		hero.update(mOrientation[1]);
	}

	private void timeScroll()
	{
		if (MainThread.gameState == GameStates.RUNNING)
		{
			if (hero.getY() <= 320)
			{
				if (timeCount < 3)
				{
					timeCount = (currentTime / 100000);
				}
				for (int i = 0; i < blockList.size(); i++)
				{
					currentY = array.get(i) + 0.5f + timeCount;
					blockList.get(i).setY(currentY);

					// Changing the Blocks Y coords in the array.
					array.remove(i);
					array.add(i, currentY);
				}
			}
		}
	}

	private void scrollUp()
	{
		for (int i = 0; i < blockList.size(); i++)
		{
			currentY = array.get(i) + 4;
			blockList.get(i).setY(currentY);

			// Changing the Blocks Y coords in the array.
			array.remove(i);
			array.add(i, currentY);
		}
	}

	protected void render(Canvas canvas)
	{
		canvas.drawColor(Color.WHITE);

		timeScroll();
		Iterator<Block> it = blockList.iterator();
		while (it.hasNext())
		{
			it.next().draw(canvas);
		}
		hero.draw(canvas);
		drawScore(canvas);

		if (MainThread.gameState == GameStates.PAUSED)
		{
			drawPaused(canvas);
		}
		if (MainThread.gameState == GameStates.GAME_OVER)
		{
			drawGameOverScreen(canvas);
		}
	}

	private void drawPaused(Canvas canvas)
	{
		textPaint.setTextAlign(Paint.Align.CENTER);
		textPaint.setTextSize(30);
		textPaint.setColor(Color.LTGRAY);
		canvas.drawText("GAME PAUSED", (float) (getWidth() * 0.50), (float) (getHeight() * 0.50), textPaint);

	}

	private void drawScore(Canvas canvas)
	{
		textPaint.setTextAlign(Paint.Align.LEFT);
		textPaint.setTextSize(15);
		textPaint.setColor(Color.BLACK);
		canvas.drawText("Score: " + score, 1, 19, textPaint);
	}

	public void drawGameOverScreen(Canvas canvas)
	{
		textPaint.setTextSize(40);
		textPaint.setTextAlign(Paint.Align.CENTER);
		textPaint.setColor(Color.LTGRAY);
		canvas.drawText("GAME OVER", (float) (getWidth() * 0.50), (float) (getHeight() * 0.50), textPaint);
		textPaint.setTextSize(25);
		canvas.drawText("Your score: " + score, (float) (getWidth() * 0.50), (float) (getHeight() * 0.70), textPaint);
		checkIfHighscore();
	}

	private void checkIfHighscore()
	{

	}

	private void getSensorData(Context context)
	{

		// Getting Sensor info
		mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
		mSensorManager.registerListener(this,
										mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
										SensorManager.SENSOR_DELAY_GAME);
		mSensorManager.registerListener(this,
										mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
										SensorManager.SENSOR_DELAY_GAME);
	}

	private int playerHight()
	{
		return hero.getBitmap().getHeight();
	}

	public MainThread getThread()
	{
		return thread;
	}

	public void setThread(MainThread thread)
	{
		this.thread = thread;
	}

}
