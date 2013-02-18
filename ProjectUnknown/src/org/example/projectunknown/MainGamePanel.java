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

	private MainThread thread;

	private PlayerHero playerHero;

	// TODO
	private final float BLOCK_Y = 310; // getHeight() - 10;

	// Sensors data arrays
	private SensorManager mSensorManager;

	private float[] mAccelerometerReading;

	private float[] mMagneticFieldReading;

	private float[] mRotationMatrix = new float[16];

	private float[] mRemapedRotationMatrix = new float[16];

	public float[] mOrientation = new float[3];

	Block block;

	List<Block> blockList = new ArrayList<Block>();

	List<Float> array = new ArrayList<Float>();

	Random rand = new Random();

	float blockX;

	float newBlockX;

	float currentY;

	public MainGamePanel(Context context)
	{

		super(context);

		// Handling the events happening on the actual surface.
		getHolder().addCallback(this);

		// blockList = new ArrayList<Block>();
		// array = new ArrayList<Float>();
		currentY = BLOCK_Y;

		for (int i = 0; i < 8; i++)
		{
			currentY -= 45;
			blockX = rand.nextInt(200) + 20;
			block = new Block(BitmapFactory.decodeResource(getResources(), R.drawable.floor), blockX, currentY);
			blockList.add(block);
			array.add(currentY);
		}

		// Creating the player
		playerHero = new PlayerHero(BitmapFactory.decodeResource(getResources(), R.drawable.smiley), 120, 315);

		// create the game loop thread
		thread = new MainThread(getHolder(), this);

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
		thread.start();
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
				thread.join();
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

	// Check for Collisions with walls and blocks
	public void update()
	{

		if (playerHero.getSpeed().getxDirection() == Velocity.DIRECTION_RIGHT
				&& playerHero.getX() + playerHero.getBitmap().getWidth() / 2 >= getWidth())
		{
			playerHero.setX(getLeft());
			playerHero.setX((playerHero.getX() + (mOrientation[1])) * Velocity.DIRECTION_RIGHT);
		}

		if (playerHero.getSpeed().getxDirection() == Velocity.DIRECTION_LEFT
				&& playerHero.getX() - playerHero.getBitmap().getWidth() / 2 <= getLeft())
		{
			playerHero.setX(getRight());
			playerHero.setX(-(playerHero.getX() - (mOrientation[1])) * Velocity.DIRECTION_LEFT);
		}

		if (playerHero.getSpeed().getyDirection() == Velocity.DIRECTION_DOWN
				&& playerHero.getY() + playerHight() / 2 >= getHeight())
		{
			playerHero.getSpeed().toggleYDirection();
			playerHero.currentY = playerHero.getY();
		}

		if (playerHero.getSpeed().getyDirection() == Velocity.DIRECTION_UP
				&& playerHero.getY() - playerHight() / 2 <= 0)
		{
			playerHero.getSpeed().toggleYDirection();
		}

		if (playerHero.getSpeed().getyDirection() == Velocity.DIRECTION_DOWN)
		{
			for (int i = 0; i < blockList.size(); i++)
			{
				if (playerHero.getY() + playerHight() >= blockList.get(i).getY()
						&& playerHero.getY() + playerHight() <= blockList.get(i).getY()
								+ blockList.get(i).getBitmap().getHeight() / 2
						&& playerHero.getX() >= blockList.get(i).getX() - blockList.get(i).getBitmap().getWidth() / 2
						&& playerHero.getX() <= blockList.get(i).getX() + blockList.get(i).getBitmap().getWidth() / 2)
				{

					playerHero.getSpeed().toggleYDirection();
					playerHero.currentY = playerHero.getY();
				}
			}
		}

		if (blockList.get(0).getY() >= 320)
		{
			blockList.remove(0);
			// System.out.println("Removed Block");
			array.remove(0);
		}

		if (blockList.size() <= 7)
		{
			blockX = rand.nextInt(200) + 20;
			currentY = blockList.get(blockList.size() - 1).getY() - 45;

			block = new Block(BitmapFactory.decodeResource(getResources(), R.drawable.floor), blockX, currentY);
			blockList.add(block);
			// System.out.println("New Block Created");
			array.add(currentY);
		}

		// Update the playerHero and block objects
		playerHero.update(mOrientation[1]);

	}

	private void scrollUp()
	{
		for (int i = 0; i < blockList.size(); i++)
		{

			currentY = array.get(i) + 3;
			array.remove(i);
			array.add(i, currentY);

			blockList.get(i).setY(currentY);
		}
	}

	protected void render(Canvas canvas)
	{
		canvas.drawColor(Color.BLACK);
		playerHero.draw(canvas);

		if (playerHero.getY() - playerHight() <= (getHeight() * 0.35))
		{
			scrollUp();

		}

		Iterator<Block> it = blockList.iterator();
		while (it.hasNext())
		{
			it.next().draw(canvas);
		}

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
		return playerHero.getBitmap().getHeight();
	}

}
