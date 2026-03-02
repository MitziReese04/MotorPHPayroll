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


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class MotorPHPayroll {

    public static void main(String[] args) {
        // Create the local scanner here
        Scanner scanner = new Scanner(System.in);

        // --- LOGIN PROCESS ---
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
        
        scanner.close(); // Proper practice to close local scanner at end of program
    }

    // --- FLOW CONTROL ---
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
                String data = findLineById("data_employee.csv", id);
                if (data != null) {
                    System.out.print("Enter month (06 to 12): ");
                    String month = scanner.nextLine();
                    calculatePayroll(smartSplit(data), month);
                } else {
                    System.out.println("Employee number does not exist.");
                }
            } else if (choice.equals("2")) {
                System.out.print("Enter month (06 to 12): ");
                String month = scanner.nextLine();
                processAll(month);
            } else if (choice.equals("3")) {
                break;
            }
        }
    }

    private static void processAll(String month) {
        try (BufferedReader br = new BufferedReader(new FileReader("data_employee.csv"))) {
            br.readLine(); 
            String line;
            while ((line = br.readLine()) != null) {
                calculatePayroll(smartSplit(line), month);
            }
        } catch (IOException e) {}
    }

    // --- TASK 7, 8, AND 9 ---
    public static void calculatePayroll(String[] emp, String month) {
        String id = emp[0];
        double monthlyBasic = Double.parseDouble(emp[13].replace(",", ""));
        double hourlyRate = Double.parseDouble(emp[18].replace(",", ""));
        
        double rice = Double.parseDouble(emp[14].replace(",", "")) / 2.0;
        double phone = Double.parseDouble(emp[15].replace(",", "")) / 2.0;
        double clothing = Double.parseDouble(emp[16].replace(",", "")) / 2.0;
        double totalAllowancesPerCutoff = rice + phone + clothing;

        double h1 = getHoursWorked(id, month, 1, 15);
        double h2 = getHoursWorked(id, month, 16, 31);

        double gross1 = (h1 * hourlyRate) + totalAllowancesPerCutoff;
        double gross2 = (h2 * hourlyRate) + totalAllowancesPerCutoff;

        double sss = computeSSS(monthlyBasic);
        double ph = computePhilHealth(monthlyBasic);
        double pi = computePagIBIG(monthlyBasic);
        
        double taxableIncome = monthlyBasic - (sss + ph + pi);
        double tax = calculateWithholdingTax(taxableIncome);
        double totalDeduc = sss + ph + pi + tax;

        String mName = getMonthName(month);

        System.out.println("\n---------------------------------------------");
        System.out.println(" Employee #: " + id);
        System.out.println(" Employee Name: " + emp[2] + " " + emp[1]);
        System.out.println(" Birthday: " + emp[3]);
        System.out.println(" Cutoff Date: " + mName + " 1 to " + mName + " 15");
        System.out.println(" Total Hours Worked: " + h1);
        System.out.println(" Gross Salary: " + gross1);
        System.out.println(" Net Salary: " + gross1);
        System.out.println(" Cutoff Date: " + mName + " 16 to " + mName + " 31 (Deductions Applied)");
        System.out.println(" Total Hours Worked: " + h2);
        System.out.println(" Gross Salary: " + gross2);
        System.out.println(" Each Deduction:");
        System.out.println("    SSS: " + sss);
        System.out.println("    PhilHealth: " + ph);
        System.out.println("    Pag-IBIG: " + pi);
        System.out.println("    Tax: " + tax + " (" + (taxableIncome <= 20832 ? "Not taxable" : "Taxable") + ")");
        System.out.println(" Total Deductions: " + totalDeduc);
        System.out.println(" Net Salary: " + (gross2 - totalDeduc));
        System.out.println("---------------------------------------------");
    }

    private static String getMonthName(String monthStr) {
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

    private static double calculateShift(String logIn, String logOut) {
        try {
            DateTimeFormatter format = DateTimeFormatter.ofPattern("H:mm");
            LocalTime timeIn = LocalTime.parse(logIn, format);
            LocalTime timeOut = LocalTime.parse(logOut, format);
            
            // Rules
            LocalTime startLimit = LocalTime.of(8, 0);
            LocalTime graceLimit = LocalTime.of(8, 10);
            LocalTime endLimit = LocalTime.of(17, 0);
            
            // Apply Grace Period Rule: If before 8:11, treat as 8:00
            LocalTime actualStart = timeIn.isAfter(graceLimit) ? timeIn : startLimit;
            // Apply 5:00 PM Window Rule
            LocalTime actualEnd = timeOut.isAfter(endLimit) ? endLimit : timeOut;
            
            if (actualStart.isAfter(actualEnd)) return 0;
            
            // Convert to minutes (Week 7: Arithmetic)
            int startMins = actualStart.getHour() * 60 + actualStart.getMinute();
            int endMins = actualEnd.getHour() * 60 + actualEnd.getMinute();
            
            // Subtract 1-hour break (60 mins)
            int workedMins = (endMins - startMins) - 60;
            
            // Return decimal hours (e.g., 7.5)
            return Math.max(0, workedMins / 60.0);
        } catch (Exception e) {
            return 0;
        }
    }

    public static double getHoursWorked(String id, String month, int start, int end) {
        double total = 0;
        try (BufferedReader br = new BufferedReader(new FileReader("data_attendance.csv"))) {
            br.readLine(); 
            String line;
            while ((line = br.readLine()) != null) {
                String[] row = line.split(","); 
                if (row[0].trim().equals(id.trim())) {
                    String[] date = row[3].split("/");
                    if (date[0].equals(month)) {
                        int d = Integer.parseInt(date[1]);
                        if (d >= start && d <= end) {
                            total += calculateShift(row[4], row[5]);
                        }
                    }
                }
            }
        } catch (IOException e) {}
        return total;
    }

    // DEDUCTIONS LOGIC 
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
                if (line.trim().startsWith(id.trim() + ",")) return line;
            }
        } catch (IOException e) {}
        return null;
    }
}