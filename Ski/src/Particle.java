import java.awt.Color;
import java.awt.Graphics2D;

public class Particle {

	Point p;
	Color color;
	int life;

	Particle(double x, double y, double z, Color c) {
		p = new Point(x, y, z);
		color = c;
		life = 20;
	}

	boolean paint(Graphics2D g, double[] camera) {
		// draw trailing smoke particles
		life--;
		Point pp = p.origin(camera);
		if (pp.x > 0) {
			double[] pxy = pp.toScreen();
			int r = 4;
			g.setColor(color);
			g.drawArc((int) (pxy[0] - r), (int) (pxy[1] - r), r * 2, r * 2, 0,
					360);
		}
		return (life < 0); // if expired, it is removed from particle list
	}
}
