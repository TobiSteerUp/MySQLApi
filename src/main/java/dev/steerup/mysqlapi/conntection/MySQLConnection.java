package dev.steerup.mysqlapi.conntection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * @author Txb1 at 07.01.2022
 * @project MySQLApi
 */

public class MySQLConnection {

    private final Connection connection;

    public MySQLConnection(Connection connection) {
        this.connection = connection;
    }

    public void consumeDirect(String statementString, ResultSetConsumer resultSetConsumer) {
        this.consume(statementString, StatementPreparation.empty(), resultSetConsumer);
    }

    public <T> T queryDirect(String statementString, ResultSetObjectGetter<T> resultSetObjectGetter) {
        return this.query(statementString, StatementPreparation.empty(), resultSetObjectGetter);
    }

    public <T> T query(String statementString, StatementPreparation statementPreparation, ResultSetObjectGetter<T> resultSetObjectGetter) {
        final List<T> objects = this.queryList(statementString, statementPreparation, resultSetObjectGetter);
        return !objects.isEmpty() ? objects.get(0) : null;
    }

    public <T> List<T> queryListDirect(String statementString, ResultSetObjectGetter<T> resultSetObjectGetter) {
        return this.queryList(statementString, StatementPreparation.empty(), resultSetObjectGetter);
    }

    public <T> List<T> queryList(String statementString, StatementPreparation statementPreparation, ResultSetObjectGetter<T> resultSetObjectGetter) {
        List<T> list = new ArrayList<>();
        this.consume(statementString, statementPreparation, resultSet -> {
            while (resultSet.next()) {
                list.add(resultSetObjectGetter.get(resultSet));
            }
        });
        return list;
    }

    public void consume(String statementString, StatementPreparation statementPreparation, ResultSetConsumer resultSetConsumer) {
        this.prepare(statementString, statementPreparation, preparedStatement -> {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                resultSetConsumer.consume(resultSet);
            }
        });
    }

    public void execute(String statementString, StatementPreparation statementPreparation) {
        this.prepare(statementString, statementPreparation, PreparedStatement::execute);
    }

    public <T> void executeBatch(String statementString, Stream<T> stream, StatementStreamPreparation<T> statementStreamPreparation) {
        this.prepare(statementString, preparedStatement -> {
            stream.forEach(entry -> {
                try {
                    statementStreamPreparation.prepare(entry, preparedStatement);
                    preparedStatement.addBatch();
                    preparedStatement.clearParameters();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
            preparedStatement.executeBatch();
        });
    }

    public void prepare(String statementString, StatementPreparation statementPreparation, StatementPreparation postStatementPreparation) {
        this.prepare(statementString, preparedStatement -> {
            statementPreparation.prepare(preparedStatement);
            postStatementPreparation.prepare(preparedStatement);
        });
    }

    public void prepare(String statementString, StatementPreparation statementPreparation) {
        try (PreparedStatement preparedStatement = this.connection.prepareStatement(statementString)) {
            statementPreparation.prepare(preparedStatement);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public interface StatementPreparation {
        static StatementPreparation EMPTY = preparedStatement -> {
        };

        static StatementPreparation empty() {
            return EMPTY;
        }

        void prepare(PreparedStatement preparedStatement) throws SQLException;
    }

    public interface StatementStreamPreparation<T> {
        void prepare(T t, PreparedStatement preparedStatement) throws SQLException;
    }

    public interface ResultSetConsumer {
        void consume(ResultSet resultSet) throws SQLException;
    }

    public interface ResultSetObjectGetter<T> {
        T get(ResultSet resultSet) throws SQLException;
    }
}