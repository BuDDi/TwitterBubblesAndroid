package com.budworks.twitterbubblesandroid.scenegraph;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Stack;

import android.content.Context;
import android.opengl.Matrix;
import android.util.Log;

import com.budworks.twitterbubblesandroid.geom.Vector3f;
import com.budworks.twitterbubblesandroid.shapes.Shader;
import com.budworks.twitterbubblesandroid.shapes.ShaderAndroidGLES20;

/**
 * Uses android's OpenGL matrix operations.
 * 
 * @author S. Buder
 * 
 */
public class ContextAndroidGLES20 implements GraphicsContext {

	private static final String LOG_TAG = GraphicsContext.class.getName();

	private float[] modelMatrix = new float[16];

	private Stack<float[]> stackModelMatrix = new Stack<float[]>();

	private float[] viewMatrix = new float[16];

	private Stack<float[]> stackViewMatrix = new Stack<float[]>();

	private float[] projectionMatrix = new float[16];

	private Stack<float[]> stackProjectionMatrix = new Stack<float[]>();

	private int matrixMode;

	/**
	 * this is the default shader that every object can use
	 */
	private Shader fixedFunctionShader;

	private Shader activeShader;

	private Context androidContext;

	private static ContextAndroidGLES20 instance;

	private ContextAndroidGLES20(Context androidContext) {
		super();
		this.androidContext = androidContext;
		loadFixedFunctionShader();
	}

	private void loadFixedFunctionShader() {
		Log.i(LOG_TAG, "Loading Fixed-Function-Shader ...");
		InputStream vertFileInputStream;
		try {
			vertFileInputStream =
					androidContext.getAssets().open("shader/FixedFuncGLES2.vert");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		InputStream fragFileInputStream;
		try {
			fragFileInputStream =
					androidContext.getAssets().open("shader/FixedFuncGLES2.frag");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		fixedFunctionShader =
				new ShaderAndroidGLES20(0).loadVertexShader(vertFileInputStream)
						.loadFragmentShader(fragFileInputStream);
		fixedFunctionShader.useShaders();
		fixedFunctionShader.startShader();
		activeShader = fixedFunctionShader;
	}

	public static GraphicsContext getInstance(Context context) {
		if (instance == null && context != null) {
			instance = new ContextAndroidGLES20(context);
		} else if (instance == null && context == null) {
			throw new RuntimeException(
					"Cannot instantiate context with context = " + context);
		}
		return instance;
	}

	@Override
	public void matrixMode(int matrixMode) {
		int modelViewProjectionMask =
				Constants.MODEL | Constants.VIEW | Constants.PROJECTION;
		// check if non allowed bits have been set
		if ((matrixMode & ~(modelViewProjectionMask)) != 0) {
			Log.e(LOG_TAG,
					"Non allowed bits for matrix mode ... ignoring new value!");
		} else if ((matrixMode & modelViewProjectionMask) == 0) {
			Log.e(LOG_TAG, "No bit for matrix mode set ... ignoring new value!");
		} else {
			this.matrixMode = matrixMode;
		}
	}

	@Override
	public int getMatrixMode() {
		return matrixMode;
	}

	@Override
	public void loadIndentity() {
		if ((matrixMode & Constants.MODEL) == Constants.MODEL) {
			Matrix.setIdentityM(modelMatrix, 0);
		}
		if ((matrixMode & Constants.VIEW) == Constants.VIEW) {
			Matrix.setIdentityM(viewMatrix, 0);
		}
		if ((matrixMode & Constants.PROJECTION) == Constants.PROJECTION) {
			Matrix.setIdentityM(projectionMatrix, 0);
		}
	}

	/**
	 * Pushes the matrix onto the stack depending on the matrix mode. The matrix
	 * remains untouched and we save a copy of the current matrix.
	 */
	@Override
	public void pushMatrix() {
		boolean pushedSomething = false;
		// create a copy of the matrix and push it onto the stack
		if ((matrixMode & Constants.MODEL) == Constants.MODEL) {
			float[] modelMatrixCopy =
					Arrays.copyOf(modelMatrix, modelMatrix.length);
			stackModelMatrix.push(modelMatrixCopy);
			// modelMatrix = new float[16];
			pushedSomething = true;
		}
		if ((matrixMode & Constants.VIEW) == Constants.VIEW) {
			float[] viewMatrixCopy = Arrays.copyOf(viewMatrix, viewMatrix.length);
			stackViewMatrix.push(viewMatrixCopy);
			pushedSomething = true;
		}
		if ((matrixMode & Constants.PROJECTION) == Constants.PROJECTION) {
			float[] projectionMatrixCopy =
					Arrays.copyOf(projectionMatrix, projectionMatrix.length);
			stackProjectionMatrix.push(projectionMatrixCopy);
			pushedSomething = true;
		}
		if (!pushedSomething) {
			Log.w(LOG_TAG, "Did not push something. Unknown matrix mode "
					+ matrixMode + ". Use eiter MODEL, VIEW and/or PROJECTION.");
		}
	}

	/**
	 * Only use in drawing thread because the shader variables will be set
	 */
	@Override
	public void popMatrix() {
		boolean poppedSomething = false;
		// create a copy of the matrix and push it onto the stack
		if ((matrixMode & Constants.MODEL) == Constants.MODEL) {
			modelMatrix = stackModelMatrix.pop();
			poppedSomething = true;
		}
		if ((matrixMode & Constants.VIEW) == Constants.VIEW) {
			viewMatrix = stackViewMatrix.pop();
			poppedSomething = true;
		}
		if ((matrixMode & Constants.PROJECTION) == Constants.PROJECTION) {
			projectionMatrix = stackProjectionMatrix.pop();
			poppedSomething = true;
		}
		if (!poppedSomething) {
			Log.w(LOG_TAG, "Did not pop something. Unknown matrix mode "
					+ matrixMode + ". Use eiter MODEL, VIEW and/or PROJECTION.");
		} /*
			 * else { Log.i(LOG_TAG,
			 * "Popped a matrix from stack refreshing shader mvpMatrix"); float[]
			 * mvpMatrix = getModelViewProjectionMatrix(); int mvpMatrixHandle =
			 * activeShader.getUniformLocation("mvpMatrix");
			 * GLES20.glUniformMatrix4fv(mvpMatrixHandle, mvpMatrix.length, false,
			 * mvpMatrix, 0); }
			 */
	}

	@Override
	public float[] getModelViewProjectionMatrix() {
		// write to new matrices so that the model-, view- and projection matrix
		// remain untouched
		float[] viewProjection = new float[16];
		Matrix.multiplyMM(viewProjection, 0, projectionMatrix, 0, viewMatrix, 0);
		float[] modelViewProjection = new float[16];
		Matrix.multiplyMM(modelViewProjection, 0, viewProjection, 0, modelMatrix,
				0);
		return modelViewProjection;
	}

	@Override
	public void translate(Vector3f vec) {
		if ((matrixMode & Constants.MODEL) == Constants.MODEL) {
			Matrix.translateM(modelMatrix, 0, vec.getX(), vec.getY(), vec.getZ());
		}
	}

	@Override
	public void rotate(float angle, Vector3f axis) {
		if ((matrixMode & Constants.MODEL) == Constants.MODEL) {
			Matrix.rotateM(modelMatrix, 0, angle, axis.getX(), axis.getY(),
					axis.getZ());
		}
	}

	@Override
	public void scale(float factor) {
		if ((matrixMode & Constants.MODEL) == Constants.MODEL) {
			Matrix.scaleM(modelMatrix, 0, factor, factor, factor);
		}
	}

	@Override
	public void scale(float facX, float facY, float facZ) {
		if ((matrixMode & Constants.MODEL) == Constants.MODEL) {
			Matrix.scaleM(modelMatrix, 0, facX, facY, facZ);
		}
	}

	@Override
	public void projection(float fovy, float aspect, float zNear, float zFar) {
		if ((matrixMode & Constants.PROJECTION) == Constants.PROJECTION) {
			Matrix.perspectiveM(projectionMatrix, 0, fovy, aspect, zNear, zFar);
		}
	}

	@Override
	public void lookAt(Vector3f eyePos, Vector3f target, Vector3f up) {
		if ((matrixMode & Constants.VIEW) == Constants.VIEW) {
			Matrix.setLookAtM(viewMatrix, 0, eyePos.getX(), eyePos.getY(),
					eyePos.getZ(), target.getX(), target.getY(), target.getZ(),
					up.getX(), up.getY(), up.getZ());
		}
	}

	@Override
	public void lookAt(Camera camera) {
		int lastMatrixMode = matrixMode;
		matrixMode(GraphicsContext.Constants.VIEW);
		lookAt(camera.getPosition(), camera.getTarget(), camera.getUp());
		// reset the matrix mode to the last value
		matrixMode(lastMatrixMode);
	}

	public int getDefaultShaderHandle() {
		return fixedFunctionShader.getProgramHandle();
	}

	@Override
	public void frustum(float left, float right, float bottom, float top,
			float near, float far) {
		if ((matrixMode & Constants.PROJECTION) == Constants.PROJECTION) {
			Matrix.frustumM(projectionMatrix, 0, left, right, bottom, top, near,
					far);
		}
	}

	public void setActiveShader(Shader shader) {
		if (shader != activeShader) {
			shader.startShader();
		}
	}

	public void useFixedFunctionShader() {
		fixedFunctionShader.startShader();
	}

	public Shader getFixedFunctionShader() {
		return fixedFunctionShader;
	}
}
