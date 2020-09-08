import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;
import java.awt.image.*; 
import java.io.*; 
import javax.imageio.*; 
import java.util.*;
import java.math.*;


//just the projectile (move and draw it)

public class Laser {
	
	
	private final int half = 5;
	public int getHalf(){
		return half;
	}
	
	private double x,y,vx,vy;
	private Rectangle2D.Double laserbox;
	
	public Laser(double x, double y, double vx, double vy){
		this.x = x;
		this.y = y;
		this.vx = vx;
		this.vy = vy;
		
		laserbox = new Rectangle2D.Double(x - half, y - half, 2*half, 2*half);
		
	}
	
	public Rectangle2D getHitbox(){
		return laserbox;
	}
	
	public double getX(){
		return x;
	}
	public double getY(){
		return y;
	}
	
	
	public void move(){
		x += vx;
		y += vy;
		laserbox.setRect(x - half, y - half, 2*half, 2*half);
	}
	
	public void draw(Graphics2D g, int screenx, int screeny){
		
		//test for now, image later
		g.setColor(Color.RED);
		g.fillRect((int)x - half - screenx, (int)y - half - screeny, 2*half, 2*half);
		
	}
	
	
}
