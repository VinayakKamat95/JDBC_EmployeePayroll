package com.bridgelabz;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public Map<String, Double> getAverageSalaryByGender() throws ClassNotFoundException {
        String sql = "SELECT gender,AVG(salary) FROM employee_payroll GROUP BY gender;";
        Map<String,Double> genderToAvgSalaryMap = new HashMap<>();
        try(Connection connection = this.getConnection()){
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while(resultSet.next()) {
                String gender = resultSet.getString("gender");
                double salary = resultSet.getDouble("AVG(salary)");
                genderToAvgSalaryMap.put(gender, salary);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return genderToAvgSalaryMap;
    }

    public EmployeePayrollData addEmployeeToPayroll(String name, String gender, double salary, LocalDate start) throws ClassNotFoundException {
        int employeeId = -1;
        Connection connection = null;
        EmployeePayrollData employeePayrollData = null;
        try {
            connection = this.getConnection();
            connection.setAutoCommit(false);
        }catch(SQLException e) {
            e.printStackTrace();
        }
        try {
            assert connection != null;
            try(Statement statement = connection.createStatement()){
                String sql = String.format("INSERT INTO employee_payroll (name,gender,salary,start) VALUES ('%s','%s','%f','%s')",
                        name, gender, salary, Date.valueOf(start));
                int rowAffected = statement.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
                if(rowAffected==1) {
                    ResultSet resultSet = statement.getGeneratedKeys();
                    if(resultSet.next()) employeeId =  resultSet.getInt(1);
                }
            }
        } catch (SQLException e) {
            try {
                connection.rollback();
                return employeePayrollData;
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }

        try(Statement statement = connection.createStatement()){
            double deductions = salary*0.2;
            double taxablePay = salary-deductions;
            double tax = taxablePay*0.1;
            double netPay = salary - tax;
            String sql =  String.format("INSERT INTO payroll_details (employee_id,basic_pay,deductions,taxable_pay,income_tax,net_pay) VALUES"
                    + "( %s, %s, %s ,%s, %s, %s)",employeeId,salary,deductions,taxablePay,tax,netPay);
            int rowAffected = statement.executeUpdate(sql);
            if(rowAffected == 1) {
                employeePayrollData = new EmployeePayrollData(employeeId,name,salary,start);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
        try {
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            if(connection!=null)
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
        }
        return employeePayrollData;
    }
}
