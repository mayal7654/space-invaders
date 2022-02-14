
import java.awt.*;

public class Characters2D {
	
	protected static int windowWidth = 800, windowHeight = 600;
	protected double x, y, xSpeed = 0;
	protected Image myImage1, myImage2;
	int framesDrawn=0;
	int winWindow;
	
	
	public Characters2D(Image i, Image i2) {
		myImage1 = i;
		myImage2 = i2;
	}
	
	public Characters2D() {
		// TODO Auto-generated constructor stub
	}

	//x and y value mutator
	public void setPosition(double xx, double yy) {
		x = xx;
		y = yy;
	}
	
	public void paint(Graphics g) {
		g.drawImage(myImage1, (int)x, (int)y, null);
	}

}
