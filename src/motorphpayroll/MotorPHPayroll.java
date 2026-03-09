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
 */

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class MotorPHPayroll {
    
    private static final String ATTENDANCE_FILE = "resources/MotorPH_Employee Data - Attendance Record";
    private static final String EMPLOYEE_FILE = "resources/MotorPH_Employee Data - Employee Details";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== MotorPH Payroll System Login ===");
        System.out.print("Enter Username: ");
        String user = scanner.nextLine();
        
        System.out.print("Enter Password: ");
        String pass = scanner.nextLine();

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

    public static void handleEmployeeFlow(Scanner scanner) {
        while (true) {
            System.out.println("\nDisplay options:");
            System.out.println("1. Enter your employee number");
            System.out.println("2. Exit the program");
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
                System.exit(0);
            }
        }
    }

    private static String findEmployeeData(String path, String id) {
    try (BufferedReader br = new BufferedReader(new FileReader(path))) {
        String line;
        while ((line = br.readLine()) != null) {
            String[] columns = smartSplit(line);
            // Check if the first column (index 0) matches the ID
            if (columns[0] != null && columns[0].trim().equals(id.trim())) {
                return line;
            }
        }
    } 
    // Specifically catch if the file is missing
    catch (java.io.FileNotFoundException e) {
        System.out.println("Error: The file at '" + path + "' was not found. Please check the resources folder.");
      } 
    // Catch other general reading errors
    catch (IOException e) {
        System.out.println("Error: There was a problem reading the file.");
      }
        return null;
    }
    
    /**
     * CSV Parser. Baeldung CSV File into Array 6.1
     * Iterates character by character to handle commas inside quotes.
     */
    private static String[] smartSplit(String line) {
        String[] results = new String[30]; 
        StringBuilder tempText = new StringBuilder();
        int columnNumber = 0;
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char currentCharacter = line.charAt(i);

            if (currentCharacter == '\"') {
                inQuotes = !inQuotes; 
            } else if (currentCharacter == ',' && !inQuotes) {
                results[columnNumber++] = tempText.toString().trim();
                tempText.setLength(0); 
            } else {
                tempText.append(currentCharacter);
            }
        }
        results[columnNumber] = tempText.toString().trim();
        return results;
    }

    public static void handlePayrollStaffFlow(Scanner scanner) {
        while (true) {
            System.out.println("\n--- PAYROLL STAFF OPTIONS ---");
            System.out.println("1. Process Payroll\n2. Exit the program");
            System.out.print("Choice: ");
            String choice = scanner.nextLine();

            if (choice.equals("1")) {
                processPayrollMenu(scanner);
            } else if (choice.equals("2")) {
                System.exit(0);
            }
        }
    }

    public static void processPayrollMenu(Scanner scanner) {
    while (true) {
        System.out.println("\n--- PROCESS PAYROLL ---");
        System.out.println("1. One employee\n2. All employees\n3. Back");
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

            // Automatically show all months from June to December for one employee
            for (int m = 6; m <= 12; m++) {
                String monthStr = (m < 10) ? "0" + m : String.valueOf(m);
                calculatePayroll(smartSplit(data), monthStr);
            }

        } else if (choice.equals("2")) {
            // Enter month for processAll for organization
            System.out.print("Enter month (06 to 12): ");
            String month = scanner.nextLine();
            if (month.length() == 1) month = "0" + month;

            processAll(month);

        } else if (choice.equals("3")) {
            break; 
          }
        }
    }
    
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

    private static void processAll(String month) {
    try (BufferedReader br = new BufferedReader(new FileReader(EMPLOYEE_FILE))) {
        br.readLine(); 
        String line;
        while ((line = br.readLine()) != null) {
            calculatePayroll(smartSplit(line), month);
        }
    } catch (IOException e) {
        System.out.println("Error: Could not find employee file in resources folder.");
        }
    }
    
    private static double calculateShift(String logIn, String logOut) {
    try {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("H:mm");
        LocalTime timeIn = LocalTime.parse(logIn, format);
        LocalTime timeOut = LocalTime.parse(logOut, format);

        LocalTime startLimit = LocalTime.of(8, 0);
        LocalTime graceLimit = LocalTime.of(8, 10);
        LocalTime endLimit = LocalTime.of(17, 0);

        LocalTime actualStart = timeIn.isAfter(graceLimit) ? timeIn : startLimit;
        LocalTime actualEnd = timeOut.isAfter(endLimit) ? endLimit : timeOut;

        if (actualStart.isAfter(actualEnd)) {
            System.err.println("Error: Start time occurs after end time.");
            return 0;
        }

        int startMins = actualStart.getHour() * 60 + actualStart.getMinute();
        int endMins = actualEnd.getHour() * 60 + actualEnd.getMinute();
        int workedMins = (endMins - startMins) - 60;

        return Math.max(0, workedMins / 60.0);

    } catch (Exception e) {
        // Catches errors. Updated with error message.
        System.err.println("Error processing shift data: " + e.getMessage());
        return 0;
       }
    }

    public static double hoursWorked(String id, String month, int start, int end) {
        double total = 0;
        java.util.List<String> records = findAttendanceData(ATTENDANCE_FILE, id); //GeeksforGeeks. List interface in Java. 

    for (String line : records) {
        String[] row = smartSplit(line);
        String[] dateParts = row[3].split("/"); // Index 3 is Date
        
        if (dateParts[0].equals(month)) {
            int day = Integer.parseInt(dateParts[1]);
            if (day >= start && day <= end) {
                total += calculateShift(row[4], row[5]); // Index 4=In, 5=Out
            }
        }
     }
    return total;
    }

    private static java.util.List<String> findAttendanceData(String path, String id) {
     java.util.List<String> records = new java.util.ArrayList<>();

    try (BufferedReader br = new BufferedReader(new FileReader(path))) {
        String line;
        br.readLine(); 

        while ((line = br.readLine()) != null) {
            String[] columns = smartSplit(line);
            if (columns[0] != null && columns[0].trim().equals(id.trim())) {
                records.add(line);
            }
        }
    } 
    // 1. Specifically catch if the file is missing
    catch (java.io.FileNotFoundException e) {
        System.out.println("Error: The file at '" + path + "' was not found. Please check the resources folder.");
    } 
    // 2. Catch other reading errors 
    catch (IOException e) {
        System.out.println("Error: There was a problem reading the file. (Technical details: " + e.getMessage() + ")");
    }
    return records;
    }
    
    // --- DEDUCTIONS LOGIC ---
    public static double computeSSS(double gross) {
        if (gross < 3250) return 135.00;
        if (gross < 3750) return 157.50;
        if (gross < 4250) return 180.00;
        if (gross < 4750) return 202.50;
        if (gross < 5250) return 225.00;
        if (gross < 5750) return 247.50;
        if (gross < 6250) return 270.00;
        if (gross < 6750) return 292.50;
        if (gross < 7250) return 315.00;
        if (gross < 7750) return 337.50;
        if (gross < 8250) return 360.00;
        if (gross < 8750) return 382.50;
        if (gross < 9250) return 405.00;
        if (gross < 9750) return 427.50;
        if (gross < 10250) return 450.00;
        if (gross < 10750) return 472.50;
        if (gross < 11250) return 495.00;
        if (gross < 11750) return 517.50;
        if (gross < 12250) return 540.00;
        if (gross < 12750) return 562.50; 
        if (gross < 13250) return 585.00;
        if (gross < 13750) return 607.50;
        if (gross < 14250) return 630.00;
        if (gross < 14750) return 652.50;
        if (gross < 15250) return 675.00;
        if (gross < 15750) return 697.50;
        if (gross < 16250) return 720.00;
        if (gross < 16750) return 742.50;
        if (gross < 17250) return 765.00;
        if (gross < 17750) return 787.50;
        if (gross < 18250) return 810.00;
        if (gross < 18750) return 832.50;
        if (gross < 19250) return 855.00;
        if (gross < 19750) return 877.50;
        if (gross < 20250) return 900.00;
        if (gross < 20750) return 922.50;
        if (gross < 21250) return 945.00;
        if (gross < 21750) return 967.50;
        if (gross < 22250) return 990.00;
        if (gross < 22750) return 1012.50;
        if (gross < 23250) return 1035.00;
        if (gross < 23750) return 1057.50;
        if (gross < 24250) return 1080.00;
        if (gross < 24750) return 1102.50;
        return 1125.00;
    }

     public static double computePhilHealth(double salary) {
        double totalPremium;
        double rate = 0.03;

        if (salary <= 10000) {
            // Floor: Minimum contribution is 300
            totalPremium = 300.0;
        } else if (salary >= 60000) {
            // Ceiling: Maximum contribution is 1800
            totalPremium = 1800.0;
        } else {
            // Calculated for salaries between 10,000.01 and 59,999.99
            totalPremium = salary * rate;
        }

        // Return only the employee's share (50% of the total) 
        return totalPremium / 2;
    }

    public static double computePagIBIG(double salary) {
        double employeeRate;
        double employerRate = 0.02; // Employer is always 2% based on rules
        double totalContribution;

        // Determine rate based on salary
        if (salary >= 1000 && salary <= 1500) {
            employeeRate = 0.01;
        } else if (salary > 1500) {
            employeeRate = 0.02;
        } else {
            // If salary is below 1000, usually there is no contribution
            return 0;
        }

        // Calculate total
        totalContribution = (salary * employeeRate) + (salary * employerRate);

        // Apply the maximum cap of 100
        if (totalContribution > 100) {
            return 100.0;
        }

        return totalContribution;
    }

    public static double calculateWithholdingTax(double taxableIncome) {
        double tax = 0;

        if (taxableIncome <= 20832) {
            // Case 1: 20,832 and below
            tax = 0;
        } else if (taxableIncome < 33333) {
            // Case 2: 20,833 to below 33,333
            tax = (taxableIncome - 20833) * 0.20;
        } else if (taxableIncome < 66667) {
            // Case 3: 33,333 to below 66,667
            tax = 2500 + (taxableIncome - 33333) * 0.25;
        } else if (taxableIncome < 166667) {
            // Case 4: 66,667 to below 166,667
            tax = 10833 + (taxableIncome - 66667) * 0.30;
        } else if (taxableIncome < 666667) {
            // Case 5: 166,667 to below 666,667
            tax = 40833.33 + (taxableIncome - 166667) * 0.32;
        } else {
            // Case 6: 666,667 and above
            tax = 200833.33 + (taxableIncome - 666667) * 0.35;
        }

        return tax;
    }

    public static void calculatePayroll(String[] emp, String month) {
    String id = emp[0];
    // Parse the hourly rate (found at index 18)
    double hourlyRate = Double.parseDouble(emp[18].replace(",", ""));

    // 1. Calculate actual hours worked for both cutoffs
    double h1 = hoursWorked(id, month, 1, 15);
    double h2 = hoursWorked(id, month, 16, 31);

    // 2. Gross Salary = Total Hours Worked * Hourly Rate
    double gross1 = h1 * hourlyRate;
    double gross2 = h2 * hourlyRate;

    // 3. Monthly Gross (used to determine correct tax/SSS brackets)
    double totalMonthlyGross = gross1 + gross2;

    // 4. Compute Deductions based on the Monthly Gross
    double sss = computeSSS(totalMonthlyGross);
    double ph = computePhilHealth(totalMonthlyGross);
    double pi = computePagIBIG(totalMonthlyGross);
    
    // 5. Taxable Income (Monthly Gross - SSS/PH/PI) for tax bracket calculation
    double taxableIncome = totalMonthlyGross - (sss + ph + pi);
    double tax = calculateWithholdingTax(taxableIncome);
    
    // 6. Total Monthly Deductions
    double totalDeduc = sss + ph + pi + tax;

    // 7. Net Salary calculation (Formula: Gross - Deductions)
    // 1st Cutoff: No deductions applied
    double netSalary1 = gross1; 
    // 2nd Cutoff: All monthly deductions applied here
    double netSalary2 = gross2 - totalDeduc;
    
    String mName = monthName(month);

        System.out.println("\n---------------------------------------------");
        System.out.println(" Employee #: " + id);
        System.out.println(" Employee Name: " + emp[2] + " " + emp[1]);
        System.out.println(" Birthday: " + emp[3]);
        System.out.println(" Cutoff Date: " + mName + " 1 to " + mName + " 15");
        System.out.println(" Total Hours Worked: " + h1);
        System.out.println(" Gross Salary: " + gross1);
        System.out.println(" Net Salary: " + netSalary1);
        System.out.println(" \nCutoff Date: " + mName + " 16 to " + mName + " 31 (Deductions Applied)");
        System.out.println(" Total Hours Worked: " + h2);
        System.out.println(" Gross Salary: " + gross2);
        System.out.println(" Each Deduction:");
        System.out.println("    SSS: " + sss);
        System.out.println("    PhilHealth: " + ph);
        System.out.println("    Pag-IBIG: " + pi);
        System.out.println("    Tax: " + tax);
        System.out.println(" Total Deductions: " + totalDeduc);
        System.out.println(" Net Salary: " + netSalary2);
        System.out.println("---------------------------------------------");
    }
}