/**
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */

package motorphpayroll;

/**
 * MotorPH Payroll System
 * Syllabus Topics Covered: 
 * Variables, Operators, Control Structures, Methods, File Handling
 * JavaTime API (handles clock times), ArrayList (for scalability), Wrapper Classes (turn texts into numbers)
 * Group 5 H1101 MO-IT101
 * adding param and return tags based on Oracle. How to Write Doc Comments for the Javadoc Tool. Baeldung. How to Document Generic Type Parameters in Javadoc.
 */

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class MotorPHPayroll {
    
    // File paths updated with .csv extension 
    private static final String ATTENDANCE_FILE = "resources/MotorPH_Employee Data - Attendance Record.csv";
    private static final String EMPLOYEE_FILE = "resources/MotorPH_Employee Data - Employee Details.csv";

    /**
     * Main entry point. Handles login authentication and system navigation.
     * using Logical Operators (&& and ||).
     * @param args Standard command-line arguments.
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== MotorPH Payroll System Login ===");
        System.out.print("Enter Username: ");
        String user = scanner.nextLine();
        
        System.out.print("Enter Password: ");
        String pass = scanner.nextLine();
        
        // Logical Operators based on role-based access
        if (pass.equals("12345") && (user.equals("employee") || user.equals("payroll_staff"))) {
            if (user.equals("employee")) {
                handleEmployeeFlow(scanner);
            } else {
                handlePayrollStaffFlow(scanner);
            }
        } else {
            System.out.println("Incorrect username and/or password.");
            System.exit(0); 
        }
        
        scanner.close();
    }

    /**
     * Manages the workflow for an Employee user.
     * Control Structures (while loops)
     * @param scanner - used for capturing user input.
     */
    public static void handleEmployeeFlow(Scanner scanner) {
        while (true) {
            System.out.println("\nDisplay options:");
            System.out.println("1. Enter your employee number");
            System.out.println("2. Logout");
            System.out.print("Choice: ");
            String choice = scanner.nextLine();

            if (choice.equals("1")) {
                System.out.print("Enter Employee Number: ");
                String id = scanner.nextLine();
                String data = findEmployeeData(EMPLOYEE_FILE, id);
                if (data != null) {
                    String[] emp = smartSplit(data);
                    System.out.println("\n[ Employee Details ]");
                    System.out.println("a. Employee Number: " + emp[0]);
                    System.out.println("b. Employee Name: " + fullName(emp));
                    System.out.println("c. Birthday: " + emp[3]);
                } else {
                    System.out.println("The employee number does not exist.");
                }
            } else if (choice.equals("2")) {
                System.exit(0);
 
            }
        }
    }

    /**
     * Searches for a specific employee ID within the Employee Details file.
     * Includes error handling for missing files or read errors.
     * Baeldung. Exception Handling in Java. 4.4 try-with-resources and 4.5. multiple catch (file not found/ file corrupted)
     * @param path - relative file path to the Employee Details CSV.
     * @param id Employee ID.
     * @return the raw line of data for the employee, or null if not found.
     */
    private static String findEmployeeData(String path, String id) {
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] columns = smartSplit(line);
                if (columns.length > 0 && columns[0] != null && columns[0].trim().equals(id.trim())) {
                    return line;
                }
            }
        } catch (java.io.FileNotFoundException e) {
            System.out.println("Error: The file '" + path + "' was not found.");
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
        return null;
    }

    /**
     * CSV Parser. Baeldung CSV File into Array 6.1
     * Iterates character by character to handle commas inside quotes.
     * Uses dynamic ArrayList. Baeldung. Guide to the Java ArrayList
     * @param line - single raw line of text from the CSV file.
     * @return the String array where each element represents a specific column.
     */
    private static String[] smartSplit(String line) {
        if (line == null || line.isEmpty()) return new String[0];
        List<String> results = new ArrayList<>();
        StringBuilder tempText = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char currentCharacter = line.charAt(i);
            if (currentCharacter == '\"') {
                inQuotes = !inQuotes; 
            } else if (currentCharacter == ',' && !inQuotes) {
                results.add(tempText.toString().trim());
                tempText.setLength(0); 
            } else {
                tempText.append(currentCharacter);
            }
        }
        results.add(tempText.toString().trim());
        
        return results.toArray(new String[0]);
    }

    /**
     * Helper to combine first and last name.
     * Centralizes name formatting and avoids repeating array indices. 
     * @param emp - Array of employee strings.
     * @return Formatted full name.
     */
    private static String fullName(String[] emp) {
        if (emp.length < 3) return "Unknown";
        
        return emp[2] + " " + emp[1]; 
    }

    /**
     * Manages the workflow for Payroll Staff.
     * @param scanner The Scanner object used for capturing user input.
     */
    public static void handlePayrollStaffFlow(Scanner scanner) {
        while (true) {
            System.out.println("\n--- PAYROLL STAFF OPTIONS ---");
            System.out.println("1. Process Payroll\n2. Logout");
            System.out.print("Choice: ");
            String choice = scanner.nextLine();

            if (choice.equals("1")) {
                processPayrollMenu(scanner);
            } else if (choice.equals("2")) {
                System.exit(0);
            }
        }
    }

    /**
     * Sub-menu to choose between Individual history or Bulk runs.
     * nested control structures
     * Individual view now shows all months (June-Dec) automatically but process all let users choose month
     * Baeldung. Validating User Input in Java.
     * @param scanner The Scanner object used for capturing user input.
     */
    public static void processPayrollMenu(Scanner scanner) {
        while (true) {
            System.out.println("\n--- PROCESS PAYROLL ---");
            System.out.println("1. One employee \n2. All employees \n3. Back");
            System.out.print("Choice: ");
            String choice = scanner.nextLine();

            if (choice.equals("1")) {
                System.out.print("Enter employee number: ");
                String id = scanner.nextLine();
                // Only for a specific ID
                processPayroll(id); 
                
            } else if (choice.equals("2")) {
                // Calls null to process everyone
                processPayroll(null); 
                
            } else if (choice.equals("3")) {
                break; 
            }
        }
    }

    /**
     * Combination of the processAll and manual loops to process one since they have same function
     * Baeldung. Exception Handling in Java. 4.4 try-with-resources and 7.1 IOException (if file fails)
     * @param id Employee ID.
     */
    private static void processPayroll(String id) {
        try (BufferedReader br = new BufferedReader(new FileReader(EMPLOYEE_FILE))) {
            br.readLine(); 
            String line;
            boolean found = false;

            while ((line = br.readLine()) != null) {
                String[] emp = smartSplit(line);
                String currentId = emp[0]; // This is the ID we found in the row

                //Logical operator to combine two tasks: One and All.
                if (id == null || currentId.equals(id)) {
                    found = true;
                    List<String> periods = findWorkingPeriods(currentId);
                    
                    for (String payPeriod : periods) {
                        // dateParts[0] = Month, dateParts[1] = Year
                        String[] dateParts = payPeriod.split("/");  //Baeldung. Split a String in Java - 2. String.split().
                        calculatePayroll(emp, dateParts[0], dateParts[1]); 
                    }
                    if (id != null) break; 
                }
            }
            if (!found && id != null) System.out.println("Employee number does not exist.");
        } catch (IOException e) {
            System.out.println("Error reading Employee file.");
        }
    }

    /**
     * Helper to find MM/YYYY in attendance 
     * Uses simple ArrayList logic to avoid duplicates.
     * @param id Employee ID.
     */
    private static List<String> findWorkingPeriods(String id) {
        List<String> workingPeriods = new ArrayList<>();
        List<String> records = findAttendanceData(ATTENDANCE_FILE, id);
        
        for (String record : records) {
            String[] columns = smartSplit(record);
            String[] dateParts = columns[3].split("/"); // Format: MM/DD/YYYY
            
            if (dateParts.length >= 3) {
                String monthYear = dateParts[0] + "/" + dateParts[2];
                
                if (!workingPeriods.contains(monthYear)) {
                    workingPeriods.add(monthYear);
                }
            }
        }
        return workingPeriods;
    }

    /**
     * Helper to map month number to name.
     * switch expressions
     * @param monthStr - numeric month string 
     * @return the full name of the month 
     */
    private static String monthName(String monthStr) {
        int month = Integer.parseInt(monthStr);
        return switch (month) {
            case 6 -> "June"; 
            case 7 -> "July";
            case 8 -> "August";
            case 9 -> "September";
            case 10 -> "October";
            case 11 -> "November";
            case 12 -> "December";
            default -> "Month " + month;
        };
    }

    /**
     * Calculates shift hours based on clock-in/out and MotorPH rules.
     * Java Time API. Grace period and lunch hour rules. 
     * Baeldung. Exception Handling in Java. 4.2 try-catch, 3.2 unchecked exception
     * @param logIn - clock-in time string (H:mm).
     * @param logOut - clock-out time string (H:mm).
     * @return the final hour count
     */
    private static double calculateShift(String logIn, String logOut) {
        try {
            DateTimeFormatter format = DateTimeFormatter.ofPattern("H:mm");
            LocalTime timeIn = LocalTime.parse(logIn, format);
            LocalTime timeOut = LocalTime.parse(logOut, format);
            LocalTime graceLimit = LocalTime.of(8, 10);
            LocalTime startLimit = LocalTime.of(8, 0);
            LocalTime endLimit = LocalTime.of(17, 0);
            
            //Calculating shift
            LocalTime actualStart = timeIn.isAfter(graceLimit) ? timeIn : startLimit;
            LocalTime actualEnd = timeOut.isAfter(endLimit) ? endLimit : timeOut;

            if (actualStart.isAfter(actualEnd)) return 0;

            int startMins = actualStart.getHour() * 60 + actualStart.getMinute();
            int endMins = actualEnd.getHour() * 60 + actualEnd.getMinute();
            
            //Subtracting 60 mins for lunch break
            return Math.max(0, (endMins - startMins - 60) / 60.0);
            
        } catch (Exception e) { 
            System.out.println("Time Error: Invalid format found (" + logIn + " or " + logOut + "). Expected H:mm.");
        
            return 0; 
        }
    }

    /**
     * Retrieves all attendance records for a specific employee.
     * Explains exactly what went wrong if the file cannot be accessed.
     * Baeldung. Exception Handling in Java. 4.4 try-with-resources and 4.5 multiple catch
     * GeeksforGeeks. ArrayList toArray() method in Java with Examples. Baeldung. Guide to the Java ArrayList.
     * @param path - relative file path to the Attendance CSV.
     * @param id - Employee ID to filter by.
     * @return the List of strings containing matching attendance records.
     */
    private static List<String> findAttendanceData(String path, String id) {
        List<String> records = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            br.readLine(); 
            String line;
            while ((line = br.readLine()) != null) {
                String[] columns = smartSplit(line);
                if (columns.length > 0 && columns[0].trim().equals(id.trim())) {
                    records.add(line);
                }
            }
          } catch (java.io.FileNotFoundException e) {
            System.out.println("Error: Attendance file '" + path + "' not found.");
        } catch (IOException e) {
            System.out.println("Error: Problem reading attendance records. Details: " + e.getMessage());
        }
        return records;
    }


    /**
    * Helper to safely extract and parse the hourly rate.
    * Purpose: Handles string cleaning and prevents crashes on bad data.
    * Baeldung. Exception Handling in Java. 3.2 unchecked exception if there is a potential typo in csv
    * @param emp - Array of employee strings.
    * @return The hourly rate as a double; returns 0.0 if the data is invalid or missing.
    */
    private static double hourlyRate(String[] emp) {
        try {
            return Double.parseDouble(emp[18].replace(",", "").trim());
            
        } catch (Exception e) { 
            System.out.println("Data Error for ID " + emp[0] + ": Invalid Hourly Rate format in CSV.");
        
        return 0.0; 
        }
    }

    /**
     * Simplified SSS Calculation using threshold loop for better readability.
     * @param salary - total monthly gross income.
     * @return the calculated SSS contribution amount.
     */
    public static double computeSSS(double salary) {
        // Case for salaries below the first bracket
        if (salary < 3250) return 135.00;
        
        // Case for salaries exceeding the maximum bracket
        if (salary >= 24750) return 1125.00;
        
        // Start the loop logic at the first active bracket (3,250 - 3,749.99)
        double threshold = 3250;
        double contribution = 157.50; 
        
        while (threshold < 24750) {
            // Check if salary falls within the current 500-peso range
            if (salary >= threshold && salary <= threshold + 499.99) {
                return contribution;
            }
            
            threshold += 500;
            contribution += 22.50;
        }
        return 1125.00;
    }

    /**
     * Computes PhilHealth contribution (Employee share).
     * Returns the 50% employee share of the PhilHealth premium.
     * @param salary - total monthly gross income.
     * @return the PhilHealth contribution amount.
     */
    public static double computePhilHealth(double salary) {
        if (salary <= 10000) return 150.0;
        if (salary >= 60000) return 900.0;
        
        return (salary * 0.03) / 2;
    }

    /**
     * Computes PagIBIG contribution with a max cap.
     * Applies PagIBIG rates with a contribution cap of 100.00
     * @param salary - total monthly gross income.
     * @return the PagIBIG contribution amount.
     */
    public static double computePagIBIG(double salary) {
        if (salary < 1000) return 0;
        double rate = (salary <= 1500) ? 0.01 : 0.02;
        double total = (salary * rate) + (salary * 0.02);
        return Math.min(total, 100.0);
    }

    /**
     * Computes Withholding Tax based on taxable income brackets.
     * Calculates tax after government deductions are subtracted from gross
     * @param taxableIncome - Gross salary minus SSS, PhilHealth, and PagIBIG.
     * @return the calculated withholding tax amount.
     */
    public static double calculateWithholdingTax(double taxableIncome) {
        if (taxableIncome <= 20832) return 0;
        if (taxableIncome < 33333) return (taxableIncome - 20833) * 0.20;
        if (taxableIncome < 66667) return 2500 + (taxableIncome - 33333) * 0.25;
        if (taxableIncome < 166667) return 10833 + (taxableIncome - 66667) * 0.30;
        if (taxableIncome < 666667) return 40833.33 + (taxableIncome - 166667) * 0.32;
        
        return 200833.33 + (taxableIncome - 666667) * 0.35;
    }

    /**
     * Payroll output engine.
     * Improve also the variable names to have descriptive names (hoursFirstCutoff)
     * @param emp - String array of employee details from the CSV.
     * @param month - specific month to process.
     * @param year - specific year to process.
     */
    public static void calculatePayroll(String[] emp, String month, String year) {
        if (emp == null || emp.length < 19 || emp[0].isEmpty()) return;

        String id = emp[0];
        double hourlyRate = hourlyRate(emp);
        String mName = monthName(month);

        double hoursFirstCutoff = 0;
        double hoursSecondCutoff = 0;
        List<String> records = findAttendanceData(ATTENDANCE_FILE, id);

        for (String line : records) {
            String[] row = smartSplit(line);
            String[] dateParts = row[3].split("/"); // looks at date and String.split
            
            // Added year for scalability
            if (dateParts[0].equals(month) && dateParts[2].equals(year)) { //Check correct month and year
                int day = Integer.parseInt(dateParts[1]);
                double shift = calculateShift(row[4], row[5]);
                if (day <= 15) hoursFirstCutoff += shift;
                else hoursSecondCutoff += shift;
            }
        }

        double grossFirstCutoff = hoursFirstCutoff * hourlyRate;
        double grossSecondCutoff = hoursSecondCutoff * hourlyRate;
        double totalMonthlyGross = grossFirstCutoff + grossSecondCutoff;
        
        //Deductions calculations
        double sss = computeSSS(totalMonthlyGross);
        double ph = computePhilHealth(totalMonthlyGross);
        double pi = computePagIBIG(totalMonthlyGross);
        double taxableIncome = totalMonthlyGross - (sss + ph + pi);
        double tax = calculateWithholdingTax(taxableIncome);
        double totalDeduc = sss + ph + pi + tax;

        System.out.println("\n---------------------------------------------");
        System.out.println(" Employee #: " + id);
        System.out.println(" Employee Name: " + fullName(emp));
        System.out.println(" Birthday: " + emp[3]);
        System.out.println(" Cutoff Date: " + mName + " 1 to " + mName + " 15");
        System.out.println(" Total Hours Worked: " + hoursFirstCutoff);
        System.out.println(" Gross Salary: " + grossFirstCutoff);
        System.out.println(" Net Salary: " + grossFirstCutoff);
        System.out.println(" \nCutoff Date: " + mName + " 16 to " + mName + " 31 (Deductions Applied)");
        System.out.println(" Total Hours Worked: " + hoursSecondCutoff);
        System.out.println(" Gross Salary: " + grossSecondCutoff);
        System.out.println(" Each Deduction:");
        System.out.println("    SSS: " + sss);
        System.out.println("    PhilHealth: " + ph);
        System.out.println("    Pag-IBIG: " + pi);
        System.out.println("    Tax: " + tax);
        System.out.println(" Total Deductions: " + totalDeduc);
        System.out.println(" Net Salary: " + (grossSecondCutoff - totalDeduc));
        System.out.println("---------------------------------------------");
    }
}
