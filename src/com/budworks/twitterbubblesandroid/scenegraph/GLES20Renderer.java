package com.budworks.twitterbubblesandroid.scenegraph;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView.Renderer;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class GLES20Renderer implements Renderer, OnTouchListener,
		SensorEventListener {

	private static final String LOG_TAG = null;

	private GraphicsContext context;

	private Scene scene;

	private Context activityContext;

	public GLES20Renderer(Context activityContext) {
		this.activityContext = activityContext;
	}

	@Override
	public void onSurfaceCreated(GL10 unused, EGLConfig config) {
		// Set the background frame color
		GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		// load the fixed function shader and initialize the context
		this.context = ContextAndroidGLES20.getInstance(activityContext);
		scene = new TwitterBubbleScene();
	}

	@Override
	public void onSurfaceChanged(GL10 unused, int width, int height) {
		Log.i(LOG_TAG,
				"Surface changed ... setting new Viewport, the projection matrix and calling setup on the current scene!");
		GLES20.glViewport(0, 0, width, height);

		float ratio = (float) width / height;
		// sets the projection matrix which is applied to object coordinates
		// in the onDrawFrame() method of each node
		context.matrixMode(GraphicsContext.Constants.PROJECTION);
		context.frustum(-ratio, ratio, -1, 1, 3, 7);
		scene.setupBase(context);
		lastDrawTimeNanos = System.nanoTime();
	}

	private long lastDrawTimeNanos;

	@Override
	public void onDrawFrame(GL10 unused) {
		long currentTimeNanos = System.nanoTime();
		long elapsedTimeNanos = currentTimeNanos - lastDrawTimeNanos;
		float elapsedTimeMillis = elapsedTimeNanos / 1000000.0f;
		// Redraw background color
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
		context.matrixMode(GraphicsContext.Constants.MODEL);
		scene.drawBase(context, elapsedTimeMillis);
		lastDrawTimeNanos = currentTimeNanos;
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		return false;
	}

}
