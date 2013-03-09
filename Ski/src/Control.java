import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

public class Control implements MouseMotionListener, KeyListener {

	double foward;
	double side;
	boolean mouseDisabled = false;
	boolean slowMo, fly;

	public void keyPressed(KeyEvent e) {
		switch (e.getKeyChar()) {
		case 'w':
			foward = 1;
			break;
		case 's':
			foward = -1;
			break;
		case 'a':
			side = -1;
			break;
		case 'd':
			side = 1;
			break;
		case ' ':
			slowMo = true;
			break;
		case '/':
			fly = true;
			break;
		case 'm':
			mouseDisabled = !mouseDisabled;
			foward = 0;
			side = 0;
			if (!mouseDisabled)
				System.out.println("mouse input: enabled");
			else
				System.out.println("mouse input: disabled");
		}
	}

	public void keyReleased(KeyEvent e) {
		switch (e.getKeyChar()) {
		case 'w':
		case 's':
			foward = 0;
			break;
		case 'a':
		case 'd':
			side = 0;
			break;
		case '/':
			fly = false;
			break;
		case ' ':
			slowMo = false;
			break;
		}
	}

	public void keyTyped(KeyEvent e) {

	}

	public void mouseDragged(MouseEvent e) {
	}

	public void mouseMoved(MouseEvent e) {
		if (!mouseDisabled) {
			foward = -e.getY() / 400.0 + 1;
			side = e.getX() / 400.0 - 1;
		}
	}

}
