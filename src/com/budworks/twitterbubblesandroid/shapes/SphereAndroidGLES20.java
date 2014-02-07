package com.budworks.twitterbubblesandroid.shapes;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import android.opengl.GLES20;
import android.util.Log;

import com.budworks.twitterbubblesandroid.scenegraph.GraphicsContext;

public class SphereAndroidGLES20 implements Mesh {

	private static final String LOG_TAG = null;

	private Shader shader;

	private FloatBuffer vertexBuffer;

	private FloatBuffer normalBuffer;

	private ShortBuffer indexBuffer;

	private FloatBuffer textureCoordBuffer;

	static final int COORDS_PER_VERTEX = 3;

	static float triangleCoords[] = {
			// in counterclockwise order:
			0.0f, 0.622008459f, 0.0f, // top
			-0.5f, -0.311004243f, 0.0f, // bottom left
			0.5f, -0.311004243f, 0.0f // bottom right
			};

	private int vertexCount;

	private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

	float color[] = { 0.63671875f, 0.76953125f, 0.22265625f, 0.0f };

	// private int vertexCount;

	private final String vertexShaderCode =
	// This matrix member variable provides a hook to manipulate
	// the coordinates of the objects that use this vertex shader
			"uniform mat4 mvpMatrix;" + "attribute vec4 vPosition;"
					+ "void main() {" +
					// the matrix must be included as a modifier of gl_Position
					// Note that the uMVPMatrix factor *must be first* in order
					// for the matrix multiplication product to be correct.
					"  gl_Position = uMVPMatrix * vPosition;" + "}";

	private final String fragmentShaderCode = "precision mediump float;"
			+ "uniform vec4 vColor;" + "void main() {"
			+ "  gl_FragColor = vColor;" + "}";

	private int mProgram;

	private int mPositionHandle;

	private int mColorHandle;

	private int mMVPMatrixHandle;

	public SphereAndroidGLES20(float radius, int stacks, int slices,
			Shader shader) {
		super();
		this.shader = shader;
		// this.shader = 0;
		// int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER,
		// vertexShaderCode);
		// int fragmentShader =
		// loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);
		//
		// mProgram = GLES20.glCreateProgram(); // create empty OpenGL ES Program
		// GLES20.glAttachShader(mProgram, vertexShader); // add the vertex shader
		// to
		// // program
		// GLES20.glAttachShader(mProgram, fragmentShader); // add the fragment
		// // shader to program
		// GLES20.glLinkProgram(mProgram); // creates OpenGL ES program
		// executables
		// initialize buffers
		init(radius, stacks, slices);
		// initialize vertex byte buffer for shape coordinates
		// ByteBuffer bb = ByteBuffer.allocateDirect(
		// // (number of coordinate values * 4 bytes per float)
		// triangleCoords.length * 4);
		// // use the device hardware's native byte order
		// bb.order(ByteOrder.nativeOrder());
		//
		// // create a floating point buffer from the ByteBuffer
		// vertexBuffer = bb.asFloatBuffer();
		// // add the coordinates to the FloatBuffer
		// vertexBuffer.put(triangleCoords);
		// // set the buffer to read the first coordinate
		// vertexBuffer.position(0);
		//
		// // prepare shaders and OpenGL program
		// int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER,
		// vertexShaderCode);
		// int fragmentShader =
		// loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);
		//
		// mProgram = GLES20.glCreateProgram(); // create empty OpenGL Program
		// GLES20.glAttachShader(mProgram, vertexShader); // add the vertex shader
		// to
		// // program
		// GLES20.glAttachShader(mProgram, fragmentShader); // add the fragment
		// // shader to program
		// GLES20.glLinkProgram(mProgram); // create OpenGL program executables

	}

	public static int loadShader(int type, String shaderCode) {

		// create a vertex shader type (GLES20.GL_VERTEX_SHADER)
		// or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
		int shader = GLES20.glCreateShader(type);

		// add the source code to the shader and compile it
		GLES20.glShaderSource(shader, shaderCode);
		GLES20.glCompileShader(shader);

		return shader;
	}

	private void init(float radius, int stacks, int slices) {
		vertexCount = (stacks + 1) * (slices + 1);
		FloatBuffer vertexBuffer =
				ByteBuffer.allocateDirect(vertexCount * GLHelpers.BYTES_PER_VERTEX)
						.order(ByteOrder.nativeOrder()).asFloatBuffer();
		FloatBuffer normalBuffer =
				ByteBuffer.allocateDirect(vertexCount * GLHelpers.BYTES_PER_VERTEX)
						.order(ByteOrder.nativeOrder()).asFloatBuffer();
		FloatBuffer textureCoordBuffer =
				ByteBuffer
						.allocateDirect(
								vertexCount * GLHelpers.BYTES_PER_TEXTURE_COORD)
						.order(ByteOrder.nativeOrder()).asFloatBuffer();
		ShortBuffer indexBuffer =
				ByteBuffer
						.allocateDirect(
								vertexCount * GLHelpers.BYTES_PER_TRIANGLE_INDEX)
						.order(ByteOrder.nativeOrder()).asShortBuffer();

		for (int stackNumber = 0; stackNumber <= stacks; ++stackNumber) {
			for (int sliceNumber = 0; sliceNumber <= slices; ++sliceNumber) {
				float theta = (float) (stackNumber * Math.PI / stacks);
				float phi = (float) (sliceNumber * 2 * Math.PI / slices);
				float sinTheta = (float) Math.sin(theta);
				float sinPhi = (float) Math.sin(phi);
				float cosTheta = (float) Math.cos(theta);
				float cosPhi = (float) Math.cos(phi);

				float nx = cosPhi * sinTheta;
				float ny = cosTheta;
				float nz = sinPhi * sinTheta;

				float x = radius * nx;
				float y = radius * ny;
				float z = radius * nz;

				float u = 1.f - (sliceNumber / (float) slices);
				float v = stackNumber / (float) stacks;

				normalBuffer.put(nx);
				normalBuffer.put(ny);
				normalBuffer.put(nz);

				vertexBuffer.put(x);
				vertexBuffer.put(y);
				vertexBuffer.put(z);

				textureCoordBuffer.put(u);
				textureCoordBuffer.put(v);
			}
		}

		for (int stackNumber = 0; stackNumber < stacks; ++stackNumber) {
			for (int sliceNumber = 0; sliceNumber < slices; ++sliceNumber) {
				int second = (sliceNumber * (stacks + 1)) + stackNumber;
				int first = second + stacks + 1;

				// int first = (stackNumber * slices) + (sliceNumber % slices);
				// int second = ((stackNumber + 1) * slices) + (sliceNumber %
				// slices);

				indexBuffer.put((short) first);
				indexBuffer.put((short) second);
				indexBuffer.put((short) (first + 1));

				indexBuffer.put((short) second);
				indexBuffer.put((short) (second + 1));
				indexBuffer.put((short) (first + 1));
			}
		}

		vertexBuffer.rewind();
		normalBuffer.rewind();
		indexBuffer.rewind();
		textureCoordBuffer.rewind();

		this.vertexBuffer = vertexBuffer;
		this.normalBuffer = normalBuffer;
		this.indexBuffer = indexBuffer;
		this.textureCoordBuffer = textureCoordBuffer;
	}

	@Override
	public void setup(GraphicsContext context) {
		// when setup is called we initialize the shader program
		// if (shader == null) {
		// int fixedFuncShaderHandle =
		// ((ContextAndroidGLES20) context).getDefaultShaderHandle();
		// shader = new ShaderAndroidGLES20(fixedFuncShaderHandle);
		// shader.loadVertexShader("shader/BubbleGLES2.vert");
		// shader.loadFragmentShader("shader/BubbleGLES2.frag");
		// shader.useShaders();
		// }
	}

	@Override
	public void draw(GraphicsContext context) {
		// // Add program to OpenGL environment
		// GLES20.glUseProgram(mProgram);
		//
		// // get handle to vertex shader's vPosition member
		// mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
		//
		// // Enable a handle to the triangle vertices
		// GLES20.glEnableVertexAttribArray(mPositionHandle);
		//
		// // Prepare the triangle coordinate data
		// GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
		// GLES20.GL_FLOAT, false, vertexStride, vertexBuffer);
		//
		// // get handle to fragment shader's vColor member
		// mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
		//
		// // Set color for drawing the triangle
		// GLES20.glUniform4fv(mColorHandle, 1, color, 0);
		//
		// // get handle to shape's transformation matrix
		// mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "mvpMatrix");
		// GLHelpers.checkGlError("glGetUniformLocation");
		//
		// // Apply the projection and view transformation
		// GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false,
		// context.getModelViewProjectionMatrix(), 0);
		// GLHelpers.checkGlError("glUniformMatrix4fv");
		//
		// // Draw the triangle
		// GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);
		//
		// // Disable vertex array
		// GLES20.glDisableVertexAttribArray(mPositionHandle);

		// TODO hand over buffer information to shader
		// start the shader if available
		if (shader != null /* && GLES20.glIsProgram(mProgram) */) {
			shader.startShader();
			// shader.
			// calculate model-, view-, projection matrix
			float[] mvpMatrix = context.getModelViewProjectionMatrix();
			// get handle to vertex shader's vPosition member
			int positionHandle = shader.getAttribLocation("vPosition");
			Log.i(LOG_TAG,
					"programinfo after getting vPosition: "
							+ GLES20.glGetProgramInfoLog(shader.getProgramHandle()));
			GLHelpers.checkGlError("get attribute vPosition");

			// Enable a handle to the triangle vertices
			GLES20.glEnableVertexAttribArray(positionHandle);

			// Prepare the triangle coordinate data
			GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT,
					false, GLHelpers.BYTES_PER_VERTEX, vertexBuffer);
			GLHelpers.checkGlError("set vertex attribute pointer");

			int matrixHandle = shader.getUniformLocation("mvpMatrix");
			GLES20.glUniformMatrix4fv(matrixHandle, 1, false, mvpMatrix, 0);
			GLHelpers.checkGlError("Get and set uniform 'mvpMatrix'");

			// get handle to fragment shader's vColor member
			// int colorHandle = shader.getUniformLocation("vColor");

			// Set color for drawing the triangle
			// currently just pure red
			// float[] color = new float[] { 1, 0, 0, 1 };
			// GLES20.glUniform4fv(colorHandle, 1, color, 0);

			// Draw the triangle
			// GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);
			/*
			 * When using glDrawArrays rendering works but the results are not
			 * correct, when using glDrawElements I get an GL_INVALID_OPERATION
			 * error.
			 */
			GLES20.glDrawElements(GLES20.GL_TRIANGLE_STRIP, vertexCount,
					GLES20.GL_UNSIGNED_SHORT, indexBuffer);
			// GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, vertexCount);

			GLHelpers.checkGlError("draw");

			// Disable vertex array
			GLES20.glDisableVertexAttribArray(positionHandle);
			// GLES20.glDisableVertexAttribArray(colorHandle);
			shader.endShader();
		} else {
			Log.e(LOG_TAG, "Nothing to draw because the shader is null");
		}

	}
}
