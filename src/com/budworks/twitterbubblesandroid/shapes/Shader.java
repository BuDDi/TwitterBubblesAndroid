package com.budworks.twitterbubblesandroid.shapes;

/**
 * Simple shader interface which can load a vertex- and a fragment shader.
 * 
 * @author S. Buder
 * 
 */
public interface Shader {

	/**
	 * Load the vertex shader.
	 * 
	 * @param file
	 */
	void loadVertexShader(String file);

	/**
	 * Load the fragment shader.
	 * 
	 * @param file
	 */
	void loadFragmentShader(String file);

	/**
	 * Creates the shader program with the already attached shaders.
	 */
	void useShaders();

	/**
	 * Uses the shader program.
	 */
	void startShader();

	/**
	 * Resets to fixed function shader.
	 */
	void endShader();

	/**
	 * Returns the handle of an attribute variable of that program.
	 * 
	 * @param name
	 *           name of the attribute variable to look for.
	 * @return handle to the attribute variable.
	 */
	int getAttribLocation(String name);

	/**
	 * Returns the handle of an attribute variable of that program.
	 * 
	 * @param name
	 *           name name of the attribute variable to look for.
	 * @return handle to the uniform variable.
	 */
	int getUniformLocation(String name);

	/**
	 * 
	 * @return the handle to this shader program
	 */
	int getProgramHandle();

}
