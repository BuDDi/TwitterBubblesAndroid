package com.budworks.twitterbubblesandroid.shapes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.opengl.GLES20;
import android.util.Log;

public class ShaderAndroidGLES20 implements Shader {

	private static final String LOG_TAG = ShaderAndroidGLES20.class.getName();

	/**
	 * the handle to this shader program
	 */
	int programHandle;

	int vertexShaderHandle;

	int fragmentShaderHandle;

	private int fixedFunctionShaderHandle;

	/**
	 * 
	 * @param fixedFunctionProgHandle
	 *           needed to reset to fixed function. if this shader is the fixed
	 *           function shader we can pass 0 here.
	 */
	public ShaderAndroidGLES20(int fixedFunctionShaderHandle) {
		this.fixedFunctionShaderHandle = fixedFunctionShaderHandle;
		programHandle = 0;
		vertexShaderHandle = 0;
		fragmentShaderHandle = 0;
	}

	@Override
	public void loadVertexShader(String file) {
		String shaderSource = readFile(file);
		// Load in the vertex shader.
		vertexShaderHandle = loadShader(GLES20.GL_VERTEX_SHADER, shaderSource);

		if (vertexShaderHandle == 0) {
			throw new RuntimeException("Error creating vertex shader.");
		}
	}

	/**
	 * For Android assets folder support
	 * 
	 * @param vertFileInputStream
	 * @return
	 */
	public ShaderAndroidGLES20 loadVertexShader(InputStream vertFileInputStream) {
		String shaderSource = readInputStream(vertFileInputStream);
		// Load in the vertex shader.
		vertexShaderHandle = loadShader(GLES20.GL_VERTEX_SHADER, shaderSource);

		if (vertexShaderHandle == 0) {
			throw new RuntimeException("Error creating vertex shader.");
		}
		return this;
	}

	@Override
	public void loadFragmentShader(String file) {
		String shaderSource = readFile(file);
		// Load in the fragment shader.
		fragmentShaderHandle =
				loadShader(GLES20.GL_FRAGMENT_SHADER, shaderSource);

		if (fragmentShaderHandle == 0) {
			throw new RuntimeException("Error creating fragment shader.");
		}
	}

	/**
	 * For Android assets folder support
	 * 
	 * @param fragFileInputStream
	 */
	public ShaderAndroidGLES20 loadFragmentShader(InputStream fragFileInputStream) {
		String shaderSource = readInputStream(fragFileInputStream);
		// Load in the vertex shader.
		fragmentShaderHandle =
				loadShader(GLES20.GL_FRAGMENT_SHADER, shaderSource);

		if (fragmentShaderHandle == 0) {
			throw new RuntimeException("Error creating fragment shader.");
		}
		return this;
	}

	private static int loadShader(int type, String shaderCode) {
		// force quit if we try to load something different than a vertex or
		// fragment shader
		if (type != GLES20.GL_VERTEX_SHADER && type != GLES20.GL_FRAGMENT_SHADER) {
			throw new RuntimeException("Unknown shader type.");
		}
		String shaderName =
				type == GLES20.GL_VERTEX_SHADER ? "vertex shader"
						: "fragment shader";
		// create a vertex shader type (GLES20.GL_VERTEX_SHADER)
		// or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
		int shaderHandle = GLES20.glCreateShader(type);
		if (shaderHandle == 0) {
			throw new RuntimeException("Error creating " + shaderName + ".");
		}
		// add the source code to the shader and compile it
		GLES20.glShaderSource(shaderHandle, shaderCode);
		GLES20.glCompileShader(shaderHandle);
		// Get the compilation status.
		final int[] compileStatus = new int[1];
		GLES20.glGetShaderiv(shaderHandle, GLES20.GL_COMPILE_STATUS,
				compileStatus, 0);
		// If the compilation failed, print error and delete the shader.
		if (compileStatus[0] == 0) {
			// get detailed error message
			String infoLog = GLES20.glGetShaderInfoLog(shaderHandle);
			Log.e(LOG_TAG, "Error compiling " + shaderName + ":\n" + infoLog);
			GLES20.glDeleteShader(shaderHandle);
			shaderHandle = 0;
		} else {
			// TODO check if that is correct
			// release the resources allocated by the shader compiler.
			GLES20.glReleaseShaderCompiler();
		}
		return shaderHandle;
	}

	@Override
	public int getAttribLocation(String name) {
		return GLES20.glGetAttribLocation(programHandle, name);
	}

	@Override
	public int getUniformLocation(String name) {
		return GLES20.glGetUniformLocation(programHandle, name);
	}

	// public void setUniformSampler2D(String varName, Texture texture) {
	// int varHandle = gl2.glGetUniformLocationARB(programObject, varName);
	//
	// gl2.glUniform1i(varHandle, 0); // Texture unit 0 is for base images.
	// gl2.glActiveTexture(GL2.GL_TEXTURE0);
	// texture.
	// // gl2.glBindTexture(GL2.GL_TEXTURE_2D, texture.);
	// }

	@Override
	public void useShaders() {
		// create empty OpenGL ES Program
		programHandle = GLES20.glCreateProgram();
		if (programHandle == 0) {
			throw new RuntimeException("Could not create GLES progam!");
		}
		if (vertexShaderHandle != 0) {
			// add the vertex shader to program
			GLES20.glAttachShader(programHandle, vertexShaderHandle);
		}
		if (fragmentShaderHandle != 0) {
			// add the fragment shader to program
			GLES20.glAttachShader(programHandle, fragmentShaderHandle);
		}
		// before we link the program we can bind the attribute locations to our
		// own indices or let them be generated automatically
		// GLES20.glBindAttribLocation(programHandle, 1, "vPosition");
		// GLES20.glBindAttribLocation(programHandle, 2, "vNormal");
		// creates OpenGL ES program executables
		GLES20.glLinkProgram(programHandle);
		// Get the link status.
		final int[] linkStatus = new int[1];
		GLES20.glGetProgramiv(programHandle, GLES20.GL_LINK_STATUS, linkStatus, 0);
		// If the linking failed, delete the program.
		if (linkStatus[0] == 0) {
			// get detailed error message
			String infoLog = GLES20.glGetProgramInfoLog(programHandle);
			Log.e(LOG_TAG, "Error linking program:\n" + infoLog);
			// detach all attached shader and delete them
			int maxCount = 2;
			int[] count = new int[1];
			int[] shaderHandles = new int[maxCount];
			GLES20.glGetAttachedShaders(programHandle, maxCount, count, 0,
					shaderHandles, 0);
			for (int i = 0; i < count[0]; i++) {
				int shader = shaderHandles[i];
				GLES20.glDetachShader(programHandle, shader);
				GLES20.glDeleteShader(shader);
			}
			// we could not link so delete the program
			GLES20.glDeleteProgram(programHandle);
			vertexShaderHandle = 0;
			fragmentShaderHandle = 0;
			programHandle = 0;
		} else {
			// Link status ok, now check if that program is valid
			GLES20.glValidateProgram(programHandle);
			int[] validateStatus = new int[1];
			GLES20.glGetProgramiv(programHandle, GLES20.GL_VALIDATE_STATUS,
					validateStatus, 0);
			if (validateStatus[0] == 0) {
				// get detailed error message
				String infoLog = GLES20.glGetProgramInfoLog(programHandle);
				Log.e(LOG_TAG, "Error validating program:\n" + infoLog);
				// detach all attached shader and delete them
				int maxCount = 2;
				int[] count = new int[1];
				int[] shaderHandles = new int[maxCount];
				GLES20.glGetAttachedShaders(programHandle, maxCount, count, 0,
						shaderHandles, 0);
				for (int i = 0; i < count[0]; i++) {
					int shader = shaderHandles[i];
					GLES20.glDetachShader(programHandle, shader);
					GLES20.glDeleteShader(shader);
				}
				// we could not validate so delete the program
				GLES20.glDeleteProgram(programHandle);
				vertexShaderHandle = 0;
				fragmentShaderHandle = 0;
				programHandle = 0;
			}
		}

	}

	@Override
	public void startShader() {
		GLES20.glUseProgram(programHandle);
	}

	@Override
	public void endShader() {
		if (fixedFunctionShaderHandle != 0) {
			GLES20.glUseProgram(fixedFunctionShaderHandle);
		}
	}

	public void uniform3f(int location, float v0, float v1, float v2) {
		GLES20.glUniform3f(location, v0, v1, v2);
	}

	public static String readFile(String filePath) {
		String text = "";
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(new File(filePath)));
			text = readBufferedReader(br);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return text;
	}

	public static String readBufferedReader(BufferedReader br) {
		String text = "";
		try {
			String line = null;
			while ((line = br.readLine()) != null) {
				text += line + "\n";
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
		return text;
	}

	public void setUniform1f(String varName, float value) {
		int varHandle = GLES20.glGetUniformLocation(programHandle, varName);
		GLES20.glUniform1f(varHandle, value);
	}

	@Override
	public int getProgramHandle() {
		return programHandle;
	}

	public static String readInputStream(InputStream vertFileInputStream) {
		// TODO Auto-generated method stub
		BufferedReader br =
				new BufferedReader(new InputStreamReader(vertFileInputStream));
		return readBufferedReader(br);
	}
}