package com.budworks.twitterbubblesandroid.scenegraph;

import java.util.ArrayList;
import java.util.List;

import android.view.View.OnTouchListener;

/**
 * Root of the scene graph. A Scene is responsible for loading it's resources in
 * other words build up it's scene graph.
 * 
 * @author S. Buder
 * 
 */
public abstract class Scene extends Node {

	// private List<ShutdownSceneCallback> shutdownListeners = new
	// ArrayList<ShutdownSceneCallback>();

	private List<OnTouchListener> touchListeners =
			new ArrayList<OnTouchListener>();

	private Thread updateThread;

	private boolean updateThreadRunning;

	/**
	 * the active camera
	 */
	private Camera camera;

	private int fps;

	/**
	 * 
	 * @param shutdownListener
	 * @param fps
	 *           frames per second for the update thread
	 */
	// public Scene(ShutdownSceneCallback shutdownListener, int fps) {
	// this.shutdownListeners.add(shutdownListener);
	// this.fps = fps;
	// }

	public Camera getCamera() {
		return camera;
	}

	public final void add(Node obj) {
		addChild(obj);
		if (OnTouchListener.class.isInstance(obj)) {
			touchListeners.add((OnTouchListener) obj);
		}
	}

	@Override
	public void setup(GraphicsContext context) {
		if (camera == null) {
			camera = new Camera();
		}
		if (updateThread == null) {
			startUpdateThread(false);
		}
	}

	private void startUpdateThread(final boolean withFps) {
		this.updateThread = new Thread(new Runnable() {

			private double millisPerFrame = (1.0 / fps) * 1000;

			// this is to count the milliseconds until one second has been reached
			private double elapsedMillis;

			private long lastUpdateTime = System.nanoTime();

			private boolean _withFps = withFps;

			@Override
			public void run() {
				int frameCounter = 1;
				updateThreadRunning = true;
				while (updateThreadRunning) {
					long currentNanoTime = System.nanoTime();
					// calculate elapsedTime in milliseconds
					float elapsedTimeMillis =
							(currentNanoTime - lastUpdateTime) / 1000000.0f;
					updateBase(elapsedTimeMillis);
					if (_withFps) {
						elapsedMillis += elapsedTimeMillis;
						// calculate the amount of milliseconds we can give the other
						// threads to do something on the objects
						// TODO if millis2sleep is negative the updateBase-Method took
						// too long
						long millis2sleep =
								Math.round((frameCounter * millisPerFrame)
										- elapsedMillis);
						if (millis2sleep > 0) {
							try {
								Thread.sleep(millis2sleep);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						// reset the frame counter if it exceeds the frames per second
						if (++frameCounter >= fps) {
							frameCounter = 1;
						}
						// reset the millisecond counter
						if (elapsedMillis >= 1000) {
							elapsedMillis = 0;
						}
					}
					lastUpdateTime = currentNanoTime;
				}
			}
		});
		updateThread.start();
	}

	/**
	 * Optional to override update for deriving Scenes. Gets called by update
	 * thread so lock shared resource with drawing thread.
	 */
	@Override
	public void update(double elapsedTime) {
		// nothing to implement here
		// childs get updated by updateBase() called by update thread
	}

	public final void shutdown(GraphicsContext context) {
		updateThreadRunning = false;
	}

}
