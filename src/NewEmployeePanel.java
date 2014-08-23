import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import Data.TimeController;


public class NewEmployeePanel extends JPanel implements ActionListener {
	
	protected MainPanel parent;
	
	protected JLabel lFName;
	protected JLabel lLName;
	protected JLabel lPassword;
	
	protected JTextField tFName;
	protected JTextField tLName;
	protected JPasswordField tPassword;
	protected JCheckBox cManager;
	protected JButton bCreate;
	protected JButton bCancel;
	
	protected TimeController timeMan;
	
	public NewEmployeePanel(MainPanel m, TimeController t)
	{
		super(new BorderLayout());
		setLayout(new GridLayout(0, 2));
		parent = m;
		timeMan = t;
		
		lFName 		= new JLabel("First Name:");
		lLName 		= new JLabel("Last Name: ");
		lPassword 	= new JLabel("Password:  ");
	
		tFName = new JTextField(20);
			tFName.setMaximumSize(new Dimension(200,25));
			tFName.setHorizontalAlignment(JTextField.CENTER);
		tLName = new JTextField(20);
			tLName.setMaximumSize(new Dimension(200,25));
			tLName.setHorizontalAlignment(JTextField.CENTER);
		tPassword = new JPasswordField(20);
			tPassword.setMaximumSize(new Dimension(200,25));
			tPassword.setHorizontalAlignment(JTextField.CENTER);
			
		cManager = new JCheckBox("Manager?");
			cManager.setMaximumSize(new Dimension(200,25));
			
		bCreate = new JButton("Create");
			bCreate.setActionCommand("create");
			bCreate.setMaximumSize(new Dimension(200,25));
			bCreate.addActionListener(this);
			
		bCancel = new JButton("Cancel");
			bCancel.setMaximumSize(new Dimension(200,25));
			bCancel.setActionCommand("cancel");
			bCancel.addActionListener(this);
			
		add(lFName);
		add(tFName);
		
		add(lLName);
		add(tLName);
		
		add(lPassword);
		add(tPassword);
		
		add(cManager);
		add(new Container());	//Just here to take up space
		
		add(bCreate);
		add(bCancel);
		
	}
	
	public void clear()
	{
		tFName.setText("");
		tLName.setText("");
		tPassword.setText("");
		cManager.setSelected(false);
		bCancel.setEnabled(true);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if(arg0.getActionCommand().equals("create"))
		{
			int authLevel = 0;
			if(cManager.isSelected())
				authLevel = 1;
			int result = timeMan.addEmployee(tFName.getText(), tLName.getText(), new String(tPassword.getPassword()), authLevel);
			if(result < 1)
			{
				if(result == -1)
					JOptionPane.showMessageDialog(null, "Employee " + tFName.getText() + " " + tLName.getText() + " already has an entry in the database.");
				if(result == -2)
					JOptionPane.showMessageDialog(null, "The specified password has already been taken, please choose a new one");
			}
			else
			{
				JOptionPane.showMessageDialog(null, "Employee " + tFName.getText() + " " + tLName.getText() + " has successfully been added to the database.");
				this.setVisible(false);
				if(parent.getLoginStatus())
					parent.adminPane.setVisible(true);
				else
					parent.completeSetup();
			}
		}
		else if(arg0.getActionCommand().equals("cancel"))
		{
			clear();
			this.setVisible(false);
			parent.adminPane.setVisible(true);
		}
		//((ActionListener) this.getParent()).actionPerformed(arg0);
	}
}
