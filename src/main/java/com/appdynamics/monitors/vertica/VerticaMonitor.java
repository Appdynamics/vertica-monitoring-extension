package com.appdynamics.monitors.vertica;

import com.appdynamics.monitors.vertica.executor.QueryExecutor;
import com.appdynamics.monitors.vertica.stats.ActiveEventsStats;
import com.appdynamics.monitors.vertica.stats.DiskStorageStats;
import com.appdynamics.monitors.vertica.stats.HostResourcesStats;
import com.appdynamics.monitors.vertica.stats.IOUsageStats;
import com.appdynamics.monitors.vertica.stats.NodeStateStats;
import com.appdynamics.monitors.vertica.stats.QueryMetricsStats;
import com.appdynamics.monitors.vertica.stats.ResourceUsageStats;
import com.appdynamics.monitors.vertica.stats.StatsCollector;
import com.appdynamics.monitors.vertica.stats.SystemResourceUsageStats;
import com.appdynamics.monitors.vertica.stats.SystemStats;
import com.singularity.ee.agent.systemagent.api.AManagedMonitor;
import com.singularity.ee.agent.systemagent.api.MetricWriter;
import com.singularity.ee.agent.systemagent.api.TaskExecutionContext;
import com.singularity.ee.agent.systemagent.api.TaskOutput;
import com.singularity.ee.agent.systemagent.api.exception.TaskExecutionException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Map;
import java.util.Properties;
import org.apache.log4j.Logger;

public class VerticaMonitor extends AManagedMonitor {

    private static final Logger LOG = Logger.getLogger(VerticaMonitor.class);

    private static final String VERTICA_JDBC_DRIVER = "com.vertica.jdbc.Driver";
    public static final String CONNECTION_URL = "jdbc:vertica://{0}:{1}/{2}";

    public VerticaMonitor() {
        String details = VerticaMonitor.class.getPackage().getImplementationTitle();
        String msg = "Using Monitor Version [" + details + "]";
        LOG.info(msg);
        System.out.println(msg);
    }

    public TaskOutput execute(Map<String, String> taskArgs, TaskExecutionContext taskExecutionContext) throws TaskExecutionException {

        LOG.info("Vertica monitor started collecting stats");
        
        String host = taskArgs.get("host");
        String port = taskArgs.get("port");
        String database = taskArgs.get("database");
        String user = taskArgs.get("user");
        String password = taskArgs.get("password");

        Connection connection = createConnection(host, port, database, user, password);

        collectAndPrintStats(connection);

        closeConnection(connection);

        LOG.info("Vertica monitor finished collecting stats");

        return new TaskOutput("Vertica monitoring task completed successfully.");
    }

    private void closeConnection(Connection connection) throws TaskExecutionException {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            LOG.error("Unable to close connection", e);
            throw new TaskExecutionException("Unable to close connection", e);
        }
    }

    private void collectAndPrintStats(Connection connection) throws TaskExecutionException {

        QueryExecutor queryExecutor = new QueryExecutor(connection);
        ResultSetExtractor resultSetExtractor = new ResultSetExtractor();

        StatsCollector statsCollector = new StatsCollector(queryExecutor, resultSetExtractor);

        Map<String, Number> activeEventsStats = statsCollector.collectStats(ActiveEventsStats.request());
        printMetric(activeEventsStats);
        Map<String, Number> diskStorageStats = statsCollector.collectStats(DiskStorageStats.request());
        printMetric(diskStorageStats);
        Map<String, Number> hostResourcesStats = statsCollector.collectStats(HostResourcesStats.request());
        printMetric(hostResourcesStats);
        Map<String, Number> ioUsageStats = statsCollector.collectStats(IOUsageStats.request());
        printMetric(ioUsageStats);
        Map<String, Number> nodeStateStats = statsCollector.collectStats(NodeStateStats.request());
        printMetric(nodeStateStats);
        Map<String, Number> queryMetricsStats = statsCollector.collectStats(QueryMetricsStats.request());
        printMetric(queryMetricsStats);
        Map<String, Number> resourceUsageStats = statsCollector.collectStats(ResourceUsageStats.request());
        printMetric(resourceUsageStats);
        Map<String, Number> systemResourceUsageStats = statsCollector.collectStats(SystemResourceUsageStats.request());
        printMetric(systemResourceUsageStats);
        Map<String, Number> systemStats = statsCollector.collectStats(SystemStats.request());
        printMetric(systemStats);

    }

    private Connection createConnection(String host, String port, String database, String user, String password) throws TaskExecutionException {

        try {
            Class.forName(VERTICA_JDBC_DRIVER);
        } catch (ClassNotFoundException e) {
            LOG.error("Unable to load driver class", e);
            throw new TaskExecutionException("Unable to load driver class", e);
        }

        Properties properties = new Properties();
        properties.put("user", user);
        properties.put("password", password);
        properties.put("ReadOnly", "true");

        String url = MessageFormat.format(CONNECTION_URL, host, port, database);
        try {
            Connection conn = DriverManager.getConnection(url, properties);
            return conn;
        } catch (SQLException e) {
            LOG.error("Unable to create connection", e);
            throw new TaskExecutionException("Unable to create connection", e);
        }
    }

    /**
     * @param metrics
     */
    private void printMetric(Map<String, Number> metrics) {

        for (Map.Entry<String, Number> metric : metrics.entrySet()) {
            MetricWriter metricWriter = super.getMetricWriter(metric.getKey(), MetricWriter.METRIC_AGGREGATION_TYPE_AVERAGE, MetricWriter.METRIC_TIME_ROLLUP_TYPE_AVERAGE, MetricWriter.METRIC_CLUSTER_ROLLUP_TYPE_COLLECTIVE
            );
            Object metricValue = metric.getValue();
            if (metricValue instanceof Double) {
                metricWriter.printMetric(String.valueOf(Math.round((Double) metricValue)));
            } else if (metricValue instanceof Float) {
                metricWriter.printMetric(String.valueOf(Math.round((Float) metricValue)));
            } else {
                metricWriter.printMetric(String.valueOf(metricValue));
            }
        }
    }
}