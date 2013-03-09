import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class Track {

	// up down fix
	// map
	// register
	// opponent collision

	static final double gravity = .01;
	Control c;
	Painter p;
	Skier user;
	Skier opponent;
	Path[] path;
	Image backgroundImage;
	int backgroundImageSpeed = 3;
	Clip music;
	double cameraX, cameraY, cameraZ, cameraA;

	void beginMusic() {
		try {
			music = AudioSystem.getClip();
			AudioInputStream inputStream = AudioSystem
					.getAudioInputStream(new File("Derezzed.wav"));
			music.open(inputStream);
			music.loop(Clip.LOOP_CONTINUOUSLY);
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		music.start();
	}

	Track() {
		c = new Control();
		p = new Painter(c);
		user = new Skier(0, 0, -2, 0.01, true);
		opponent = new Skier(0, -1, -2, 0.01, false);

		path = new PathBuilder().trackGood();

		makeBackgroundImage();
	}

	void makeBackgroundImage() {
		int w = 800 * backgroundImageSpeed, h = 400;
		backgroundImage = new BufferedImage(w + 2 * h, 2 * h,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D brush = (Graphics2D) backgroundImage.getGraphics();
		int cMax = 150;
		for (int i = 0; i < 100; i++) {
			brush.setStroke(new BasicStroke((int) (Math.random() * 10)));
			brush.setColor(new Color((int) (Math.random() * cMax), (int) (Math
					.random() * cMax), (int) (Math.random() * cMax)));
			double r = Math.random() / 10;
			brush.drawArc((int) (Math.random() * w), (int) (Math.random() * h
					/ 2 + h / 4), (int) (r * 100), (int) (r * 100), (int) (0),
					(int) (360));
		}
		brush.setStroke(new BasicStroke(5));
		brush.setColor(new Color(255, 255, 255));
		brush.drawArc(w / 2 - 100, h / 2 - 100, 200, 200, 0, 360);

		// loop around
		brush.drawImage(backgroundImage, w, 0, w + 2 * h, h * 2, 0, 0, h * 2,
				h * 2, null);
	}

	void paintBackground() {
		double anglePercent = (cameraA / Math.PI / 2) % 1;
		if (anglePercent < 0)
			anglePercent++;
		int shift = (int) (anglePercent * backgroundImageSpeed * 800);
		p.brush.drawImage(backgroundImage, 0, 0, 800, 800, shift, 0,
				shift + 800, 800, null);
	}

	void paintTrack() {
		// draw far to near
		int i = user.segment;
		do {
			i--;
			if (i < 0)
				i = path.length - 1;
			path[i].paint(p.brush, camera());
		} while (i != user.segment);

		// draw reverse order
		// for (int i = path.length - 1; i > 0; i--) {
		// path[i].paint(p.brush, user.camera());
		// }

		// draw each
		// for (Path segment : path) {
		// segment.paint(p.brush, user.camera());
		// }

		// draw corners cut

		// Graphics2D g = p.brush;
		// double[] camera = user.camera();
		// int i = user.segment;
		// double[][] pp = path[i].line.toScreen(camera);
		//
		// do {
		// i--;
		// if (i < 0)
		// i = path.length - 1;
		//
		// Color c = new Color(path[i].shade(), 0, 0, 250);
		// g.setColor(c);
		// double[][] ppnext = path[i].line.toScreen(camera);
		//
		// intersect(ppnext, pp);
		//
		// if (path[i].exists(pp[0]) && path[i].exists(pp[1])
		// && path[i].exists(pp[2]) && path[i].exists(pp[3])) {
		// int[] x = new int[] { (int) pp[0][0], (int) pp[1][0],
		// (int) pp[3][0], (int) pp[2][0] };
		// int[] y = new int[] { (int) pp[0][1], (int) pp[1][1],
		// (int) pp[3][1], (int) pp[2][1] };
		// g.fillPolygon(new Polygon(x, y, 4));
		// }
		//
		// g.setColor(new Color(255, 100, 0));
		// g.setStroke(new BasicStroke(5));
		// if (pp[0] != null && pp[1] != null)
		// g.drawLine((int) pp[0][0], (int) pp[0][1], (int) pp[1][0],
		// (int) pp[1][1]);
		// if (pp[2] != null && pp[3] != null)
		// g.drawLine((int) pp[2][0], (int) pp[2][1], (int) pp[3][0],
		// (int) pp[3][1]);
		// pp = ppnext;
		// } while (i != user.segment);

	}

	void intersect(double[][] line1, double[][] line2) {
		for (int i = 0; i < 3; i += 2)

			if (Path.exists(line1[0 + i]) && Path.exists(line1[1 + i])
					&& Path.exists(line2[0 + i]) && Path.exists(line2[1 + i])) {

				double x1 = line1[0 + i][0], y1 = line1[0 + i][1];
				double dx1 = line1[1 + i][0] - x1, dy1 = line1[1 + i][1] - y1;
				double x2 = line2[0 + i][0], y2 = line2[0 + i][1];
				double dx2 = line2[1 + i][0] - x2, dy2 = line2[1 + i][1] - y2;

				double t = (y1 * dx2 - y2 * dx2 - x1 * dy2 + x2 * dy2)
						/ (dx1 * dy2 - dy1 * dx2);
				double s = (x1 + dx1 * t - x2) / dx2;

				if (0 < t && t < 1 && 0 < s && s < 1) {
					double newX1 = x1 + dx1 * t;
					double newY1 = y1 + dy1 * t;
					line1[0 + i][0] = newX1;
					line1[0 + i][1] = newY1;
				}
			}
	}

	void paintSkiers() {
		user.paint(p.brush);
		opponent.paint(p.brush);
		// for (Skier s : opponents)
		// s.paint(p.brush());
	}

	void drawText() {
		p.brush.drawString("mouse input: (m)" + !c.mouseDisabled, 10, 50);
		// if (slowMo <= .6)
		// p.brush.drawString("SLOW MO", 360, 390);
	}

	void begin() {
		beginMusic();
		while (true) {
			user.ski(c.foward, c.side, c.slowMo);
			opponent.ski(opponent.autoDrive(), c.slowMo);

			paintBackground();
			paintTrack();
			paintSkiers();
			drawText();
			p.paint(c.slowMo);

			wait(10);
		}
	}

	public void wait(int howLong) {
		try {
			Thread.sleep(howLong);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new Track().begin();
	}

	class Skier {
		double x, y, z;
		int segment;
		double angle;
		double vx, vy, vz;
		double thrust;
		double angleZ;
		int cameraW = 10;
		Color color;
		boolean cameraControl;

		Skier(double x, double y, double z, double thrust, boolean human) {
			this.x = x;
			this.y = y;
			this.z = z;
			this.thrust = thrust;

			if (human) {
				cameraControl = true;
				color = new Color(0, 250, 0, 150);// green
			} else
				color = new Color(0, 0, 250, 150);// blue
		}

		void ski(double foward, double side, boolean slowMoB) {
			double slowMo = 1;
			if (slowMoB)
				slowMo = .3;

			if (cameraControl) {// camera only follows 1 player (human)
				cameraX = average(cameraX, x, cameraW);
				cameraY = average(cameraY, y, cameraW);
				cameraZ = average(cameraZ, z, cameraW);
				cameraA = average(cameraA, angle, cameraW);
			}

			double pz = path[segment].getZ(x, y);
			boolean tooLargePz = (Math.abs(pz) > 1000 * Math.abs(z));
			if (tooLargePz) {
				System.out.println(pz);
				pz = z;
			}
			if (z >= pz - 1) {
				vx -= .02 * vx * slowMo;
				vy -= .02 * vy * slowMo;
				double accel = foward * thrust * 2;
				angleZ = average(angleZ, path[segment].angle(angle), 1);
				double flatAccel = accel * Math.cos(angleZ);
				vx += flatAccel * Math.cos(angle) * slowMo;
				vy += flatAccel * Math.sin(angle) * slowMo;
				vz += accel * Math.sin(angleZ) * slowMo;
			} else {
				angleZ = average(angleZ, -Math.PI / 2, 10);
				vz += gravity * slowMo;
			}
			vz *= .99;

			x += vx * slowMo;
			y += vy * slowMo;
			z += vz * slowMo;
			if (z > pz) {
				z = pz;
				// bounce
				vz *= -.2;
			}
			angle += side * (.01 + 0.1 * Math.sqrt((vx * vx) + (vy * vy)));

			if (path[segment].line.progress(x, y) >= 1.1)
				segment = (segment + 1) % path.length;
			else if (path[segment].line.progress(x, y) < -.1) {
				segment--;
				if (segment < 0)
					segment = path.length - 1;
			}

			double[] pxy = path[segment].onto(x, y);
			if (pxy != null) {
				x = pxy[0];
				y = pxy[1];
			}

		}

		void ski(double[] instructions, boolean slowMoB) {
			ski(instructions[0], instructions[1], slowMoB);
		}

		double[] autoDrive() {
			double foward = .5;
			double side = 0;
			side = -path[segment].perpindicularDistance(x, y) / 2;
			if (side < -1)
				side = -.5;
			if (side > 1)
				side = .5;

			// // avoid oversteering
			// if (angle > path[segment].line.angle + .01)
			// side = Math.max(side, 0);
			// else if (angle < path[segment].line.angle - .01)
			// side = Math.min(side, 0);

			return new double[] { foward, side };
		}

		void paint(Graphics2D g) {

			double backD = 1.8;
			double backDT = 1;
			double dist = .4;

			double backX = x - backD * Math.cos(angle);
			double backY = y - backD * Math.sin(angle);

			if (new Point(backX, backY, z).origin(camera()).x > .1) {

				double backXT = x - backDT * Math.cos(angle);
				double backYT = y - backDT * Math.sin(angle);
				double backLX = backX - dist * Math.cos(angle + Math.PI / 2);
				double backLY = backY - dist * Math.sin(angle + Math.PI / 2);
				double backRX = backX + dist * Math.cos(angle + Math.PI / 2);
				double backRY = backY + dist * Math.sin(angle + Math.PI / 2);
				double backLXT = backXT - dist * Math.cos(angle + Math.PI / 2);
				double backLYT = backYT - dist * Math.sin(angle + Math.PI / 2);
				double backRXT = backXT + dist * Math.cos(angle + Math.PI / 2);
				double backRYT = backYT + dist * Math.sin(angle + Math.PI / 2);

				// BOTTOM
				double fLX = x - dist * Math.cos(angle + Math.PI / 2);
				double fLY = y - dist * Math.sin(angle + Math.PI / 2);
				double fRX = x + dist * Math.cos(angle + Math.PI / 2);
				double fRY = y + dist * Math.sin(angle + Math.PI / 2);

				double[] bl = new Point(backLX, backLY, z - backD
						* Math.sin(angleZ)).origin(camera()).toScreen();
				double[] br = new Point(backRX, backRY, z - backD
						* Math.sin(angleZ)).origin(camera()).toScreen();
				double[] fl = new Point(fLX, fLY, z).origin(camera())
						.toScreen();
				double[] fr = new Point(fRX, fRY, z).origin(camera())
						.toScreen();

				// TOP
				double top = -.5;
				double[] blT = new Point(backLXT, backLYT, z - backDT
						* Math.sin(angleZ) + top).origin(camera()).toScreen();
				double[] brT = new Point(backRXT, backRYT, z - backDT
						* Math.sin(angleZ) + top).origin(camera()).toScreen();
				double[] flT = new Point(fLX, fLY, z + top).origin(camera())
						.toScreen();
				double[] frT = new Point(fRX, fRY, z + top).origin(camera())
						.toScreen();

				// SHADOW
				double groundZ = path[segment].getZ(this.x, this.y);
				double[] blS = new Point(backLX, backLY, groundZ - backD
						* Math.sin(angleZ)).origin(camera()).toScreen();
				double[] brS = new Point(backRX, backRY, groundZ - backD
						* Math.sin(angleZ)).origin(camera()).toScreen();
				double[] flS = new Point(fLX, fLY, groundZ).origin(camera())
						.toScreen();
				double[] frS = new Point(fRX, fRY, groundZ).origin(camera())
						.toScreen();

				int[] xS = new int[] { (int) blS[0], (int) brS[0],
						(int) frS[0], (int) flS[0] };
				int[] yS = new int[] { (int) blS[1], (int) brS[1],
						(int) frS[1], (int) flS[1] };
				g.setColor(new Color(0, 0, 0, 100));
				g.fillPolygon(new Polygon(xS, yS, 4));

				int[] x = new int[] { (int) bl[0], (int) br[0], (int) fr[0],
						(int) fl[0] };
				int[] y = new int[] { (int) bl[1], (int) br[1], (int) fr[1],
						(int) fl[1] };
				g.setColor(color);
				g.fillPolygon(new Polygon(x, y, 4));

				int[] xT = new int[] { (int) blT[0], (int) brT[0],
						(int) frT[0], (int) flT[0] };
				int[] yT = new int[] { (int) blT[1], (int) brT[1],
						(int) frT[1], (int) flT[1] };
				g.fillPolygon(new Polygon(xT, yT, 4));

				// Point p = new Point(x, y, z);
				// double[] pp = p.origin(camera()).toScreen();
				// int r = 10;
				// g.fillOval((int) pp[0] - r, (int) pp[1] - r, 2 * r, 2 * r);
				//
				// double f = .4;
				// p = new Point(x + f * Math.cos(angle), y + f *
				// Math.sin(angle),
				// z);
				// pp = p.origin(camera()).toScreen();
				// r = 8;
				// g.fillOval((int) pp[0] - r, (int) pp[1] - r, 2 * r, 2 * r);
			}
		}
	}

	double[] camera() {
		return new double[] { cameraX - 5 * Math.cos(cameraA),
				cameraY - 5 * Math.sin(cameraA), -5 + cameraZ, cameraA };
		// return new double[] { x - 5 * Math.cos(angle),
		// y - 5 * Math.sin(angle), -5 + z, angle };
	}

	double average(double a, double b, int weight) {
		return (a * (weight - 1) + b) / weight;
	}

}
