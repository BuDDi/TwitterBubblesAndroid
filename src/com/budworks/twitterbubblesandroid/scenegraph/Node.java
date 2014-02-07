package com.budworks.twitterbubblesandroid.scenegraph;

import java.util.ArrayList;
import java.util.List;

import com.budworks.twitterbubblesandroid.geom.Rotation;
import com.budworks.twitterbubblesandroid.geom.Vector3f;

public abstract class Node {

	// private UUID id;

	private Vector3f position;

	private Rotation rotation;

	protected List<Node> childs = new ArrayList<Node>();

	// TODO use components
	private List<NodeComponent> components = new ArrayList<NodeComponent>();

	/**
	 * Create a new <code>Node</code> at origin position.
	 */
	public Node() {
		this.position = new Vector3f();
		this.rotation = new Rotation();
	}

	public Node(Vector3f position) {
		this.position = position;
		this.rotation = new Rotation();
	}

	protected final void updateBase(double elapsedTime) {
		update(elapsedTime);
		// use a copy of childs to prevent ConcurrentModificationException
		Node[] childsCopy = new Node[childs.size()];
		childsCopy = childs.toArray(childsCopy);
		for (Node child : childsCopy) {
			child.updateBase(elapsedTime);
		}
	}

	protected final void setupBase(GraphicsContext context) {
		setup(context);
		// use a copy of childs to prevent ConcurrentModificationException
		Node[] childsCopy = new Node[childs.size()];
		childsCopy = childs.toArray(childsCopy);
		for (Node child : childsCopy) {
			child.setupBase(context);
		}
	}

	/**
	 * Base class implementation calls setup for every child.
	 * 
	 * @param gl
	 */
	public abstract void setup(GraphicsContext context);

	/**
	 * 
	 * @param elapsedTime
	 *           elapsed milliseconds since last update call
	 */
	public abstract void update(double elapsedMillis);

	protected final void drawBase(GraphicsContext context, double elapsedTime) {
		// push parent's matrix onto the stack
		context.pushMatrix();
		context.translate(position);
		context.rotate(rotation.xAngle, new Vector3f(1, 0, 0));
		context.rotate(rotation.yAngle, new Vector3f(0, 1, 0));
		context.rotate(rotation.zAngle, new Vector3f(0, 0, 1));
		draw(context, elapsedTime);
		// use a copy of childs to prevent ConcurrentModificationException
		Node[] childsCopy = new Node[childs.size()];
		childsCopy = childs.toArray(childsCopy);
		for (Node child : childsCopy) {
			child.drawBase(context, elapsedTime);
		}
		context.popMatrix();
	}

	// TODO encapsulate gl context and use own drawing context
	/**
	 * Transformations are handled by base class so:</br><b>DO NOT</b> use the
	 * <code>gl</code> context for transformation.
	 * 
	 * @param gl
	 *           drawing context
	 * @param elapsedTime
	 *           elapsed time in milliseconds since last draw call
	 */
	public abstract void draw(GraphicsContext context, double elapsedTime);

	public final void addChild(Node child) {
		childs.add(child);
	}

	public final void removeChild(Node child) {
		childs.remove(child);
	}

	public final void addComponent(NodeComponent comp) {
		components.add(comp);
	}

	public final void translate(Vector3f vec) {
		this.position.add(vec);
	}

	/**
	 * 
	 * @param angle
	 *           in degrees
	 */
	public final void rotateX(double angle) {
		rotation.xAngle += angle;
		if (angle > 0 && rotation.xAngle >= 360) {
			rotation.xAngle -= 360;
		} else if (angle < 0 && rotation.xAngle <= -360) {
			rotation.xAngle += 360;
		}
	}

	/**
	 * 
	 * @param angle
	 *           in degrees
	 */
	public final void rotateY(double angle) {
		rotation.yAngle += angle;
		if (angle > 0 && rotation.yAngle >= 360) {
			rotation.yAngle -= 360;
		} else if (angle < 0 && rotation.yAngle <= -360) {
			rotation.yAngle += 360;
		}
	}

	/**
	 * 
	 * @param angle
	 *           in degrees
	 */
	public final void rotateZ(double angle) {
		rotation.zAngle += angle;
		if (angle > 0 && rotation.zAngle >= 360) {
			rotation.zAngle -= 360;
		} else if (angle < 0 && rotation.zAngle <= -360) {
			rotation.zAngle += 360;
		}
	}

	public final Vector3f getPosition() {
		return position;
	}

}
