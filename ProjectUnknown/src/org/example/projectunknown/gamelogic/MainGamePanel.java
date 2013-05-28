package org.example.projectunknown.gamelogic;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.example.projectunknown.R;
import org.example.projectunknown.gameactivities.Game;
import org.example.projectunknown.gameactivities.ProjectUnknown;
import org.example.projectunknown.model.Block;
import org.example.projectunknown.model.Enemy;
import org.example.projectunknown.model.PlayerHero;
import org.example.projectunknown.model.components.Velocity;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
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
	private Enemy enemy;

	// Blocks variables
	private Block block;
	private List<Block> blockList = new ArrayList<Block>();
	private List<Float> array = new ArrayList<Float>();
	private final float BLOCK_Y = 310;

	float blockX;
	float newBlockX;
	float currentY;

	private int blockType;

	// Sensors data arrays
	private SensorManager mSensorManager;
	private float[] mAccelerometerReading;
	private float[] mMagneticFieldReading;
	private float[] mRotationMatrix = new float[16];
	private float[] mRemapedRotationMatrix = new float[16];
	public float[] mOrientation = new float[3];
	private Random rand = new Random();

	public static int score = 0;

	public long beginTime;
	public static long currentTime;

	public static final float scale = 1.0f;

	long timeCount;

	int random;
	int blockSkin;
	int heroSkin;

	int background;

	private Bitmap bgPic;

	private Matrix matrix;

	private int textColor;

	private Game game;

	public MainGamePanel(Context context, int theme)
	{
		super(context);

		// Handling the events happening on the actual surface.
		getHolder().addCallback(this);
		beginTime = System.currentTimeMillis();

		switch (theme)
		{
			case 0:
				Log.d(TAG, "Loading Space skins.");
				blockSkin = R.drawable.space_block;
				background = R.drawable.space_bg;
				textColor = Color.LTGRAY;
				break;
			case 1:
				Log.d(TAG, "Loading Ice skins.");
				blockSkin = R.drawable.ice_block;
				background = R.drawable.ice_bg;
				textColor = Color.BLACK;
				break;
			case 2:
				Log.d(TAG, "Loading Nature skins.");
				blockSkin = R.drawable.nature_block;
				background = R.drawable.nature_bg;
				textColor = Color.BLACK;
				break;
		}
		heroSkin = R.drawable.penguin_blue;
		
		bgPic = BitmapFactory.decodeResource(getResources(), background);
		matrix = new Matrix();
		matrix.postScale(1.4f, 1.4f);

		// Creating blocks.
		currentY = BLOCK_Y;
		for (int i = 0; i < 7; i++)
		{
			currentY -= 50;
			blockX = rand.nextInt(200) + 20;
			block = new Block(BitmapFactory.decodeResource(getResources(), blockSkin), blockX, currentY);
			blockList.add(block);
			array.add(currentY);
		}

		// Creating the player.
		hero = new PlayerHero(BitmapFactory.decodeResource(getResources(), heroSkin), 120, 310);
		
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
			} catch (InterruptedException e)
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
			if (MainThread.gameState == GameStates.GAME_OVER)
			{
				android.os.Process.killProcess(android.os.Process.myPid());				
			}
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

	protected void update()
	{
		if (hero.getY() <= 320)

		{
			random = rand.nextInt(1000);
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
			if (hero.getVelocity().getyDirection() == Velocity.DIRECTION_DOWN && hero.getY() + heroHeight() / 2 >= 320)
			{
				if (currentTime <= 5000)
				{
					hero.getVelocity().toggleYDirection();
				} else
				{
					MainThread.gameState = GameStates.GAME_OVER;
				}
			}
			// Collision with highest point of the screen that the hero can get.
			if (hero.getVelocity().getyDirection() == Velocity.DIRECTION_UP
					&& hero.getY() - heroHeight() / 2 <= COLLISION_UP)
			{
				hero.getVelocity().toggleYDirection();
			}

			/* BLOCKS COLLISIONS. */

			// Collision of the playerHero with BLOCKS.
			if (hero.getVelocity().getyDirection() == Velocity.DIRECTION_DOWN)
			{
				for (int i = 0; i < blockList.size(); i++)
				{
					if (hero.getY() + heroHeight()/2 >= blockList.get(i).getY() - blockList.get(i).getBitmap().getHeight()/2
							&& hero.getY() + heroHeight()/2 <= blockList.get(i).getY()
									+ blockList.get(i).getBitmap().getHeight() / 2
							&& hero.getX() + hero.getBitmap().getWidth()/2 >= blockList.get(i).getX() - blockList.get(i).getBitmap().getWidth() / 2
							&& hero.getX() - hero.getBitmap().getWidth()/2 <= blockList.get(i).getX() + blockList.get(i).getBitmap().getWidth() / 2)
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

				block = new Block(BitmapFactory.decodeResource(getResources(), blockSkin), blockX, currentY);

				if (blockType <= 2)
				{
					block.setType(Block.BLOCK_TYPE_MOVING);
				}
				blockList.add(block);
				array.add(currentY);
				score += 5;
			}

			// Collisions of blocks with sides of screen.
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

				timeScroll();
				// Update the current block.
				blockList.get(i).update();
			}

			// ENEMY COLLISIONS.
			if (enemy == null && random < 5 && currentTime >= 7000)
			{
				System.out.println("Enemy created");
				enemy = new Enemy(	BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher),
									(rand.nextInt(200) + 20),
									-20);
			}

			if (enemy != null)
			{
				// Collision with right side of screen.
				if (enemy.getX() >= getWidth() - enemy.getBitmap().getWidth() / 2)
				{
					enemy.getVelocity().toggleXDirection();
				}
				// Collision with left side of screen.
				if (enemy.getX() <= 0 + enemy.getBitmap().getWidth() / 2)
				{
					enemy.getVelocity().toggleXDirection();
				}

				// Deleting enemy if(enemy is out of scren).
				if (enemy.getY() > 320)
				{
					enemy = null;
				}

				// Game Over if(player is hit).
				if (hero.getVelocity().getyDirection() == Velocity.DIRECTION_UP
						|| hero.getVelocity().getyDirection() == Velocity.DIRECTION_LEFT
						|| hero.getVelocity().getyDirection() == Velocity.DIRECTION_RIGHT)
				{
					if (enemy != null)
					{
						if ((hero.getY() - heroHeight() / 2) <= (enemy.getY() + enemyHeight() / 2)
								&& (hero.getY() - heroHeight() / 2) >= (enemy.getY() - enemyHeight() / 2)
								&& (hero.getX() - hero.getBitmap().getWidth() / 2 > enemy.getX()
										- enemy.getBitmap().getWidth() / 2)
								&& (hero.getX() + hero.getBitmap().getWidth() / 2 < enemy.getX()
										+ enemy.getBitmap().getWidth() / 2))
						{
							System.out.println("COLIZIQQQ");
							MainThread.gameState = GameStates.GAME_OVER;
						}
					}

				}

				// Update enemy.
				if (enemy != null)
				{
					enemy.update();
				}

			}

			// Player update.
			hero.update(mOrientation[1]);
		}// End if(player in screen).
	}

	private void timeScroll()
	{
		if (MainThread.gameState == GameStates.RUNNING)
		{
			if (hero.getY() <= 320)
			{
				if (timeCount < 0.3f)
				{
					timeCount = (currentTime / 100000);
				}
				for (int i = 0; i < blockList.size(); i++)
				{
					currentY = array.get(i) + 0.1f + timeCount;
					blockList.get(i).setY(currentY);

					// Changing the Blocks Y coords in the array.
					array.remove(i);
					array.add(i, currentY);
				}
				if (enemy != null)
				{
					enemy.setY(enemy.getY() + 0.2f + timeCount);
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
		if (enemy != null)
		{
			enemy.setY(enemy.getY() + 4);
		}
	}

	protected void render(Canvas canvas)
	{
		canvas.drawColor(Color.BLACK);
		canvas.drawBitmap(bgPic, matrix, null);

		Iterator<Block> it = blockList.iterator();
		while (it.hasNext())
		{
			it.next().draw(canvas);
		}

		if (enemy != null)
		{
			enemy.draw(canvas);
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
		textPaint.setColor(textColor);
		canvas.drawText("Score: " + score, 1, 19, textPaint);
	}

	public void drawGameOverScreen(Canvas canvas)
	{
		textPaint.setTextSize(40);
		textPaint.setTextAlign(Paint.Align.CENTER);
		textPaint.setColor(Color.LTGRAY);
		canvas.drawText("GAME OVER", (float) (getWidth() * 0.50), (float) (getHeight() * 0.50), textPaint);
		textPaint.setTextSize(25);
		canvas.drawText("Your score is: " + score, (float) (getWidth() * 0.50), (float) (getHeight() * 0.70), textPaint);
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

	private int heroHeight()
	{
		return hero.getBitmap().getHeight();
	}

	private int enemyHeight()
	{
		return enemy.getBitmap().getHeight();
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
