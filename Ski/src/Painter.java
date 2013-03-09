import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

public class Painter extends JFrame {

	BufferedImage canvas;
	Graphics2D brush;
	final int FRAME_SIZE = 800;

	Painter(Control control) {
		setResizable(false);
		setSize(FRAME_SIZE, FRAME_SIZE);
		this.setUndecorated(true);
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		canvas = new BufferedImage(FRAME_SIZE, FRAME_SIZE,
				BufferedImage.TYPE_INT_RGB);
		brush = (Graphics2D) canvas.getGraphics();
		brush.setFont(new Font("monospaced", Font.PLAIN, 15));

		this.addKeyListener(control);
		this.addMouseMotionListener(control);
	}

	public void paint(boolean slowMo) {
		paint(getGraphics(), slowMo);
	}

	public void paint(Graphics g, boolean slowMo) {
		if (g != null) {
			// draw
			g.drawImage(canvas, 0, 0, getWidth(), getHeight(), null);

			// clear
			// brush.clearRect(0, 0, getWidth(), getHeight());

			// fade
			if (slowMo) {
				brush.setColor(new Color(0f, 0f, 0f, 0.02f));
			} else
				brush.setColor(new Color(0f, 0f, 0f, 0.1f));
			brush.fillRect(0, 0, getWidth(), getHeight());
		}
	}
}