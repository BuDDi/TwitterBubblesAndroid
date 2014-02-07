package com.budworks.twitterbubblesandroid.geom;

public class Rotation {

	// in degrees
	public float xAngle, yAngle, zAngle;

	@Override
	public String toString() {
		return xAngle + "," + yAngle + "," + zAngle;
	}

	public static Rotation fromString(String description) {
		String[] angles = description.split(",");
		if (angles.length != 3) {
			return null;
		}
		Rotation rot = new Rotation();
		rot.xAngle = Float.parseFloat(angles[0]);
		rot.yAngle = Float.parseFloat(angles[1]);
		rot.zAngle = Float.parseFloat(angles[2]);
		return rot;
	}
}
