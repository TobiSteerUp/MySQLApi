package dev.steerup.mysqlapi.conntection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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

    public void consume(String statementString, StatementPreparation statementPreparationConsumer, ResultSetConsumer resultSetConsumer) {
        try (PreparedStatement preparedStatement = this.connection.prepareStatement(statementString)){
            statementPreparationConsumer.prepare(preparedStatement);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                resultSetConsumer.consume(resultSet);
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public void execute(String statementString, StatementPreparation statementPreparationConsumer) {
        try (PreparedStatement preparedStatement = this.connection.prepareStatement(statementString)) {
            statementPreparationConsumer.prepare(preparedStatement);

            preparedStatement.execute();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public interface StatementPreparation {
        static StatementPreparation EMPTY = new StatementPreparation() {
            @Override
            public void prepare(PreparedStatement preparedStatement) throws SQLException {

            }
        };

        static StatementPreparation empty() {
            return EMPTY;
        }

        void prepare(PreparedStatement preparedStatement) throws SQLException;
    }

    public interface ResultSetConsumer {
        void consume(ResultSet resultSet) throws SQLException;
    }

    public interface ResultSetObjectGetter<T> {
        T get(ResultSet resultSet) throws SQLException;
    }
}