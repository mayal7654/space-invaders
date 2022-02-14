import java.awt.Image;

public class Spaceship extends Characters2D{
	private double xSpeed = 0;
	
	public Spaceship(Image i) {
		myImage1 = i;
	}
	
	public void setXSpeed(double dx) {
		xSpeed=dx;
	}
	
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}
	
	public void move(){	
		x += xSpeed;
		
		if (x<=0) {
			x=0;
			xSpeed=0;
		}
		else if (x>=windowWidth-myImage1.getWidth(null)) {
			x = windowWidth-myImage1.getWidth(null);
			xSpeed=0;
		}
	}
	
	
}
