package com.appdynamics.monitors.vertica.executor;

import com.singularity.ee.agent.systemagent.api.exception.TaskExecutionException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.log4j.Logger;

public class QueryExecutor {

    private static final Logger LOG = Logger.getLogger(QueryExecutor.class);

    private Connection connection;

    public QueryExecutor(Connection connection) {
        this.connection = connection;
    }

    public ResultSet executeQuery(String query) throws TaskExecutionException {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            return preparedStatement.executeQuery();
        } catch (SQLException e) {
            LOG.error("Error executing query", e);
            throw new TaskExecutionException();
        }
    }
}