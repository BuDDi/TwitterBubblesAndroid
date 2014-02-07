package com.budworks.twitterbubblesandroid.scenegraph;

import com.budworks.twitterbubblesandroid.geom.Vector3f;

public class Camera {

	private Vector3f position;

	private Vector3f up;

	private Vector3f target;

	/**
	 * Creates a basic camera with <b>position = (0, 0, 0)</b> <b>target = (0, 0,
	 * 0)</b> and <b>up = (0, 1, 0)</b>
	 */
	public Camera() {
		super();
		position = new Vector3f();
		target = new Vector3f();
		up = new Vector3f(0, 1, 0);
	}

	public Vector3f getPosition() {
		return position;
	}

	public Vector3f getUp() {
		return up;
	}

	public Vector3f getTarget() {
		return target;
	}

}
