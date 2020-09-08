import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;
import java.awt.image.*; 
import java.io.*; 
import javax.imageio.*; 
import java.util.*;
import java.math.*;


//stationary guy that looks towards player
//occasionally shoots lasers that do damage


public class LaserEye {
	
	//position always stays the same
	private double x;
	private double y;
	
	
	private final int halfsize = 15;//of hurtbox rect
	
	//eye has to be this close to shoot on you
	private final double aggroDist = 250.0;
	
	//all eyes share the same room
	public static Room currRoom;
	public static void setRoom(Room c){
		LaserEye.currRoom = c;
	}
	
	//same with bats, hp == 0 is not isAlive, because isAlive only turns false when the death animation is finished
	private int hp;
	private boolean isAlive = true;
	
	public boolean getLiving(){
		return isAlive;
	}
	
	public int getHP(){
		return hp;
	}
	
	//use Rectangle2D for built in fns
	private Rectangle2D.Double hurtbox;
	public Rectangle2D.Double getHurtbox(){
		return hurtbox;
	}
	

	public LaserEye(double x, double y, int hp){
		this.hp = hp;
		this.x = x;
		this.y = y;
		ang = 0;
		roundedAng = 0;
		hurtbox = new Rectangle2D.Double(x - halfsize, y - halfsize, 2*halfsize, 2*halfsize);
		allLasers = new ArrayList<Laser>();
		
	}
	
	
	
	/* 			Hitting and getting the player hit by both lasers and body				*/
	public void getHit(int dmg){
		hp -= dmg;
		hp = Math.max(0,hp);
		
		//reset timeSinceLastLaser aka pseudo stun
		timeSinceLastLaser = 20;
	}
	
	
	//see if the rectangle collides with any laser, or the actual hurtbox
	//these hurtboxes are less generous than bats because they move less, need to be more precise to dodge it
	public boolean collidesWith(Rectangle2D.Double guy){
		
		boolean ans = false;
		
		//loop thru all lasers
		for (Laser t : allLasers){
			if (guy.intersects(t.getHitbox())){
				ans = true;
				break;
			}
		}
		
		ans = ans || guy.intersects(hurtbox);
		
		
		return ans;	
	}
	
	
	
	
	//stuff to adjust angle to look towards the player
	private double ang;
	
	public void adjustAngle(double sourcex, double sourcey){
		ang = Math.toDegrees(Math.atan2(sourcey - y, sourcex - x));
		
		//always want positive answer
		ang += 360;
		ang = ang%360;
		
		roundedAng = roundAngle(ang);
		
		
		
	}
	
	//to nearest 15 degs
	private int roundedAng;
	private int roundAngle(double ang){
		int temp = (int)Math.round(ang/15) * 15;
		
		if (temp == 360){
			temp = 0;
		}
		
		
		return temp;
	}

	
	
	

	
	
	/*								lasers									*/
	
	ArrayList<Laser> allLasers;
	
	//create lasers at this speed
	private final double laserSpeed = 2.5;
	
	
	private int timeSinceLastLaser = 0;
	private final int delay = 100;//counter must be bigger than this to make new laser
	
	
	//if counter is high enougn and person is close enough 
	public void tryCreateLaser(double aimx, double aimy){
		
		//enough time has passed;
		if (timeSinceLastLaser > delay){
			
			//close enough
			if (Math.hypot(x - aimx, y - aimy) <= aggroDist){
				timeSinceLastLaser = 0;
				
				//add new laser into arraylist
				double tx = laserSpeed*Math.cos(Math.toRadians(ang));
				double ty = laserSpeed*Math.sin(Math.toRadians(ang));
				
				
				allLasers.add(new Laser(x,y,tx,ty));
						
			}
			
	
		}
		else{
			timeSinceLastLaser++;
			timeSinceLastLaser = Math.min(delay + 10, timeSinceLastLaser);//safety from int overflow
		}
		
		
	}
	
	//move all current lasers and remove them is they collide or are too far
	public void adjustLasers(){
		
		for(int i = allLasers.size() - 1; i >= 0; i--){
			
			Laser temp = allLasers.get(i);
			//too far
			if (Math.hypot(x - temp.getX(), y - temp.getY()) > aggroDist || currRoom.checkCollision(temp.getX(), temp.getY(),temp.getHalf(), "LASER")){
				allLasers.remove(i);
			}

			else{
				temp.move();
			}
			
			
		}
	}
	
	
	
	
	
	
	/*					death 								*/
	
	//4 images for deathh effect
	private int dframe = 0;
	private int dbufferframe = 0;
	private final int dtime = 10;
	
	//9 images in bat
	private void incrementDFrame(){
		dbufferframe++;
		dbufferframe = dbufferframe%dtime;
		if (dbufferframe == 0){
			dframe++;
			dframe = dframe%4;	
		}
		
		//retrun to last possible one aka death animation played
		if (dframe == 3 && dbufferframe == 6){
			isAlive = false;//causes the gamepanel.move to remove this bat now
		}
		
	}
	
	
	
	
	
	
	
	//split into hp > 0 and hp == 0 for both regular sprites and death animation
	public void draw(Graphics2D g, int screenx, int screeny){
		
		
		
		//still alive (works just like bat)
		if (hp != 0){
			
			BufferedImage temp = EnemySpriteContainer.getEyeImage(roundedAng);
			Point offset = EnemySpriteContainer.getEyeOffset(roundedAng);
			
			g.drawImage(temp, (int)x - (int)offset.getX() - screenx, (int)y - (int)offset.getY() - screeny, null);
			
			//draw all lasers
			for (Laser tlaser : allLasers){
				tlaser.draw(g,screenx, screeny);
			}	
			
			
		}
		else{
			
			//when dead just remove all current lasers
			allLasers.clear();
			
			incrementDFrame();
			BufferedImage temp = EnemySpriteContainer.getDeathImage(dframe);
			Point offset = EnemySpriteContainer.getDeathOffset(dframe);
			
			g.drawImage(temp, (int)x - (int)offset.getX() - screenx, (int)y - (int)offset.getY() - screeny, null);
		}
			
		
		
		
		
		
		//visualize aggrodist
		g.setColor(Color.MAGENTA);
		g.drawOval((int)x - screenx - (int)aggroDist,(int)y - screeny - (int)aggroDist,2*(int)aggroDist,2*(int)aggroDist);
		
		/*
		//temporarily just draw rects for laser and guy;
		g.setColor(Color.MAGENTA);
		g.drawRect((int)x - halfsize - screenx, (int)y - halfsize - screeny, 2*halfsize, 2*halfsize);
		*/
		
		
		
	}
	
	
	
	
	
	
	
}
