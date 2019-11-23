package SDA;
import SDA.model.Employee;
import SDA.model.GenderEnum;
import SDA.model.Salary;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class App {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/employees";
    private static final String USER_NAME = "root";
    private static final String PASSWORD = "";
    private static Connection connection;

    public static void main(String[] args) throws SQLException {

        connection = DriverManager.getConnection(DB_URL, USER_NAME, PASSWORD);

//        String sql = "SELECT * FROM departments";
//        Statement statement = connection.createStatement();
//        ResultSet resultSet = statement.executeQuery(sql);
//
//        while (resultSet.next()) {
//            String departmentNo = resultSet.getString("dept_no");
//            String departmentName = resultSet.getString("dept_name");
//            System.out.print(departmentNo + " ");
//            System.out.println(departmentName);
//        }
//
//        String sql1 = "SELECT first_name, last_name, title FROM employees JOIN titles USING(emp_no)";
//        Statement statement1 = connection.createStatement();
//        ResultSet resultSet1 = statement1.executeQuery(sql1);

//        while (resultSet1.next()) {
//            String firstName = resultSet1.getString("first_name");
//            String lastName = resultSet1.getString("last_name");
//            String title = resultSet1.getString("title");
//            System.out.println(firstName + " " + lastName + " " + title);
//        }
//
//        resultSet.close();
//        statement.close();
//        resultSet1.close();
//        statement1.close();

//        sql = "INSERT INTO departments(dept_no, dept_name) VALUES ('d020', 'Leadership')";
//        statement = connection.createStatement();
//
//        statement.executeUpdate(sql);
//        statement.close();

//        sql = "SELECT * FROM departments WHERE dept_no = 'd020'";
//        statement = connection.createStatement();
//        resultSet = statement.executeQuery(sql);
//
//        while (resultSet.next()) {
//            String departmentNo = resultSet.getString("dept_no");
//            String departmentName = resultSet.getString("dept_name");
//            System.out.println(departmentNo + " " + departmentName);
//        }

//        getDepartmentByDepartmentNumber("d011");
//        getDepartmentByDepartmentNumber("d005");
//        List<Employee> employees = getEmployeesByGenderAndSalary(50000D, 80000D, "F");
//        printEmployeesList(employees);

        Employee employee = new Employee(11L, new java.util.Date(), "FirstTesting", "lastName", GenderEnum.M, new java.util.Date());
        Salary salary = new Salary(employee.getNumber(), 10000L, new java.util.Date(), new java.util.Date());

        saveEmployeeAndSalary(employee, salary);

//        insertDepartment("d099", "Management1");


        connection.close();

    }

    private static void getDepartmentByDepartmentNumber(String departmentNo) throws SQLException {
        String sql = "SELECT * FROM departments WHERE dept_no = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, departmentNo);

        ResultSet resultSet = statement.executeQuery();

        while (resultSet.next()) {
            String departmentNr = resultSet.getString("dept_no");
            String departmentName = resultSet.getString("dept_name");
            System.out.println(departmentNr + " " + departmentName);
        }

        resultSet.close();
        statement.close();
    }

    private static List<Employee> getEmployeesByGenderAndSalary(Double minSalary, Double maxSalary, String gender) throws SQLException {

        String sql = "SELECT employees.* FROM employees JOIN salaries USING(emp_no) WHERE salary BETWEEN ? AND ? AND gender = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setDouble(1, minSalary);
        statement.setDouble(2, maxSalary);
        statement.setString(3, gender);

        ResultSet resultSet = statement.executeQuery();

        List<Employee> employees = new ArrayList<>();

        while (resultSet.next()) {
            String firstName = resultSet.getString("first_name");
            String lastName = resultSet.getString("last_name");
            Date birthDate = resultSet.getDate("birth_date");
            Date hireDate = resultSet.getDate("hire_date");
            long employeeNo = resultSet.getLong("emp_no");
            String empGender = resultSet.getString("gender");

//            int salary = resultSet.getInt("salary");
//            System.out.println(firstName + " " + lastName + " " + salary);

            Employee employee = new Employee();
            employee.setNumber(employeeNo);
            employee.setFirstName(firstName);
            employee.setLastName(lastName);
            employee.setBirthDate(birthDate);
            employee.setHireDate(hireDate);
            employee.setGender(GenderEnum.getGenderByValue(empGender));

            employees.add(employee);
        }
            resultSet.close();
            statement.close();

        return employees;
    }

    private static void printEmployeesList(List<Employee> employees) {
        for (Employee employee : employees) {
            System.out.println(employee);
        }
    }


    private static void insertDepartment(String departmentNo, String departmentName) throws SQLException {
        String sql = "INSERT INTO departments(dept_no, dept_name) VALUES (?, ?)";
        PreparedStatement statement = connection.prepareStatement(sql);

        statement.setString(1, departmentNo);
        statement.setString(2, departmentName);

        statement.executeUpdate();
        statement.close();
    }


    private static void saveEmployeeAndSalary(Employee employee, Salary salary) {
        try {
            connection.setAutoCommit(false);

            String insertEmployeeSql = "INSERT INTO employees(emp_no, birth_date, first_name, last_name, gender, hire_date) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(insertEmployeeSql);

            statement.setLong(1, employee.getNumber());
            statement.setDate(2, new java.sql.Date(employee.getBirthDate().getTime()));
            statement.setString(3, employee.getFirstName());
            statement.setString(4, employee.getLastName());
            statement.setString(5, employee.getGender().name());
            statement.setDate(6, new java.sql.Date(employee.getHireDate().getTime()));

            statement.executeUpdate();

            String insertSalarySql = "INSERT INTO salaries(emp_no, salary, from_date, to_date) VALUES (?, ?, ?, ?)";
            PreparedStatement salaryStatement = connection.prepareStatement(insertSalarySql);

            salaryStatement.setLong(1, salary.getEmployeeNumber());
            salaryStatement.setLong(2, salary.getSalary());
            salaryStatement.setDate(3, new java.sql.Date(salary.getFromDate().getTime()));
            salaryStatement.setDate(4, new java.sql.Date(salary.getToDate().getTime()));
//            statement.setNull(4, Types.DATE);

            salaryStatement.executeUpdate();
            connection.commit();

            statement.close();
            salaryStatement.close();

        } catch (SQLException e) {
            e.printStackTrace();
            try {
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }



    }


}
