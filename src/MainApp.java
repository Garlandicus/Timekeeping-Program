import java.awt.Container;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class MainApp extends JFrame implements ActionListener
{
	
	MainPanel contentPane;
	public MainApp()
	{
		super("Fullscreen");
		contentPane = new MainPanel();
		setContentPane(contentPane);
		getContentPane().setPreferredSize( Toolkit.getDefaultToolkit().getScreenSize());
		pack();
		setResizable(false);
		setVisible(true);
		
		SwingUtilities.invokeLater(new Runnable() 
		{
			public void run()
			{
				Point p = new Point(0, 0);
				SwingUtilities.convertPointToScreen(p, getContentPane());
				Point l = getLocation();
				l.x -= p.x;
				l.y -= p.y;
				setLocation(l);
				    
				Container contentPane = getContentPane();
			}
		});
	}
  
  	public static void main(String[] args)
	{
		new MainApp();
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		
	}

}