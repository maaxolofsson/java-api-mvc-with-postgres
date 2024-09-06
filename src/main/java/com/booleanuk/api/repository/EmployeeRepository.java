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


}
