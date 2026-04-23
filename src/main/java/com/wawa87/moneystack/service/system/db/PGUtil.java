package com.wawa87.moneystack.service.system.db;

import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;

public class PGUtil {
    private static final DataSource DATA_SOURCE = createDataSource();

    private static DataSource createDataSource() {
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setURL("jdbc:postgresql://localhost:5432/moneystack_db?user=dev&password=dev");
        return dataSource;
    }

    public static DataSource getDataSource() {
        return DATA_SOURCE;
    }
}
