/**
Andy Liu Compsci FSE

Zelda Dungeon Remake


 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.image.*; 
import java.io.*; 
import javax.imageio.*; 
import java.util.*;


/*
This JFrame launches the actual game, has regular template functionality but I added:
- zeldaFSE.kill() to stop the timer and dispose the JFrame, used with  calling StartMenu.main(new String[0]) to exit the game and create a new menu
- zeldaFSE.loadAll() to load all images (sprites, backgrounds, tiles, etc.) into their respective static classes
*/

public class zeldaFSE extends JFrame implements ActionListener{
	
	private GamePanel game;
	private javax.swing.Timer myTimer;
	
	
	public zeldaFSE(){
		
		
		super("game!");
		
		
		
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(800,600);
		setLayout(new BorderLayout());
		setLocationRelativeTo(null);
		
		
		//make game run continuously
		myTimer = new javax.swing.Timer (10,this);
		
		//init and add gamepanel to this
		game = new GamePanel(this);
		add(game);
		
		//DO NOT PACK IF YOU WANT CONSISTENCY WITH PIXEL LENGTHS
		//pack();
		
		
		//after everything is done 
		setResizable(false);
		setVisible(true);
		
		
	}
	
	
	public void start(){
		myTimer.start();
	}
	
	public void kill(){
		myTimer.stop();
		//have to kill the timer or else actionPerformed will still keep running and making more startmenus
		//aka annoying and also RAM destroying
		dispose();
	}
	
	
	
	public void actionPerformed(ActionEvent evt){
		//long before = System.nanoTime();
		
		game.repaint();
		game.move();
		
		//long after = System.nanoTime();
		//System.out.println((after - before)/1000);
		
		
		
		
		
		
	}
	
	private static boolean alreadyLoaded = false;
	public static void loadAll(){
		//only load once even though every JFrame will call this (needed as a failsafe), saves time
		if (!alreadyLoaded){
			System.out.println("loaded");
			alreadyLoaded = true;
			
			//from research this enhances performance becuase OpenGL is a hardware accellorator
	    	//https://docs.oracle.com/javase/8/docs/technotes/guides/2d/flags.html#opengl
	    	System.setProperty("sun.java2d.opengl", "True");
	    	
	    	//load all images into their classes
			ParallaxBack.load();
	    	TileContainer.load();
	    	PlayerSpriteContainer.load();
	    	EnemySpriteContainer.load();
	    	MenuSpriteContainer.load();	
		}
	}
	
    
    public static void main(String[] aoorgs)  {
    	
    	zeldaFSE.loadAll();
    	
    	zeldaFSE frame = new zeldaFSE();
    }
}
