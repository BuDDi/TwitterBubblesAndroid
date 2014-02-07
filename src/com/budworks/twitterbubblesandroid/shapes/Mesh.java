package com.budworks.twitterbubblesandroid.shapes;

import com.budworks.twitterbubblesandroid.scenegraph.GraphicsContext;

public interface Mesh {

	/**
	 * Whenever OpenGL is ready call this method for instantiation of resources
	 * and so on. This method can also be called when the context changed.
	 */
	void setup(GraphicsContext context);

	/**
	 * Only call this method from OpenGL-Thread
	 */
	void draw(GraphicsContext context);

}
