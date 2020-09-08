import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/*
Identical to ControlMenu
*/

public class CreditMenu extends JFrame{
	
	private JLayeredPane layeredPane = new JLayeredPane();	
	
	
	
	public CreditMenu(){
		super("credits (X to return)");
		setSize(800,600);
		setLocationRelativeTo(null);
		
		
		//layers
		
		//background image	
		ImageIcon backpic = new ImageIcon("menu images/back.png");
		JLabel back = new JLabel(backpic);
		back.setBounds(0, 0,backpic.getIconWidth(),backpic.getIconHeight());
		layeredPane.add(back,1);
		
		JLabel title = new JLabel("CREDITS");
		title.setFont(new Font("Serif", Font.BOLD, 75));
		title.setBounds(0, 0,800,150);
		title.setForeground(Color.WHITE);
		title.setHorizontalAlignment(SwingConstants.CENTER);
		layeredPane.add(title, 0);
		
		
		layeredPane.add(makeText("Made by: Andy Liu", 0,100),0);
		layeredPane.add(makeText("31500782", 0,200),0);
		layeredPane.add(makeText("Thanks for playing!", 0,300),0);

		

		setContentPane(layeredPane);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setResizable(false);
		setVisible(true);
		
		
	}
	

	public JLabel makeText(String text, int x, int y){
		JLabel title = new JLabel(text);
		title.setFont(new Font("Serif", Font.BOLD, 50));
		title.setBounds(x, y,800,150);
		title.setForeground(Color.WHITE);
		title.setHorizontalAlignment(SwingConstants.CENTER);
		return title;	
	}
	
	public void kill(){
		dispose();
	}
	

	

	
}

