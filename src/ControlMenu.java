import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/*
similar to StartMenu but...
- has DISPOSE_ON_CLOSE because when ControlMenu is created, both StartMenu and ControlMenu exist at same time,
  so pressing X on control menu doesn't close startmenu
  
- .makeText() just makes it easier for me to draw text consistently onto JLabels, then add those JLabels to JLayeredPane


*/

public class ControlMenu extends JFrame{
	
	private JLayeredPane layeredPane = new JLayeredPane();	
	
	
	
	public ControlMenu(){
		super("controls (X to return)");
		setSize(800,600);
		setLocationRelativeTo(null);
		
		
		//layers
		
		//background image	
		ImageIcon backpic = new ImageIcon("menu images/back.png");
		JLabel back = new JLabel(backpic);
		back.setBounds(0, 0,backpic.getIconWidth(),backpic.getIconHeight());
		layeredPane.add(back,1);
		
		JLabel title = new JLabel("INSTRUCTIONS");
		title.setFont(new Font("Serif", Font.BOLD, 75));
		title.setBounds(0, 0,800,150);
		title.setForeground(Color.WHITE);
		title.setHorizontalAlignment(SwingConstants.CENTER);
		layeredPane.add(title, 0);
		
		
		layeredPane.add(makeText("WASD to move", 50,100, 40),0);
		layeredPane.add(makeText("Double tap to run", 50,150, 40),0);
		layeredPane.add(makeText("Click for sword", 50,200, 40),0);
		layeredPane.add(makeText("Good luck!", 300,400, 50),0);
		layeredPane.add(makeText("<html> Kill all enemies <br/> to advance </html>", 400,150, 50),0);
		//researched that JLabel can use html for formatting but not \n
		

		setContentPane(layeredPane);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setResizable(false);
		setVisible(true);
		
		
	}
	

	public JLabel makeText(String text, int x, int y, int size){
		JLabel title = new JLabel(text);
		title.setFont(new Font("Serif", Font.BOLD, size));
		title.setBounds(x, y,800,150);
		title.setForeground(Color.ORANGE);
		title.setHorizontalAlignment(SwingConstants.LEFT);
		return title;	
	}
	
	public void kill(){
		dispose();
	}
	

	

	
}

