import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/*
JFrame uses JLayeredPane to show buttons, background correctly

- Jbuttons to access other JFrame screens (actual game, controls, credits)
	- it will kill itself when going to the actual zeldaFSE game, but not when going to credis or controls so u can see both at once
- background
- .kill() to dispose itself

main method here constructs the StartMenu
another JFrame will call StartMenu.main(new String[0]) and then dispose itself to get to this menu


*/

public class StartMenu extends JFrame{
	
	private JLayeredPane layeredPane = new JLayeredPane();	
	
	
	
	public StartMenu(){
		super("menu");
		setSize(800,600);
		setLocationRelativeTo(null);
		
		
		//layers
		
		//background image	
		ImageIcon backpic = new ImageIcon("menu images/back.png");
		JLabel back = new JLabel(backpic);
		back.setBounds(0, 0,backpic.getIconWidth(),backpic.getIconHeight());
		layeredPane.add(back,1);
		
		//title
		JLabel title = new JLabel("Space Dungeon");
		title.setFont(new Font("Serif", Font.BOLD, 100));
		title.setBounds(0, 0,800,150);
		title.setForeground(Color.WHITE);
		title.setHorizontalAlignment(SwingConstants.CENTER);
		layeredPane.add(title,0);
		
		
		//start game
		JButton startBtn = new JButton("START");
		startBtn.setFont(new Font("Serif", Font.BOLD, 25));
		startBtn.setBounds(300, 200,200,75);
		startBtn.addActionListener(new ClickStart());
		layeredPane.add(startBtn, 0);
		
		//instructions
		JButton inBtn = new JButton("CONTROLS");
		inBtn.setFont(new Font("Serif", Font.BOLD, 25));
		inBtn.setBounds(300, 300,200,75);
		inBtn.addActionListener(new ContStart());
		layeredPane.add(inBtn, 0);
		
		//instructions
		JButton conBtn = new JButton("CREDITS");
		conBtn.setFont(new Font("Serif", Font.BOLD, 25));
		conBtn.setBounds(300, 400,200,75);
		conBtn.addActionListener(new CredStart());
		layeredPane.add(conBtn, 0);
		
		
		
		
		
		
		
		setContentPane(layeredPane);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		setVisible(true);
		
		
	}
	
	public static void main(String[] args){
		zeldaFSE.loadAll();
		StartMenu men = new StartMenu();
		
			
	}
	
	
	public void kill(){
		dispose();
	}
	
	class ClickStart implements ActionListener{
    	@Override
    	public void actionPerformed(ActionEvent evt){
    		zeldaFSE.main(new String[0]);
    		kill();
    	}
    }
    
    class ContStart implements ActionListener{
    	@Override
    	public void actionPerformed(ActionEvent evt){
    		ControlMenu c = new ControlMenu();
    	}
    }
    
    class CredStart implements ActionListener{
    	@Override
    	public void actionPerformed(ActionEvent evt){
    		CreditMenu c = new CreditMenu();
    	}
    }
	

	
}

