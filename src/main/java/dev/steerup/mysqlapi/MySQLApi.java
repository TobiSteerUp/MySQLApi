package dev.steerup.mysqlapi;

import com.mysql.cj.jdbc.MysqlDataSource;
import dev.steerup.mysqlapi.conntection.MySQLConnection;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author Txb1 at 07.01.2022
 * @project MySQLApi
 */

public class MySQLApi {

    private static final String OPTIONS = "?jdbcCompliantTruncation=false&autoReconnect=true&zeroDateTimeBehavior=convertToNull&max_allowed_packet=512M";

    public static MySQLConnection createConnection(String host, String port, String database, String user, String password) {
        MysqlDataSource source = new MysqlDataSource();
        source.setUrl("jdbc:mysql://" + host + "/" + database + OPTIONS);
        try {
            final Connection connection = source.getConnection(user, password);
            return new MySQLConnection(connection);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return null;
    }
}