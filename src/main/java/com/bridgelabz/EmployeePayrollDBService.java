package com.bridgelabz;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EmployeePayrollDBService {
    private static EmployeePayrollDBService employeePayrollDBService;
    private PreparedStatement employeePayrollDataStatement;

    private EmployeePayrollDBService() {
    }

    public static EmployeePayrollDBService getInstance() {
        if (employeePayrollDBService == null)
            employeePayrollDBService = new EmployeePayrollDBService();
        return employeePayrollDBService;
    }

    private Connection getConnection() throws ClassNotFoundException, SQLException {
        String jdbcURL = "jdbc:mysql://localhost:3306/payroll_service?useSSL=false";
        String userName = "root";
        String password = "root";
        Connection connection;
        Class.forName("com.mysql.jdbc.Driver");
        System.out.println("Driver loaded!!!");
        System.out.println("Connecting to database:"+jdbcURL);
        connection = DriverManager.getConnection(jdbcURL, userName, password);
        System.out.println("Connection is successful!!!"+connection);
        return connection;
    }

    public List<EmployeePayrollData> readData() {
        String sql = "SELECT * FROM employee_payroll;";
        List<EmployeePayrollData> employeePayrollList = new ArrayList<>();
        try {
            Connection connection= this.getConnection();
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery(sql);
            while (result.next()){
                int id = result.getInt("employee_id");
                String name = result.getString("name");
                double salary = result.getDouble("salary");
                LocalDate startDate = result.getDate("start").toLocalDate();
                employeePayrollList.add(new EmployeePayrollData(id, name, salary, startDate));
            }
            connection.close();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return employeePayrollList;
    }


    public int updateEmployeeData(String name, double salary) {
        //return this.updateEmployeeDataUsingStatement(name,salary);
        return this.updateEmployeeDataUsingPreparedStatement(name,salary);
    }


    private int updateEmployeeDataUsingStatement(String name, double salary) {
        String sql = String.format("UPDATE employee_payroll SET salary = %.2f WHERE name= '%s';",salary,name);
        try(Connection connection = this.getConnection()){
            Statement statement = connection.createStatement();
            return statement.executeUpdate(sql);
        }catch (SQLException | ClassNotFoundException e){
            e.printStackTrace();
        }
        return 0;
    }

    public int updateEmployeeDataUsingPreparedStatement(String name, double salary) {
        try(Connection connection = this.getConnection()){
            String sql = "UPDATE employee_payroll SET salary = ? WHERE name= ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setDouble(1,salary);
            preparedStatement.setString(2,name);
            //int result= preparedStatement.executeUpdate();
            return  preparedStatement.executeUpdate();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }


    public List<EmployeePayrollData> getEmployeePayrollData(String name) throws SQLException {
        List<EmployeePayrollData> employeePayrollList;
        if (this.employeePayrollDataStatement == null)
            this.prepareStatementForEmployeeData();
            employeePayrollDataStatement.setString(1, name);
            ResultSet resultSet;
            resultSet = employeePayrollDataStatement.executeQuery();
            employeePayrollList = this.getEmployeePayrollData(resultSet);
        return employeePayrollList;
    }

    private List<EmployeePayrollData> getEmployeePayrollData(ResultSet resultSet) throws SQLException {
        List<EmployeePayrollData> employeePayrollList = new ArrayList<>();
        while (resultSet.next()){
            int id = resultSet.getInt("employee_id");
            String name = resultSet.getString("name");
            double salary = resultSet.getDouble("salary");
            LocalDate startDate = resultSet.getDate("start").toLocalDate();
            employeePayrollList.add(new EmployeePayrollData(id, name, salary, startDate));
        }
        return employeePayrollList;
    }

    public List<EmployeePayrollData> retrieveEmployeePayrollDateRange(String startDate, String endDate) throws SQLException {
        List<EmployeePayrollData> employeePayrollList;
        if (this.employeePayrollDataStatement == null)
            this.prepareStatementForRetrieveEmployeePayrollDateRange();
            employeePayrollDataStatement.setString(1, startDate);
            employeePayrollDataStatement.setString(2, endDate);
            ResultSet resultSet;
            resultSet = employeePayrollDataStatement.executeQuery();
            employeePayrollList = this.retrieveEmployeePayrollDateRange(resultSet);
        return employeePayrollList;
    }

    private List<EmployeePayrollData> retrieveEmployeePayrollDateRange(ResultSet resultSet) throws SQLException {
        List<EmployeePayrollData> employeePayrollList = new ArrayList<>();
        while (resultSet.next()){
            int id = resultSet.getInt("employee_id");
            String name = resultSet.getString("name");
            double salary = resultSet.getDouble("salary");
            LocalDate startDate = resultSet.getDate("start").toLocalDate();
            employeePayrollList.add(new EmployeePayrollData(id, name, salary, startDate));
        }
        return employeePayrollList;
    }

    private void prepareStatementForRetrieveEmployeePayrollDateRange() {
        Connection connection = null;
        try {
            connection = this.getConnection();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        String sql = "SELECT * FROM employee_payroll WHERE start BETWEEN ? AND ?";
        try {
            assert connection != null;
            employeePayrollDataStatement = connection.prepareStatement(sql);
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
    }

    private void prepareStatementForEmployeeData() {
        Connection connection = null;
        try {
            connection = this.getConnection();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        String sql = "SELECT * FROM employee_payroll WHERE name = ?";
        try {
            assert connection != null;
            employeePayrollDataStatement = connection.prepareStatement(sql);
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
    }

}
