package com.appdynamics.extensions.sql;

import com.appdynamics.extensions.AMonitorTaskRunnable;
import com.appdynamics.extensions.MetricWriteHelper;
import com.appdynamics.extensions.metrics.Metric;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

public class SQLMonitorTask implements AMonitorTaskRunnable {

    private static final String METRIC_SEPARATOR = "|";
    private long previousTimestamp;
    private long currentTimestamp;
    private String metricPrefix;
    private MetricWriteHelper metricWriter;
    private JDBCConnectionAdapter jdbcAdapter;
    private Map server;
    private Boolean status = true;
    private final Yaml yaml = new Yaml();
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(SQLMonitorTask.class);

    public void run() {
        List<Map> queries = (List<Map>) server.get("queries");
        Connection connection = null;

        if (queries != null && !queries.isEmpty()) {
            try {
                connection = getConnection();
                for (Map query : queries) {
                    try {
                        executeQuery(connection, query);
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
            }
        }
    }


    private void executeQuery(Connection connection, Map query) throws SQLException {

        String dbServerDisplayName = (String) server.get("displayName");
        String queryDisplayName = (String) query.get("displayName");
        ResultSet resultSet = null;
        try {
            resultSet = getResultSet(connection, query);

            ColumnGenerator columnGenerator = new ColumnGenerator();
            List<Column> columns = columnGenerator.getColumns(query);
            List<Map<String, String>> metricReplacer = getMetricReplacer();

            MetricCollector metricCollector = new MetricCollector(metricPrefix, dbServerDisplayName, queryDisplayName, metricReplacer);

            List<Metric> metricList = metricCollector.goingThroughResultSet(resultSet, columns);
            metricWriter.transformAndPrintMetrics(metricList);
        } catch (SQLException e) {
            logger.error("Error in connecting the result. ", e);
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    logger.error("Unable to close the ResultSet", e);
                }
            }
        }
    }


    private ResultSet getResultSet(Connection connection, Map query) throws SQLException {
        String queryStmt = (String) query.get("queryStmt");
        queryStmt = substitute(queryStmt);
        Statement statement = null;
        ResultSet resultSet = jdbcAdapter.queryDatabase(connection, queryStmt, statement);
        if (statement != null) {
            try {
                jdbcAdapter.closeStatement(statement);
            } catch (Exception e) {
                logger.error("Unable to close Statement");
            }
        }
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
            BigDecimal one = new BigDecimal(1);
            metricWriter.printMetric(metricPrefix+"|"+(String)server.get("displayName"), one, "AVG.AVG.IND");
        } else {
            BigDecimal zero = new BigDecimal(0);
            metricWriter.printMetric(metricPrefix+"|"+(String)server.get("displayName"), zero, "AVG.AVG.IND");
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
