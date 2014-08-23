import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import Data.Employee;
import Data.TimeController;


public class MainPanel extends JPanel implements ActionListener {
	
	/**
	 * Default Serial Version UID
	 */
	private static final long serialVersionUID = 1L;
	
	//Data
	protected TimeController timeMan;
	protected Employee currentEmployee;
	private boolean loggedIn;
	private String dbFileName = "ktk.db";
	
	//Panels
	protected JPanel loginPane;
	protected JPanel messagePane;
	protected EmployeePanel userPane;
	protected AdminPanel adminPane;
	protected NewEmployeePanel newEEPane;
	
	//Components
	protected JLabel lMessage;
	protected JLabel lPassword;
	protected JButton bLogin;
	protected JButton bLogout;
	protected JPasswordField tPassword;

	public MainPanel(){
		super(new BorderLayout());
		setLayout(new BoxLayout(this,BoxLayout.PAGE_AXIS));		
		
		//---------------------------------------------------
		//Initialize Data Management Objects
		//---------------------------------------------------
		//Time Management Initialization
		timeMan = new TimeController(dbFileName);
		loggedIn = false;
		
		//---------------------------------------------------		
		//Initialize Components
		//---------------------------------------------------
		newEEPane = new NewEmployeePanel(this, timeMan);
			newEEPane.setMaximumSize(new Dimension(400,300));
		adminPane = new AdminPanel(timeMan);
		userPane = new EmployeePanel(timeMan);
		
		//Message Panel
		messagePane = new JPanel();
			messagePane.setMaximumSize(new Dimension(600,200));
			messagePane.setLayout(new GridBagLayout());
		lMessage = new JLabel("");
			lMessage.setMaximumSize(new Dimension(600, 200));
		messagePane.add(lMessage);
		
		//Login Box
		loginPane = new JPanel();
		loginPane.setLayout(new BoxLayout(loginPane, BoxLayout.LINE_AXIS));
		lPassword = new JLabel("Password: ");
		
		
		bLogin = new JButton("Login");
		bLogin.setActionCommand("login");
		bLogin.addActionListener(this);
		
		
		bLogout = new JButton("Logout");
		bLogout.setActionCommand("logout");
		bLogout.setEnabled(false);
		bLogout.addActionListener(this);
		
		tPassword = new JPasswordField(20);
		tPassword.setPreferredSize(new Dimension(100,25));
		tPassword.setMaximumSize(new Dimension(100, 25));
		tPassword.setHorizontalAlignment(JTextField.CENTER);
		tPassword.setActionCommand("login");
		tPassword.addActionListener(this);
		
		        
		loginPane.add(Box.createRigidArea(new Dimension(0,100)));
		loginPane.add(lPassword);
			loginPane.add(Box.createRigidArea(new Dimension(10,0)));
		loginPane.add(tPassword);
			loginPane.add(Box.createRigidArea(new Dimension(10,0)));
		loginPane.add(bLogin);
			loginPane.add(Box.createRigidArea(new Dimension(10,0)));
		loginPane.add(bLogout);
			loginPane.add(Box.createRigidArea(new Dimension(10,0)));			
		
		//Add Child Panes
		add(messagePane);
		add(loginPane);
			loginPane.setVisible(false);
		add(userPane);
			userPane.setVisible(false);
		add(adminPane);
			adminPane.setVisible(false);
		add(newEEPane);
			newEEPane.setVisible(false);	
			
		//Check to see if first time setup
		if(!timeMan.ready())
		{
			firstTimeSetup();
			timeMan.generateEmployees(10);			//-----------------------used for testing purposes only
		}
	}
	
	public void firstTimeSetup()
	{
		lMessage.setText("<html><center>It looks like this is the first time you're running this program.<br>Please create your administrative profile below before continuing<center></html>");
		timeMan.initialize();
		newEEPane.setVisible(true);
		newEEPane.cManager.setSelected(true);
		newEEPane.bCancel.setEnabled(false);		
	}
	
	public void completeSetup()
	{
		lMessage.setText("<html><center>Welcome to the Kibberia Timekeeping System.<br>Please login below:<center></html>");
		loginPane.setVisible(true);
	}
	
	public boolean getLoginStatus()
	{
		return loggedIn;
	}
	
	public void login(String password)
	{
		currentEmployee = timeMan.login(password);
		
		if(currentEmployee == null)
		{
			JOptionPane.showMessageDialog(null, "Sorry, no password match found. Please try again.");
			return;
		}
		else
		{
			bLogin.setEnabled(false);
			bLogout.setEnabled(true);
			tPassword.setEnabled(false);
			loggedIn = true;
			
			if(currentEmployee.getAuthLevel() == 0)
			{
				userPane.loadEmployee(currentEmployee);
				userPane.setVisible(true);
			}
			
			else if(currentEmployee.getAuthLevel() >= 1)
				adminPane.setVisible(true);				
		}
	}
	
	public void logout()
	{
		currentEmployee = null;
		bLogin.setEnabled(true);
		bLogout.setEnabled(false);
		tPassword.setEnabled(true);
		loggedIn = false;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if("login".equals(e.getActionCommand()))
		{
			login(new String(tPassword.getPassword()));
		}
		else if("logout".equals(e.getActionCommand()))
		{
			bLogin.setEnabled(true);
			bLogout.setEnabled(false);
			tPassword.setEnabled(true);
			
			userPane.setVisible(false);
		}	
	}

}
