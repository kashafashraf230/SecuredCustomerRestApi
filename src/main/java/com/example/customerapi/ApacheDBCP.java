package com.example.customerapi;

import org.apache.commons.dbcp2.BasicDataSource;

public class ApacheDBCP {

    public static BasicDataSource dataSource = null;

    static {
        String driver = "com.mysql.cj.jdbc.Driver";
        dataSource = new BasicDataSource();
        dataSource.setUrl("jdbc:mysql://localhost:3306/customer");
        dataSource.setUsername("root");
        dataSource.setPassword("admin");

        dataSource.setMinIdle(5);
        dataSource.setMaxIdle(10);
        dataSource.setMaxTotal(25);
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }
}
