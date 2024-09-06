package com.booleanuk.api.repository;

import javax.sql.DataSource;

import com.booleanuk.api.model.Employee;
import org.postgresql.ds.PGSimpleDataSource;

import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class EmployeeRepository {

    private DataSource datasource;
    private String dbUser;
    private String dbURL;
    private String dbPassword;
    private String dbDatabase;
    private Connection connection;

    public EmployeeRepository() throws SQLException {
        this.getDatabaseConnection();
        this.datasource = this.createDataSource();
        this.connection = this.datasource.getConnection();
    }

    private DataSource createDataSource() {
        final String url = "jdbc:postgresql://" + this.dbURL + ":5432/" + this.dbDatabase + "?user=" + this.dbUser + "&password=" + this.dbPassword;
        final PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setUrl(url);
        return dataSource;
    }

    private void getDatabaseConnection() {
        InputStream input;
        try {
            input = new FileInputStream("src/main/resources/config.properties");

            Properties prop = new Properties();
            prop.load(input);
            this.dbUser = prop.getProperty("db.user");
            this.dbURL = prop.getProperty("db.url");
            this.dbPassword = prop.getProperty("db.password");
            this.dbDatabase = prop.getProperty("db.database");
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }

    public List<Employee> getall() throws SQLException {
        List<Employee> toReturn = new ArrayList<>();
        PreparedStatement statement = this.connection.prepareStatement("SELECT * FROM employees");

        ResultSet res = statement.executeQuery();

        while (res.next()) {
            Employee e = new Employee(
                    res.getLong("id"),
                    res.getString("name"),
                    res.getString("jobName"),
                    res.getString("salaryGrade"),
                    res.getString("department")
            );
            toReturn.add(e);
        }

        return toReturn;
    }


    public Employee getOne(long id) throws SQLException {
        PreparedStatement statement = this.connection.prepareStatement("SELECT * FROM employees WHERE id = ?");
        statement.setLong(1, id);

        ResultSet res = statement.executeQuery();
        Employee e = null;
        if (res.next()) {
            e = new Employee(
                    res.getLong("id"),
                    res.getString("name"),
                    res.getString("jobName"),
                    res.getString("salaryGrade"),
                    res.getString("department")
            );
        }
        return e;
    }

    public Employee add(Employee employee) throws SQLException {
        String query = "INSERT INTO employees (name, jobName, salaryGrade, department) VALUES (?, ?, ?, ?)";
        PreparedStatement statement = this.connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        statement.setString(1, employee.getName());
        statement.setString(2, employee.getJobName());
        statement.setString(3, employee.getSalaryGrade());
        statement.setString(4, employee.getDepartment());
        final int rowsAffected = statement.executeUpdate();
        long newId = 0;
        if (rowsAffected > 0) {
            ResultSet res;
            try {
                res = statement.getGeneratedKeys();
                if (res.next()) {
                    newId = res.getLong(1);
                }
            } catch (Exception e) {
                System.out.println("Error: " + e);
            }
            employee.setId(newId);
        } else {
            employee = null;
        }
        return employee;
    }

}
