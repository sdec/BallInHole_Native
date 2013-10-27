package com.ballinhole;

import com.ballinhole.model.GameObject;
import com.ballinhole.model.GameState;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class CanvasView extends View {

	private GameObject ball;
	private GameObject hole;
	private GameState state;
	
	Paint paint;
	Bitmap ballBitmap;
	Bitmap holeBitmap;
	
	public CanvasView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		paint = new Paint();
		paint.setColor(Color.TRANSPARENT);
		ballBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ball);
		holeBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.hole);
	}
	
	public void init() {
		
		invalidate();
		
		int size = holeBitmap.getWidth();
		int x = (int)(Math.random() * (getWidth() - size));
		int y = (int)(Math.random() * (getHeight() - size));
		hole = new GameObject(x, y, size);
		
		size = ballBitmap.getWidth();
		x = (int)(Math.random() * (getWidth() - size));
		y = (int)(Math.random() * (getHeight() - size));
		ball = new GameObject(x, y, size);
		
	}

	@SuppressLint("DrawAllocation")
	@Override
	public void onDraw(Canvas canvas) {
		if(state != GameState.STATE_PAUSED) {
			canvas.drawRect(0, 0, getWidth(), getHeight(), paint);
			if(state == GameState.STATE_PLAYING) {
				canvas.drawBitmap(holeBitmap, hole.getX(), hole.getY(), null);
		        canvas.drawBitmap(ballBitmap, ball.getX(), ball.getY(), null);
			}
		}
	}

	public GameState getState() {
		return state;
	}

	public void setState(GameState state) {
		this.state = state;
	}

	public GameObject getBall() {
		return ball;
	}

	public void setBall(GameObject ball) {
		this.ball = ball;
	}

	public GameObject getHole() {
		return hole;
	}

	public void setHole(GameObject hole) {
		this.hole = hole;
	}
	
}
