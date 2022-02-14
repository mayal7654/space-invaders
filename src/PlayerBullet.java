import java.awt.Image;

public class PlayerBullet extends Characters2D {
	protected boolean isAlive;
	
	public PlayerBullet(Image i) {
		myImage1 = i;
		isAlive = true;
	}
	
	public void move() {
		y -= 10;
	}
}
