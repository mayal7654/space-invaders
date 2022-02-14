import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;

public class InvadersApplication extends JFrame implements Runnable, KeyListener{

	private static final Dimension WindowSize = new Dimension(800, 600);
	private static final int NUMGAMEOBJECTS = 30;
	private Alien[] EnemyArray = new Alien[NUMGAMEOBJECTS];
	private Spaceship playerShip;
	private ArrayList<PlayerBullet> bulletList = new ArrayList<PlayerBullet>();
	private boolean collision = false, gameMenu = true;
	private BufferStrategy strategy;
	private Graphics offScreenGraphics;
	private static String workingDirectory;
	private Image alienImage1, alienImage2, bullet, shipImage;
	private int score = 0, wave = 0;
	private long previousBulletTime = System.currentTimeMillis();

	
	public InvadersApplication(){
		//key listener added
		addKeyListener(this);
		
		//initialising and setting up aliens and player
		ImageIcon icon1 = new ImageIcon(workingDirectory + "\\alien1.png");
		ImageIcon icon2 = new ImageIcon(workingDirectory + "\\alien2.png");
		ImageIcon icon3 = new ImageIcon(workingDirectory + "\\player_ship.png");
		ImageIcon icon4 = new ImageIcon(workingDirectory + "\\bullet.png");
		alienImage1 = icon1.getImage();
		alienImage2 = icon2.getImage();
		shipImage = icon3.getImage();
		bullet = icon4.getImage();
		createAliensAndPlayer(); //seperate method for initialising 

		
		//game screen window initialising 
		this.setTitle("Space Invaders");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Dimension screensize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		int x = screensize.width / 2 - WindowSize.width / 2;
		int y = screensize.height / 2 - WindowSize.height / 2;
		setBounds(x, y, WindowSize.width, WindowSize.height);
		setVisible(true);
		
		Thread t = new Thread(this);
		t.start(); //starts thread
		
		//creating double buffer 
		//after setBounds and setVisible 
		createBufferStrategy(2);
		strategy = getBufferStrategy();
		offScreenGraphics = strategy.getDrawGraphics();
	}

	public void keyTyped(KeyEvent e) {
		
	}

	
	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();
		
		if (!gameMenu) {
			//alters x values when keys are pressed
			if (key == KeyEvent.VK_RIGHT) {
				playerShip.setXSpeed(10);
			}
			if (key == KeyEvent.VK_LEFT) {
				playerShip.setXSpeed(-10);
			}
			if (key == KeyEvent.VK_SPACE) {
				//limits the amount of bullets the player can shoot at once
				if (System.currentTimeMillis() - previousBulletTime > 250) {
				shootBullet();
				previousBulletTime = System.currentTimeMillis();
				}
			}
		}
		//any key will trigger the game to start
		else {
			gameMenu = false;
		}
	}
	
	//resets player speed to 0 when key is released
	public void keyReleased(KeyEvent e) {
		int key = e.getKeyCode();
		
		if (key == KeyEvent.VK_RIGHT) {
			playerShip.setXSpeed(0);
		}
		if (key == KeyEvent.VK_LEFT) {
			playerShip.setXSpeed(0);
		}
	}
	
	//painting background, array of aliens and player's ship
	public void paint(Graphics g) {
		//double buffering occurs here
		g = offScreenGraphics;
		g.setColor(Color.black);
		g.fillRect(0, 0, WindowSize.width, WindowSize.height);
		//based on the gameMenu value, the windoe will eithe display aliens or the starting screen
		if (!gameMenu) {
			//shows the wave number and player's score
			g.setFont(new Font("Courier", 0, 22));
			g.setColor(Color.white);
			g.drawString( "Wave "+(wave+1)+"    "+"Score: "+score, (WindowSize.width/2)-110, 60);
			
			//prints all alive aliens
			for (Alien alien : EnemyArray) {
				if (alien.getisAlive() == true) {
					alien.paint(g);
				}
			}
			
			//prints bullets 
			Iterator iterator = bulletList.iterator();
			while(iterator.hasNext()) {
				PlayerBullet b = (PlayerBullet) iterator.next();
				b.paint(g);
			}
			
			//prints player
			playerShip.paint(g);
			strategy.show();
		}
		else {
			//start screen
			g.setFont(new Font("Courier", 0, 42));
			g.setColor(Color.white);
			g.drawString( "Space Invaders", (WindowSize.width/2)-180, (WindowSize.height/2)-30);
			g.setFont(new Font("Courier", 0, 32));
			g.drawString( "Press any key to play", (WindowSize.width/3)-65, (WindowSize.height/3)*2);
			strategy.show();
		}
	}
	
	public void run() {
		while (true) {
			try {
				Thread.sleep(50);
			}
			catch (Exception e) {}
			if (!gameMenu) {
				int numAlive = 30;
				//player's ship is moved depending on xSpeed
				playerShip.move();
				//aliens all move
				//checkCollision is used to check for collisions which sets the collision boolean to true
				for (Alien alien : EnemyArray) {
					if (alien.getisAlive()) {
						if (alien.move()) {
							collision = true;
						}
						//if an alien touches the player, the player loses and a new game starts
						if (checkCollision(playerShip)) {
							startNewGame();
						}
					}
				}
				
				//if the collision boolean is true, all aliens reverse their direction and the boolean is set back to false 
				if (collision) {
					Alien.reverseDirection();
					for (Alien alien : EnemyArray){
						alien.jumpDown();
					}
					collision = false;
				}
				
				//bullets move across the window -- if one touches an alien, it is removed
				Iterator iterator = bulletList.iterator();
				while(iterator.hasNext()) {
					PlayerBullet b = (PlayerBullet) iterator.next();
					b.move();
					if (checkCollision(b) || b.y == 0)
						iterator.remove();
				}
				
				//checks how many aliens are alive
				for (Alien alien : EnemyArray) {
					if (!alien.getisAlive()) {
						numAlive--;
					}
				}
				
				//if none are alive, a new wave begins
				if (numAlive == 0)
					startNewWave();
				
				
				this.repaint();
			}
			else
				this.repaint();
		}
	}
	
	//sets position for all aliens
	public void setAlienPosition() {
		for (int i = 0 ;i < NUMGAMEOBJECTS;i++){
			double x = (i%5)*80 + 40;
			double y = (i/5)*40 + 70;
			EnemyArray[i].setPosition(x,y);
		}
	}
	
	//initialises aliens and player + sets positions
	public void createAliensAndPlayer() {
		for (int i = 0 ;i < NUMGAMEOBJECTS;i++) {
			EnemyArray[i] = new Alien(alienImage1, alienImage2);
			Alien.setAlienXSpeed(5);
		}
		setAlienPosition();
		playerShip = new Spaceship(shipImage);
		playerShip.setPosition((WindowSize.width/2)-25, WindowSize.height-80);
	}
	
	//new wave -> sets all aliens to alive and increases their speed
	public void startNewWave() {
		wave++;
		for (Alien a : EnemyArray) {
			a.setAlive(true);
		}
		setAlienPosition();
		Alien.setAlienXSpeed(5+(2*wave));
	}
	
	//restarts all stats and resets alien and player positions
	public void startNewGame(){
		for (Alien a : EnemyArray) {
			a.setAlive(true);
		}
		Alien.setAlienXSpeed(5);
		setAlienPosition();
		playerShip.setPosition((WindowSize.width/2)-25, WindowSize.height-80);
		score = 0;
		wave = 0;
		gameMenu = true;
	}
	
	//adds bullet object to the array list and sets its beginning position
	public void shootBullet() {
		PlayerBullet b = new PlayerBullet(bullet);
		b.setPosition(playerShip.x+54/2, playerShip.y);
		bulletList.add(b);
	}
	
	//checks for collisions
	public boolean checkCollision(Characters2D b) {
		double x1, x2, w1, w2, h1, h2, y1, y2;
		for (Alien alien : EnemyArray) {
			x1 = alien.x;
			x2 = b.x;
			y1 = alien.y;
			y2 = b.y;
			w1 = alienImage1.getWidth(null);
			w2 = bullet.getWidth(null); //bullet image is used but can also apply to playerShip
			h1 = alienImage1.getHeight(null);
			h2 = bullet.getHeight(null); 
			if (alien.getisAlive()) {
				if ( ((x1<x2 && x1+w1>x2) || (x2<x1 && x2+w2>x1)) && ((y1<y2 && y1+h1>y2) || (y2<y1 && y2+h2>y1))) {
					alien.setAlive(false); //alien status is set to dead
					score += 20; //increases player score
					return true;
				}
			}
		}
		return false;
	}
	
	public static void main(String[] args) {
		workingDirectory = System.getProperty("user.dir");
		System.out.println("Working Directory = " + workingDirectory); 
		InvadersApplication i_a = new  InvadersApplication();
	}
}
