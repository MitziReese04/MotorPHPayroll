/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package motorphpayroll;

/*
 * MotorPH Payroll System - Final Consolidated Version
 * Syllabus Topics Covered: 
 * Week 6: Variables & Data Types | Week 7: Operators | Week 8: Control Structures 
 * Week 9: Java Methods | Week 10: File Handling
 */

/*
 * MotorPH Payroll System - Final Version
 * Syllabus Concepts: Selection Constructs, Iterative Loops, Methods, and File IO.
 */

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class MotorPHPayroll {

    // WEEK 8: Global Scanner for user input
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        // --- LOGIN PROCESS (Week 8: Selection) ---
        System.out.println("=== MotorPH Payroll System Login ===");
        System.out.print("Enter Username: ");
        String user = scanner.nextLine();
        System.out.print("Enter Password: ");
        String pass = scanner.nextLine();

        if (pass.equals("12345") && (user.equals("employee") || user.equals("payroll_staff"))) {
            if (user.equals("employee")) {
                handleEmployeeFlow();
            } else {
                handlePayrollStaffFlow();
            }
        } else {
            System.out.println("Incorrect username and/or password.");
            System.exit(0); 
        }
    }

    // --- FLOW CONTROL (Week 9: Organizing Code) ---

    public static void handleEmployeeFlow() {
        while (true) { // Week 8: Iteration (Loops)
            System.out.println("\nDisplay options:");
            System.out.println("1. Enter your employee number");
            System.out.println("2. Exit the program");
            System.out.print("Choice: ");
            String choice = scanner.nextLine();

            if (choice.equals("1")) {
                System.out.print("Enter Employee Number: ");
                String id = scanner.nextLine();
                String data = findLineById("data_employee.csv", id);
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

    public static void handlePayrollStaffFlow() {
        while (true) {
            System.out.println("\n--- PAYROLL STAFF OPTIONS ---");
            System.out.println("1. Process Payroll\n2. Exit the program");
            System.out.print("Choice: ");
            String choice = scanner.nextLine();

            if (choice.equals("1")) {
                processPayrollMenu();
            } else if (choice.equals("2")) {
                System.exit(0);
            }
        }
    }

    public static void processPayrollMenu() {
        while (true) {
            System.out.println("\n--- PROCESS PAYROLL ---");
            System.out.println("1. One employee\n2. All employees\n3. Back");
            System.out.print("Choice: ");
            String choice = scanner.nextLine();

            if (choice.equals("1")) {
                System.out.print("Enter employee number: ");
                String id = scanner.nextLine();
                String data = findLineById("data_employee.csv", id);
                if (data != null) {
                    calculatePayroll(smartSplit(data));
                } else {
                    System.out.println("Employee number does not exist.");
                }
            } else if (choice.equals("2")) {
                processAll();
            } else if (choice.equals("3")) {
                break;
            }
        }
    }

    private static void processAll() {
        try (BufferedReader br = new BufferedReader(new FileReader("data_employee.csv"))) {
            br.readLine(); 
            String line;
            while ((line = br.readLine()) != null) {
                calculatePayroll(smartSplit(line));
            }
        } catch (IOException e) {}
    }

    // --- TASK 7, 8, AND 9: LOGICAL ORDER ---

    public static void calculatePayroll(String[] emp) {
        // --- DATA SETUP (Week 6) ---
        String id = emp[0];
        double monthlyBasic = Double.parseDouble(emp[13].replace(",", ""));
        double hourlyRate = Double.parseDouble(emp[18].replace(",", ""));
        double rice = Double.parseDouble(emp[14].replace(",", ""));
        double phone = Double.parseDouble(emp[15].replace(",", ""));
        double clothing = Double.parseDouble(emp[16].replace(",", ""));
        double totalAllowances = rice + phone + clothing;

        // --- TASK 7: CALCULATE HOURS WORKED (Grace Period Applied) ---
        // Uses LocalTime to compare login with 8:10 AM grace limit.
        double h1 = getHoursWorked(id, "06", 1, 15);
        double h2 = getHoursWorked(id, "06", 16, 30);

        // --- TASK 8: COMPUTE SEMI-MONTHLY SALARY (Arithmetic) ---
        // Multiplication of hours by the hourly rate.
        double gross1 = h1 * hourlyRate;
        double gross2 = h2 * hourlyRate;

        // --- TASK 9: APPLY DEDUCTIONS (Procedural Methods) ---
        // Contributions are calculated first, then Withholding Tax.
        double sss = computeSSS(monthlyBasic);
        double ph = computePhilHealth(monthlyBasic);
        double pi = computePagIBIG(monthlyBasic);
        
        double taxableIncome = monthlyBasic - (sss + ph + pi);
        double tax = calculateWithholdingTax(taxableIncome);
        double totalDeduc = sss + ph + pi + tax;

        // --- 17-LINE OUTPUT REQUIREMENT ---
        System.out.println("\n---------------------------------------------");
        System.out.println("1. Employee #: " + id);
        System.out.println("2. Employee Name: " + emp[2] + " " + emp[1]);
        System.out.println("3. Birthday: " + emp[3]);
        System.out.println("4. Cutoff Date: June 1 to June 15");
        System.out.printf("5. Total Hours Worked: %.2f\n", h1);
        System.out.printf("6. Gross Salary: %.2f\n", gross1);
        System.out.printf("7. Net Salary: %.2f\n", gross1);
        System.out.println("8. Cutoff Date: June 16 to June 30 (Deductions Applied)");
        System.out.printf("9. Total Hours Worked: %.2f\n", h2);
        System.out.printf("10. Gross Salary: %.2f\n", gross2);
        System.out.println("11. Each Deduction:");
        System.out.printf("   12. SSS: %.2f\n", sss);
        System.out.printf("   13. PhilHealth: %.2f\n", ph);
        System.out.printf("   14. Pag-IBIG: %.2f\n", pi);
        System.out.printf("   15. Tax: %.2f (%s)\n", tax, (taxableIncome <= 20832 ? "Not taxable" : "Taxable"));
        System.out.printf("16. Total Deductions: %.2f\n", totalDeduc);
        System.out.printf("17. Net Salary: %.2f\n", (gross2 + totalAllowances - totalDeduc));
        System.out.println("---------------------------------------------");
    }

    // --- TASK 7 HELPER: TIME MATH ---
    // Why private? To prevent other classes from changing our internal clock logic (Encapsulation).
    private static double calculateShift(String logIn, String logOut) {
        try {
            
            DateTimeFormatter format = DateTimeFormatter.ofPattern("H:mm");
            LocalTime timeIn = LocalTime.parse(logIn, format);
            LocalTime timeOut = LocalTime.parse(logOut, format);
            
            // Grace Period: 8:11 AM is the first minute of being late
            LocalTime officialStart = LocalTime.of(8, 0);
            LocalTime graceLimit = LocalTime.of(8, 10);
            
            LocalTime start = timeIn.isAfter(graceLimit) ? timeIn : officialStart;
            
            long minutes = ChronoUnit.MINUTES.between(start, timeOut);
            return Math.max(0, (minutes - 60) / 60.0); // Less 1hr lunch
        } catch (Exception e) { return 0; }
    }

    public static double getHoursWorked(String id, String month, int start, int end) {
        double total = 0;
        try (BufferedReader br = new BufferedReader(new FileReader("data_attendance.csv"))) {
            br.readLine(); 
            String line;
            while ((line = br.readLine()) != null) {
                String[] row = line.split(","); 
                if (row[0].equals(id)) {
                    String[] date = row[3].split("/");
                    int d = Integer.parseInt(date[1]);
                    if (date[0].equals(month) && d >= start && d <= end) {
                        total += calculateShift(row[4], row[5]);
                    }
                }
            }
        } catch (IOException e) {}
        return total;
    }

    // --- TASK 9 HELPERS: DEDUCTIONS (Selection Constructs) ---
    // Why private? These calculations are specific to this class's payroll rules.

    public static double computeSSS(double gross) {
        if (gross <= 3250) return 135.0;
        if (gross >= 24750) return 1125.0;
        return 157.5 + ((int)((gross - 3250) / 500) * 22.5);
    }

    public static double computePhilHealth(double gross) {
        return (gross <= 10000) ? 150.0 : (gross >= 60000) ? 900.0 : (gross * 0.03) / 2;
    }

    public static double computePagIBIG(double gross) {
        return Math.min(100.0, (gross > 1500) ? gross * 0.02 : gross * 0.01);
    }

    public static double calculateWithholdingTax(double taxableIncome) {
        
        if (taxableIncome <= 20832) return 0;
        else if (taxableIncome < 33333) return (taxableIncome - 20833) * 0.20;
        else if (taxableIncome < 66667) return 2500 + (taxableIncome - 33333) * 0.25;
        else if (taxableIncome < 166667) return 10833 + (taxableIncome - 66667) * 0.30;
        else if (taxableIncome < 666667) return 40833.33 + (taxableIncome - 166667) * 0.32;
        else return 200833.33 + (taxableIncome - 666667) * 0.35;
    }

    // --- PRIVATE HELPER METHODS (CSV Splitting) ---

    private static String[] smartSplit(String line) {
        String[] cols = new String[30];
        String word = "";
        boolean inQuotes = false;
        int slot = 0;
        for (char c : line.toCharArray()) {
            if (c == '\"') inQuotes = !inQuotes;
            else if (c == ',' && !inQuotes) {
                cols[slot++] = word.trim();
                word = "";
            } else word += c;
        }
        cols[slot] = word.trim();
        return cols;
    }

    private static String findLineById(String path, String id) {
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith(id + ",")) return line;
            }
        } catch (IOException e) {}
        return null;
    }
}