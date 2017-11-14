package com.appdynamics.extensions.sql;

import com.appdynamics.extensions.AMonitorTaskRunnable;
import com.appdynamics.extensions.MetricWriteHelper;
import com.appdynamics.extensions.metrics.Metric;
import org.slf4j.LoggerFactory;

import javax.swing.plaf.nimbus.State;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

public class SQLMonitorTask implements AMonitorTaskRunnable {

    private long previousTimestamp;
    private long currentTimestamp;
    private String metricPrefix;
    private MetricWriteHelper metricWriter;
    private JDBCConnectionAdapter jdbcAdapter;
    private Map server;
    private Boolean status = true;
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(SQLMonitorTask.class);

//    public void run11() {
//        List<Map> queries = (List<Map>) server.get("queries");
//        Connection connection = null;
//
//        if (queries != null && !queries.isEmpty()) {
//            try {
//                connection = getConnection();
//                for (Map query : queries) {
//                    try {
//                        executeQuery(connection, query);
//                    } catch (SQLException e) {
//                        logger.error("Error during executing query.");
//                    }
//                }
//            } catch (SQLException e) {
//                logger.error("Error Opening connection", e);
//                status = false;
//            } catch (ClassNotFoundException e) {
//                logger.error("Class not found while opening connection", e);
//                status = false;
//            } finally {
//                try {
//                    closeConnection(connection);
//                } catch (Exception e) {
//                    logger.error("Issue closing the connection", e);
//                }
//            }
//        }
//    }


    public void run() {
        List<Map> queries = (List<Map>) server.get("queries");
        Connection connection = null;
        ResultSet resultSet = null;
        Statement statement = null;
        if (queries != null && !queries.isEmpty()) {
            try {
                connection = getConnection();
                for (Map query : queries) {
                    try {
                        resultSet = executeQuery(connection, query, resultSet, statement);
                        List<Metric> metricList = getMetricsFromResultSet(query,resultSet);
                        metricWriter.transformAndPrintMetrics(metricList);

                    } catch (SQLException e) {
                        logger.error("Error during executing query.");
                    }
                }
            } catch (SQLException e) {
                logger.error("Error Opening connection", e);
                status = false;
            } catch (ClassNotFoundException e) {
                logger.error("Class not found while opening connection", e);
                status = false;
            } finally {
                try {
                    closeConnection(connection);
                } catch (Exception e) {
                    logger.error("Issue closing the connection", e);
                }

                try{
                    resultSet.close();
                }
                catch (Exception rs){
                    logger.error("Unable to close ResultSet", rs);
                }

                try{
                    jdbcAdapter.closeStatement(statement);
                }
                catch (Exception S){
                    logger.error("Unable to close Statement", S);
                }
            }
        }
    }

    private ResultSet executeQuery (Connection connection, Map query, ResultSet resultSet, Statement statement) throws SQLException {

        try {
            resultSet = getResultSet(connection, query, statement);

        } catch (SQLException e) {
            logger.error("Error in connecting the result. ", e);
        }

        return resultSet;
    }

    private List<Metric> getMetricsFromResultSet(Map query, ResultSet resultSet) throws SQLException{
        String dbServerDisplayName = (String) server.get("displayName");
        String queryDisplayName = (String) query.get("displayName");
        ColumnGenerator columnGenerator = new ColumnGenerator();
        List<Column> columns = columnGenerator.getColumns(query);
        List<Map<String, String>> metricReplacer = getMetricReplacer();

        MetricCollector metricCollector = new MetricCollector(metricPrefix, dbServerDisplayName, queryDisplayName, metricReplacer);

        List<Metric> metricList = metricCollector.goingThroughResultSet(resultSet, columns);
        return metricList;
    }



    private ResultSet getResultSet(Connection connection, Map query, Statement statement) throws SQLException {
        String queryStmt = (String) query.get("queryStmt");
        queryStmt = substitute(queryStmt);
        statement = connection.createStatement();
        ResultSet resultSet = jdbcAdapter.queryDatabase(queryStmt, statement);
        return resultSet;
    }

    private List<Map<String, String>> getMetricReplacer() {
        List<Map<String, String>> metricReplace = (List<Map<String, String>>) server.get("metricCharacterReplacer");
        return metricReplace;
    }

    private Connection getConnection() throws SQLException, ClassNotFoundException {
        Connection connection = jdbcAdapter.open((String) server.get("driver"));
        return connection;
    }

    private void closeConnection(Connection connection) throws Exception {
        jdbcAdapter.closeConnection(connection);
    }

    private String substitute(String statement) {
        String stmt = statement;
        stmt = stmt.replace("{{previousTimestamp}}", Long.toString(previousTimestamp));
        stmt = stmt.replace("{{currentTimestamp}}", Long.toString(currentTimestamp));
        return stmt;
    }

    public void onTaskComplete() {
        logger.debug("Task Complete");
        if (status == true) {
            metricWriter.printMetric(metricPrefix+"|"+(String)server.get("displayName"), "1", "AVG","AVG","IND");
        } else {
            metricWriter.printMetric(metricPrefix+"|"+(String)server.get("displayName"), "0", "AVG","AVG","IND");
        }
    }

    public static class Builder {
        private SQLMonitorTask task = new SQLMonitorTask();

        Builder metricPrefix(String metricPrefix) {
            task.metricPrefix = metricPrefix;
            return this;
        }

        Builder metricWriter(MetricWriteHelper metricWriter) {
            task.metricWriter = metricWriter;
            return this;
        }

        Builder server(Map server) {
            task.server = server;
            return this;
        }

        Builder jdbcAdapter(JDBCConnectionAdapter adapter) {
            task.jdbcAdapter = adapter;
            return this;
        }

        Builder previousTimestamp(long timestamp) {
            task.previousTimestamp = timestamp;
            return this;
        }

        Builder currentTimestamp(long timestamp) {
            task.currentTimestamp = timestamp;
            return this;
        }

        SQLMonitorTask build() {
            return task;
        }
    }
}
