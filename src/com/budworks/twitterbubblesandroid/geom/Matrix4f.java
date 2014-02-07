package com.budworks.twitterbubblesandroid.geom;

/**
 * Mutable implementation of a 4x4 matrix.
 * 
 * @author S. Buder
 * 
 */
public class Matrix4f {

	private float[][] mat = new float[4][4];

	/**
	 * Initializes an identity matrix
	 */
	public Matrix4f() {
		super();
		mat[0][0] = 1.0f;
		mat[1][1] = 1.0f;
		mat[2][2] = 1.0f;
		mat[3][3] = 1.0f;
	}

	public Matrix4f(Matrix4f other) {
		for (int row = 0; row < mat.length; row++) {
			for (int col = 0; col < mat[0].length; col++) {
				mat[row][col] = other.mat[row][col];
			}
		}
	}

	/**
	 * Multiplies other matrix with this matrix.
	 * 
	 * @param other
	 */
	public void multiply(Matrix4f other) {
		// square 4x4 matrices so no need to check the lengths here
		float[][] result = new float[lengthRow()][lengthCol()];
		for (int row = 0; row < lengthRow(); row++) {
			float[] thisRow = mat[row];
			// the length of thisRow and otherCol should match
			for (int col = 0; col < lengthCol(); col++) {
				float[] otherCol = other.getColumn(col);
				// multiply the row of this with the column of other
				result[row][col] = Vector3f.dot(thisRow, otherCol);
			}
		}
		mat = result;
	}

	/**
	 * Sets <code>vec</code> as the translation part of this matrix.
	 * 
	 * @param vec
	 */
	private void setTranslation(Vector3f vec) {
		mat[0][3] = vec.getX();
		mat[1][3] = vec.getX();
		mat[2][3] = vec.getX();
	}

	/**
	 * Sets the scaling part of this matrix to <code>factor</code>.
	 * 
	 * @param vec
	 */
	private void setScale(float factor) {
		mat[0][0] = factor;
		mat[1][1] = factor;
		mat[2][2] = factor;
	}

	/**
	 * Returns a copy array of all values at column.
	 * 
	 * @param column
	 *           to look for values
	 * @return a copy array at specified column
	 * @throws ArrayIndexOutOfBoundsException
	 *            if the specified column is less than 0 or more than
	 *            {@link Matrix4f#lengthCol()}
	 */
	public float[] getColumn(int column) {
		if (column < 0 || column >= lengthCol()) {
			throw new ArrayIndexOutOfBoundsException(column);
		}
		float[] result = new float[lengthRow()];
		for (int row = 0; row < lengthRow(); row++) {
			result[row] = mat[row][column];
		}
		return result;
	}

	/**
	 * Returns a copy array of all values at row.
	 * 
	 * @param row
	 *           to look for values
	 * @return a copy array at specified column
	 * @throws ArrayIndexOutOfBoundsException
	 *            if the specified row is less than 0 or more than
	 *            {@link Matrix4f#lengthRow()}
	 */
	public float[] getRow(int row) {
		if (row < 0 || row >= lengthRow()) {
			throw new ArrayIndexOutOfBoundsException(row);
		}
		float[] result = new float[lengthCol()];
		for (int col = 0; col < lengthCol(); col++) {
			result[col] = mat[row][col];
		}
		return result;
	}

	/**
	 * Creates a copy of <code>this</code> and multiplies it with
	 * <code>other</code>.
	 * 
	 * @param other
	 * @return result of the multiplication of <code>this</code> and
	 *         <code>other</other> without modifying <code>this</code>.
	 */
	public Matrix4f multiplied(Matrix4f other) {
		Matrix4f result = new Matrix4f(this);
		result.multiply(other);
		return result;
	}

	/**
	 * 
	 * @param row
	 * @param col
	 * @return
	 */
	public float get(int row, int col) {
		// take an else if to fire a detailed exception
		if (row < 0 || row >= lengthRow()) {
			throw new ArrayIndexOutOfBoundsException("row index out of bounds: "
					+ row);
		} else if (col < 0 || col >= lengthCol()) {
			throw new ArrayIndexOutOfBoundsException(
					"column index out of bounds: " + col);
		}
		return mat[row][col];
	}

	public int lengthRow() {
		return mat.length;
	}

	public int lengthCol() {
		return mat[0].length;
	}

	@Override
	public String toString() {
		String delimiter = ", ";
		StringBuilder sb = new StringBuilder();
		for (int row = 0; row < mat.length; row++) {
			sb.append("[");
			for (int col = 0; col < mat[0].length; col++) {
				sb.append(mat[row][col]);
				sb.append(delimiter);
			}
			// remove the last delimiter
			sb.replace(sb.length() - delimiter.length(), sb.length() - 1, "");
			sb.append("]\n"); // line break row
		}
		// remove last line break
		sb.replace(sb.length() - 1, sb.length() - 1, "");
		return sb.toString();
	}

	// =========================================================================
	// Static Part -> Methods that return a new Matrix4f
	// =========================================================================

	// TODO this maybe works not correct, so check it
	/**
	 * 
	 * @param axis
	 * @param angle
	 *           in degrees
	 * @return
	 */
	public static Matrix4f getRotation(Vector3f axis, float angle) {
		Matrix4f matrix = new Matrix4f();
		Vector3f axisNormed = Vector3f.normalized(axis);
		float angleRad = (float) (angle * (Math.PI / 180));
		float sinAngle = (float) Math.sin(angleRad);
		float cosAngle = (float) Math.cos(angleRad);
		// row 0
		matrix.mat[0][0] =
				(float) (cosAngle + (Math.pow(axisNormed.getX(), 2) * (1 - cosAngle)));
		matrix.mat[0][1] =
				(axisNormed.getX() * axisNormed.getY() * (1 - cosAngle))
						- (axisNormed.getZ() * sinAngle);
		matrix.mat[0][2] =
				(axisNormed.getX() * axisNormed.getZ() * (1 - cosAngle))
						+ (axisNormed.getY() * sinAngle);
		// row 1
		matrix.mat[1][0] =
				(axisNormed.getY() * axisNormed.getX() * (1 - cosAngle))
						+ (axisNormed.getZ() * sinAngle);
		matrix.mat[1][1] =
				(float) (cosAngle + (Math.pow(axisNormed.getY(), 2) * (1 - cosAngle)));
		matrix.mat[1][2] =
				(axisNormed.getY() * axisNormed.getZ() * (1 - cosAngle))
						- (axisNormed.getX() * sinAngle);
		// row 2
		matrix.mat[2][0] =
				(axisNormed.getZ() * axisNormed.getX() * (1 - cosAngle))
						- (axisNormed.getY() * sinAngle);
		matrix.mat[2][1] =
				(axisNormed.getZ() * axisNormed.getY() * (1 - cosAngle))
						+ (axisNormed.getX() * sinAngle);
		matrix.mat[2][2] =
				(float) (cosAngle + (Math.pow(axisNormed.getZ(), 2) * (1 - cosAngle)));
		return matrix;
	}

	/**
	 * 
	 * @param angle
	 *           in degrees
	 * @return
	 */
	public static Matrix4f getRotationX(float angle) {
		float angleRad = (float) (angle * (Math.PI / 180));
		float sinAngle = (float) Math.sin(angleRad);
		float cosAngle = (float) Math.cos(angleRad);
		Matrix4f matrix = new Matrix4f();
		matrix.mat[1][1] = cosAngle;
		matrix.mat[1][2] = -sinAngle;
		matrix.mat[2][1] = sinAngle;
		matrix.mat[2][2] = cosAngle;
		return matrix;
	}

	/**
	 * 
	 * @param angle
	 *           in degrees
	 * @return
	 */
	public static Matrix4f getRotationY(float angle) {
		float angleRad = (float) (angle * (Math.PI / 180));
		float sinAngle = (float) Math.sin(angleRad);
		float cosAngle = (float) Math.cos(angleRad);
		Matrix4f matrix = new Matrix4f();
		matrix.mat[0][0] = cosAngle;
		matrix.mat[0][2] = sinAngle;
		matrix.mat[2][0] = -sinAngle;
		matrix.mat[2][2] = cosAngle;
		return matrix;
	}

	/**
	 * 
	 * @param angle
	 *           in degrees
	 * @return
	 */
	public static Matrix4f getRotationZ(float angle) {
		float angleRad = (float) (angle * (Math.PI / 180));
		float sinAngle = (float) Math.sin(angleRad);
		float cosAngle = (float) Math.cos(angleRad);
		Matrix4f matrix = new Matrix4f();
		matrix.mat[0][0] = cosAngle;
		matrix.mat[0][1] = -sinAngle;
		matrix.mat[1][0] = sinAngle;
		matrix.mat[1][1] = cosAngle;
		return matrix;
	}

	public static Matrix4f getTranslationMatrix(Vector3f vec) {
		Matrix4f result = new Matrix4f();
		result.setTranslation(vec);
		return result;
	}

	public static Matrix4f getScalingMatrix(float factor) {
		Matrix4f result = new Matrix4f();
		result.setScale(factor);
		return result;
	}

	public void translate(Vector3f vec) {
		mat[0][3] += vec.getX();
		mat[1][3] += vec.getY();
		mat[2][3] += vec.getZ();
	}
}
