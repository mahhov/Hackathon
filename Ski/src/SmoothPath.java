import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;

public class SmoothPath {
	Line line;
	double width;

	SmoothPath(Path p) {
	}

	double getZ(double x, double y) {
		double p = line.progress(x, y);
		return p * line.deltaZ + line.i.z;
	}

	double angle(double angle) {
		return (line.angleZ * Math.cos(angle - line.angle));
	}

	class Line {
		Point i, f;
		double angle, angleZ;
		double distanceFlat;
		double deltaZ;

		// double deltaX, deltaY, deltaZ;
		// double slope;

		Line(double[][] c) {
			i = new Point(c[0], 2, 1.2);
			f = new Point(c[1], 2, 1.2);
			double deltaX = f.x - i.x;
			double deltaY = f.y - i.y;
			deltaZ = f.z - i.z;
			distanceFlat = i.distanceFrom(f);
			angle = Math.atan2(deltaY, deltaX);
			angleZ = Math.atan2(deltaZ,
					Math.sqrt(deltaX * deltaX + deltaY * deltaY));
			// slope = deltaZ / i.distanceFrom(f);
		}

		Line(Point ii, Point ff) {
			i = ii;
			f = ff;
			double deltaX = f.x - i.x;
			double deltaY = f.y - i.y;
			deltaZ = f.z - i.z;
			distanceFlat = i.distanceFrom(f);
			angle = Math.atan2(deltaY, deltaX);
			angleZ = Math.atan2(deltaZ,
					Math.sqrt(deltaX * deltaX + deltaY * deltaY));
		}

		double progress(double x, double y) {
			double distanceFromI = i.distanceFrom(x, y);
			double angle = i.angleWith(x, y) - line.angle;
			return Math.cos(angle) * distanceFromI / distanceFlat;
		}

		Line origin(double[] camera) {
			Point ii = i.origin(camera);
			Point ff = f.origin(camera);
			return new Line(ii, ff);
		}

		double[][] toScreen(double[] camera) {
			Point[] is = i.shift(angle, width);
			Point[] fs = f.shift(angle, width);

			Line left = new Line(is[0], fs[0]).origin(camera);
			Line right = new Line(is[1], fs[1]).origin(camera);

			Point p1 = left.i;
			Point p2 = left.f;
			double[] pp1 = null, pp2 = null;
			if (p2.x > 0 || p1.x > 0) {
				if (p1.x <= 0) {
					double progress = (.1 - p1.x) / (p2.x - p1.x);
					p1 = new Point(p1.x + progress * (p2.x - p1.x), p1.y
							+ progress * (p2.y - p1.y), p1.z + progress
							* (p2.z - p1.z));
				} else if (p2.x < 0) {
					Point t = p2;
					p2 = p1;
					p1 = p2;
					double progress = (.1 - p1.x) / (p2.x - p1.x);
					p1 = new Point(p1.x + progress * (p2.x - p1.x), p1.y
							+ progress * (p2.y - p1.y), p1.z + progress
							* (p2.z - p1.z));
					t = p2;
					p2 = p1;
					p1 = p2;
				}
				pp1 = p1.toScreen();
				pp2 = p2.toScreen();
			}

			Point p3 = right.i;
			Point p4 = right.f;
			double[] pp3 = null, pp4 = null;
			if (p4.x > 0 || p3.x > 0) {
				if (p3.x <= 0) {
					double progress = (.1 - p3.x) / (p4.x - p3.x);
					p3 = new Point(p3.x + progress * (p4.x - p3.x), p3.y
							+ progress * (p4.y - p3.y), p3.z + progress
							* (p4.z - p3.z));
					pp3 = p3.toScreen();
					pp4 = p4.toScreen();
				} else if (p4.x <= 0) {
					Point t = p4;
					p4 = p3;
					p3 = p4;
					double progress = (.1 - p3.x) / (p4.x - p3.x);
					p3 = new Point(p3.x + progress * (p4.x - p3.x), p3.y
							+ progress * (p4.y - p3.y), p3.z + progress
							* (p4.z - p3.z));
					pp3 = p3.toScreen();
					pp4 = p4.toScreen();
					t = p4;
					p4 = p3;
					p3 = p4;
				}
				pp3 = p3.toScreen();
				pp4 = p4.toScreen();
			}

			return new double[][] { pp1, pp2, pp3, pp4 };
		}
	}

	int shade() {
		// normal
		double vz = Math.cos(line.angleZ);
		double vf = Math.sin(line.angleZ);
		double vx = vf * Math.cos(line.angle);
		double vy = vf * Math.sin(line.angle);

		double dp = dotProduct(vx, vy, vz, 1, 1, 1) / Math.sqrt(3);
		return (int) (250 * dp);
		// double cp = crossProduct(vx, vy, vz, 1, 1, 1) / Math.sqrt(3);
		// return (int) (250 * cp);
	}

	void paint(Graphics2D g, double[] camera) {
		Color c = new Color(shade(), 0, 0, 250);
		g.setColor(c);
		double[][] pp = line.toScreen(camera);

		if (exists(pp[0]) && exists(pp[1]) && exists(pp[2]) && exists(pp[3])) {
			// if (pp[0] != null && pp[1] != null && pp[2] != null && pp[3] !=
			// null) {
			int[] x = new int[] { (int) pp[0][0], (int) pp[1][0],
					(int) pp[3][0], (int) pp[2][0] };
			int[] y = new int[] { (int) pp[0][1], (int) pp[1][1],
					(int) pp[3][1], (int) pp[2][1] };
			g.fillPolygon(new Polygon(x, y, 4));
		}

		g.setColor(new Color(255, 100, 0));
		g.setStroke(new BasicStroke(5));
		if (pp[0] != null && pp[1] != null)
			g.drawLine((int) pp[0][0], (int) pp[0][1], (int) pp[1][0],
					(int) pp[1][1]);
		if (pp[2] != null && pp[3] != null)
			g.drawLine((int) pp[2][0], (int) pp[2][1], (int) pp[3][0],
					(int) pp[3][1]);
	}

	boolean exists(double[] p) {
		return (p != null && (p[0] > 0 || p[1] > 0));
	}

	public double[] onto(double x, double y) {
		// System.out.println(x + ", " + y);
		double d = line.i.distanceFrom(x, y)
				* Math.sin(line.i.angleWith(x, y) - line.angle);
		if (d > width) {
			d = d - width;
			x -= d * Math.cos(line.angle + Math.PI / 2);
			y -= d * Math.sin(line.angle + Math.PI / 2);
			return new double[] { x, y };
		} else if (d < -width) {
			d = -width - d;
			x -= d * Math.cos(line.angle - Math.PI / 2);
			y -= d * Math.sin(line.angle - Math.PI / 2);
			return new double[] { x, y };
		}
		return null;
	}

	double crossProduct(double a1, double a2, double a3, double b1, double b2,
			double b3) {
		double c = a1 * b2 - b1 * a2;
		double d = a2 * b3 - a3 * b2;
		double e = a3 * b1 - a1 * b3;
		return Math.sqrt(c * c + d * d + e * e);
	}

	double dotProduct(double a1, double a2, double a3, double b1, double b2,
			double b3) {
		double c = a1 * b1;
		double d = a2 * b2;
		double e = a3 * b3;
		return c + d + e;
	}

}
