CP1: 
# MotorPH Payroll System
payroll application that handles user authentication, employee data retrieval, and automated salary calculations including Philippine statutory deductions (SSS, PhilHealth, Pag-IBIG) and Withholding Tax.

Members of Group 5:  
*Mitzi Reese Arrogante (Lead Developer, Technical Documentation);  
*Chantal Louise Flor (Project Lead, Tester);  
*Maricel Canaveral (Technical Documentation);  
*Rhayn Lee Hann Suarez (Tester);  
*Hanna Jane Dalendeg 

**How to run the project**   
1. Open in Netbeans by selecting file, open project "MotorPHPayroll" folder.
2. To make sure that you are in the right class, please right click project, then go to Properties, and then Run and click Browse next to main class, and select "motorphpayroll.MotorPHPayroll".
3. Clean and build by right clicking the project after clicking the class, and select Clean and Build.
4. Click the green Play button. 

**Features:**   
1. **Imports**   
import java.io.BufferedReader (Week 10: To read files line by line efficiently);  
import java.io.FileReader (Week 10: To open the connection to the .csv file);  
import java.io.IOException (Week 10: Handles potential input/output failures, such as a missing file or a restricted directory);   
import java.util.Scanner (Week 8: To capture user input from the console);  
import java.time.LocalTime (Week 7/8: Represents a time (HH:mm) used for time-based logic (grace periods));  
import java.time.format.DateTimeFormatter (Week 7: Standardizes how time is displayed or parsed, ensuring user input matches the required 24 hour pattern);  
import java.util.ArrayList (For scalability - To use a list that will be automatically grow size if we add more records in csv files);  
import java.util.List (To ensure any list we create follows the same standard rules for adding or removing data);  

2. **Files**  
Resources: Reads from the attendance.csv and the employee.csv files  
private static final String ATTENDANCE_FILE = "resources/MotorPH_Employee Data - Attendance Record.csv";(Relative path for clock-in and clockout of MotorPH employees);  
private static final String EMPLOYEE_FILE = "resources/MotorPH_Employee Data - Employee Details.csv";(Relative path for personal information and salary rates of MotorPH employees);  

3. **Main** - starts with a login gate that supports two user types:  
Employee: Can view their own basic profile details.  
Payroll Staff: Can process payroll for a specific individual or the entire company.  
Using Logical Operators (&& and ||) for conditional checking that sends user to the correct department  
There are also functions: handleEmployeeFlow & handlePayrollStaffFlow that separates the user experience based on the login role.  

  - Username: employee or payroll_staff  
  - Password: 12345

4. **handleEmployeeFlow** - controls the menu loop for regular employees, allowing them to input an ID number to view their personal profile details.
Uses a while(true) loop to keep the employee menu active until they exit.
- Employee Number  
- Employee Name  
- Birthday  

5. **findEmployeeData** - search utility that opens a specific CSV file for a matching Employee ID. 
private static String findEmployeeData(String path, String id) {
    try (BufferedReader br = new BufferedReader(new FileReader(path))) {
        String line;
        while ((line = br.readLine()) != null) {
            String[] columns = smartSplit(line);
Implements BufferedReader and FileReader to search through external files.

6. **smartSplit** -  ensures that commas inside quotation marks (like in an address or a name) are treated as text rather than column separators.
 private static String[] smartSplit(String line) {
        String[] results = new String[30]; 
        StringBuilder tempText = new StringBuilder();
        int columnNumber = 0;
        boolean inQuotes = false;
Uses a for loop and char analysis to parse CSV data manually.
Unlike a simple split(","), this custom method uses a logic loop to handle CSV files where data (like names or addresses) might contain commas inside quotation marks.

7. **fullName** - helper for combining first and last name, if statement.  

8. **handlePayrollStaffFlow** - manages the primary navigation menu for Payroll Staff, providing options to enter the payroll processing section or exit the system. Control flow switch/if.  

9. **processPayrollMenu** - A sub-menu for staff that allows for the selection of either "One employee" or "All employees" when generating payroll reports. Payroll history shall show up. Nested control flow. Infinite while loop that runs until user selects 3 to break.

10. **processPayroll** -  reads the entire employee.csv file and automatically runs the calculatePayrollto identify if it is for specific employee or all employees which will then trigger the payroll calculation. 

11. **findWorkingPeriods** - scans the attendance.csv file to return the month-year combination. This method assist in knowing the number of payslips as it looks at the date (month and year).  
    
12. **monthName** - utility that helps convert numeric month inputs (e.g., "06") into their corresponding names (e.g., "June").
Uses switch expression
private static String monthName(String monthStr) {
        int month = Integer.parseInt(monthStr);
        return switch (month) {  

13. **calculateShift** - Determines the total hours worked for a single day. It applies the 8:10 AM grace period, sets a 5:00 PM logout limit, and subtracts 1 hour for lunch. Time and operators. LocalTime for grace period.  

14. **findAttendancedata** - dynamic container that holds multiple records for one ID. This is a collection of group of data points. This methods assist in calculating exact hours worked.  

15. **hourlyRate** - helper for try-catch block to prevent data entry errors.  

16. **computeSSS** - determines the SSS contribution by matching the monthly gross income against the official Philippine SSS contribution brackets.

17. **computePhilHealth** - calculates the PhilHealth premium based on a 3% rate, then returns the 50% employee share. Arithmetic operators to calculate the salary and rate; then divide the 50/50 share.  

18. **computePagIBIG** - calculates the Pag-IBIG contribution (1% or 2%) based on the salary amount. Logic operators.    

19. **calculateWithholdingTax** - applies the BIR graduated tax table to the taxable income (Gross minus SSS, PhilHealth, and Pag-IBIG) to calculate the monthly withholding tax.
Used nested arithmetic and conditions

20. **calculatePayroll** -  core logic method. It calculates the bi-monthly gross, applies all government deductions, and prints a detailed payslip to the console.
.replace(",", "") and Double.parseDouble to clean and convert text into math-ready numbers.

21. **System.exit** - the termination command used throughout the program to safely close the application when the user chooses to exit. 

- main → handleEmployeeFlow or handlePayrollStaffFlow  
- if handleEmployeeFlow → calls findEmployeeData → calls fullName (shows Employee Number, Employee Name, Birthday)  
- if handlePayrollStaffFlow → processPayrollMenu → processPayroll  
- processPayroll → findWorkingPeriods (when employee worked) → findAttendanceData (get logs) and smartSplit (csv commas) → calculatePayroll  
- calculatePayroll → hourlyRate and smartSplit → calculateShift and monthName → computeSSS and computePhilHealth and computePagIBIG and calculateWithholdingTax
- System.exit     

**Syllabus Covered**  
Variables & Operators: For salary arithmetic and tax tiering.  
Control Structures: switch expressions for months and if-else for tax brackets.  
Methods: Modularized logic for clean, reusable code.  
File Handling: Used BufferedReader and FileReader implementation.  
Java Time API: LocalTime and DateTimeFormatter for precise attendance tracking.
ArrayList. Wrapper Classes. 

**Notes**  
The CSV files are located in the resources/ folder.  
Year: For the 2024 attendance data  
Salary and Deductions calculators from Phase 1 MotorPH (SSS, PhilHealth, PagIBIG, Withholdingtax)  
Payroll cutoff (1-15) displays gross salary and net salary (no deductions)  
Payroll cutoff (16-31) displays gross salary and applies all monthly deductions to calculate the final Net Salary.  

**Additional Revision Notes after consultation (March 7, 2026)**
1. Integrated a file path for findAttendanceData to ensure stable access to the attendance CSV/source file.  
2. Single Employee View: Removed the month selection requirement. Searching for an individual now displays their entire payroll history across all months.  
Process All: Maintained the month-based filter for bulk processing to allow for standard monthly payroll runs.  
3. Allowance Removal: General allowances have been removed to focus strictly on the core earnings formula.  
4. Formula Standardization for Total Hours Worked, Gross Salary, and Net Salary.  
5. Made sure to add error messages for Try-Catch.

**Additional Revision Notes after consultation (March 16, 2026)**
1. Added csv code for file handling to ensure program can find and open the files.
2. Added a try-catch month validation for processAll months so that it will block if user types 13, etc.
3. Descriptive naming variables.
4. While-loop for deduction logic so that there would not be +45 lines especially for SSS (looked for pattern). 
5. DRY codes such as fullName (single method for lastName and firstName), and findWorkingPeriods (detect months rather than hardcoded loop).
6. added comments with param and return as per Javadoc.
7. Refactor payroll logic for scalability by removing the hard-coded months and added the year filter.

**Note on File Structure:** Project restored on March 26, 2026 5AM due to environmental error and crash. Attempts were made to reorganize and clean the project files via Github. However, this led to several dependency and pathing issues. To maintain stability of the build, the file structure has been left in its original state. 

[Project Plan](https://docs.google.com/spreadsheets/d/1rbrQGOejCtMpRpwfM78M2QLEYzDD2OVu1BiteN0ZaC8/edit?usp=sharing)   
[Excel Test for calculatePayroll](https://docs.google.com/spreadsheets/d/1lgwjecejDZlg4Ws7lmHo8aKcW4ddLghxu1lab4PAMwg/edit?usp=sharing)  
[QA Test from Group 6](https://docs.google.com/spreadsheets/d/1_NJbuYLilaNfLnaERIAPnRERlTXDbpk0E6Riatdu8ks/edit?usp=sharing)  




