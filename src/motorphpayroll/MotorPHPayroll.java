/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */

package motorphpayroll;

/*
 * MotorPH Payroll System
 * Syllabus Topics Covered: 
 * Variables, Operators, Control Structures, Methods, File Handling
 * Group 5 H1101 MO-IT101
 *
 * adding param and return tags based on Oracle and Baeldung "How to Document Generic Type Parameters in Javadoc"
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
        
        // Logical Operators for role-based access
        if (pass.equals("12345") && (user.equals("employee") || user.equals("payroll_staff"))) {
            if (user.equals("employee")) {
                handleEmployeeFlow(scanner);
            } else {
                handlePayrollStaffFlow(scanner);
            }
        } else {
            System.out.println("Incorrect username and/or password.");
        }
        
        scanner.close();
    }

    /**
     * Manages the workflow for an Employee user.
     * Control Structures (while loops)
     * @param scanner The Scanner object used for capturing user input.
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
                    System.out.println("b. Employee Name: " + emp[2] + " " + emp[1]);
                    System.out.println("c. Birthday: " + emp[3]);
                } else {
                    System.out.println("The employee number does not exist.");
                }
            } else if (choice.equals("2")) {
                return; 
            }
        }
    }

    /**
     * Searches for a specific employee ID within the Employee Details file.
     * Includes error handling for missing files or read errors.
     * @param path The relative file path to the Employee Details CSV.
     * @param id   The Employee ID to search for.
     * @return The raw line of data for the employee, or null if not found.
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
            System.out.println("Error: The file '" + path + "' was not found. Please check the resources folder.");
        } catch (IOException e) {
            System.out.println("Error: There was a problem reading the employee file. Details: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * CSV Parser. Baeldung CSV File into Array 6.1
     * Iterates character by character to handle commas inside quotes.
     * Uses dynamic ArrayList.
     * @param line A single raw line of text from the CSV file.
     * @return A String array where each element represents a specific column.
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
                return;
            }
        }
    }

    /**
     * Sub-menu to choose between Individual history or Monthly Bulk runs.
     * nested control structures
     * Individual view now shows all months (June-Dec) automatically but process all let users choose month
     * Reference: Baeldung - "Validating User Input in Java"
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
                String data = findEmployeeData(EMPLOYEE_FILE, id);

                if (data == null) {
                    System.out.println("Employee number does not exist.");
                    continue; 
                }
                for (int m = 6; m <= 12; m++) {
                    String monthStr = (m < 10) ? "0" + m : String.valueOf(m);
                    calculatePayroll(smartSplit(data), monthStr);
                }
            } else if (choice.equals("2")) {
                System.out.print("Enter month (06 to 12): ");
                String month = scanner.nextLine();
                
                try {
                    int monthVal = Integer.parseInt(month);
                    
                    // REVISION: Logical check for valid MotorPH months (June to December)
                    if (monthVal >= 6 && monthVal <= 12) {
                        // Standardize format: if user typed "6", make it "06"
                        if (month.length() == 1) {
                            month = "0" + month;
                        }
                        processAll(month);
                    } else {
                        System.out.println("Error: Invalid month. Please enter a value between 06 and 12.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Error: Please enter a numeric month (e.g., 06).");
                }

            } else if (choice.equals("3")) {
                break; 
            }
        }
    }
    
    /**
     * Helper to map month number to name.
     * switch expressions
     * @param monthStr The numeric month string 
     * @return The full name of the month 
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
     * Processes payroll for every employee in the records
     * @param month The numeric month to process (06 to 12).
     */
    private static void processAll(String month) {
        try (BufferedReader br = new BufferedReader(new FileReader(EMPLOYEE_FILE))) {
            br.readLine(); // Header
            String line;
            while ((line = br.readLine()) != null) {
                calculatePayroll(smartSplit(line), month);
            }
        } catch (IOException e) {
            System.out.println("Error reading file.");
        }
    }
    
    /**
     * Calculates shift hours based on clock-in/out and MotorPH rules.
     * Java Time API. Grace period and lunch hour rules. 
     * @param logIn  The clock-in time string (H:mm).
     * @param logOut The clock-out time string (H:mm).
     * @return The total worked hours as a double (standard shift is 8.0).
     */
    private static double calculateShift(String logIn, String logOut) {
        try {
            DateTimeFormatter format = DateTimeFormatter.ofPattern("H:mm");
            LocalTime timeIn = LocalTime.parse(logIn, format);
            LocalTime timeOut = LocalTime.parse(logOut, format);
            LocalTime graceLimit = LocalTime.of(8, 10);
            LocalTime startLimit = LocalTime.of(8, 0);
            LocalTime endLimit = LocalTime.of(17, 0);
            
            //Shift calculation
            LocalTime actualStart = timeIn.isAfter(graceLimit) ? timeIn : startLimit;
            LocalTime actualEnd = timeOut.isAfter(endLimit) ? endLimit : timeOut;

            if (actualStart.isAfter(actualEnd)) return 0;

            int startMins = actualStart.getHour() * 60 + actualStart.getMinute();
            int endMins = actualEnd.getHour() * 60 + actualEnd.getMinute();
            
            // Subtracting 60 minutes for lunch break
            return Math.max(0, (endMins - startMins - 60) / 60.0);
        } catch (Exception e) { return 0; }
    }

    /**
     * Calculates total hours worked within a date range.
     * Scans attendance CSV for records matching the employee ID and month.
     * @param id    The Employee ID.
     * @param month The numeric month.
     * @param start The starting day of the cutoff.
     * @param end   The ending day of the cutoff.
     * @return The total accumulated hours worked during the cutoff.
     */
    public static double hoursWorked(String id, String month, int start, int end) {
        double total = 0;
        List<String> records = findAttendanceData(ATTENDANCE_FILE, id); 
        for (String line : records) {
            String[] row = smartSplit(line);
            String[] dateParts = row[3].split("/"); 
            if (dateParts[0].equals(month)) {
                int day = Integer.parseInt(dateParts[1]);
                if (day >= start && day <= end) total += calculateShift(row[4], row[5]);
            }
        }
        return total;
    }
    
    /**
     * Retrieves all attendance records for a specific employee.
     * Explains exactly what went wrong if the file cannot be accessed.
     * @param path The relative file path to the Attendance CSV.
     * @param id   The Employee ID to filter by.
     * @return A List of strings containing matching attendance records.
     */
    private static List<String> findAttendanceData(String path, String id) {
        List<String> records = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            br.readLine(); // Skip CSV header
            String line;
            while ((line = br.readLine()) != null) {
                String[] columns = smartSplit(line);
                if (columns.length > 0 && columns[0] != null && columns[0].trim().equals(id.trim())) {
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
     * Simplified SSS Calculation using threshold loop for better readability.
     * @param salary The total monthly gross income.
     * @return The calculated SSS contribution amount.
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
     * @param salary The total monthly gross income.
     * @return The PhilHealth contribution amount.
     */
    public static double computePhilHealth(double salary) {
        if (salary <= 10000) return 150.0;
        if (salary >= 60000) return 900.0;
        
        return (salary * 0.03) / 2;
    }

    /**
     * Computes PagIBIG contribution with a max cap.
     * Applies PagIBIG rates with a contribution cap of 100.00
     * @param salary The total monthly gross income.
     * @return The PagIBIG contribution amount.
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
     * @param taxableIncome Gross salary minus SSS, PhilHealth, and Pag-IBIG.
     * @return The calculated withholding tax amount.
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
     * Revised: Descriptive internal names (hoursFirstCutoff)
     * @param emp   The String array of employee details from the CSV.
     * @param month The specific month to process.
     */
    public static void calculatePayroll(String[] emp, String month) {
        if (emp == null || emp.length < 19 || emp[0].isEmpty()) return;

        String id = emp[0];
        double hourlyRate = Double.parseDouble(emp[18].replace(",", ""));
        String mName = monthName(month);

        // Improved variable names per feedback
        double hoursFirstCutoff = hoursWorked(id, month, 1, 15);
        double hoursSecondCutoff = hoursWorked(id, month, 16, 31);

        double grossFirstCutoff = hoursFirstCutoff * hourlyRate;
        double grossSecondCutoff = hoursSecondCutoff * hourlyRate;
                
        // Fix: Calculate the total once and reuse the variable
        double totalMonthlyGross = grossFirstCutoff + grossSecondCutoff;

        double sss = computeSSS(totalMonthlyGross);
        double ph = computePhilHealth(totalMonthlyGross);
        double pi = computePagIBIG(totalMonthlyGross);
        double taxableIncome = totalMonthlyGross - (sss + ph + pi);
        double tax = calculateWithholdingTax(taxableIncome);
        double totalDeduc = sss + ph + pi + tax;

        System.out.println("\n---------------------------------------------");
        System.out.println(" Employee #: " + id);
        System.out.println(" Employee Name: " + emp[2] + " " + emp[1]);
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
