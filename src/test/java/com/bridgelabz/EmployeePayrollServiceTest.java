package com.bridgelabz;

import org.junit.Assert;
import org.junit.Test;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.bridgelabz.EmployeePayrollService.IOService.DB_I0;
import static com.bridgelabz.EmployeePayrollService.IOService.FILE_IO;

public class EmployeePayrollServiceTest {

    @Test
    public void given3EmployeesWhenWrittenToFileShouldMatchEmployeeEntries() {
        EmployeePayrollData[] arrayOfEmps = {
                new EmployeePayrollData(1,"Jeff Bezos", 100000.0),
                new EmployeePayrollData(2,"Bill Gates",200000.0),
                new EmployeePayrollData(3,"Mark Zuckerberg",300000.0)
        };
        EmployeePayrollService employeePayrollService;
        employeePayrollService = new EmployeePayrollService(Arrays.asList(arrayOfEmps));
        employeePayrollService.writeEmployeePayrollData(FILE_IO);
        employeePayrollService.printData(FILE_IO);
        long entries = employeePayrollService.countEntries(FILE_IO);
        Assert.assertEquals(3,entries);
    }

    @Test
    public void givenEmployeePayrollInDB_WhenRetrieve_ShouldMatchEmployeeCount() {
        EmployeePayrollService employeePayrollService = new EmployeePayrollService();
        List<EmployeePayrollData> employeePayrollData = employeePayrollService.readEmployeePayrollData(DB_I0);
        Assert.assertEquals(3,employeePayrollData.size());
    }

    @Test
    public void givenNewSalaryForEmployee_WhenUpdated_ShouldMatchWithDB() throws SQLException {
        EmployeePayrollService employeePayrollService = new EmployeePayrollService();
        List<EmployeePayrollData> employeePayrollData = employeePayrollService.readEmployeePayrollData(DB_I0);
        employeePayrollService.updateEmployeeSalary("Terisa", 4000000.00);
        boolean result = employeePayrollService.checkEmployeePayrollInSyncWithDB("Terisa");
        Assert.assertTrue(result);
    }

    @Test
    public void givenDataRange_WhenRetrieved_ShouldMatchEmployeeCount() throws SQLException {
        EmployeePayrollService employeePayrollService = new EmployeePayrollService();
        List<EmployeePayrollData> employeePayrollData = employeePayrollService.readEmployeePayrollData(DB_I0);
        List<EmployeePayrollData> employeesForgivenDateRange = employeePayrollService.retrieveEmployeesForgivenDateRange("2018-01-01", "2019-12-01");
        Assert.assertEquals(2,employeesForgivenDateRange.size());
    }

    @Test
    public void givenPayrollData_WhenAverageSalaryRetrievedByGender_ShouldReturnProperValue() throws ClassNotFoundException {
        EmployeePayrollService employeePayrollService = new EmployeePayrollService();
        Map<String,Double> averageSalaryByGender  = employeePayrollService.readAverageSalaryByGender(DB_I0);
        System.out.println(averageSalaryByGender);
        Assert.assertTrue(averageSalaryByGender.get("M").equals(2000000.00)&&
                averageSalaryByGender.get("F").equals(4000000.00));
    }

    @Test
    public void givenNewEmployee_WhenAdded_ShouldSyncWithDB() throws SQLException, ClassNotFoundException {
        EmployeePayrollService employeePayrollService = new EmployeePayrollService();
        List<EmployeePayrollData> employeePayrollData = employeePayrollService.readEmployeePayrollData(DB_I0);
        employeePayrollService.addEmployeeToPayroll("Mark", "M", 5000000.00, LocalDate.now());
        boolean result =  employeePayrollService.checkEmployeePayrollInSyncWithDB("Mark");
        Assert.assertTrue(result);
    }
}
