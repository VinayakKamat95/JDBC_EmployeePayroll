package com.bridgelabz;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class EmployeePayrollService {
    public static List<EmployeePayrollData> employeePayrollList;

    public EmployeePayrollService(List<EmployeePayrollData> employeePayrollList) {
        this.employeePayrollList = employeePayrollList;
    }

    public EmployeePayrollService() {}

    public enum IOService {CONSOLE_IO, FILE_IO, DB_I0, REST_IO};


    public static void main(String[] args) {
        employeePayrollList = new ArrayList<>();
        EmployeePayrollService employeePayrollService = new EmployeePayrollService(employeePayrollList);
        Scanner consoleInputReader = new Scanner(System.in);
        employeePayrollService.readEmployeePayrollData(consoleInputReader);
        employeePayrollService.writeEmployeePayrollData(IOService.CONSOLE_IO);
    }

    private void readEmployeePayrollData(Scanner consoleInputReader) {
        System.out.println("Enter Employee ID:");
        int id = consoleInputReader.nextInt();
        System.out.println("Enter Employee Name:");
        String name = consoleInputReader.next();
        System.out.println("Enter Employee Salary : ");
        double salary = consoleInputReader.nextDouble();
        System.out.println("Enter Employee startDate : ");
        LocalDate startDate = LocalDate.parse(consoleInputReader.next());
        employeePayrollList.add(new EmployeePayrollData(id, name, salary, startDate));
    }

    public void writeEmployeePayrollData(IOService ioService) {
        if(ioService.equals(IOService.CONSOLE_IO))
            System.out.println("\nWriting Employee Payroll Roaster to Console\n" + employeePayrollList);
        else if(ioService.equals(IOService.FILE_IO))
            new EmployeePayrollFileIOService().writeData(employeePayrollList);
    }

    public List<EmployeePayrollData> readEmployeePayrollData(IOService ioService) {
        if(ioService.equals(IOService.CONSOLE_IO))
            System.out.println("\nWriting Employee Payroll Roaster to Console\n" + employeePayrollList);
        else if(ioService.equals(IOService.FILE_IO))
            new EmployeePayrollFileIOService().readData(employeePayrollList);
        else if (ioService.equals(IOService.DB_I0))
            employeePayrollList = new EmployeePayrollDBService().readData();
        return employeePayrollList;
    }


    public void printData(IOService ioService) {
        if(ioService.equals(IOService.CONSOLE_IO))
            System.out.println("\nWriting Employee Payroll Roaster to Console\n" + employeePayrollList);
        else if(ioService.equals(IOService.FILE_IO))
            new EmployeePayrollFileIOService().printData();
    }

    public long countEntries(IOService ioService) {
        if(ioService.equals(IOService.FILE_IO))
            return new EmployeePayrollFileIOService().countEntries();
        return 0;
    }
}