import java.util.ArrayList;

public class PathBuilder {
	ArrayList<double[]> points;

	PathBuilder() {
		points = new ArrayList<double[]>();
	}

	// must be used first
	void add(double x, double y, double z) {
		points.add(new double[] { x, y, z });
	}

	// DO NOT use on EMPTY pathBuilder (there is no last point to continue)
	void move(double dx, double dy, double dz) {
		double[] last = points.get(points.size() - 1);
		points.add(new double[] { last[0] + dx, last[1] + dy, last[2] + dz });
	}

	// DO NOT use on EMPTY pathBuilder (there is no last point to continue)
	void arcMove(double r, double startAngle, double theta, double raise,
			int segments) {
		double dz = raise / segments;
		double dtheta = theta / segments;

		double[] last = points.get(points.size() - 1);

		double[] n;
		for (int i = 0; i < segments; i++) {
			double dx = r * Math.cos(startAngle);
			double dy = r * Math.sin(startAngle);
			startAngle += dtheta;
			n = new double[] { last[0] + dx, last[1] + dy, last[2] + dz * i };
			points.add(n);
			last = n;
		}
	}

	// after calling buildPath(), DONOT add any more points !
	Path[] buildPath() {
		Path[] r = new Path[points.size()];
		points.add(points.get(0));
		for (int i = 0; i < r.length; i++) {
			r[i] = new Path(new double[][] { points.get(i), points.get(i + 1) });
		}
		return r;
	}

	// you can make a Path with pathBuilder or without (trackCrazy)

	Path[] trackCrazy() {
		Path[] path = new Path[9];
		path[0] = new Path(new double[][] { { -5, 0, 0 }, { 10, 0, -5 } });
		path[1] = new Path(new double[][] { { 10, 0, -5 }, { 10, 0, 0 } });
		path[2] = new Path(new double[][] { { 10, 0, 0 }, { 15, 5, 0 } });
		path[3] = new Path(new double[][] { { 15, 5, 0 }, { 15, 10, 0 } });
		path[4] = new Path(new double[][] { { 15, 10, 0 }, { 10, 15, 0 } });
		path[5] = new Path(new double[][] { { 10, 15, 0 }, { -5, 15, -5 } });
		path[6] = new Path(new double[][] { { -5, 15, -5 }, { -10, 10, 0 } });
		path[7] = new Path(new double[][] { { -10, 10, 0 }, { -10, 5, -5 } });
		path[8] = new Path(new double[][] { { -10, 5, -5 }, { -5, 0, 0 } });
		return path;
	}

	Path[] trackBad() {
		add(-5, 0, 0);
		move(15, 0, 0);
		add(15, 0, 5);
		add(18, 2, 5);
		add(20, 5, 5);
		move(0, 5, 0);
		move(-25, 0, -5);
		return buildPath();
	}

	Path[] trackSpiral() {
		add(0, 0, 0);
		arcMove(3, 0, Math.PI * 1.7, -2, 10);
		return buildPath();
	}

	Path[] trackSpiralBig() {
		add(0, 0, 0);
		arcMove(10, 0, Math.PI * 1.7, -10, 100);
		return buildPath();
	}

	Path[] trackGood() {
		double a90 = Math.PI / 2;
		add(-5, 0, 0);
		move(30, 0, 0);
		arcMove(5, 0, a90, -7, 10);
		printLast();
		move(0, 10, 0);
		arcMove(5, a90, a90, 7, 10);
		printLast();
		move(-5, 0, 0);
		move(-4, 0, -10);
		move(-2, 0, 0);
		move(-4, 0, 10);
		printLast();
		arcMove(10, a90 * 2, a90, -10, 10);
		move(0, -20, 0);
		move(0, -5, 5);
		arcMove(5, 3 * a90, -2 * a90, 0, 10);
		move(0, 5, 5);
		arcMove(5, a90, 2 * a90, 0, 10);
		move(0, -5, -5);
		arcMove(5, 3 * a90, -2 * a90, 0, 10);
		move(0, 5, -5);
		move(0, 5, 5);
		arcMove(5, a90, -a90, 5, 10);
		move(3, 0, -3);
		move(3, 0, 3);
		move(8, 0, 0);
		move(3, 0, -3);
		move(3, 0, 3);
		return buildPath();
	}

	// essential for making sure your map ends where it starts , and findint
	// where mistakes are
	void printLast() {
		double[] last = points.get(points.size() - 1);
		System.out.println(last[0] + ", " + last[1] + ", " + last[2]);
	}

}
