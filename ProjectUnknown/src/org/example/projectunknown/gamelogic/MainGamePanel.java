package org.example.projectunknown.gamelogic;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.example.projectunknown.R;
import org.example.projectunknown.gameactivities.ProjectUnknown;
import org.example.projectunknown.media.Music;
import org.example.projectunknown.model.Block;
import org.example.projectunknown.model.Coin;
import org.example.projectunknown.model.Enemy;
import org.example.projectunknown.model.PlayerHero;
import org.example.projectunknown.model.components.Velocity;

import android.content.Context;
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
import android.media.MediaPlayer;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class MainGamePanel extends SurfaceView implements SurfaceHolder.Callback, SensorEventListener
{

	private static final String TAG = MainGamePanel.class.getSimpleName();

	private float CollisionUp;
	private float StartingBlockY;

	private MainThread thread;
	private PlayerHero hero;
	private Paint textPaint;
	private Enemy enemy;

	// Blocks variables
	private Block block;
	private Coin coin;
	private List<Block> blockList = new ArrayList<Block>();
	private List<Float> array = new ArrayList<Float>();
	private List<Coin> coinList = new ArrayList<Coin>();
	private List<Float> coinArray = new ArrayList<Float>();

	float blockX;
	float newBlockX;
	float currentY;

	private int blockType;
	private int randCoin;
	public static boolean artifactFound = false;

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

	float timeCount;

	int random;
	private int blockSkin;
	private int heroSkin;
	private int coinSkin;
	private int artifactSkin;

	int background;

	private Bitmap bgPic;

	private Matrix matrix;

	private int textColor;

	private float coinY;

	private long artifactTime;

	private int enemySkin;

	public static float density;

	public MainGamePanel(Context context, int theme)
	{
		super(context);

		// Handling the events happening on the actual surface.
		getHolder().addCallback(this);

		density = getResources().getDisplayMetrics().density;

		System.out.println("Density is:" + density);

		beginTime = System.currentTimeMillis();

		switch (theme)
		{
			case 0:
				Log.d(TAG, "Loading Space skins.");
				blockSkin = R.drawable.space_block;
				background = R.drawable.space_bg;
				coinSkin = R.drawable.star2;
				enemySkin = R.drawable.asteroid;
				textColor = Color.LTGRAY;
				break;
			case 1:
				Log.d(TAG, "Loading Ice skins.");
				blockSkin = R.drawable.ice_block;
				background = R.drawable.ice_bg;
				coinSkin = R.drawable.fish;
				enemySkin = R.drawable.shark;
				textColor = Color.BLACK;
				break;
			case 2:
				Log.d(TAG, "Loading Nature skins.");
				blockSkin = R.drawable.nature_block;
				background = R.drawable.nature_bg;
				coinSkin = R.drawable.banana;
				enemySkin = R.drawable.bear;
				textColor = Color.BLACK;
				break;
		}

		artifactSkin = R.drawable.artifact;
		heroSkin = R.drawable.penguin_blue;

		bgPic = BitmapFactory.decodeResource(getResources(), background);
		matrix = new Matrix();
		matrix.postScale(1.6f, 1.6f);

		// Creating the player.
		hero = new PlayerHero(BitmapFactory.decodeResource(getResources(), heroSkin), 120, 310);

		// Creating Texts.
		textPaint = new Paint();

		// Create the game loop thread.
		setThread(new MainThread(getHolder(), this));

		StartingBlockY = 500;
		// Creating blocks.
		currentY = StartingBlockY;
		for (int i = 0; i < 7; i++)
		{
			currentY -= 50;
			blockX = rand.nextInt(200) + 20;
			block = new Block(BitmapFactory.decodeResource(getResources(), blockSkin), blockX, currentY);
			blockList.add(block);
			array.add(currentY);
		}

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
		CollisionUp = getHeight() / 6;

		if (hero.getY() <= getHeight() + 10)

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
			if (hero.getVelocity().getyDirection() == Velocity.DIRECTION_DOWN
					&& hero.getY() + heroHeight() / 2 >= getHeight())
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
					&& hero.getY() - heroHeight() / 2 <= CollisionUp)
			{
				hero.getVelocity().toggleYDirection();
			}

			/* BLOCKS COLLISIONS. */

			// Collision of the playerHero with BLOCKS.
			if (hero.getVelocity().getyDirection() == Velocity.DIRECTION_DOWN)
			{
				for (int i = 0; i < blockList.size(); i++)
				{
					if ((hero.getY() + (heroHeight() / 2)) >= (blockList.get(i).getY() - (blockList.get(i)
																									.getBitmap()
																									.getHeight() / 2)) // otskok
							&& (hero.getY() + (heroHeight() / 2)) <= blockList.get(i).getY()
							&& (hero.getX() + (hero.getBitmap().getWidth() / 2)) >= (blockList.get(i).getX() - (blockList.get(i)
																															.getBitmap()
																															.getWidth() / 2))
							&& (hero.getX() - (hero.getBitmap().getWidth() / 2)) <= (blockList.get(i).getX() + (blockList.get(i)
																															.getBitmap()
																															.getWidth() / 2)))
					{
						if (ProjectUnknown.prefMusic.getBoolean("SOUNDS", false) == true)
						{
							MediaPlayer.create(getContext(), R.raw.gameover).start();
						}
						hero.getVelocity().toggleYDirection();

						hero.setDestinationY(hero.getY() + ((getHeight() / 3) * hero.getVelocity().getyDirection()));
						score += 10;
					}
				}
			}

			// Scrolling the screen.
			if (hero.getY() <= blockList.get(3).getY())
			{
				if (hero.getDestinationY() < CollisionUp)
				{
					hero.setDestinationY(hero.getDestinationY() + 2);
				}
				scrollUp();
			}

			// Collision when block is under the bottom of screen.
			if (blockList.get(0).getY() >= getHeight())
			{
				blockList.remove(0);
				array.remove(0);
			}

			if (!coinList.isEmpty())
			{
				if (coinList.get(0).getY() >= getHeight())
				{
					coinList.remove(0);
					coinArray.remove(0);
				}
			}

			// Collision when block is deleted.
			if (blockList.size() <= 6)
			{
				blockType = rand.nextInt(10);
				randCoin = rand.nextInt(1000);

				blockX = (block.getBitmap().getWidth() / 2) + rand.nextInt(getWidth() - (block.getBitmap().getWidth()));
				currentY = blockList.get(blockList.size() - 1).getY() - getHeight() / 7;

				block = new Block(BitmapFactory.decodeResource(getResources(), blockSkin), blockX, currentY);

				if (blockType <= 2)
				{
					block.setType(Block.BLOCK_TYPE_MOVING);
				}

				blockList.add(block);
				array.add(currentY);

				if (randCoin >= 700 && blockType > 2)
				{
					coinY = (currentY - (block.getBitmap().getHeight()));
					coin = new Coin(BitmapFactory.decodeResource(getResources(), coinSkin), blockX, coinY);
					coin.setType(Coin.COIN_TYPE_NORMAL);
					coinList.add(coin);
					coinArray.add(coinY);
				}

				if (randCoin < 50 && blockType > 2)
				{
					coinY = (currentY - (block.getBitmap().getHeight()));
					coin = new Coin(BitmapFactory.decodeResource(getResources(), artifactSkin), blockX, coinY);
					coin.setType(Coin.COIN_TYPE_SPECIAL);
					coinList.add(coin);
					coinArray.add(coinY);
				}

				score += 5;
			}

			for (int i = 0; i < coinList.size(); i++)
			{
				coinList.get(i).update();
			}

			// Collisions of blocks with sides of screen.
			for (int i = 0; i < blockList.size(); i++)
			{
				// Collision with right side of screen.
				if (blockList.get(i).getX() + (blockList.get(i).getBitmap().getWidth() / 2) >= getWidth())
				{
					blockList.get(i).getVelocity().toggleXDirection();
				}
				// Collision with left side of screen.
				if (blockList.get(i).getX() - (blockList.get(i).getBitmap().getWidth() / 2) <= 0)
				{
					blockList.get(i).getVelocity().toggleXDirection();
				}

				timeScroll();
				// Update the current block.
				blockList.get(i).update();
			}

			// PLAYER - COIN COLLISIONS.

			if (!coinList.isEmpty())
			{
				for (int i = 0; i < coinList.size(); i++)
				{
					// If player goes through a coin.
					if ((hero.getY() + (heroHeight() / 2)) >= (coinList.get(i).getY() - (coinList.get(i)
																									.getBitmap()
																									.getHeight() / 2))
							&& (hero.getY() + (heroHeight() / 2)) <= (coinList.get(i).getY() + (coinList.get(i)
																										.getBitmap()
																										.getHeight() / 2))
							&& (hero.getX() + (hero.getBitmap().getWidth() / 2)) >= (coinList.get(i).getX() - (coinList.get(i)
																														.getBitmap()
																														.getWidth() / 2))
							&& (hero.getX() - (hero.getBitmap().getWidth() / 2)) <= (coinList.get(i).getX() + (coinList.get(i)
																														.getBitmap()
																														.getWidth() / 2)))
					{
						if (coinList.get(i).getType() == Coin.COIN_TYPE_NORMAL)
						{
							if (ProjectUnknown.prefMusic.getBoolean("SOUNDS", false) == true)
							{
								MediaPlayer.create(getContext(), R.raw.gameover).start();
							}
							score += 200;

						} else if (coinList.get(i).getType() == Coin.COIN_TYPE_SPECIAL)
						{
							if (ProjectUnknown.prefMusic.getBoolean("SOUNDS", false) == true)
							{
								MediaPlayer.create(getContext(), R.raw.gameover).start();
							}
							artifactFound = true;
							artifactTime = System.currentTimeMillis();
							score += 10000;
						}

						coinList.remove(i);
						coinArray.remove(i);
					} // End if
				}// End For
			} // End first if

			// ENEMY COLLISIONS.
			if (enemy == null && random < 5 && currentTime >= 7000)
			{
				System.out.println("Enemy created");
				enemy = new Enemy(	BitmapFactory.decodeResource(getResources(), enemySkin),
									(100 + rand.nextInt(getWidth()) - 150),
									-20);
			}

			// Collisions with Screen
			if (enemy != null)
			{
				// Collision with right side of screen.
				if (enemy.getX() + (enemy.getBitmap().getWidth() / 2) >= getWidth())
				{
					enemy.getVelocity().toggleXDirection();
				}
				// Collision with left side of screen.
				if (enemy.getX() - (enemy.getBitmap().getWidth() / 2) <= 0)
				{
					enemy.getVelocity().toggleXDirection();
				}

				// Deleting enemy if(enemy is out of scren).
				if (enemy.getY() > getHeight())
				{
					enemy = null;
				}

				// PLAYER - ENEMY COLLISIONS
				if (enemy != null)
				{

					if (((hero.getX() - hero.getBitmap().getWidth() / 3 <= enemy.getX() + enemy.getBitmap().getWidth()
							/ 3) && (hero.getX() - hero.getBitmap().getWidth() / 3 >= enemy.getX()
							- enemy.getBitmap().getWidth() / 3))
							|| ((hero.getX() + hero.getBitmap().getWidth() / 3 >= enemy.getX()
									- enemy.getBitmap().getWidth() / 3) && (hero.getX() + hero.getBitmap().getWidth()
									/ 3 <= enemy.getX() + enemy.getBitmap().getWidth() / 3)))
					{
						if (((hero.getY() - heroHeight() / 3 <= enemy.getY() + enemy.getBitmap().getHeight() / 3) && (hero.getY()
								- heroHeight() / 3 >= enemy.getY() - enemy.getBitmap().getHeight() / 3))
								|| ((hero.getY() + heroHeight() / 3 >= enemy.getY() - enemy.getBitmap().getHeight() / 3) && (hero.getY()
										+ heroHeight() / 3 <= enemy.getY() + enemy.getBitmap().getHeight() / 3)))
						{
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

			if (System.currentTimeMillis() - artifactTime > 2000)
			{
				artifactFound = false;
			}
		}// End if(player in screen).
	}

	private void timeScroll()
	{
		if (MainThread.gameState == GameStates.RUNNING)
		{
			if (hero.getY() <= getHeight())
			{
				if (timeCount < 1.0f)
				{
					timeCount = (((float) currentTime) / 500000.0f);
				}

				for (int i = 0; i < blockList.size(); i++)
				{
					currentY = array.get(i) + 0.1f + timeCount;
					blockList.get(i).setY(currentY);

					// Changing the Blocks Y coords in the array.
					array.remove(i);
					array.add(i, currentY);
				}

				if (!coinList.isEmpty())
				{
					for (int i = 0; i < coinList.size(); i++)
					{
						coinY = coinArray.get(i) + 0.1f + timeCount;
						coinList.get(i).setY(coinY);

						// Changing the Blocks Y coords in the array.
						coinArray.remove(i);
						coinArray.add(i, coinY);
					}
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
		// Move blocks.
		for (int i = 0; i < blockList.size(); i++)
		{
			currentY = array.get(i) + 2;
			blockList.get(i).setY(currentY);

			// Changing the Blocks Y coords in the array.
			array.remove(i);
			array.add(i, currentY);
		}

		// Move coins.
		if (!coinList.isEmpty())
		{
			for (int i = 0; i < coinList.size(); i++)
			{
				coinY = coinArray.get(i) + 2;
				coinList.get(i).setY(coinY);

				// Changing the Blocks Y coords in the array.
				coinArray.remove(i);
				coinArray.add(i, coinY);
			}
		}
		// Move enemy.
		if (enemy != null)
		{
			enemy.setY(enemy.getY() + 2);
		}
	}

	protected void render(Canvas canvas)
	{
		canvas.drawColor(Color.BLACK);

		// Draw background.
		canvas.drawBitmap(bgPic, matrix, null);

		// Draw coins.
		if (!coinList.isEmpty())
		{
			Iterator<Coin> it1 = coinList.iterator();
			while (it1.hasNext())
			{
				it1.next().draw(canvas);
			}
		}

		// Draw blocks.
		Iterator<Block> it = blockList.iterator();
		while (it.hasNext())
		{
			it.next().draw(canvas);
		}

		// Draw player.
		hero.draw(canvas);

		// Draw enemy.
		if (enemy != null)
		{
			enemy.draw(canvas);
		}

		// Draw texts.
		drawScore(canvas);

		if (artifactFound)
		{
			drawArtifactText(canvas);
		}

		if (MainThread.gameState == GameStates.PAUSED)
		{
			drawPaused(canvas);
		}

		if (MainThread.gameState == GameStates.GAME_OVER)
		{
			drawGameOverScreen(canvas);
		}
	}

	private void drawArtifactText(Canvas canvas)
	{

		textPaint.setTextAlign(Paint.Align.CENTER);
		textPaint.setTextSize(20);
		textPaint.setColor(Color.RED);
		canvas.drawText("Artifact Found", hero.getX(), hero.getY() - heroHeight(), textPaint);

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

	public MainThread getThread()
	{
		return thread;
	}

	public void setThread(MainThread thread)
	{
		this.thread = thread;
	}

}
