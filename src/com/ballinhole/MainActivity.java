package com.ballinhole;

import com.ballinhole.model.GameState;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements SensorEventListener {

	private static final int FPS = 10;

	private CanvasView canvasView;

	private ImageButton buttonPlay;
	private ImageButton buttonPause;
	private ImageButton buttonStop;
	private TextView timerText;

	private long timepassed = 0;
	private SensorManager sensor;
	private Handler timerHandler = new Handler();
	private Runnable timerRunnable;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		canvasView = (CanvasView) findViewById(R.id.canvas);
		buttonPlay = (ImageButton) findViewById(R.id.buttonPlay);
		buttonPause = (ImageButton) findViewById(R.id.buttonPause);
		buttonStop = (ImageButton) findViewById(R.id.buttonStop);
		timerText = (TextView) findViewById(R.id.timer);

		sensor = (SensorManager) getSystemService(SENSOR_SERVICE);
		sensor.registerListener(this, sensor.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);

		init();

		timerRunnable = new Runnable() {
			@Override
			public void run() {
				timerHandler.postDelayed(this, 1000 / FPS);
				timepassed += (1000 / FPS);
				refreshTimer();
			}
		};
	}

	public void init() {

		timepassed = 0;
		refreshTimer();
		changeGameState(GameState.STATE_STOPPED);
		canvasView.init();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void playGame(View view) {
		if(canvasView.getState() != GameState.STATE_PLAYING) {
			if(canvasView.getState() != GameState.STATE_PAUSED)
				canvasView.init();
			changeGameState(GameState.STATE_PLAYING);
			timerHandler.postDelayed(timerRunnable, 0);
		}
	}

	public void pauseGame(View view) {
		if(canvasView.getState() == GameState.STATE_PLAYING) {
			timerHandler.removeCallbacks(timerRunnable);
			changeGameState(GameState.STATE_PAUSED);
		}
	}

	public void stopGame(View view) {
		if(canvasView.getState() != GameState.STATE_STOPPED) {
			timerHandler.removeCallbacks(timerRunnable);
			changeGameState(GameState.STATE_STOPPED);
			init();
		}
	}

	public void changeGameState(GameState gs) {
		canvasView.setState(gs);
		if(canvasView.getState() == GameState.STATE_PLAYING) {
			buttonPlay.setClickable(false);
			buttonPause.setClickable(true);
			buttonStop.setClickable(true);
		} else if(canvasView.getState() == GameState.STATE_PAUSED) {
			buttonPlay.setClickable(true);
			buttonPause.setClickable(false);
			buttonStop.setClickable(true);
		} else {
			buttonPlay.setClickable(true);
			buttonPause.setClickable(false);
			buttonStop.setClickable(false);
		}
	}

	public String msToTime(long ms) {
		long millis = ms % 1000;
		long second = (ms / 1000) % 60;
		long minute = (ms / (1000 * 60)) % 60;
		return String.format("%02d:%02d:%03d", minute, second, millis);
	}

	public void refreshTimer() {
		String time = msToTime(timepassed);
		timerText.setText(time);
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER && canvasView.getState() == GameState.STATE_PLAYING){
			double vx = -event.values[0];
			double vy = event.values[1];
			
			canvasView.getBall().setX(canvasView.getBall().getX() + (int) vx);
			canvasView.getBall().setY(canvasView.getBall().getY() + (int) vy);
			
			if(canvasView.getBall().getX() < 0)
				canvasView.getBall().setX(0);
			else if(canvasView.getBall().getX() > (canvasView.getWidth() - canvasView.getBall().getSize())) {
				canvasView.getBall().setX((canvasView.getWidth() - canvasView.getBall().getSize()));
			}
			
			if(canvasView.getBall().getY() < 0)
				canvasView.getBall().setY(0);
			else if(canvasView.getBall().getY() > (canvasView.getHeight() - canvasView.getBall().getSize())) {
				canvasView.getBall().setY((canvasView.getHeight() - canvasView.getBall().getSize()));
			}
			
			canvasView.invalidate();
			
			int bminx = canvasView.getBall().getX();
			int bminy = canvasView.getBall().getY();
			int bmaxx = canvasView.getBall().getX() + canvasView.getBall().getSize();
			int bmaxy = canvasView.getBall().getY() + canvasView.getBall().getSize();
			
			int hminx = canvasView.getHole().getX();
			int hminy = canvasView.getHole().getY();
			int hmaxx = canvasView.getHole().getX() + canvasView.getHole().getSize();
			int hmaxy = canvasView.getHole().getY() + canvasView.getHole().getSize();
			
			if(bminx >= hminx && bminy >= hminy && bmaxx <= hmaxx && bmaxy <= hmaxy) {
				Toast t = Toast.makeText(getApplicationContext(), 
						"Congratulations!\nYou have completed the game in " + msToTime(timepassed) + "!", 
						Toast.LENGTH_LONG);
				t.show();
				stopGame(null);
			}
		}

	}

}
