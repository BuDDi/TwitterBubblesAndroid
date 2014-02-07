package com.budworks.twitterbubblesandroid.activities;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.budworks.twitterbubblesandroid.scenegraph.GLES20Renderer;

public class OpenGLES20Activity extends Activity {

	private static final String LOG_TAG = OpenGLES20Activity.class.getName();

	private GLSurfaceView mGLView;

	private boolean accelerometerAvailable;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		if (hasGLES20()) {
			mGLView = new GLSurfaceView(this);
			// activate GL debugging
			mGLView.setDebugFlags(GLSurfaceView.DEBUG_CHECK_GL_ERROR
					| GLSurfaceView.DEBUG_LOG_GL_CALLS);
			mGLView.setEGLContextClientVersion(2);
			mGLView.setPreserveEGLContextOnPause(true);
			GLES20Renderer renderer = new GLES20Renderer(this);
			// register listener
			mGLView.setOnTouchListener(renderer);
			SensorManager manager =
					(SensorManager) getSystemService(Context.SENSOR_SERVICE);
			accelerometerAvailable =
					manager.getSensorList(Sensor.TYPE_ACCELEROMETER).size() > 0;
			Sensor accelerometer =
					manager.getSensorList(Sensor.TYPE_ACCELEROMETER).get(0);
			if (!manager.registerListener(renderer, accelerometer,
					SensorManager.SENSOR_DELAY_GAME)) {
				accelerometerAvailable = false;
			}
			// make OpenGL ES 2.0 work on the emulator with the following line
			mGLView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
			// actually set the renderer
			mGLView.setRenderer(renderer);
		} else {
			// take another renderer depending on the version available
			String msg = "No OpenGLES 2.0 available no renderer";
			Log.e(LOG_TAG, msg);
			throw new RuntimeException(msg);
		}

		setContentView(mGLView);
	}

	private boolean hasGLES20() {
		ActivityManager am =
				(ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		ConfigurationInfo info = am.getDeviceConfigurationInfo();
		// second part of return statement is a solution for making OpenGL ES 2.0
		// work on emulator cause first part always returns false
		return info.reqGlEsVersion >= 0x20000
				|| Build.FINGERPRINT.startsWith("generic");
	}

	@Override
	protected void onResume() {
		super.onResume();
		/*
		 * The activity must call the GL surface view's onResume() on activity
		 * onResume().
		 */
		if (mGLView != null) {
			mGLView.onResume();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		/*
		 * The activity must call the GL surface view's onPause() on activity
		 * onPause().
		 */
		if (mGLView != null) {
			mGLView.onPause();
		}
	}

}
