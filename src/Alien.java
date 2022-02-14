import java.awt.Graphics;
import java.awt.Image;

public class Alien extends Characters2D{
	public static int xSpeed = 0;
	public static int framesDrawn=0;
	private boolean isAlive;
	
	public Alien(Image i, Image i2) {
		myImage1 = i;
		myImage2 = i2;
		isAlive = true;
	}
	
	public boolean getisAlive() {
		return isAlive;
	}
	
	public void setAlive(boolean b) {
		isAlive = b;
	}
	
	public boolean move() {
		x += xSpeed;
		
		if (x<=0 || (x>=windowWidth-myImage1.getWidth(null) || x>=windowWidth-myImage2.getWidth(null)))
			return true;
		else
			return false;
	}
	
	public static void reverseDirection() {
		xSpeed *= -1;
	}
	
	public static void setAlienXSpeed(int dx) {
		xSpeed = dx;
	}
	
	public static int getXSpeed() {
		return xSpeed;
	}
	
	public void jumpDown() {
		y += 20;
	}
	
	public void paint(Graphics g) {
		if(framesDrawn%100<50) {
			g.drawImage(myImage1, (int)x, (int)y, null);
		}
		else
			g.drawImage(myImage2, (int)x, (int)y, null);
	}
}
