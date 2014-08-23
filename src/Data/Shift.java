package Data;

import java.util.Calendar;
import java.util.Date;

public class Shift {

	//Variables
	Calendar startTime;
	Calendar endTime;
	
	public Shift(Calendar start, Calendar end)
	{
		startTime = start;
		endTime = end;
	}
	
	public Shift(Calendar start)
	{
		startTime = start;
		endTime = null;
	}
	
	public long getTotalTime(){
		if(endTime != null)
			return endTime.getTimeInMillis()-startTime.getTimeInMillis();
		return -1;
	}
	
	public Calendar getStartTime(){
		return startTime;
	}
	
	public Calendar getEndTime(){
		if(endTime != null)
			return endTime;
		return null;
	}
	
	public boolean setStartTime(Calendar start){
		if(endTime == null || start.compareTo(endTime) < 0)
			startTime= start;
		else
			return false;
		return true;
	}
	
	public boolean setEndTime(Calendar end){
		if(end.compareTo(startTime) >= 0)
			endTime = end;
		else
			return false;
		return true;
	}
	
	public boolean setStartEndTime(Calendar start, Calendar end)
	{
		if(endTime == null || start.compareTo(endTime) < 0)
			startTime= start;
		else
			return false;
		if(end.compareTo(startTime) > 0)
			endTime = end;
		else
			return false;
		return true;
	}
	
	public String toString()
	{
		if(startTime == null)
			return "[Unstarted Shift]";
		else if(endTime == null)
			return "[Current Shift: "+printCalendar(startTime)+"]";
		else
			return "[Completed Shift: "+printCalendar(startTime) + " to " + printCalendar(endTime)+"]";
	}
	
	public String printCalendar(Calendar c)
	{
		String value = "" + c.get(Calendar.YEAR)+"\\"+c.get(Calendar.MONTH)+"\\"+c.get(Calendar.DAY_OF_MONTH) + " " + c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE);
		return value;
	}
}
