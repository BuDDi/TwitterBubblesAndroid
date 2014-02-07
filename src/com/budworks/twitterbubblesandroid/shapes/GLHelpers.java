package com.budworks.twitterbubblesandroid.shapes;

import android.opengl.GLES20;
import android.util.Log;

public abstract class GLHelpers {

	static final int FLOATS_PER_VERTEX = 3; // amount floats per vertex (x, y, z)

	static final int FLOATS_PER_COLOR = 4; // amount of floats per color (r,
														// g, b, a)

	static final int FLOATS_PER_TEXTURE_COORD = 2;

	static final int SHORTS_PER_TRIANGLE_INDEX = 6;

	static final int BYTES_PER_FLOAT = 4;

	static final int BYTES_PER_SHORT = 2;

	static final int BYTES_PER_VERTEX = FLOATS_PER_VERTEX * BYTES_PER_FLOAT;

	static final int BYTES_PER_COLOR = FLOATS_PER_COLOR * BYTES_PER_FLOAT;

	static final int BYTES_PER_TRIANGLE_INDEX = SHORTS_PER_TRIANGLE_INDEX
			* BYTES_PER_SHORT;

	static final int BYTES_PER_TEXTURE_COORD = FLOATS_PER_TEXTURE_COORD
			* BYTES_PER_FLOAT;

	private static final String LOG_TAG = GLHelpers.class.getName();

	public static void checkGlError(String glOperation) {
		int error;
		while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
			Log.e(LOG_TAG, glOperation + ": glError " + error);
			// throw new RuntimeException(glOperation + ": glError " + error);
		}
	}
}
