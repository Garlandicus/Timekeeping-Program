package Data;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Random;
import java.util.Vector;

import javax.swing.JOptionPane;

public class TimeController {
	protected Vector<Employee> listOfEmployees;
	protected Vector<Employee> listOfRemovedEmployees;
	
	public TimeController()
	{
		listOfEmployees = new Vector<Employee>();
		listOfRemovedEmployees = new Vector<Employee>();
	}
	
	public TimeController(String dbName)
	{
		listOfEmployees = null;
		importDB(dbName);
	}
	
	public boolean ready()
	{
		return (listOfEmployees != null);
	}
	
	//Clears current contents of ListOfEmployees and ListOfRemovedEmployees for first time usage
	public void initialize()
	{
		listOfEmployees = new Vector<Employee>();
		listOfRemovedEmployees = new Vector<Employee>();
	}
	
	public Employee login(String password)
	{
		for(int x = 0; x < listOfEmployees.size(); x++)
		{
			if(getEmployee(x).getLoginCode().equals(password))
				return getEmployee(x);
		}
		return null;
	}
	
	public int addEmployee(String fName, String lName, String password, int authLevel)
	{
		for(int x = 0; x < listOfEmployees.size(); x++)
		{
			if((getEmployee(x).getFirstName().equals(fName) && getEmployee(x).getLastName().equals(lName)))
				return -1;
			else if(getEmployee(x).getLoginCode().equals(password))
				return -2;
		}
		listOfEmployees.add(new Employee(listOfEmployees.size(), fName, lName, password, authLevel));
		return 1;
		
	}
	
	public boolean removeEmployee(int number)
	{
		Employee toBeRemoved = getEmployee(number);
		if(!toBeRemoved.getWorkingStatus())
		{
			toBeRemoved.setEmploymentStatus(false);
			return true;
		}
		else
			JOptionPane.showMessageDialog(null, "Employee " + toBeRemoved.getEmpNumber() + " must be timed out before changing employment status");
		return false;
		
	}
	
	public boolean timeInEmployee(int number)
	{
		Employee e = listOfEmployees.get(number);
		if(e.getWorkingStatus() || !e.getEmployedStatus())
			return false;
		Calendar now = new GregorianCalendar();
		e.timeIn(now);
		return true;
	}
	
	public boolean timeOutEmployee(int number)
	{
		Employee e = listOfEmployees.get(number);
		if(!e.getWorkingStatus())
			return false;
		Calendar now = new GregorianCalendar();
		e.timeOut(now);
		return true;
	}
	
	public Employee getEmployee(int number)
	{
		Employee e = null;
		if(listOfEmployees.size() > number)
			e = listOfEmployees.get(number);
		return e;
	}
	
	public boolean exportDB(String dbName)
	{
		if(listOfEmployees == null)
			return false;
		FileOutputStream fos;
		ObjectOutputStream oos;
		try{
			fos = new FileOutputStream(dbName);
			oos = new ObjectOutputStream(fos);
			
			oos.writeObject(listOfEmployees);
			oos.writeObject(listOfRemovedEmployees);
			
			fos.close();
			oos.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return false;	
		}
		return true;
	}
	
	@SuppressWarnings("unchecked")
	public boolean importDB(String dbName)
	{
		FileInputStream fis;
		ObjectInputStream ois;
		try{
			fis = new FileInputStream(dbName);
			ois = new ObjectInputStream(fis);
			
			listOfEmployees = (Vector<Employee>) ois.readObject();
			listOfRemovedEmployees = (Vector<Employee>) ois.readObject();
			
			ois.close();
			fis.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}
		return true;
		
	}
	
	public void generateEmployees(int number)
	{
		Random gen = new Random();
		for(int x = 0; x < number; x++)
		{
			Employee e = new Employee(listOfEmployees.size(),""+x, ""+x, ""+x, 0);
			System.out.println("Creating new employee with password: " + x);
			for(int y = 0; y < 10; y++)
			{
				Calendar c = new GregorianCalendar();
				c.add(Calendar.HOUR_OF_DAY, (int) (-gen.nextDouble()*12));
				c.add(Calendar.DAY_OF_MONTH, (int) (-gen.nextDouble()*31));
				Calendar d = (GregorianCalendar) c.clone();
				d.add(Calendar.HOUR_OF_DAY, (int) ((24-c.get(Calendar.HOUR_OF_DAY))*gen.nextDouble()));
				d.add(Calendar.MINUTE, (int) -gen.nextDouble()*60);
				System.out.println("Generated new shift: " + printCalendar(c) + " to " + printCalendar(d));
				e.timeIn(c);
				e.timeOut(d);
			}
			listOfEmployees.add(e);
		}
	}
	
	public String printCalendar(Calendar c)
	{
		String value = "" + c.get(Calendar.YEAR)+"\\"+c.get(Calendar.MONTH)+"\\"+c.get(Calendar.DAY_OF_MONTH) + " " + c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE);
		return value;
	}
}
