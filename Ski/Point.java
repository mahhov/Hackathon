class Point {
	double x, y, z;

	Point(double[] c, double scale, double zScale) {
		this(c[0], c[1], c[2], zScale);
	}

	Point(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	Point(double x, double y, double z, double scale) {
		this.x = x * scale;
		this.y = y * scale;
		this.z = z;
	}

	double distanceFrom(Point o) {
		return distanceFrom(o.x, o.y);
	}

	double distanceFrom(double x2, double y2) {
		double dx = x2 - this.x;
		double dy = y2 - this.y;
		return Math.sqrt(dx * dx + dy * dy);
	}

	double angleWith(double x2, double y2) {
		return Math.atan2(y2 - y, x2 - x);
	}

	Point origin(double[] camera) {
		double ox = x - camera[0];
		double oy = y - camera[1];
		double oz = z - camera[2];
		double oa = camera[3];
		double d = Math.sqrt(ox * ox + oy * oy);
		oa = Math.atan2(oy, ox) - oa;
		oy = d * Math.sin(oa);
		ox = d * Math.cos(oa);
		// double ox2 = ox * Math.cos(oa) - oy * Math.sin(oa);
		// double oy2 = ox * Math.sin(oa) - oy * Math.cos(oa);
		// ox = ox2;
		// oy = oy2;
		return new Point(ox, oy, oz);
	}

	double[] toScreen() {
		if (x == 0)
			return new double[] { 0, 0 };
		return new double[] { 400.0 + 200.0 * y / x, 400.0 + 200.0 * z / x };
	}

	Point[] shift(double angle, double width) {
		double dx = width * Math.cos(angle + Math.PI / 2);
		double dy = width * Math.sin(angle + Math.PI / 2);
		return new Point[] { new Point(x + dx, y + dy, z),
				new Point(x - dx, y - dy, z) };
	}

}