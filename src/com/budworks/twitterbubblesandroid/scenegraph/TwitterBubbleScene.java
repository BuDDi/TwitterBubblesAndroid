package com.budworks.twitterbubblesandroid.scenegraph;

import android.opengl.GLES20;

import com.budworks.twitterbubblesandroid.shapes.Mesh;
import com.budworks.twitterbubblesandroid.shapes.Shader;
import com.budworks.twitterbubblesandroid.shapes.SphereAndroidGLES20;
import com.budworks.twitterbubblesandroid.shapes.Triangle;

public class TwitterBubbleScene extends Scene {

	private Mesh bubbleTest;

	private Triangle triangleTest;

	@Override
	public void setup(GraphicsContext context) {
		// TODO Auto-generated method stub
		Shader fixedFunctionShader =
				((ContextAndroidGLES20) context).getFixedFunctionShader();
		bubbleTest = new SphereAndroidGLES20(1, 12, 12, fixedFunctionShader);
		triangleTest = new Triangle();
		super.setup(context);
	}

	@Override
	public void draw(GraphicsContext context, double elapsedTime) {
		// set view matrix given by camera fields
		context.lookAt(getCamera());
		GLES20.glClearColor(1, 0, 0, 1);
		// bubbleTest.draw(context);
		triangleTest.draw(context.getModelViewProjectionMatrix());
	}

}
