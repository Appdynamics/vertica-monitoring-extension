package com.appdynamics.monitors.vertica;

import com.appdynamics.extensions.ArgumentsValidator;
import com.appdynamics.extensions.PathResolver;
import com.appdynamics.extensions.yml.YmlReader;
import com.appdynamics.monitors.vertica.config.Configuration;
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
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.singularity.ee.agent.systemagent.api.AManagedMonitor;
import com.singularity.ee.agent.systemagent.api.MetricWriter;
import com.singularity.ee.agent.systemagent.api.TaskExecutionContext;
import com.singularity.ee.agent.systemagent.api.TaskOutput;
import com.singularity.ee.agent.systemagent.api.exception.TaskExecutionException;
import org.apache.log4j.Logger;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class VerticaMonitor extends AManagedMonitor {

    private static final Logger LOG = Logger.getLogger(VerticaMonitor.class);

    private static final String VERTICA_JDBC_DRIVER = "com.vertica.jdbc.Driver";
    public static final String CONNECTION_URL = "jdbc:vertica://{0}:{1}/{2}";

    private static final String CONFIG_ARG = "config-file";

    public static final String DEFAULT_METRIC_PREFIX = "Custom Metrics|Vertica|";


    private static final Map<String, String> defaultArgs = new HashMap<String, String>() {{
        put("config-file", "monitors/VerticaMonitor/config.yml");
    }};

    private static enum SupportedSysTables {
        ACTIVE_EVENTS, DISK_STORAGE, HOST_RESOURCES, IO_USAGE, NODE_STATES, QUERY_METRICS, RESOURCE_USAGE, SYSTEM_RESOURCE_USAGE, SYSTEM;
    }

    public VerticaMonitor() {
        String details = VerticaMonitor.class.getPackage().getImplementationTitle();
        String msg = "Using Monitor Version [" + details + "]";
        LOG.info(msg);
        System.out.println(msg);
    }

    public TaskOutput execute(Map<String, String> taskArgs, TaskExecutionContext taskExecutionContext) throws TaskExecutionException {

        LOG.info("Vertica monitor started collecting stats");

        taskArgs = ArgumentsValidator.validateArguments(taskArgs, defaultArgs);

        String configFilename = getConfigFilename(taskArgs.get(CONFIG_ARG));
        Configuration config = YmlReader.readFromFile(configFilename, Configuration.class);

        String host = config.getHost();
        String port = String.valueOf(config.getPort());
        String database = config.getDatabase();

        if (Strings.isNullOrEmpty(host) || Strings.isNullOrEmpty(port) || Strings.isNullOrEmpty(database)) {
            LOG.error("Please specify required parameters in monitor.xml");
            throw new TaskExecutionException("Please specify required parameters in monitor.xml");
        }

        String user = config.getUser();
        String password = config.getPassword();

        Connection connection = createConnection(host, port, database, user, password);

        collectAndPrintStats(connection, config);

        closeConnection(connection);

        LOG.info("Vertica monitor finished collecting stats");

        return new TaskOutput("Vertica monitoring task completed successfully.");
    }

    private String getConfigFilename(String filename) {
        if (filename == null) {
            return "";
        }
        // for absolute paths
        if (new File(filename).exists()) {
            return filename;
        }
        // for relative paths
        File jarPath = PathResolver.resolveDirectory(AManagedMonitor.class);
        String configFileName = "";
        if (!Strings.isNullOrEmpty(filename)) {
            configFileName = jarPath + File.separator + filename;
        }
        return configFileName;
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

    private void collectAndPrintStats(Connection connection, Configuration config) throws TaskExecutionException {

        QueryExecutor queryExecutor = new QueryExecutor(connection);
        ResultSetExtractor resultSetExtractor = new ResultSetExtractor();

        StatsCollector statsCollector = new StatsCollector(queryExecutor, resultSetExtractor);

        String metricPrefix = config.getMetricPrefix();
        if (Strings.isNullOrEmpty(metricPrefix)) {
            metricPrefix = DEFAULT_METRIC_PREFIX;
        } else {
            metricPrefix = metricPrefix.trim();
            if (!metricPrefix.endsWith("|")) {
                metricPrefix = metricPrefix + "|";
            }
        }

        String sysTables = config.getSysTables();
        Iterable<String> split = Splitter.on(",").trimResults().split(sysTables);
        ArrayList<String> sysTablesToInclude = Lists.newArrayList(split);

        if (sysTablesToInclude.contains(SupportedSysTables.ACTIVE_EVENTS.name())) {
            Map<String, Number> activeEventsStats = statsCollector.collectStats(ActiveEventsStats.request(metricPrefix));
            printMetric(activeEventsStats);
        }

        if (sysTablesToInclude.contains(SupportedSysTables.DISK_STORAGE.name())) {
            Map<String, Number> diskStorageStats = statsCollector.collectStats(DiskStorageStats.request(metricPrefix));
            printMetric(diskStorageStats);
        }

        if (sysTablesToInclude.contains(SupportedSysTables.HOST_RESOURCES.name())) {
            Map<String, Number> hostResourcesStats = statsCollector.collectStats(HostResourcesStats.request(metricPrefix));
            printMetric(hostResourcesStats);
        }

        if (sysTablesToInclude.contains(SupportedSysTables.IO_USAGE.name())) {
            Map<String, Number> ioUsageStats = statsCollector.collectStats(IOUsageStats.request(metricPrefix));
            printMetric(ioUsageStats);
        }

        if (sysTablesToInclude.contains(SupportedSysTables.NODE_STATES.name())) {
            Map<String, Number> nodeStateStats = statsCollector.collectStats(NodeStateStats.request(metricPrefix));
            printMetric(nodeStateStats);
        }

        if (sysTablesToInclude.contains(SupportedSysTables.QUERY_METRICS.name())) {
            Map<String, Number> queryMetricsStats = statsCollector.collectStats(QueryMetricsStats.request(metricPrefix));
            printMetric(queryMetricsStats);
        }

        if (sysTablesToInclude.contains(SupportedSysTables.RESOURCE_USAGE.name())) {
            Map<String, Number> resourceUsageStats = statsCollector.collectStats(ResourceUsageStats.request(metricPrefix));
            printMetric(resourceUsageStats);
        }

        if (sysTablesToInclude.contains(SupportedSysTables.SYSTEM_RESOURCE_USAGE.name())) {
            Map<String, Number> systemResourceUsageStats = statsCollector.collectStats(SystemResourceUsageStats.request(metricPrefix));
            printMetric(systemResourceUsageStats);
        }

        if (sysTablesToInclude.contains(SupportedSysTables.SYSTEM.name())) {
            Map<String, Number> systemStats = statsCollector.collectStats(SystemStats.request(metricPrefix));
            printMetric(systemStats);
        }
    }

    private Connection createConnection(String host, String port, String database, String user, String password) throws TaskExecutionException {

        try {
            Class.forName(VERTICA_JDBC_DRIVER);
        } catch (ClassNotFoundException e) {
            LOG.error("Unable to load driver class", e);
            throw new TaskExecutionException("Unable to load driver class", e);
        }

        Properties properties = new Properties();
        properties.put("ReadOnly", "true");
        if (!Strings.isNullOrEmpty(user)) {
            properties.put("user", user);
        }
        if (!Strings.isNullOrEmpty(password)) {
            properties.put("password", password);
        }

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
            String metricKey = metric.getKey();
            MetricWriter metricWriter = super.getMetricWriter(metricKey, MetricWriter.METRIC_AGGREGATION_TYPE_AVERAGE, MetricWriter.METRIC_TIME_ROLLUP_TYPE_AVERAGE, MetricWriter.METRIC_CLUSTER_ROLLUP_TYPE_COLLECTIVE
            );
            Object metricValue = metric.getValue();

            if (metricValue != null) {
                if (metricValue instanceof Double) {
                    metricWriter.printMetric(String.valueOf(Math.round((Double) metricValue)));
                } else if (metricValue instanceof Float) {
                    metricWriter.printMetric(String.valueOf(Math.round((Float) metricValue)));
                } else {
                    metricWriter.printMetric(String.valueOf(metricValue));
                }
            } else {
                LOG.debug("Ignoring metric [" + metricKey + "], as it has null value");
            }
        }
    }
}