package com.budworks.twitterbubblesandroid.geom;

/**
 * Mutable three component float vector
 * 
 * @author Steffen Buder
 * 
 */
public class Vector3f {

	/**
	 * use a length of 4 to enable homogenous
	 */
	private float[] vec = new float[4];

	public Vector3f() {
		super();
		vec[0] = 0.0f;
		vec[1] = 0.0f;
		vec[2] = 0.0f;
		vec[3] = 1.0f;
	}

	public Vector3f(float x, float y, float z) {
		super();
		vec[0] = x;
		vec[1] = y;
		vec[2] = z;
		vec[3] = 1;
	}

	public float getX() {
		return vec[0];
	}

	public void setX(float x) {
		vec[0] = x;
	}

	public float getY() {
		return vec[1];
	}

	public void setY(float y) {
		vec[1] = y;
	}

	public float getZ() {
		return vec[2];
	}

	public void setZ(float z) {
		vec[2] = z;
	}

	/**
	 * Adds the <code>other</code> vector to this vector.
	 * 
	 * @param other
	 *           vector to add
	 */
	public void add(Vector3f other) {
		vec[0] += other.vec[0];
		vec[1] += other.vec[1];
		vec[2] += other.vec[2];
	}

	/**
	 * Subtract the <code>other</code> vector from this vector. Not
	 * 
	 * @param other
	 *           vector to subtract
	 */
	public void subtract(Vector3f other) {
		vec[0] -= other.vec[0];
		vec[1] -= other.vec[1];
		vec[2] -= other.vec[2];
	}

	public void scale(float length) {
		vec[0] *= length;
		vec[1] *= length;
		vec[2] *= length;
	}

	/**
	 * Multiply <code>this</code> vector with any transformation matrix.
	 * 
	 * @param transform
	 */
	public void mult(Matrix4f transform) {
		// do not check here because we know that the matrix is 4x4
		// copy old values into new array
		float[] result = new float[transform.lengthRow()];
		for (int i = 0; i < result.length; i++) {
			result[i] = Vector3f.dot(transform.getRow(i), vec);
		}
		vec = result;
	}

	/**
	 * @param other
	 * @return the dot product of <code>this</code> and <code>other</code>
	 */
	public float dot(Vector3f other) {
		return (vec[0] * other.vec[0]) + (vec[1] * other.vec[1])
				+ (vec[2] * other.vec[2]);
	}

	/**
	 * 
	 * @param other
	 * @return a <code>new Vector3D</code> that is the cross product from
	 *         <code>this</code> vector and <code>other</code> vector
	 */
	public Vector3f cross(Vector3f other) {
		float xCross = (vec[1] * other.vec[2]) - (vec[2] * other.vec[1]);
		float yCross = (vec[2] * other.vec[0]) - (vec[0] * other.vec[2]);
		float zCross = (vec[0] * other.vec[1]) - (vec[1] * other.vec[0]);
		return new Vector3f(xCross, yCross, zCross);
	}

	/**
	 * 
	 * @return the length of this vector
	 */
	public float length() {
		return (float) Math.sqrt(Math.pow(vec[0], 2) + Math.pow(vec[1], 2)
				+ Math.pow(vec[2], 2));
	}

	/**
	 * 
	 * @return the distance from <code>this</code> vector to <code>other</code>
	 *         vector.
	 */
	public float distance(Vector3f other) {
		// get the direction Vector of this and other and return its length
		return subtract(this, other).length();
	}

	/**
	 * Normalize <code>this</code> vector
	 */
	public void normalize() {
		float length = length();
		vec[0] /= length;
		vec[1] /= length;
		vec[2] /= length;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "(" + vec[0] + ", " + vec[1] + ", " + vec[2] + ")";
	}

	// =========================================================================
	// Static Part -> Methods that return a new Vector3D
	// =========================================================================

	/**
	 * 
	 * @param a
	 * @param b
	 * @return a <code>new Vector3D</code> that is the sum of <code>a</code> and
	 *         <code>b</code>
	 */
	public static Vector3f add(Vector3f a, Vector3f b) {
		return new Vector3f(a.vec[0] + b.vec[0], a.vec[1] + b.vec[1], a.vec[2]
				+ b.vec[2]);
	}

	/**
	 * 
	 * @param a
	 * @param b
	 * @return a <code>new Vector3D</code> that is the difference of
	 *         <code>a</code> and <code>b</code>
	 */
	public static Vector3f subtract(Vector3f a, Vector3f b) {
		return new Vector3f(a.vec[0] - b.vec[0], a.vec[1] - b.vec[1], a.vec[2]
				- b.vec[2]);
	}

	/**
	 * 
	 * @param other
	 * @return a <code>new Vector3D</code> that is the cross product from
	 *         <code>this</code> vector and <code>other</code> vector
	 */
	public static Vector3f cross(Vector3f a, Vector3f b) {
		float xCross = (a.vec[1] * b.vec[2]) - (a.vec[2] * b.vec[1]);
		float yCross = (a.vec[2] * b.vec[0]) - (a.vec[0] * b.vec[2]);
		float zCross = (a.vec[0] * b.vec[1]) - (a.vec[1] * b.vec[0]);
		return new Vector3f(xCross, yCross, zCross);
	}

	/**
	 * 
	 * @param a
	 * @return a <code>new Vector3D</code> that is the normalized one of
	 *         <code>a</code>
	 */
	public static Vector3f normalized(Vector3f a) {
		float lengthA = a.length();
		return new Vector3f(a.vec[0] / lengthA, a.vec[1] / lengthA, a.vec[2]
				/ lengthA);
	}

	public static Vector3f scaled(Vector3f vec, float perSec) {
		return new Vector3f(vec.getX() * perSec, vec.getY() * perSec, vec.getZ()
				* perSec);
	}

	public static float dot(float[] lhs, float[] rhs) {
		if (lhs.length != rhs.length) {
			throw new IllegalArgumentException(
					"Length of lhs must math length of rhs: " + lhs.length + " != "
							+ rhs.length);
		}
		float result = 0.0f;
		for (int i = 0; i < lhs.length; i++) {
			result += lhs[i] * rhs[i];
		}
		return result;
	}

}
