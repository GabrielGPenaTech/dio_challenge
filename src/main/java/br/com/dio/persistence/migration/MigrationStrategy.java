package br.com.dio.persistence.migration;

import liquibase.Liquibase;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import lombok.AllArgsConstructor;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.SQLException;

import static br.com.dio.persistence.config.ConnectionConfig.getConnection;

@AllArgsConstructor
public class MigrationStrategy {

    private final Connection connection;

    public void executeMigration() throws SQLException {
        var originalOut = System.out;
        var originalErr = System.err;

        try (
             var fos = new FileOutputStream("liquibase.log");
             var connection = getConnection();
             var jdbcConnection = new JdbcConnection(connection);
        ){
            System.setOut(new PrintStream(fos));
            System.setErr(new PrintStream(fos));

            var liquibase = new Liquibase(
                    "db/changelog/db.changelog-master.yml",
                    new ClassLoaderResourceAccessor(),
                    jdbcConnection
            );
            liquibase.update();

            System.setErr(originalErr);

        } catch (IOException | LiquibaseException ex) {
            ex.printStackTrace();
        } finally {
            System.setOut(originalOut);
            System.setErr(originalErr);
        }
    }
}
