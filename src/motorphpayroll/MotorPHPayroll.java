/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */

package motorphpayroll;

/*
 * MotorPH Payroll System
 * Syllabus Topics Covered: 
 * Variables, Operators, Control Structures, Methods, File Handling
 */

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class MotorPHPayroll {

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
                String data = findEmployeeData("data_employee.csv", id);
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
                String data = findEmployeeData("data_employee.csv", id);
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

    public static void calculatePayroll(String[] emp, String month) {
        String id = emp[0];
        double monthlyBasic = Double.parseDouble(emp[13].replace(",", ""));
        double hourlyRate = Double.parseDouble(emp[18].replace(",", ""));
        
        double rice = Double.parseDouble(emp[14].replace(",", "")) / 2.0;
        double phone = Double.parseDouble(emp[15].replace(",", "")) / 2.0;
        double clothing = Double.parseDouble(emp[16].replace(",", "")) / 2.0;
        double totalAllowancesPerCutoff = rice + phone + clothing;

        double h1 = hoursWorked(id, month, 1, 15);
        double h2 = hoursWorked(id, month, 16, 31);

        double gross1 = (h1 * hourlyRate) + totalAllowancesPerCutoff;
        double gross2 = (h2 * hourlyRate) + totalAllowancesPerCutoff;

        double sss = computeSSS(monthlyBasic);
        double ph = computePhilHealth(monthlyBasic);
        double pi = computePagIBIG(monthlyBasic);
        
        double taxableIncome = monthlyBasic - (sss + ph + pi);
        double tax = calculateWithholdingTax(taxableIncome);
        double totalDeduc = sss + ph + pi + tax;

        String mName = monthName(month);

        System.out.println("\n---------------------------------------------");
        System.out.println(" Employee #: " + id);
        System.out.println(" Employee Name: " + emp[2] + " " + emp[1]);
        System.out.println(" Birthday: " + emp[3]);
        System.out.println(" Cutoff Date: " + mName + " 1 to " + mName + " 15");
        System.out.println(" Total Hours Worked: " + h1);
        System.out.println(" Gross Salary: " + gross1);
        System.out.println(" Net Salary: " + gross1);
        System.out.println(" \nCutoff Date: " + mName + " 16 to " + mName + " 31 (Deductions Applied)");
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
            
            if (actualStart.isAfter(actualEnd)) return 0;
            
            int startMins = actualStart.getHour() * 60 + actualStart.getMinute();
            int endMins = actualEnd.getHour() * 60 + actualEnd.getMinute();
            int workedMins = (endMins - startMins) - 60;
            
            return Math.max(0, workedMins / 60.0);
        } catch (Exception e) {
            return 0;
        }
    }

    public static double hoursWorked(String id, String month, int start, int end) {
        double total = 0;
        try (BufferedReader br = new BufferedReader(new FileReader("data_attendance.csv"))) {
            br.readLine(); 
            String line;
            while ((line = br.readLine()) != null) {
                String[] row = smartSplit(line); 
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
        double employerRate = 0.02; // Employer is always 2% based on your rules
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

    /**
     * CSV Parser.
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

    private static String findEmployeeData(String path, String id) {
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().startsWith(id.trim() + ",")) return line;
            }
        } catch (IOException e) {}
        return null;
    }
}