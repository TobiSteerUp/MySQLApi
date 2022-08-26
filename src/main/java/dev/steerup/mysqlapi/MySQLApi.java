package dev.steerup.mysqlapi;

import com.mysql.cj.jdbc.MysqlDataSource;
import dev.steerup.mysqlapi.conntection.MySQLConnection;
import dev.steerup.mysqlapi.model.MySQLInfo;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author Txb1 at 07.01.2022
 * @project MySQLApi
 */

public class MySQLApi {

    private static final String OPTIONS = "?jdbcCompliantTruncation=false&autoReconnect=true&zeroDateTimeBehavior=convertToNull&max_allowed_packet=512M&useLegacyDatetimeCode=false&serverTimezone=Europe/Berlin";

    public static MySQLConnection createConnection(String host, int port, String database, String user, String password) {
        return new MySQLConnection(new MySQLInfo(host, port, database, user, password)).openConnection();
    }

    public static Connection openConnection(MySQLInfo info) {
        String host = info.host();
        int port = info.port();
        String database = info.database();
        String user = info.user();
        String password = info.password();

        MysqlDataSource source = new MysqlDataSource();
        source.setUrl("jdbc:mysql://" + host + ":" + port + "/" + database + OPTIONS);
        try {
            source.setAutoReconnect(true);
            source.setAutoReconnectForPools(true);
            return source.getConnection(user, password);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return null;
    }
}