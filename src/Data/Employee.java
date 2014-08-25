package Data;

import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

import javax.swing.JOptionPane;

/**
 * Employee Class 
 * Keep records for a variable number of employees, containing the following fields
		Time In/Out specified to the minute for at least 3 shifts during the day, up to 5, for every day they work (up to 7 days a week, for every week)
		Name
		Total Hours Worked
		?Employment status
		?Hours available to work (ie mornings, evenings etc)
		?Schedule
		
		to do: Sort employee shifts for faster access
		
 * @author Ryan
 *
 */
public class Employee {
	
	//VARIABLES
	private int empNumber;
	private String firstName;
	private String lastName;
	private String loginCode;
	private boolean employed;
	private int authLevel;
	
	private boolean working;
	
	private Shift currentShift;
	private Vector<Shift> shifts;
	
	//Functions
	public Employee(int number, String first, String last, String code, int level)
	{
		empNumber = number;
		firstName = first;
		lastName = last;
		loginCode = code;
		authLevel = level;
		
		//Default
		employed = true;
		shifts = new Vector<Shift>();
	}
	
	//EMPLOYEE LEVEL FUNCTIONS-----------------------------------------------------
	
	//Returns the number of hours worked this week
	public double getHoursWorked(Date start, Date end)
	{
		long total = 0;
		int x = 0;
		
		//Get to the first date within the range
		while(shifts.get(x).getStartTime().before(start)) x++;
		//Start adding dates while within the range until we exit the range
		while(shifts.get(x).getStartTime().before(end))
		{
			total += shifts.get(x).getTotalTime();
			x++;
		}
		
		return total;
	}
	
	//Returns the number of hours worked on a specified date
	//day must be a value starting at 00:00
	public double getHoursWorked(Calendar day)
	{
		long total = 0;
		int x = 0;
		
		//Get to the first date within the specified date
		while(shifts.get(x).getStartTime().before(day)) x++;
		//Start adding dates while within the range until we exit the range
		while(shifts.get(x).getStartTime().getTimeInMillis() < (day.getTimeInMillis()+(24*60*60*1000)))
		{
			total += shifts.get(x).getTotalTime();
			x++;
		}
		
		return total;
	}
	
	public Vector<Shift> getShiftsBetween(Calendar start, Calendar end)
	{
		if(shifts.size() > 0)
		{
			Vector<Shift> result = new Vector<Shift>();
			int x = 0;
			
			//Get to the first date within the specified date
			while(x < shifts.size() && shifts.get(x).getStartTime().before(start)) x++;
			//Start adding dates while within the range until we exit the range
			while(x < shifts.size() && shifts.get(x).getStartTime().before(end))
			{
				result.add(shifts.get(x));
				x++;
			}
			
			return result;
		}
		return null;
	}
	
	/**
	 * Returns the first shift that 
	 * @param date
	 * @return
	 */
	public Shift getShiftAt(Calendar date)
	{
		if(date == null)
			return null;
		if(shifts.size() > 0)
		{
			int x = 0;
			
			//Get to the first date within the specified date
			if(currentShift != null && currentShift.getStartTime().before(date) && (currentShift.getEndTime() == null || currentShift.getEndTime().after(date)))
				return currentShift;
			
			while(x < shifts.size())
			{
				if(shifts.get(x).getStartTime().before(date) && (shifts.get(x).getEndTime() == null || shifts.get(x).getEndTime().after(date)))
					return shifts.get(x);
				else
					x++;
			}			
		}
		return null;
	}
	
	//"Times in" an employee
	public boolean timeIn(Calendar in)
	{
		if(!working)
		{
			currentShift = new Shift(in);
			working = true;
			return true;
		}
		return false;
	}
	
	//"Times out" an employee
	public boolean timeOut(Calendar out)
	{
		if(working && out.after(currentShift.getStartTime()))
		{
			currentShift.setEndTime(out);
			working = false;
			addShift(currentShift.getStartTime(),currentShift.getEndTime());
			currentShift = null;
			return true;
		}
		return false;		
	}
	
	private void addShift(Calendar start, Calendar end)
	{
		int x = 0;
		while(x < shifts.size() && start.after(shifts.get(x).getStartTime()))x++;
		shifts.add(x, new Shift(start,end));
		System.out.println("Added new completed shift at position " + x + ": "+shifts.get(x).toString());
	}
	
	
	//ADMINISTRATOR LEVEL FUNCTIONS-----------------------------------------------------
	
	/**
	 * Used to check the total accumulated hours an employee has worked
	 * @return total of number of hours worked on record for this empoyee
	 */
	public double getTotalHoursWorked()
	{
		long total = 0;
		int x = 0;
		//Start adding dates while within the range until we exit the range
		while(x < shifts.size())
		{
			total += shifts.get(x).getTotalTime();
			x++;
		}
		
		return total;
	}
	
	/**
	 * Allows an administrator to override a shift's recorded starting and ending times
	 * @param shiftNumber	The index of the selected shift to change
	 * @param startTime		The start time will be set to this value
	 * @param endTime		The end time will be set to this value
	 * @return 0 for successfully adjusted
	 * 		  -1 for unsuccessfully adjusted (reasons yet to be determined) 
	 */
	public boolean overrideTime(int shiftNumber, Calendar startTime, Calendar endTime)
	{
		return shifts.get(shiftNumber).setStartEndTime(startTime, endTime);		
	}
	
	/**
	 * Sets the employee's employment status
	 * @param state True for employed, false for removed
	 */
	public void setEmploymentStatus(boolean state)
	{
		if(!working)
			employed = false;
	}
	
	public boolean reopenShift(Shift shift)
	{
		if(!working)
		{
			shift.setEndTime(null);
			working = true;
			return true;
		}
		JOptionPane.showMessageDialog(null,"Error: Please close any other open shifts before re-opening this shift.");
		return false;
		
	}
	
	public boolean removeShift(Shift shift)
	{
		boolean success = false;
		if(shift.getEndTime() == null)
		{
			working = false;
			currentShift = null;
			success = true;
		}
		else{
			for(int x = 0; x < shifts.size(); x++)
			{
				Shift temp = shifts.get(x);
				if(temp == shift)
				{
					shifts.remove(x);
					success = true;
				}
			}
		}
		return success;	
	}
	
	
	//Setters&Getters
	
	public boolean getWorkingStatus(){
		return working;
	}
	
	public boolean getEmployedStatus(){
		return employed;
	}
	
	public int getEmpNumber(){
		return empNumber;
	}
	
	public String getFirstName(){
		return firstName;
	}
	
	public String getLastName(){
		return lastName;
	}
	
	public String getLoginCode(){
		return loginCode;
	}
	
	public int getAuthLevel(){
		return authLevel;
	}	
	
	public Shift getCurrentShift(){
		return currentShift;
	}
}
