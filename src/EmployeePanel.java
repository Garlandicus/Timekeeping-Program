import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.table.AbstractTableModel;

import Data.Employee;
import Data.Shift;
import Data.TimeController;


public class EmployeePanel extends JPanel implements ActionListener {

	protected TimeController timeMan;
	protected Employee currentEmployee;
	protected EmployeeTableModel tableModel;
	
	protected final String[] columnRef  = {"Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"};
	protected String[]  tableColumnNames = {"-","","","","","","",""};
	protected Object[][] tableData;
	protected Calendar firstDay;
	protected int startingDay;
	protected final int tableRows = 7;
	protected final int tableColumns = 8;
	
	protected JButton bTimeIn;
	protected JButton bTimeOut;
	protected JTable employeeTable;
	protected JLabel totalHoursLabel;
	
	protected JPanel timeInControls;
	
	public EmployeePanel(TimeController t)
	{
		super(new BorderLayout());
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		timeMan = t;
		
		tableModel = new EmployeeTableModel();
		firstDay = null;
		startingDay = 0;
			setStartingDay(startingDay);
		
		timeInControls = new JPanel();
			timeInControls.setLayout(new BoxLayout(timeInControls, BoxLayout.LINE_AXIS));
		
		bTimeIn = new JButton("Time In");
			bTimeIn.setActionCommand("timeIn");
			bTimeIn.addActionListener(this);
		
		bTimeOut = new JButton("Time Out");
			bTimeOut.setActionCommand("timeOut");
			bTimeOut.addActionListener(this);		

		totalHoursLabel = new JLabel("Time worked this week: ");
			
		timeInControls.add(bTimeIn);
			timeInControls.add(Box.createRigidArea(new Dimension(20,0)));
			timeInControls.add(bTimeOut);
			timeInControls.add(Box.createRigidArea(new Dimension(20,0)));
			timeInControls.add(totalHoursLabel);
			
		tableData = new Object[tableRows][tableColumns];
		employeeTable = new JTable(tableData, tableColumnNames);
			employeeTable.setFillsViewportHeight(true);
			employeeTable.setModel(tableModel);
			
		JScrollPane scrollPane = new JScrollPane(employeeTable);
			scrollPane.setMaximumSize(new Dimension(1000,500));
			scrollPane.setBorder(BorderFactory.createCompoundBorder(new EmptyBorder(10, 10, 10, 10),  new EtchedBorder()));
			
		add(timeInControls);		
		//add(employeeTable.getTableHeader());
		add(scrollPane);
		
	}
	
	//Requires a new loadEmployee to apply the changes to tableData
	public void setStartingDay(int newDay)
	{
		for(int x = 0; x < 7; x++)
		{
			tableColumnNames[x+1] = columnRef[(newDay+x)%7];
		}
	}
	
	public void clearTableData()
	{
		for(int y = 0; y < tableRows; y++)
			for(int x = 0; x < tableColumns; x++)
				tableData[y][x] = null;
	}
	
	
	//TODO: Fix this to load employees depending on the starting day of the week rather than arbitrary column placement
	public void loadEmployee(Employee e)
	{
		clearTableData();
		currentEmployee = e;
		
		GregorianCalendar from = new GregorianCalendar();
		if(from.get(Calendar.DAY_OF_WEEK) != startingDay+1)
			from.add(Calendar.DAY_OF_MONTH, -7 + from.get(Calendar.DAY_OF_WEEK)-1+startingDay);
		GregorianCalendar to = ((GregorianCalendar)from.clone());
				to.add(Calendar.DAY_OF_MONTH, 6);
		firstDay = from;
		
		for(int c = 0; c < 7; c++)
		{
			employeeTable.getTableHeader().getColumnModel().getColumn(c+1).setHeaderValue(""+
					from.get(Calendar.MONTH)+"/"+
					(from.get(Calendar.DAY_OF_MONTH)+c)+"/"+
					from.get(Calendar.YEAR)+" - "+
					tableColumnNames[c+1]);
		}
		
		
		
		for(int r = 0; r < tableRows-2; r+=2)
		{
			tableData[r][0] = "IN:";
			tableData[r+1][0] = "OUT:";
		}
		tableData[tableRows-1][0] = "TOTAL:";
		System.out.println("Polling for a list of shifts between " + from.get(Calendar.YEAR) + "/"+from.get(Calendar.MONTH)+"/"+from.get(Calendar.DAY_OF_MONTH));
		System.out.println("and " + to.get(Calendar.YEAR) + "/"+to.get(Calendar.MONTH)+"/"+to.get(Calendar.DAY_OF_MONTH));
		Vector<Shift> employeeShifts = e.getShiftsBetween(from, to);
		for(int x = 0; x < employeeShifts.size(); x++)
		{
			Shift s = employeeShifts.get(x);
			System.out.println("Adding shift "+x+" to table: " + s.toString());
			int row = 0;
			int col = 0;
			switch(s.getStartTime().get(Calendar.DAY_OF_WEEK)){
			case(Calendar.SUNDAY):
				col = 1; break;
			case(Calendar.MONDAY):
				col = 2; break;
			case(Calendar.TUESDAY):
				col = 3; break;
			case(Calendar.WEDNESDAY):
				col = 4; break;
			case(Calendar.THURSDAY):
				col = 5; break;
			case(Calendar.FRIDAY):
				col = 6; break;
			case(Calendar.SATURDAY):
				col = 7; break;
			}
			while(tableData[row][col] != null)
			{
				row++;
			}
			tableModel.writeValueAt("" + s.getStartTime().get(Calendar.HOUR_OF_DAY) + ":" + s.getStartTime().get(Calendar.MINUTE),row,col);
			tableModel.writeValueAt("" + s.getEndTime().get(Calendar.HOUR_OF_DAY) + ":" + s.getEndTime().get(Calendar.MINUTE),row+1,col);
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}	
	
	public class EmployeeTableModel extends AbstractTableModel{

		/**
		 * EmployeeTableModel
		 * Used for managing the employee time table
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public int getColumnCount() {
			// TODO Auto-generated method stub
			return tableColumns;
			
		}

		@Override
		public int getRowCount() {
			// TODO Auto-generated method stub
			return tableRows;
		}

		@Override
		public Object getValueAt(int arg0, int arg1) {
			// TODO Auto-generated method stub
			return tableData[arg0][arg1];
		}
		
		public String getColumnName(int col) {
	        return tableColumnNames[col];
	    }
	    
		//================================Editing Table Values
		/**
		 * Determines whether a cell should be editable
		 */
		public boolean isCellEditable(int row, int col) {
	        //Note that the data/cell address is constant,
	        //no matter where the cell appears onscreen.
	        if ((col > 0) && tableData[row][col]!=null && row < tableRows-1) {
	            return true;
	        } 
	        else if(tableData[row][col] == null && (row == 0 || tableData[row-1][col] != null)) {
	        	return true;
	        }
	        return false;
	    }
		
		/**
		 * Less safe version of setValueAt, this is for read-only operations such as loading information from the employee's shift data for initial display
		 * @param value The value to be displayed
		 * @param row	The row index that the value will be written to
		 * @param col	the column index that the value will be written to
		 */
		public void writeValueAt(Object value, int row, int col){
			tableData[row][col] = validateData(value);
			updateTotal(col);
			fireTableCellUpdated(row, col);
		}
		
		/**
		 * More safe version of writeValueAt, this function is used to validate and apply changes made to the table and employee shifts through user input
		 * @param value The value to be displayed
		 * @param row	The row index that the value will be written to
		 * @param col	the column index that the value will be written to
		 */
		public void setValueAt(Object value, int row, int col) {
						
			String time = validateData(value);
			Calendar date = null;
				if(time != null)
					date = codeToCalendar(time, col);
			
			boolean success = false;
			
			//Additive Changes
			if(date != null)
			{
				Shift currentShift = currentEmployee.getShiftAt(date);
				//If the employee is timing in to a new shift
				if(currentShift == null && tableData[row][col] == null && row%2 == 0)
				{
					success = currentEmployee.timeIn(date);
				}
				
				//If they are changing the start time of a shift
				else if(currentShift != null && tableData[row][col] != null && row%2 == 0)
				{
					success = currentShift.setStartTime(date);
				}
				
				//If they are ending a shift
				else if(currentShift != null && tableData[row][col] == null && row%2 == 1)
				{
					success = currentEmployee.timeOut(date);
				}
				
				//If they are changing the end time of a shift
				else if(currentShift != null && tableData[row][col] != null && row%2 == 1)
				{
					success = currentShift.setEndTime(date);				
				}
			}
			//If date is null, we are re-opening a shift or deleting a shift entirely
			else
			{
				Shift currentShift = getShift(row, col);
				//Reopen a shift, accidental time out
				if(row%2 == 1)
				{
					success = currentEmployee.reopenShift(currentShift);
				}
				//Delete a shift
				else if(row%2 == 0)
				{
					success = currentEmployee.removeShift(currentShift);
				}
			}
			
			//If things check out in the employee class, we're good to make the relevant changes to the table
			
			if(success)
			{
				tableData[row][col] = time;
				updateTotal(col);
				fireTableCellUpdated(row, col);
			}
			
			
			
			
			
			//Moving most of the below to the Employee Class for more proper encapsulation
			
			/*
			//Short Circuiting for when the user mistakenly edits a cell and clears it or does not enter anything
			if(value == null && tableData[row][col] == null) 
				return;
			else
				System.out.println("Value:"+value);
			
			if((col > 0) && row < tableRows-1)
			{
				String time = validateData(value);
				
				if(time != null)
				{
					//If they are attempting to change the time an employee times out, check the time they timed in first.
					if(time != "")
					{
						if((row%2 == 1) && timeToCode(time) < timeToCode(tableData[row-1][col]))
						{
							JOptionPane.showMessageDialog(null,"Error: The time entered is before the employee clocked in!");
							return;
						}
						else if((row%2 == 0))
						{
							if(tableData[row+1][col] != null && timeToCode(time) > timeToCode(tableData[row+1][col]))
							{
								JOptionPane.showMessageDialog(null,"Error: The time entered is after the employee clocked out!");
								return;
							}
							else if(row > 0 && timeToCode(time) < timeToCode(tableData[row-1][col]))
							{
								JOptionPane.showMessageDialog(null,"Error: The time entered is before the employee last clocked out!");
								return;
							}
						}
					}	
					if(time == "")
						time = null;
			        tableData[row][col] = time;
			        
			        updateShift(row,col);
			        
			        
				}
			}
			*/
	    }
		
		/**
		 * Updates the "Total" row in the specified column to reflect the total number of hours worked. Called when setValueAt is called.
		 * @param col
		 */
		public void updateTotal(int col)
		{
			int totalHours = 0;
			int totalMinutes = 0;
			
			int currentRow = 0;

			
			while(currentRow < tableRows-1 && tableData[currentRow][col] != null && tableData[currentRow+1][col] != null)
			{
				//Elaborated for visibility purposes
				int hourIn = timeToCode(tableData[currentRow][col])/100;
				int minIn = timeToCode(tableData[currentRow][col])%100;
				
				int hourOut = timeToCode(tableData[currentRow+1][col])/100;
				int minOut = timeToCode(tableData[currentRow+1][col])%100;
				
				totalHours += hourOut-hourIn;
				totalMinutes += minOut-minIn;
						
				currentRow+=2;
			}
			
			//Dealing with negative minutes
			if(totalMinutes < 0)
			{
				totalHours = totalHours + (-1 + totalMinutes/60);
				totalMinutes = (totalMinutes%60) + 60;
			}
			
			//Dealing with an excess of minutes
			if(totalMinutes > 60)
			{
				totalHours = totalHours + totalMinutes / 60;
				totalMinutes = totalMinutes%60;
			}
			
			//If everything checks out, send the resulting validated string
			if(totalMinutes < 10)
				tableData[tableRows-1][col] = ""+totalHours+":0"+totalMinutes;
			else
				tableData[tableRows-1][col] = ""+totalHours+":"+totalMinutes;
			
			fireTableCellUpdated(tableRows-1, col);
			
			//Update the label for total hours per week as well
			updateTotalWeekHours();
			
		}
		
		public void updateTotalWeekHours(){
			int totalHours = 0; 
			int totalMinutes = 0;
			for(int x = 1; x < tableColumns; x++)
			{
				if(tableData[tableRows-1][x]!=null)
				{
					totalHours += timeToCode(tableData[tableRows-1][x])/100;
					totalMinutes += timeToCode(tableData[tableRows-1][x])%100;
				}
			}
			
			//Dealing with an excess of minutes
			if(totalMinutes > 60)
			{
				totalHours = totalHours + totalMinutes / 60;
				totalMinutes = totalMinutes%60;
			}
		
			if(totalMinutes < 10)
				totalHoursLabel.setText("Time worked this week: " + totalHours + ":0" + totalMinutes);
			else
				totalHoursLabel.setText("Time worked this week: " + totalHours + ":" + totalMinutes);
			
		}
		
		/**
		 * Converts a String time stored in the EmployeeTable to an integer code HHMM, for comparison purposes
		 * @param time: The value to be converted
		 * @return The value of time converted into an integer format for comparison, following the form HHMM, H=hour M=minute
		 */
		public int timeToCode(Object tableData)
		{
			int hours = -1;
			int minutes = -1;
			boolean afternoon = false;
			
			try {
				//Convert the given time to an actual time
				if(tableData.toString().contains("PM") || tableData.toString().contains("pm"))
					afternoon = true;
				String temp = tableData.toString().replaceAll("[A-Za-z]*","").trim();
				
				hours = Integer.parseInt(temp.substring(0,temp.indexOf(':')));
				if(afternoon && hours <= 12)
					hours = hours + 12;
				minutes = Integer.parseInt(temp.substring(temp.indexOf(':')+1,temp.length()));
			}
			catch(Exception e)
			{
				JOptionPane.showMessageDialog(null,"Not a valid time entry, should follow the form HH:MM [AM/PM], such as 1:30 PM for 13:30");
				return -1;
			}
			if(hours < 0 || hours > 23 || minutes < 0 || minutes > 59)
				return -1;
			return hours*100+minutes;
		}
		
		public Calendar cellToCalendar(int row, int col){
			int hour = timeToCode(tableData[row][col])/100;
			int minute = timeToCode(tableData[row][col])%100;
			
			return new GregorianCalendar(firstDay.get(Calendar.YEAR),
					   firstDay.get(Calendar.MONTH),
					   firstDay.get(Calendar.DAY_OF_MONTH)+col,
					   hour, minute);
		}
		
		public Calendar codeToCalendar(String code, int dayOfWeek){
			int hour = timeToCode(code)/100;
			int minute = timeToCode(code)%100;
			
			return new GregorianCalendar(firstDay.get(Calendar.YEAR),
					   firstDay.get(Calendar.MONTH),
					   firstDay.get(Calendar.DAY_OF_MONTH)+dayOfWeek,
					   hour, minute);
		}
		
		public Shift getShift(int row, int col){
			
			int startHour = 0;
			int startMinute = 0;
			int finishHour = 23;
			int finishMinute = 59;
			
			if(row%2 == 0)
			{
				startHour = timeToCode(tableData[row][col])/100;
				startMinute = timeToCode(tableData[row][col])%100;
				
				if(tableData[row+1][col] != null)
				{
					finishHour = timeToCode(tableData[row+1][col])/100;
					finishMinute = timeToCode(tableData[row+1][col])%100;
				}
				
			}
			else
			{	
				startHour = timeToCode(tableData[row-1][col])/100;
				startMinute = timeToCode(tableData[row-1][col])%100;
				finishHour = timeToCode(tableData[row][col])/100;
				finishMinute = timeToCode(tableData[row][col])%100;
			}
			
			Vector<Shift> found = currentEmployee.getShiftsBetween(new GregorianCalendar(firstDay.get(Calendar.YEAR),
																   firstDay.get(Calendar.MONTH),
																   firstDay.get(Calendar.DAY_OF_MONTH)+col,
																   startHour, startMinute),
										     new GregorianCalendar(firstDay.get(Calendar.YEAR),
																   firstDay.get(Calendar.MONTH),
																   firstDay.get(Calendar.DAY_OF_MONTH)+col,
																   finishHour, finishMinute));
			if(found.size() > 0)
				return found.get(0);
			return null;
		}
		
		public String validateData(Object value){
			int hours = -1;
			int minutes = -1;
			boolean afternoon = false;
			
			//If they're trying to clear the cell, clear the cell.
			if (value.toString().length() == 0)
				return "";
			
			try {
				
				//Convert the given time to an actual time
				if(value.toString().contains("PM") || value.toString().contains("pm"))
					afternoon = true;
				String temp = value.toString().replaceAll("[A-Za-z]*","").trim();
				
				hours = Integer.parseInt(temp.substring(0,temp.indexOf(':')));
				if(afternoon && hours <= 11)
					hours = hours + 12;
				minutes = Integer.parseInt(temp.substring(temp.indexOf(':')+1,temp.length()));
			}
			catch(Exception e)
			{
				JOptionPane.showMessageDialog(null,"Not a valid time entry, should follow the form HH:MM [AM/PM], such as 1:30 PM for 13:30.");
				return null;
			}			
			if(hours < 0 || hours > 23 || minutes < 0 || minutes > 59)
			{
				JOptionPane.showMessageDialog(null,"Not a valid time entry, time cannot be negative or greater than 23:59 or 11:59 PM.");
				return null;
			}
			
			//If everything checks out, send the resulting validated string
			if(minutes < 10)
				return ""+hours+":0"+minutes;	
			return ""+hours+":"+minutes;
			
		}
		
		
		public void updateShift(int row, int col){
			//find the relevant shift
			//TODO:Finish fixing this function, rework whatever needs to be tweaked, shift some more functionality into employee or timecontroller. Whatever.
			Shift shift = getShift(row,col);
			//if(shift.getStartTime().before())
		}
	}
	
	
}
