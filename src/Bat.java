import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;
import java.awt.image.*; 
import java.io.*; 
import javax.imageio.*; 
import java.util.*;
import java.math.*;


//this enemy ignores collision and follows the player roughly
//deals damge by intersecting with the player's hurtbox
//gets hit by the sword



public class Bat {
	
	
	private final int halfsize = 25;
	
	private Rectangle2D.Double hurtbox;
	public Rectangle2D.Double getHurtbox(){
		return hurtbox;
	}
	
	private int hp;
	private boolean isAlive = true;
	
	public boolean getLiving(){
		return isAlive;
	}
	
	public int getHP(){
		return hp;
	}
	
	public Bat(double x, double y, int hp){
		this.hp = hp;
		this.x = x;
		this.y = y;
		vx = 0;
		vy = 0;
		hurtbox = new Rectangle2D.Double(x - halfsize, y - halfsize, 2*halfsize, 2*halfsize);
		
		//start animation on random frame so the bats look more "disorganized"
		frame = randint(0,8);
	}
	
		
	private double x;
	private double y;
	
	private double vx;
	private double vy;
	private final double maxSpeed = 1;
	private final double minSpeed = -1 * maxSpeed;
	
	//accellaration value
	private final double a = 0.01;
	
	//bat has to be this close to aggro on you
	private final double aggroDist = 300.0;
	
	public void moveTowards(double px, double py){
		

		
		double dist = Math.hypot(px-x, py - y);
		
		//only accellarate when close enough but still keeps ice physics regardless
		if (dist <= aggroDist){
			

			//treat each velocity component seperately
			if (x > px){
				vx -= a;
				vx = Math.min(vx, maxSpeed);
			}
			else if (x < px){
				vx += a;
				vx = Math.max(vx, minSpeed);
			}
			
		
			if (y > py){
				vy -= a;
				vy = Math.min(vy, maxSpeed);
			}
			else if (y < py){
				vy += a;
				vy = Math.max(vy, minSpeed);
			}
			
			
		}
		//slow down
		else{
			vx *= 0.99;
			vy *= 0.99;
			
			//stop eventually
			if (Math.abs(vx) < 0.0002){
				vx = 0;
			}
			if (Math.abs(vy) < 0.0002){
				vy = 0;
			}
			
		}
			
		x += vx;
		y += vy;
			
		hurtbox.setRect(x-halfsize,y-halfsize,2*halfsize,2*halfsize);
		
		
		
	}
	
	//make sure the bats are always close enough to level rangeso you don't accidently softlock urself
	//out of killing a bat
	public void adjustPos(int w, int h){
		x = Math.max(-0.5*aggroDist, Math.min(w + 0.5*aggroDist, x));
		y = Math.max(-0.5*aggroDist, Math.min(h + 0.5*aggroDist, y));	
	}
	
	
	public void getHit(int dmg, double sourceX, double sourceY){
		hp -= dmg;
		hp = Math.max(0,hp);
		
		
		//fake knockback --> i want u to go away from the guy at whatever speed but away
		//always bounces at 45* for now
		
		//bat is left, bounce left
		if (x > sourceX){
			vx = maxSpeed;
		}
		else{
			vx = minSpeed;
		}
		
		if (y > sourceY){
			vy = maxSpeed;
		}
		else{
			vy = minSpeed;
		}
		
	
	}
	
	private int frame;
	private int bufferframe = 0;
	private final int time = 7;
	
	//9 images in bat
	private void incrementFrame(){
		bufferframe++;
		bufferframe = bufferframe%time;
		if (bufferframe == 0){
			frame++;
			frame = frame%9;	
		}
		
	}
	
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
	
	
	public void draw(Graphics2D g, int screenx, int screeny){
		
		//still have hp
		if (hp != 0){
			incrementFrame();
			BufferedImage temp = EnemySpriteContainer.getBatImage(frame);
			Point offset = EnemySpriteContainer.getBatOffset(frame);
			
			g.drawImage(temp, (int)x - (int)offset.getX() - screenx, (int)y - (int)offset.getY() - screeny, null);
		}
		//play death effect
		else{
			incrementDFrame();
			BufferedImage temp = EnemySpriteContainer.getDeathImage(dframe);
			Point offset = EnemySpriteContainer.getDeathOffset(dframe);
			
			g.drawImage(temp, (int)x - (int)offset.getX() - screenx, (int)y - (int)offset.getY() - screeny, null);
				
		}
		
		/*
		//box
		g.setColor(new Color (255,255,0));
		g.drawRect((int)x - screenx - halfsize,(int)y - screeny - halfsize,2*halfsize,2*halfsize);
		*/
	}
	
	private int randint(int low, int high){
		return (int)(Math.random()*(high-low+1)+low);
	}
	
	
	
	
	
	
}
