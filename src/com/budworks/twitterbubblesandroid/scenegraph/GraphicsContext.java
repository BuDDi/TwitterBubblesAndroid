package com.budworks.twitterbubblesandroid.scenegraph;

import com.budworks.twitterbubblesandroid.geom.Vector3f;

/**
 * Context that wraps GL matrix operations. Implementations should follow the
 * state machine approach of OpenGL to get the scene graph principle working.
 * 
 * @author S. Buder
 * 
 */
public interface GraphicsContext {

	public static interface Constants {
		public static final byte MODEL = 1;

		public static final byte VIEW = 2;

		public static final byte PROJECTION = 4;
	}

	/**
	 * <code>matrixMode</code> can be used as a bitmask.
	 * 
	 * @param matrixMode
	 *           valid values are {@link Constants#MODEL}, {@link Constants#VIEW}
	 *           and/or {@link Constants#PROJECTION}
	 */
	void matrixMode(int matrixMode);

	/**
	 * Sets the appropriate matrix depending on the matrix mode.
	 */
	void loadIndentity();

	/**
	 * Saves the current matrix depending on the matrix mode.
	 */
	void pushMatrix();

	/**
	 * pops the last saved matrix from stack depending on the current matrix
	 * mode.
	 */
	void popMatrix();

	float[] getModelViewProjectionMatrix();

	/**
	 * Translates the matrix depending on the matrix mode.
	 * 
	 * @param vec
	 *           translation vector
	 */
	void translate(Vector3f vec);

	/**
	 * 
	 * @param angle
	 *           in degrees
	 * @param axis
	 */
	void rotate(float angle, Vector3f axis);

	void scale(float factor);

	void scale(float facX, float facY, float facZ);

	/**
	 * Sets the current projection matrix.
	 * 
	 * @param field
	 *           of view in y direction, in degrees
	 * @param aspect
	 *           width to height aspect ratio of the viewport
	 * @param zNear
	 *           near clipping plane
	 * @param zFar
	 *           far clipping plane
	 */
	void projection(float fovy, float aspect, float zNear, float zFar);

	/**
	 * Defines a viewing transformation in terms of an eye point, a target, and
	 * an up vector.
	 * 
	 * @param eyePos
	 *           the position of your camera, in world space
	 * @param target
	 *           where you want to look at, in world space upVector
	 * @param up
	 *           probably (0,1,0), but (0,-1,0) would make you looking
	 *           upside-down, which can be great too
	 */
	void lookAt(Vector3f eyePos, Vector3f target, Vector3f up);

	/**
	 * Defines a viewing transformation in terms of a camera.
	 * 
	 * @param camera
	 *           contains lookAt parameter eyePos, target and up
	 */
	void lookAt(Camera camera);

	/**
	 * Sets the projection matrix by left, rigth, bottom, top, near, far
	 * 
	 * @param left
	 * @param right
	 * @param bottom
	 * @param top
	 * @param near
	 * @param far
	 */
	void frustum(float left, float right, float bottom, float top, float near,
			float far);

	/**
	 * 
	 * @return the current matrix mode.
	 */
	int getMatrixMode();
}
